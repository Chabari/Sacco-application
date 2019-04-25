package com.george.mhadhirisaccoltd.Account;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by George on 2/25/2019.
 */

public class RandomMembershipNumber {

    public static String generateNumber() {
        //Length of the ID has to be 13
        char[] characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        int length = 5;
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }
}
