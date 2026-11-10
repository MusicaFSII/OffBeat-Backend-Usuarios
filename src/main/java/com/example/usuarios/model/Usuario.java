package com.example.usuarios.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Usuario{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    private Long id;

    @Column(name="nombre", nullable = false)
    private String nombre;

    @Column(name="apellido", nullable = false)
    private String apellido;

    @Column(name="correo",unique = true, nullable = false)
    private String correo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    @Column(name="contrasena", nullable = false)
    private String contrasena;

    @Column(name="rut", nullable = false)
    private String rut;

    @Column(name="telefono", nullable = false)
    private String telefono;

    @Column(name="region", nullable = false)
    private String region;

    @Column(name="comuna", nullable = false)
    private String comuna;

    @Column(name="rol", nullable = false)
    private String rol = "cliente";
}