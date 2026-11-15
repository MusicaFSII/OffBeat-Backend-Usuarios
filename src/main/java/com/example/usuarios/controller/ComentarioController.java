package com.example.usuarios.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.usuarios.dto.ComentarioDTO;
import com.example.usuarios.model.Comentario;
import com.example.usuarios.service.ComentarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comentarios")
@CrossOrigin(origins = "*")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService){
        this.comentarioService = comentarioService;
    }

    //obtine todos los comentarios
    @GetMapping
    public ResponseEntity<List<Comentario>> obtenerComentarios(){
        return ResponseEntity.ok(comentarioService.obtenerComentarios());
    }

    //guardar un comentario del formulario de contacto
    @PostMapping("/guardar")
    public ResponseEntity<Comentario> registrarComentario(@Valid @RequestBody ComentarioDTO dto){
        Comentario nuevo = comentarioService.registrarComentario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
}