# Especificación de Requisitos de Software (ERS)
**Proyecto:** EBOGE  
**Basado en:** IEEE Std 830-1998  

---

## 1. Introducción

### 1.1 Propósito  
El presente documento tiene como finalidad establecer de forma estructurada, verificable y trazable los **requisitos funcionales** del sistema **EBOGE**, un juego de mesa digital desarrollado en **Java**.  
Este documento servirá como **base contractual y técnica** para el diseño, desarrollo, pruebas y mantenimiento del sistema, de acuerdo con las recomendaciones del estándar **IEEE Std 830-1998**.

---

### 1.2 Alcance del sistema  
**EBOGE** es un juego de mesa digital multijugador local en el que los participantes compiten por alcanzar la meta antes que sus oponentes.  
Los jugadores lanza un **dado configurable (de 3 a 20 caras)** para avanzar por el tablero. 
El tablero tiene un tamaño definido segun el numero de caras del dado y las casillas se general de forma aleatoria, permitiendo variaciones dinámicas en cada partida.   
Según el tipo de casilla en que caiga, el sistema activa una carta de uno de los **cuatro mazos** disponibles:  
- **Propia**  
- **Target**  
- **Global**  
- **Maestra**

---

### 1.3 Definiciones, acrónimos y abreviaturas  

| Término | Definición |
|----------|-------------|
| **Casilla normal** | Casilla sin efecto especial; no dispara cartas ni aplica estados. |
| **Casilla de restricción** | Casilla que aplica una inmovilización temporal al jugador durante *n* turnos, con 1 ≤ n ≤ 3. |
| **Casilla propia** | Casilla que dispara una carta del mazo **Propia**, afectando únicamente al jugador activo. |
| **Casilla target** | Casilla que dispara una carta del mazo **Target**, aplicando su efecto a un jugador objetivo. |
| **Casilla global** | Casilla que dispara una carta del mazo **Global**, aplicando su efecto a todos los jugadores. |
| **Casilla maestra** | Casilla que dispara una carta del mazo **Maestra**; el jugador decide si activarla. Puede modificar el tablero. |
| **Casilla meta** | Casilla final del recorrido; al alcanzarla o sobrepasarla, el jugador gana la partida. |
| **Mapa procedural** | Tablero generado mediante algoritmo pseudoaleatorio con distribución variable de casillas. |
| **Estado de inmovilización** | Condición temporal que impide mover por *n* turnos. |
| **Turno** | Ciclo de juego: lanzamiento de dado → movimiento → resolución de efectos. |
| **Dado N** | Dado configurable de *N* caras (3 ≤ N ≤ 20). |


---

### 1.4 Referencias  
- **IEEE Std 830-1998.** Recommended Practice for Software Requirements Specifications.

---

### 1.5 Visión general del documento  (Resumen)
El documento se estructura en cuatro secciones principales:

1. Introducción y definiciones.  
2. Descripción general del sistema.  
3. Requisitos funcionales y no funcionales.  
---

## 2. Descripción general

### 2.1 Perspectiva del producto  
EBOGE es un **software de escritorio autónomo** , clasificado como una aplicación independiente. No depende de red, servidores externos ni de otros sistemas de software. Es el producto principal sin otros productos relacionados.Su núcleo se basa en: 
- Un motor de turnos
- un sistema de generación aleatoria de casillas 
- y un sistema modular de cartas.

---

### 2.2 Funciones del producto  
- Configuración inicial de partida (jugadores, dado, generación del mapa).  
- Generación aleatoria  de casillas normales, de restricción y especiales.  
- Gestión automática del orden de turnos.  
- Lanzamiento de dado y movimiento del jugador.  
- Activación y resolución de efectos de cartas (*propia, target, global, maestra*).  
- Registro y notificación de eventos en pantalla.  
- Detección de condición de victoria.

---

### 2.3 Características de los usuarios  
|Características  |Detalles|
|------|--------------|
|Rol  | Jugador (con rol activo en turno) y Observador (en turnos ajenos) |
|Conocimiento técnico  | Jugador casual. No se requiere conocimiento técnico.| 
|Experiencia de juego  | Familiarizado con los juegos de mesa tradicionales  |


---

### 2.4 Restricciones  

| Tipo | Descripción |
|------|--------------|
| **Lenguaje** | Java (JDK 21 o superior). |
| **Modalidad** | Multijugador local. |
| **Idioma** | Español. |
| **Entrada** | Teclado y ratón. |

---

### 2.5 Suposiciones y dependencias  
- Todos los jugadores comparten el mismo dispositivo.  
- Los efectos de cartas y la generación aleatoria están predefinidos.
- Se asume que el sistema se ejecutará en una máquina de escritorio con mínimo 4 GB de RAM y una resolución de pantalla de al menos 1280x720 píxeles.

---


## 3. Requisitos específicos

### 3.1 Requisitos funcionales  

