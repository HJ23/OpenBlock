package appsec.openblock.utils;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utilities {

    public static List<String> generateCaptcha() {
        List<String> retList = new ArrayList<String>();
        Random random = new Random();
        String operators[] = {"+", "-", "*"};
        int a = random.nextInt(0, 10);
        int b = random.nextInt(0, 10);
        int c = random.nextInt(0, 10);
        String firstOperator = operators[random.nextInt(0, 3)];
        String secondOperator = operators[random.nextInt(0, 3)];
        ExpressionParser parser = new SpelExpressionParser();
        String question = Integer.toString(a) + firstOperator + Integer.toString(b) + secondOperator + Integer.toString(c);
        String result = parser.parseExpression(question).getValue(String.class);
        retList.add(question);
        retList.add(result);
        return retList;
    }

    public static String generateMD5Hash(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(password.getBytes(), 0, password.length());
        String hashedPass = new BigInteger(1, messageDigest.digest()).toString(16);
        if (hashedPass.length() < 32) {
            hashedPass = "0" + hashedPass;
        }
        return hashedPass;
    }

    public static String generatePrivateUserToken(String userSecret) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(userSecret.getBytes(), 0, userSecret.length());
        String hashedPass = new BigInteger(1, messageDigest.digest()).toString(16);
        if (hashedPass.length() < 32) {
            hashedPass = "0" + hashedPass;
        }
        return hashedPass;
    }

    public static int generateOTP() {
        return new Random().nextInt(1000, 9999);
    }

}
