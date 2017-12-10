package Agentes;
import java.util.List;

import App.Bolsa;
import jadex.commons.future.IFuture;

public interface BolsaService {
	public IFuture<List<Bolsa>> getValoresBolsa();
}