| ID | Requisito Funcional | Descripción |
|----|----------------------|--------------|
| **RF-01** | Configuración de jugadores | Permitir entre 2 y 8 jugadores con nombres únicos. |
| **RF-02** | Configuración de dado | Seleccionar número de caras entre 3 y 20. |
| **RF-03** | Generación de mapa | Crear mapa con tamaño variable según las caras del dado con distribución aleatoria de casillas. |
| **RF-04** | Orden de turnos | Determinar automáticamente el orden de los jugadores. |
| **RF-05** | Movimiento | Lanzar el dado y mover la ficha según el resultado. |
| **RF-06** | Detección de casilla | Determinar el tipo de casilla y activar carta si aplica. |
| **RF-07** | Cartas propias | Aplicar efectos al jugador activo. |
| **RF-08** | Cartas target | Solicitar objetivo y aplicar efecto al jugador seleccionado. |
| **RF-09** | Cartas globales | Aplicar efectos simultáneamente a todos los jugadores. |
| **RF-10** | Cartas maestras | Mostrar opción para decidir si usar la carta maestra. |
| **RF-11** | Modificación del tablero | Aplicar cambios estructurales si se usa la carta maestra. |
| **RF-12** | Rebarajado | Rebarajar los mazos al agotarse. |
| **RF-13** | Condición de victoria | Declarar ganador al llegar o sobrepasar la casilla meta. |
| **RF-14** | Casilla de restricción | Aplicar inmovilización por *n* turnos (1 ≤ n ≤ 3). |
| **RF-15** | Casilla normal | No aplicar efecto adicional ni activar carta alguna. |

### 3.2. Requisitos de Rendimiento 
| ID | Requisito no Funcional | Descripción |
|----|----------------------|--------------|
| **RNF-01** | Tiempo de Respuesta de Turno | Tiempo máximo de procesamiento de 1 minuto para un ciclo completo (lanzamiento, movimiento de casillas, resolución de carta)|
| **RNF-02** | Tiempo máximo de creación del mapa | Tiempo máximo de 3 minutos para crear un mapa completamente nuevo.|

### 3.3 Restricciones de Diseño
| ID | Requisito de diseño | Descripción |
|----|----------------------|--------------|
| **RD-01** | Lenguaje y entorno | El desarrollo del código del backend y lógica de negocio debe ser exclusivamente (o en su gran mayoría) de Java 21 o versiones posteriores. |
| **RD-02** | Framework de la Interfaz Gráfica | La Interfaz del Usuario (IU) debe implementarse usando JavaFX. |
| **RD-03** | Arquitectura | El diseño de software de la aplicación seguirá un patron arquitectónico MVC para la separación de la lógica de negocio con la interfaz de usuario. |
| **RD-04** | Estándares de Codificación | El proceso de la construcción de software seguirá los estandáres de Java y posibles adaptaciones de este para asegurar la consistencia y mantenibilidad del código. |

### 3.4 Requisitos no funcionales 

### 3.4.1 Fiabilidad
|ID | Requisito no funcional | Descripción |
|----|----------------------|--------------|
| **RNF-01** | Aleatoriedad | La generación de resultados del dado y distribución de las casillas deben ser impredecibles por el usuario |

### 3.4.2 Usabilidad 
|ID | Requisito no funcional | Descripción |
|----|----------------------|--------------|
| **RNF-02** | Facilidad de inicio de la partida | El número máximo de acciones requeridas para comenzar la partida al iniciar la aplicación deberá ser de menor que 5 clics |
| **RNF-03** | Claridad de Estado | Los estados activos de los jugadores (ej. inmovilizado) deben ser visualmente obvios.|

### 3.4.3 Seguridad
|ID | Requisito no funcional | Descripción |
|----|----------------------|--------------|
| **RNF-04** | Manejo de errores | La aplicación debe gestionar errores de forma controlada mostrando un mensaje de error al usuario con la causa del error.|

### 3.4.4 Mantenibilidad
|ID | Requisito no funcional | Descripción |
|----|----------------------|--------------|
|**RNF-05** | Cobertura de Pruebas | El código principal (motor de turnos, lógica de cartas y generación de mapa) debe tener una cobertura de pruebas unitarias mínima del 80%.|
|**RNF-06** | Estructura modular | La lógica de los efectos de las cartas y la generación de casillas debe ser completamente modular, permitiendo la adición/modificación de nuevos efectos sin alterar el código del motor de turnos (alta cohesión, bajo acoplamiento).|
|**RNF-07** | Documentación de clases | Todas las clases y métodos públicos deben incluir comentarios Javadoc que describan su propósito, parámetros de entrada y valores de retorno.|
|**RNF-08** | Estándares de codificación | Todo el código desarrollado debe adherirse a un estándar de codificación Java y posibles adaptaciones de este para garantizar la uniformidad.|
|**RNF-09** | Abstracción de la UI | El diseño de la interfaz de usuario (IU) debe estar separado de la lógica de negocio (MVC) para permitir modificar la apariencia sin afectar el comportamiento del juego.|
|**RNF-10** | Control de Versiones | Todos los artefactos del código fuente deben ser gestionados mediante el sistema de control de versiones Git, utilizando un flujo de trabajo definido (Plan de Trabajo). |










