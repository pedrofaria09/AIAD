package Auxiliar;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
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

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		mainForm = new JPanel();
		mainForm.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(23, 2, new Insets(0, 0, 0, 0), -1, -1));
		mainForm.add(panel1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		adicionarButton = new JButton();
		adicionarButton.setText("Adicionar");
		adicionarButton.setVerticalAlignment(0);
		panel1.add(adicionarButton, new GridConstraints(20, 0, 3, 2, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		percentToBuy = new JTextField();
		panel1.add(percentToBuy, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		nome = new JTextField();
		nome.setText("");
		panel1.add(nome, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		percentToSell = new JTextField();
		percentToSell.setText("");
		panel1.add(percentToSell, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		percentMinToSellAndLoose = new JTextField();
		percentMinToSellAndLoose.setText("");
		panel1.add(percentMinToSellAndLoose, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		valueToBuyAction = new JTextField();
		valueToBuyAction.setText("");
		panel1.add(valueToBuyAction, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		percentMinToFollow = new JTextField();
		percentMinToFollow.setText("");
		panel1.add(percentMinToFollow, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		numberOfCotacoesToCheck = new JTextField();
		numberOfCotacoesToCheck.setText("");
		panel1.add(numberOfCotacoesToCheck, new GridConstraints(13, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("Nome");
		panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("valueToBuyAction");
		panel1.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setText("percentToBuy");
		panel1.add(label3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setText("percentToSell");
		panel1.add(label4, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label5 = new JLabel();
		label5.setText("percentMinToSellAndLoose");
		panel1.add(label5, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label6 = new JLabel();
		label6.setText("percentMinToFollow");
		panel1.add(label6, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label7 = new JLabel();
		label7.setText("numberOfCotacoesToCheck");
		panel1.add(label7, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		GoalActionsNumber = new JTextField();
		GoalActionsNumber.setText("");
		panel1.add(GoalActionsNumber, new GridConstraints(15, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label8 = new JLabel();
		label8.setText("GoalActionsNumber");
		panel1.add(label8, new GridConstraints(14, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		TimeToAskBolsa = new JTextField();
		TimeToAskBolsa.setText("");
		panel1.add(TimeToAskBolsa, new GridConstraints(17, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label9 = new JLabel();
		label9.setText("TimeToAskBolsa");
		panel1.add(label9, new GridConstraints(16, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		initialCash = new JTextField();
		initialCash.setText("");
		panel1.add(initialCash, new GridConstraints(19, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label10 = new JLabel();
		label10.setText("initialCash");
		panel1.add(label10, new GridConstraints(18, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainForm.add(panel2, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		bolsaTick = new JTextField();
		bolsaTick.setText("");
		panel2.add(bolsaTick, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label11 = new JLabel();
		label11.setText("BolsaTick");
		panel2.add(label11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, true));
		mainForm.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		okButton = new JButton();
		okButton.setText("OK");
		panel3.add(okButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		defaultButton = new JButton();
		defaultButton.setText("Default");
		panel3.add(defaultButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return mainForm;
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
			//if (isModified(temp)) {
				agents.add(temp);
				System.out.println("Adicionado Agente " + temp.getName());
				System.out.println("Objetivo de vendas de acoes = " + temp.getGoalNumber());
				System.out.println("Banca inicial = " + temp.getStartingMoney());
				setData(new AgentArgs());
			//}
		}
	}

}

