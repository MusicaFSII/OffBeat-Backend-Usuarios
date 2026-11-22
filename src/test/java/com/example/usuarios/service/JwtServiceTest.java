package com.example.usuarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.usuarios.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private String secretBase64;
    private SecretKey clave;
    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // clave de prueba generada al vuelo (el servicio la espera en Base64)
        secretBase64 = Encoders.BASE64.encode(Jwts.SIG.HS256.key().build().getEncoded());
        clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        jwtService = new JwtService(secretBase64, 3600000L); // expira en 1 hora

        usuario = new Usuario();
        usuario.setId(7L);
        usuario.setCorreo("ana@correo.cl");
        usuario.setRol("vendedor");
    }

    @Test
    void generarToken_contieneCorreoIdYRol() {
        String token = jwtService.generarToken(usuario);

        Claims claims = Jwts.parser().verifyWith(clave).build()
                .parseSignedClaims(token).getPayload();

        assertEquals("ana@correo.cl", claims.getSubject());
        assertEquals("vendedor", claims.get("rol", String.class));
        assertEquals(7, claims.get("id", Integer.class));
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void generarToken_firmaConOtraClave_noSeAceptaComoValido() {
        String token = jwtService.generarToken(usuario);
        SecretKey otraClave = Jwts.SIG.HS256.key().build();

        assertThrows(JwtException.class,
                () -> Jwts.parser().verifyWith(otraClave).build().parseSignedClaims(token));
    }
}