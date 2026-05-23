package proyecto_ssii.agents;

import java.util.HashMap;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgenteLogica extends Agent {

    // Base de datos de prueba
    private Map<String, Boolean> pedidosPendientes;
    private boolean[] plazasParking;
    
    private String matriculaActual = "";
    private int plazaAsignadaActual = -1;

    // Constantes para los estados de la FSM
    private static final String ESTADO_ESPERAR = "ESPERAR";
    private static final String ESTADO_COMPROBAR = "COMPROBAR";
    private static final String ESTADO_ASIGNAR = "ASIGNAR";
    private static final String ESTADO_ENTREGA = "ENTREGA";
    private static final String ESTADO_LIBERAR = "LIBERAR";

    @Override
    protected void setup() {
        System.out.println("Agente " + getLocalName() + " iniciado.");
        System.out.println("AID: " + this.getAID());

        //Leemos los parámetros de entrada
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            System.out.println("Parametros recibidos: " + args[0]);
        }

        //Crear servicios proporcionados por el agente y registrarlos en el DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName("Servicio-Parking");
        sd.setType("Gestion-ClickAndCollect");
        dfd.addServices(sd);
        
        try {
            DFService.register(this, dfd);
            System.out.println("Registro en el DF correcto");
        } catch (FIPAException e) {
            System.err.println("Error al registrar servicio: " + e.getMessage());
        }

        // Inicializar datos ficticios
        pedidosPendientes = new HashMap<>();
        pedidosPendientes.put("1111AAA", false);
        pedidosPendientes.put("2222BBB", false);
        pedidosPendientes.put("1234FPM", true); //matricula a escanear correcta
        plazasParking = new boolean[5];

        //Añadimos comportamientos
        FSMBehaviour fsm = new FSMBehaviour(this);

        fsm.registerFirstState(new EsperarCocheBehaviour(), ESTADO_ESPERAR);
        fsm.registerState(new ComprobarMatriculaBehaviour(), ESTADO_COMPROBAR);
        fsm.registerState(new AsignarPlazaBehaviour(), ESTADO_ASIGNAR);
        
        /* Uso de WakerBehaviour para simular la espera de que entreguen la compra 
        	30 segundos de tiempo entre asignar una plaza y liberarla*/
        fsm.registerState(new WakerBehaviour(this, 30000) {
            protected void onWake() {
                System.out.println("Pedido cargado en el coche de " + matriculaActual);
            }
        }, ESTADO_ENTREGA);
        
        fsm.registerState(new LiberarPlazaBehaviour(), ESTADO_LIBERAR);

        // TRANSICIONES (0 = Error/Denegado, 1 = Éxito/Permitido)
        fsm.registerTransition(ESTADO_ESPERAR, ESTADO_COMPROBAR, 1);
        fsm.registerTransition(ESTADO_COMPROBAR, ESTADO_ASIGNAR, 1);
        fsm.registerTransition(ESTADO_COMPROBAR, ESTADO_ESPERAR, 0); // Vuelve a esperar si falla
        
        fsm.registerDefaultTransition(ESTADO_ASIGNAR, ESTADO_ENTREGA);
        fsm.registerDefaultTransition(ESTADO_ENTREGA, ESTADO_LIBERAR);
        fsm.registerDefaultTransition(ESTADO_LIBERAR, ESTADO_ESPERAR);

        addBehaviour(fsm);
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println("Servicio borrado del DF.");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println("Agente de lógica finalizado");
    }

// COMPORTAMIENTOS

    class EsperarCocheBehaviour extends Behaviour {
        private boolean recibido = false;

        public void action() {
            recibido = false;
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            
            if (msg != null) {
                matriculaActual = msg.getContent();
                System.out.println("Recibo el mensaje de: " + msg.getSender().getLocalName());
                System.out.println("Coche en barrera: " + matriculaActual);
                recibido = true;
            } else {
                block(); //Bloqueamos para no consumir
            }
        }

        public boolean done() {
            return recibido; 
        }

        public int onEnd() {
            return 1; //Pasamos a COMPROBAR
        }
    }

    class ComprobarMatriculaBehaviour extends OneShotBehaviour {
        private int result;

        public void action() {
            System.out.println("Procesando...");
            if (pedidosPendientes.containsKey(matriculaActual) && pedidosPendientes.get(matriculaActual)) {
                System.out.println("Todo OK"); 
                result = 1;
            } else {
                System.out.println("Hubo error al comprobar la matrícula"); 
                enviarMensajeUI("ACCESO DENEGADO", -1, 0);
                result = 0;
            }
        }

        public int onEnd() {
            return result; 
        }
    }

    class AsignarPlazaBehaviour extends OneShotBehaviour {
        public void action() {
            plazaAsignadaActual = -1;
            for (int i = 0; i < plazasParking.length; i++) {
                if (!plazasParking[i]) {
                    plazasParking[i] = true; 
                    plazaAsignadaActual = i + 1; 
                    break;
                }
            }

            if (plazaAsignadaActual != -1) {
                System.out.println("Plaza " + plazaAsignadaActual + " asignada.");
                enviarMensajeUI("PLAZA ASIGNADA" , plazaAsignadaActual, 30);
            } else {
                System.out.println("Parking lleno.");
                enviarMensajeUI("PARKING LLENO", -1, 0);
            }
        }
    }

    class LiberarPlazaBehaviour extends OneShotBehaviour {
        public void action() {
            if (plazaAsignadaActual != -1) {
                plazasParking[plazaAsignadaActual - 1] = false; 
                System.out.println("Plaza " + plazaAsignadaActual + " liberada.");
                enviarMensajeUI("PLAZA LIBERADA", plazaAsignadaActual, 0 );
                matriculaActual = "";
                plazaAsignadaActual = -1;
            }
        }
    }

    // Método auxiliar para enviar mensajes a la UI
    private void enviarMensajeUI(String accion, int plaza, int tiempoSegundos) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("AgenteInterfaz", AID.ISLOCALNAME));
        String contenidoFormateado = accion + ";" + plaza + ";" + tiempoSegundos;
        msg.setContent(contenidoFormateado);
        
        send(msg);
    }
}