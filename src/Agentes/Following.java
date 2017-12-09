package Agentes;

public class Following extends CloneableObject {

	private InvestidorAgentBDI agent;
	private int valueToFollow;

	public Following(InvestidorAgentBDI agent, int valueToFollow) {
		this.agent = agent;
		this.valueToFollow = valueToFollow;
	}

	public InvestidorAgentBDI getAgent() {
		return agent;
	}

	public void setAgent(InvestidorAgentBDI agent) {
		this.agent = agent;
	}

	public int getValueToFollow() {
		return valueToFollow;
	}

	public void setValueToFollow(int valueToFollow) {
		this.valueToFollow = valueToFollow;
	}
	
    public Following clone() {
        return (Following)super.clone();
    }

}
