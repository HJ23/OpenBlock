package appsec.openblock.controller.api;

import appsec.openblock.DTO.Buy;
import appsec.openblock.DTO.OTP;
import appsec.openblock.model.Card;
import appsec.openblock.model.Complain;
import appsec.openblock.model.NFT;
import appsec.openblock.model.User;
import appsec.openblock.service.CardService;
import appsec.openblock.service.ComplainService;
import appsec.openblock.service.NFTService;
import appsec.openblock.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class APIController {
    @Autowired
    NFTService nftService;
    @Autowired
    UserService userService;

    @Autowired
    CardService cardService;
    @Autowired
    ComplainService complainService;

    @PostMapping(value="/api/v1/contact")
    public ResponseEntity<String> contact(@RequestBody Complain complain){
        complainService.saveComplain(complain);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping(value="/api/v1/buy")
    public ResponseEntity<String> buy(@RequestBody Buy buy){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        System.out.println("FAKE TRANSACTION FROM "+buy.getCard()+"-- "+buy.getEth().toString()+" eth. --> "+authentication.getName());
        User user=userService.getUserDetails(authentication.getName()).get();
        user.setBalance(user.getBalance()+buy.getEth());
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping(value={"/api/v1/otp"})
    public ResponseEntity<String> otpCheck(@RequestBody OTP otp){
        System.out.println(otp.getToken()+"--------------"+otp.getOtp());

        byte[] bdetails= Base64.getDecoder().decode(otp.getToken());
        String details = new String(bdetails, StandardCharsets.UTF_8);
        String email=details.split("-")[0];
        String privateToken=details.split("-")[1];
        User user=userService.getUserDetails(email).get();

        if(user.getLastOtp().toString().equals(otp.getOtp()) && user.getPrivateUserToken().equals(privateToken)){
            userService.enableUser(user);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    @PostMapping(value = {"/api/v1/collection"})
    public ResponseEntity<String> addNFT(@RequestParam("fileUpload") MultipartFile image,
                                     @RequestParam("artistFullName") String artistFullName,
                                     @RequestParam("endBidding") String time,
                                     @RequestParam("initialPrice") String initialPrice,
                                     @RequestParam("collection") String collection,
                                         @RequestParam("token") String cryptoAddress
                                     ){
        LocalDateTime ldt=LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        String fileName = image.getOriginalFilename();

        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path auctionPath = Paths.get(currentPath.toString(),"src","main","resources","static","collections","Auctions",fileName);

        File serverFile = new File(auctionPath.toAbsolutePath().toString());
        try {
            image.transferTo(serverFile);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        User user=userService.getUserDetails(email).stream().findFirst().get();

        NFT tmp=new NFT();
        tmp.setCollection(collection);
        tmp.setArtistFullName(artistFullName);
        tmp.setArtistCryptoAddress(cryptoAddress);
        tmp.setFilePath(auctionPath.toAbsolutePath().toString());
        tmp.setIsSold(false);
        tmp.setInitialPrice(initialPrice);
        nftService.setOwner(user,tmp);

        System.out.println(nftService.getByOwner(user));
        System.out.println(nftService.getByOwner(user).get(0).getFilePath());

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping(value = {"/api/v1/card"})
    public @ResponseBody  String saveCard(@Valid @ModelAttribute Card card, BindingResult bindingResult){
        System.out.println(card.getCardNumber()+"--"+card.getExpireDate()+"--"+card.getSecurityCode()+"--"+card.getFullName());
        if(bindingResult.hasErrors()){
            return bindingResult.getAllErrors().get(0).getDefaultMessage();
        }
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        User user=userService.getUserDetails(email).stream().findFirst().get();

        cardService.setOwner(user,card);
        return "OK!";
    }


    @PostMapping(value = {"/api/v1/profile"})
    public ResponseEntity<String> profileEdit(@RequestParam("profilePic") MultipartFile image,
                                         @RequestParam("email") String newEmail,
                                         @RequestParam("newPassword") String newPassword,
                                         @RequestParam("mobile") String mobile
    ){


        System.out.println(newEmail+"---"+newPassword+"---"+mobile);

        String fileName = image.getOriginalFilename();
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path profilePicturePath = Paths.get(currentPath.toString(),"src","main","resources","static","profile-pictures",fileName);
        Path profilePictureDBPath = Paths.get("profile-pictures",fileName);
        File serverFile = new File(profilePicturePath.toString());
        try {
            image.transferTo(serverFile);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        User user=userService.getUserDetails(email).get();
        userService.updateUser(user,newEmail,newPassword,profilePictureDBPath.toString(),mobile);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }




}







