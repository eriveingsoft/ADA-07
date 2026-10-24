package controlador;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;
import modelo.MotorDeTurnos;
import modelo.Partida;
import modelo.cartas.Carta;
import modelo.cartas.TipoCarta;
import modelo.jugador.Jugador;
import javafx.scene.control.ChoiceDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import modelo.cartas.EfectoConObjetivo;
import modelo.cartas.EfectoSinObjetivo;

public class ControladorMazosYTurnos {
	
	private static final Logger LOGGER = Logger.getLogger(ControladorMazosYTurnos.class.getName());
	private ControladorPrincipal controladorPrincipal;

	private final Button btnAbrirMazoPropio;
	private final Button btnAbrirMazoObjetivo;
	private final Button btnAbrirMazoGlobal;
	private final Button btnAbrirMazoMaestro;
	private final Button btnLanzarDado;

	private final Label lblTurnoTexto;
	private final Label lblNombreJugador;
	private final Label lblResultadoDado;

	private final StackPane overlayCarta;
	private final Button btnCartaGrande;
	
	private final Label lblMensajeEstado;

	public ControladorMazosYTurnos(Button btnPropio, Button btnObjetivo, Button btnGlobal, Button btnMaestro,
			Button btnLanzarDado, Label lblTurnoTexto, Label lblNombreJugador, Label lblResultadoDado,
			StackPane overlayCarta, Button btnCartaGrande, Label lblMensajeEstado) {

		this.btnAbrirMazoPropio = btnPropio;
		this.btnAbrirMazoObjetivo = btnObjetivo;
		this.btnAbrirMazoGlobal = btnGlobal;
		this.btnAbrirMazoMaestro = btnMaestro;
		this.btnLanzarDado = btnLanzarDado;

		this.lblTurnoTexto = lblTurnoTexto;
		this.lblNombreJugador = lblNombreJugador;
		this.lblResultadoDado = lblResultadoDado;

		this.overlayCarta = overlayCarta;
		this.btnCartaGrande = btnCartaGrande;
		
		this.lblMensajeEstado = lblMensajeEstado;

		if (overlayCarta != null) {
			overlayCarta.setVisible(false);
			overlayCarta.setManaged(false);
		}

		deshabilitarTodosLosMazos();
	}

	public void estadoInicial() {
		if (lblTurnoTexto != null)
			lblTurnoTexto.setText("Turno de:");
		if (lblResultadoDado != null)
			lblResultadoDado.setText("-");
	}
	
	private void notificarEvento(String mensaje) {
        if (lblMensajeEstado != null) {
            lblMensajeEstado.setText(mensaje);
        }
        System.out.println(mensaje);
    }

	public void actualizarLabelTurno(Jugador actual) {		
		lblTurnoTexto.setText("Turno de:");
		lblNombreJugador.setText(actual.getNombre());
		
		// Aplicamos el color del jugador al texto para mejor feedback visual
		String hex = actual.getColor();
		lblNombreJugador.setStyle("-fx-text-fill: " + hex + ";" + "-fx-font-size: 20px;" + "-fx-font-weight: bold;");
	}
	

	public void lanzarDado(MotorDeTurnos motor) {
		Partida partida = motor.getPartida();
		if (partida == null || partida.getJuegoTerminado()) return;

		btnLanzarDado.setDisable(true); // Evitamos doble clic accidental

		Jugador jugador = motor.iniciarTurno();
		int pasos = motor.lanzarDado();
		
		mostrarResultadosDado(jugador, pasos);
		
		motor.calcularNuevaPosicion(pasos);
		motor.ejecutarAccionEnCasilla();
		
		if (partida.getJuegoTerminado()) {
	        manejarVictoria(jugador);
	        return; 
	    }

	    verificarEfectosTrampa(jugador);	
	    gestionarFlujoDeCartas(motor, partida);
	}
	
	public void mostrarResultadosDado(Jugador jugador, int pasos) {
		if (lblResultadoDado != null) {
			lblResultadoDado.setText("Resultado: " + pasos);
		}
		notificarEvento(jugador.getNombre() + " avanzó " + pasos + " casillas.");
	}
	
	public void setControladorPrincipal(ControladorPrincipal controladorPrincipal) {
		this.controladorPrincipal = controladorPrincipal;
	}

	public ControladorPrincipal getControladorPrincipal() {
		return this.controladorPrincipal;
	}

	public void reiniciarPartida() {
		if (controladorPrincipal != null) {
			controladorPrincipal.reiniciarPartida();
		}
	}

