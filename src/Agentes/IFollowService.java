package Agentes;

import App.Acao;
import jadex.commons.future.IFuture;

public interface IFollowService {

	//Verifica os agentes que pode seguir em função dos bons resultados
	public IFuture<Void> checkToFollow(Following f);
	
	//Verifica se está a seguir agentes que começaram a ter maus resultados
	public IFuture<Void> checkToNotFollow(Following f);
	
	//Informa um agente para comprar a ação que ele comprou
	public IFuture<Boolean> buyThisAction(InvestidorAgentBDI agent, Acao acao);
	
	//Informa o agente que agora ele me está a seguir
	public IFuture<Void> tellIsFollowing(InvestidorAgentBDI agent);
	
	//Informa o agente que agora ele me está a seguir
	public IFuture<Void> tellIsNotFollowing(InvestidorAgentBDI agent);
	
}
