package ch.dragbone.anonsomenauts;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class ExceptionDialog extends JDialog{
	private static final long serialVersionUID = 5402122879594257117L;
	private final JPanel contentPanel = new JPanel();
	private final ExceptionDialog dialog;

	/**
	 * Create the dialog.
	 */
	public ExceptionDialog(JFrame parent, Exception e){
		super(parent);
		dialog = this;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JLabel lblSorryAnException = new JLabel("An exception occured:");
			contentPanel.add(lblSorryAnException, BorderLayout.NORTH);
		}
		{
			JTextArea textArea = new JTextArea();
			contentPanel.add(textArea, BorderLayout.CENTER);
			textArea.setText(e.getMessage());
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setHorizontalAlignment(SwingConstants.LEFT);
				okButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						SwingUtilities.invokeLater(new Runnable(){
							@Override
							public void run(){
								dialog.dispose();
							}
						});
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

}