	private void manejarVictoria(Jugador jugador) {
	    notificarEvento("¡VICTORIA! " + jugador.getNombre() + " HA GANADO LA PARTIDA ");
	    if (btnLanzarDado != null) {
	        btnLanzarDado.setText("Reiniciar");
	        btnLanzarDado.setDisable(false);
	        btnLanzarDado.setOnAction(e -> reiniciarPartida());
	        btnLanzarDado.getStyleClass().add("btn-reiniciar");
	    }
	    try {
	        Platform.runLater(() -> mostrarDialogoVictoria(jugador));
	    } catch (IllegalStateException e) {
	        LOGGER.log(Level.WARNING, "Toolkit de JavaFX no inicializado: " + e.getMessage());
	    }
	}

	protected void mostrarDialogoVictoria(Jugador jugador) {
	    Alert alerta = new Alert(Alert.AlertType.INFORMATION);
	    alerta.initStyle(StageStyle.UNDECORATED);
	    alerta.setTitle("EBOGE - ¡Victoria!");
	    alerta.setHeaderText("¡VICTORIA! ¡" + jugador.getNombre() + " HA GANADO LA PARTIDA!");
	    alerta.setContentText("¡Felicidades! Has alcanzado la meta.\n¿Deseas reiniciar la partida para volver a la configuración o salir?");
	    alerta.setGraphic(null);

	    if (btnLanzarDado != null && btnLanzarDado.getScene() != null && btnLanzarDado.getScene().getWindow() != null) {
	        alerta.initOwner(btnLanzarDado.getScene().getWindow());
	    }

	    ButtonType btnReiniciar = new ButtonType("Reiniciar", ButtonBar.ButtonData.OK_DONE);
	    ButtonType btnSalir = new ButtonType("Salir", ButtonBar.ButtonData.CANCEL_CLOSE);
	    alerta.getButtonTypes().setAll(btnReiniciar, btnSalir);

	    try {
	        if (getClass().getResource("/vista/Partida.css") != null) {
	            alerta.getDialogPane().getStylesheets().add(
	                getClass().getResource("/vista/Partida.css").toExternalForm()
	            );
	            alerta.getDialogPane().getStyleClass().add("dialogo-gamer");
	        }
	    } catch (Exception e) {
	        LOGGER.log(Level.WARNING, "No se pudo cargar el estilo del diálogo: " + e.getMessage());
	    }

	    Button botonReiniciar = (Button) alerta.getDialogPane().lookupButton(btnReiniciar);
	    if (botonReiniciar != null) {
	        botonReiniciar.setId("btnReiniciar");
	        botonReiniciar.getStyleClass().addAll("btn-reiniciar", "btn-restart");
	        botonReiniciar.setAccessibleText("Restart");
	    }

	    Button botonSalir = (Button) alerta.getDialogPane().lookupButton(btnSalir);
	    if (botonSalir != null) {
	        botonSalir.setId("btnSalir");
	    }

	    Optional<ButtonType> respuesta = alerta.showAndWait();
	    if (respuesta.isPresent() && respuesta.get() == btnReiniciar) {
	        reiniciarPartida();
	    } else if (respuesta.isPresent() && respuesta.get() == btnSalir) {
	        Platform.exit();
	        System.exit(0);
	    }
	}

	private void verificarEfectosTrampa(Jugador jugador) {
	    if (jugador.estaInmovilizado()) {
	        notificarEvento(jugador.getNombre() + " cayó en una trampa por " + jugador.getTurnosEfecto() + " turnos.");
	    }
	}

	private void gestionarFlujoDeCartas(MotorDeTurnos motor, Partida partida) {
	    TipoCarta tipoMazo = motor.getMazoHabilitado();
	    actualizarBotonesMazos(tipoMazo);

	    if (tipoMazo != null) {
	    	// Esperamos interacción del usuario para robar carta
	        notificarEvento("Puedes agarrar una carta de tipo " + tipoMazo);
	    } else if (!motor.hayCartaPendiente()) {
	    	// Si no hay carta que robar ni usar, el turno acaba automáticamente
	        terminarTurnoUI(motor, partida);
	    }
	}

	private void terminarTurnoUI(MotorDeTurnos motor, Partida partida) {
		ocultarCartaEnPantalla();
	    motor.terminarTurno(); 
	    actualizarLabelTurno(partida.getJugadorActual());
	    btnLanzarDado.setDisable(false);
	}

