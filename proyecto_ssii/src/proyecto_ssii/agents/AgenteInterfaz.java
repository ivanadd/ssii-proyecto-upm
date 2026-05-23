package proyecto_ssii.agents;

//Imports
import jade.core.Agent;
import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class AgenteInterfaz extends Agent {

    // Tipos de acción que llegan en el contenido del mensaje desde AgenteLogica
    public static final String ACCESO_PERMITIDO  = "ACCESO_PERMITIDO";
    public static final String ACCESO_DENEGADO   = "ACCESO_DENEGADO";
    public static final String PEDIDO_EN_PROCESO = "PEDIDO_EN_PROCESO";
    private JFrame frameBarrera;
    private JLabel lblEstado;
    private JLabel lblMatricula;
    private JLabel lblPlaza;
    
    // Tiempo en segundos que tarda un pedido en ser entregado
    public static final int TIEMPO_ENTREGA = 30;
    
    // Segundos que se muestra el resultado en la barrera antes de resetear
    public static final int TIEMPO_RESET_BARRERA = 5;
     
    @Override
    protected void setup() {
        System.out.println("[DEBUG INTERFAZ] AgenteInterfaz iniciado.");
    }
    
    @Override
    protected void takeDown() {
        System.out.println("[DEBUG INTERFAZ] AgenteInterfaz finalizado.");
        SwingUtilities.invokeLater(this::construirVentanaBarrera);
    }
    
    private void construirVentanaBarrera() {
        frameBarrera = new JFrame("Pantalla Barrera — Click and Collect");
        frameBarrera.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frameBarrera.setSize(480, 280);
        frameBarrera.setLocationRelativeTo(null);
        frameBarrera.getContentPane().setBackground(Color.BLACK);
        frameBarrera.setLayout(new GridLayout(3, 1, 0, 10));
 
        lblEstado    = crearLabel("Leyendo matrícula...", 26, Color.WHITE);
        lblMatricula = crearLabel("",                     20, Color.LIGHT_GRAY);
        lblPlaza     = crearLabel("",                     20, Color.LIGHT_GRAY);
 
        frameBarrera.add(lblEstado);
        frameBarrera.add(lblMatricula);
        frameBarrera.add(lblPlaza);
        frameBarrera.setVisible(true);
 
        System.out.println("[DEBUG INTERFAZ] Ventana barrera construida.");
    }
 
    
    
    
    
    
}