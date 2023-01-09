package appsec.openblock.service;

import appsec.openblock.model.User;
import org.springframework.stereotype.Service;


public interface UserService {

    public void saveUser(User user);
    public boolean isUserPresent(User user);
    public boolean isMobilePresent(User user);

}
