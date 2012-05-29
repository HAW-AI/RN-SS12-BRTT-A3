package haw.ai.rn.client;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JSplitPane;
import java.awt.Color;

import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JDesktopPane;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;

public class Chat {

	private JFrame frame;
	private JTextField textMessage;
	private JTextPane messages;
	private JTextPane nicks;
	private Controller controller;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Chat window = new Chat();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Chat(Controller controller) {
		this.controller = controller;
		initialize();
	}
	
	/**
	 * Create the application.
	 */
	public Chat() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmConnectToServer = new JMenuItem("connect to server");
		mntmConnectToServer.setAction(new AbstractAction() {	
			{
				putValue(NAME, "Connect to server");
				putValue(SHORT_DESCRIPTION, "Some short description");
			}
			@Override
			public void actionPerformed(ActionEvent e) {
                String server = JOptionPane.showInputDialog("Server IP", "localhost");
                String name = JOptionPane.showInputDialog("User Name");
				Chat.this.connect(server, name);
			}
		});
		mnFile.add(mntmConnectToServer);
		
		JMenuItem menuItem = new JMenuItem("-");
		menuItem.setEnabled(false);
		mnFile.add(menuItem);
		
		JMenuItem mntmClose = new JMenuItem("close");
		mntmClose.setAction(new AbstractAction() {
			{
				putValue(NAME, "Close");
				putValue(SHORT_DESCRIPTION, "close chat");
			}
			@Override
			public void actionPerformed(ActionEvent e) {
				Chat.this.controller.close();
			}
		});
		mnFile.add(mntmClose);
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		
		JSplitPane container = new JSplitPane();
		container.setOrientation(JSplitPane.VERTICAL_SPLIT);
		springLayout.putConstraint(SpringLayout.NORTH, container, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, container, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, container, 246, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, container, 440, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(container);
		
		JSplitPane incomming = new JSplitPane();
		incomming.setOneTouchExpandable(true);
		container.setLeftComponent(incomming);
		
		JScrollPane msgPane = new JScrollPane();
		incomming.setLeftComponent(msgPane);
		
		messages = new JTextPane();
		messages.setEditable(false);
		msgPane.setViewportView(messages);
		
		JScrollPane nickPane = new JScrollPane();
		incomming.setRightComponent(nickPane);
		
		nicks = new JTextPane();
		nicks.setEditable(false);
		nickPane.setViewportView(nicks);
		incomming.setDividerLocation(280);
		
		JPanel panel = new JPanel();
		container.setRightComponent(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		textMessage = new JTextField();
		panel.add(textMessage);
		textMessage.setColumns(10);
		textMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Chat.this.controller.sendMessage(textMessage.getText());
                textMessage.setText("");
            }
        });
		
		JButton btnSend = new JButton("send");
		btnSend.setToolTipText("send message");
		btnSend.setAction(new AbstractAction() {
			{
				putValue(NAME, "send");
				putValue(SHORT_DESCRIPTION, "Send message");
			}
			@Override
			public void actionPerformed(ActionEvent e) {
				Chat.this.controller.sendMessage(textMessage.getText());
				textMessage.setText("");
			}
		});
		panel.add(btnSend);
		container.setDividerLocation(200);
	}
	public JTextPane getMessages() {
		return messages;
	}
	public JTextPane getNicks() {
		return nicks;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	public void connect(String server, String name) {
		this.controller.connect(server,name);
	}
}
