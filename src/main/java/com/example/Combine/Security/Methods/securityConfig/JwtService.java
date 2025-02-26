package com.example.Combine.Security.Methods.securityConfig;

import com.example.Combine.Security.Methods.entity.InvalidatedToken;
import com.example.Combine.Security.Methods.repository.InvalidatedTokenRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    private String secret = "Hau7xDiufIWgIkom9Df9v9OM3QjB03iB3fVV47gItPS+PJsTfia/fA7PwE0LTHKq\r\n";

    private long expiration = 3600;

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    public String generateToken(UserDetails userDetails) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(userDetails.getUsername()).issuer("YourApplication")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(expiration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString()).claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        try {
            signedJWT.sign(new MACSigner(secret.getBytes()));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error signing JWT", e);
        }
    }

    public JWTClaimsSet extractAllClaims(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(secret.getBytes());
        if (signedJWT.verify(verifier)) {
            return signedJWT.getJWTClaimsSet();
        } else {
            return null;
        }
    }

    public String extractUsername(String token) throws ParseException, JOSEException {
        JWTClaimsSet claims = extractAllClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    public List<String> extractRoles(String token) throws ParseException, JOSEException {
        JWTClaimsSet claims = extractAllClaims(token);
        return claims != null ? (List<String>)claims.getClaim("roles") : null;
    }

    public boolean isTokenExpired(String token) throws ParseException, JOSEException {
        JWTClaimsSet claims = extractAllClaims(token);
        return claims != null && claims.getExpirationTime().before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws ParseException, JOSEException {
        final String username = extractUsername(token);
        return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public void invalidateToken(String token) throws ParseException, JOSEException {
        JWTClaimsSet claims = extractAllClaims(token);
        if (claims != null) {
            Date expiryDate = claims.getExpirationTime();
            LocalDateTime expiryLocalDateTime = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            invalidatedTokenRepository.save(new InvalidatedToken(token, expiryLocalDateTime));
        }
    }

    public boolean isTokenInvalidated(String token) {
        LocalDateTime now = LocalDateTime.now();
        return invalidatedTokenRepository.existsByTokenAndExpiryDateAfter(token, now);
    }


    public static class JwtVerificationException extends RuntimeException {
        public JwtVerificationException(String message) {
            super(message);
        }
    }

    public static class JwtParseException extends RuntimeException {
        public JwtParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class JwtJoseException extends RuntimeException {
        public JwtJoseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