	public void abrirMazo(TipoCarta tipo, MotorDeTurnos motor) {
		if (motor == null) return;

		Carta carta = motor.robarCartaDeMazo(tipo);
		if (carta == null) {
			notificarEvento("No puedes robar una carta de tipo " + tipo + "en este momento");
			return;
		}
		
		notificarEvento("Has robado una carta de tipo  " + carta.getNombre());
		mostrarCartaEnPantalla(carta);
		deshabilitarTodosLosMazos();
	}

	private void deshabilitarTodosLosMazos() {
		if (btnAbrirMazoPropio != null) btnAbrirMazoPropio.setDisable(true);
		if (btnAbrirMazoObjetivo != null) btnAbrirMazoObjetivo.setDisable(true);
		if (btnAbrirMazoGlobal != null) btnAbrirMazoGlobal.setDisable(true);
		if (btnAbrirMazoMaestro != null) btnAbrirMazoMaestro.setDisable(true);
	}

	private void actualizarBotonesMazos(TipoCarta tipoMazo) {
		deshabilitarTodosLosMazos();
		if (tipoMazo == null) return;

		switch (tipoMazo) {
		case PROPIA -> { if (btnAbrirMazoPropio != null) btnAbrirMazoPropio.setDisable(false); }
		case BLANCO -> { if (btnAbrirMazoObjetivo != null) btnAbrirMazoObjetivo.setDisable(false); }
		case GLOBAL -> { if (btnAbrirMazoGlobal != null) btnAbrirMazoGlobal.setDisable(false); }
		case MAESTRA -> { if (btnAbrirMazoMaestro != null) btnAbrirMazoMaestro.setDisable(false); }
		}
	}

	private void mostrarCartaEnPantalla(Carta carta) {
		if (overlayCarta == null || btnCartaGrande == null) return;

		overlayCarta.setVisible(true);
		overlayCarta.setManaged(true);

		String ruta = carta.getImagenRuta();
		if (ruta != null && !ruta.isBlank()) {
			String style = "-fx-background-image: url('" + ruta + "');" + "-fx-background-size: contain;"
					+ "-fx-background-repeat: no-repeat;" + "-fx-background-position: center;";
			btnCartaGrande.setStyle(style);
		}
		btnLanzarDado.setDisable(true); // Bloqueo mientras lee la carta
	}

	private void ocultarCartaEnPantalla() {
		if (overlayCarta != null) {
			overlayCarta.setVisible(false);
			overlayCarta.setManaged(false);
		}
	}
	
	
	public void usarCartaPendiente(MotorDeTurnos motor) {
	    if (motor == null) return;
	    Partida partida = motor.getPartida();

	    if (!motor.hayCartaPendiente()) {
	        ocultarCartaEnPantalla();
	        btnLanzarDado.setDisable(false);
	        return;
	    }

	    Carta carta = motor.getCartaPendiente();

	    // CASO 1: Efecto directo
	    if (carta instanceof EfectoSinObjetivo) {
	        motor.usarCartaPendienteSinObjetivo();
	        terminarTurnoUI(motor, partida);
	        return;
	    }

	    // CASO 2: Diálogo de selección
	    if (carta instanceof EfectoConObjetivo) {
	        List<Jugador> objetivos = new ArrayList<>(partida.getJugadores());
	        objetivos.remove(partida.getJugadorActual());

	        if (objetivos.isEmpty()) { btnLanzarDado.setDisable(false); return; }

	        List<String> nombres = new ArrayList<>();
	        for (Jugador jugador : objetivos) nombres.add(jugador.getNombre());
	        
	        //Mostrar la ventana de selección
	        ChoiceDialog<String> dialog = new ChoiceDialog<>(nombres.get(0), nombres);
	        dialog.initStyle(StageStyle.UNDECORATED);
	        dialog.setHeaderText("Usando: " + carta.getNombre());
	        dialog.setContentText("Elige víctima:");
	        dialog.setGraphic(null);

	        //Cargamos el CSS de Partida
	        dialog.getDialogPane().getStylesheets().add(
	            getClass().getResource("/vista/Partida.css").toExternalForm()
	        );

	        dialog.getDialogPane().getStyleClass().add("dialogo-gamer");


	        Optional<String> result = dialog.showAndWait();
	        if (result.isPresent()) {
	            for (Jugador jugador : objetivos) {
	                if (jugador.getNombre().equals(result.get())) {
	                    motor.usarCartaPendienteConObjetivo(jugador);
	                    terminarTurnoUI(motor, partida);
	                    return;
	                }
	            }
	        }
	    }	   	    
	}
}
