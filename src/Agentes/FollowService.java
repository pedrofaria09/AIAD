package Agentes;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;

@Service
public class FollowService implements IFollowService {

	@ServiceComponent
	protected InvestidorAgentBDI agent;

	public String niceToFollow(String agentName, int valueToFollow) {		
		if(agent.getCash() >= valueToFollow) {
			return agent.getNome();
		} else {
			return null;
		}
	}
	
	public String notNiceToFollow(String agentName, int valueToFollow) {		
		if(agent.getCash() < valueToFollow) {
			return agent.getNome();
		} else {
			return null;
		}
	}

}