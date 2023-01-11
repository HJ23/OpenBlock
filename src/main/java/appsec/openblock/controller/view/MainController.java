package appsec.openblock.controller.view;

import appsec.openblock.DTO.LoginDTO;
import appsec.openblock.model.NFT;
import appsec.openblock.model.User;
import appsec.openblock.service.NFTService;
import appsec.openblock.service.UserService;
import appsec.openblock.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;


@Controller
public class MainController {
    private Logger logger=Logger.getLogger(MainController.class.getName());

    @Autowired
    UserService userService;

    @Autowired
    NFTService nftService;

    @RequestMapping(value={"/","/index","*"},method = RequestMethod.GET)
    public ModelAndView index(){
        HashSet<String> items=new HashSet<String>();
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path collectionPath = Paths.get(currentPath.toString(),"src","main","resources","static","collections");

        File file=new File(collectionPath.toString());
        String directories[]=file.list();

        Random generator=new Random();

        while(items.size()!=20){
            int index=generator.nextInt(directories.length);
            String directory=directories[index];
            Path toFiles=Paths.get(collectionPath.toString(),directory);
            File files=new File(toFiles.toString());
            String fileNames[]=files.list();
            index=generator.nextInt(fileNames.length);
            Path finalPath=Paths.get("collections",directory,fileNames[index]);
            items.add(finalPath.toString());
        }
        ModelAndView mav=new ModelAndView();
        mav.addObject("items",items);
        mav.setViewName("index");
        return mav;
    }

    @RequestMapping(value={"/login"},method = RequestMethod.GET)
    public ModelAndView login(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        logger.info("***********************************************");
        logger.info(authentication.getName()+"----"+authentication.getAuthorities().toString());
        logger.info("***********************************************");

        if(authentication==null || authentication instanceof AnonymousAuthenticationToken) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("login");
            return mav;
        }
            return new ModelAndView("redirect:/profile");
    }

    @RequestMapping(value={"/login"},method = RequestMethod.POST)
    public @ResponseBody String loginSubmit(@Valid @ModelAttribute LoginDTO login, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return bindingResult.getAllErrors().get(0).getDefaultMessage();
        }
        return "OK";

    }


    @RequestMapping(value={"/register"},method = RequestMethod.GET)
    public ModelAndView register(){
        ModelAndView mav=new ModelAndView();
        mav.addObject("QuestionAndAnswer",Utilities.generateCaptcha());
        mav.setViewName("register");
        return mav;
    }

    @RequestMapping(value={"/register"},method = RequestMethod.POST)
    public @ResponseBody String registerSubmit(@Valid @ModelAttribute User user, BindingResult bindingResult){
        if( bindingResult.hasErrors() ) {
            return bindingResult.getAllErrors().get(0).getDefaultMessage();
        }
        if( !user.getCaptchaInput().equals(user.getCaptchaInput()) ){
            logger.warning(user.getCaptchaAnswer()+"----"+user.getCaptchaInput());
            return "Captcha is wrong!";
        }
        if(userService.isUserPresent(user))
            return "Email already registered!";
        if(userService.isMobilePresent(user))
            return "Mobile number already registered!";
        userService.saveUser(user);
        return "OK";
    }

    @RequestMapping(value={"/profile"},method = RequestMethod.GET)
    public ModelAndView profile(){
        ModelAndView mav=new ModelAndView();

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        User user=userService.getUserDetails(email).stream().findFirst().get();
        Set<NFT> ownedNFTs=nftService.getOwnedArts(user.getPrivateUserToken());


        for(NFT nft:ownedNFTs) {
            logger.warning(nft.getToken());
        }

        //mav.addObject();
        mav.setViewName("profile");
        return mav;
    }

    @RequestMapping(value={"/admin1"},method = RequestMethod.GET)
    public ModelAndView admin1(){
        ModelAndView mav=new ModelAndView();
        mav.setViewName("admin");
        return mav;
    }


    @RequestMapping(value={"/contact"},method = RequestMethod.GET)
    public ModelAndView contact(){
        ModelAndView mav=new ModelAndView();
        mav.setViewName("contact");
        return mav;
    }


    @RequestMapping(value={"/404"},method = RequestMethod.GET)
    public ModelAndView notFound(){
        ModelAndView mav=new ModelAndView();
        mav.setViewName("404");
        return mav;
    }


    @RequestMapping(value={"/collections/{collection_id}"},method = RequestMethod.GET)
    public ModelAndView  collections(@PathVariable("collection_id") int collection_id){
        String collections[]={"BoredApeYachtClub","CryptoPunks","CryptoUnicorns","MoonBirds","MutantApeYachtClub","Panksnoted","ThePotatoz"};
        if(collection_id>collections.length){
            return new ModelAndView("redirect:/404");
        }
        ModelAndView mav=new ModelAndView();
        mav.setViewName("contact");
        return mav;
    }

}