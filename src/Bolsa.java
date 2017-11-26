import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Bolsa {
	private String nome;
	private List<Cotacao> ListVariacaoCotacao = new ArrayList<Cotacao>();

	public Bolsa(String nome, Cotacao cotacao) {
		this.nome = nome;
		this.ListVariacaoCotacao.add(cotacao);
	}

	public String getNome() {
		return this.nome;
	}
	
	public List<Cotacao> getListVariacaoCotacao() {
		return this.ListVariacaoCotacao;
	}

	public void addListVariacaoCotacao(Cotacao cotacao) {
		this.ListVariacaoCotacao.add(cotacao);
	}

	public void imprime() {
		SimpleDateFormat ft = new SimpleDateFormat ("H:mm:ss d/M/y");
		System.out.println("Nome:"+this.nome);
		for(Cotacao cot : this.ListVariacaoCotacao) {
			System.out.println(" Cotacao: "+cot.getCotacao() + " Data: " + ft.format(cot.getData()));
		}
	}
}
