package Agentes;
import App.Acao;
import App.Bolsa;
import App.Cotacao;

public class BolsaAgent_MAIN {

	public static void main(String[] args) {
		BolsaAgent bolsa = new BolsaAgent();

		for(Bolsa bol: bolsa.getBolsa()) {
			bol.imprime();
		}

		InvestidorAgent agent = new InvestidorAgent();

		System.out.println(agent.getListAcoesCompradas().size());

		Bolsa bolsa1 = bolsa.getBolsa().get(1);
		Acao acao = new Acao("Agent1",bolsa1.getNome(),bolsa1.getListVariacaoCotacao().get(bolsa1.getListVariacaoCotacao().size()-1));

		agent.addListAcoesCompradas(acao);

		System.out.println(agent.getListAcoesCompradas().size());
		

		while(true) {
			try {
				Thread.sleep(2000);

				bolsa.updateBolsa();

				for(Bolsa bol: bolsa.getBolsa()) {
					bol.imprime();
				}

			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

	}

}
