package model;

public class Validations {

    public static boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }

    public static boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,30}$");
//        return true;
    }
    
    public static boolean isDouble(String insertedText) {
        return insertedText.matches("^\\d+(\\.\\d{2})?$");
    }
    
    public static boolean isInteger(String insertedText) {
        return insertedText.matches("^\\d+$");
    }
    
    public static boolean isMobileNumberValidSriLankan(String insertedText) {
        return insertedText.matches("^07[01245678]{1}[0-9]{7}$");
    }
}
