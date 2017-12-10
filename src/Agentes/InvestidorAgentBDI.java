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
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Agent
@Arguments({
		@Argument(name = "nome", clazz = String.class, defaultvalue = "N/A"),
		@Argument(name = "valueToBuyAction", clazz = int.class, defaultvalue = "-1"),
		@Argument(name = "percentToBuy", clazz = int.class, defaultvalue = "-1"),
		@Argument(name = "percentToSell", clazz = int.class, defaultvalue = "-1"),
		@Argument(name = "percentMinToSellAndLoose", clazz = int.class, defaultvalue = "-1"),
		@Argument(name = "percentMinToFollow", clazz = double.class, defaultvalue = "-1"),
		@Argument(name = "numberOfCotacoesToCheck", clazz = int.class, defaultvalue = "1"),
		@Argument(name = "isRandomAgent", clazz = boolean.class, defaultvalue = "false"),
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
	private int percentToBuy;
	private int percentToSell;
	private int percentMinToSellAndLoose;
	private double percentMinToFollow;
	private int valueToFollow;
	private int numberOfCotacoesToCheck;
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
		this.percentMinToSellAndLoose = - (int) agent.getArgument("percentMinToSellAndLoose");
		this.percentMinToFollow = (double) agent.getArgument("percentMinToFollow");
		this.numberOfCotacoesToCheck = (int) agent.getArgument("numberOfCotacoesToCheck");
		this.isRandomAgent = (boolean) agent.getArgument("isRandomAgent");
		this.timeToAskBolsa = (int) agent.getArgument("timeToAskBolsa");
		this.goalActionsNumber = (int) agent.getArgument("goalActionsNumber");

		frame = new AgentLogFrame();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setLocation(600, 2);
				frame.setTitle(nome);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.jTextArea1.append("Ola, sou o " + nome + ", tenho de vender no minimo " + goalActionsNumber + " acoes\n");
				frame.setSize(600,400);
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
		return Auxiliar.round(this.cash, 2);
	}

	public void addCash(double cash) {
		this.cash += Auxiliar.round(cash, 2);
	}

	public void retCash(double cash) {
		this.cash -= Auxiliar.round(cash, 2);
	}

	public String getNome() {
		return nome;
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
		this.valueToFollow = (int) (getCash() + (getCash()*(percentMinToFollow*0.01)));
		
		agent.dispatchTopLevelGoal(new AGoalActionsNumber(this.goalActionsNumber));
	}
	
	@Goal
	public class AGoalActionsNumber {
		@GoalResult
		public int r;

		public AGoalActionsNumber(int r) {
			this.r = r;
		}
	}

	@Plan(trigger = @Trigger(goals = AGoalActionsNumber.class))
	public void getValoresABolsa(IPlan plan) {
		while (!this.soldAll) {
			plan.waitFor(timeToAskBolsa).get();
			BolsaService bolsa = SServiceProvider.getService(agent.getServiceProvider(), BolsaService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();

			bolsa.getValoresBolsa().addResultListener(new DefaultResultListener<List<Bolsa>>() {
				public void resultAvailable(List<Bolsa> listaValoresBolsa) {
					//Um pedido assincrono que n terminou pode alterar a variavel soldAll, enquanto eu estou no waitFor
					if(!soldAll) {
						setValoresBolsa(listaValoresBolsa);
						checkForNewAgentsToFollow();
					}					
				}
			});
		}
		
		imprime();
		frame.jTextArea1.append("*** Acabei vendendo o numero de acoes desejadas - Valor em conta: " + getCash() + " *** \n");
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
		follow = new Following(this, this.valueToFollow);
		SServiceProvider.getServices(agent.getServiceProvider(), IFollowService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new IntermediateDefaultResultListener<IFollowService>() {
			public void intermediateResultAvailable(IFollowService is) {
				is.checkToFollow(follow.clone());
				is.checkToNotFollow(follow.clone());
			}
			
			//Quando termina a comunicacao com todos os agentes, imprime como ficou a sua lista de seguidores e de agentes que esta a seguir
			public void finished() {
				printFollowersAndFollowing();
		    }
		});
	}
	
	/*Recebe um objecto Following que contem:
	 * Agente que procura agentes para seguir
	 * Valor que faz com que ele queira seguir um agente
	*/
	public IFuture<Void> checkToFollow(Following f) {	
		//Para nao comunicar com ele proprio
		if(f.getAgent().getNome() == this.nome) {
			return new Future<Void>();
		}
		
		//Este agente serve para ele
		if(this.getCash() >= f.getValueToFollow()) {
			if(!this.listFollowers.contains(f.getAgent())) {
				this.listFollowers.add(f.getAgent());
			
				f.getAgent().tellIsFollowing(this);
			}		
		} 	
		return new Future<Void>();
	}
	
	public IFuture<Void> checkToNotFollow(Following f) {	
		//Para nao comunicar com ele proprio
		if(f.getAgent().getNome() == this.nome) {
			return new Future<Void>();
		}
		
		//Este agente serve para ele
		if(this.getCash() < f.getValueToFollow()) {
			if(this.listFollowers.contains(f.getAgent())) {
				this.listFollowers.remove(f.getAgent());
				
				f.getAgent().tellIsNotFollowing(this);				
			}
		} 		
		return new Future<Void>();
	}
	
	public IFuture<Void> tellIsFollowing(InvestidorAgentBDI agente) {
		if(!this.listFollowing.contains(agente)) {
			this.listFollowing.add(agente);
		}
		return new Future<Void>();
	}
	
	public IFuture<Void> tellIsNotFollowing(InvestidorAgentBDI agente) {
		if(this.listFollowing.contains(agente)) {
			this.listFollowing.remove(agente);
		}
		return new Future<Void>();
	}

	@Plan(trigger = @Trigger(factchangeds = "valoresBolsa"))
	public void printTime() {
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
				valor = Auxiliar.round(valor,2);
				if (valor >= percentToSell || valor <= percentMinToSellAndLoose) {
					sellAction(acao,valor);
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
					valor = Auxiliar.round(valor,2);
					sellAction(acao, valor);
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

	private void sellAction(Acao ac, double valor) {
		Acao acao = ac;
		Bolsa bolsa = null;
		Cotacao lastCotacao = null;
		Acao acaoAtual = null;
		double lucroToMe, lucroToTheOtherAgent, valorAretirar = 0;

		for (Bolsa bol : getValoresBolsa()) {
			if (bol.getNome().equals(acao.getNomeBolsa()))
				bolsa = bol;
		}
		lastCotacao = bolsa.getListVariacaoCotacao().get(bolsa.getListVariacaoCotacao().size() - 1);

		double taxa = valor*0.01;
		double lucro = (acao.getValorDeCompra() * taxa);
		
		if(ac.getAgenteSugeriu() != null && lucro > 0) {
			lucroToMe = lucro * (2/3);
			lucroToTheOtherAgent = lucro - lucroToMe;
			
			ac.getAgenteSugeriu().giveMoney(this.nome, Auxiliar.round(lucroToTheOtherAgent, 2));
			lucro = lucroToMe;
			
			valorAretirar = acao.getValorDeCompra() + lucro;
			acaoAtual = new Acao(getNome(), bolsa.getNome(), lastCotacao, valorAretirar, ac.getAgenteSugeriu());
		} else {
			valorAretirar = acao.getValorDeCompra() + lucro;
			acaoAtual = new Acao(getNome(), bolsa.getNome(), lastCotacao, valorAretirar);
		}
		
		valorAretirar = Auxiliar.round(valorAretirar, 2);
		
		addListAcoesVendidas(acaoAtual);
		addCash(valorAretirar);

		frame.jTextArea1.append("\nVendi a acao: " + acao.getNomeBolsa() + " com uma %: " + valor + " com uma cotacao de: " + lastCotacao.getCotacao() + " com um valor de: " + valorAretirar + "\n");
		frame.jTextArea1.append("Valor em conta: " + getCash() + "\n");
		
		//Se a acao vendida, corresponder a uma acao que foi comprada por sugestao de um agente
		if(ac.getAgenteSugeriu() != null) {			
			frame.jTextArea1.append("Acao sugerida por: "+ac.getAgenteSugeriu().getNome() +"\n");
		}		
	}

	//Metodo que recebe o lucro recebido de um follower
	private void giveMoney(String agentName, double lucroToTheOtherAgent) {
		this.addCash(lucroToTheOtherAgent);
		
		frame.jTextArea1.append("Recebi do agente : "+ agentName +" o seguinte lucro: "+ lucroToTheOtherAgent + "\n");
	}

	private double getPercetWithAtualCotacao(Acao ac) {
		double lastCotacao = 0;
		for (Bolsa bolsa : getValoresBolsa()) {
			if (ac.getNomeBolsa().equals(bolsa.getNome())) {
				lastCotacao = bolsa.getListVariacaoCotacao().get(bolsa.getListVariacaoCotacao().size() - 1).getCotacao();
				return (100 - ((ac.getCotacao().getCotacao() * 100) / lastCotacao));
			}
		}
		return 0;
	}

	private void checkBuyActionsRandom() {
		// Get Percent of difference taking into action PERCENTTOBUY value
		for (Bolsa bol : getValoresBolsa()) {
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

		frame.jTextArea1.append("\nComprei a acao: " + bolsa.getNome() + " com uma cotacao de: " + lastCotacao.getCotacao() + " gastando " + valueToBuyAction + "\n");
		frame.jTextArea1.append("Valor em conta: " + getCash() + "\n");

		tellFollowersToBuy(acao);
	}
	
	private void tellFollowersToBuy(Acao acao) {
		for(InvestidorAgentBDI agent : this.listFollowers) {
			agent.buyThisAction(this, acao);
		}
	}
	
	//Agente e informado por quem esta a seguir de uma acao para comprar
	public IFuture<Boolean> buyThisAction(InvestidorAgentBDI agent, Acao acao) {
		if(!checkIfDontHaveAction(acao.getNomeBolsa())) {
			String text = "";
			text += "O agente " +agent.getNome()+ " disse para comprar a acao "+ acao.getNomeBolsa() +", mas ja a tenho.\n";
			frame.jTextArea1.append(text);
			return new Future<Boolean>(false);
		}		
		
		if(this.cash > acao.getValorDeCompra()) {
			Acao nAcao = new Acao(getNome(), acao.getNomeBolsa(), acao.getCotacao(), valueToBuyAction, agent);
			this.addListAcoesCompradas(nAcao);
			this.retCash(valueToBuyAction);
			
			String text = "";
			text += "Vou comprar a acao que o " +agent.getNome()+ " comprou:\n";
			text += "Comprei a acao: " + nAcao.getNomeBolsa() + " com uma cotacao de: " + nAcao.getCotacao().getCotacao() + " gastando " + nAcao.getValorDeCompra() + "\n";
			text += "Valor em conta: " + getCash() + "\n";
			
			frame.jTextArea1.append(text);
			return new Future<Boolean>(true);
		} else {
			String text = "";
			text += "Ia a acao "+acao.getNomeBolsa() + " que o " +agent.getNome()+ " comprou, mas nao tenho dinheiro.\n";
			frame.jTextArea1.append(text);
			return new Future<Boolean>(false);
		}
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

}