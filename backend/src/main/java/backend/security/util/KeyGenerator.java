package backend.security.util;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateKey(String prefix){
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);

        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        return prefix + "_" + encoded;
    }
}
