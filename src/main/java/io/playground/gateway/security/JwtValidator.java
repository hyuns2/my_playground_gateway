package io.playground.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerErrorException;

@Component
public class JwtValidator {
    @Value("${jwt.secret_key}")
    private String secretKey;
    @Value("${jwt.grant_type}")
    private String grantType;
    @Value("${jwt.token_type_claim}")
    private String tokenTypeClaim;
    private static final String TOKEN_TYPE = "access";

    public void validate(String tokenWithGrantType) {
        String token = resolveToken(tokenWithGrantType);
        Claims claims = parseClaims(token);

        if (!claims.get(tokenTypeClaim).equals(TOKEN_TYPE))
            throw new IllegalArgumentException("Invalid token type");
    }

    private String resolveToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(grantType))
            return token.substring(7);
        else
            throw new IllegalArgumentException("No token or Invalid token grant type");
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(
                            Decoders.BASE64.decode(secretKey)
                    )).build()
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (Exception e) {
            throw new ServerErrorException("Failed to parse JWT token", e);
        }
    }
}
