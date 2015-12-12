package com.skwei;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 * @author Wei
 *
 */
public class Client {
	static final int EMO_COL = 7, EMO_ROW = 4, EMO_WIDTH = 62, EMO_HEIGHT = 62;
	
	private static final Color[][] THEME_COLOR = new Color[][]{
			{new Color(0x0099cc), new Color(0x33b5e5)}, 
			{new Color(0x669900), new Color(0x99cc00)}, 
			{new Color(0x9933cc), new Color(0xaa66cc)}, 
			{new Color(0xff8800), new Color(0xffbb33)}, 
			{new Color(0xcc0000), new Color(0xff4444)}};
	
	private final int THEME_NUM = THEME_COLOR.length;
	
//	private String SERVER_IP = "afsaccess1.njit.edu";
	private String SERVER_IP = "localhost";
	private int SERVER_PORT = Server.DEFAULT_PORT;
	
	private ClientThread clientThread;
	private JFrame frameLogin;
	private JFrame frameChat;
	private JList<DataObject> listViewChat;
	private ChatListRenderer chatListRenderer;
	private DefaultListModel<DataObject> chatListModel;
	
	private JLabel labelWhom;
	private JTextArea taInput;
	
	private JPanel toWhomPanel;
	private JButton btnTheme;
	private JButton btnSave;
	private JButton btnSend;
	private JButton btnEmotion;
	
	private JButton btnSendDrawing;
	private JButton btnClearDrawing;
	private JButton btnChangeColor;
	private JPanel panelAll;
	private JScrollPane scrollOutput;
	private JPanel inputArea;
	private JPanel panelRight;
	private JScrollPane scrollUserList;
	private JPanel drawingArea;
	private JPanel panelFunction;
	private JPanel panelUserListAndDrawing;
	private JPanel drawingButtonArea;
	
	private DrawingPanel drawingPanel;
	
	private Vector<String> userVector;
	private JList<String> userListView;
	public static String username;
	private String toWhom;
	
	public static  Color colorText = Color.WHITE;
	public static Color colorBg = THEME_COLOR[0][0];
	public static  Color colorFg = THEME_COLOR[0][1];
	
	
	private Client(){
		userVector = new Vector<>();
	}
	
