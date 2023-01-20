package appsec.openblock.service;

import appsec.openblock.model.NFT;
import appsec.openblock.model.User;

import java.util.List;
import java.util.Optional;

public interface NFTService {
    public void saveNFT(NFT nft);

    public void initialSaveNFT(NFT nft);

    public void setOwner(User user, NFT nft);

    public List<NFT> getByOwner(User user);

    public List<NFT> getAllUnSoldItems();

    public void setLastBidder(Long nftId, Long userId, Double bid);

    public Optional<NFT> getById(Long id);
}
