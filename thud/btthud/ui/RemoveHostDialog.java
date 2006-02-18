//
//  RemoveHostDialog.java
//  Thud
//
//  Created by asp on September 25, 2002, 1:05 PM
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package btthud.ui;

import btthud.data.*;
import btthud.util.*;
import java.awt.*;
import javax.swing.*;

public class RemoveHostDialog extends javax.swing.JDialog {

    private javax.swing.JLabel whichHostLabel;
    private javax.swing.JComboBox whichHost;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton removeButton;

    Thud	thudClass = null;
    
    /** Creates new form RemoveHostDialog */
    public RemoveHostDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        thudClass = (Thud) parent;

        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        whichHostLabel = new javax.swing.JLabel();
        whichHost = new javax.swing.JComboBox();
        cancelButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        
        getContentPane().setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        
        whichHostLabel.setText("Remove this Host: ");
        whichHostLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.weightx = 0.25;
        gridBagConstraints1.weighty = 0.25;
        getContentPane().add(whichHostLabel, gridBagConstraints1);

        for (int i = 0; i < thudClass.prefs.hosts.size(); i++)
        {
            whichHost.addItem((MUHost) thudClass.prefs.hosts.get(i));
        }
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.weightx = 1.0;
        getContentPane().add(whichHost, gridBagConstraints1);
        
        cancelButton.setText("Cancel");
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.weighty = 1.0;
        getContentPane().add(cancelButton, gridBagConstraints1);

        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        
        removeButton.setText("Remove");
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        getContentPane().add(removeButton, gridBagConstraints1);

        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        
        pack();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // They canceled. Easy to handle:
        closeDialog(null);
    }

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // They want to remove a selected item
        MUHost			removeHost = (MUHost) whichHost.getSelectedItem();
        thudClass.prefs.removeHost(removeHost);

        // Redraw our menus
        thudClass.addMenus();
        
        closeDialog(null);
    }

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
    }

}
