package com.example.usuarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.usuarios.client.GoogleClient;
import com.example.usuarios.dto.CambiarClaveDTO;
import com.example.usuarios.dto.LoginDTO;
import com.example.usuarios.dto.LoginRespuestaDTO;
import com.example.usuarios.dto.RegistroDTO;
import com.example.usuarios.exception.CredencialesInvalidasException;
import com.example.usuarios.exception.UsuarioNoEncontradoException;
import com.example.usuarios.mapper.UsuarioMapper;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    // Dependencias simuladas: no se usa base de datos ni Google real
    @Mock private UsuarioRepository usuarioRepo;
    @Mock private UsuarioMapper usuarioMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private GoogleClient googleClient;
    @Mock private JwtService jwtService;

    // Mockito crea el servicio e inyecta los mocks de arriba en su constructor
    @InjectMocks
    private UsuarioService usuarioService;

    // ---------- helpers ----------
    private Usuario crearUsuario() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNombre("Ana");
        u.setApellido("Perez");
        u.setCorreo("ana@correo.cl");
        u.setContrasena("hashGuardado");
        u.setRol("cliente");
        return u;
    }

    private RegistroDTO crearRegistroDTO(String clave, String confirmacion) {
        RegistroDTO dto = new RegistroDTO();
        dto.setNombre("Ana");
        dto.setApellido("Perez");
        dto.setRut("12345678K");
        dto.setCorreo("ana@correo.cl");
        dto.setContrasena(clave);
        dto.setContrasenaConf(confirmacion);
        dto.setTelefono("12345678");
        dto.setRegion("RM");
        dto.setComuna("Santiago");
        return dto;
    }

    // ---------- buscar ----------
    @Test
    void obtenerUsuarioById_existente_retornaUsuario() {
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(crearUsuario()));

        Usuario resultado = usuarioService.obtenerUsuarioById(1L);

        assertEquals("ana@correo.cl", resultado.getCorreo());
    }

    @Test
    void obtenerUsuarioById_inexistente_lanzaUsuarioNoEncontrado() {
        when(usuarioRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> usuarioService.obtenerUsuarioById(99L));
    }

    // ---------- login ----------
    @Test
    void login_credencialesCorrectas_retornaTokenYDatos() {
        Usuario u = crearUsuario();
        LoginDTO dto = new LoginDTO("ana@correo.cl", "1234");
        when(usuarioRepo.findByCorreo("ana@correo.cl")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("1234", "hashGuardado")).thenReturn(true);
        when(jwtService.generarToken(u)).thenReturn("token123");

        LoginRespuestaDTO respuesta = usuarioService.login(dto);

        assertEquals("token123", respuesta.getToken());
        assertEquals("Bearer", respuesta.getTipo());
        assertEquals("cliente", respuesta.getRol());
    }

    @Test
    void login_contrasenaIncorrecta_lanzaCredencialesInvalidas() {
        LoginDTO dto = new LoginDTO("ana@correo.cl", "mala");
        when(usuarioRepo.findByCorreo("ana@correo.cl")).thenReturn(Optional.of(crearUsuario()));
        when(passwordEncoder.matches("mala", "hashGuardado")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class, () -> usuarioService.login(dto));
        // si falla el login, nunca se debe generar un token
        verify(jwtService, never()).generarToken(any());
    }

    // ---------- registro ----------
    @Test
    void registrar_primerUsuario_quedaComoAdminYContrasenaEncriptada() {
        RegistroDTO dto = crearRegistroDTO("1234", "1234");
        when(usuarioRepo.existsByCorreo("ana@correo.cl")).thenReturn(false);
        when(usuarioMapper.toEntity(dto)).thenReturn(new Usuario());
        when(passwordEncoder.encode("1234")).thenReturn("hashNuevo");
        when(usuarioRepo.count()).thenReturn(0L);
        when(usuarioRepo.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = usuarioService.registrar(dto);

        assertEquals("admin", resultado.getRol());
        assertEquals("hashNuevo", resultado.getContrasena());
        assertNotEquals("1234", resultado.getContrasena());
    }

    @Test
    void registrar_usuarioPosterior_quedaComoCliente() {
        RegistroDTO dto = crearRegistroDTO("1234", "1234");
        when(usuarioRepo.existsByCorreo("ana@correo.cl")).thenReturn(false);
        when(usuarioMapper.toEntity(dto)).thenReturn(new Usuario());
        when(passwordEncoder.encode("1234")).thenReturn("hashNuevo");
        when(usuarioRepo.count()).thenReturn(5L);
        when(usuarioRepo.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = usuarioService.registrar(dto);

        assertEquals("cliente", resultado.getRol());
    }

    @Test
    void registrar_correoRepetido_lanzaExcepcionYNoGuarda() {
        RegistroDTO dto = crearRegistroDTO("1234", "1234");
        when(usuarioRepo.existsByCorreo("ana@correo.cl")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.registrar(dto));
        verify(usuarioRepo, never()).save(any());
    }

    @Test
    void registrar_contrasenasDistintas_lanzaExcepcionYNoGuarda() {
        RegistroDTO dto = crearRegistroDTO("1234", "9999");
        when(usuarioRepo.existsByCorreo("ana@correo.cl")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.registrar(dto));
        verify(usuarioRepo, never()).save(any());
    }

    // ---------- cambiar clave ----------
    @Test
    void cambiarClave_claveActualIncorrecta_lanzaExcepcionYNoGuarda() {
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(crearUsuario()));
        when(passwordEncoder.matches("equivocada", "hashGuardado")).thenReturn(false);

        CambiarClaveDTO dto = new CambiarClaveDTO("equivocada", "nueva");

        assertThrows(IllegalArgumentException.class, () -> usuarioService.cambiarClave(1L, dto));
        verify(usuarioRepo, never()).save(any());
    }

    // ---------- eliminar ----------
    @Test
    void eliminar_existente_llamaDeleteById() {
        when(usuarioRepo.existsById(1L)).thenReturn(true);

        usuarioService.eliminar(1L);

        verify(usuarioRepo).deleteById(1L);
    }

    @Test
    void eliminar_inexistente_lanzaExcepcionYNoElimina() {
        when(usuarioRepo.existsById(99L)).thenReturn(false);

        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.eliminar(99L));
        verify(usuarioRepo, never()).deleteById(any());
    }
}