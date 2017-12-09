package Agentes;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;

import java.util.HashMap;
import java.util.Map;

public class BolsaAgent_MAIN {

	public static void main(String[] args) {
		//BolsaAgentBDI bolsa = new BolsaAgentBDI();

		//InvestidorAgentBDI agenteArriscado = new InvestidorAgentBDI("Agente Arriscado", 2000, 5, 5, 3, false);

		//InvestidorAgentBDI agenteCauteloso = new InvestidorAgentBDI("Agente Cauteloso", 2000, 10, 5, 4, false);	

		//Agente Arriscado
        Map<String, Object> arriscadoArgs = new HashMap<String, Object>();
        arriscadoArgs.put("nome", "Agente Arriscado");
        arriscadoArgs.put("valueToBuyAction", 3000);
        arriscadoArgs.put("percentToBuy", 5);
        arriscadoArgs.put("percentToSell", 10);
        arriscadoArgs.put("percentMinToSellAndLoose", 2);
        arriscadoArgs.put("numberOfCotacoesToCheck", 3);
        arriscadoArgs.put("timeToAskBolsa", 7050);
        arriscadoArgs.put("isRandomAgent", false);
        arriscadoArgs.put("goalActionsNumber", 5);
        
		//Agente Cauteloso
        Map<String, Object> cautelosoArgs = new HashMap<String, Object>();
        cautelosoArgs.put("nome", "Agente Cauteloso");
        cautelosoArgs.put("valueToBuyAction", 2000);
        cautelosoArgs.put("percentToBuy", 7);
        cautelosoArgs.put("percentToSell", 5);
        cautelosoArgs.put("percentMinToSellAndLoose", 2);
        cautelosoArgs.put("numberOfCotacoesToCheck", 3);
        cautelosoArgs.put("timeToAskBolsa", 7000);
        cautelosoArgs.put("isRandomAgent", false);
        cautelosoArgs.put("goalActionsNumber", 4);
        
		//Agente Random
        Map<String, Object> randomArgs = new HashMap<String, Object>();
        randomArgs.put("nome", "Agente Random");
        randomArgs.put("valueToBuyAction", 2000);
        randomArgs.put("numberOfCotacoesToCheck", 3);
        randomArgs.put("timeToAskBolsa", 6950);
        randomArgs.put("isRandomAgent", true);
        randomArgs.put("goalActionsNumber", 10);
        
        CreationInfo investidorArriscadoInfo = new CreationInfo(arriscadoArgs);
        CreationInfo investidorCautelosoInfo = new CreationInfo(cautelosoArgs);
        CreationInfo investidorRandomInfo = new CreationInfo(randomArgs);

		final ThreadSuspendable sus = new ThreadSuspendable();
		final IExternalAccess platform = Starter.createPlatform(new String[0]).get(sus);
		IComponentManagementService cms = SServiceProvider.getService(platform.getServiceProvider(),
				IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(sus);

		IComponentIdentifier agenteBolsa = cms.createComponent("bin/Agentes/BolsaAgentBDI.class", null).getFirstResult(sus);
		IComponentIdentifier agenteInvestidor1 = cms.createComponent("bin/Agentes/InvestidorAgentBDI.class", investidorArriscadoInfo).getFirstResult(sus);
		IComponentIdentifier agenteInvestidor2 = cms.createComponent("bin/Agentes/InvestidorAgentBDI.class", investidorCautelosoInfo).getFirstResult(sus);
		IComponentIdentifier agenteInvestidor3 = cms.createComponent("bin/Agentes/InvestidorAgentBDI.class", investidorRandomInfo).getFirstResult(sus);


	}

}
