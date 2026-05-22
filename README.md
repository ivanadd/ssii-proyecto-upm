# Proyecto de Sistemas Inteligentes (2025-2026)
---
Repositorio colaborativo para la práctica de "Multiagentes" de la asignatura "Sistemas Inteligentes", perteneciente al Plan 09 del Grado en Ingeniería Informática por la ETSIINF-UPM.

## Resumen y objetivos
Este sistema se encarga de automatizar, mediante un sistema multiagente, las compras realizadas mediante _Click and Collect_ y derivados.

Este sistema tratará de modelizar un sistema de automatización para un entorno de tipo _Click and Collect_, en el que el usuario, con su coche, se acerca al _párking_ del establecimiento y le cargan la compra en su coche con un pedido previamente asociado a su matrícula. 
La diferencia de nuestra práctica con lo que ya existe es que, con ella, se pretende una mejor en la eficiencia de estos métodos de compra. La idea es separar una zona del _párking_ ya existente y destinarla únicamente a _Click and Collect_. Para acceder a este subconjunto de plazas, será necesario pasar por una barrera que detecte, mediante una cámara, la matrícula del coche que está esperando frente a ella. Este sistema detectará si el usuario tiene un pedido pendiente para ese día. Si lo tiene, le dejará pasar y le asignará una plaza, mostrando toda esta información mediante una pantalla colocada al lado de la barrera. El sistema también tendrá guardado qué plazas están ocupadas dentro del subconjunto de plazas destinadas a _Click and Collect_. Al asignar una plaza, damos por supuesto que una plaza está ocupada, y al liberar el pedido, damos por supuesto que esa plaza está liberada. En la propia plaza del _párking_ se situará una pantalla en la que se mostrará un tiempo restante aproximado para la entrega del pedido (TEMPORA, ESTÁ POR VER).
Pensamos meter los usuarios con sus datos en una base de datos y conectarlo con el proyecto, pero por simplicidad para nuestra práctica y siguiendo las indicaciones del profesor, hemos decidido almacenar los usuarios en estructuras de datos de Java. (TEMPORAL, ESTÁ POR VER QUÉ ESTRUCTURA CONCRETA!) 

## Instrucciones de instalación

= A COMPLETAR =

## Dependencias necesarias para la instalación

Para el agente de percepción, se ha hecho uso de las siguientes librerías y ficheros adicionales:
- `opencv-4120.jar`: Encargado de la apertura de la cámara y gestión de la misma. Puede descargarse desde su [página web](https://opencv.org/releases/) oficial.
- `tess4j-5.18.0.jar`: Encargado de la realización del OCR. Puede descargarse la última versión desde su [repositorio](https://github.com/nguyenq/tess4j/) de GitHub, en [_releases_](https://github.com/nguyenq/tess4j/releases).

## Instrucciones de ejecución

= A COMPLETAR =

## Datos de ejemplo para la ejecución de la práctica

= A COMPLETAR =

## Diagrama de la arquitectura del sistema

= A COMPLETAR =

## Declaración de uso de Inteligencia Artificial

= A COMPLETAR =


---
### Miembros del grupo
- Arias de Dios, Iván
- Vergara Martínez, Víctor Manuel
- Garrote Martín, Víctor
- Juaranz Domínguez, André
- Lahoz Vives, Hugo

(Universidad Politécnica de Madrid - Escuela Técnica Superior de Ingenieros Informáticos - 25/26)
