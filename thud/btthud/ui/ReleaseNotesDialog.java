/*
 * ReleaseNotesDialog.java
 *
 * Created on October 18, 2002, 12:39 AM
 */

/**
 *
 * @author  asp
 */

package btthud.ui;

import javax.swing.*;
import java.awt.*;

public class ReleaseNotesDialog extends javax.swing.JDialog {

    JButton 		okButton;
    JTextArea		notesTextArea;
        
    public ReleaseNotesDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
    }

    private void initComponents()
    {
        okButton = new javax.swing.JButton();
        notesTextArea = new JTextArea("Test notes");
        notesTextArea.setLineWrap(true);
        notesTextArea.setRows(20);
        notesTextArea.setColumns(80);
        notesTextArea.setEditable(false);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        
        okButton.setText("OK");
        getContentPane().add(okButton, java.awt.BorderLayout.SOUTH);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               okButtonActionPerformed(evt);
            }
        });
        
        getContentPane().add(notesTextArea, java.awt.BorderLayout.CENTER);
        
        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        closeDialog(null);
    }
    
    private void closeDialog(java.awt.event.WindowEvent evt)
    {
        setVisible(false);
        dispose();
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new ReleaseNotesDialog(new javax.swing.JFrame(), true).show();
    }
}
