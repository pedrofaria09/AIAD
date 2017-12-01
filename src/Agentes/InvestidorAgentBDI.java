package Agentes;
import java.util.ArrayList;
import java.util.List;

import App.Acao;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.micro.annotation.*;

@RequiredServices({
		@RequiredService(name="sum", type=IBolsaService.class,
				binding=@Binding(scope= RequiredServiceInfo.SCOPE_GLOBAL))
})
@Agent
public class InvestidorAgentBDI{
	private List<Acao> ListAcoesCompradas = new ArrayList<Acao>();
	private List<Acao> ListAcoesAtuais = new ArrayList<Acao>();
	private List<Acao> ListAcoesVendidas = new ArrayList<Acao>();
	private List<InvestidorAgentBDI> ListASeguir = new ArrayList<InvestidorAgentBDI>();
	private double cash = 100000;
	private String nome;
	
	@AgentFeature
	protected IInternalAccess agent;
	
	public InvestidorAgentBDI(String nome) {
		this.nome = nome;
	}
	
	public InvestidorAgentBDI() {
		
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
			for(InvestidorAgentBDI ag : this.ListASeguir) {
				System.out.println("\t " + ag.getNome());
			}
		}
		
	}

	@AgentService
	private IBolsaService bolsa;

	@AgentBody
	public void body() {
		System.out.println("Vou pedir valores da bolsa" + agent.getComponentIdentifier().getLocalName());
		bolsa.getValoresBolsa();

	}
}
