//
//  ThudActions.java
//  Thud
//
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

class ThudKeyAction extends AbstractAction {
	JTextPane textPane;
	
	public ThudKeyAction() {
		super();
	}
	
	public ThudKeyAction(JTextPane textPane_arg) {
		super();
		this.textPane = textPane_arg;
	}
	public void actionPerformed(ActionEvent e) {}
}

class PageUpAction extends ThudKeyAction {
	
	public PageUpAction(JTextPane textPane_arg) {
		this.textPane = textPane_arg;
	}
	
    public void actionPerformed(ActionEvent e) {
    	try {    		
        	JScrollPane scrollPane = (JScrollPane) textPane.getParent().getParent();
        	int increment = scrollPane.getVerticalScrollBar().getBlockIncrement(0);
        	Rectangle vCaretPos = textPane.modelToView(textPane.getCaretPosition());
        	Point newLoc = new Point (vCaretPos.x, vCaretPos.y - increment);
        	textPane.setCaretPosition (textPane.viewToModel(newLoc));           	                                
    	} catch (Exception ex) {
    		System.out.println(ex);
    	}
    }
}

class PageDownAction extends ThudKeyAction {
	
	public PageDownAction(JTextPane textPane_arg) {
		this.textPane = textPane_arg;
	}
	
    public void actionPerformed(ActionEvent e) {
    	try {
        	JScrollPane scrollPane = (JScrollPane) textPane.getParent().getParent();
        	int increment = scrollPane.getVerticalScrollBar().getBlockIncrement(0);
        	Rectangle vCaretPos = textPane.modelToView(textPane.getCaretPosition());
        	Point newLoc = new Point (vCaretPos.x, vCaretPos.y + increment);
        	textPane.setCaretPosition (textPane.viewToModel(newLoc));           	                                
    	} catch (Exception ex) {
    		System.out.println(ex);
    	}
    }
}

class HomeAction extends ThudKeyAction {
	
	public HomeAction(JTextPane textPane_arg) {
		this.textPane = textPane_arg;
	}
	
    public void actionPerformed(ActionEvent e) {
    	try {
        	textPane.setCaretPosition (0);           	                                
    	} catch (Exception ex) {
    		System.out.println(ex);
    	}
    }
}

class EndAction extends ThudKeyAction {
		
	public EndAction(JTextPane textPane_arg) {
		this.textPane = textPane_arg;		
	}
	
    public void actionPerformed(ActionEvent e) {
    	try {    		
        	textPane.setCaretPosition (this.textPane.getDocument().getLength());           	                                
    	} catch (Exception ex) {
    		System.out.println(ex);
    	}
    }
}

class NumpadAction extends ThudKeyAction {
	int key = 0; // which key are we concerned about?
	JTextField textField;
	Thud thud;
	public NumpadAction(JTextField textField_arg, int key_arg, Thud thud_arg) {
		this.textField = textField_arg;
		this.key = key_arg;
		this.thud = thud_arg;
	}
	
    public void actionPerformed(ActionEvent e) {
    	try {
    		int direction = 0;
    		switch(key) {
    			case 1: direction = 240; break;
    			case 2: direction = 180; break;
    			case 3: direction = 120; break;
    			case 4: direction = 270; break;
    			case 6: direction = 90; break;
    			case 7: direction = 300; break;
    			case 8: direction = 0; break;
    			case 9: direction = 60; break;
    		}
    		this.thud.conn.sendCommand("heading " + direction);
    	} catch (Exception ex) {
    		System.out.println(ex);
    	}
    }
}


