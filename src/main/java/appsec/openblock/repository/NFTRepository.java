package appsec.openblock.repository;

import appsec.openblock.model.NFT;
import appsec.openblock.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface NFTRepository extends JpaRepository<NFT,Long> {
    List<NFT> findByUserId(Long id);
}
