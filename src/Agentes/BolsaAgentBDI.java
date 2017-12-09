package Agentes;
import java.util.ArrayList;
import java.util.List;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.IPlan;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.ProvidedService;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import java.util.concurrent.ThreadLocalRandom;

import App.Acao;
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
		System.out.println("Criou o Agente Bolsa");
		frame = new AgentLogFrame();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setTitle("Bolsa");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.jTextArea1.append("Hello! I'm the Bolsa Service \n");
				frame.setSize(300, 300);
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
	/*
	private List<Acao> ListaAcoesCompradas = new ArrayList<Acao>();
	private List<Acao> ListaAcoesVendidas = new ArrayList<Acao>();
	private List<Acao> ListaAcoesAtuais = new ArrayList<Acao>();
	private List<InvestidorBDI> ListaInvestidores = new ArrayList<InvestidorBDI>();

	// Getters and adds
	public List<Bolsa> getBolsa(){
		return this.ListaBolsa;
	}

	public List<Acao> getListaAcoesCompradas() {
		return this.ListaAcoesCompradas;
	}

	public void addListaAcoesCompradas(Acao acao) {
		this.ListaAcoesCompradas.add(acao);
	}

	public List<Acao> getListaAcoesVendidas() {
		return this.ListaAcoesVendidas;
	}

	public void addListaAcoesVendidas(Acao acao) {
		this.ListaAcoesVendidas.add(acao);
	}

	public List<Acao> getListaAcoesAtuais() {
		return this.ListaAcoesAtuais;
	}

	public void addListaAcoesAtuais(Acao acao) {
		this.ListaAcoesAtuais.add(acao);
	}

	public List<InvestidorBDI> getListaInvestidores() {
		return this.ListaInvestidores;
	}

	public void addListaInvestidores(InvestidorBDI agente) {
		this.ListaInvestidores.add(agente);
	}*/



	public List<Bolsa> getValoresBolsa() {
		//System.out.println("Vou passar a bolsa para o investidor");
		return this.ListaBolsa;
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
	/*
	public void comprarAcao(InvestidorBDI agent, String nomeAcao, double valorDeCompra) {
		Bolsa bolsa = null;
		Acao acao = null;
		Cotacao lastCotacao = null;

		for(Bolsa bol: ListaBolsa) {
			if(bol.getNome().equals(nomeAcao))
				bolsa = bol;
		}

		if(bolsa != null) {
			lastCotacao = bolsa.getListVariacaoCotacao().get(bolsa.getListVariacaoCotacao().size()-1);
			acao = new Acao(agent.getNome(),bolsa.getNome(),lastCotacao, valorDeCompra);

			agent.addListAcoesCompradas(acao);
			agent.retCash(valorDeCompra);
		}else {
			System.out.println("Bolsa a comprar não foi encontrada");
		}
	}

	public void venderAcao(InvestidorBDI agent, String nomeAcao) {
		Acao acaoAretirar = null, acaoAtual = null;
		Bolsa bolsa = null;
		Cotacao lastCotacao = null;
		for(Acao ac : agent.getListAcoesAtuais()) {
			if(ac.getNomeBolsa().equals(nomeAcao))
				acaoAretirar = ac;
		}

		if(acaoAretirar != null) {

			for(Bolsa bol: ListaBolsa) {
				if(bol.getNome().equals(nomeAcao))
					bolsa = bol;
			}
			lastCotacao = bolsa.getListVariacaoCotacao().get(bolsa.getListVariacaoCotacao().size()-1);

			double taxa = lastCotacao.getCotacao() - acaoAretirar.getCotacao().getCotacao();
			double valorAretirar = acaoAretirar.getValorDeCompra()+acaoAretirar.getValorDeCompra()*taxa;
			valorAretirar = Auxiliar.round(valorAretirar,2);
			acaoAtual = new Acao(agent.getNome(),bolsa.getNome(), lastCotacao, valorAretirar);
			agent.addListAcoesVendidas(acaoAtual);
			agent.addCash(valorAretirar);
			agent.getListAcoesAtuais().remove(acaoAretirar);

		}else {
			System.out.println("Bolsa a vender não foi encontrada");
		}

	}*/

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
			percentagem = (100-(ultimaCotacao*100/penultimaCotacao));
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
