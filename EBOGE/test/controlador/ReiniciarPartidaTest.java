package controlador;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import modelo.AccionCasillaVictoria;
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

@DisplayName("Pruebas Unitarias: Funcionalidad de Reinicio de Partida (CR-01 / RF-13)")
public class ReiniciarPartidaTest {

    private List<Jugador> jugadores;
    private Mapa mapa;
    private Partida partida;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Ya inicializado
        } catch (Exception e) {
            // Entorno sin soporte gráfico
        }
    }

    @BeforeEach
    void setUp() {
        jugadores = new ArrayList<>();
        jugadores.add(new Jugador("Jugador 1", ColorJugador.ROJO));
        jugadores.add(new Jugador("Jugador 2", ColorJugador.AZUL));

        Casilla[][] matriz = new Casilla[1][5];
        matriz[0][0] = new Casilla(TipoCasilla.INICIO, 0);
        matriz[0][1] = new Casilla(TipoCasilla.NORMAL, 1);
        matriz[0][2] = new Casilla(TipoCasilla.NORMAL, 2);
        matriz[0][3] = new Casilla(TipoCasilla.NORMAL, 3);
        matriz[0][4] = new Casilla(TipoCasilla.FINAL, 4);

        mapa = new Mapa(5, 1, matriz);
        partida = new Partida(jugadores, 6, mapa);
    }

    @Test
    @DisplayName("RF-13: Alcanzar la casilla final declara la condición de victoria")
    void testCondicionDeVictoria() {
        assertFalse(partida.getJuegoTerminado(), "El juego no debe comenzar terminado");

        Jugador actual = partida.getJugadorActual();
        partida.moverPorDado(actual, 4);

        assertEquals(4, actual.getPosicion(), "El jugador debe estar en la casilla meta (FINAL)");

        AccionCasillaVictoria accionVictoria = new AccionCasillaVictoria();
        accionVictoria.ejecutar(partida, actual);

        assertTrue(partida.getJuegoTerminado(), "El juego debe estar terminado al alcanzar la casilla meta");
    }

    @Test
    @DisplayName("CR-01: reiniciarPartida en ControladorPrincipal limpia el estado previo de la partida")
    void testControladorPrincipalReiniciarPartida() {
        ControladorPrincipal cp = new ControladorPrincipal();
        cp.configurarPartida(jugadores, 6);

        assertEquals(jugadores, cp.getJugadoresPartida(), "Los jugadores deben estar configurados");
        assertEquals(6, cp.getCarasDadoPartida(), "Las caras del dado deben estar configuradas");

        cp.reiniciarPartida();

        assertNull(cp.getPartida(), "La partida debe haber sido limpiada");
        assertNull(cp.getJugadoresPartida(), "La lista de jugadores debe haber sido limpiada");
        assertEquals(0, cp.getCarasDadoPartida(), "Las caras del dado deben reiniciar a 0");
    }

    @Test
    @DisplayName("CR-01: ControladorVentanaDePartida propaga la referencia de ControladorPrincipal")
    void testPropagacionControladorPrincipal() {
        ControladorVentanaDePartida cvp = new ControladorVentanaDePartida();
        ControladorPrincipal cp = new ControladorPrincipal();

        cvp.setControladorPrincipal(cp);
        assertEquals(cp, cvp.getControladorPrincipal(), "El controlador principal debe asignarse en ControladorVentanaDePartida");
    }

    @Test
    @DisplayName("CR-01: ControladorMazosYTurnos invoca reiniciarPartida en ControladorPrincipal")
    void testControladorMazosYTurnosReiniciarPartida() {
        ControladorPrincipal cp = new ControladorPrincipal();
        cp.configurarPartida(jugadores, 6);

        ControladorMazosYTurnos cmt = new ControladorMazosYTurnos(
                null, null, null, null, null,
                null, null, null, null, null, null
        );

        cmt.setControladorPrincipal(cp);
        assertEquals(cp, cmt.getControladorPrincipal());

        cmt.reiniciarPartida();

        assertNull(cp.getPartida(), "Al reiniciar desde ControladorMazosYTurnos, la partida debe limpiarse");
        assertNull(cp.getJugadoresPartida(), "Los jugadores deben limpiarse");
        assertEquals(0, cp.getCarasDadoPartida(), "El dado debe resetearse a 0");
    }

    @Test
    @DisplayName("CR-01: Componentes de UI de Victoria y reinicio en ControladorVentanaDePartida")
    void testComponentesVictoriaYReinicioUI() {
        ControladorPrincipal cp = new ControladorPrincipal();
        cp.configurarPartida(jugadores, 6);

        ControladorVentanaDePartida cvp = new ControladorVentanaDePartida();
        cvp.setControladorPrincipal(cp);

        Button btnReiniciar = new Button("Reiniciar Partida");
        StackPane overlayVictoria = new StackPane();
        Label lblVictoriaMensaje = new Label();
        Button btnReiniciarOverlay = new Button("Reiniciar Partida");

        cvp.setBtnReiniciar(btnReiniciar);
        cvp.setOverlayVictoria(overlayVictoria);
        cvp.setLblVictoriaMensaje(lblVictoriaMensaje);
        cvp.setBtnReiniciarOverlay(btnReiniciarOverlay);

        assertEquals(btnReiniciar, cvp.getBtnReiniciar());
        assertEquals(overlayVictoria, cvp.getOverlayVictoria());
        assertEquals(lblVictoriaMensaje, cvp.getLblVictoriaMensaje());
        assertEquals(btnReiniciarOverlay, cvp.getBtnReiniciarOverlay());

        cvp.reiniciarPartida();

        assertNull(cp.getPartida(), "Al reiniciar desde ControladorVentanaDePartida, la partida debe limpiarse");
        assertNull(cp.getJugadoresPartida(), "Los jugadores deben limpiarse");
        assertEquals(0, cp.getCarasDadoPartida(), "Las caras del dado deben resetearse");
    }
}
