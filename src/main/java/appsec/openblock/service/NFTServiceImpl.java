package appsec.openblock.service;

import appsec.openblock.model.NFT;
import appsec.openblock.model.User;
import appsec.openblock.repository.NFTRepository;
import appsec.openblock.repository.UserRepository;
import appsec.openblock.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Logger;

@Service
public class NFTServiceImpl implements NFTService {
    @Autowired
    UserRepository userRepository;
    private Logger logger= Logger.getLogger(NFTServiceImpl.class.getName());
    @Autowired
    NFTRepository nftRepository;

    @Override
    public void saveNFT(NFT nft) {
        try {
            nft.setIsSold(false);
            Random randomGenerator = new Random();
            nft.setToken(Utilities.generateMD5Hash(String.valueOf(randomGenerator.nextInt(0, 100000))+String.valueOf(System.currentTimeMillis())));
            nftRepository.save(nft);
        }catch (NoSuchAlgorithmException exception){
            logger.warning(exception.getMessage());
        }

    }

    @Override
    public void setOwner(User user,NFT nft) {
        userRepository.findById(user.getId()).map( us->{
            nft.setUser(us);
            return nftRepository.save(nft);
        });
    }

    @Override
    public List<NFT> getByOwner(User user) {
        return nftRepository.findByUserId(user.getId());
    }
}
