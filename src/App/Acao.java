package App;

import java.text.SimpleDateFormat;

public class Acao {
	private String NomeAgente;
	private String NomeBolsa;
	private Cotacao cotacao;
	private double valor;

	public Acao(String nomeAgente, String nomeBolsa, Cotacao cotacao, double valorDeCompra) {
		this.NomeAgente = nomeAgente;
		this.NomeBolsa = nomeBolsa;
		this.cotacao = cotacao;
		this.valor = valorDeCompra;
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

	public void setCotacao(Cotacao cotacao) {
		this.cotacao = cotacao;
	}

	public double getValorDeCompra() {
		return valor;
	}

	public void imprime() {
		System.out.print("Nome Agente: "+this.NomeAgente + " - Nome Bolsa: " + this.NomeBolsa + " - Valor: "+this.valor);
		this.cotacao.imprime();
	}

}
