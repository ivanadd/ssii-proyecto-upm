package proyecto_ssii.testeo;
import org.opencv.core.Core;

public class TestOpenCV {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("OpenCV cargado.");
    }
}
