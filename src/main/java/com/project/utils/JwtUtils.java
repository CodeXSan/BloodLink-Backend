package com.project.utils;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.project.security.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	 private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	  @Value("${app.jwtSecret}")
	  private String jwtSecret;

	  @Value("${app.jwtExpirationMs}")
	  private int jwtExpirationMs;

	  public String generateJwtToken(Authentication authentication) {

	    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
	    logger.debug("Generating token for user: {}", userPrincipal.getUsername());
	    return Jwts.builder()
	    		.setSubject((userPrincipal.getUsername()))
	    		.setIssuedAt(new Date())
	    		.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
	    		.signWith(key(), SignatureAlgorithm.HS256)
	        .compact();
	  }
	  
	  private Key key() {
	    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	  }

	  public String getUserNameFromJwtToken(String token) {
	    return Jwts.parser().setSigningKey(key()).build()
	               .parseClaimsJws(token).getBody().getSubject();
	  }

	  public boolean validateJwtToken(String authToken) {
	    try {
	      Jwts.parser().setSigningKey(key()).build().parse(authToken);
	      logger.debug("Validating token for user: {}", getUserNameFromJwtToken(authToken));
	      return true;
	    } catch (MalformedJwtException e) {
	      logger.error("Invalid JWT token: {}", e.getMessage());
	    } catch (ExpiredJwtException e) {
	      logger.error("JWT token is expired: {}", e.getMessage());
	    } catch (UnsupportedJwtException e) {
	      logger.error("JWT token is unsupported: {}", e.getMessage());
	    } catch (IllegalArgumentException e) {
	      logger.error("JWT claims string is empty: {}", e.getMessage());
	    }

	    return false;
	  }
}