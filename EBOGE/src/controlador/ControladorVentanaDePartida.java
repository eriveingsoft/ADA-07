package controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import modelo.Partida;
import modelo.cartas.TipoCarta;
import modelo.MotorDeTurnos;

public class ControladorVentanaDePartida implements Initializable {

	@FXML
	private AnchorPane panelFondo;
	@FXML
	private GridPane gridTablero;
	@FXML
	private HBox barraInferior;
	@FXML
	private Button btnAbrirMazoPropio;
	@FXML
	private Button btnAbrirMazoObjetivo;
	@FXML
	private Button btnAbrirMazoGlobal;
	@FXML
	private Button btnAbrirMazoMaestro;
	@FXML
	private Button btnLanzarDado;
	@FXML
	private Label lblTurnoTexto;
	@FXML
	private Label lblNombreJugador;
	@FXML
	private Label lblResultadoDado;
	
	@FXML
    private Label lblMensajeEstado;

	@FXML
	private StackPane overlayCarta;
	@FXML
	private Button btnCartaGrande;

	private static final double BORDE_LATERAL = 90.0;
	private static final double MARGEN_SUPERIOR = 70.0;
	private static final double FACTOR_ALTURA_TABLERO = 0.60;

	private MotorDeTurnos motor;

	private ControladorTablero controladorTablero;
	private ControladorMazosYTurnos controladorMazosYTurnos;
	private ControladorPrincipal controladorPrincipal;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		gridTablero.setLayoutX(BORDE_LATERAL);
		gridTablero.setLayoutY(MARGEN_SUPERIOR);

		gridTablero.prefWidthProperty().bind(panelFondo.widthProperty().subtract(BORDE_LATERAL * 2));
		gridTablero.prefHeightProperty().bind(panelFondo.heightProperty().multiply(FACTOR_ALTURA_TABLERO));

		controladorTablero = new ControladorTablero(gridTablero);

		controladorMazosYTurnos = new ControladorMazosYTurnos(btnAbrirMazoPropio, btnAbrirMazoObjetivo,
				btnAbrirMazoGlobal, btnAbrirMazoMaestro, btnLanzarDado, lblTurnoTexto, lblNombreJugador,
				lblResultadoDado, overlayCarta, btnCartaGrande, lblMensajeEstado);

		btnLanzarDado.setOnAction(e -> controladorMazosYTurnos.lanzarDado(motor));
		btnAbrirMazoPropio.setOnAction(e -> controladorMazosYTurnos.abrirMazo(TipoCarta.PROPIA, motor));
		btnAbrirMazoObjetivo.setOnAction(e -> controladorMazosYTurnos.abrirMazo(TipoCarta.BLANCO, motor));
		btnAbrirMazoGlobal.setOnAction(e -> controladorMazosYTurnos.abrirMazo(TipoCarta.GLOBAL, motor));
		btnAbrirMazoMaestro.setOnAction(e -> controladorMazosYTurnos.abrirMazo(TipoCarta.MAESTRA, motor));

		
		btnCartaGrande.setOnAction(e -> controladorMazosYTurnos.usarCartaPendiente(motor));

		controladorMazosYTurnos.estadoInicial();
		if (controladorPrincipal != null) {
			controladorMazosYTurnos.setControladorPrincipal(controladorPrincipal);
		}
	}

	/**
	 * Asigna la referencia al controlador principal de la aplicación.
	 * @param controladorPrincipal Instancia de ControladorPrincipal.
	 */
	public void setControladorPrincipal(ControladorPrincipal controladorPrincipal) {
		this.controladorPrincipal = controladorPrincipal;
		if (controladorMazosYTurnos != null) {
			controladorMazosYTurnos.setControladorPrincipal(controladorPrincipal);
		}
	}

	/**
	 * Obtiene el controlador principal asignado a esta vista.
	 * @return Instancia de ControladorPrincipal o null si no se ha asignado.
	 */
	public ControladorPrincipal getControladorPrincipal() {
		return controladorPrincipal;
	}

	/**
	 * Obtiene el controlador de mazos y turnos.
	 * @return Instancia de ControladorMazosYTurnos.
	 */
	public ControladorMazosYTurnos getControladorMazosYTurnos() {
		return controladorMazosYTurnos;
	}

	public void inicializarConMotor(MotorDeTurnos motor) {
		this.motor = motor;
		Partida partida = motor.getPartida();

		partida.getGestorMovimientos().suscribir(controladorTablero);
		partida.getMapa().getGestorDeCambios().suscribir(controladorTablero);

		controladorTablero.dibujarTablero(partida.getMapa(), partida.getJugadores());
		controladorMazosYTurnos.actualizarLabelTurno(partida.getJugadorActual());
	}

	public double getAnchoTablero() {
		return gridTablero.getWidth();
	}

	public double getAltoTablero() {
		return gridTablero.getHeight();
	}
}
