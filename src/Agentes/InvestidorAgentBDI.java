package Agentes;

import App.Acao;
import App.Bolsa;
import App.Cotacao;
import Auxiliar.AgentLogFrame;
import Auxiliar.Auxiliar;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Agent
@Arguments({
		@Argument(name = "nome", clazz = String.class, defaultvalue = "N/A"),
		@Argument(name = "valueToBuyAction", clazz = int.class, defaultvalue = "-1"),
		@Argument(name = "percentToBuy", clazz = int.class, defaultvalue = "-1"),
		@Argument(name = "numberOfCotacoesToCheck", clazz = int.class, defaultvalue = "1"),
		@Argument(name = "isRandomAgent", clazz = boolean.class, defaultvalue = "false"),
		@Argument(name = "percentToSell", clazz = int.class, defaultvalue = "-1"),
		@Argument(name = "timeToAskBolsa", clazz = int.class, defaultvalue = "-1"),
		@Argument(name = "goalActionsNumber", clazz = int.class, defaultvalue = "-1")
})
@Service
@Description("This agent buys and sells actions")
@ProvidedServices(@ProvidedService(type=IFollowService.class))
@RequiredServices(@RequiredService(name="chatservices", type=IFollowService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)))
public class InvestidorAgentBDI implements IFollowService {
	@Agent
	protected BDIAgent agent;
	private List<Acao> listAcoesCompradas;
	private List<Acao> listAcoesAtuais;
	private List<Acao> listAcoesVendidas;
	private List<Bolsa> valoresBolsa;
	private String nome;
	private double cash = 100000;
	private int timeToAskBolsa;
	private int valueToBuyAction;
	private int percentToBuy; //5% of variation of the action
	private int percentToSell;
	private int numberOfCotacoesToCheck; // will check the last 3 actions to buy.
	private boolean isRandomAgent;
	private AgentLogFrame frame;
	private int goalActionsNumber;
	private boolean soldAll;
	
	private List<InvestidorAgentBDI> listFollowing;	//Quem estou a seguir
	private List<InvestidorAgentBDI> listFollowers;	//Os meus seguidores
	
	protected Following follow;
	
