package com.example.ripan.map;

import java.util.*;

public class StringID {
    private static final int length = 24;

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

        // Allow alphanumeric chars, or +_.
        return id.matches("^[a-zA-Z0-9+_]+$");
    }

}
