package com.example.usuarios.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.example.usuarios.dto.ComentarioDTO;
import com.example.usuarios.model.Comentario;
import com.example.usuarios.repository.ComentarioRepository;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepo;

    public ComentarioService(ComentarioRepository comentarioRepo){
        this.comentarioRepo = comentarioRepo;
    }

    //obtiene todos los comentarios, el mas reciente primero
    public List<Comentario> obtenerComentarios(){
        return comentarioRepo.findAllByOrderByIdDesc();
    }
    
    //guarda un comentario del formulario de contacto
    public Comentario registrarComentario(ComentarioDTO dto){
        Comentario comentario = new Comentario();
        comentario.setCorreo(dto.getCorreo());
        comentario.setNombre(dto.getNombre());
        comentario.setApellido(dto.getApellido());
        comentario.setComentario(dto.getComentario());
        return comentarioRepo.save(comentario);
    }
}