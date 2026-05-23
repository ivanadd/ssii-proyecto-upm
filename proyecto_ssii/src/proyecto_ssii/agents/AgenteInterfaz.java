package proyecto_ssii.agents;

import jade.core.Agent;

@SuppressWarnings("serial")
public class AgenteInterfaz extends Agent {

    // Tipos de acción que llegan en el contenido del mensaje desde AgenteLogica
    public static final String ACCESO_PERMITIDO  = "ACCESO_PERMITIDO";
    public static final String ACCESO_DENEGADO   = "ACCESO_DENEGADO";
    public static final String PEDIDO_EN_PROCESO = "PEDIDO_EN_PROCESO";

    //Tiempo en segundos que tarda un pedido en ser entregado
    public static final int TIEMPO_ENTREGA = 30;
    
    // Segundos que se muestra el resultado en la barrera antes de resetear
    public static final int TIEMPO_RESET_BARRERA = 5;
     
    
    
    
    
    
    
    
    
    
}