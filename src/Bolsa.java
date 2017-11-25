import java.text.SimpleDateFormat;
import java.util.Date;

public class Bolsa {
	private String nome;
	private double cotacao;
	private Date date;

	public Bolsa(String nome, double cotacao) {
		this.nome = nome;
		this.cotacao = cotacao;
		this.date = new Date();
	}

	public String getNome() {
		return this.nome;
	}

	public Double getCotacao() {
		return this.cotacao;
	}

	public String getData() {
		SimpleDateFormat ft = new SimpleDateFormat ("H:m:s d/M/y");
		return ft.format(this.date);
	}

	public void imprime() {
		SimpleDateFormat ft = new SimpleDateFormat ("H:m:s d/M/y");
		System.out.println("Nome:"+this.nome+" Valor:"+this.cotacao+ " Data:" + ft.format(this.date));
	}
}
