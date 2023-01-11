package appsec.openblock.service;

import appsec.openblock.model.NFT;
import appsec.openblock.repository.NFTRepository;
import appsec.openblock.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

@Service
public class NFTServiceImpl implements NFTService {
    private Logger logger= Logger.getLogger(NFTServiceImpl.class.getName());
    @Autowired
    NFTRepository nftRepository;

    @Override
    public Set<NFT> getOwnedArts(String privateUserToken) {
        return nftRepository.findByPrivateUserToken(privateUserToken);
    }

    @Override
    public void setAsNFTOwner(String privateUserToken, String token) {

    }

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
}
