package Agentes;
import java.util.ArrayList;
import java.util.List;

import App.Acao;
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
	private int Seguidores = 0;

	public InvestidorAgent() {}

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

	public void incrementaSeguidores() {
		this.Seguidores = this.Seguidores+1;
	}


	@AgentBody
	public IFuture<Void> executeBody() {
		BolsaService bolsa = SServiceProvider.getService(this.getServiceProvider(), BolsaService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		System.out.println(this.getComponentIdentifier().getLocalName());
		bolsa.getValoresBolsa();

		return new Future<>();
	}
}
