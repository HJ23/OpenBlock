package appsec.openblock.controller.view;

import appsec.openblock.DTO.Login;
import appsec.openblock.model.Card;
import appsec.openblock.model.NFT;
import appsec.openblock.model.User;
import appsec.openblock.service.*;
import appsec.openblock.utils.Utilities;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;


@Controller
public class MainController {
    private Logger logger = Logger.getLogger(MainController.class.getName());

    @Autowired
    FilesStorageService filesStorageService;
    @Autowired
    CollectionStorageService collectionStorageService;
    @Autowired
    ComplainService complainService;

    @Autowired
    UserService userService;

    @Autowired
    NFTService nftService;

    @Autowired
    CardService cardService;

    @RequestMapping(value = {"/index", "/"}, method = RequestMethod.GET)
    public ModelAndView index() {
        HashSet<String> items = new HashSet<String>();
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path collectionPath = Paths.get(currentPath.toString(), "src", "main", "resources", "static", "collections");

        File file = new File(collectionPath.toString());
        String directories[] = file.list();
        List<String> list = new ArrayList<String>(Arrays.asList(directories));
        list.remove("Auctions");
        directories = list.toArray(new String[0]);

        Random generator = new Random();

        while (items.size() != 20) {
            int index = generator.nextInt(directories.length);
            String directory = directories[index];
            Path toFiles = Paths.get(collectionPath.toString(), directory);
            File files = new File(toFiles.toString());
            String fileNames[] = files.list();
            index = generator.nextInt(fileNames.length);
            Path finalPath = Paths.get("collections", directory, fileNames[index]);
            items.add(finalPath.toString());
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("items", items);
        mav.setViewName("index");
        return mav;
    }

    // Authentication mechanism vulnerable to brute-force.
    // After 5-6 unsuccessful attempt account should be locked.
    // And after side-channel verification,
    // It can be unlocked again.
    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public ModelAndView login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        return mav;
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.POST)
    public @ResponseBody String loginSubmit(@Valid @ModelAttribute Login login, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return bindingResult.getAllErrors().get(0).getDefaultMessage();
        }
        return "OK";

    }

    // Verification page has token parameter which is basically
    // base64 encoded private values of user and OTP.
    // Also base64 encoded token handled on client-side
    // makes client-side vulnerable to DOM-XSS via token param.
    @RequestMapping(value = "/verification", method = RequestMethod.GET)
    public ModelAndView verify(@RequestParam("token") String token) {
        if (!token.equals("")) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("otp");
            return mav;
        }
        return new ModelAndView("redirect:/404");
    }

    // In invoice generation process back-end handles parameters.
    // without any sanitization or encoding leads to SSRF.
    @RequestMapping(value = {"/invoice"}, method = RequestMethod.GET)
    public void generateInvoice(HttpServletResponse response) throws IOException {
        String fileName = UUID.randomUUID().toString();
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path invoicePath = Paths.get(currentPath.toString(), "src", "main", "resources", "static", "invoices", fileName);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserDetails(email).stream().findFirst().get();
        List<NFT> nfts = nftService.getByOwner(user);
        String invoice = "<html><body><table border='1'><th>NFT token</th><th>Price</th><th>Date</th><th>Artist crypto address</th>";

        for (NFT nft : nfts) {
            invoice += "<tr>";
            invoice += "<td>" + nft.getToken() + "</td>";
            invoice += "<td>" + nft.getInitialPrice() + "</td>";
            invoice += "<td>" + nft.getEndBidding() + "</td>";
            invoice += "<td>" + nft.getArtistCryptoAddress() + "</td>";
            invoice += "</tr>";
        }
        invoice += "</table>";
        invoice += "</body>";
        invoice += "</html>";

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--run-all-compositor-stages-before-draw");
        ChromeDriver driver = new ChromeDriver(options);

        PrintWriter writer = new PrintWriter(invoicePath.toAbsolutePath().toString().replace("\\", "/") + ".html", "UTF-8");
        writer.println(invoice);
        writer.close();

        driver.get("file:///" + invoicePath.toAbsolutePath().toString().replace("\\", "/") + ".html");
        String command = "Page.printToPDF";
        Map<String, Object> output = driver.executeCdpCommand(command, new HashMap<>());
        byte[] byteArray = java.util.Base64.getDecoder().decode((String) output.get("data"));
        ByteArrayInputStream out = new ByteArrayInputStream(byteArray);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=invoice.pdf");
        IOUtils.copy(out, response.getOutputStream());
    }

    // All parameters in registration page are not encoded or sanitized.
    @RequestMapping(value = {"/register"}, method = RequestMethod.GET)
    public ModelAndView register() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("QuestionAndAnswer", Utilities.generateCaptcha());
        mav.setViewName("register");
        return mav;
    }

    // Captcha value here is easily computable.
    // Also captcha values are not token based (recaptcha).
    // Result stored on client-side as hidden element.
    @RequestMapping(value = {"/register"}, method = RequestMethod.POST)
    public @ResponseBody String registerSubmit(@Valid @ModelAttribute User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return bindingResult.getAllErrors().get(0).getDefaultMessage();
        }
        if (!user.getCaptchaInput().equals(user.getCaptchaInput())) {
            logger.warning(user.getCaptchaAnswer() + "----" + user.getCaptchaInput());
            return "Captcha is wrong!";
        }
        if (userService.isUserPresent(user))
            return "Email already registered!";
        if (userService.isMobilePresent(user))
            return "Mobile number already registered!";
        userService.initialSaveUser(user);
        return "OK";
    }

    // Because of /register page this becomes vulnerable to stored self-XSS
    @RequestMapping(value = {"/profile"}, method = RequestMethod.GET)
    public ModelAndView profile() {
        ModelAndView mav = new ModelAndView();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserDetails(email).get();
        List<Card> cards = cardService.getByOwner(user);
        List<NFT> nfts = nftService.getByOwner(user);

        mav.addObject("user", user);
        mav.addObject("nfts", nfts);
        mav.addObject("cards", cards);
        mav.addObject("id", user.getId());

        mav.setViewName("profile");
        return mav;
    }

    @RequestMapping(value = {"/admin"}, method = RequestMethod.GET)
    public ModelAndView admin() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("admin");
        return mav;
    }

    // This page vulnerable to stored-XSS via user supplied Message and Name parameter.
    // Also api endpoint of this page doesn't check authentication of current user.
    @RequestMapping(value = {"/complains"}, method = RequestMethod.GET)
    public ModelAndView complains() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("complains", complainService.getComplains());
        mav.setViewName("complains");
        return mav;
    }

    @RequestMapping(value = "/auction", method = RequestMethod.GET)
    public ModelAndView auctionCollection() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<NFT> nfts = nftService.getAllUnSoldItems();
        if (nfts.size() != 0) {
            ModelAndView mav = new ModelAndView();
            mav.addObject("nfts", nfts);
            mav.addObject("id", userService.getUserDetails(authentication.getName()).get().getId());
            mav.setViewName("auction");
            return mav;
        }
        return new ModelAndView("redirect:/404");
    }

    // Here name parameter vulnerable to stored-XSS.
    @RequestMapping(value = "/auctionDetails", method = RequestMethod.GET)
    public ModelAndView auction(@RequestParam("id") Long id) {
        String collections[] = {"BoredApeYachtClub", "CryptoPunks", "CryptoUnicorns", "MoonBirds", "MutantApeYachtClub", "Panksnoted", "ThePotatoz", "Freestyle"};
        Optional<NFT> nft = nftService.getById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = userService.getUserDetails(authentication.getName()).get();
        ModelAndView mav = new ModelAndView();
        nft.ifPresent(obj -> {
            mav.addObject("nft", obj);
            if (obj.getLastBidder() != null) {
                User user = userService.getById(obj.getLastBidder()).get();
                mav.addObject("name", user.getFirstName());
            }
            mav.addObject("uid", authenticatedUser.getId());
            mav.setViewName("auctionDetails");
            mav.addObject("collection", collections[obj.getCollection()]);
        });
        return mav;
    }

    @RequestMapping(value = {"/contact"}, method = RequestMethod.GET)
    public ModelAndView contact() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String name = userService.getUserDetails(email).get().getFirstName();
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", name);
        mav.addObject("email", email);
        mav.setViewName("contact");
        return mav;
    }

    @RequestMapping(value = {"/addCard"}, method = RequestMethod.GET)
    public ModelAndView addCard() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("addcard");
        return mav;
    }

    //
    @RequestMapping(value = {"/increaseBalance"}, method = RequestMethod.GET)
    public ModelAndView increaseBalance(@RequestParam Long id) {
        Optional<User> user = userService.getById(id);
        List<Card> cards = cardService.getByOwner(user.get());
        if (cards.size() == 0) {
            return new ModelAndView("redirect:/addCard");
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("cards", cards);
        mav.setViewName("increaseBalance");
        return mav;
    }

    // Directory traversal possible via nft picture upload functionality
    @RequestMapping(value = {"/collections"}, method = RequestMethod.GET)
    public ModelAndView collections(@RequestParam("id") int collection_id) {
        String collections[] = {"BoredApeYachtClub", "CryptoPunks", "CryptoUnicorns", "MoonBirds", "MutantApeYachtClub", "Panksnoted", "ThePotatoz", "Freestyle"};
        if (collection_id > collections.length) {
            return new ModelAndView("redirect:/404");
        }
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path collectionPath = Paths.get(currentPath.toString(), "src", "main", "resources", "static", "collections", collections[collection_id]);
        File file = new File(collectionPath.toString());

        String nfts[] = file.list();
        List<String> list = new ArrayList<String>(Arrays.asList(nfts));
        for (int i = 0; i < list.size(); i++) {
            Path nftPath = Paths.get("collections", collections[collection_id], list.get(i));
            list.set(i, nftPath.toString());
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("items", list);
        mav.addObject("name", collections[collection_id]);
        mav.setViewName("gallery");
        return mav;
    }

    // Open-redirect vulnerability here.
    // url white-listing should take place in this function.
    // only valid urls must allowed for redirection.
    @GetMapping(value = "/redirectto")
    public ModelAndView redirect(@RequestParam("url") String url) {
        return new ModelAndView("redirect:" + url);
    }

    @GetMapping(value = {"/profile-pictures/{filename}"})
    @ResponseBody
    public ResponseEntity<Resource> serveProfilePictures(@PathVariable String filename) {
        Resource file = filesStorageService.load(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"").header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(file);
    }

    @GetMapping(value = {"/auctions/{filename}"})
    @ResponseBody
    public ResponseEntity<Resource> serveAuctionPictures(@PathVariable String filename) {
        Resource file = collectionStorageService.load(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"").header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(file);
    }
}