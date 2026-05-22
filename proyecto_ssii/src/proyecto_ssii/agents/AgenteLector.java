package proyecto_ssii.agents;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.*;

@SuppressWarnings({ "unused", "serial" })
public class AgenteLector extends Agent {
	protected TickerBehaviour tickerBehaviour; 
	private VideoCapture camara;
	
	@Override
	protected void setup() {
		System.out.println("[DEBUG LECTOR] AgenteLector iniciado.");
		
		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		} catch (UnsatisfiedLinkError error) {
			System.out.println("[ERROR LECTOR] No se pudo cargar la librería nativa de OpenCV: " + error.getMessage());
			doDelete();
			return;
		}

		// inicialización camara
		camara = new VideoCapture(0);
		if(!camara.isOpened()) {
			System.out.println("[ERROR LECTOR] Cámara no inicializada correctamente.");
			doDelete();
			return;
		}
		
		tickerBehaviour = new TickerBehaviour(this, 100) {
				// 1. Leer frame webcam
				// 2. Detectar matricula
				// 3. OCR
				// 4. Dibujar overlay	
				// 5. Mostrar img

			@Override
			protected void onTick() {
				Mat frame = new Mat();
				if(camara.read(frame)) {
					HighGui.imshow("[AGENTE LECTOR] Cámara en directo", frame);
					HighGui.waitKey(1);
				}
			}
		};
		addBehaviour(tickerBehaviour);
	}
	
	@Override
	protected void takeDown() {
		if(camara != null) camara.release();
		HighGui.destroyAllWindows();
		System.out.println("[DEBUG LECTOR] Agente lector finalizado.");
	}
}
