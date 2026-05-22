package proyecto_ssii.agents;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.*;

@SuppressWarnings({ "unused", "serial" })
public class AgenteLector extends Agent {
	protected TickerBehaviour tickerBehaviour; 
	protected void setup() {
		tickerBehaviour = new TickerBehaviour(this, ) {
			
				// TODO Auto-generated method stub
				// 1. Leer frame webcam
				
				
				// 2. Detectar matricula
				
				
				// 3. OCR
				
				
				// 4. Dibujar overlay
				
				
				// 5. Mostrar img
				
				

			@Override
			protected void onTick() {
				// TODO Auto-generated method stub
				
			}
		};
		addBehaviour(tickerBehaviour);
	}
}
