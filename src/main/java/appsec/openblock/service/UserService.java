package appsec.openblock.service;

import appsec.openblock.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;


public interface UserService {

    public void saveUser(User user);
    public boolean isUserPresent(User user);
    public boolean isMobilePresent(User user);

    public Optional<User> getUserDetails(String email);

}
