package Agentes;
import java.text.SimpleDateFormat;
import jadex.bdiv3.annotation.*;
import jadex.micro.annotation.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadLocalRandom;

import App.Acao;
import App.Bolsa;
import App.Cotacao;
import Auxiliar.Auxiliar;
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
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.ProvidedService;
import jadex.bdiv3.annotation.Trigger;

@Agent
@Arguments({
	@Argument(name="nome", clazz = String.class, defaultvalue="N/A"),
	@Argument(name="valueToBuyAction", clazz = int.class, defaultvalue="-1"),
	@Argument(name="percentToBuy", clazz = int.class, defaultvalue="-1"),
	@Argument(name="numberOfCotacoesToCheck", clazz = int.class, defaultvalue="1"),
	@Argument(name="isRandomAgent", clazz = boolean.class, defaultvalue="false"),
	@Argument(name="percentToSell", clazz = int.class, defaultvalue="-1"),
	@Argument(name="timeToAskBolsa", clazz = int.class, defaultvalue="-1"),
	@Argument(name="goalActionsNumber", clazz = int.class, defaultvalue="-1")
})
public class InvestidorAgentBDI{
	private List<Acao> ListAcoesCompradas;
	private List<Acao> ListAcoesAtuais;
	private List<Acao> ListAcoesVendidas;
	private List<InvestidorAgentBDI> ListASeguir;
	private List<Bolsa> valoresBolsa;
	
	private String nome;
	private double cash = 100000;
	
	private int timeToAskBolsa;	
	private int valueToBuyAction;	
	private int percentToBuy; //5% of variation of the action
	private int percentToSell;
	private int numberOfCotacoesToCheck; // will check the last 3 actions to buy.
	private boolean isRandomAgent;
	private int goalActionsNumber;
	
	@Agent
	protected BDIAgent agent;
	
	@AgentCreated
	public void init() {
		this.nome = (String) agent.getArgument("nome");
		this.valueToBuyAction = (int) agent.getArgument("valueToBuyAction");
		this.percentToBuy = (int) agent.getArgument("percentToBuy");
		this.percentToSell= (int)agent.getArgument("percentToSell");
		this.numberOfCotacoesToCheck = (int) agent.getArgument("numberOfCotacoesToCheck");
		this.isRandomAgent = (boolean) agent.getArgument("isRandomAgent");
		this.timeToAskBolsa = (int) agent.getArgument("timeToAskBolsa");
		this.goalActionsNumber = (int) agent.getArgument("goalActionsNumber");
	}

	@Belief
	public List<Bolsa> getValoresBolsa(){
		return this.valoresBolsa;
	}

	@Belief
	public void setValoresBolsa(List<Bolsa> lista){
		this.valoresBolsa = lista;
	}
	
	public List<Acao> getListAcoesCompradas() {
		return this.ListAcoesCompradas;
	}
	
	public List<Acao> getListAcoesAtuais() {
		return this.ListAcoesAtuais;
	}

	public void addListAcoesCompradas(Acao acao) {
		this.ListAcoesCompradas.add(acao);
		this.ListAcoesAtuais.add(acao);
	}
	
	@Goal
	public class AGoalActionsNumber {
		@GoalResult
		public int r;
	 
		public AGoalActionsNumber(int r) {
			this.r = r;
		}
	}
	
	@AgentBody
	public void body() {
		this.valoresBolsa = new ArrayList<Bolsa>();
		this.ListAcoesCompradas = new ArrayList<Acao>();
		this.ListAcoesAtuais = new ArrayList<Acao>();
		this.ListAcoesVendidas = new ArrayList<Acao>();
		this.ListASeguir = new ArrayList<InvestidorAgentBDI>();
		
		agent.dispatchTopLevelGoal(new AGoalActionsNumber(this.goalActionsNumber));	
	}
	
