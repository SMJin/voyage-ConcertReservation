package kr.hhplus.be.server.common.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kr.hhplus.be.server.auth.entity.RefreshToken;
import kr.hhplus.be.server.auth.repository.RefreshTokenRepository;
import kr.hhplus.be.server.user.entity.CustomUserDetails;
import kr.hhplus.be.server.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService userDetailsService;

    private final String SECRET_KEY = "8mlc7DZiAcVsJRvIy7AsPaJNaASDuGFx";
    private final long accessTokenValidity = 15 * 60 * 1000L; // 15분
    private final long refreshTokenValidity = 7 * 24 * 60 * 60 * 1000L; // 7일

    public String createAccessToken(String username, List<String> roles) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + accessTokenValidity);
        return createToken(username, null, now, expiredDate);
    }

    public String createRefreshToken(String username) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + refreshTokenValidity);
        String rtk = createToken(username, null, now, expiredDate);

        RefreshToken refreshToken = RefreshToken.builder()
                .username(username)
                .token(rtk)
                .expires_at(expiredDate)
                .build();
        refreshTokenRepository.save(refreshToken);

        return rtk;
    }

    private String createToken(String username, List<String> roles, Date now, Date expiredDate) {
        Claims claims = Jwts.claims().setSubject(username);
        if (roles != null) claims.put("roles", roles);

        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token)
                .getSubject();
    }

    public String getTokenValidity(String token) {
        return getClaims(token)
                .getExpiration()
                .toString();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

}
