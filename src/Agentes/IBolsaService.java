package Agentes;
import jadex.commons.future.IFuture;

public interface IBolsaService {
	public IFuture<Void> getValoresBolsa();
}
