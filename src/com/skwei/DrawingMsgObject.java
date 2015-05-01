package com.skwei;

import java.io.Serializable;
import java.util.ArrayList;

import com.skwei.DrawingPanel.DrawingLine;

public class DrawingMsgObject extends MsgObject implements Serializable{
	private ArrayList<DrawingLine> drawingLines;

	public DrawingMsgObject(){}
	
	public DrawingMsgObject(ArrayList<DrawingLine> drawingLines) {
		this.drawingLines = drawingLines;
	}

	public ArrayList<DrawingLine> getDrawingLines() {
		return drawingLines;
	}

	public void setDrawingLines(ArrayList<DrawingLine> drawingLines) {
		this.drawingLines = drawingLines;
	}
	
}
