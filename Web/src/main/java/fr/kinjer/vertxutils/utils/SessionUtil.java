package fr.kinjer.vertxutils.utils;

public class SessionUtil {

    public static String generateSessionId() {
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 25; i++) {
            token.append(letters.charAt((int) (Math.random() * letters.length())));
        }
        return token.toString();
    }

}
