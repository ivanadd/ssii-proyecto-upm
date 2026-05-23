package proyecto_ssii.agents;

//Imports
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class AgenteInterfaz extends Agent {

    //Tipos de acción que llegan en el contenido del mensaje desde AgenteLogica
    public static final String ACCESO_PERMITIDO  = "ACCESO_PERMITIDO";
    public static final String ACCESO_DENEGADO   = "ACCESO_DENEGADO";
    public static final String PEDIDO_EN_PROCESO = "PEDIDO_EN_PROCESO";
    private JFrame frameBarrera;
    private JLabel lblEstado;
    private JLabel lblMatricula;
    private JLabel lblPlaza;
    private JFrame framePlaza;
    private JLabel lblMensajePlaza;
    private JLabel lblTiempo;
    
    //Tiempo en segundos que tarda un pedido en ser entregado
    public static final int TIEMPO_ENTREGA = 30;
    
    //Segundos que se muestra el resultado en la barrera antes de resetear
    public static final int TIEMPO_RESET_BARRERA = 5;
     
    @Override
    protected void setup() {
        System.out.println("[DEBUG INTERFAZ] AgenteInterfaz iniciado.");
        SwingUtilities.invokeLater(this::construirVentanaBarrera);
        addBehaviour(new OneShotBehaviour(this) {
            @Override
            public void action() {
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                actualizarBarrera("Leyendo matrícula...", "", "", Color.WHITE);
                actualizarMensajePlaza("Esperando vehículo...");
                actualizarTiempoPlaza(0);
                System.out.println("[DEBUG INTERFAZ] Estado inicial mostrado.");
            }
        });
    }
    
    @SuppressWarnings("unused")
	private void construirVentanas() {
        construirVentanaBarrera();
        construirVentanaPlaza();
    }
    
    private void construirVentanaBarrera() {
        frameBarrera = new JFrame("Pantalla Barrera — Click and Collect");
        frameBarrera.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frameBarrera.setSize(480, 280);
        frameBarrera.setLocationRelativeTo(null);
        frameBarrera.getContentPane().setBackground(Color.BLACK);
        frameBarrera.setLayout(new GridLayout(3, 1, 0, 10));
 
        lblEstado = crearLabel("Leyendo matrícula...", 26, Color.WHITE);
        lblMatricula = crearLabel("",20, Color.LIGHT_GRAY);
        lblPlaza = crearLabel("",20, Color.LIGHT_GRAY);
 
        frameBarrera.add(lblEstado);
        frameBarrera.add(lblMatricula);
        frameBarrera.add(lblPlaza);
        frameBarrera.setVisible(true);
 
        System.out.println("[DEBUG INTERFAZ] Ventana barrera construida.");
    }
    
    @SuppressWarnings("unused")
	private void construirVentanaPlaza() {
        framePlaza = new JFrame("Pantalla Plaza — Click and Collect");
        framePlaza.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        framePlaza.setSize(480, 200);
        framePlaza.setLocation(
            frameBarrera.getLocation().x,
            frameBarrera.getLocation().y + frameBarrera.getHeight() + 10
        );
        framePlaza.getContentPane().setBackground(new Color(20, 20, 20));
        framePlaza.setLayout(new GridLayout(2, 1, 0, 8));
 
        lblMensajePlaza = crearLabel("Esperando vehículo...", 18, Color.CYAN);
        lblTiempo = crearLabel("", 22, Color.WHITE);
 
        framePlaza.add(lblMensajePlaza);
        framePlaza.add(lblTiempo);
        framePlaza.setVisible(true);
 
        System.out.println("[DEBUG INTERFAZ] Ventana plaza construida.");
    }
    
    private JLabel crearLabel(String texto, int fontSize, Color color) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        label.setForeground(color);
        return label;
    }
    
    //Actualiza la pantalla de la barrera
    @SuppressWarnings("unused")
	private void actualizarBarrera(String estado, String matricula, String plaza, Color colorEstado) {
        SwingUtilities.invokeLater(() -> {
            if (lblEstado == null) return;
            lblEstado.setText(estado);
            lblEstado.setForeground(colorEstado);
            lblMatricula.setText(matricula);
            lblPlaza.setText(plaza);
        });
    }
    //Actualiza el mensaje principal de la pantalla de la plaza 
    @SuppressWarnings("unused")
	private void actualizarMensajePlaza(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            if (lblMensajePlaza == null) return;
            lblMensajePlaza.setText(mensaje);
        });
    }
 
    //Actualiza el contador de tiempo de la pantalla de la plaza
    @SuppressWarnings("unused")
	private void actualizarTiempoPlaza(int segundos) {
        SwingUtilities.invokeLater(() -> {
            if (lblTiempo == null) return;
            if (segundos <= 0) {
                lblTiempo.setText("");
            } else {
                int min = segundos / 60;
                int seg = segundos % 60;
                lblTiempo.setText(String.format("Tiempo restante: %02d:%02d", min, seg));
            }
        });
    }
    
    //Resetea la barrera al estado inicial tras TIEMPO_RESET_BARRERA segundos
    @SuppressWarnings("unused")
	private void resetearBarreraTrasEspera() {
        new Thread(() -> {
            try {
                Thread.sleep(TIEMPO_RESET_BARRERA * 1000L);
            } catch (InterruptedException ignored) {}
            actualizarBarrera("Leyendo matrícula...", "", "", Color.WHITE);
            System.out.println("[DEBUG INTERFAZ] Barrera reseteada.");
        }).start();
    }
    
    
   
    @Override
    protected void takeDown() {
        SwingUtilities.invokeLater(() -> {
            if (frameBarrera != null) frameBarrera.dispose();
            if (framePlaza   != null) framePlaza.dispose();
        });
        System.out.println("[DEBUG INTERFAZ] AgenteInterfaz finalizado.");
    }
}