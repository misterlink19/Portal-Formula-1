package com.portal.formula1;

import com.portal.formula1.model.Equipo;
import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.repository.UsuarioRegistradoDAO;
import com.portal.formula1.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTests {

    @Mock
    private UsuarioRegistradoDAO usuarioRegistradoDAO;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRegistrado usuario;

    @BeforeEach
    void setUp() {
        usuario = new UsuarioRegistrado();
        usuario.setUsuario("user1");
        usuario.setRol(Rol.USUARIO_BASICO);
    }

    @Test
    void registrarUsuario_DebeGuardarUsuarioConRolBasico() {
        // Given
        usuarioService.registrarUsuario(usuario);

        // Then
        verify(usuarioRegistradoDAO).save(usuario);
        assertEquals(Rol.USUARIO_BASICO, usuario.getRol());
    }

    @Test
    void registrarUsuario_UsuarioConRolDistinto_DebeGuardarUsuarioConRolSolicitado() {
        // Given
        usuario.setRol(Rol.JEFE_DE_EQUIPO);

        // When
        usuarioService.registrarUsuario(usuario);

        // Then
        verify(usuarioRegistradoDAO).save(usuario);
        assertEquals(Rol.USUARIO_BASICO, usuario.getRol());
        assertEquals(Rol.JEFE_DE_EQUIPO, usuario.getRolSolicitado());
    }

    @Test
    void actualizarUsuario_DebeGuardarUsuario() {
        // Given
        usuarioService.actualizarUsuario(usuario);

        // Then
        verify(usuarioRegistradoDAO).save(usuario);
    }

    @Test
    void validarUsuarios_DebeActualizarValidacion() {
        // Given
        String userId = "user1";
        List<String> usuariosIds = Arrays.asList(userId);
        usuario.setValidacion(false);
        when(usuarioRegistradoDAO.findById(userId)).thenReturn(Optional.of(usuario));

        // When
        usuarioService.validarUsuarios(usuariosIds);

        // Then
        assertTrue(usuario.isValidacion());
        verify(usuarioRegistradoDAO).save(usuario);
    }

    @Test
    void validarUsuariosRol_DebeActualizarRoles() {
        // Given
        String userId = "user1";
        List<String> usuariosIds = Arrays.asList(userId);
        usuario.setRolSolicitado(Rol.JEFE_DE_EQUIPO);
        when(usuarioRegistradoDAO.findById(userId)).thenReturn(Optional.of(usuario));

        // When
        usuarioService.validarUsuariosRol(usuariosIds);

        // Then
        assertEquals(Rol.JEFE_DE_EQUIPO, usuario.getRol());
        assertNull(usuario.getRolSolicitado());
        verify(usuarioRegistradoDAO).save(usuario);
    }

    @Test
    void obtenerTodosLosUsuarios_DebeRetornarTodosLosUsuarios() {
        // Given
        List<UsuarioRegistrado> usuarios = Arrays.asList(usuario);
        when(usuarioRegistradoDAO.findAll()).thenReturn(usuarios);

        // When
        List<UsuarioRegistrado> resultado = usuarioService.obtenerTodosLosUsuarios();

        // Then
        assertEquals(usuarios, resultado);
    }

    @Test
    void obtenerUsuarioPorUsuario_DebeRetornarUsuario() {
        // Given
        when(usuarioRegistradoDAO.findById("user1")).thenReturn(Optional.of(usuario));

        // When
        UsuarioRegistrado resultado = usuarioService.obtenerUsuarioPorUsuario("user1");

        // Then
        assertEquals(usuario, resultado);
    }

    @Test
    void filtrarPorRol_DebeRetornarUsuariosFiltrados() {
        // Given
        List<UsuarioRegistrado> usuarios = Arrays.asList(usuario);
        when(usuarioRegistradoDAO.findByRol("USUARIO_BASICO")).thenReturn(usuarios);

        // When
        List<UsuarioRegistrado> resultado = usuarioService.filtrarPorRol("USUARIO_BASICO");

        // Then
        assertEquals(usuarios, resultado);
    }

    @Test
    void buscarPorNombre_DebeRetornarUsuarios() {
        // Given
        List<UsuarioRegistrado> usuarios = Arrays.asList(usuario);
        when(usuarioRegistradoDAO.findByNombreContainingIgnoreCase("Nombre")).thenReturn(usuarios);

        // When
        List<UsuarioRegistrado> resultado = usuarioService.buscarPorNombre("Nombre");

        // Then
        assertEquals(usuarios, resultado);
    }

    @Test
    void obtenerUsuariosNoValidados_DebeRetornarUsuariosNoValidados() {
        // Given
        usuario.setValidacion(false);
        List<UsuarioRegistrado> usuarios = Arrays.asList(usuario);
        when(usuarioRegistradoDAO.findByValidacionFalse()).thenReturn(usuarios);

        // When
        List<UsuarioRegistrado> resultado = usuarioService.obtenerUsuariosNoValidados();

        // Then
        assertEquals(usuarios, resultado);
    }

    @Test
    void ordenarPorFechaRegistro_DebeRetornarUsuariosOrdenados() {
        // Given
        List<UsuarioRegistrado> usuarios = Arrays.asList(usuario);
        when(usuarioRegistradoDAO.findAllByOrderByFechaRegistroDesc()).thenReturn(usuarios);

        // When
        List<UsuarioRegistrado> resultado = usuarioService.ordenarPorFechaRegistro();

        // Then
        assertEquals(usuarios, resultado);
    }

    @Test
    void filtrarPorRolYNombre_DebeRetornarUsuariosFiltrados() {
        // Given
        List<UsuarioRegistrado> usuarios = Arrays.asList(usuario);
        when(usuarioRegistradoDAO.findByRolAndNombreContainingIgnoreCase("USUARIO_BASICO", "Nombre")).thenReturn(usuarios);

        // When
        List<UsuarioRegistrado> resultado = usuarioService.filtrarPorRolYNombre("USUARIO_BASICO", "Nombre");

        // Then
        assertEquals(usuarios, resultado);
    }

    @Test
    void obtenerUsuarioPorIdEquipo_DebeRetornarUsuarios() {
        // Given
        Long idEquipo = 1L;
        List<UsuarioRegistrado> usuarios = Arrays.asList(usuario);
        when(usuarioRegistradoDAO.findByEquipo_Id(idEquipo)).thenReturn(usuarios);

        // When
        List<UsuarioRegistrado> resultado = usuarioService.obtenerUsuarioPorIdEquipo(idEquipo);

        // Then
        assertEquals(usuarios, resultado);
    }

    @Test
    void eliminarUsuarioRespondable_DebeEliminarUsuario() {
        // Given
        String userId = "user1";

        // When
        usuarioService.eliminarUsuarioRespondable(userId);

        // Then
        verify(usuarioRegistradoDAO).deleteUsuarioRegistradoByUsuario(userId);
    }

    @Test
    void crearResponsableEquipo_DebeCrearResponsable() {
        // Given
        UsuarioRegistrado creador = crearUsuarioJefeEquipo();
        UsuarioRegistrado nuevoResponsable = new UsuarioRegistrado();
        nuevoResponsable.setUsuario("resp1");
        nuevoResponsable.setNombre("Responsable 1");
        nuevoResponsable.setEmail("responsable1@example.com");

        when(usuarioRegistradoDAO.findById("resp1")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        UsuarioRegistrado resultado = usuarioService.crearResponsableEquipo(creador, nuevoResponsable);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getContrasena());
        verify(usuarioRegistradoDAO).save(any(UsuarioRegistrado.class));
    }


    @Test
    void cambiarContrasena_DebeCambiarContrasenaCorrectamente() {
        // Given
        String usuarioId = "user1";
        String contrasenaActual = "oldPass";
        String nuevaContrasena = "newPass";
        String confirmarContrasena = "newPass";

        usuario.setContrasena("oldEncodedPass");
        when(usuarioRegistradoDAO.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(contrasenaActual, usuario.getContrasena())).thenReturn(true);
        when(passwordEncoder.encode(nuevaContrasena)).thenReturn("newEncodedPass");

        // When
        usuarioService.cambiarContrasena(usuarioId, contrasenaActual, nuevaContrasena, confirmarContrasena);

        // Then
        verify(usuarioRegistradoDAO).actualizarContrasena(usuarioId, "newEncodedPass");
    }

    @Test
    void eliminarUsuario_DebeEliminarUsuarioCorrectamente() {
        // Given
        String usuarioAEliminar = "user1";
        String usuarioAdmin = "admin1";

        UsuarioRegistrado admin = new UsuarioRegistrado();
        admin.setUsuario(usuarioAdmin);
        admin.setRol(Rol.ADMIN);

        when(usuarioRegistradoDAO.findById(usuarioAEliminar)).thenReturn(Optional.of(usuario));
        when(usuarioRegistradoDAO.findById(usuarioAdmin)).thenReturn(Optional.of(admin));

        // When
        usuarioService.eliminarUsuario(usuarioAEliminar, usuarioAdmin);

        // Then
        verify(usuarioRegistradoDAO).delete(usuario);
    }

    @Test
    void darseDeBaja_DebeDarseDeBajaCorrectamente() {
        // Given
        String usuarioAEliminar = "user1";

        when(usuarioRegistradoDAO.findById(usuarioAEliminar)).thenReturn(Optional.of(usuario));

        // When
        usuarioService.darseDeBaja(usuarioAEliminar);

        // Then
        verify(usuarioRegistradoDAO).delete(usuario);
    }

    //Metodos auxiliares
    private UsuarioRegistrado crearUsuarioRegistrado(String usuario, String nombre, Rol rol) {
        UsuarioRegistrado usuarioRegistrado = new UsuarioRegistrado();
        usuarioRegistrado.setUsuario(usuario);
        usuarioRegistrado.setNombre(nombre);
        usuarioRegistrado.setRol(rol);
        return usuarioRegistrado;
    }

    private UsuarioRegistrado crearUsuarioJefeEquipo() {
        UsuarioRegistrado jefeEquipo = crearUsuarioRegistrado("jefe1", "Jefe Equipo", Rol.JEFE_DE_EQUIPO);
        jefeEquipo.setEquipo(new Equipo());
        jefeEquipo.getEquipo().setId(1L);
        return jefeEquipo;
    }
}