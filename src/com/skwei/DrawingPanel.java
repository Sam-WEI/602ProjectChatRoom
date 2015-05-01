package com.skwei;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JPanel;

public class DrawingPanel extends JPanel {
	private static final long serialVersionUID = -3294425219045375579L;

	protected int lastX = 0, lastY = 0;
	private Graphics graphics;
	private Color penColor = Color.BLACK;
	
	private ArrayList<DrawingLine> drawingLines;
	
	public DrawingPanel() {
		this(true);
	}
	
	public DrawingPanel(boolean canBeDrawn){
		super();
		setBackground(Color.white);
		if(canBeDrawn){
			addMouseListener(new PositionRecorder());
			addMouseMotionListener(new LineDrawer());
		}
		drawingLines = new ArrayList<>();
	}
	
	private class PositionRecorder extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent event) {
			record(event.getX(), event.getY());
		}
	}

	private class LineDrawer extends MouseMotionAdapter {
		@Override
		public void mouseDragged(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();
			graphics = getGraphics();
			graphics.setColor(penColor);
			graphics.drawLine(lastX, lastY, x, y);
			drawingLines.add(new DrawingLine(penColor, lastX, lastY, x, y));
			record(x, y);
		}
	}

	

	public Color getPenColor() {
		return penColor;
	}

	public void setPenColor(Color penColor) {
		this.penColor = penColor;
	}
	
	public void restoreDrawing(final ArrayList<DrawingLine> drawingLines) {
		this.drawingLines = drawingLines;
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for(DrawingLine obj : drawingLines){
					
					graphics = getGraphics();
					graphics.setColor(obj.getPenColor());
					
					graphics.drawLine(obj.getStartX(), obj.getStartY(), obj.getEndX(), obj.getEndY());
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
	}

	public ArrayList<DrawingLine> getDrawingLines(){
		return drawingLines;
	}
	
	public void clearRecords(){
		drawingLines.clear();
		drawingLines = new ArrayList<>();
	}

	protected void record(int x, int y) {
		lastX = x;
		lastY = y;
	}

	static class DrawingLine implements Serializable{
		private static final long serialVersionUID = -2962661302715270070L;
		private Color penColor;
		private int startX;
		private int startY;
		private int endX;
		private int endY;
		
		public DrawingLine(){}
		
		public DrawingLine(Color penColor, int startX, int startY, int endX, int endY) {
			this.penColor = penColor;
			this.startX = startX;
			this.startY = startY;
			this.endX = endX;
			this.endY = endY;
		}


		public Color getPenColor() {
			return penColor;
		}
		public void setPenColor(Color penColor) {
			this.penColor = penColor;
		}
		public int getStartX() {
			return startX;
		}
		public void setStartX(int startX) {
			this.startX = startX;
		}
		public int getStartY() {
			return startY;
		}
		public void setStartY(int startY) {
			this.startY = startY;
		}
		public int getEndX() {
			return endX;
		}
		public void setEndX(int endX) {
			this.endX = endX;
		}
		public int getEndY() {
			return endY;
		}
		public void setEndY(int endY) {
			this.endY = endY;
		}
		
		
	}
	
}
