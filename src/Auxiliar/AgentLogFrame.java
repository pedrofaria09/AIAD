package Auxiliar;

import javax.swing.*;

public class AgentLogFrame extends JFrame {

	private JPanel jPanel;
	public JTextArea jTextArea1;

	public AgentLogFrame(){
		initComponents();
	}

	private void initComponents() {
		jPanel = new javax.swing.JPanel();
		jTextArea1 = new JTextArea(2, 25);
		jTextArea1.setEditable(false);
		add(new JScrollPane(jTextArea1));
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	}


}
