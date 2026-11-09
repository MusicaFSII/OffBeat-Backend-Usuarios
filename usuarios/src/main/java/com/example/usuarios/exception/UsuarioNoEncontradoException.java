package com.example.usuarios.exception;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(){
        super("Usuario no encontrado");
    }
}