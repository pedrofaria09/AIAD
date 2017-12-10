package Auxiliar;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

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
		//DefaultCaret caret = (DefaultCaret)jTextArea1.getCaret();
		//caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		add(new JScrollPane(jTextArea1));
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	}


}
