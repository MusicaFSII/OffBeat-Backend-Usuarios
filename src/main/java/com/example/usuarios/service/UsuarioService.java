package com.example.usuarios.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.usuarios.dto.CambiarClaveDTO;
import com.example.usuarios.dto.LoginDTO;
import com.example.usuarios.dto.RegistroDTO;
import com.example.usuarios.dto.UsuarioDTO;
import com.example.usuarios.exception.CredencialesInvalidasException;
import com.example.usuarios.exception.UsuarioNoEncontradoException;
import com.example.usuarios.mapper.UsuarioMapper;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepo, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder){
        this.usuarioRepo = usuarioRepo;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    //obtiene todos los usuarios
    public List<Usuario> obtenerUsuarios(){
        return usuarioRepo.findAll();
    }

    //obtiene un usuario por id o error 404 si no existe
    public Usuario obtenerUsuarioById(Long id){
        return usuarioRepo.findById(id)
            .orElseThrow(UsuarioNoEncontradoException::new);
    }

    //obtiene un usuario por correo o error 404 si no existe
    public Usuario obtenerUsuarioByCorreo(String correo){
        return usuarioRepo.findByCorreo(correo)
            .orElseThrow(UsuarioNoEncontradoException::new);
    }

    //cuenta la cantidad de usuarios (metrica dashboard)
    public long contarUsuarios(){
        return usuarioRepo.count();
    }

    //buscar por el correo y deja pasar solo si la constraseña coincide con el hash
    public Usuario login(LoginDTO dto){
        return usuarioRepo.findByCorreo(dto.getCorreo())
            .filter(u -> passwordEncoder.matches(dto.getContrasena(), u.getContrasena()))
            .orElseThrow(CredencialesInvalidasException::new);
    }

    //valida el correo y las contraseñas, encripta la contraseña y guarda (el primer usuario creado tiene rol de admin)
    public Usuario registrar(RegistroDTO dto){
        if(usuarioRepo.existsByCorreo(dto.getCorreo())){
            throw new IllegalArgumentException("El correo ya esta registrado");
        }
        if(!dto.getContrasena().equals(dto.getContrasenaConf())){
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario.setRol(usuarioRepo.count() == 0 ? "admin" : "cliente");
        return usuarioRepo.save(usuario);
    }

    //verifica que el correo no sea de otro usuario y actualiza los datos (no la contraseña)
    public Usuario actualizar(Long id, UsuarioDTO dto){
        Usuario existing = obtenerUsuarioById(id);

        if(usuarioRepo.existsByCorreoAndIdNot(dto.getCorreo(), id)){
            throw new IllegalArgumentException("El correo ya esta registrado");
        }

        usuarioMapper.updateEntity(existing, dto);
        return usuarioRepo.save(existing);
    }

    //comprueba la contraseña actual y guarda la nueva encriptada
    public void cambiarClave(Long id, CambiarClaveDTO dto){
        Usuario existing = obtenerUsuarioById(id);

        if(!passwordEncoder.matches(dto.getContrasenaActual(), existing.getContrasena())){
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        existing.setContrasena(passwordEncoder.encode(dto.getContrasenaNueva()));
        usuarioRepo.save(existing);
    }

    //eliminar un usuario por id
    public void eliminar(Long id){
        if(!usuarioRepo.existsById(id)){
            throw new UsuarioNoEncontradoException();
        }
        usuarioRepo.deleteById(id);
    }
}