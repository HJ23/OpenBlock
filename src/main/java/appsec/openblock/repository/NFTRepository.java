package appsec.openblock.repository;

import appsec.openblock.model.NFT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Set;

@Repository
public interface NFTRepository extends JpaRepository<NFT,Long> {
    Set<NFT> findByPrivateUserToken(String privateUserToken);
}
