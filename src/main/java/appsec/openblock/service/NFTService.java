package appsec.openblock.service;

import appsec.openblock.model.NFT;

import java.util.ArrayList;
import java.util.Set;

public interface NFTService {
    public Set<NFT> getOwnedArts(String privateUserToken);
    public void saveNFT(NFT nft);
    public void setAsNFTOwner(String privateUserToken,String token);
}
