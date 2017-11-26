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

@Agent
@Service
@ProvidedServices(@ProvidedService(type=BolsaService.class))
public class BolsaAgent extends MicroAgent implements BolsaService {
	private List<Bolsa> bolsaList = new ArrayList<Bolsa>();
	private List<Acao> ListaCompradas = new ArrayList<Acao>();
	private List<Acao> ListaVendidas = new ArrayList<Acao>();
	private List<InvestidorAgent> ListaInvestidores = new ArrayList<InvestidorAgent>();
	

	public BolsaAgent() {
		this.bolsaList = loadBolsa();
	}

	public IFuture<Void> getValoresBolsa() {
		for(Bolsa bol: this.bolsaList) {
			bol.imprime();
		}

		return new Future<>();
	}
	
	public void updateBolsa() {
		int FlagUpdate;
		Cotacao cot;
		double newCot;
		for(Bolsa bol: this.bolsaList) {
			FlagUpdate = ThreadLocalRandom.current().nextInt(0, 2);
			cot = bol.getListVariacaoCotacao().get(bol.getListVariacaoCotacao().size()-1);
			
			if(FlagUpdate == 1) {
				newCot = (cot.getCotacao() + 0.1);
			}else {
				newCot = (cot.getCotacao() - 0.1);
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
		this.bolsaList.add(bolsa);

		cot = new Cotacao(15.9);
		bolsa = new Bolsa("GALP",cot);
		this.bolsaList.add(bolsa);

		cot = new Cotacao(0.7);
		bolsa = new Bolsa("FCP",cot);
		this.bolsaList.add(bolsa);

		cot = new Cotacao(0.7);
		bolsa = new Bolsa("SCP",cot);
		this.bolsaList.add(bolsa);
		
		cot = new Cotacao(1.7);
		bolsa = new Bolsa("Teste",cot);
		this.bolsaList.add(bolsa);

		return this.bolsaList;
	}
	
	public List<Bolsa> getBolsa(){
		return this.bolsaList;
	}
	
	
}
