package com.generation.blogpessoal.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Component

public class JwtService {
	
	//constante que gera uma chave para encodar as informações do token
	public static final String SECRET = "f0440dc3ad6ee715355c06fda16af51d6504d93a4664821a091d8ecf0953505a";
	
	//TOKEN do site https://www.keygen.io/#fakeLink -> SHA 256-bit Key (ingrid@gmail.com 2024-06-04 9:40 assinatura
	
	//assinatura do token
	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	//Claims - trabalha as declarações (usuário, data de expiração, assinatura) do token.
	//Nesse caso, assinatura
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()).build()
				.parseClaimsJws(token).getBody();
	}
	//pega a assinatura extraída e trata ela para tornar ela entendível
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	//recuperar os dados da parte sub do claim onde encontramos o e-mail(usuário)
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	//data que o token expira
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	//valida se a data que o token expira esta dentro da validade, ou seja, a data atual ainda não atingiu o limite
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	//validar se o usuário que foi extraído do token condiz com o usuário que a userDetails tem e se esta dentro da data de validade ainda o token
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	//objetivo é calcular o tempo de validade do token, formar o claim com as informações do token
	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder()
					.setClaims(claims)
					.setSubject(userName)
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
					.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}
	//gerar o token puxando os calims formados no metodo anterior
	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userName);
	}
}
