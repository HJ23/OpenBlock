package appsec.openblock.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;

public class Md5PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        try {
            return Utilities.generatePasswordHash(rawPassword.toString());
        }catch (NoSuchAlgorithmException exp){}
        return "";
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try{
            return Utilities.generatePasswordHash(rawPassword.toString()).equals(encodedPassword);
        }catch (NoSuchAlgorithmException e){}
        return false;
    }
}
