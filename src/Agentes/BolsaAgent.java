package Agentes;
import java.util.ArrayList;
import java.util.List;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.ProvidedService;
import java.util.concurrent.ThreadLocalRandom;

import App.Acao;
import App.Bolsa;
import App.Cotacao;
import Aux.Auxiliar;

@Agent
@Service
@ProvidedServices(@ProvidedService(type=BolsaService.class))
public class BolsaAgent extends MicroAgent implements BolsaService {
	private List<Bolsa> ListaBolsa = new ArrayList<Bolsa>();
	private List<Acao> ListaAcoesCompradas = new ArrayList<Acao>();
	private List<Acao> ListaAcoesVendidas = new ArrayList<Acao>();
	private List<Acao> ListaAcoesAtuais = new ArrayList<Acao>();
	private List<InvestidorAgent> ListaInvestidores = new ArrayList<InvestidorAgent>();

	// Construtor de BolsaAgent
	public BolsaAgent() {
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
			FlagUpdate = ThreadLocalRandom.current().nextInt(0, 2);
			cot = bol.getListVariacaoCotacao().get(bol.getListVariacaoCotacao().size()-1);

			if(FlagUpdate == 1) {
				newCot = (cot.getCotacao() + 0.1);
			}else {
				newCot = (cot.getCotacao() - 0.1);
				if(newCot < 0)
					newCot = 0;
			}
			
			newCot = Auxiliar.round(newCot,2);
			cot = new Cotacao(newCot);
			bol.addListVariacaoCotacao(cot);
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
