
public class Acao {
	private String NomeAgente;
	private String NomeBolsa;
	private Cotacao cotacao;

	public Acao(String nomeAgente, String nomeBolsa, Cotacao cotacao) {
		this.NomeAgente = nomeAgente;
		this.NomeBolsa = nomeBolsa;
		this.cotacao = cotacao;
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

}
