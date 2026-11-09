package com.example.usuarios.exception;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(){
        super("Usuario o contraseña incorrectos");
    }
}