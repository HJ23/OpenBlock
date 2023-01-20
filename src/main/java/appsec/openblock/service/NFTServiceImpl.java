package appsec.openblock.service;

import appsec.openblock.model.NFT;
import appsec.openblock.model.User;
import appsec.openblock.repository.NFTRepository;
import appsec.openblock.repository.UserRepository;
import appsec.openblock.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

@Service
public class NFTServiceImpl implements NFTService {
    @Autowired
    UserRepository userRepository;
    private Logger logger = Logger.getLogger(NFTServiceImpl.class.getName());
    @Autowired
    NFTRepository nftRepository;

    @Override
    public void initialSaveNFT(NFT nft) {
        try {
            nft.setIsSold(false);
            Random randomGenerator = new Random();
            nft.setToken(Utilities.generateMD5Hash(String.valueOf(randomGenerator.nextInt(0, 100000)) + String.valueOf(System.currentTimeMillis())));
            nftRepository.save(nft);
        } catch (NoSuchAlgorithmException exception) {
            logger.warning(exception.getMessage());
        }

    }

    @Override
    public void saveNFT(NFT nft) {
        nftRepository.save(nft);
    }

    @Override
    public void setOwner(User user, NFT nft) {
        userRepository.findById(user.getId()).map(us -> {
            nft.setIsSold(true);
            Random randomGenerator = new Random();
            try {
                nft.setToken(Utilities.generateMD5Hash(String.valueOf(randomGenerator.nextInt(0, 100000)) + String.valueOf(System.currentTimeMillis())));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            nft.setUser(us);
            return nftRepository.save(nft);
        });
    }

    @Override
    public List<NFT> getByOwner(User user) {
        return nftRepository.findByUserId(user.getId());
    }

    @Override
    public List<NFT> getAllUnSoldItems() {
        return nftRepository.findAll().stream().filter(item -> !item.isSold()).toList();
    }

    @Override
    public void setLastBidder(Long nftId, Long userId, Double bid) {
        Optional<NFT> nft = nftRepository.findById(nftId);
        nft.ifPresent(obj -> {
            obj.setLastBiddingPrice(bid);
            obj.setLastBidder(userId);
            nftRepository.save(obj);
        });
    }

    @Override
    public Optional<NFT> getById(Long id) {
        return nftRepository.findById(id);
    }
}
