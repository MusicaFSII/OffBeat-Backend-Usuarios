package com.example.usuarios.controller;

import java.util.List;
import java.net.URI;

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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.usuarios.dto.ComentarioDTO;
import com.example.usuarios.model.Comentario;
import com.example.usuarios.service.ComentarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService){
        this.comentarioService = comentarioService;
    }

    //obtine todos los comentarios
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Lista de comentarios")
    public ResponseEntity<List<Comentario>> obtenerComentarios(){
        return ResponseEntity.ok(comentarioService.obtenerComentarios());
    }

    //guardar un comentario del formulario de contacto
    @PostMapping
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    public ResponseEntity<Comentario> registrarComentario(@Valid @RequestBody ComentarioDTO dto){
        Comentario nuevo = comentarioService.registrarComentario(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(nuevo.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(nuevo);
    }
}