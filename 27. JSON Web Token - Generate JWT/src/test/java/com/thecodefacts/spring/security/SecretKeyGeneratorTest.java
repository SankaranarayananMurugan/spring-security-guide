package com.thecodefacts.spring.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SecretKeyGeneratorTest {
    private static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.HS256;

    @Test
    public void generateBase64EncodedSecretKeyString() {
        // Generate and Base64 encode to string to store it in disk
        SecretKey originalKey = Keys.secretKeyFor(ALGORITHM);
        String base64EncodedKeyString = Base64.getEncoder().encodeToString(originalKey.getEncoded());
        System.out.println("Base64 encoded secret key generated below, store it in a secure place");
        System.out.println(base64EncodedKeyString);

        // Base64 decode from string and regenerate SecretKey
        byte[] base64DecodedKeyBytes = Base64.getDecoder().decode(base64EncodedKeyString);
        SecretKey regeneratedKey = new SecretKeySpec(base64DecodedKeyBytes, 0, base64DecodedKeyBytes.length, ALGORITHM.getJcaName());

        assert originalKey.equals(regeneratedKey);
    }
}
