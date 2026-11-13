package com.example.usuarios.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.usuarios.model.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service 
public class JwtService {
    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration}") long expiration
    ){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    public String generarToken(Usuario usuario){
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime()+expiration);

        return Jwts.builder()
            .subject(usuario.getCorreo())
            .claim("id", usuario.getId())
            .claim("rol", usuario.getRol())
            .issuedAt(ahora)
            .expiration(expiracion)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();

    }
}
