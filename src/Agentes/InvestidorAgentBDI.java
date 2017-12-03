package Agentes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import App.Acao;
import App.Bolsa;
import App.Cotacao;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.ProvidedService;
import jadex.bdiv3.annotation.Trigger;

@Agent
public class InvestidorAgentBDI{
	/*private List<Acao> ListAcoesCompradas = new ArrayList<Acao>();
	private List<Acao> ListAcoesAtuais = new ArrayList<Acao>();
	private List<Acao> ListAcoesVendidas = new ArrayList<Acao>();
	private List<InvestidorBDI> ListASeguir = new ArrayList<InvestidorBDI>();
	private double cash = 100000;*/
	//private String nome;

	private final int TIMETOASKBOLSA = 7000;

	@Agent
	protected BDIAgent agent;

	private List<Bolsa> valoresBolsa;

	@Belief
	public List<Bolsa> getValoresBolsa(){
		return this.valoresBolsa;
	}

	@Belief
	public void setValoresBolsa(List<Bolsa> lista){
		this.valoresBolsa = lista;
	}

	/*public InvestidorAgentBDI(String nome) {
		this.nome = nome;
	}

	public InvestidorAgentBDI() {
		System.out.println("Criou o Agente Investidor");
	}*/

	@Plan(trigger=@Trigger(factchangeds="valoresBolsa"))
	public void printTime() {
		System.out.println("A bolsa foi alterada, oportunidade de analisar os valores!");
	}	


	@AgentBody
	public void body() {
		this.valoresBolsa = new ArrayList<Bolsa>();
		agent.adoptPlan("getValoresABolsa");

	}

	@Plan
	public void getValoresABolsa(IPlan plan) {
		while(true) {
			plan.waitFor(TIMETOASKBOLSA).get();
			BolsaService bolsa = SServiceProvider.getService(agent.getServiceProvider(), BolsaService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
			System.out.println("AGENTE: " + agent.getComponentIdentifier().getLocalName());
			setValoresBolsa(bolsa.getValoresBolsa());
		}
	}

	public void imprimeBolsa() {
		for(Bolsa bol: getValoresBolsa()) {
			bol.imprime();
		}
	}

	/*public void addListAcoesCompradas(Acao acao) {
		this.ListAcoesCompradas.add(acao);
		this.ListAcoesAtuais.add(acao);
	}

	public List<Acao> getListAcoesCompradas() {
		return this.ListAcoesCompradas;
	}

	public List<Acao> getListAcoesAtuais() {
		return this.ListAcoesAtuais;
	}

	public void addListAcoesVendidas(Acao acao) {
		this.ListAcoesVendidas.add(acao);
	}

	public List<InvestidorBDI> getListASeguir() {
		return this.ListASeguir;
	}

	public void addListASeguir(InvestidorBDI agent) {
		this.ListASeguir.add(agent);
	}

	public double getCash() {
		return this.cash;
	}

	public void addCash(double cash) {
		this.cash += cash;
	}

	public void retCash(double cash) {
		this.cash -= cash;
	}

	public String getNome() {
		return nome;
	}

	public void imprime() {
		System.out.println("Nome: " + this.nome + " - Valor em conta: " + this.cash);

		System.out.println("Acoes Atuais: " + this.ListAcoesAtuais.size());
		if(this.ListAcoesAtuais.size() > 0) {
			for(Acao ac : this.ListAcoesAtuais) {
				System.out.print("\t ");
				ac.imprime();
			}
		}

		System.out.println("Acoes compradas: " + this.ListAcoesCompradas.size());
		if(this.ListAcoesCompradas.size() > 0) {
			for(Acao ac : this.ListAcoesCompradas) {
				System.out.print("\t ");
				ac.imprime();
			}
		}

		System.out.println("Acoes Vendidas: " + this.ListAcoesVendidas.size());
		if(this.ListAcoesVendidas.size() > 0) {
			for(Acao ac : this.ListAcoesVendidas) {
				System.out.print("\t ");
				ac.imprime();
			}
		}

		System.out.println("Seguidores: " + this.ListASeguir.size());
		if(this.ListASeguir.size() > 0) {
			for(InvestidorBDI ag : this.ListASeguir) {
				System.out.println("\t " + ag.getNome());
			}
		}

	}*/

}