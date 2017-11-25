import jadex.bdiv3.BDIAgent;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class InvestigadorAgent extends MicroAgent{
	
	@AgentBody
	public IFuture<Void> executeBody() {
		BolsaService bolsa = SServiceProvider.getService(this.getServiceProvider(), BolsaService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		System.out.println(this.getComponentIdentifier().getLocalName());
		bolsa.getValoresBolsa();
		
		return new Future<>();
	}
}
