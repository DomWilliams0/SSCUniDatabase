package dxw405.gui.dialog;

import javax.swing.*;
import java.awt.*;

public class ProgressDialog extends JDialog
{
	public ProgressDialog(String message)
	{
		JPanel panel = new JPanel(new BorderLayout());

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		JLabel label = new JLabel(message, SwingConstants.CENTER);
		panel.add(progressBar,BorderLayout.SOUTH);
		panel.add(label,BorderLayout.CENTER);

		JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE);
		pane.setOptions(new Object[]{});
		setTitle("Working...");
		setContentPane(pane);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);

		setModal(false);
		pack();
		setLocationRelativeTo(null);
	}

	public void begin()
	{
		setVisible(true);
	}

	public void destroy()
	{
		setVisible(false);
		dispose();
	}
}
