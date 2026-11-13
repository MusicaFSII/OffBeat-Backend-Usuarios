/*este lo usa el admin para editar el usuario.*/
package com.example.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(regexp = "\\d{7,8}[0-9kK]", message = "RUT invalido (ejemplo: 12345678K)")
    private String rut;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato valido")
    private String correo;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @NotBlank(message = "La region es obligatoria")
    private String region;

    @NotBlank(message = "La comuna es obligatoria")
    private String comuna;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "cliente|admin", message = "El rol debe ser cliente o admin")
    private String rol;
}