package com.example.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class LoginRespuestaDTO {
    
    private String token;
    private String tipo;
    private Long id;
    private String correo;
    private String rol;
}
