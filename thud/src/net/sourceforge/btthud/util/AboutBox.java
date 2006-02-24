//
//  AboutBox.java
//  Thud
//
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AboutBox extends JFrame
                      implements ActionListener
{
    protected JButton okButton;
    protected JLabel aboutText;

    public AboutBox() {
	super();
        this.getContentPane().setLayout(new BorderLayout(15, 15));
        this.setFont(new Font ("SansSerif", Font.BOLD, 14));

        aboutText = new JLabel ("by Tony @ 3030MUX - asp@mac.com");
        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        textPanel.add(aboutText);
        this.getContentPane().add (textPanel, BorderLayout.NORTH);
		
        okButton = new JButton("OK");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.add (okButton);
        okButton.addActionListener(this);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        this.pack();
    }
	
    public void actionPerformed(ActionEvent newEvent) {
        setVisible(false);
    }	
	
}