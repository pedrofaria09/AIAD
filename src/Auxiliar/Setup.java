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
import java.util.LinkedList;
import java.util.Map;

public class Setup extends JFrame {

	private LinkedList<AgentArgs> agents;
	private ThreadSuspendable sus;
	private IComponentManagementService cms;
	private IExternalAccess platform;
	private JButton okButton;
	private JButton defaultButton;
	private JButton adicionarButton;
	private JTextField valueToBuyAction;
	private JTextField percentMinToSellAndLoose;
	private JTextField nome;
	private JTextField percentToSell;
	private JTextField percentToBuy;
	private JTextField percentMinToFollow;
	private JTextField numberOfCotacoesToCheck;
	private JTextField bolsaTick;
	private JPanel mainForm;
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3;
	private JTextField GoalActionsNumber;
	private JTextField TimeToAskBolsa;
	private JTextField initialCash;

	public Setup(IComponentManagementService cms, ThreadSuspendable sus, final IExternalAccess platform) {
		this.cms = cms;
		this.sus = sus;
		this.platform = platform;
		agents = new LinkedList<AgentArgs>();
		setLayout(new BorderLayout());
		setSize(500, 500);
		add(mainForm);
		pack();
		//setLocationRelativeTo(null);

		defaultButton.addActionListener(new DefaultButtonListener() {
		});
		okButton.addActionListener(new okButtonListener() {
		});
		adicionarButton.addActionListener(new addButtonListener() {
		});
	}

	public void defaultConfiguration(IComponentManagementService cms, ThreadSuspendable sus, IExternalAccess platform) {
		//Agente Arriscado
		Map<String, Object> arriscadoArgs = new HashMap<String, Object>();
		arriscadoArgs.put("nome", "Agente Arriscado");
		arriscadoArgs.put("initialCash", 100000);
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
		cautelosoArgs.put("initialCash", 100000);
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
		randomArgs.put("initialCash", 100000);
		randomArgs.put("valueToBuyAction", 2000);
		randomArgs.put("percentMinToFollow", 0.5);
		randomArgs.put("numberOfCotacoesToCheck", 3);
		randomArgs.put("timeToAskBolsa", 6950);
		randomArgs.put("isRandomAgent", true);
		randomArgs.put("goalActionsNumber", 10);

		//Agente Bolsa
		Map<String, Object> bolsaArgs = new HashMap<String, Object>();
		bolsaArgs.put("TIMEBOLSA", 2000);

		CreationInfo investidorArriscadoInfo = new CreationInfo(arriscadoArgs);
		CreationInfo investidorCautelosoInfo = new CreationInfo(cautelosoArgs);
		CreationInfo investidorRandomInfo = new CreationInfo(randomArgs);
		CreationInfo bolsaInfo = new CreationInfo(bolsaArgs);

		IComponentIdentifier agenteBolsa = this.cms.createComponent("bin/Agentes/BolsaAgentBDI.class", bolsaInfo).getFirstResult(this.sus);
		IComponentIdentifier agenteInvestidor1 = this.cms.createComponent("bin/Agentes/InvestidorAgentBDI.class", investidorArriscadoInfo).getFirstResult(this.sus);
		IComponentIdentifier agenteInvestidor2 = this.cms.createComponent("bin/Agentes/InvestidorAgentBDI.class", investidorCautelosoInfo).getFirstResult(this.sus);
		IComponentIdentifier agenteInvestidor3 = this.cms.createComponent("bin/Agentes/InvestidorAgentBDI.class", investidorRandomInfo).getFirstResult(this.sus);
	}

	public void setData(AgentArgs data) {
		percentToBuy.setText(data.getPercentToBuy());
		nome.setText(data.getName());
		percentToSell.setText(data.getPercentToSell());
		percentMinToSellAndLoose.setText(data.getPercentToLoose());
		valueToBuyAction.setText(data.getValueToBuyAction());
		percentMinToFollow.setText(data.getPercentToFollow());
		numberOfCotacoesToCheck.setText(data.getNumberOfCheck());
		GoalActionsNumber.setText(data.getGoalNumber());
		TimeToAskBolsa.setText(data.getTimeBolsa());
		initialCash.setText(data.getStartingMoney());
	}

