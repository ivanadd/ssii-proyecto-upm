# Proyecto de Sistemas Inteligentes (2025-2026)
---
Repositorio colaborativo para la práctica de "Multiagentes" de la asignatura "Sistemas Inteligentes", perteneciente al Plan 09 del Grado en Ingeniería Informática por la ETSIINF-UPM. Para acceder al directorio compartido de OneDrive para la realización de la presentación, pulse [aquí](https://upm365-my.sharepoint.com/:f:/r/personal/ivan_arias_alumnos_upm_es/Documents/PROYECTO%20SISTEMAS%20INTELIGENTES?csf=1&web=1&e=Flc34U) (previa invitación).

## Resumen y objetivos
Este sistema se encarga de automatizar, mediante un sistema multiagente, las compras realizadas mediante _Click and Collect_ y derivados.

Este sistema tratará de modelizar un sistema de automatización para un entorno de tipo _Click and Collect_, en el que el usuario, con su coche, se acerca al _párking_ del establecimiento y le cargan la compra en su coche con un pedido previamente asociado a su matrícula. 
La diferencia de nuestra práctica con lo que ya existe es que, con ella, se pretende una mejor en la eficiencia de estos métodos de compra. La idea es separar una zona del _párking_ ya existente y destinarla únicamente a _Click and Collect_. Para acceder a este subconjunto de plazas, será necesario pasar por una barrera que detecte, mediante una cámara, la matrícula del coche que está esperando frente a ella. Este sistema detectará si el usuario tiene un pedido pendiente para ese día. Si lo tiene, le dejará pasar y le asignará una plaza, mostrando toda esta información mediante una pantalla colocada al lado de la barrera. El sistema también tendrá guardado qué plazas están ocupadas dentro del subconjunto de plazas destinadas a _Click and Collect_. Al asignar una plaza, damos por supuesto que una plaza está ocupada, y al liberar el pedido, damos por supuesto que esa plaza está liberada. En la propia plaza del _párking_ se situará una pantalla en la que se mostrará un tiempo restante aproximado para la entrega del pedido.

Pensamos meter los usuarios con sus datos en una base de datos y conectarlo con el proyecto, pero por simplicidad para nuestra práctica y siguiendo las indicaciones del profesor, hemos decidido almacenar los usuarios en estructuras de datos de Java. 

## Instrucciones de instalación
Clone este repositorio en la ruta deseada y siga las instrucciones de ejecución indicadas posteriormente. Recuerde que debe tener instalado Java 8 y un JDK igual o superior a la versión 21.0.
Es necesaria la instalación del programa `tesseract-ocr-w64-setup.exe`, en la ruta `C:\Program Files\Tesseract-OCR\`. Puede encontrar el descargable `.exe` en [este](https://github.com/UB-Mannheim/tesseract/wiki) enlace.

## Dependencias necesarias para la instalación

Para el agente de percepción, se ha hecho uso de las siguientes librerías y ficheros adicionales:
- `opencv-4120.jar`: Encargado de la apertura de la cámara y gestión de la misma. Puede descargarse desde su [página web](https://opencv.org/releases/) oficial.
- `tess4j-5.18.0.jar`: Encargado de la realización del OCR. Puede descargarse la última versión desde su [repositorio](https://github.com/nguyenq/tess4j/) de GitHub, en [_releases_](https://github.com/nguyenq/tess4j/releases).
- `jna-5.x.jar`: Dependencia transitiva necesaria para `tess4j`. Si no está en el classpath, `AgenteLector` puede iniciar sin OCR, pero no realizará reconocimiento de texto.
- `commons-codec-1.3.jar` y `commons-io-2.22.0.jar`: Dependencia necesaria para `tess4j`.
- `jar-imageio-core-1.4.0.jar`: Dependencia de `tess4j`.
- `jbig2-imageio-3.0.4.jar` y `lept4j-1.23.0.jar`: Dependencias de `tess4j`.
- `tesseract-ocr-w64-setup.exe`: Encargado de OCR también. Descargable .exe para _Windows_ desde [aquí](https://github.com/UB-Mannheim/tesseract/wiki). Debe ser instalado en la ruta `C:\Program Files\Tesseract-OCR\` para su correcto funcionamiento con el sistema multiagentes. 

## Instrucciones de ejecución

Para ejecutar este proyecto, debe hacer uso del siguiente mandato en la raíz del proyecto (`/proyecto_ssii`):
```text
java -cp "bin;lib/*.jar" jade.Boot -gui AgenteLector:proyecto_ssii.agents.AgenteLector;AgenteLogica:proyecto_ssii.agents.AgenteLogica;AgenteInterfaz:proyecto_ssii.agents.AgenteInterfaz
```

O, en su defecto, y mucho más cómodo, lanzar el proyecto desde un IDE (por ejemplo, Eclipse), configurado de la siguiente manera:
- Despliegue "Run" y acceda a "Run Configurations".
- Añada un nuevo "Java Application" y renómbrelo con el identificador que prefiera.
- En el campo "Main Class", escriba `jade.Boot`.
- Acceda a la pestaña "Arguments" y escriba:
```text
-gui AgenteLector:proyecto_ssii.agents.AgenteLector;AgenteLogica:proyecto_ssii.agents.AgenteLogica;AgenteInterfaz:proyecto_ssii.agents.AgenteInterfaz
```
- Pinche en "Run" y arranque el proyecto con la configuración definida en estos pasos.

## Datos de ejemplo para la ejecución de la práctica
Tras arrancar el programa siguiendo las indicaciones previamente explicadas, verá que se abre una ventana con una cámara en directo. Muestre una matrícula impresa o real ante la cámara. El programa se encargará de buscar la forma de la matrícula y, si corresponde, realizará un OCR del texto de la matrícula. Si la matrícula está registrada en el sistema, dará por válido el acceso y le redirigirá a una plaza concreta. Si su matrícula no está añadida al sistema, rechazará la entrada del vehículo al conjunto de plazas. Puede ver una lectura correcta en [esta]() imagen. (ATENCIOn !!! AÑADIR IMAGEN) La matricula probada en el ejemplo y que se encuentra registrada en el sistema es la "1234FPM".

## Diagrama de la arquitectura del sistema

= A COMPLETAR =

## Declaración de uso de Inteligencia Artificial

Agente de percepción:
- `[22/05]`: Usado el LLM "GitHub Copilot" haciendo uso del modo "Auto", el cual ha seleccionado el modelo "GPT-5.4 mini". Se ha usado este LLM para corregir un error que obtenía por consola. Al arrancar JADE desde el agente de entrada (percepción), obtenía un error por pantalla, que indicaba que el agente había muerto sin haber sido terminado correctamente. Este LLM ha sido el encargado de añadir un _try-catch_ que maneja la excepción que provocaba la muerte del agente.

```text
***  Uncaught Exception for agent AgenteLector  ***
java.lang.UnsatisfiedLinkError: 'long org.opencv.videoio.VideoCapture.VideoCapture_5(int)'
	at org.opencv.videoio.VideoCapture.VideoCapture_5(Native Method)
	at org.opencv.videoio.VideoCapture.<init>(VideoCapture.java:182)
	at proyecto_ssii.agents.AgenteLector.setup(AgenteLector.java:21)
	at jade.core.Agent$ActiveLifeCycle.init(Agent.java:1641)
	at jade.core.Agent.run(Agent.java:1587)
	at java.base/java.lang.Thread.run(Thread.java:1570)
ERROR: Agent AgenteLector died without being properly terminated !!!
State was 2
[DEBUG LECTOR] Agente lector finalizado.
```

- `[23/05]`: Usado el LLM "ChatGPT" haciendo uso del modelo GPT-5.5. Este uso se justifica debido a que la primera implementación de la detección de contornos para la matrícula era incorrecta. Durante los testeos de la primera implementación, nos dimos cuenta de que el agente detectaba objetos normales y pequeñas secciones de la cámara como esquinas o muebles. Una vez ajustados los parámetros, en el programa recortamos manualmente la imagen para que solo detecte una matrícula y no varias sobre el mismo objeto (algo que hicimos sin LLM). El LLM solo fue usado para corregir lecturas falsas.
Aquí puedes ver la [lectura con error](https://github.com/ivanadd/ssii-proyecto-upm/blob/main/img/img_ag_lect/lect_con_error1.png), otra [lectura con error](https://github.com/ivanadd/ssii-proyecto-upm/blob/main/img/img_ag_lect/lect_con_error2.png) tras ajustar algún parámetro y la [lectura correcta](https://github.com/ivanadd/ssii-proyecto-upm/blob/main/img/img_ag_lect/lect_correcta.png) tras aplicar el recorte y modificar parámetros de nuevo. Atendiendo a las imágenes, podemos apreciar que aparece el texto fijo "Matrícula", puesto que cuando estuvimos solucionando estos errores, aún no habíamos terminado de implementar el reconocimiento OCR.

Agente de Cálculo y Lógica del programa:
- `[23/05]`: Usado el LLM "Gemini" para hacer una prueba del agente de procesamiento. Codigo que no es usado para la implementación final del agente. 

---
### Miembros del grupo
- Arias de Dios, Iván
- Vergara Martínez, Víctor Manuel
- Garrote Martín, Víctor
- Juaranz Domínguez, André
- Lahoz Vives, Hugo

Grupo 34.
(Universidad Politécnica de Madrid - Escuela Técnica Superior de Ingenieros Informáticos - 25/26)