	@Plan(trigger=@Trigger(goals=AGoalActionsNumber.class))
	public void getValoresABolsa(IPlan plan) {
		while(this.goalActionsNumber > 0) {
			plan.waitFor(timeToAskBolsa).get();
			BolsaService bolsa = SServiceProvider.getService(agent.getServiceProvider(), BolsaService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
			
			setValoresBolsa(bolsa.getValoresBolsa());
		}
		
		imprime();
		System.out.println("["+this.nome+"] - Acabei vendendo o número de ações desejadas");
		
	}

	
	@Plan(trigger=@Trigger(factchangeds="valoresBolsa"))
	public void printTime() {
		//System.out.println("A bolsa foi alterada, oportunidade de analisar os valores!");
		
		if(this.isRandomAgent) {
			if(this.goalActionsNumber > 0) {
				checkBuyActionsRandom();
			}
			checkSellActionsRandom();
		} else {
			if(this.goalActionsNumber > 0) {
				checkBuyActions();
			}
			checkSellActions();
		}
	}


	private void checkSellActions() {
		double valor = 0;
		
		if(!this.ListAcoesAtuais.isEmpty()) {
			for(int i = 0; i< this.ListAcoesAtuais.size(); i++) {
				Acao acao = this.ListAcoesAtuais.get(i);
				valor = getPercetWithAtualCotacao(acao);
				if(valor >= percentToSell) {
					System.out.print("["+this.nome+"] ");
					System.out.println("VOU VENDER :" + acao.getNomeBolsa() + " a uma %: " + valor);
					sellAction(acao);
					this.ListAcoesAtuais.remove(acao);
					i--;
					this.goalActionsNumber--;
				}
			}
		}
		
	}
	
	private void checkSellActionsRandom() {
		double valor = 0;
		
		if(!this.ListAcoesAtuais.isEmpty()) {
			for(int i = 0; i< this.ListAcoesAtuais.size(); i++) {
				Acao acao = this.ListAcoesAtuais.get(i);

				int FlagUpdate = ThreadLocalRandom.current().nextInt(0, 2);
				
				if(FlagUpdate == 1) {
					System.out.print("["+this.nome+"] ");
					System.out.println("VOU VENDER: " + acao.getNomeBolsa() + " a uma %: " + valor);
					sellAction(acao);
					this.ListAcoesAtuais.remove(acao);
					i--;
					this.goalActionsNumber--;
				}
			}
		}
		
	}

	private void sellAction(Acao ac) {
		Acao acao = ac;
		Bolsa bolsa = null;
		Cotacao lastCotacao = null;
		Acao acaoAtual = null;
		
		for(Bolsa bol: getValoresBolsa()) {
			if(bol.getNome().equals(acao.getNomeBolsa()))
				bolsa = bol;
		}
		lastCotacao = bolsa.getListVariacaoCotacao().get(bolsa.getListVariacaoCotacao().size()-1);

		double taxa = lastCotacao.getCotacao() - acao.getCotacao().getCotacao();
		double valorAretirar = acao.getValorDeCompra()+acao.getValorDeCompra()*taxa;
		valorAretirar = Auxiliar.round(valorAretirar,2);
		acaoAtual = new Acao(getNome(),bolsa.getNome(), lastCotacao, valorAretirar);
		addListAcoesVendidas(acaoAtual);
		addCash(valorAretirar);
		
		//TODO maybe change this to a Trigger????
		System.out.println("Vendi a acao:" + acao.getNomeBolsa() + " com uma cotacao de: " + lastCotacao.getCotacao() +" e ganhei: " + valorAretirar);
		System.out.println("Valor em conta: " + getCash());
	}

	private double getPercetWithAtualCotacao(Acao ac) {
		double lastCotacao = 0;
		for(Bolsa bolsa: getValoresBolsa()) {
			if(ac.getNomeBolsa().equals(bolsa.getNome())) {
				lastCotacao = bolsa.getListVariacaoCotacao().get(bolsa.getListVariacaoCotacao().size()-1).getCotacao();
				return (100-(ac.getCotacao().getCotacao()*100/lastCotacao));
			}
		}
		return 0;
	}
	
	private void checkBuyActionsRandom() {
		double valor = 0;
		
		// Get Percent of difference taking into action PERCENTTOBUY value
		for(Bolsa bol: getValoresBolsa()) {
			valor = 0;
			if(bol.getListVariacaoCotacao().size() >= numberOfCotacoesToCheck) {				
				if(checkIfDontHaveAction(bol.getNome())) {
					int FlagUpdate = ThreadLocalRandom.current().nextInt(0, 2);
					
					if(FlagUpdate == 1) {
						buyAction(bol);
					}					
				}
			}
		}
	}

	private void checkBuyActions() {
		double valor = 0;
		
		// Get Percent of difference taking into action PERCENTTOBUY value
		for(Bolsa bol: getValoresBolsa()) {
			valor = 0;
			if(bol.getListVariacaoCotacao().size() >= numberOfCotacoesToCheck) {
				valor = bol.getPercetOfNCotacoes(numberOfCotacoesToCheck);
				if(valor >= percentToBuy && checkIfDontHaveAction(bol.getNome())) {
					buyAction(bol);
				}
			}
		}
	}
	

	private void buyAction(Bolsa bolsa) {
		
		Cotacao lastCotacao = bolsa.getListVariacaoCotacao().get(bolsa.getListVariacaoCotacao().size()-1);
		Acao acao = new Acao(getNome(),bolsa.getNome(),lastCotacao, valueToBuyAction);

		addListAcoesCompradas(acao);
		retCash(valueToBuyAction);
		
		//TODO maybe change this to a Trigger????
		System.out.print("["+this.nome+"] ");
		System.out.println("Comprei a acao: " + bolsa.getNome() + " com uma cotacao de: " + lastCotacao.getCotacao() + " gastando " + valueToBuyAction);
		System.out.println("Valor em conta: " + getCash());
	}

	private boolean checkIfDontHaveAction(String nome) {
		
		if (getListAcoesAtuais().isEmpty())
			return true;
		
		for(Acao ac : getListAcoesAtuais()) {
			if(ac.getNomeBolsa().equals(nome))
				return false;
		}
		
		return true;
	}
	
	public void imprimeBolsa() {
		for(Bolsa bol: getValoresBolsa()) {
			bol.imprime();
		}
	}


	public List<Acao> getListAcoesVendidas() {
		return ListAcoesVendidas;
	}

	public void addListAcoesVendidas(Acao acao) {
		this.ListAcoesVendidas.add(acao);
	}

	public List<InvestidorAgentBDI> getListASeguir() {
		return this.ListASeguir;
	}

	public void addListASeguir(InvestidorAgentBDI agent) {
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
		System.out.println("Nome: " + getNome() + " - Valor em conta: " + getCash());

		System.out.println("Acoes Atuais: " + getListAcoesAtuais().size());
		if(getListAcoesAtuais().size() > 0) {
			for(Acao ac : getListAcoesAtuais()) {
				System.out.print("\t ");
				ac.imprime();
			}
		}

		System.out.println("Acoes compradas: " + getListAcoesCompradas().size());
		if(getListAcoesCompradas().size() > 0) {
			for(Acao ac : getListAcoesCompradas()) {
				System.out.print("\t ");
				ac.imprime();
			}
		}

		System.out.println("Acoes Vendidas: " + getListAcoesVendidas().size());
		if(getListAcoesVendidas().size() > 0) {
			for(Acao ac : getListAcoesVendidas()) {
				System.out.print("\t ");
				ac.imprime();
			}
		}

		System.out.println("Seguidores: " + getListASeguir().size());
		if(getListASeguir().size() > 0) {
			for(InvestidorAgentBDI ag : getListASeguir()) {
				System.out.println("\t " + ag.getNome());
			}
		}

	}

}