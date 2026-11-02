package controlador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * Controlador para la pantalla de carga.
 * Gestiona la barra de progreso y los consejos aleatorios.
 */
public class ControladorPantallaCarga implements Initializable{
	/**
	 * Inicia la carga real asíncrona generando el mapa y la partida en segundo plano.
	 * La barra de progreso avanza en sincronía con la preparación real y culmina
	 * inmediatamente cuando el mapa y el estado del juego están listos.
	 */
	public void iniciarCargaReal(Runnable tareaGeneracionMapa, Runnable accionAlTerminar) {
		this.accionAlTerminar = accionAlTerminar;

		if (timelineBarra != null) {
			timelineBarra.stop();
		}

		Task<Void> tareaCarga = new Task<>() {
			@Override
			protected Void call() throws Exception {
				updateProgress(0.15, 1.0);
				Thread.sleep(120);

				updateProgress(0.40, 1.0);
				if (tareaGeneracionMapa != null) {
					tareaGeneracionMapa.run();
				}
				updateProgress(0.85, 1.0);
				Thread.sleep(150);

				updateProgress(1.0, 1.0);
				Thread.sleep(80);
				return null;
			}
		};

		barraCarga.progressProperty().bind(tareaCarga.progressProperty());
		tareaCarga.progressProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				lblCargando.setText((int) Math.round(newVal.doubleValue() * PORCENTAJE_CONVERSOR) + "%");
			}
		});

		tareaCarga.setOnSucceeded(event -> {
			barraCarga.progressProperty().unbind();
			barraCarga.setProgress(1.0);
			lblCargando.setText("100%");
			if (timelineConsejos != null) {
			timelineConsejos.stop();
			}
			if (this.accionAlTerminar != null) {
				this.accionAlTerminar.run();
			}
		});

		tareaCarga.setOnFailed(event -> {
			barraCarga.progressProperty().unbind();
			if (timelineConsejos != null) {
				timelineConsejos.stop();
			}
			LOGGER.log(Level.SEVERE, "Error durante la carga real del mapa", tareaCarga.getException());
		});

		Thread hiloCarga = new Thread(tareaCarga, "EBOGE-CargaMapaThread");
		hiloCarga.setDaemon(true);
		hiloCarga.start();
	}

	
	 private static final String RUTA_CURIOSIDADES = "/archivos/curiosidades.txt";
     private static final double INCREMENTO_PORCENTAJE = 0.01;
     private static final double PORCENTAJE_MAXIMO = 1.0;
     private static final int PORCENTAJE_CONVERSOR = 100;
     
     private Runnable accionAlTerminar;
     
     private static final Logger LOGGER = Logger.getLogger(ControladorPantallaCarga.class.getName());
    
	 @FXML
	 private ProgressBar barraCarga;

	 @FXML
	 private Label lblCargando;

	 @FXML
	 private Label lblDatosCuriosos;
	 
	 private double progresoActual = 0.0;	 
	 private Timeline timelineBarra;	 
	 private Timeline timelineConsejos;
	    
	 
	 	 
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {		
		barraCarga.setProgress(0.0);
		lblCargando.setText("0%");
		cargarConsejos();
	}
	
	public void cargarBarraProgreso() {
		timelineBarra = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(200), event -> {
            
        progresoActual += INCREMENTO_PORCENTAJE; //0.01 
            
        barraCarga.setProgress(progresoActual);
           
        lblCargando.setText((int)(progresoActual * PORCENTAJE_CONVERSOR) + "%");
            
        if (progresoActual >= PORCENTAJE_MAXIMO) {
        	timelineBarra.stop();
            if (timelineConsejos != null) {
                timelineConsejos.stop();
            }
            
            Stage stage = null;
            if (barraCarga.getScene() != null) {
                stage = (Stage) barraCarga.getScene().getWindow();
            }

            if (accionAlTerminar != null) {
                accionAlTerminar.run(); 
            }        
        }
        });
        
        timelineBarra.getKeyFrames().add(keyFrame);        
        timelineBarra.setCycleCount(Timeline.INDEFINITE);       
        timelineBarra.play();	
	}
		
	public void cargarConsejos() {
		List<String> listaCuriosidades = leerCuriosidades();
		
		if (listaCuriosidades.isEmpty()) {
            return;
        }
		
		timelineConsejos = new Timeline();
		KeyFrame keyframe = new KeyFrame(Duration.millis(4500), event -> {			
			Random random = new Random();
		    int indiceAleatorio = random.nextInt(listaCuriosidades.size());	        
		    lblDatosCuriosos.setText(listaCuriosidades.get(indiceAleatorio));	    				
		}); 
				
		timelineConsejos.getKeyFrames().add(keyframe);		
		timelineConsejos.setCycleCount(Timeline.INDEFINITE);
		timelineConsejos.play();		
	}
	
	public List<String> leerCuriosidades(){
		List<String> listaCuriosidades = new ArrayList<>();
		
		try (InputStream archivo = getClass().getResourceAsStream(RUTA_CURIOSIDADES)) {
            
            if (archivo == null) {
                return Collections.emptyList(); 
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(archivo, StandardCharsets.UTF_8))) {
                
                String linea;
                while ((linea = reader.readLine()) != null) {
                    if (!linea.trim().isEmpty()) {
                        listaCuriosidades.add(linea);
                    }
                }
            }
        } catch (IOException e) {
        	LOGGER.log(Level.SEVERE, "Error al leer el archivo de curiosidades " + e.getMessage(), e);
            return Collections.emptyList();
        }
		
		return listaCuriosidades;
	}
	
	public void setOnCargaTerminada(Runnable accion) {
        this.accionAlTerminar = accion;
    }
	
}
