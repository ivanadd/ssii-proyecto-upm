package proyecto_ssii.testeo;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

public class TestOpenCV {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("OpenCV cargado.");
        VideoCapture camera = new VideoCapture(0);
        Mat frame = new Mat();
        if(camera.read(frame)) {
            HighGui.imshow("Camara", frame);
            HighGui.waitKey(1);
        }
    }
}
