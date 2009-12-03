package net.sourceforge.btthud.data;

import java.util.Vector;
import java.io.File;
import java.net.URL;
import sun.net.www.content.text.PlainTextInputStream;
import java.io.*;
import net.sourceforge.btthud.ui.map.MUMapComponent;
import net.sourceforge.btthud.ui.Thud;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MUMapActions {
	protected Vector<MUMapAction>	myActionList;
	
	public MUMapActions ()
	{
		// need to load the map actions if they exist
		
		myActionList = new Vector<MUMapAction> ();
		
		String		myHomeDir = System.getProperty("user.home");
		
		File myThudConfig = new File (myHomeDir + "/Thud.cfg");
		
		System.out.println ("THUDCFG :" + myThudConfig.getAbsolutePath ());
		
		if (!myThudConfig.exists ())
		{
			System.out.println ("Thud Config File does not exist :" + myThudConfig.getAbsolutePath ());
			
			// pull the default out of the jar file
			
			URL urlToDefault = getClass().getResource("/media/ThudDefaultConfig.txt");
			
			try
			{
				PlainTextInputStream	myStream;
				
				myStream = (PlainTextInputStream)urlToDefault.getContent ();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(myStream));
				BufferedWriter writer = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (myThudConfig)));
				
				while (true)
				{
					String sLine = reader.readLine ();
					if (sLine == null)
						break;
					
					System.out.println ("LINE: " + sLine);
					
					writer.write(sLine);
					writer.newLine ();
				}
				
				reader.close ();
				writer.close ();
			}
			catch (Exception ex)
			{
				System.out.println ("Exception ex :" + ex.getMessage ());
				ex.printStackTrace ();
			}
		}

		myThudConfig = new File (myHomeDir + "/Thud.cfg");
		
		System.out.println ("HERE 001");
		
		try
		{
			System.out.println ("HERE 002");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader (new FileInputStream (myThudConfig)));

			while (true)
			{
				String sLine = reader.readLine ();
				if (sLine == null)
					break;
				
				System.out.println ("LINE 01: " + sLine);
				
				if (sLine.length () < 10)
					continue;
				
				if (sLine.charAt (0) == '#')
					continue;
				
				System.out.println ("LINE 02: " + sLine);
				
				MUMapAction myAction = MUMapAction.parseLine(sLine);
				if (myAction != null)
				{
					myActionList.add(myAction);
					System.out.println ("MUMapAction Added");
					myAction.debug ();
				}
				else
				{
					System.out.println ("MUMapAction Not Added");
				}
			}
		}
		catch (Exception ex)
		{
			System.out.println ("Exception : " + ex.getMessage ());
			ex.printStackTrace ();
		}		
	}
	
	public void fireKeyEvent (Thud thud, MUMapComponent map, KeyEvent ke)
	{
		int				iLen = myActionList.size ();
		int				iIdx;
		MUMapAction		myAction;
		
		System.out.println ("Firing Key Event");
		
		for (iIdx = 0; iIdx < iLen; ++iIdx)
		{
			myAction = myActionList.get(iIdx);
			
			if (myAction.foundKeyAction(ke))
			{
				myAction.executeActions(thud, map, null);
				break;
			}
		}
	}
	
	public void fireMouseEvent (Thud thud, MUMapComponent map, MouseEvent me)
	{
		int				iLen = myActionList.size ();
		int				iIdx;
		MUMapAction		myAction;
		
		System.out.println ("Firing Mouse Event");
		
		for (iIdx = 0; iIdx < iLen; ++iIdx)
		{
			myAction = myActionList.get(iIdx);
			
			if (myAction.foundMouseAction(me))
			{
				myAction.executeActions(thud, map, me);
				break;
			}
		}
	}
	
	public void fireMouseWheelEvent (Thud thud, MUMapComponent map, MouseWheelEvent me)
	{
		int				iLen = myActionList.size ();
		int				iIdx;
		MUMapAction		myAction;
		
		System.out.println ("Firing MouseWheel Event");
		
		for (iIdx = 0; iIdx < iLen; ++iIdx)
		{
			myAction = myActionList.get(iIdx);
			
			if (myAction.foundMouseWheelAction(me))
			{
				myAction.executeActions(thud, map, null);
				break;
			}
		}
	}
}

