//
//  MUStatus.java
//  Thud
//
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package btthud.ui;

import btthud.data.*;
import btthud.engine.*;
import btthud.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;

import java.util.*;
import java.text.*;

/**
 * Implements a status report window that displays heading, speed, heat, and weapon information
 * very similar to the MUX's 'status'.
 * @author tkrajcar
 */
public class MUStatus extends JFrame
                           implements Runnable,
                                      ActionListener
{
    MUConnection		conn;
    MUData				data;
    MUPrefs				prefs;

    Font				mFont;
    
    JTextPane			statusPane;
    Thread				thread = null;
    SimpleAttributeSet conRegular, conIrregular;
    ArrayList			elements;
    
    boolean				go = true;

    
    public MUStatus(MUConnection conn, MUData data, MUPrefs prefs)
    {
        super("Status Report");
        
        this.data = data;
        this.conn = conn;
        this.prefs = prefs;

        mFont = new Font("Monospaced", Font.PLAIN, prefs.contactFontSize);
        
        elements = new ArrayList();
        BulkStyledDocument	bsd = new BulkStyledDocument(prefs.contactFontSize, 1000, mFont);        // Yes, max of 1000 contacts. So sue me.
        statusPane = new JTextPane(bsd);
        statusPane.setBackground(Color.black);
        statusPane.setEditable(false);
        statusPane.setDoubleBuffered(true);

        statusPane.setFont(mFont);
 
        initAttributeSets();
        JScrollPane scrollPane = new JScrollPane(statusPane,
                                                 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setDoubleBuffered(true);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(scrollPane);
        contentPane.setDoubleBuffered(true);
        
        setContentPane(contentPane);

        setSize(prefs.statusSizeX, prefs.statusSizeY);
        setLocation(prefs.statusLoc);
        // Show the window now

        this.show();
        
        start();
    }


    
    public void newPreferences(MUPrefs prefs)
    {
        this.prefs = prefs;
        mFont = new Font("Monospaced", Font.PLAIN, prefs.contactFontSize);
        statusPane.setFont(mFont);

    }
    
    // --------------------
    public void start()
    {
        if (thread == null)
        {
            thread = new Thread(this, "MUStatusReport");
            thread.start();
        }
    }

    protected void initAttributeSets()
    {
        conRegular = new SimpleAttributeSet();
        StyleConstants.setFontSize(conRegular, prefs.contactFontSize);
        StyleConstants.setForeground(conRegular, Color.white);

        conIrregular = new SimpleAttributeSet();
        StyleConstants.setFontSize(conIrregular, prefs.contactFontSize);
        StyleConstants.setForeground(conIrregular, Color.white);
        StyleConstants.setBold(conIrregular,true);
        
    }
    
    public void run()
    {
        MUMyInfo				mydata;
        BulkStyledDocument 		doc = (BulkStyledDocument) statusPane.getDocument();                
        
        while (go)
        {
            try
            {
                if (data.hudRunning)
                {
                	elements.clear();
                    mydata = this.data.myUnit;
                                                          
                    String s = "";
                    
                    // Move/heat block
                    s = mydata.leftJust( 
                    		mydata.name +
                        	" (" +
                        	mydata.ref +
                        	") [" +
                        	mydata.id +
                        	"]",44,false) +
                        "Heat Prod: " + 
                    	mydata.rightJust(String.valueOf(mydata.heat),3,false) +
                    	" deg C.";
                    addString(s, conRegular);
                    addBlankLine();
                    
                    NumberFormat speedFormatter = new DecimalFormat("##0.0");
                                      
                    s = "Speed:      " + 
                    	mydata.rightJust(speedFormatter.format(mydata.speed),5,false) +
                    	" KPH  Heading: " +
                    	mydata.rightJust(String.valueOf(mydata.heading),5,false)
                    	+ " deg  Heat Sinks: " +
                    	mydata.rightJust(String.valueOf(mydata.heatSinks),3,false);
                    addString(s, conRegular);
                    addBlankLine();
                    
                    s = "Des. Speed: " +
                    	mydata.rightJust(speedFormatter.format(mydata.desiredSpeed),5,false) +
                    	" KPH  Des. Hdg:  " +
                    	mydata.rightJust(String.valueOf(mydata.desiredHeading),3,false) +
                    	" deg  Heat Dissp: " +
                    	mydata.rightJust(String.valueOf(mydata.heatDissipation),3,false) +
                    	" deg C.";
                    addString(s, conRegular);
                    addBlankLine();
                                	
                    s = "------- Weapon ------- [##] Loc - Status || --- Ammo Type --- Rds";
                    addString(s, conRegular);
                    addBlankLine();
                    
                   	MUUnitWeapon weapons[] = mydata.unitWeapons;
                   	MUUnitAmmo ammo[] = mydata.unitAmmo;
                   	
                    for(int i = 0; i < weapons.length; i++) {
                    	if (weapons[i] != null) {
	                    	MUUnitWeapon weapon = weapons[i];
	                    	MUWeapon weapontype = mydata.getWeapon(weapon.typeNumber);

	                    	String weapname = weapontype.name;

	                    	weapname = weapname.replaceAll("IS\\.","");
	                    	weapname = weapname.replaceAll("Clan\\.","");
	                    	
	                    	
	                    	s = " " + 
	                    		mydata.leftJust(weapname,22,false) + 
	                    		"[" + 
	                    		mydata.rightJust(String.valueOf(weapon.number),2,false) + 
	                    		"] " + 
	                    		mydata.rightJust(weapon.loc,3,false) + 	                    		 
	                    		"   ";
	                    	addString(s, conRegular);
	                    	
                    		if (weapon.status.equals("R") || weapon.status.equals("0")) {
                    			StyleConstants.setForeground(conIrregular, Color.green);
                    			addString(" Ready", conIrregular);                    			                    	
                    		} else if(weapon.status.equals("*")) {
                    			StyleConstants.setForeground(conIrregular,new Color(128,128,128));
                    			addString(" *****", conIrregular);
                    		}
                    		else
                    		{
                    			addString(mydata.rightJust(weapon.status,6,false), conRegular);                    			                    		
                    		}                     		

                    		s = " || ";
                    		addString(s, conRegular);	                                            			
                    	} else {
                    		addString("                               ", conRegular);                    		
                    	}

                    	/* end of weapon bar - now do ammo */
                    	if(ammo[i] != null) {
                    		MUUnitAmmo thisAmmo = ammo[i];
                    		MUWeapon thisWeapon = mydata.getWeapon(thisAmmo.weaponTypeNumber);
	                    	String weapname = thisWeapon.name;

	                    	String mode = thisAmmo.ammoMode;	                    	
	                    	if(mode.equals("-")) 
	                    		mode = " ";
	                    	
	                    	weapname = weapname.replaceAll("IS\\.","");
	                    	weapname = weapname.replaceAll("Clan\\.","");
	                    		
                    		s = " " +
                    			mydata.leftJust(weapname + " " + mode,17,false);
                    		addString(s, conRegular);
                    		
                    		s =	mydata.rightJust(String.valueOf(thisAmmo.roundsRemaining),3,false);
                    		StyleConstants.setForeground(conIrregular, MUUnitInfo.colorForPercent(AmmoPercent(thisAmmo)));
                    		addString(s, conIrregular);                    		                    
                    	}
                       
                    	/* end of ammo bar - blank line */
                    	addBlankLine();
                    }
                    	
                }
                
            	doc.clearAndInsertParsedString(elements);
                // Don't scroll
                // contactPane.setCaretPosition(doc.getLength());
                
                // This should probably sleep until notified or something
                Thread.sleep(1000);

            }
            catch (InterruptedException e)
            {
                // no big deal
            }
            catch (Exception e)
            {
                System.out.println("Error: status refresh: " + e);
            }
        }
    }
    
    public void pleaseStop()
    {
        go = false;
        this.dispose();
    }

    /* ---------------------- */

    public void actionPerformed(ActionEvent newEvent)
    {

    }	
    
    /** Adds a blank line to ArrayList elements and sends it back. Used to eliminate code duplication
     * 
     * @param elements ArrayList to append blank line elements to
     */
    private void addBlankLine() {
        elements.add(new DefaultStyledDocument.ElementSpec(conRegular, DefaultStyledDocument.ElementSpec.EndTagType));
        elements.add(new DefaultStyledDocument.ElementSpec(conRegular, DefaultStyledDocument.ElementSpec.StartTagType));            	
    }
    
    /** Adds a given line to ArrayList elements and sends it back. Used to eliminate code duplication
     * 
     * @param elements ArrayList to append blank line elements to
     * @param s String of text to append
     * @param attrs Attributes to use when adding string
     */
    private void addString(String s, MutableAttributeSet attrs) {
        elements.add(new DefaultStyledDocument.ElementSpec(new SimpleAttributeSet(attrs),
                DefaultStyledDocument.ElementSpec.ContentType,
                s.toCharArray(),
                0,
                s.length()));
    }
    
    /** Percent of front armor remaining in a given section.
     * 
     * @param	section		The section to return.
     * @return				Percentage (0-100) remaining a section.
     */
    private float ArmorPercent(MUSection section) {
    	if(section.of == 0) {
    		return (float) 1.0;
    	} else {
    		return ((float) section.f / (float) section.of) * 100;
    	}
    } 
    
    /** Percent of internal points remaining in a given section.
     * 
     * @param	section		The section to return.
     * @return				Percentage (0-100) remaining a section.
     */
    private float InternalPercent(MUSection section) {
    	if(section.oi == 0) {
    		return (float) 1.0;
    	} else {
    		return ((float) section.i / (float) section.oi) * 100;
    	}
    }

    /** Percent of rear armor remaining in a given section.
     * 
     * @param	section		The section to return.
     * @return				Percentage (0-100) remaining a section.
     */
    private float RearArmorPercent(MUSection section) {
    	if(section.or == 0) {
    		return (float) 1.0;
    	} else {
    		return ((float) section.r / (float) section.or) * 100;
    	}
    }
    
   /** Percentage of ammo remaining in a bin.
    * 
    * @param	a			Ammo bin to check
    * @return				Percentage (0-100) remaining in the bin.
    */
   private float AmmoPercent(MUUnitAmmo a) {
	   if(a.roundsOriginal == 0) {
		   return (float) 1.0;
	   } else {		   
		   return ((float) a.roundsRemaining / (float) a.roundsOriginal) * 100;
	   }	  
   }
}
