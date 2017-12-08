package App;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
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
	
	public double getPercetOfNCotacoes(int number) {
		int ultimo = getListVariacaoCotacao().size()-1;
		double valorUltimo = getListVariacaoCotacao().get(ultimo).getCotacao();
		int primeiro = ultimo + 1 - number;
		double valorPrimeiro = getListVariacaoCotacao().get(primeiro).getCotacao();
		
		return (100-(valorPrimeiro*100/valorUltimo));
	}

	public LinkedList<String> imprime() {
		LinkedList<String> list = new LinkedList<String>();
		SimpleDateFormat ft = new SimpleDateFormat ("H:mm:ss d/M/y");
		list.add("Nome:"+this.nome);
		for(Cotacao cot : this.ListVariacaoCotacao) {
			list.add(" Cotacao: "+cot.getCotacao() + " Data: " + ft.format(cot.getData()));
		}
		return list;
	}
}
