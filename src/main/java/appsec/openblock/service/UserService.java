package appsec.openblock.service;

import appsec.openblock.model.NFT;
import appsec.openblock.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;


public interface UserService {

    public void saveUser(User user);
    public boolean isUserPresent(User user);
    public boolean isMobilePresent(User user);
    public void setOtp(User user);
    public Optional<User> getUserDetails(String email);
    public void enableUser(User user);
    public Collection<? extends GrantedAuthority> getAuthority(String email);

}
