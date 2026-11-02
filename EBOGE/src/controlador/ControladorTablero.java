package controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import modelo.CambioCasillaListener;
import modelo.MovimientoListener;
import modelo.jugador.Jugador;
import modelo.mapa.Casilla;
import modelo.mapa.Mapa;
import modelo.mapa.TipoCasilla;

public class ControladorTablero implements MovimientoListener, CambioCasillaListener {

    private final GridPane gridTablero;
    private final Map<Jugador, Pane> fichasJugadores = new HashMap<>();

    private int filas;
    private int columnas;
    private enum DireccionCamino {
        ABAJO, DERECHA, ARRIBA, IZQUIERDA
    }


    public ControladorTablero(GridPane gridTablero) {
        this.gridTablero = gridTablero;
    }

    public void dibujarTablero(Mapa mapa, List<Jugador> jugadores) {
        this.filas = mapa.getLargoMapa();
        this.columnas = mapa.getAnchoMapa();

        gridTablero.getChildren().clear();
        gridTablero.getColumnConstraints().clear();
        gridTablero.getRowConstraints().clear();

        gridTablero.setHgap(4);
        gridTablero.setVgap(4);
        gridTablero.setPadding(new Insets(6));
        gridTablero.setAlignment(Pos.CENTER);

        configurarDistribucionGrid(filas, columnas);

        Casilla[][] matrizCasillas = mapa.getCasillas();
        int totalCasillas = filas * columnas;

        // Mapear posición (fila, columna) de cada casilla para determinar la dirección del sendero
        int[][] posiciones = new int[totalCasillas][2];
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Casilla cas = matrizCasillas[f][c];
                if (cas != null && cas.getIndice() < totalCasillas) {
                    posiciones[cas.getIndice()][0] = f;
                    posiciones[cas.getIndice()][1] = c;
                }
            }
        }

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Casilla casilla = matrizCasillas[f][c];
                if (casilla == null) continue;

                int idx = casilla.getIndice();
                DireccionCamino direccion = null;
                if (idx < totalCasillas - 1) {
                    int filaActual = posiciones[idx][0];
                    int colActual = posiciones[idx][1];
                    int filaSig = posiciones[idx + 1][0];
                    int colSig = posiciones[idx + 1][1];

                    if (filaSig > filaActual) {
                        direccion = DireccionCamino.ABAJO;
                    } else if (filaSig < filaActual) {
                        direccion = DireccionCamino.ARRIBA;
                    } else if (colSig > colActual) {
                        direccion = DireccionCamino.DERECHA;
                    } else if (colSig < colActual) {
                        direccion = DireccionCamino.IZQUIERDA;
                    }
                }

                // Detectar conectividad en los 4 costados (camino continuo vs separación entre vueltas)
                boolean topConnected = esCasillaConectada(f - 1, c, idx, matrizCasillas, filas, columnas);
                boolean bottomConnected = esCasillaConectada(f + 1, c, idx, matrizCasillas, filas, columnas);
                boolean leftConnected = esCasillaConectada(f, c - 1, idx, matrizCasillas, filas, columnas);
                boolean rightConnected = esCasillaConectada(f, c + 1, idx, matrizCasillas, filas, columnas);

                StackPane celda = crearCelda(casilla, filas, columnas, direccion,
                        topConnected, bottomConnected, leftConnected, rightConnected);
                gridTablero.add(celda, c, f);
            }
        }

        colocarFichasIniciales(jugadores);
    }

    private boolean esCasillaConectada(int f, int c, int indiceActual, Casilla[][] matriz, int totalFilas, int totalCols) {
        if (f < 0 || f >= totalFilas || c < 0 || c >= totalCols) {
            return false;
        }
        Casilla vecina = matriz[f][c];
        if (vecina == null) {
            return false;
        }
        int vecinaIdx = vecina.getIndice();
        return vecinaIdx == indiceActual - 1 || vecinaIdx == indiceActual + 1;
    }

    private void configurarDistribucionGrid(int numeroFilas, int numeroColumnas) {
        for (int c = 0; c < numeroColumnas; c++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / numeroColumnas);
            gridTablero.getColumnConstraints().add(col);
        }

        for (int f = 0; f < numeroFilas; f++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / numeroFilas);
            gridTablero.getRowConstraints().add(row);
        }
    }

    private StackPane crearCelda(Casilla casilla, int totalFilas, int totalColumnas, DireccionCamino direccion,
                                boolean topConn, boolean bottomConn, boolean leftConn, boolean rightConn) {
        StackPane celda = new StackPane();
        celda.setMinSize(0, 0);
        celda.setPrefSize(0, 0);
        celda.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Margen uniforme (5px) para que todas las cuadrículas tengan exactamente el mismo tamaño y alineación perfecta
        GridPane.setMargin(celda, new Insets(5));

        celda.setStyle(
            "-fx-background-color: #1c1c28;" +
            "-fx-border-radius: 4px;" +
            "-fx-background-radius: 4px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.65), 5, 0, 0, 2);"
        );

        int indiceCasilla = casilla.getIndice();
        celda.setId(String.valueOf(indiceCasilla));

        Image imagenCasilla = new Image(
                getClass().getResourceAsStream(casilla.getTipo().getRutaImagen())
        );

        // 1. Imagen de textura de la casilla
        ImageView imagenFondoCasilla = new ImageView(imagenCasilla);
        imagenFondoCasilla.setPreserveRatio(false);
        imagenFondoCasilla.setManaged(false);
        imagenFondoCasilla.fitWidthProperty().bind(celda.widthProperty());
        imagenFondoCasilla.fitHeightProperty().bind(celda.heightProperty());
        celda.getChildren().add(imagenFondoCasilla);

        // 2. Overlay de borde dorado sobre la imagen (garantiza color dorado brillante #FFD700 sin que la imagen lo tape)
        Pane overlayBorde = new Pane();
        overlayBorde.setMouseTransparent(true);
        overlayBorde.setManaged(false);
        overlayBorde.prefWidthProperty().bind(celda.widthProperty());
        overlayBorde.prefHeightProperty().bind(celda.heightProperty());

        String topWidth = topConn ? "0px" : "4.5px";
        String bottomWidth = bottomConn ? "0px" : "4.5px";
        String leftWidth = leftConn ? "0px" : "4.5px";
        String rightWidth = rightConn ? "0px" : "4.5px";

        String topColor = topConn ? "transparent" : "#FFD700";
        String bottomColor = bottomConn ? "transparent" : "#FFD700";
        String leftColor = leftConn ? "transparent" : "#FFD700";
        String rightColor = rightConn ? "transparent" : "#FFD700";

        overlayBorde.setStyle(
            "-fx-border-width: " + topWidth + " " + rightWidth + " " + bottomWidth + " " + leftWidth + ";" +
            "-fx-border-color: " + topColor + " " + rightColor + " " + bottomColor + " " + leftColor + ";" +
            "-fx-border-radius: 3px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(255, 215, 0, 0.7), 6, 0.5, 0, 0);"
        );
        celda.getChildren().add(overlayBorde);

        Label etiquetaID = new Label(String.valueOf(indiceCasilla));
        etiquetaID.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;" +
                "-fx-effect: dropshadow(gaussian, black, 4, 0.8, 0, 0);"
        );
        StackPane.setAlignment(etiquetaID, Pos.TOP_LEFT);
        StackPane.setMargin(etiquetaID, new Insets(3));
        celda.getChildren().add(etiquetaID);

        // Indicador direccional de sendero en espiral hacia la siguiente casilla
        if (direccion != null) {
            Label indicadorDireccion = new Label();
            indicadorDireccion.setMouseTransparent(true);
            switch (direccion) {
                case ABAJO:
                    indicadorDireccion.setText("▼");
                    StackPane.setAlignment(indicadorDireccion, Pos.BOTTOM_CENTER);
                    StackPane.setMargin(indicadorDireccion, new Insets(0, 0, 2, 0));
                    break;
                case DERECHA:
                    indicadorDireccion.setText("▶");
                    StackPane.setAlignment(indicadorDireccion, Pos.CENTER_RIGHT);
                    StackPane.setMargin(indicadorDireccion, new Insets(0, 2, 0, 0));
                    break;
                case ARRIBA:
                    indicadorDireccion.setText("▲");
                    StackPane.setAlignment(indicadorDireccion, Pos.TOP_CENTER);
                    StackPane.setMargin(indicadorDireccion, new Insets(2, 0, 0, 0));
                    break;
                case IZQUIERDA:
                    indicadorDireccion.setText("◀");
                    StackPane.setAlignment(indicadorDireccion, Pos.CENTER_LEFT);
                    StackPane.setMargin(indicadorDireccion, new Insets(0, 0, 0, 2));
                    break;
            }
            indicadorDireccion.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1a24;" +
                "-fx-background-color: #FFD700;" +
                "-fx-background-radius: 6px;" +
                "-fx-padding: 0 4px 0 4px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 4, 0.8, 0, 0);"
            );
            celda.getChildren().add(indicadorDireccion);
        }

        FlowPane contenedorFichas = new FlowPane();
        contenedorFichas.setPickOnBounds(false);
        contenedorFichas.setId("fichas-" + indiceCasilla);
        contenedorFichas.setHgap(3);
        contenedorFichas.setVgap(3);
        contenedorFichas.setPrefWrapLength(40);
        StackPane.setAlignment(contenedorFichas, Pos.CENTER);
        celda.getChildren().add(contenedorFichas);

        return celda;
    }

    private void colocarFichasIniciales(List<Jugador> jugadores) {
        fichasJugadores.clear();
        int index = 0;

        for (Jugador jugador : jugadores) {
            int indiceCasilla = jugador.getPosicion();
            Pane contenedor = buscarContenedorFichas(indiceCasilla);
            if (contenedor == null) {
                System.err.println("No se encontró contenedor de fichas para casilla " +
                        indiceCasilla + " (" + jugador.getNombre() + ")");
                continue;
            }

            Pane ficha = crearFichaParaJugador(jugador, index++);
            fichasJugadores.put(jugador, ficha);
            contenedor.getChildren().add(ficha);
        }
    }

    private Pane crearFichaParaJugador(Jugador jugador, int index) {
        Pane ficha = new Pane();
        ficha.setPrefSize(16, 16);

        String hex = jugador.getColor();
        ficha.setStyle(
                "-fx-background-color: " + hex + ";" +
                "-fx-background-radius: 4;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 1;"
        );

        ficha.setId("ficha-" + jugador.getNombre());
        return ficha;
    }

    private StackPane buscarCeldaPorIndice(int indiceCasilla) {
        return (StackPane) gridTablero.lookup("#" + indiceCasilla);
    }

    private Pane buscarContenedorFichas(int indiceCasilla) {
        StackPane celda = buscarCeldaPorIndice(indiceCasilla);
        if (celda == null) return null;
        return (Pane) celda.lookup("#fichas-" + indiceCasilla);
    }

    private void moverFicha(Jugador jugador, int posAnterior, int nuevaPosicion) {
        Pane ficha = fichasJugadores.get(jugador);
        if (ficha == null) {
            ficha = crearFichaParaJugador(jugador, 0);
            fichasJugadores.put(jugador, ficha);
        }

        Pane contAnterior = buscarContenedorFichas(posAnterior);
        Pane contNuevo = buscarContenedorFichas(nuevaPosicion);

        if (contAnterior != null) {
            contAnterior.getChildren().remove(ficha);
        }
        if (contNuevo != null) {
            contNuevo.getChildren().add(ficha);
        }

        System.out.println("Moviendo " + jugador.getNombre() +
                " de " + posAnterior + " a " + nuevaPosicion);
    }





    @Override
    public void tipoCasillaCambiado(int indiceCasilla,
                                    TipoCasilla tipoAnterior,
                                    TipoCasilla nuevoTipo) {
        StackPane celda = buscarCeldaPorIndice(indiceCasilla);
        if (celda == null) return;

        if (!celda.getChildren().isEmpty()) {
            celda.getChildren().remove(0); 
        }

        Image img = new Image(getClass().getResourceAsStream(nuevoTipo.getRutaImagen()));
        ImageView fondo = new ImageView(img);
        fondo.setPreserveRatio(false);
        fondo.setManaged(false);
        fondo.fitWidthProperty().bind(celda.widthProperty());
        fondo.fitHeightProperty().bind(celda.heightProperty());

        celda.getChildren().add(0, fondo);

        System.out.println("Casilla " + indiceCasilla +
                " actualizada a tipo " + nuevoTipo);
    }

	@Override
	public void movimiento(Jugador jugador, int posAnterior, int nuevaPos) {
		 moverFicha(jugador, posAnterior, nuevaPos);
	}
}
