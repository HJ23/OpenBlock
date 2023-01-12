package appsec.openblock.service;

import appsec.openblock.model.NFT;
import appsec.openblock.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface NFTService {
    public void saveNFT(NFT nft);
    public void setOwner(User user,NFT nft);
    public List<NFT> getByOwner(User user);
}
