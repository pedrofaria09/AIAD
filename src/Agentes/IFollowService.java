package Agentes;

import App.Acao;
import jadex.commons.future.IFuture;

public interface IFollowService {

	//Verifica os agentes que pode seguir em fun��o dos bons resultados
	public IFuture<Void> checkToFollow(Following f);
	
	//Verifica se est� a seguir agentes que come�aram a ter maus resultados
	public IFuture<Void> checkToNotFollow(Following f);
	
	//Informa um agente para comprar a a��o que ele comprou
	public IFuture<Boolean> buyThisAction(InvestidorAgentBDI agent, Acao acao);
	
	//Informa o agente que agora ele me est� a seguir
	public IFuture<Void> tellIsFollowing(InvestidorAgentBDI agent);
	
	//Informa o agente que agora ele me est� a seguir
	public IFuture<Void> tellIsNotFollowing(InvestidorAgentBDI agent);
	
}
