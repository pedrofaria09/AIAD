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
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
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
@Description("This agent buys and sells actions")
@ProvidedServices(@ProvidedService(type=IFollowService.class, implementation=@Implementation(FollowService.class)))
@RequiredServices(@RequiredService(name="chatservices", type=IFollowService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)))
public class InvestidorAgentBDI {
	@Agent
	protected BDIAgent agent;
	private List<Acao> ListAcoesCompradas;
	private List<Acao> ListAcoesAtuais;
	private List<Acao> ListAcoesVendidas;
	private List<InvestidorAgentBDI> ListASeguir;
	private List<Bolsa> valoresBolsa;
	private List<String> agentsFollowing;
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
		return this.ListAcoesCompradas;
	}

	public List<Acao> getListAcoesAtuais() {
		return this.ListAcoesAtuais;
	}

	public void addListAcoesCompradas(Acao acao) {
		this.ListAcoesCompradas.add(acao);
		this.ListAcoesAtuais.add(acao);
	}

	@AgentBody
	public void body() {
		this.valoresBolsa = new ArrayList<Bolsa>();
		this.ListAcoesCompradas = new ArrayList<Acao>();
		this.ListAcoesAtuais = new ArrayList<Acao>();
		this.ListAcoesVendidas = new ArrayList<Acao>();
		this.ListASeguir = new ArrayList<InvestidorAgentBDI>();
		this.soldAll = false;
		this.agentsFollowing = new ArrayList<String>();

		agent.dispatchTopLevelGoal(new AGoalActionsNumber(this.goalActionsNumber));
	}

	@Plan(trigger = @Trigger(goals = AGoalActionsNumber.class))
	public void getValoresABolsa(IPlan plan) {
		while (!this.soldAll) {
			plan.waitFor(timeToAskBolsa).get();
			BolsaService bolsa = SServiceProvider.getService(agent.getServiceProvider(), BolsaService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();

			setValoresBolsa(bolsa.getValoresBolsa());
			checkForNewAgentsToFollow();
		}

		imprime();
		frame.jTextArea1.append("*** Acabei vendendo o numero de acoes desejadas - Valor em conta: " + this.cash + " ***");
	}
	
	public void checkForNewAgentsToFollow() {
		IFuture<Collection<IFollowService>> chatservices = agent.getServiceContainer().getRequiredServices("chatservices");
		chatservices.addResultListener(new DefaultResultListener<Collection<IFollowService>>() {	
			public void resultAvailable(Collection<IFollowService> result) {				
				for(IFollowService cs : result) {
					//Para j� ele envia 100.000, mas dps isto passa a uma vari�vel do agente
					String agentToFollow = cs.niceToFollow(agent.getComponentIdentifier().getLocalName(), 100000);
					String agentToUnFollow = cs.notNiceToFollow(agent.getComponentIdentifier().getLocalName(), 100000);
					
					if(agentToFollow != null && agentToFollow != nome) {
						if(!agentsFollowing.contains(agentToFollow)) {
							agentsFollowing.add(agentToFollow);
						}						
					}
					
					if(agentToUnFollow != null && agentToUnFollow != nome) {
						if(agentsFollowing.contains(agentToUnFollow)) {
							agentsFollowing.remove(agentToUnFollow);
						}						
					}
					
				}
				
				String text = "";
				
				if(!agentsFollowing.isEmpty()) {
					text += "==============================================\n";
					text += "Neste momento estou a seguir os agentes:\n";
					for(String agente : agentsFollowing) {
						text += "-" +agente;
					}
					text += "\n==============================================\n\n";
				} else {
					text +="\nNeste momento nao estou a seguir ninguem\n";
				}
				
				frame.jTextArea1.append(text);
			}			
		});
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

		if (!this.ListAcoesAtuais.isEmpty()) {
			for (int i = 0; i < this.ListAcoesAtuais.size(); i++) {
				Acao acao = this.ListAcoesAtuais.get(i);
				valor = getPercetWithAtualCotacao(acao);
				valor = Auxiliar.round(valor,2);
				if (valor >= percentToSell) {
					frame.jTextArea1.append("VOU VENDER: " + acao.getNomeBolsa() + " a uma %: " + valor + "\n");
					sellAction(acao);
					this.ListAcoesAtuais.remove(acao);
					i--;
					this.goalActionsNumber--;
					if (this.goalActionsNumber <= 0 && this.ListAcoesAtuais.isEmpty()) {
						this.soldAll = true;
					}
				}
			}
		}

	}

	private void checkSellActionsRandom() {
		double valor = 0;

		if (!this.ListAcoesAtuais.isEmpty()) {
			for (int i = 0; i < this.ListAcoesAtuais.size(); i++) {
				Acao acao = this.ListAcoesAtuais.get(i);

				int FlagUpdate = ThreadLocalRandom.current().nextInt(0, 2);

				if (FlagUpdate == 1) {
					valor = getPercetWithAtualCotacao(acao);
					valor = Auxiliar.round(valor,2);
					frame.jTextArea1.append("VOU VENDER: " + acao.getNomeBolsa() + " a uma %: " + valor + "\n");
					sellAction(acao);
					this.ListAcoesAtuais.remove(acao);
					i--;
					this.goalActionsNumber--;
					if (this.goalActionsNumber <= 0 && this.ListAcoesAtuais.isEmpty()) {
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
		frame.jTextArea1.append("Vendi a acao:" + acao.getNomeBolsa() + " com uma cotacao de: " + lastCotacao.getCotacao() + " com um valor de: " + valorAretirar + "\n");
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