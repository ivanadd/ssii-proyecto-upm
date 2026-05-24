package proyecto_ssii.agents;

import java.util.ArrayList;	
import java.util.List;
import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.*;

@SuppressWarnings({ "unused", "serial" })
public class AgenteLector extends Agent {
	protected TickerBehaviour tickerBehaviour; 
	private VideoCapture camara;
	private Tesseract tesseract;
	private boolean ocrDisponible;
	
	private static final String PATH_TESSERACT = "C:\\Program Files\\Tesseract-OCR\\tessdata";
	private static final String LANG = "eng";
	
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
		
		// inicializacion
		try {
			this.tesseract = new Tesseract();
			this.tesseract.setDatapath(PATH_TESSERACT);
			this.tesseract.setLanguage(LANG); // se usa eng porque matriculas son alfanumericas
			this.tesseract.setVariable("tessedit_char_whitelist","0123456789BCDFGHJKLMNPRSTVWXYZ");
			this.tesseract.setPageSegMode(7); // una sola linea texto
			this.ocrDisponible = true;
		} catch (NoClassDefFoundError | UnsatisfiedLinkError error) {
			this.tesseract = null;
			this.ocrDisponible = false;
			System.out.println("[ERROR LECTOR] OCR deshabilitado: falta una dependencia nativa o de clase (JNA/Tesseract). "
					+ error.getMessage());
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
					Rect bestMatr = null; double bestArea = 0.0; double area;
					
					for(MatOfPoint contorno : contornos) {
					    MatOfPoint2f contorno2f = new MatOfPoint2f(contorno.toArray());
					    double perimetro = Imgproc.arcLength(contorno2f, true);
					    MatOfPoint2f aprox = new MatOfPoint2f();
					    Imgproc.approxPolyDP(
					            contorno2f,
					            aprox,
					            0.02 * perimetro,
					            true
					    );

					    // Solo rectángulos
					    if(aprox.total() == 4) {
					        rectangulo = Imgproc.boundingRect(contorno);
					        anchura = rectangulo.width;
					        altura = rectangulo.height;
					        if(altura == 0) continue;
					        aspectRatio = (double) anchura / altura;
					        area = anchura * altura;

					        // Filtro tipo matrícula
					        if(aspectRatio > 2.5 && aspectRatio < 6.5 && anchura > 120 && altura > 30 && area > 5000) {
					            if(area > bestArea) {	// Nos quedamos con el más grande
					                bestArea = area;
					                bestMatr = rectangulo;
					            }
					        }
					    }
					}
					
					if(bestMatr != null) {	
						if (ocrDisponible && tesseract != null) {
							try {
								int recorteIzquierda = (int)(bestMatr.width * 0.15);

								Rect roiRect = new Rect(
								        bestMatr.x + recorteIzquierda,
								        bestMatr.y,
								        bestMatr.width - recorteIzquierda,
								        bestMatr.height
								);

								Mat plateROI = new Mat(frame, roiRect);
								
							    Mat grayPlate = new Mat();
							    
							    Imgproc.cvtColor(
							            plateROI,
							            grayPlate,
							            Imgproc.COLOR_BGR2GRAY
							    );

							    Imgproc.threshold(
							            grayPlate,
							            grayPlate,
							            120,
							            255,
							            Imgproc.THRESH_BINARY
							    );

							    Imgproc.resize(
							    	    grayPlate,
							    	    grayPlate,
							    	    new Size(),
							    	    2.0,
							    	    2.0
							    );
							    
							    // ocr 
							    BufferedImage plateImage = matToBufferedImage(grayPlate);
							    String text = tesseract.doOCR(plateImage);

							    text = text.replaceAll("\\s+", "").replaceAll("[^A-Z0-9]", "").trim();

							    // System.out.println("OCR: " + text);

							    Imgproc.putText(
							            frame,
							            text,
							            new Point(bestMatr.x,
							                    bestMatr.y - 10),
							            Imgproc.FONT_HERSHEY_SIMPLEX,
							            1,
							            new Scalar(0,255,0),
							            2
							    );
							} catch (TesseractException e) {
							    e.printStackTrace();
							}
						} else {
							System.out.println("[DEBUG LECTOR] OCR omitido porque no está disponible.");
						}
						
					    Imgproc.rectangle(
					            frame,
					            new Point(bestMatr.x, bestMatr.y),
					            new Point(bestMatr.x + bestMatr.width,
					            		bestMatr.y + bestMatr.height),
					            new Scalar(0,255,0),
					            3
					    );

					}
					HighGui.imshow("[AGENTE LECTOR] Cámara en directo", frame);
					HighGui.waitKey(1);
					
					grises.release();
					blur.release();
					bordes.release();
					hier.release();
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
	
	/**
	 * Metodo que convierte Mat en BufferedImage
	 * @param mat
	 * @return BufferedImage
	 */
	private BufferedImage matToBufferedImage(Mat mat) {
		BufferedImage res = null;
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", mat, mob);
		byte[] byteArray = mob.toArray();
		try {
			res = javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(byteArray));
		} catch(Exception e) {e.printStackTrace();}
		
		return res;
	}
	
}
