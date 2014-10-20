package ch.dragbone.anonsomenauts;

import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.FileUtils;

public class MainWindow implements DocumentListener{

	private JFrame frame;
	private JTextField replayDirectoryTextField;
	private JTextField playernameTextField;
	private JPanel panel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Throwable e){
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				try{
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow(){
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(){
		frame = new JFrame();
		frame.setBounds(100, 100, 565, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		replayDirectoryTextField = new JTextField();
		replayDirectoryTextField.setBounds(109, 8, 331, 20);
		frame.getContentPane().add(replayDirectoryTextField);
		replayDirectoryTextField.setColumns(10);
		replayDirectoryTextField.getDocument().addDocumentListener(this);

		JButton findReplayButton = new JButton("Find replay");
		findReplayButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				FileDialog fd = new FileDialog(frame, "Select Replays.info or Chatlog.info", FileDialog.LOAD);
				fd.setFile("*.info");
				fd.setVisible(true);
				final String replayPath = fd.getDirectory();
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run(){
						replayDirectoryTextField.setText(replayPath);
					}
				});
			}
		});
		findReplayButton.setBounds(450, 7, 89, 23);
		frame.getContentPane().add(findReplayButton);

		panel = new JPanel();
		panel.setBounds(10, 70, 529, 146);
		frame.getContentPane().add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0};
		gbl_panel.rowHeights = new int[]{0};
		gbl_panel.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{Double.MIN_VALUE};
		panel.setLayout(gbl_panel);

		playernameTextField = new JTextField();
		playernameTextField.setText("Anon");
		playernameTextField.setBounds(109, 39, 331, 20);
		frame.getContentPane().add(playernameTextField);
		playernameTextField.setColumns(10);

		JLabel replayDirectoryLabel = new JLabel("Replay directory");
		replayDirectoryLabel.setBounds(10, 11, 89, 14);
		frame.getContentPane().add(replayDirectoryLabel);

		JLabel playernameLabel = new JLabel("Playername");
		playernameLabel.setBounds(10, 42, 89, 14);
		frame.getContentPane().add(playernameLabel);

		JButton anonifyButton = new JButton("Anonify");
		anonifyButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new Replay(replayDirectoryTextField.getText(), playernameTextField.getText()).anonify();
			}
		});
		anonifyButton.setBounds(450, 227, 89, 23);
		frame.getContentPane().add(anonifyButton);
	}

	@Override
	public void insertUpdate(DocumentEvent e){
		checkAndReadReplay();
	}

	@Override
	public void removeUpdate(DocumentEvent e){
		checkAndReadReplay();
	}

	@Override
	public void changedUpdate(DocumentEvent e){
		checkAndReadReplay();
	}

	private void checkAndReadReplay(){
		String dir = replayDirectoryTextField.getText();
		File replay = new File(dir, "Replays.info");
		if(replay.exists() && replay.isFile()){
			List<String> lines;
			try{
				lines = FileUtils.readLines(replay, Replay.defaultCharset);
			}catch(IOException e){
				// ignore
				return;
			}
			final LinkedHashSet<String> redTeam = new LinkedHashSet<>();
			final LinkedHashSet<String> blueTeam = new LinkedHashSet<>();
			float duration = 0f;
			for(String line : lines){
				String name = StringHelper.firstSubMatch(line, " name=\"(.*?)\"");
				String team = StringHelper.firstSubMatch(line, " team=\"(\\d)\"");
				String time = StringHelper.firstSubMatch(line, " timeLeft=\"(\\d+\\.\\d+)\"");
				if(name == null || name.length() == 0 || team == null || team.length() != 1 || time == null)
					continue;
				duration = Math.max(duration, Float.parseFloat(time));
				name = StringHelper.unEscapeXMLString(name); // fix XML escaped name
				if(team.equals("0"))
					redTeam.add(name);
				else if(team.equals("1"))
					blueTeam.add(name);
			}

			final int matchDurationSeconds = (int) duration;
			final int height = Math.max(redTeam.size(), blueTeam.size());

			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run(){
					panel.removeAll();
					int i = 0;
					for(String player : redTeam){
						GridBagConstraints gbc = new GridBagConstraints();
						gbc.fill = GridBagConstraints.HORIZONTAL;
						gbc.gridx = 0;
						gbc.gridy = i++;
						gbc.weightx = 1f;
						panel.add(new JLabel(player, SwingConstants.RIGHT), gbc);
					}

					i = 0;
					for(String player : blueTeam){
						GridBagConstraints gbc = new GridBagConstraints();
						gbc.fill = GridBagConstraints.HORIZONTAL;
						gbc.gridx = 2;
						gbc.gridy = i++;
						gbc.weightx = 1f;
						panel.add(new JLabel(player, SwingConstants.LEFT), gbc);
					}

					// Time
					GridBagConstraints gbct = new GridBagConstraints();
					gbct.fill = GridBagConstraints.VERTICAL;
					gbct.gridx = 0;
					gbct.gridy = height;
					gbct.gridwidth = 3;
					gbct.weighty = 1f;
					int minutes = matchDurationSeconds / 60;
					int seconds = (matchDurationSeconds) % 60;
					panel.add(new JLabel("Match duration: " + minutes + ":" + seconds), gbct);

					// VS
					gbct = new GridBagConstraints();
					gbct.fill = GridBagConstraints.VERTICAL;
					gbct.gridx = 1;
					gbct.gridy = 0;
					gbct.gridheight = height;
					gbct.weightx = 0.5f;
					panel.add(new JLabel("VS"), gbct);

					panel.validate();
					panel.repaint();
				}
			});

		}else{
			// Path no longer valid replay -> remove summary
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run(){
					panel.removeAll();
					panel.validate();
					panel.repaint();
				}
			});
		}
	}
}
