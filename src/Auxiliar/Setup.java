package Auxiliar;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Setup extends JFrame {

	private ThreadSuspendable sus;
	private IComponentManagementService cms;
	private IExternalAccess platform;


	private JButton okButton;
	private JButton defaultButton;
	private JButton adicionarButton;
	private JTextField startingMoney;
	private JTextField percentageToSell;
	private JTextField nameField;
	private JTextField percentageToBuy;
	private JTextField historyLookback;
	private JTextField numberOfActionsSell;
	private JTextField textField1;
	private JTextField bolsaTick;
	private JPanel mainForm;
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3;

	public Setup(IComponentManagementService cms, ThreadSuspendable sus, final IExternalAccess platform) {
		this.cms = cms;
		this.sus = sus;
		this.platform = platform;
		setLayout(new BorderLayout());
		setSize(500, 500);
		add(mainForm);
		pack();
		//setLocationRelativeTo(null);

		defaultButton.addActionListener(new DefaultButtonListener() {
		});
	}

	public void defaultConfiguration(IComponentManagementService cms, ThreadSuspendable sus, IExternalAccess platform) {
		//Agente Arriscado
		Map<String, Object> arriscadoArgs = new HashMap<String, Object>();
		arriscadoArgs.put("nome", "Agente Arriscado");
		arriscadoArgs.put("valueToBuyAction", 3000);
		arriscadoArgs.put("percentToBuy", 5);
		arriscadoArgs.put("percentToSell", 10);
		arriscadoArgs.put("percentMinToSellAndLoose", 10);
		arriscadoArgs.put("percentMinToFollow", 0.5);
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
		cautelosoArgs.put("percentMinToSellAndLoose", 5);
		cautelosoArgs.put("percentMinToFollow", 0.5);
		cautelosoArgs.put("numberOfCotacoesToCheck", 3);
		cautelosoArgs.put("timeToAskBolsa", 7000);
		cautelosoArgs.put("isRandomAgent", false);
		cautelosoArgs.put("goalActionsNumber", 4);

		//Agente Random
		Map<String, Object> randomArgs = new HashMap<String, Object>();
		randomArgs.put("nome", "Agente Random");
		randomArgs.put("valueToBuyAction", 2000);
		randomArgs.put("percentMinToFollow", 0.5);
		randomArgs.put("numberOfCotacoesToCheck", 3);
		randomArgs.put("timeToAskBolsa", 6950);
		randomArgs.put("isRandomAgent", true);
		randomArgs.put("goalActionsNumber", 10);

		CreationInfo investidorArriscadoInfo = new CreationInfo(arriscadoArgs);
		CreationInfo investidorCautelosoInfo = new CreationInfo(cautelosoArgs);
		CreationInfo investidorRandomInfo = new CreationInfo(randomArgs);

		IComponentIdentifier agenteBolsa = this.cms.createComponent("bin/Agentes/BolsaAgentBDI.class", null).getFirstResult(this.sus);
		IComponentIdentifier agenteInvestidor1 = this.cms.createComponent("bin/Agentes/InvestidorAgentBDI.class", investidorArriscadoInfo).getFirstResult(this.sus);
		IComponentIdentifier agenteInvestidor2 = this.cms.createComponent("bin/Agentes/InvestidorAgentBDI.class", investidorCautelosoInfo).getFirstResult(this.sus);
		IComponentIdentifier agenteInvestidor3 = this.cms.createComponent("bin/Agentes/InvestidorAgentBDI.class", investidorRandomInfo).getFirstResult(this.sus);
	}

	public class DefaultButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			defaultConfiguration(cms, sus, platform);
			setVisible(false);
		}
	}

}

