package com.example.usuarios.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.usuarios.dto.CambiarClaveDTO;
import com.example.usuarios.dto.GoogleLoginDTO;
import com.example.usuarios.dto.LoginDTO;
import com.example.usuarios.dto.LoginRespuestaDTO;
import com.example.usuarios.dto.RegistroDTO;
import com.example.usuarios.dto.UsuarioDTO;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.service.UsuarioService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")

public class UsuarioController{
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }


    /*##-------------------------------METODOS GET-------------------------------##*/
    //obtiene todos los usuarios
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Lista de usuarios")
    public ResponseEntity<List<Usuario>> obtenerUsuarios(){
        return ResponseEntity.ok(usuarioService.obtenerUsuarios());
    }

    //cuenta el total de usuarios
    @GetMapping("/count")
    @ApiResponse(responseCode = "200", description = "Total de usuarios")
    public ResponseEntity<Map<String, Object>> contarUsuarios(){
        return ResponseEntity.ok(Map.of("total", usuarioService.contarUsuarios()));
    }

    //busca a un usuario por id (error 404 si no existe)
    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "El usuario no existe")
    public ResponseEntity<Usuario> obtenerUsuarioById(@PathVariable("id") Long id){
        return ResponseEntity.ok(usuarioService.obtenerUsuarioById(id));
    }

    //busca a un usuario por correo 
    @GetMapping("/correo/{correo}")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "El usuario no existe")
    public ResponseEntity<Usuario> obtenerUsuarioByCorreo(@PathVariable("correo") String correo){
       return ResponseEntity.ok(usuarioService.obtenerUsuarioByCorreo(correo));
    }                                                                                                                       


    /*##-------------------------------METODOS POST-------------------------------##*/
    //inicia sesion (error 401 si las credenciales estan mal)
    @PostMapping("/login")
    @ApiResponse(responseCode = "200", description = "Sesion iniciada")
    @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos")
    public ResponseEntity<LoginRespuestaDTO> login(@Valid @RequestBody LoginDTO dto){
        return ResponseEntity.ok(usuarioService.login(dto));
    }

    //iniciar sesión con Google
    @PostMapping("/login/google")
    @ApiResponse(responseCode = "200", description = "Sesion iniciada")
    @ApiResponse(responseCode = "401", description = "Token de Google invalido")
    @ApiResponse(responseCode = "404", description = "La cuenta de Google no esta registrada")
    public ResponseEntity<LoginRespuestaDTO> loginConGoogle(@Valid @RequestBody GoogleLoginDTO dto){
        return ResponseEntity.ok(usuarioService.loginConGoogle(dto));
    }

    //crea un nuevo usuario
    @PostMapping
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos o correo ya registrado")
    public ResponseEntity<Usuario> registro(@Valid @RequestBody RegistroDTO dto){
        Usuario nuevo = usuarioService.registrar(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(nuevo.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(nuevo);
    }


    /*##-------------------------------METODOS PUT-------------------------------##*/
    //edita los datos del usuario
    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos o correo ya registrado")
    @ApiResponse(responseCode = "404", description = "El usuario no existe")
    public ResponseEntity<Usuario> actualizar(@PathVariable("id") Long id, @Valid @RequestBody UsuarioDTO dto){
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }

    //cambia la contraseña del usuario
    @PutMapping("/{id}/clave")
    @ApiResponse(responseCode = "204", description = "Contraseña actualizada")
    @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta o datos invalidos")
    @ApiResponse(responseCode = "404", description = "El usuario no existe")
    public ResponseEntity<Void> cambiarClave(@PathVariable("id") Long id, @Valid @RequestBody CambiarClaveDTO dto){
        usuarioService.cambiarClave(id, dto);
        return ResponseEntity.noContent().build();
    }


    /*##-------------------------------METODOS DELETE-------------------------------##*/
    //elimina al usuario
    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    @ApiResponse(responseCode = "404", description = "El usuario no existe")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id){
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}