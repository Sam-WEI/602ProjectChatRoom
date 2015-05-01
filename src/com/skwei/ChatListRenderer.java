package com.skwei;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;

public class ChatListRenderer implements ListCellRenderer<DataObject> {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
	
	private Cell cell = new Cell();
	
	private ImageIcon[][] emotions;

	
	@Override
	public Component getListCellRendererComponent(JList<? extends DataObject> list, DataObject value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof DrawingMsgObject) {
			DrawingMsgObject msg = (DrawingMsgObject) value;
			
			String result = getFormattedPrefix(msg).append("I drew a picture. Click to open.").toString();
			cell.textPane.setText(result);
			
		} else if (value instanceof EmotionMsgObject) {
			EmotionMsgObject msg = (EmotionMsgObject) value;
			
			cell.textPane.setText(getFormattedPrefix(msg).toString());
			
			String emoStr = msg.getMessage();
			String[] pos = emoStr.split("_");
			if(pos.length == 2){
				int r = Integer.parseInt(pos[0]);
				int c = Integer.parseInt(pos[1]);
				
				if(emotions == null) {
					initEmotion();
				}
				cell.textPane.insertIcon(emotions[r][c]);
			}
			
		} else if (value instanceof MsgObject) {
			MsgObject msg = (MsgObject) value;
			final String text = msg.getMessage();
			String result = getFormattedPrefix(msg).append(text).toString();
			cell.textPane.setText(result);

		} else if (value instanceof SysMsgObject) {
			final SysMsgObject sysmsg = (SysMsgObject) value;
			cell.textPane.setText(sysmsg.getMessage());
		}
		cell.textPane.setBackground(Client.colorFg);
		
		return cell;
	}
	
	private void initEmotion(){
		final int ROW = Client.EMO_ROW, COL = Client.EMO_COL, WIDTH = Client.EMO_WIDTH, HEIGHT = Client.EMO_HEIGHT;
		emotions = new ImageIcon[ROW][COL];
		try {
			BufferedImage bufferedImage = ImageIO.read(new File("images/emotion.png"));
			BufferedImage tmp;
			for(int r = 0; r < ROW; r++){
				for(int c = 0; c < COL; c++){
					tmp = bufferedImage.getSubimage(0 + c * WIDTH, 0 + r * HEIGHT, WIDTH, HEIGHT);
					emotions[r][c] = new ImageIcon(tmp);
				}
			}
			bufferedImage = null;
			tmp = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private StringBuilder getFormattedPrefix(MsgObject msg){
		final String time = dateFormat.format(new Date(msg.getTimeStamp()));
		final String toWhom = msg.getToWhom();
		final String fromWhom = msg.getFromWhom();

		StringBuilder result = new StringBuilder();

		result.append("[" + fromWhom + "]");
		result.append(" " + time);
		if (toWhom != null && !toWhom.equals("")) {
			if (Client.username.equals(toWhom)) {
				result.append(" (privately to you)");
			} else if (Client.username.equals(fromWhom)) {
				result.append(" (privately to " + toWhom + ")");
			}
		}
		result.append(" : ");
		
		return result;
	}
	
	private class Cell extends JPanel{
		private static final long serialVersionUID = -5774545132184453528L;
		JTextPane textPane = new JTextPane();
		public Cell(){
			super();
			setBackground(Client.colorFg);
			setLayout(new BorderLayout());
			add(textPane, BorderLayout.CENTER);
			textPane.setForeground(Client.colorText);
			textPane.setBackground(Client.colorFg);
		}
	}
}
