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
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
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

@Agent
@Service
@ProvidedServices(@ProvidedService(type=BolsaService.class))
@Plans(@Plan(body=@Body(SleepingPlan.class)))
public class BolsaAgentBDI implements BolsaService {
	
	@Agent
	protected BolsaAgentBDI agent;
	
	@Belief
	private List<Bolsa> ListaBolsa = new ArrayList<Bolsa>();
	@Belief
	private List<Acao> ListaAcoesCompradas = new ArrayList<Acao>();
	@Belief
	private List<Acao> ListaAcoesVendidas = new ArrayList<Acao>();
	@Belief
	private List<Acao> ListaAcoesAtuais = new ArrayList<Acao>();
	@Belief
	private List<InvestidorAgent> ListaInvestidores = new ArrayList<InvestidorAgent>();

	// Construtor de BolsaAgent
	public BolsaAgentBDI() {
		this.ListaBolsa = loadBolsa();
	}

	
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

	public List<InvestidorAgent> getListaInvestidores() {
		return this.ListaInvestidores;
	}

	public void addListaInvestidores(InvestidorAgent agente) {
		this.ListaInvestidores.add(agente);
	}

	@AgentBody
	public void body() {
		agent.adoptPlan("methodPlan");
	}
	
	@Plan
	public void methodPlan(IPlan plan) {
		System.out.println("Executing a method plan.");
		plan.waitFor(5000).get();
		System.out.println("Method plan done waiting");
	}
	
	public IFuture<Void> getValoresBolsa() {
		for(Bolsa bol: this.ListaBolsa) {
			bol.imprime();
		}

		return new Future<>();
	}

	public void updateBolsa() {
		int FlagUpdate;
		Cotacao cot;
		double newCot;
		
		
		for(Bolsa bol: this.ListaBolsa) {
			FlagUpdate = ThreadLocalRandom.current().nextInt(0, 6);
			cot = bol.getListVariacaoCotacao().get(bol.getListVariacaoCotacao().size()-1);

			if(FlagUpdate == 0 || FlagUpdate == 1 || FlagUpdate == 5) {
				double auxCotacao = ThreadLocalRandom.current().nextDouble(0.01,0.06);
				newCot = (cot.getCotacao() + cot.getCotacao()*auxCotacao);
			}else if(FlagUpdate == 2 || FlagUpdate == 3){
				double auxCotacao = ThreadLocalRandom.current().nextDouble(0.01,0.06);
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

	public void comprarAcao(InvestidorAgent agent, String nomeAcao, double valorDeCompra) {
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
	
	public void venderAcao(InvestidorAgent agent, String nomeAcao) {
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