	public void getData(AgentArgs data) {
		data.setPercentToBuy(percentToBuy.getText());
		data.setName(nome.getText());
		data.setPercentToSell(percentToSell.getText());
		data.setPercentToLoose(percentMinToSellAndLoose.getText());
		data.setValueToBuyAction(valueToBuyAction.getText());
		data.setPercentToFollow(percentMinToFollow.getText());
		data.setNumberOfCheck(numberOfCotacoesToCheck.getText());
		data.setGoalNumber(GoalActionsNumber.getText());
		data.setTimeBolsa(TimeToAskBolsa.getText());
		data.setStartingMoney(initialCash.getText());
	}

	public boolean isModified(AgentArgs data) {
		if (percentToBuy.getText() != null ? !percentToBuy.getText().equals(data.getPercentToBuy()) : data.getPercentToBuy() != null)
			return true;
		if (nome.getText() != null ? !nome.getText().equals(data.getName()) : data.getName() != null) return true;
		if (percentToSell.getText() != null ? !percentToSell.getText().equals(data.getPercentToSell()) : data.getPercentToSell() != null)
			return true;
		if (percentMinToSellAndLoose.getText() != null ? !percentMinToSellAndLoose.getText().equals(data.getPercentToLoose()) : data.getPercentToLoose() != null)
			return true;
		if (valueToBuyAction.getText() != null ? !valueToBuyAction.getText().equals(data.getValueToBuyAction()) : data.getValueToBuyAction() != null)
			return true;
		if (percentMinToFollow.getText() != null ? !percentMinToFollow.getText().equals(data.getPercentToFollow()) : data.getPercentToFollow() != null)
			return true;
		if (numberOfCotacoesToCheck.getText() != null ? !numberOfCotacoesToCheck.getText().equals(data.getNumberOfCheck()) : data.getNumberOfCheck() != null)
			return true;
		if (GoalActionsNumber.getText() != null ? !GoalActionsNumber.getText().equals(data.getGoalNumber()) : data.getGoalNumber() != null)
			return true;
		if (TimeToAskBolsa.getText() != null ? !TimeToAskBolsa.getText().equals(data.getTimeBolsa()) : data.getTimeBolsa() != null)
			return true;
		if (initialCash.getText() != null ? !initialCash.getText().equals(data.getStartingMoney()) : data.getStartingMoney() != null)
			return true;
		return false;
	}

	private void start(IComponentManagementService cms, ThreadSuspendable sus, IExternalAccess platform, String text, LinkedList<AgentArgs> agents) {

		Map<String, Object> bolsaArgs = new HashMap<String, Object>();
		bolsaArgs.put("TIMEBOLSA", Integer.parseInt(text));
		CreationInfo bolsaInfo = new CreationInfo(bolsaArgs);
		cms.createComponent("bin/Agentes/BolsaAgentBDI.class", bolsaInfo).getFirstResult(sus);

		Map<String, Object> args;
		for (AgentArgs agent : agents) {
			args = new HashMap<String, Object>();
			args.put("nome", agent.getName());
			args.put("initialCash", Integer.parseInt(agent.getStartingMoney()));
			args.put("valueToBuyAction", Integer.parseInt(agent.getValueToBuyAction()));
			args.put("percentMinToFollow", Double.parseDouble(agent.getPercentToFollow()));
			args.put("numberOfCotacoesToCheck", Integer.parseInt(agent.getNumberOfCheck()));
			args.put("timeToAskBolsa", Integer.parseInt(agent.getTimeBolsa()));
			args.put("isRandomAgent", false);
			args.put("goalActionsNumber", Integer.parseInt(agent.getGoalNumber()));
			CreationInfo agentInfo = new CreationInfo(args);
			cms.createComponent("bin/Agentes/InvestidorAgentBDI.class", agentInfo).getFirstResult(sus);
		}




	}

	public class DefaultButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			defaultConfiguration(cms, sus, platform);
			setVisible(false);
		}
	}

	public class okButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			start(cms, sus, platform, bolsaTick.getText(), agents);
			setVisible(false);
		}
	}

	public class addButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			AgentArgs temp = new AgentArgs();
			getData(temp);
			agents.add(temp);
			setData(new AgentArgs());
		}
	}

}

