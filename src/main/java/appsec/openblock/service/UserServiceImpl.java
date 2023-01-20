package appsec.openblock.service;

import appsec.openblock.model.Role;
import appsec.openblock.model.User;
import appsec.openblock.repository.UserRepository;
import appsec.openblock.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    UserRepository userRepository;


    @Override
    public void initialSaveUser(User user) {
        try {
            user.setBalance(0.0);
            user.setAccountLocked(true);
            user.setPassword(Utilities.generateMD5Hash(user.getPassword()));
            user.setRole(Role.USER);
            user.setPrivateUserToken(Utilities.generatePrivateUserToken(user.getPassword() + user.getEmail()));
            user.setLastOtp(Utilities.generateOTP());
        } catch (NoSuchAlgorithmException exception) {
            return;
        }
        userRepository.save(user);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean isUserPresent(User user) {
        Optional<User> userFromDB = userRepository.findByEmail(user.getEmail());
        if (userFromDB.isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public void setOtp(User user) {
        user.setLastOtp(Utilities.generateOTP());
        userRepository.save(user);
    }

    @Override
    public void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void updateUser(User user, String email, String password, String profilePic, String mobile) {
        try {
            user.setProfilePic(profilePic);
            user.setEmail(email);
            user.setMobile(mobile);
            user.setPassword(Utilities.generateMD5Hash(password));
            userRepository.save(user);
        } catch (NoSuchAlgorithmException exp) {
        }
    }

    @Override
    public boolean isMobilePresent(User user) {
        Optional<User> userFromDB = userRepository.findByMobile(user.getMobile());
        if (userFromDB.isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public Optional<User> getUserDetails(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthority(String email) {
        return userRepository.findByEmail(email).get().getAuthorities();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean isEnabled(String email) {
        return userRepository.findByEmail(email).get().isEnabled();
    }
}
