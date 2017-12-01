package Agentes;
import App.Acao;
import App.Bolsa;
import App.Cotacao;

public class BolsaAgent_MAIN {

	public static void main(String[] args) {
		BolsaAgentBDI bolsa = new BolsaAgentBDI();
		InvestidorAgentBDI agent = new InvestidorAgentBDI("TESTEAGENT");
		agent.imprime();
		
		for(int i = 0; i < 4; i++) {
			try {
				Thread.sleep(2000);
				bolsa.updateBolsa();
				
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

		for(Bolsa bol: bolsa.getBolsa()) {
			bol.imprime();
		}

		bolsa.addListaInvestidores(agent);
		bolsa.comprarAcao(agent, "GALP", 2000);
		bolsa.comprarAcao(agent, "FCP", 500);
		agent.imprime();

		
		for(int i = 0; i < 4; i++) {
			try {
				Thread.sleep(2000);
				bolsa.updateBolsa();
				
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

		for(Bolsa bol: bolsa.getBolsa()) {
			bol.imprime();
		}
		
		bolsa.venderAcao(agent, "GALP");
		bolsa.venderAcao(agent, "FCP");
		agent.imprime();

	}

}
