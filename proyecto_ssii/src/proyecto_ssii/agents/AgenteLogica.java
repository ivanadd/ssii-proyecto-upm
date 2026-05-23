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

@SuppressWarnings("serial")
public class AgenteLogica extends Agent {

    //Base de datos ficticia y estado del parking
    private Map<String, Boolean> pedidosPendientes; // Matrícula 
    private boolean[] plazasParking; // Array para simular 5 plazas (false = libre, true = ocupada)
    
    //Variables de estado para la máquina (compartidas entre comportamientos)
    private String matriculaActual = "";
    private int plazaAsignadaActual = -1;

    //Nombres de los estados de la FSM
    private static final String ESTADO_ESPERAR_COCHE = "ESPERAR";
    private static final String ESTADO_COMPROBAR = "COMPROBAR";
    private static final String ESTADO_ASIGNAR = "ASIGNAR";
    private static final String ESTADO_ESPERAR_ENTREGA = "ENTREGA";
    private static final String ESTADO_LIBERAR = "LIBERAR";

    @Override
    protected void setup() {
        System.out.println("[LÓGICA] Agente " + getAID().getLocalName() + " iniciado.");

        // 1. Inicializar datos ficticios
        pedidosPendientes = new HashMap<>();
        pedidosPendientes.put("1111AAA", false);
        pedidosPendientes.put("1234FPM", true); //tiene pedido pendiente
        pedidosPendientes.put("3333CCC", false); 
        
        plazasParking = new boolean[5]; // 5 plazas libres por defecto (0 a 4)

        //2.  Crear la Máquina de Estados Finitos
        FSMBehaviour fsm = new FSMBehaviour(this);

        // 3. Registrar los estados (Comportamientos)
        fsm.registerFirstState(new EsperarCocheBehaviour(), ESTADO_ESPERAR_COCHE);
        fsm.registerState(new ComprobarMatriculaBehaviour(), ESTADO_COMPROBAR);
        fsm.registerState(new AsignarPlazaBehaviour(), ESTADO_ASIGNAR);
        
        // Usamos un WakerBehaviour para simular la espera de la entrega (ej: 5 segundos)
        fsm.registerState(new WakerBehaviour(this, 5000) {
            protected void onWake() {
                System.out.println("[LÓGICA] Pedido entregado al coche " + matriculaActual);
            }
        }, ESTADO_ESPERAR_ENTREGA);
        
        fsm.registerState(new LiberarPlazaBehaviour(), ESTADO_LIBERAR);

        // 4. Definir las transiciones entre estados
        // De ESPERAR pasa siempre a COMPROBAR cuando recibe mensaje (evento 1)
        fsm.registerTransition(ESTADO_ESPERAR_COCHE, ESTADO_COMPROBAR, 1);
        
        // De COMPROBAR puede pasar a ASIGNAR (evento 1: válido) o volver a ESPERAR (evento 0: inválido)
        fsm.registerTransition(ESTADO_COMPROBAR, ESTADO_ASIGNAR, 1);
        fsm.registerTransition(ESTADO_COMPROBAR, ESTADO_ESPERAR_COCHE, 0);
        
        // El resto son transiciones por defecto (pasan automáticamente al terminar el comportamiento)
        fsm.registerDefaultTransition(ESTADO_ASIGNAR, ESTADO_ESPERAR_ENTREGA);
        fsm.registerDefaultTransition(ESTADO_ESPERAR_ENTREGA, ESTADO_LIBERAR);
        fsm.registerDefaultTransition(ESTADO_LIBERAR, ESTADO_ESPERAR_COCHE); // Vuelve al inicio

        // Añadir la FSM al agente
        addBehaviour(fsm);
    }

    // =========================================================================
    // DEFINICIÓN DE LOS COMPORTAMIENTOS (ESTADOS)
    // =========================================================================

    /**
     * ESTADO 1: Espera a que el Agente de Percepción envíe una matrícula.
     */
    private class EsperarCocheBehaviour extends Behaviour {
        private boolean recibido = false;

        @Override
        public void action() {
            recibido = false;
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                matriculaActual = msg.getContent();
                System.out.println("\n[LÓGICA] <-- Coche detectado en barrera: " + matriculaActual);
                recibido = true;
            } else {
                block(); // No consume recursos mientras espera
            }
        }

        @Override
        public boolean done() {
            return recibido; // Termina el estado solo cuando recibe el mensaje
        }

        @Override
        public int onEnd() {
            return 1; // Dispara la transición a COMPROBAR
        }
    }

    /**
     * ESTADO 2: Comprueba si la matrícula tiene pedido.
     */
    private class ComprobarMatriculaBehaviour extends OneShotBehaviour {
        private int resultadoValidacion = 0; // 0 = Denegado, 1 = Permitido

        @Override
        public void action() {
            System.out.println("[LÓGICA] Comprobando pedido para " + matriculaActual + "...");
            
            if (pedidosPendientes.containsKey(matriculaActual) && pedidosPendientes.get(matriculaActual)) {
                System.out.println("[LÓGICA] Pedido ENCONTRADO. Acceso permitido.");
                resultadoValidacion = 1;
            } else {
                System.out.println("[LÓGICA] Pedido NO ENCONTRADO. Acceso denegado.");
                enviarMensajeUI("ACCESO DENEGADO para " + matriculaActual);
                resultadoValidacion = 0;
            }
        }

        @Override
        public int onEnd() {
            return resultadoValidacion; // Dispara transición a ASIGNAR(1) o vuelve a ESPERAR(0)
        }
    }

    /**
     * ESTADO 3: Asigna una plaza libre y avisa a la UI.
     */
    private class AsignarPlazaBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            plazaAsignadaActual = -1;
            // Busca la primera plaza libre
            for (int i = 0; i < plazasParking.length; i++) {
                if (!plazasParking[i]) {
                    plazasParking[i] = true; // Ocupa la plaza
                    plazaAsignadaActual = i + 1; // Plazas del 1 al 5
                    break;
                }
            }

            if (plazaAsignadaActual != -1) {
                System.out.println("[LÓGICA] Plaza " + plazaAsignadaActual + " asignada al coche " + matriculaActual);
                enviarMensajeUI("PLAZA ASIGNADA: " + plazaAsignadaActual + " a matrícula " + matriculaActual);
            } else {
                System.out.println("[LÓGICA] Parking LLENO.");
                enviarMensajeUI("PARKING LLENO, por favor espere.");
            }
        }
    }

    /**
     * ESTADO 5: Libera la plaza tras entregarse el pedido.
     */
    private class LiberarPlazaBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            if (plazaAsignadaActual != -1) {
                plazasParking[plazaAsignadaActual - 1] = false; // Libera la plaza
                System.out.println("[LÓGICA] Plaza " + plazaAsignadaActual + " liberada. El coche se va.");
                enviarMensajeUI("PLAZA LIBERADA: " + plazaAsignadaActual);
                
                // Limpiamos variables para el próximo coche
                matriculaActual = "";
                plazaAsignadaActual = -1;
            }
        }
    }

    //Método auxiliar para comunicarse con el Agente de Visualización
    private void enviarMensajeUI(String contenido) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        // Aseguraos de llamar a vuestro agente de interfaz "AgenteVisualizacion" en el lanzador
        msg.addReceiver(new AID("AgenteVisualizacion", AID.ISLOCALNAME));
        msg.setContent(contenido);
        send(msg);
    }
}