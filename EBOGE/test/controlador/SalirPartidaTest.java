package controlador;

import javafx.application.Platform;
import javafx.scene.control.Button;
import modelo.Partida;
import modelo.jugador.ColorJugador;
import modelo.jugador.Jugador;
import modelo.mapa.Casilla;
import modelo.mapa.Mapa;
import modelo.mapa.TipoCasilla;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias: Funcionalidad de Salir al Menú Principal (CR-02 / FR-16)")
public class SalirPartidaTest {

    private List<Jugador> jugadores;
    private Mapa mapa;
    private Partida partida;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit ya inicializado
        } catch (Exception e) {
            // Entorno sin soporte gráfico
        }
    }

    @BeforeEach
    void setUp() {
        jugadores = new ArrayList<>();
        jugadores.add(new Jugador("Jugador Alfa", ColorJugador.ROJO));
        jugadores.add(new Jugador("Jugador Beta", ColorJugador.AZUL));

        Casilla[][] matriz = new Casilla[1][3];
        matriz[0][0] = new Casilla(TipoCasilla.INICIO, 0);
        matriz[0][1] = new Casilla(TipoCasilla.NORMAL, 1);
        matriz[0][2] = new Casilla(TipoCasilla.FINAL, 2);

        mapa = new Mapa(3, 1, matriz);
        partida = new Partida(jugadores, 6, mapa);
    }

    @Test
    @DisplayName("CR-02 / FR-16: salirAlMenuPrincipal en ControladorPrincipal resetea el estado activo de la partida")
    void testControladorPrincipalSalirAlMenuPrincipal() {
        ControladorPrincipal cp = new ControladorPrincipal();
        cp.configurarPartida(jugadores, 6);
        cp.setPartida(partida);

        // Verificamos estado activo antes de salir (evita falsa confianza - RF-02)
        assertNotNull(cp.getPartida(), "La partida activa no debe ser nula antes de salir");
        assertEquals(jugadores, cp.getJugadoresPartida(), "Los jugadores deben estar configurados inicialmente");
        assertEquals(6, cp.getCarasDadoPartida(), "Las caras del dado deben estar configuradas");

        cp.salirAlMenuPrincipal();

        assertNull(cp.getPartida(), "La partida activa debe ser nula tras salir al menú principal");
        assertNull(cp.getJugadoresPartida(), "La lista de jugadores debe ser nula tras salir");
        assertEquals(0, cp.getCarasDadoPartida(), "El contador de caras del dado debe reiniciarse a 0");
    }

    @Test
    @DisplayName("CR-02 / AC-16: ControladorVentanaDePartida delega la salida al ControladorPrincipal")
    void testControladorVentanaDePartidaDelegaSalida() {
        ControladorVentanaDePartida cvp = new ControladorVentanaDePartida();
        ControladorPrincipal cp = new ControladorPrincipal();
        cp.configurarPartida(jugadores, 8);
        cp.setPartida(partida);

        cvp.setControladorPrincipal(cp);
        assertEquals(cp, cvp.getControladorPrincipal(), "El controlador principal debe estar asociado");
        assertNotNull(cp.getPartida(), "La partida activa debe estar presente antes de delegar la salida");

        cvp.salirAlMenuPrincipal();

        assertNull(cp.getPartida(), "El ControladorPrincipal debe recibir la orden de salir y limpiar partida");
        assertNull(cp.getJugadoresPartida(), "Los jugadores deben quedar limpios");
        assertEquals(0, cp.getCarasDadoPartida(), "Caras del dado deben reiniciar");
    }

    @Test
    @DisplayName("CR-02: Componentes UI del botón salir en ControladorVentanaDePartida son accesibles")
    void testBotonesSalirEnControladorVentanaDePartida() {
        ControladorVentanaDePartida cvp = new ControladorVentanaDePartida();

        Button btnSalir = new Button("Salir al Menú");
        Button btnSalirOverlay = new Button("Salir al Menú Overlay");

        cvp.setBtnSalirMenu(btnSalir);
        cvp.setBtnSalirMenuOverlay(btnSalirOverlay);

        assertSame(btnSalir, cvp.getBtnSalirMenu(), "El botón de salir principal debe ser el asignado");
        assertSame(btnSalirOverlay, cvp.getBtnSalirMenuOverlay(), "El botón de salir del overlay debe ser el asignado");
    }

    @Test
    @DisplayName("CR-02: salirAlMenuPrincipal maneja de forma segura la ausencia de ControladorPrincipal")
    void testSalirSinControladorPrincipalNoLanzaExcepcion() {
        ControladorVentanaDePartida cvp = new ControladorVentanaDePartida();
        cvp.setControladorPrincipal(null);

        assertDoesNotThrow(() -> cvp.salirAlMenuPrincipal(),
                "Llamar a salirAlMenuPrincipal sin controlador principal no debe causar excepción");
    }
}
