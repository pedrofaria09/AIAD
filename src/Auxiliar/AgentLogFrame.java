package Auxiliar;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class AgentLogFrame extends JFrame {

	private JPanel jPanel;
	public JTextArea jTextArea1;

	public AgentLogFrame(){
		initComponents();
	}

	private void initComponents() {

		jPanel = new javax.swing.JPanel();

		jTextArea1 = new JTextArea(30, 40);
		jTextArea1.setEditable(false);

		String SO = System.getProperty("os.name");
		if(!SO.equals("Mac OS X")) {
			DefaultCaret caret = (DefaultCaret)jTextArea1.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		}
		
		add(new JScrollPane(jTextArea1),BorderLayout.CENTER);
		super.setPreferredSize(new Dimension(300,300));
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

	}
}
