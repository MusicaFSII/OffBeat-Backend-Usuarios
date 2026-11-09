/*pasa los datos de los DTO a la entidad */

package com.example.usuarios.mapper;

import org.springframework.stereotype.Component;

import com.example.usuarios.dto.RegistroDTO;
import com.example.usuarios.dto.UsuarioDTO;
import com.example.usuarios.model.Usuario;

@Component
public class UsuarioMapper {

    //convierte el registro en un usuario nuevo (la contraseña y el rol los asigna el service)
    public Usuario toEntity(RegistroDTO dto){
        if(dto == null){
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setRut(dto.getRut());
        usuario.setCorreo(dto.getCorreo());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRegion(dto.getRegion());
        usuario.setComuna(dto.getComuna());
        return usuario;
    }

    //copia los datos editados sobre un usuario que ya existe (no toca la contraseña)
    public void updateEntity(Usuario usuario, UsuarioDTO dto){
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setRut(dto.getRut());
        usuario.setCorreo(dto.getCorreo());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRegion(dto.getRegion());
        usuario.setComuna(dto.getComuna());
        usuario.setRol(dto.getRol());
    }
}