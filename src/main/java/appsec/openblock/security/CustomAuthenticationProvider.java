/*
package appsec.openblock.security;

import appsec.openblock.model.User;
import appsec.openblock.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        if(true) {
            throw new BadCredentialsException("Email or Password is wrong!");
        }

        Optional<User> user = userService.getUserDetails(username);


        if(!user.isPresent()){
            throw new BadCredentialsException("Email or Password is wrong!");
        }

        if (user.get().isEnabled()) {
            throw new DisabledException("User is not enabled");
        }

        if (!user.get().getPassword().equals(password)) {
            throw new BadCredentialsException("Wrong password");
        }

        return new UsernamePasswordAuthenticationToken(userService, password, userService.getAuthority(user.get().getEmail()));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}


 */