package com.example.ripan.map;

import java.util.*;

public class StringID {
    private static final int length = 24;
    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvxyz0123456789+_";

    public static String randomID() {
        StringBuilder idBuilder = new StringBuilder();

        Random random = new Random(System.currentTimeMillis() ^ System.nanoTime());

        for (int i = 0; i < length; i++) {
            int e = random.nextInt(characters.length());
            char c = characters.charAt(e);

            idBuilder.append(c);
        }

        return idBuilder.toString();
    }

    public static boolean isValidID(String id) {
        if (id == null || id.length() != length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            char c = id.charAt(i);

            if (characters.indexOf(c) == -1) {
                return false;
            }
        }

        return true;
    }

}
