package Agentes;

public interface IFollowService {

	//public InvestidorAgentBDI niceToFollow(String agentName, int valueToFollow);
	
	//public InvestidorAgentBDI notNiceToFollow(String agentName, int valueToFollow);
	
	public void checkToFollow(Following f);
	
	public void checkToNotFollow(Following f);
	
}
