package appsec.openblock.service;

import appsec.openblock.model.Role;
import appsec.openblock.model.User;
import appsec.openblock.repository.UserRepository;
import appsec.openblock.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    UserRepository userRepository;


    @Override
    public void saveUser(User user) {
        try {
            user.setPassword(Utilities.generatePasswordHash(user.getPassword()));
            user.setRole(Role.USER);
        }catch (NoSuchAlgorithmException exception){
            return;
        }
        userRepository.save(user);
    }

    @Override
    public boolean isUserPresent(User user){
        Optional<User> userFromDB=userRepository.findByEmail(user.getEmail());
        if(userFromDB.isPresent()){
            return true;
        }
        return false;
    }

    @Override
    public boolean isMobilePresent(User user){
        Optional<User> userFromDB=userRepository.findByMobile(user.getMobile());
        if(userFromDB.isPresent()){
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       return userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
    }
}
