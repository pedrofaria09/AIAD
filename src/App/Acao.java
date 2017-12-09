package App;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

import Agentes.InvestidorAgentBDI;

public class Acao {
	private String NomeAgente;
	private String NomeBolsa;
	private Cotacao cotacao;
	private double valor;
	private InvestidorAgentBDI agenteSugeriu;

	public Acao(String nomeAgente, String nomeBolsa, Cotacao cotacao, double valorDeCompra) {
		this.NomeAgente = nomeAgente;
		this.NomeBolsa = nomeBolsa;
		this.cotacao = cotacao;
		this.valor = valorDeCompra;
		this.agenteSugeriu = null;
	}
	
	public Acao(String nomeAgente, String nomeBolsa, Cotacao cotacao, double valorDeCompra, InvestidorAgentBDI agenteASeguir) {
		this.NomeAgente = nomeAgente;
		this.NomeBolsa = nomeBolsa;
		this.cotacao = cotacao;
		this.valor = valorDeCompra;
		this.agenteSugeriu = agenteASeguir;
	}

	public String getNomeAgente() {
		return this.NomeAgente;
	}

	public void setNomeAgente(String nomeAgente) {
		this.NomeAgente = nomeAgente;
	}

	public String getNomeBolsa() {
		return this.NomeBolsa;
	}

	public void setNomeBolsa(String nomeBolsa) {
		this.NomeBolsa = nomeBolsa;
	}

	public Cotacao getCotacao() {
		return this.cotacao;
	}
	
	public InvestidorAgentBDI getAgenteSugeriu() {
		return this.agenteSugeriu;
	}

	public void setCotacao(Cotacao cotacao) {
		this.cotacao = cotacao;
	}

	public double getValorDeCompra() {
		return valor;
	}

	public LinkedList<String> imprime() {
		LinkedList<String> l = new LinkedList<String>();
		l.add("Nome Agente: "+this.NomeAgente + " - Nome Bolsa: " + this.NomeBolsa + " - Valor: "+this.valor + "\n");
		if(this.getAgenteSugeriu() != null) {
			l.add("Acao sugerida pelo agente: "+this.getAgenteSugeriu().getNome() + "\n");
		}
		l.add(this.cotacao.imprime());
		return l;
	}

}
