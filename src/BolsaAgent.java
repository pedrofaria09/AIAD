import java.util.ArrayList;
import java.util.List;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.ProvidedService;

@Agent
@Service
@ProvidedServices(@ProvidedService(type=BolsaService.class))
public class BolsaAgent extends MicroAgent implements BolsaService {
	private List<Bolsa> bolsaList = new ArrayList<Bolsa>();

	public BolsaAgent() {
		this.bolsaList = loadBolsa();
	}

	public IFuture<Void> getValoresBolsa() {
		for(Bolsa bol: this.bolsaList) {
			bol.imprime();
		}

		return new Future<>();
	}


	public List<Bolsa> loadBolsa() {

		Bolsa bolsa;
		bolsa = new Bolsa("EDP",2.909);
		this.bolsaList.add(bolsa);

		bolsa = new Bolsa("GALP",15.925);
		this.bolsaList.add(bolsa);

		bolsa = new Bolsa("FCP",0.73);
		this.bolsaList.add(bolsa);

		bolsa = new Bolsa("SCP",0.70);
		this.bolsaList.add(bolsa);

		return this.bolsaList;
	}
}
