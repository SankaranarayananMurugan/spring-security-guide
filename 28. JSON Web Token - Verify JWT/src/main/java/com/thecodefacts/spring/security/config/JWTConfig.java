package com.thecodefacts.spring.security.config;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

@Configuration
@ConfigurationProperties(prefix = "jwt.token")
@Setter
public class JWTConfig {
    private String base64EncodedSecretKey;

    private Integer expiryInSeconds;

    private String signingAlgorithm;

    public SignatureAlgorithm getSignatureAlgorithm() {
        return SignatureAlgorithm.valueOf(signingAlgorithm);
    }

    public SecretKey getSecretKey() {
        byte[] base64DecodedKeyBytes = Base64.getDecoder().decode(base64EncodedSecretKey);
        SecretKey secretKey = new SecretKeySpec(base64DecodedKeyBytes, 0,
                base64DecodedKeyBytes.length, this.getSignatureAlgorithm().getJcaName());
        return secretKey;
    }

    public Date getIssueTime() {
        return new Date(System.currentTimeMillis());
    }

    public Date getExpiryTime(Date issueDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(issueDate);
        calendar.add(Calendar.SECOND, expiryInSeconds);
        return calendar.getTime();
    }
}