	@AgentCreated
	public void init() {
		this.nome = (String) agent.getArgument("nome");
		this.valueToBuyAction = (int) agent.getArgument("valueToBuyAction");
		this.percentToBuy = (int) agent.getArgument("percentToBuy");
		this.percentToSell = (int) agent.getArgument("percentToSell");
		this.numberOfCotacoesToCheck = (int) agent.getArgument("numberOfCotacoesToCheck");
		this.isRandomAgent = (boolean) agent.getArgument("isRandomAgent");
		this.timeToAskBolsa = (int) agent.getArgument("timeToAskBolsa");
		this.goalActionsNumber = (int) agent.getArgument("goalActionsNumber");

		frame = new AgentLogFrame();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setTitle(nome);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.jTextArea1.append("Hello! I'm " + nome + " and i have to sell at least " + goalActionsNumber + " actions\n");
				frame.setSize(300, 300);
				frame.setVisible(true);
			}
		});

	}

	@Belief
	public List<Bolsa> getValoresBolsa() {
		return this.valoresBolsa;
	}

	@Belief
	public void setValoresBolsa(List<Bolsa> lista) {
		this.valoresBolsa = lista;
	}

	public List<Acao> getListAcoesCompradas() {
		return this.listAcoesCompradas;
	}

	public List<Acao> getListAcoesAtuais() {
		return this.listAcoesAtuais;
	}

	public void addListAcoesCompradas(Acao acao) {
		this.listAcoesCompradas.add(acao);
		this.listAcoesAtuais.add(acao);
	}

	@AgentBody
	public void body() {
		this.valoresBolsa = new ArrayList<Bolsa>();
		this.listAcoesCompradas = new ArrayList<Acao>();
		this.listAcoesAtuais = new ArrayList<Acao>();
		this.listAcoesVendidas = new ArrayList<Acao>();
		this.listFollowing = new ArrayList<InvestidorAgentBDI>();
		this.listFollowers = new ArrayList<InvestidorAgentBDI>();
		this.soldAll = false;

		agent.dispatchTopLevelGoal(new AGoalActionsNumber(this.goalActionsNumber));
	}

	@Plan(trigger = @Trigger(goals = AGoalActionsNumber.class))
	public void getValoresABolsa(IPlan plan) {
		while (!this.soldAll) {
			plan.waitFor(timeToAskBolsa).get();
			BolsaService bolsa = SServiceProvider.getService(agent.getServiceProvider(), BolsaService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();

			setValoresBolsa(bolsa.getValoresBolsa());
			checkForNewAgentsToFollow();
			printFollowersAndFollowing();
		}

		imprime();
		frame.jTextArea1.append("*** Acabei vendendo o n√∫mero de a√ß√µes desejadas - Valor em conta: " + this.cash + " ***");
	}
	
	public void printFollowersAndFollowing() {
		String text = "";
		
		//Seguidores
		text += "\n=======================";
		text += "\n Os meus seguidores: \n";
		for(InvestidorAgentBDI agente : this.listFollowers) {
			text += "- "+agente.getNome()+"\n";
		}
		text += "=======================\n";
		
		//A seguir
		text += "\n=======================";
		text += "\n Quem estou a seguir: \n";
		for(InvestidorAgentBDI agente : this.listFollowing) {
			text += "- "+agente.getNome()+"\n";
		}
		text += "=======================\n"; 
		
		frame.jTextArea1.append(text);
	}
	
	public void checkForNewAgentsToFollow() {		
		follow = new Following(this, 100000);
		SServiceProvider.getServices(agent.getServiceProvider(), IFollowService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new IntermediateDefaultResultListener<IFollowService>() {
			public void intermediateResultAvailable(IFollowService is) {
				is.checkToFollow(follow.clone());
				is.checkToNotFollow(follow.clone());
			}
		});
	}
	
	/*Recebe um objecto Following que contÈm:
	 * Agente que procura agentes para seguir
	 * Valor que faz com que ele queira seguir um agente
	*/
	public void checkToFollow(Following f) {	
		//Para n„o comunicar com ele prÛprio
		if(f.getAgent().getNome() == this.nome) {
			return;
		}
		
		//Este agente serve para ele
		if(this.getCash() >= f.getValueToFollow()) {
			
			if(!this.listFollowers.contains(f.getAgent())) {
				this.listFollowers.add(f.getAgent());
			
				f.getAgent().tellIsFollowing(this);
			}		
			
		} 	
		
	}
	
	public void checkToNotFollow(Following f) {	
		//Para n„o comunicar com ele prÛprio
		if(f.getAgent().getNome() == this.nome) {
			return;
		}
		
		//Este agente serve para ele
		if(this.getCash() < f.getValueToFollow()) {
			if(this.listFollowers.contains(f.getAgent())) {
				boolean result = this.listFollowers.remove(f.getAgent());				
				f.getAgent().tellIsNotFollowing(this);
			}
		} 		
	}
	
	public void tellIsFollowing(InvestidorAgentBDI agente) {
		if(!this.listFollowing.contains(agente)) {
			this.listFollowing.add(agente);
		}
	}
	
	public void tellIsNotFollowing(InvestidorAgentBDI agente) {
		if(this.listFollowing.contains(agente)) {
			boolean result = this.listFollowing.remove(agente);
		}
	}
	
	public boolean containsName(List<InvestidorAgentBDI> list, String name){
	    for(InvestidorAgentBDI agent : list) {
	    	if(agent.getNome().equals(name)) {
	    		System.out.println("ContainsName");
	    		return true;
	    	}
	    }
	    return false;
	}

	@Plan(trigger = @Trigger(factchangeds = "valoresBolsa"))
	public void printTime() {
		//System.out.println("A bolsa foi alterada, oportunidade de analisar os valores!");

		if (this.isRandomAgent) {
			if (this.goalActionsNumber > 0) {
				checkBuyActionsRandom();
			}
			checkSellActionsRandom();
		} else {
			if (this.goalActionsNumber > 0) {
				checkBuyActions();
			}
			checkSellActions();
		}
	}

	private void checkSellActions() {
		double valor = 0;

		if (!this.listAcoesAtuais.isEmpty()) {
			for (int i = 0; i < this.listAcoesAtuais.size(); i++) {
				Acao acao = this.listAcoesAtuais.get(i);
				valor = getPercetWithAtualCotacao(acao);
				if (valor >= percentToSell) {
					frame.jTextArea1.append("VOU VENDER: " + acao.getNomeBolsa() + " a uma %: " + valor + "\n");
					sellAction(acao);
					this.listAcoesAtuais.remove(acao);
					i--;
					this.goalActionsNumber--;
					if (this.goalActionsNumber <= 0 && this.listAcoesAtuais.isEmpty()) {
						this.soldAll = true;
					}
				}
			}
		}

	}

	private void checkSellActionsRandom() {
		double valor = 0;

		if (!this.listAcoesAtuais.isEmpty()) {
			for (int i = 0; i < this.listAcoesAtuais.size(); i++) {
				Acao acao = this.listAcoesAtuais.get(i);

				int FlagUpdate = ThreadLocalRandom.current().nextInt(0, 2);

				if (FlagUpdate == 1) {
					valor = getPercetWithAtualCotacao(acao);
					frame.jTextArea1.append("VOU VENDER: " + acao.getNomeBolsa() + " a uma %: " + valor + "\n");
					sellAction(acao);
					this.listAcoesAtuais.remove(acao);
					i--;
					this.goalActionsNumber--;
					if (this.goalActionsNumber <= 0 && this.listAcoesAtuais.isEmpty()) {
						this.soldAll = true;
					}
				}
			}
		}

	}

	private void sellAction(Acao ac) {
		Acao acao = ac;
		Bolsa bolsa = null;
		Cotacao lastCotacao = null;
		Acao acaoAtual = null;

		for (Bolsa bol : getValoresBolsa()) {
			if (bol.getNome().equals(acao.getNomeBolsa()))
				bolsa = bol;
		}
		lastCotacao = bolsa.getListVariacaoCotacao().get(bolsa.getListVariacaoCotacao().size() - 1);

		double taxa = lastCotacao.getCotacao() - acao.getCotacao().getCotacao();
		double valorAretirar = acao.getValorDeCompra() + acao.getValorDeCompra() * taxa;
		valorAretirar = Auxiliar.round(valorAretirar, 2);
		acaoAtual = new Acao(getNome(), bolsa.getNome(), lastCotacao, valorAretirar);
		addListAcoesVendidas(acaoAtual);
		addCash(valorAretirar);

		//TODO maybe change this to a Trigger????
		frame.jTextArea1.append("Vendi a acao:" + acao.getNomeBolsa() + " com uma cotacao de: " + lastCotacao.getCotacao() + " e ganhei: " + valorAretirar + "\n");
		frame.jTextArea1.append("Valor em conta: " + getCash() + "\n");
	}

	private double getPercetWithAtualCotacao(Acao ac) {
		double lastCotacao = 0;
		for (Bolsa bolsa : getValoresBolsa()) {
			if (ac.getNomeBolsa().equals(bolsa.getNome())) {
				lastCotacao = bolsa.getListVariacaoCotacao().get(bolsa.getListVariacaoCotacao().size() - 1).getCotacao();
				return (100 - (ac.getCotacao().getCotacao() * 100 / lastCotacao));
			}
		}
		return 0;
	}

	private void checkBuyActionsRandom() {
		double valor = 0;

		// Get Percent of difference taking into action PERCENTTOBUY value
		for (Bolsa bol : getValoresBolsa()) {
			valor = 0;
			if (bol.getListVariacaoCotacao().size() >= numberOfCotacoesToCheck) {
				if (checkIfDontHaveAction(bol.getNome())) {
					int FlagUpdate = ThreadLocalRandom.current().nextInt(0, 2);

					if (FlagUpdate == 1) {
						buyAction(bol);
					}
				}
			}
		}
	}

	private void checkBuyActions() {
		double valor = 0;

		// Get Percent of difference taking into action PERCENTTOBUY value
		for (Bolsa bol : getValoresBolsa()) {
			valor = 0;
			if (bol.getListVariacaoCotacao().size() >= numberOfCotacoesToCheck) {
				valor = bol.getPercetOfNCotacoes(numberOfCotacoesToCheck);
				if (valor >= percentToBuy && checkIfDontHaveAction(bol.getNome())) {
					buyAction(bol);
				}
			}
		}
	}

	private void buyAction(Bolsa bolsa) {

		Cotacao lastCotacao = bolsa.getListVariacaoCotacao().get(bolsa.getListVariacaoCotacao().size() - 1);
		Acao acao = new Acao(getNome(), bolsa.getNome(), lastCotacao, valueToBuyAction);

		addListAcoesCompradas(acao);
		retCash(valueToBuyAction);

		//TODO maybe change this to a Trigger????
		frame.jTextArea1.append("Comprei a acao: " + bolsa.getNome() + " com uma cotacao de: " + lastCotacao.getCotacao() + " gastando " + valueToBuyAction + "\n");
		frame.jTextArea1.append("Valor em conta: " + getCash() + "\n");

	}

	private boolean checkIfDontHaveAction(String nome) {

		if (getListAcoesAtuais().isEmpty())
			return true;

		for (Acao ac : getListAcoesAtuais()) {
			if (ac.getNomeBolsa().equals(nome))
				return false;
		}

		return true;
	}

	public void imprimeBolsa() {
		for (Bolsa bol : getValoresBolsa()) {
			bol.imprime();
		}
	}

	public List<Acao> getListAcoesVendidas() {
		return listAcoesVendidas;
	}

	public void addListAcoesVendidas(Acao acao) {
		this.listAcoesVendidas.add(acao);
	}

	public List<InvestidorAgentBDI> getListASeguir() {
		return this.listFollowing;
	}

	public void addListASeguir(InvestidorAgentBDI agent) {
		this.listFollowing.add(agent);
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

		frame.jTextArea1.append("Nome: " + getNome() + " - Valor em conta: " + getCash() + "\n");

		frame.jTextArea1.append("Acoes Atuais: " + getListAcoesAtuais().size() + "\n");

		if (getListAcoesAtuais().size() > 0) {
			for (Acao ac : getListAcoesAtuais()) {
				frame.jTextArea1.append("\t ");
				LinkedList<String> l;
				l = ac.imprime();
				for (String s : l) {
					frame.jTextArea1.append(s);
				}
			}
		}

		frame.jTextArea1.append("Acoes compradas: " + getListAcoesCompradas().size() + " \n");
		if (getListAcoesCompradas().size() > 0) {
			for (Acao ac : getListAcoesCompradas()) {
				frame.jTextArea1.append("\t ");
				LinkedList<String> l;
				l = ac.imprime();
				for (String s : l) {
					frame.jTextArea1.append(s);
				}
			}
		}

		frame.jTextArea1.append("Acoes Vendidas: " + getListAcoesVendidas().size() + " \n");
		if (getListAcoesVendidas().size() > 0) {
			for (Acao ac : getListAcoesVendidas()) {
				frame.jTextArea1.append("\t ");
				LinkedList<String> l;
				l = ac.imprime();
				for (String s : l) {
					frame.jTextArea1.append(s);
				}
			}
		}
		frame.jTextArea1.append("Seguidores: " + getListASeguir().size() + "\n");
		if (getListASeguir().size() > 0) {
			for (InvestidorAgentBDI ag : getListASeguir()) {
				frame.jTextArea1.append("\t " + ag.getNome() + "\n");
			}
		}
	}


	@Goal
	public class AGoalActionsNumber {
		@GoalResult
		public int r;

		public AGoalActionsNumber(int r) {
			this.r = r;
		}
	}
}