	//init the login ui
	public void initLogin(){
		JPanel panelAll = new JPanel();
		panelAll.setLayout(new GridLayout(3, 1));
		
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();
		
		p1.setBackground(colorBg);
		p2.setBackground(colorBg);
		p3.setBackground(colorBg);
		
		JLabel labelName = new JLabel("nickname");
		labelName.setForeground(colorText);
		final JTextField tfName = new JTextField(10);
		p2.add(labelName);
		p2.add(tfName);
		tfName.setBackground(colorFg);
		tfName.setForeground(colorText);
		
		JButton btnSetting = new JButton(new ImageIcon("images/setting.png"));
		btnSetting.setBorderPainted(false);
		btnSetting.setContentAreaFilled(false);
		p2.add(btnSetting);
		
		btnSetting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSettingDialog();
			}
		});
		
		final JButton btnLogin = new JButton("log in");
		btnLogin.setForeground(colorText);
		btnLogin.setBackground(colorFg);
		p3.add(btnLogin);
		JButton btnExit = new JButton("exit");
		btnExit.setForeground(colorText);
		btnExit.setBackground(colorFg);
		p3.add(btnExit);
		
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		panelAll.add(p1);
		panelAll.add(p2);
		panelAll.add(p3);
		
		frameLogin = new JFrame();
		frameLogin.setResizable(false);
		frameLogin.setTitle("welcome to chatroom");
		setFrameIcon(frameLogin);
		frameLogin.setContentPane(panelAll);
		frameLogin.setVisible(true);
		frameLogin.setSize(300, 200);
		frameLogin.setLocationRelativeTo(null);
		frameLogin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		btnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = tfName.getText().trim();
				username = name;
				if(name != null && !name.equals("")){
					tryToLogin(name);
				} else {
					JOptionPane.showMessageDialog(frameLogin,
						    "Please input a nickname.",
						    "error",
						    JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		
		tfName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					btnLogin.doClick();
				}
			}
		});
		
		frameLogin.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	//hide the login ui and show chatroom ui
	public void initUI(){
		panelAll = new JPanel();
		panelAll.setBackground(colorBg);
		panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panelAll.setLayout(new BorderLayout());
		
		JPanel panelLeft = new JPanel();
		panelLeft.setLayout(new BorderLayout());
		panelLeft.setPreferredSize(new Dimension(450, 550));
		
		listViewChat = new JList<>();
		listViewChat.setForeground(colorText);
		listViewChat.setBackground(colorFg);
		chatListRenderer = new ChatListRenderer();
		listViewChat.setCellRenderer(chatListRenderer);
		chatListModel = new DefaultListModel<>();
		listViewChat.setModel(chatListModel);
		
		listViewChat.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int index = listViewChat.getSelectedIndex();
					if (index < 0) {
						return;
					}
					DataObject item = chatListModel.getElementAt(index);
					if (item instanceof DrawingMsgObject) {
						final JDialog dialog = new JDialog(frameChat, "Drawing viewer", false);
						dialog.setResizable(false);
						DrawingPanel panel = new DrawingPanel(false);
						dialog.setPreferredSize(new Dimension(250, 250));
						dialog.setContentPane(panel);
						dialog.pack();
						dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						int x = frameChat.getLocationOnScreen().x + (frameChat.getWidth() - dialog.getWidth()) / 2;
						int y = frameChat.getLocationOnScreen().y + (frameChat.getHeight() - dialog.getHeight()) / 2;
						dialog.setLocation(x, y);
						dialog.setVisible(true);
						dialog.addWindowListener(new WindowAdapter() {
							@Override
							public void windowClosed(WindowEvent e) {
								listViewChat.clearSelection();
							};
						});
						
						panel.restoreDrawing(((DrawingMsgObject)item).getDrawingLines());
						
					}
				}
			}
		});
				
		scrollOutput = new JScrollPane(listViewChat);
		scrollOutput.setPreferredSize(new Dimension(0, 300));
		scrollOutput.setBackground(colorBg);
		TitledBorder outputTitleBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Chat content");
		outputTitleBorder.setTitleColor(colorText);
		scrollOutput.setBorder(outputTitleBorder);
		panelLeft.add(scrollOutput, BorderLayout.NORTH);
		
		//input area
		inputArea = new JPanel();
		inputArea.setLayout(new BorderLayout());
		
		toWhomPanel = new JPanel();
		toWhomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		toWhomPanel.setBackground(colorBg);
		
		JLabel labelTo = new JLabel("To:");
		labelTo.setForeground(colorText);
		toWhomPanel.add(labelTo);
		labelWhom = new JLabel("All the users (Select one user from user list for private chat)");
		labelWhom.setForeground(new Color(0xeeeeee));
		labelWhom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				labelWhom.setText("All the users");
				toWhom = null;
				userListView.clearSelection();
			}
		});
		toWhomPanel.add(labelWhom);
		
		
		inputArea.add(toWhomPanel, BorderLayout.NORTH);
		inputArea.setBackground(colorBg);
		
		taInput = new JTextArea(5, 30);
		taInput.requestFocus();
		taInput.setForeground(colorText);
		taInput.setBackground(colorFg);
		taInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER){
					sendMsgFromInputArea();
				}
			}
		});
		// 1102
		
		JScrollPane scrollInput = new JScrollPane(taInput);
		scrollInput.setPreferredSize(new Dimension(450, 0));
		inputArea.add(scrollInput, BorderLayout.CENTER);
		TitledBorder inputTitleBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Input your message");
		inputTitleBorder.setTitleColor(colorText);
		inputArea.setBorder(inputTitleBorder);
		
		panelLeft.add(inputArea, BorderLayout.CENTER);
		
		//save and send buttons
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new GridLayout(1, 3));
		
		btnEmotion = new JButton("Send emotion");
		btnEmotion.setForeground(colorText);
		btnEmotion.setBackground(colorFg);
		panelButtons.add(btnEmotion);
		
		btnSend = new JButton("Send (Ctrl+Enter)");
		btnSend.setForeground(colorText);
		btnSend.setBackground(colorFg);
		panelButtons.add(btnSend);
		
		panelLeft.add(panelButtons, BorderLayout.SOUTH);
		
		btnEmotion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showEmotionPanel();
			}
		});
		
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMsgFromInputArea();
			}
		});
		
		//right panel
		panelRight = new JPanel();
		panelRight.setLayout(new BorderLayout());
		panelRight.setBackground(colorBg);
		
		panelFunction = new JPanel();
		panelFunction.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelFunction.setBackground(colorBg);
		
		btnTheme = new JButton("Theme");
		btnTheme.setForeground(colorText);
		btnTheme.setBackground(colorFg);
		panelFunction.add(btnTheme);
		
		btnSave = new JButton("Save chat history");
		btnSave.setForeground(colorText);
		btnSave.setBackground(colorFg);
		panelFunction.add(btnSave);
		
		btnTheme.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showThemeChanger();
			}
		});
		
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						ArrayList<DataObject> historyList = Collections.list(chatListModel.elements());
						
						try {
							final File dir = new File("chatHistory/");
							dir.mkdirs();
							final File historyFile = new File("chatHistory/" + username);
							historyFile.createNewFile();
							FileOutputStream fos = new FileOutputStream(historyFile);
							ObjectOutputStream oos = new ObjectOutputStream(fos);
							oos.writeObject(historyList);
							
							oos.close();
							fos.close();
							
							final String absDir = historyFile.getAbsolutePath();
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									JOptionPane.showMessageDialog(frameChat, "Chat history saved successfully to\n\""
											+ absDir + "\".\n"
											+ "You have to login later with the same name to load chat history.", 
											"Yeah!", JOptionPane.INFORMATION_MESSAGE);
								}
							});
							
						} catch (Exception e) {
							e.printStackTrace();
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									JOptionPane.showMessageDialog(frameChat, "Chat history saving failed!", "Oops!", JOptionPane.ERROR_MESSAGE);
								}
							});
						} 
						
					}
				}).start();
			}
		});
		
		panelRight.add(panelFunction, BorderLayout.NORTH);
		
		panelUserListAndDrawing = new JPanel(new BorderLayout());
		
		DefaultListModel<String> listModel = new DefaultListModel<>();
		userListView = new JList<>(listModel);
		userListView.setForeground(colorText);
		userListView.setBackground(colorFg);
		userListView.setListData(userVector);
		
		userListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userListView.setLayoutOrientation(JList.VERTICAL);
		
		userListView.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()){
					int index = userListView.getSelectedIndex();
					if(index < 0) {
						return;
					}
					String wantToWhom = userVector.get(index);
					if(username.equals(wantToWhom)){
						JOptionPane.showMessageDialog(frameChat,
							    "Do not try to talk to yourself~",
							    "Oops!",
							    JOptionPane.PLAIN_MESSAGE);
						userListView.clearSelection();
						return;
					}
					toWhom = wantToWhom;
					labelWhom.setText(toWhom);
				}
			}
		});

		scrollUserList = new JScrollPane(userListView);
		scrollUserList.setPreferredSize(new Dimension(0, 200));
		scrollUserList.setBackground(colorBg);
		TitledBorder userlistTitleBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Users");
		userlistTitleBorder.setTitleColor(colorText);
		scrollUserList.setBorder(userlistTitleBorder);
		panelUserListAndDrawing.add(scrollUserList, BorderLayout.NORTH);
		
		//drawing area
		drawingArea = new JPanel();
		drawingArea.setBackground(colorBg);
		drawingArea.setLayout(new BorderLayout());
		drawingPanel = new DrawingPanel();
		drawingArea.add(drawingPanel, BorderLayout.CENTER);
		
		drawingButtonArea = new JPanel();
		drawingButtonArea.setLayout(new FlowLayout(FlowLayout.CENTER));
		drawingButtonArea.setBackground(colorBg);
		btnSendDrawing = new JButton("Send");
		btnSendDrawing.setBackground(colorFg);
		btnSendDrawing.setForeground(colorText);
		btnClearDrawing = new JButton("Clear");
		btnClearDrawing.setBackground(colorFg);
		btnClearDrawing.setForeground(colorText);
		btnChangeColor = new JButton("Color");
		btnChangeColor.setBackground(colorFg);
		btnChangeColor.setForeground(colorText);
		
		btnChangeColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(frameChat, "Choose a color", drawingPanel.getPenColor());
				if(c != null){
					drawingPanel.setPenColor(c);
				}
			}
		});
		
		btnClearDrawing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingPanel.repaint();
				drawingPanel.clearRecords();
			}
		});
		
		btnSendDrawing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DrawingMsgObject drawingMsg = new DrawingMsgObject();
				drawingMsg.setDrawingLines(drawingPanel.getDrawingLines());
				
				if(toWhom != null && !toWhom.equals("")){
					drawingMsg.setToWhom(toWhom);
				}
				clientThread.send(drawingMsg);
				drawingPanel.repaint();
				drawingPanel.clearRecords();
			}
		});
		
		drawingButtonArea.add(btnChangeColor);
		drawingButtonArea.add(btnClearDrawing);
		drawingButtonArea.add(btnSendDrawing);
		
		drawingArea.add(drawingButtonArea, BorderLayout.SOUTH);
		TitledBorder drawingTitleBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Drawing");
		drawingTitleBorder.setTitleColor(colorText);
		drawingArea.setBorder(drawingTitleBorder);
		
		panelUserListAndDrawing.add(drawingArea, BorderLayout.CENTER);
		
		panelRight.add(panelUserListAndDrawing, BorderLayout.CENTER);
		
		panelAll.add(panelLeft, BorderLayout.WEST);
		panelAll.add(panelRight, BorderLayout.CENTER);
		
		//frame
		frameChat = new JFrame();
		setFrameIcon(frameChat);
		frameChat.setResizable(false);
		frameChat.setTitle("chatroom : " + username);
		frameChat.setContentPane(panelAll);
		frameChat.setVisible(true);
		frameChat.setSize(700, 550);
		frameChat.setLocationRelativeTo(null);
		frameChat.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frameChat.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				RegMsgObject exitMsg = new RegMsgObject();
				exitMsg.setMsgType(RegMsgObject.MSGTYPE_EXIT);
				clientThread.send(exitMsg);
			}
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		
		taInput.requestFocus();
		
		loadChatHistory();
	}
	
	
	private void showSettingDialog(){
		final JDialog dialog = new JDialog(frameLogin, "Settings", true);
		dialog.setResizable(false);
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		Label labelIP = new Label("Server IP");
		labelIP.setPreferredSize(new Dimension(52, 22));
		panel.add(labelIP); 
		
		final JTextField tfIP = new JTextField(SERVER_IP);
		tfIP.setPreferredSize(new Dimension(110, tfIP.getPreferredSize().height));
		
		tfIP.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				tfIP.selectAll();
			}
		});
		panel.add(tfIP);
		
		Label labelPort = new Label("Port");
		labelPort.setPreferredSize(new Dimension(52, 22));
		panel.add(labelPort); 
		
		final JTextField tfPort = new JTextField("" + SERVER_PORT);
		tfPort.setPreferredSize(new Dimension(110, tfPort.getPreferredSize().height));
		tfPort.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				tfPort.selectAll();
			}
		});
		panel.add(tfPort);
		
		JButton btnOK = new JButton("OK");
		panel.add(btnOK);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String port = tfPort.getText().trim();
				SERVER_PORT = Integer.parseInt(port);
				
				SERVER_IP = tfIP.getText().trim();
				dialog.dispose();
			}
		});
		
		tfIP.requestFocus();
		dialog.setPreferredSize(new Dimension(180, 120));
		dialog.setContentPane(panel);
		dialog.pack();
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(frameLogin);
		dialog.setVisible(true);
	
	}
	
	private void showThemeChanger() {
		final JDialog dialog = new JDialog(frameChat, "Theme", true);
		dialog.setResizable(false);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, THEME_NUM));
		
		for(int i = 0; i < THEME_NUM; i++){
			JButton btnThemeColor = new JButton();
			btnThemeColor.setBackground(THEME_COLOR[i][1]);
			panel.add(btnThemeColor);
			final int index = i;
			btnThemeColor.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					changeComponentsTheme(THEME_COLOR[index][0], THEME_COLOR[index][1]);
				}
			});
		}
		
		dialog.setPreferredSize(new Dimension(250, 80));
		dialog.setContentPane(panel);
		dialog.pack();
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		int x = btnTheme.getLocationOnScreen().x - (dialog.getWidth() - btnTheme.getWidth()) / 2;
		int y = btnTheme.getLocationOnScreen().y + btnTheme.getHeight();
		dialog.setLocation(x, y);
		dialog.setVisible(true);
	}
	
	private void showEmotionPanel(){
		final JDialog dialog = new JDialog(frameChat, "Emotion", true);
		dialog.setResizable(false);
		JPanel panel = new JPanel(new GridLayout(EMO_ROW, EMO_COL));
		panel.setPreferredSize(new Dimension(EMO_COL * EMO_HEIGHT, EMO_ROW * EMO_WIDTH));
		
		try {
			BufferedImage bufferedImage = ImageIO.read(new File("images/emotion.png"));
			BufferedImage tmp;
			ImageIcon ii;
			JButton jb;
			for(int r = 0; r < EMO_ROW; r++){
				for(int c = 0; c < EMO_COL; c++){
					tmp = bufferedImage.getSubimage(0 + c * EMO_WIDTH, 0 + r * EMO_HEIGHT, EMO_WIDTH, EMO_HEIGHT);
					ii = new ImageIcon(tmp);
					jb = new JButton(ii);
					jb.setPreferredSize(new Dimension(62, 62));
					panel.add(jb);
					
					final int fRow = r, fCol = c;
					jb.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dialog.dispose();
							EmotionMsgObject msg = new EmotionMsgObject();
							if(toWhom != null && !toWhom.equals("")){
								msg.setToWhom(toWhom);
							}
							msg.setMessage(fRow + "_" + fCol);
							clientThread.send(msg);
						}
					});
				}
			}
			bufferedImage = null;
			tmp = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		dialog.setContentPane(panel);
		dialog.pack();
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(btnEmotion);
		dialog.setVisible(true);
	}
	
	private void changeComponentsTheme(Color colorBg, Color colorFg){
		Client.colorBg = colorBg;
		Client.colorFg = colorFg;
		listViewChat.setBackground(colorFg);
		toWhomPanel.setBackground(colorBg);
		taInput.setBackground(colorFg);
		btnTheme.setBackground(colorFg);
		btnSave.setBackground(colorFg);
		btnSend.setBackground(colorFg);
		btnEmotion.setBackground(colorFg);
		userListView.setBackground(colorFg);
		btnSendDrawing.setBackground(colorFg);
		btnClearDrawing.setBackground(colorFg);
		btnChangeColor.setBackground(colorFg);
		
		panelAll.setBackground(colorBg);
		scrollOutput.setBackground(colorBg);
		inputArea.setBackground(colorBg);
		panelRight.setBackground(colorBg);
		scrollUserList.setBackground(colorBg);
		drawingArea.setBackground(colorBg);
		
		panelFunction.setBackground(colorBg);
		drawingButtonArea.setBackground(colorBg);
		
	}

	private void tryToLogin(String name){
		clientThread = new ClientThread(name);
		clientThread.start();
	}
	
	private void sendMsgFromInputArea(){
		String text = taInput.getText().trim();
		if(text == null || text.equals("")){
			return;
		}
		MsgObject msgObject = new MsgObject();
		msgObject.setMessage(text);
		if(toWhom != null && !toWhom.equals("")){
			msgObject.setToWhom(toWhom);
		}
		
		clientThread.send(msgObject);
		taInput.setText("");
		taInput.requestFocus();
	}
	
	private void setFrameIcon(JFrame frame){
		ImageIcon ii = new ImageIcon("images/icon.png");
		frame.setIconImage(ii.getImage());
	}
	
	private void loadChatHistory(){
		final File historyFile = new File("chatHistory/" + username);
		if(historyFile.exists()){
			new Thread(new Runnable() {
				@SuppressWarnings("unchecked")
				@Override
				public void run() {
					try {
						FileInputStream fos = new FileInputStream(historyFile);
						ObjectInputStream oos = new ObjectInputStream(fos);
						ArrayList<DataObject> history = (ArrayList<DataObject>) oos.readObject();
						synchronized (chatListModel) {
							for(DataObject data : history){
								chatListModel.addElement(data);
								
							}
						}
						oos.close();
						
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(frameChat, "Chat history loaded successfully!", 
										"Yeah!", JOptionPane.INFORMATION_MESSAGE);
							}
						});
						
					} catch (Exception e) {
						e.printStackTrace();
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(frameChat, "Chat history loading failed!", 
										"Oops!", JOptionPane.ERROR_MESSAGE);
							}
						});
					} 
				}
			}).start();
			
		}
		
	}
	
	
	
	
	private class ClientThread extends Thread {
		
		private boolean clientOn = true;
		private Socket socket;
		private String name;
		private ObjectOutputStream oos = null;
		private ObjectInputStream ois = null;
		
		ClientThread(String name){
			this.name = name;
		}
		@Override
		public void run() {
			try {
				socket = new Socket(SERVER_IP, SERVER_PORT);
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
				RegMsgObject regMsg = new RegMsgObject();
				regMsg.setMsgType(RegMsgObject.MSGTYPE_REGISTER);
				regMsg.setMessage(name);
				oos.writeObject(regMsg);
				while(clientOn){
					System.out.println("client waiting to read object...");
					Object obj = ois.readObject();
					if(obj instanceof RegMsgObject){
						RegMsgObject respond = (RegMsgObject) obj;
						if(respond.getMsgType() == RegMsgObject.MSGTYPE_USERNAME_OCCUPIED){
							clientOn = false;
							JOptionPane.showMessageDialog(frameLogin,
								    "Sorry, nickname [" + name + "] has already been used.",
								    "error",
								    JOptionPane.ERROR_MESSAGE);
							socket.close();
						} else if (respond.getMsgType() == RegMsgObject.MSGTYPE_REGISTER_SUCCEED){
							frameLogin.setVisible(false);
							initUI();
						}
						
					} else if (obj instanceof ListMsgObject){
						ListMsgObject listMsg = (ListMsgObject) obj;
						userVector = new Vector<>(listMsg.getUserList());
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								userListView.setListData(userVector);
							}
						});
					} else if(obj instanceof DrawingMsgObject) {
						final DrawingMsgObject msg = (DrawingMsgObject) obj;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								synchronized (chatListModel) {
									chatListModel.addElement(msg);
									listViewChat.ensureIndexIsVisible(chatListModel.getSize() - 1);
								}
								
							}
						});
					} else if (obj instanceof MsgObject) {
						final MsgObject msg = (MsgObject) obj;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								synchronized (chatListModel) {
									chatListModel.addElement(msg);
									listViewChat.ensureIndexIsVisible(chatListModel.getSize() - 1);
								}

							}
						});

					} else if (obj instanceof SysMsgObject) {
						final SysMsgObject sysmsg = (SysMsgObject) obj;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								synchronized (chatListModel) {
									chatListModel.addElement(sysmsg);
									listViewChat.ensureIndexIsVisible(chatListModel.getSize() - 1);
								}
								
							}
						});
					}
					
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (ConnectException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frameLogin,
					    "Fail to connect to the server",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if(socket != null){
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		public void send(DataObject msg) {
			try {
				oos.writeObject(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static Client getInstance(){
		Client c = new Client();
		return c;
	}
	
	public static void main(String[] args) {
		Client.getInstance().initLogin();
	}
	
}
