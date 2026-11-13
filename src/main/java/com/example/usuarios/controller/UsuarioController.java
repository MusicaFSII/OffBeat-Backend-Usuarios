package com.example.usuarios.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.usuarios.dto.CambiarClaveDTO;
import com.example.usuarios.dto.LoginDTO;
import com.example.usuarios.dto.LoginRespuestaDTO;
import com.example.usuarios.dto.RegistroDTO;
import com.example.usuarios.dto.UsuarioDTO;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")
@CrossOrigin(origins = "*")

public class UsuarioController{
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }


    /*##-------------------------------METODOS GET-------------------------------##*/
    //obtiene todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerUsuarios(){
        return ResponseEntity.ok(usuarioService.obtenerUsuarios());
    }

    //cuenta el total de usuarios
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> contarUsuarios(){
        return ResponseEntity.ok(Map.of("total", usuarioService.contarUsuarios()));
    }

    //busca a un usuario por id (error 404 si no existe)
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioById(@PathVariable("id") Long id){
        return ResponseEntity.ok(usuarioService.obtenerUsuarioById(id));
    }

    //busca a un usuario por correo 
    @GetMapping("/correo/{correo}")
    public ResponseEntity<Usuario> obtenerUsuarioByCorreo(@PathVariable("correo") String correo){
       return ResponseEntity.ok(usuarioService.obtenerUsuarioByCorreo(correo));
    }


    /*##-------------------------------METODOS POST-------------------------------##*/
    //inicia sesion (error 401 si las credenciales estan mal)
    @PostMapping("/login")
    public ResponseEntity<LoginRespuestaDTO> login(@Valid @RequestBody LoginDTO dto){
        return ResponseEntity.ok(usuarioService.login(dto));
    }

    //crea un nuevo usuario
    @PostMapping("/registro")
    public ResponseEntity<Usuario> registro(@Valid @RequestBody RegistroDTO dto){
        Usuario nuevo = usuarioService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }


    /*##-------------------------------METODOS PUT-------------------------------##*/
    //edita los datos del usuario
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable("id") Long id, @Valid @RequestBody UsuarioDTO dto){
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }

    //cambia la contraseña del usuario
    @PutMapping("/cambiar-clave/{id}")
    public ResponseEntity<Void> cambiarClave(@PathVariable("id") Long id, @Valid @RequestBody CambiarClaveDTO dto){
        usuarioService.cambiarClave(id, dto);
        return ResponseEntity.noContent().build();
    }


    /*##-------------------------------METODOS DELETE-------------------------------##*/
    //elimina al usuario
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id){
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}