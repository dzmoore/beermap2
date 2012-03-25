package com.spacepocalypse.beermap2.util.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SimplePasswordTools {
    private static class SessionIdentifierGenerator {
        
        private SecureRandom random = new SecureRandom();
        
        public String nextSessionId() {
            return new BigInteger(130, random).toString(32);
        }
        
    }
    
    public static String hashPassAndSalt(final String password, final String salt) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        final byte[] pwBytes = password.getBytes("iso-8859-1");
        final byte[] saltBytes = salt.getBytes("iso-8859-1");
        
        final int newLen = pwBytes.length + saltBytes.length;
        
        byte[] combinedBytes = new byte[saltBytes.length + pwBytes.length];
        System.arraycopy(saltBytes, 0, combinedBytes, 0, saltBytes.length);
        System.arraycopy(pwBytes, 0, combinedBytes, saltBytes.length, pwBytes.length);
        
        return convertToHex(hash(combinedBytes, newLen));
    }
    
    
    public static String getSalt() {
        return new SessionIdentifierGenerator().nextSessionId();
    }
    
    private static byte[] hash(byte[] bytes, int length)
            throws NoSuchAlgorithmException {
        byte[] sha1hash;
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(bytes, 0, length);
        sha1hash = md.digest();
        return sha1hash;
    }
    
    private static String convertToHex(byte[] data) {
        final StringBuilder buf = new StringBuilder();

        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;

            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));

                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }

                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
