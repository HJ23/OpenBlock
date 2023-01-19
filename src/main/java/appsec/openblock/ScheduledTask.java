package appsec.openblock;

import appsec.openblock.model.NFT;
import appsec.openblock.model.User;
import appsec.openblock.service.NFTService;
import appsec.openblock.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;


@Component
public class ScheduledTask {
    @Autowired
    NFTService nftService;
    @Autowired
    UserService userService;

    // Bussiness login error 30 second delay even after end bidding time passed
    // Solution: Bid submission page check if end bidding time passed or not.
    @Scheduled(fixedDelay = 30000)
    public void checkBiddingDateTime(){
        LocalDateTime now;
        LocalDateTime endBidding;
        for(NFT nft:nftService.getAllUnSoldItems()) {
            now=LocalDateTime.now();
            endBidding = nft.getEndBidding();
            if(now.isAfter(endBidding)){
                Optional<User> user=userService.getById(nft.getLastBidder());
                user.ifPresent(obj->{
                    nft.setIsSold(true);
                    nft.setUser(obj);
                    nftService.saveNFT(nft);
                    obj.setBalance(obj.getBalance()-nft.getLastBiddingPrice());
                });
            }
        }
    }
}
