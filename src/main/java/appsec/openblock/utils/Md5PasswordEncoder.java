package appsec.openblock.utils;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;


// MD5 used for password hashing without any salt or pepper,
// More secure hashing algorithms highly recommended.
public class Md5PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        try {
            return Utilities.generateMD5Hash(rawPassword.toString());
        } catch (NoSuchAlgorithmException exp) {
        }
        return "";
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
            return Utilities.generateMD5Hash(rawPassword.toString()).equals(encodedPassword);
        } catch (NoSuchAlgorithmException e) {
        }
        return false;
    }
}
