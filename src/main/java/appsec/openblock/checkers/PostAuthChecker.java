package appsec.openblock.checkers;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

public class PostAuthChecker implements UserDetailsChecker {
    @Override
    public void check(UserDetails toCheck) {
        if(!toCheck.isEnabled()){
            throw new DisabledException("User is disabled");
        }
    }
}
