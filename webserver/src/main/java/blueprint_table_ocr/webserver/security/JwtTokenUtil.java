package blueprint_table_ocr.webserver.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;



@Component
public class JwtTokenUtil {
	private final String SECRET_KEY = "secretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKey";//어케할지 생각해보자
	Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
	//private final SecretKey SECRET_KEY =Keys.secretKeyFor(SignatureAlgorithm.HS256);  
	//Key key = SECRET_KEY;
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour
    
     
    public String generateToken(Authentication authentication) {//토큰 만드는거
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        return Jwts.builder()// 헤더는 alg과 typ으로 구성->자동 생성
        		.setHeaderParam("typ", "JWT")//헤더에 typ추가
                .setClaims(claims)//권한+@추가
                .setSubject(userDetails.getUsername())//이름
                .setIssuedAt(new Date())//발행시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))//만료시간
                .signWith(key,SignatureAlgorithm.HS256)//signature//대칭키 알고리즘과sha-256
                .compact();
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {//토근의 유효성 검증
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {//유저네임 가져오기
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {//기한 가져오기
        return extractClaim(token, Claims::getExpiration);
    }
    
    public Object extractRole(String token) {
        return extractClaim(token, claims -> claims.get("roles"));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {//JWT 토큰을 파싱하여 모든 클레임을 Claims 객체로 반환
        return Jwts.parserBuilder()
                .setSigningKey(key)//키를 설정햄
                .build() 
                .parseClaimsJws(token)//이 객체는 서명된 JWT의 헤더, 본문(클레임), 서명을 포함합니다. 주어진 토큰이 유효하지 않거나 서명이 올바르지 않으면 예외가 발생
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
