package com.springTemplate.springTemplate.security;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

public abstract class JwtUtil {

    private static final int expireHourToken = 24;
    private static final int expireHourRefreshToken = 72;

    /**
     * Clef secrète permettant d'effectuer le chiffrement du jeton
     */
    private static final String SECRET = "AHI898697394CDBC534E7EDGTF628FE6B309E0A21DRFB130E0369C";

    /**
     * Création d'un JWT.
     *
     * @param mail   Le nom du l'utilisateur authentifié
     * @param issuer L'url du client qui a fait la demandé d'authentification
     * @param roles  Les rôles de l'utilisateur
     * @return Une représentation sous forme de chaîne de caractères des rôles de
     *         l'utilisateur
     */
    public static String createAccessToken(String mail, String issuer, Integer idUser, String last_name,
            String first_name, Boolean firstConnection, List<String> roles) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(mail)
                    .issuer(issuer)
                    .claim("idUser", idUser)
                    .claim("mail", mail)
                    .claim("roles", roles)
                    .claim("last_name", last_name)
                    .claim("first_name", first_name)
                    .claim("firstConnection", firstConnection)
                    .expirationTime(Date.from(Instant.now().plusSeconds(expireHourToken * 3600)))
                    .issueTime(new Date())
                    .build();

            Payload payload = new Payload(claims.toJSONObject());

            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), payload);

            jwsObject.sign(new MACSigner(SECRET));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Erreur lors de la création du JWT", e);
        }
    }

    /**
     * Même chose que la création d'un JWT d'accès mais sans le fournisseur ni les
     * rôles.
     */
    public static String createRefreshToken(String username) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(username)
                    .expirationTime(Date.from(Instant.now().plusSeconds(expireHourRefreshToken * 3600)))
                    .build();

            Payload payload = new Payload(claims.toJSONObject());

            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256),
                    payload);

            jwsObject.sign(new MACSigner(SECRET));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Erreur lors de la création d'un JWT", e);
        }
    }

    /**
     * Analyse d'un JWT.
     *
     * @param token Le JWT à traiter
     */
    public static UsernamePasswordAuthenticationToken parseToken(String token)
            throws JOSEException, ParseException, BadJOSEException {

        byte[] secretKey = SECRET.getBytes();
        SignedJWT signedJWT = SignedJWT.parse(token);
        signedJWT.verify(new MACVerifier(secretKey));
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.HS256,
                new ImmutableSecret<>(secretKey));
        jwtProcessor.setJWSKeySelector(keySelector);
        jwtProcessor.process(signedJWT, null);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        String username = claims.getSubject();

        // récupération de la liste des rôles
        List<String> roles = (List<String>) claims.getClaim("roles");
        List<SimpleGrantedAuthority> authorities = null;
        if (roles != null) {
            authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        // classe encapsulant les information d'un utilisateur
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    public static String parseTokenToUsernameUser(String token) throws JOSEException, ParseException, BadJOSEException {
        byte[] secretKey = SECRET.getBytes();
        String tokenParsed = token.substring("Bearer ".length());

        SignedJWT signedJWT = SignedJWT.parse(tokenParsed);
        signedJWT.verify(new MACVerifier(secretKey));
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.HS256,
                new ImmutableSecret<>(secretKey));
        jwtProcessor.setJWSKeySelector(keySelector);
        jwtProcessor.process(signedJWT, null);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        return claims.getSubject();
    }

    /**
     * Analyse d'un JWT.
     * 
     * @param token Le JWT à traiter
     */
    public static String parseTokentoEmail(String authorizationHeader)
            throws JOSEException, ParseException, BadJOSEException {

        // le fameux token contient "Bearer", on s'en débarasse
        String token = authorizationHeader.substring("Bearer ".length());
        byte[] secretKey = SECRET.getBytes();
        SignedJWT signedJWT = SignedJWT.parse(token);
        signedJWT.verify(new MACVerifier(secretKey));
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.HS256,
                new ImmutableSecret<>(secretKey));
        jwtProcessor.setJWSKeySelector(keySelector);
        jwtProcessor.process(signedJWT, null);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        // récupération de la liste des rôles
        String mail = (String) claims.getClaim("mail");

        // classe encapsulant les information d'un utilisateur
        return mail;
    }
}
