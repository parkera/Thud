//
//	File:	AboutBox.java
//

package btthud.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AboutBox extends JFrame
                      implements ActionListener
{
    protected JButton 		okButton, treeButton;
    ImageIcon				treeImage;

    public AboutBox() {
        super();

        treeImage = new ImageIcon(ClassLoader.getSystemClassLoader().getResource("img/trees.png"));
        
        this.getContentPane().setLayout(new BorderLayout(5, 5));
        this.setFont(new Font ("SansSerif", Font.BOLD, 14));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        treeButton = new JButton(treeImage);
        //treeButton.setEnabled(false);
        treeButton.setPreferredSize(new Dimension(128, 128));
        topPanel.add(treeButton);
        topPanel.add(new JLabel("Thump 1.1b, (c) 2003 Anthony Parker <asp@mac.com>"));

        this.getContentPane().add(topPanel, BorderLayout.NORTH);
		
        okButton = new JButton("OK");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.add (okButton);
        okButton.addActionListener(this);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        this.pack();

        this.setLocation(128, 128);
    }
	
    public void actionPerformed(ActionEvent newEvent) {
        setVisible(false);
    }	
	
}