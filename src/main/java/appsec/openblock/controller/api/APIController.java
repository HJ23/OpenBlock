package appsec.openblock.controller.api;

import appsec.openblock.model.NFT;
import appsec.openblock.service.NFTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/collection")
public class APIController {
    @Autowired
    NFTService nftService;

    @PostMapping
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
        NFT tmp=new NFT();
        tmp.setCollection(collection);
        tmp.setArtistFullName(artistFullName);
        tmp.setArtistCryptoAddress(cryptoAddress);
        tmp.setFilePath(auctionPath.toAbsolutePath().toString());
        tmp.setIsSold(false);
        tmp.setInitialPrice(initialPrice);
        nftService.saveNFT(tmp);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }


}
