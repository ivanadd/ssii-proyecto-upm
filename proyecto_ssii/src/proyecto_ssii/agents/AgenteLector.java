package proyecto_ssii.agents;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
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
					Mat grises = new Mat();
					Imgproc.cvtColor(frame,grises,Imgproc.COLOR_BGR2GRAY); // webcam normal -> escala de grises
					
					Mat blur = new Mat();
					Imgproc.GaussianBlur(grises,blur,new Size(5,5), 0); // blur para reducir ruido
					
					Mat bordes = new Mat();
					Imgproc.Canny(blur,bordes,100,200); // algoritmo de deteccion de bordes
					
					List<MatOfPoint> contornos = new ArrayList<>();
					Mat hier = new Mat();
					Imgproc.findContours(
						bordes,
						contornos,
						hier,
						Imgproc.RETR_TREE,
						Imgproc.CHAIN_APPROX_SIMPLE
					);
					
					Rect rectangulo; int anchura, altura; double aspectRatio;
					for(MatOfPoint contorno : contornos) {
						rectangulo = Imgproc.boundingRect(contorno);
						anchura = rectangulo.width;
						altura = rectangulo.height;
						if(altura == 0) continue;
						aspectRatio = (double)(anchura/altura);
						
						if(aspectRatio > 2 && aspectRatio < 6) {
							if(anchura > 120 && altura > 30) {
								// pinta rectangulo porque ha encontrado matricula
								Point p1 = new Point(rectangulo.x, rectangulo.y);
								Point p2 = new Point(rectangulo.x + rectangulo.width,
										rectangulo.y + rectangulo.height);
								Scalar scalar = new Scalar(0,255,0); // rectangulo verde
								Imgproc.rectangle(frame,p1,p2,scalar);
								
								// texto sobre rectangulo
								Imgproc.putText(frame,"Matricula",new Point(rectangulo.x, rectangulo.y-10),
										Imgproc.FONT_HERSHEY_SIMPLEX,0.7, new Scalar(0,255,0),2);
							}
						}
						
					}
					
					// mostrar camara
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
