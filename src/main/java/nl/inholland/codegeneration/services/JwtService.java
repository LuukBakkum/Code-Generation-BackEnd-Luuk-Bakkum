package nl.inholland.codegeneration.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // TODO: Error handling
    private static final String SecretKey ="566D5971337336763979244226452948404D635166546A576E5A723475377721";// TODO: get this key from key file

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public <T> T extractClaim(String jwt,Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
        .setClaims(claims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) //expires in 10 hours maybe add to config
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact(); //expires in 10 hours
    }

    public Boolean validateToken(String jwt, UserDetails userDetails)  {
        final String username = extractUsername(jwt);
      

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
    }

    private  boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts
        .parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(jwt)
        .getBody();

    }

    private Key getSigningKey() {
        byte[] keybytes = Decoders.BASE64.decode(SecretKey);
        return Keys.hmacShaKeyFor(keybytes);
    }

}
