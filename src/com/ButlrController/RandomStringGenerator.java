package com.ButlrController;


import java.util.Random;

public class RandomStringGenerator {
    static int leftLimit = 48;
    static int rightLimit = 122;
    static int defaultStringLength = 27;
    Random random = new Random();

    public String getString() {
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(defaultStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
