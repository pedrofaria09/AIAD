package Agentes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import App.Acao;
import App.Cotacao;
import jadex.bdiv3.BDIAgent;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class InvestidorAgent extends MicroAgent{
	private List<Acao> ListAcoesCompradas = new ArrayList<Acao>();
	private List<Acao> ListAcoesAtuais = new ArrayList<Acao>();
	private List<Acao> ListAcoesVendidas = new ArrayList<Acao>();
	private List<InvestidorAgent> ListASeguir = new ArrayList<InvestidorAgent>();
	private double cash = 100000;
	private String nome;
	
	public InvestidorAgent(String nome) {
		this.nome = nome;
	}

	public void addListAcoesCompradas(Acao acao) {
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

	public List<InvestidorAgent> getListASeguir() {
		return this.ListASeguir;
	}

	public void addListASeguir(InvestidorAgent agent) {
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
			for(InvestidorAgent ag : this.ListASeguir) {
				System.out.println("\t " + ag.getNome());
			}
		}
		
	}

	@AgentBody
	public IFuture<Void> executeBody() {
		BolsaService bolsa = SServiceProvider.getService(this.getServiceProvider(), BolsaService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		System.out.println(this.getComponentIdentifier().getLocalName());
		bolsa.getValoresBolsa();

		return new Future<>();
	}
}
