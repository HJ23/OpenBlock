package appsec.openblock.controller.api;

import appsec.openblock.DTO.Bid;
import appsec.openblock.DTO.Buy;
import appsec.openblock.DTO.OTP;
import appsec.openblock.model.Card;
import appsec.openblock.model.Complain;
import appsec.openblock.model.NFT;
import appsec.openblock.model.User;
import appsec.openblock.service.*;
import appsec.openblock.utils.MyXMLHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.validation.Valid;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@RestController
public class APIController {
    @Autowired
    NFTService nftService;
    @Autowired
    UserService userService;
    @Autowired
    CollectionStorageService collectionStorageService;
    @Autowired
    CardService cardService;
    @Autowired
    ComplainService complainService;

    @Autowired
    FilesStorageService filesStorageService;

    // No sanitization or encoding performed.
    // That is why name and message parameters lead to stored-XSS.
    @PostMapping(value = "/api/v1/contact")
    public ResponseEntity<String> contact(@RequestBody Complain complain) {
        complainService.saveComplain(complain);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    // Performs transaction without checking that card belongs to that user first.
    @PostMapping(value = "/api/v1/buy")
    public ResponseEntity<String> buy(@RequestBody Buy buy) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("FAKE TRANSACTION FROM " + buy.getCard() + "-- " + buy.getEth().toString() + " eth. --> " + authentication.getName());
        User user = userService.getUserDetails(authentication.getName()).get();
        user.setBalance(user.getBalance() + buy.getEth());
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping(value = {"/api/v1/otp"})
    public ResponseEntity<String> otpCheck(@RequestBody OTP otp) {
        byte[] bdetails = Base64.getDecoder().decode(otp.getToken());
        String details = new String(bdetails, StandardCharsets.UTF_8);
        String email = details.split("-")[0];
        String privateToken = details.split("-")[1];
        User user = userService.getUserDetails(email).get();

        if (user.getLastOtp().toString().equals(otp.getOtp()) && user.getPrivateUserToken().equals(privateToken)) {
            userService.enableUser(user);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    // File traversal possible via collection file also same named files will be overwritten.
    // filename should be unique (uuid) or canonical path should be checked.
    @PostMapping(value = {"/api/v1/collection"})
    public ResponseEntity<String> addNFT(@RequestParam("fileUpload") MultipartFile image, @RequestParam("artistFullName") String artistFullName, @RequestParam("endBidding") String time, @RequestParam("initialPrice") String initialPrice, @RequestParam("collection") int collection, @RequestParam("token") String cryptoAddress) {
        collectionStorageService.save(image);
        LocalDateTime ldt = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        NFT tmp = new NFT();
        tmp.setCollection(collection);
        tmp.setArtistFullName(artistFullName);
        tmp.setArtistCryptoAddress(cryptoAddress);
        tmp.setFilePath("auctions\\" + image.getOriginalFilename());
        tmp.setIsSold(false);
        tmp.setInitialPrice(initialPrice);
        tmp.setLastBiddingPrice(0.0);
        tmp.setArtistTotalSale(new Random().nextInt(1, 203));
        tmp.setEndBidding(ldt);
        nftService.initialSaveNFT(tmp);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping(value = {"/api/v1/card"})
    public @ResponseBody String saveCard(@Valid @ModelAttribute Card card, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return bindingResult.getAllErrors().get(0).getDefaultMessage();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserDetails(email).stream().findFirst().get();
        cardService.setOwner(user, card);
        return "OK!";
    }

    // Path traversal via profile picture parameter
    // image name should be unique (uuid) or canonical path should be checked.
    @PostMapping(value = {"/api/v1/profile"})
    public ResponseEntity<String> profileEdit(@RequestParam("profilePic") MultipartFile image, @RequestParam("email") String newEmail, @RequestParam("newPassword") String newPassword, @RequestParam("mobile") String mobile) {

        filesStorageService.save(image);
        String fileName = image.getOriginalFilename();
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path profilePicturePath = Paths.get(currentPath.toString(), "src", "main", "resources", "static", "profile-pictures", fileName);
        Path profilePictureDBPath = Paths.get("profile-pictures", fileName);
        File serverFile = new File(profilePicturePath.toString());
        try {
            image.transferTo(serverFile);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserDetails(email).get();
        userService.updateUser(user, newEmail, newPassword, profilePictureDBPath.toString(), mobile);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    // bidXML parameter handles xml data insecurely.
    // In order to secure it DTD option should be set to false. (XXE)
    // Also user id handled without any check so it is possible to bid,
    // behalf of another user which leads financial gain/loss.
    @PostMapping(value = {"/api/v1/bid"})
    public ResponseEntity<String> bid(@RequestParam("bidXML") String bidXML) {
        Bid bid;
        try {
            String xmlData = new String(Base64.getDecoder().decode(bidXML), StandardCharsets.UTF_8);
            InputSource is = new InputSource(new StringReader(xmlData));
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);

            SAXParser saxParser = factory.newSAXParser();
            MyXMLHandler handler = new MyXMLHandler();
            saxParser.parse(is, handler);
            bid = handler.getBidObject();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Optional<NFT> nft = nftService.getById(bid.getId());
        Optional<User> user = userService.getById(bid.getUid());
        if (user.isPresent() && user.get().getBalance() >= bid.getPrice()) {
            nft.ifPresent(obj -> {
                obj.setLastBidder(bid.getUid());
                obj.setLastBiddingPrice(bid.getPrice());
                nftService.saveNFT(obj);
            });
        } else {
            return new ResponseEntity<String>(bid.toString(), HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<String>(bid.toString(), HttpStatus.ACCEPTED);
    }
}