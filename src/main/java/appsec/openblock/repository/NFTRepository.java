package appsec.openblock.repository;

import appsec.openblock.model.NFT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NFTRepository extends JpaRepository<NFT,Long> {
    List<NFT> findByUserId(Long id);
}
