package App;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cotacao {
	private double cotacao;
	private Date date;

	public Cotacao(double cotacao) {
		this.cotacao = cotacao;
		this.date = new Date();
	}

	public Double getCotacao() {
		return this.cotacao;
	}

	public String getStringData() {
		SimpleDateFormat ft = new SimpleDateFormat ("H:m:s d/M/y");
		return ft.format(this.date);
	}

	public Date getData() {
		return this.date;
	}
	
	public String imprime() {
		SimpleDateFormat ft = new SimpleDateFormat ("H:mm:ss d/M/y");
		return "Cotacao: "+this.cotacao + " - Data: " + ft.format(this.date) + "\n";
	}

}
