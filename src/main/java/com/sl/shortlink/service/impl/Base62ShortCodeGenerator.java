package com.sl.shortlink.service.impl;

import com.sl.shortlink.service.ShortCodeGenerator;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

@Component
public class Base62ShortCodeGenerator implements ShortCodeGenerator {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String generateCode(int length) {

        UUID uuid = UUID.randomUUID();
        byte[] uuidBytes = ByteBuffer
                .allocate(16)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits())
                .array();

        BigInteger bigInt = new BigInteger(1, uuidBytes);
        StringBuilder sb = new StringBuilder();

        while (bigInt.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divMode = bigInt.divideAndRemainder(BigInteger.valueOf(62));
            bigInt = divMode[0];
            sb.append(BASE62.charAt(divMode[1].intValue()));
        }

        String code = sb.reverse().toString();

        return code.length() > length ? code.substring(0, length) : code;

    }

}
