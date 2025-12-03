package ru.bmstu.iu6.culinarycompanion.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtil {
    
    private static final int BCRYPT_COST = 12;
    
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());
    }
    
    public static boolean verifyPassword(String password, String hash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hash);
        return result.verified;
    }
}
