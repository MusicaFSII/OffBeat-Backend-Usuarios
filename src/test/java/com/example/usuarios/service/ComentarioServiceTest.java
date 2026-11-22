package com.example.usuarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.usuarios.dto.ComentarioDTO;
import com.example.usuarios.model.Comentario;
import com.example.usuarios.repository.ComentarioRepository;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceTest {

    // Repositorio simulado: no se usa base de datos real
    @Mock
    private ComentarioRepository comentarioRepo;

    // Mockito crea el servicio real e inyecta el repositorio falso
    @InjectMocks
    private ComentarioService comentarioService;

    @Test
    void obtenerComentarios_retornaLaListaDelRepositorioEnSuOrden() {
        Comentario reciente = new Comentario(2L, "b@correo.cl", "Beto", "Soto", "Segundo");
        Comentario antiguo = new Comentario(1L, "a@correo.cl", "Ana", "Perez", "Primero");
        when(comentarioRepo.findAllByOrderByIdDesc()).thenReturn(List.of(reciente, antiguo));

        List<Comentario> resultado = comentarioService.obtenerComentarios();

        assertEquals(2, resultado.size());
        // el servicio no reordena: respeta "el mas reciente primero" del repositorio
        assertEquals(2L, resultado.get(0).getId());
    }

    @Test
    void obtenerComentarios_sinComentarios_retornaListaVacia() {
        when(comentarioRepo.findAllByOrderByIdDesc()).thenReturn(List.of());

        List<Comentario> resultado = comentarioService.obtenerComentarios();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void registrarComentario_copiaLosDatosDelDTOYGuarda() {
        ComentarioDTO dto = new ComentarioDTO("ana@correo.cl", "Ana", "Perez", "Muy buena tienda");
        Comentario guardado = new Comentario(10L, "ana@correo.cl", "Ana", "Perez", "Muy buena tienda");
        when(comentarioRepo.save(any(Comentario.class))).thenReturn(guardado);

        Comentario resultado = comentarioService.registrarComentario(dto);

        // captura el objeto que el servicio le paso al repositorio
        ArgumentCaptor<Comentario> captor = ArgumentCaptor.forClass(Comentario.class);
        verify(comentarioRepo).save(captor.capture());
        Comentario enviado = captor.getValue();

        assertEquals("ana@correo.cl", enviado.getCorreo());
        assertEquals("Ana", enviado.getNombre());
        assertEquals("Perez", enviado.getApellido());
        assertEquals("Muy buena tienda", enviado.getComentario());
        // y devuelve lo que retorno el repositorio (ya con id)
        assertEquals(10L, resultado.getId());
    }
}