package Agentes;
import jadex.commons.future.IFuture;

public interface BolsaService {
	public IFuture<Void> getValoresBolsa();
}
