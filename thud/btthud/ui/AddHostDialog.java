/*
 * AddHostDialog.java
 *
 * Created on September 25, 2002, 12:57 PM
 */

/**
 *
 * @author  asp
 */

package btthud.ui;

import btthud.data.*;
import btthud.util.*;
import java.awt.*;
import javax.swing.*;

public class AddHostDialog extends javax.swing.JDialog {

    private javax.swing.JLabel hostAddressLabel;
    private javax.swing.JTextField hostAddress;
    private javax.swing.JLabel hostPortLabel;
    private javax.swing.JTextField hostPort;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton saveButton;

    private Thud		thudClass = null;
    
    /** Creates new form AddHostDialog */
    public AddHostDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        thudClass = (Thud) parent;
        
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        hostAddressLabel = new javax.swing.JLabel();
        hostAddress = new javax.swing.JTextField();
        hostPortLabel = new javax.swing.JLabel();
        hostPort = new javax.swing.JTextField();
        cancelButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        
        getContentPane().setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        
        hostAddressLabel.setText("Host Address: ");
        hostAddressLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 0.5;
        gridBagConstraints1.weighty = 0.5;
        getContentPane().add(hostAddressLabel, gridBagConstraints1);
        
        hostAddress.setToolTipText("The address of the game that you wish to add");

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(hostAddress, gridBagConstraints1);
        
        hostPortLabel.setText("Host Port: ");
        hostPortLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.weightx = 0.5;
        gridBagConstraints1.weighty = 0.5;
        getContentPane().add(hostPortLabel, gridBagConstraints1);
        
        hostPort.setToolTipText("The port of the game that you wish to add");

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(hostPort, gridBagConstraints1);
        
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 3;
        getContentPane().add(cancelButton, gridBagConstraints1);
        
        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        getContentPane().add(saveButton, gridBagConstraints1);
        
        pack();
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // They've added a host
        String			newHost = hostAddress.getText();
        String			newPortString = hostPort.getText();
        int				newPort = 23;

        try {
            newPort = Integer.parseInt(newPortString);
        } catch (NumberFormatException e) {
            // Easy way out, just set the port to 23 and don't tell the user. :P
            newPort = 23;
        }

        // Add the host
        thudClass.prefs.addHost(newHost, newPort);
        // Redraw our menus
        thudClass.addMenus();
        
        // Close
        closeDialog(null);
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // They've canceled
        closeDialog(null);
    }

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
    }

}
