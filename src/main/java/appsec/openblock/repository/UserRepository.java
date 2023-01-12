package appsec.openblock.repository;

import appsec.openblock.model.NFT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import appsec.openblock.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByEmail(String email);
    Optional<User> findByMobile(String mobile);
    Optional<User> findById(Long id);
}
