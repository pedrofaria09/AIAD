package Agentes;
import java.util.ArrayList;
import java.util.List;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.runtime.IPlan;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.ProvidedService;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import java.util.concurrent.ThreadLocalRandom;

import App.Bolsa;
import App.Cotacao;
import Auxiliar.Auxiliar;
import Auxiliar.AgentLogFrame;

import javax.swing.*;

@Agent
@Service
@ProvidedServices(@ProvidedService(type=BolsaService.class))
public class BolsaAgentBDI implements BolsaService {
	private List<Bolsa> ListaBolsa = new ArrayList<Bolsa>();
	private final int TIMEBOLSA = 2000;

	private AgentLogFrame frame;

	public BolsaAgentBDI() {
		frame = new AgentLogFrame();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setTitle("Bolsa");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.jTextArea1.append("Ola, sou o Agente Bolsa \n");
				frame.setSize(500, 400);
				frame.setVisible(true);
			}
		});
		this.ListaBolsa = loadBolsa();
	}

	@Agent
	protected BDIAgent agent;

	@AgentBody
	public void body() {
		agent.adoptPlan("goToUpdateBolsa");
	}

	@Plan
	public void goToUpdateBolsa(IPlan plan) {
		while(true) {
			updateBolsa();
			plan.waitFor(TIMEBOLSA).get();
			frame.jTextArea1.append("Bolsa Atualizada \n");
			imprimeBolsa2();
		}
	}
	
	public IFuture<List<Bolsa>> getValoresBolsa() {
		return new Future<List<Bolsa>>(this.ListaBolsa);
	}

	public void updateBolsa() {
		int FlagUpdate;
		Cotacao cot;
		double newCot;


		for(Bolsa bol: this.ListaBolsa) {
			FlagUpdate = ThreadLocalRandom.current().nextInt(0, 6);
			cot = bol.getListVariacaoCotacao().get(bol.getListVariacaoCotacao().size()-1);
			double auxCotacao = ThreadLocalRandom.current().nextDouble(0.01,0.06);
			
			if(FlagUpdate == 0 || FlagUpdate == 1 || FlagUpdate == 2) {
				newCot = (cot.getCotacao() + cot.getCotacao()*auxCotacao);
			}else if(FlagUpdate == 3 || FlagUpdate == 4){
				newCot = (cot.getCotacao() - cot.getCotacao()*auxCotacao);
				if(newCot < 0)
					newCot = 0;
			}else {
				newCot = cot.getCotacao();
			}

			newCot = Auxiliar.round(newCot,2);
			cot = new Cotacao(newCot);
			bol.addListVariacaoCotacao(cot);
		}
	}
	
	public void imprimeBolsa() {
		for(Bolsa bol: this.ListaBolsa) {
			bol.imprime();
		}
	}
	
	public void imprimeBolsa2() {
		String name;
		Double ultimaCotacao;
		Double penultimaCotacao;
		Double percentagem;
		for(Bolsa bol: this.ListaBolsa) {
			name = bol.getNome();
			ultimaCotacao = bol.getListVariacaoCotacao().get(bol.getListVariacaoCotacao().size()-1).getCotacao();
			penultimaCotacao = bol.getListVariacaoCotacao().get(bol.getListVariacaoCotacao().size()-2).getCotacao();
			percentagem = (100-((ultimaCotacao*100)/penultimaCotacao));
			percentagem = Auxiliar.round(percentagem,2);
			frame.jTextArea1.append(name + " - Variação: " + ultimaCotacao + " - > " + penultimaCotacao + " Percentagem: " + percentagem + "%\n");
		}
	}

	public List<Bolsa> loadBolsa() {

		Bolsa bolsa;
		Cotacao cot;

		cot = new Cotacao(2.9);
		bolsa = new Bolsa("EDP",cot);
		this.ListaBolsa.add(bolsa);

		cot = new Cotacao(15.9);
		bolsa = new Bolsa("GALP",cot);
		this.ListaBolsa.add(bolsa);

		cot = new Cotacao(0.7);
		bolsa = new Bolsa("FCP",cot);
		this.ListaBolsa.add(bolsa);

		cot = new Cotacao(0.7);
		bolsa = new Bolsa("SCP",cot);
		this.ListaBolsa.add(bolsa);

		cot = new Cotacao(1.7);
		bolsa = new Bolsa("Teste",cot);
		this.ListaBolsa.add(bolsa);

		return this.ListaBolsa;
	}


}
