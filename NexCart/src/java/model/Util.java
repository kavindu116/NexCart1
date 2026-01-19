
package model;

import java.security.SecureRandom;

/**
 *
 * @author Shaik Sameer
 */
public class Util {
     public static String genarateCode() {
        double r = Math.random();
        int x = (int) (r * 1000000);
        return String.format("%06d", x);
    }
    
    public static boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }
    
    public static boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");
    } 
    
    public static boolean isVerifyCodeValid(String Code){
        return Code.matches("\\d+");
    }
    
    public static boolean isCodeValid(String Code){
        return Code.matches("\\d{4,5}$");
    }
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  
    private static final SecureRandom random = new SecureRandom();
    
     public static String generateOTP() {
        StringBuilder otp = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(CHARACTERS.length());
            otp.append(CHARACTERS.charAt(index));
        }

        return otp.toString();
    }
     
      public static boolean isInteger(String value){
        return value.matches("^\\d+$");
    }
      
      public static boolean isDouble(String text){
        return text.matches("^\\d+(\\.\\d{2})?$");
    }
      public static boolean isMobileValid(String mobile){
        return mobile.matches("^07[0145678][0-9]{7}$");
    }
}
