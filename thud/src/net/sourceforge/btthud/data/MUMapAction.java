package net.sourceforge.btthud.data;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

import net.sourceforge.btthud.engine.commands.UserCommand;
import net.sourceforge.btthud.ui.Thud;
import net.sourceforge.btthud.ui.map.MUMapComponent;
import net.sourceforge.btthud.util.StringTokenParser;

public class MUMapAction {
	public static final int		ERROR = -1;
	public static final int		KEY = 0;
	public static final int		MOUSE = 1;
	public static final int		MOUSEWHEEL = 2;
	public static final int		FORWARD = 0;
	public static final int		BACKWARD = 1;
	
	protected	int				iType;
	protected	int				iKeyModifiers;
	protected	int				iItem;
	protected	String			sActions;
	protected	String			sOriginal;
	
	public MUMapAction ()
	{
		iType = ERROR;
		iKeyModifiers = 0;
		iItem = 0;
		sActions = null;
		sOriginal = null;
	}
	
	public static MUMapAction parseLine (String sLine)
	{
		StringTokenParser tk = new StringTokenParser (sLine, ':');
		MUMapAction		myAction = new MUMapAction ();
		
		String sToken;
		
		sToken = tk.nextToken ();
		System.out.println ("TOKEN 01:" + sToken + ":");
		if (sToken.equals("KEY"))
		{
			myAction.iType = KEY;
			
			sToken = tk.nextToken ();
			myAction.iKeyModifiers = parseModifiers (sToken);
			System.out.println ("TOKEN 02:" + sToken + ":");
			
			sToken = tk.nextToken ();
			System.out.println ("TOKEN 03:" + sToken + ":");
			myAction.iItem = parseKeyString (sToken);
			
			if (myAction.iItem == -1)
				return null;
			
			myAction.sActions = tk.nextToken ();
			System.out.println ("TOKEN 03.01:" + myAction.sActions + ":");
			
			myAction.sOriginal = new String (sLine);
			
			return myAction;
		}
		else if (sToken.equals("MOUSE"))
		{
			myAction.iType = MOUSE;
			
			sToken = tk.nextToken ();
			System.out.println ("TOKEN 04:" + sToken + ":");
			myAction.iKeyModifiers = parseModifiers (sToken);
			
			sToken = tk.nextToken ();
			System.out.println ("TOKEN 05:" + sToken + ":");
			myAction.iItem = parseMouse (sToken);
			
			if (myAction.iItem == -1)
				return null;
			
			myAction.sActions = tk.nextToken ();
			System.out.println ("TOKEN 05.01:" + myAction.sActions + ":");
			
			myAction.sOriginal = new String (sLine);
			
			return myAction;
		}
		else if (sToken.equals("MOUSEWHEEL"))
		{
			myAction.iType = MOUSEWHEEL;
			
			sToken = tk.nextToken ();
			System.out.println ("TOKEN 06:" + sToken + ":");
			myAction.iKeyModifiers = parseModifiers (sToken);
			
			sToken = tk.nextToken ();
			System.out.println ("TOKEN 07:" + sToken + ":");
			myAction.iItem = parseMouseWheel (sToken);
			
			if (myAction.iItem == -1)
				return null;
			
			myAction.sActions = tk.nextToken ();
			System.out.println ("TOKEN 07.01:" + myAction.sActions + ":");
			
			myAction.sOriginal = new String (sLine);
			
			return myAction;
		}
			
		return null;
	}
	
	public static int parseModifiers (String sModifiers)
	{
		int		iRet = 0;
		
		if (sModifiers.contains("ALT"))
			iRet = iRet | InputEvent.ALT_DOWN_MASK;
		
		if (sModifiers.contains("SHIFT"))
			iRet = iRet | InputEvent.SHIFT_DOWN_MASK;
		
		if (sModifiers.contains("CTRL"))
			iRet = iRet | InputEvent.CTRL_DOWN_MASK;
		
		return iRet;
	}
	
	public static int parseMouse (String sModifiers)
	{
		int		iRet = -1;
		
		if (sModifiers.contains("BUTTON1"))
			iRet = MouseEvent.BUTTON1;
		
		if (sModifiers.contains("BUTTON2"))
			iRet = MouseEvent.BUTTON2;
		
		if (sModifiers.contains("BUTTON3"))
			iRet = MouseEvent.BUTTON3;
		
		return iRet;
	}
	
	public static int parseMouseWheel (String sModifiers)
	{
		int		iRet = -1;
		
		if (sModifiers.contains("FORWARD"))
			iRet = FORWARD;
		
		if (sModifiers.contains("BACKWARD"))
			iRet = BACKWARD;
		
		return iRet;
	}
	
	public static int parseKeyString (String s)
	{
		int		iRet = -1;

		if (s.equals ("VK_0")) return KeyEvent.VK_0;
		if (s.equals ("VK_1")) return KeyEvent.VK_1;
		if (s.equals ("VK_2")) return KeyEvent.VK_2;
		if (s.equals ("VK_3")) return KeyEvent.VK_3;
		if (s.equals ("VK_4")) return KeyEvent.VK_4;
		if (s.equals ("VK_5")) return KeyEvent.VK_5;
		if (s.equals ("VK_6")) return KeyEvent.VK_6;
		if (s.equals ("VK_7")) return KeyEvent.VK_7;
		if (s.equals ("VK_8")) return KeyEvent.VK_8;
		if (s.equals ("VK_9")) return KeyEvent.VK_9;
		if (s.equals ("VK_A")) return KeyEvent.VK_A;
		if (s.equals ("VK_ACCEPT")) return KeyEvent.VK_ACCEPT;
		if (s.equals ("VK_ADD")) return KeyEvent.VK_ADD;
		if (s.equals ("VK_AGAIN")) return KeyEvent.VK_AGAIN;
		if (s.equals ("VK_ALL_CANDIDATES")) return KeyEvent.VK_ALL_CANDIDATES;
		if (s.equals ("VK_ALPHANUMERIC")) return KeyEvent.VK_ALPHANUMERIC;
		if (s.equals ("VK_ALT")) return KeyEvent.VK_ALT;
		if (s.equals ("VK_ALT_GRAPH")) return KeyEvent.VK_ALT_GRAPH;
		if (s.equals ("VK_AMPERSAND")) return KeyEvent.VK_AMPERSAND;
		if (s.equals ("VK_ASTERISK")) return KeyEvent.VK_ASTERISK;
		if (s.equals ("VK_AT")) return KeyEvent.VK_AT;
		if (s.equals ("VK_B")) return KeyEvent.VK_B;
		if (s.equals ("VK_BACK_QUOTE")) return KeyEvent.VK_BACK_QUOTE;
		if (s.equals ("VK_BACK_SLASH")) return KeyEvent.VK_BACK_SLASH;
		if (s.equals ("VK_BACK_SPACE")) return KeyEvent.VK_BACK_SPACE;
		if (s.equals ("VK_BEGIN")) return KeyEvent.VK_BEGIN;
		if (s.equals ("VK_BRACELEFT")) return KeyEvent.VK_BRACELEFT;
		if (s.equals ("VK_BRACERIGHT")) return KeyEvent.VK_BRACERIGHT;
		if (s.equals ("VK_C")) return KeyEvent.VK_C;
		if (s.equals ("VK_CANCEL")) return KeyEvent.VK_CANCEL;
		if (s.equals ("VK_CAPS_LOCK")) return KeyEvent.VK_CAPS_LOCK;
		if (s.equals ("VK_CIRCUMFLEX")) return KeyEvent.VK_CIRCUMFLEX;
		if (s.equals ("VK_CLEAR")) return KeyEvent.VK_CLEAR;
		if (s.equals ("VK_CLOSE_BRACKET")) return KeyEvent.VK_CLOSE_BRACKET;
		if (s.equals ("VK_CODE_INPUT")) return KeyEvent.VK_CODE_INPUT;
		if (s.equals ("VK_COLON")) return KeyEvent.VK_COLON;
		if (s.equals ("VK_COMMA")) return KeyEvent.VK_COMMA;
		if (s.equals ("VK_COMPOSE")) return KeyEvent.VK_COMPOSE;
		if (s.equals ("VK_CONTEXT_MENU")) return KeyEvent.VK_CONTEXT_MENU;
		if (s.equals ("VK_CONTROL")) return KeyEvent.VK_CONTROL;
		if (s.equals ("VK_CONVERT")) return KeyEvent.VK_CONVERT;
		if (s.equals ("VK_COPY")) return KeyEvent.VK_COPY;
		if (s.equals ("VK_CUT")) return KeyEvent.VK_CUT;
		if (s.equals ("VK_D")) return KeyEvent.VK_D;
		if (s.equals ("VK_DEAD_ABOVEDOT")) return KeyEvent.VK_DEAD_ABOVEDOT;
		if (s.equals ("VK_DEAD_ABOVERING")) return KeyEvent.VK_DEAD_ABOVERING;
		if (s.equals ("VK_DEAD_ACUTE")) return KeyEvent.VK_DEAD_ACUTE;
		if (s.equals ("VK_DEAD_BREVE")) return KeyEvent.VK_DEAD_BREVE;
		if (s.equals ("VK_DEAD_CARON")) return KeyEvent.VK_DEAD_CARON;
		if (s.equals ("VK_DEAD_CEDILLA")) return KeyEvent.VK_DEAD_CEDILLA;
		if (s.equals ("VK_DEAD_CIRCUMFLEX")) return KeyEvent.VK_DEAD_CIRCUMFLEX;
		if (s.equals ("VK_DEAD_DIAERESIS")) return KeyEvent.VK_DEAD_DIAERESIS;
		if (s.equals ("VK_DEAD_DOUBLEACUTE")) return KeyEvent.VK_DEAD_DOUBLEACUTE;
		if (s.equals ("VK_DEAD_GRAVE")) return KeyEvent.VK_DEAD_GRAVE;
		if (s.equals ("VK_DEAD_IOTA")) return KeyEvent.VK_DEAD_IOTA;
		if (s.equals ("VK_DEAD_MACRON")) return KeyEvent.VK_DEAD_MACRON;
		if (s.equals ("VK_DEAD_OGONEK")) return KeyEvent.VK_DEAD_OGONEK;
		if (s.equals ("VK_DEAD_SEMIVOICED_SOUND")) return KeyEvent.VK_DEAD_SEMIVOICED_SOUND;
		if (s.equals ("VK_DEAD_TILDE")) return KeyEvent.VK_DEAD_TILDE;
		if (s.equals ("VK_DEAD_VOICED_SOUND")) return KeyEvent.VK_DEAD_VOICED_SOUND;
		if (s.equals ("VK_DECIMAL")) return KeyEvent.VK_DECIMAL;
		if (s.equals ("VK_DELETE")) return KeyEvent.VK_DELETE;
		if (s.equals ("VK_DIVIDE")) return KeyEvent.VK_DIVIDE;
		if (s.equals ("VK_DOLLAR")) return KeyEvent.VK_DOLLAR;
		if (s.equals ("VK_DOWN")) return KeyEvent.VK_DOWN;
		if (s.equals ("VK_E")) return KeyEvent.VK_E;
		if (s.equals ("VK_END")) return KeyEvent.VK_END;
		if (s.equals ("VK_ENTER")) return KeyEvent.VK_ENTER;
		if (s.equals ("VK_EQUALS")) return KeyEvent.VK_EQUALS;
		if (s.equals ("VK_ESCAPE")) return KeyEvent.VK_ESCAPE;
		if (s.equals ("VK_EURO_SIGN")) return KeyEvent.VK_EURO_SIGN;
		if (s.equals ("VK_EXCLAMATION_MARK")) return KeyEvent.VK_EXCLAMATION_MARK;
		if (s.equals ("VK_F")) return KeyEvent.VK_F;
		if (s.equals ("VK_F1")) return KeyEvent.VK_F1;
		if (s.equals ("VK_F10")) return KeyEvent.VK_F10;
		if (s.equals ("VK_F11")) return KeyEvent.VK_F11;
		if (s.equals ("VK_F12")) return KeyEvent.VK_F12;
		if (s.equals ("VK_F13")) return KeyEvent.VK_F13;
		if (s.equals ("VK_F14")) return KeyEvent.VK_F14;
		if (s.equals ("VK_F15")) return KeyEvent.VK_F15;
		if (s.equals ("VK_F16")) return KeyEvent.VK_F16;
		if (s.equals ("VK_F17")) return KeyEvent.VK_F17;
		if (s.equals ("VK_F18")) return KeyEvent.VK_F18;
		if (s.equals ("VK_F19")) return KeyEvent.VK_F19;
		if (s.equals ("VK_F2")) return KeyEvent.VK_F2;
		if (s.equals ("VK_F20")) return KeyEvent.VK_F20;
		if (s.equals ("VK_F21")) return KeyEvent.VK_F21;
		if (s.equals ("VK_F22")) return KeyEvent.VK_F22;
		if (s.equals ("VK_F23")) return KeyEvent.VK_F23;
		if (s.equals ("VK_F24")) return KeyEvent.VK_F24;
		if (s.equals ("VK_F3")) return KeyEvent.VK_F3;
		if (s.equals ("VK_F4")) return KeyEvent.VK_F4;
		if (s.equals ("VK_F5")) return KeyEvent.VK_F5;
		if (s.equals ("VK_F6")) return KeyEvent.VK_F6;
		if (s.equals ("VK_F7")) return KeyEvent.VK_F7;
		if (s.equals ("VK_F8")) return KeyEvent.VK_F8;
		if (s.equals ("VK_F9")) return KeyEvent.VK_F9;
		if (s.equals ("VK_FINAL")) return KeyEvent.VK_FINAL;
		if (s.equals ("VK_FIND")) return KeyEvent.VK_FIND;
		if (s.equals ("VK_FULL_WIDTH")) return KeyEvent.VK_FULL_WIDTH;
		if (s.equals ("VK_G")) return KeyEvent.VK_G;
		if (s.equals ("VK_GREATER")) return KeyEvent.VK_GREATER;
		if (s.equals ("VK_H")) return KeyEvent.VK_H;
		if (s.equals ("VK_HALF_WIDTH")) return KeyEvent.VK_HALF_WIDTH;
		if (s.equals ("VK_HELP")) return KeyEvent.VK_HELP;
		if (s.equals ("VK_HIRAGANA")) return KeyEvent.VK_HIRAGANA;
		if (s.equals ("VK_HOME")) return KeyEvent.VK_HOME;
		if (s.equals ("VK_I")) return KeyEvent.VK_I;
		if (s.equals ("VK_INPUT_METHOD_ON_OFF")) return KeyEvent.VK_INPUT_METHOD_ON_OFF;
		if (s.equals ("VK_INSERT")) return KeyEvent.VK_INSERT;
		if (s.equals ("VK_INVERTED_EXCLAMATION_MARK")) return KeyEvent.VK_INVERTED_EXCLAMATION_MARK;
		if (s.equals ("VK_J")) return KeyEvent.VK_J;
		if (s.equals ("VK_JAPANESE_HIRAGANA")) return KeyEvent.VK_JAPANESE_HIRAGANA;
		if (s.equals ("VK_JAPANESE_KATAKANA")) return KeyEvent.VK_JAPANESE_KATAKANA;
		if (s.equals ("VK_JAPANESE_ROMAN")) return KeyEvent.VK_JAPANESE_ROMAN;
		if (s.equals ("VK_K")) return KeyEvent.VK_K;
		if (s.equals ("VK_KANA")) return KeyEvent.VK_KANA;
		if (s.equals ("VK_KANA_LOCK")) return KeyEvent.VK_KANA_LOCK;
		if (s.equals ("VK_KANJI")) return KeyEvent.VK_KANJI;
		if (s.equals ("VK_KATAKANA")) return KeyEvent.VK_KATAKANA;
		if (s.equals ("VK_KP_DOWN")) return KeyEvent.VK_KP_DOWN;
		if (s.equals ("VK_KP_LEFT")) return KeyEvent.VK_KP_LEFT;
		if (s.equals ("VK_KP_RIGHT")) return KeyEvent.VK_KP_RIGHT;
		if (s.equals ("VK_KP_UP")) return KeyEvent.VK_KP_UP;
		if (s.equals ("VK_L")) return KeyEvent.VK_L;
		if (s.equals ("VK_LEFT")) return KeyEvent.VK_LEFT;
		if (s.equals ("VK_LEFT_PARENTHESIS")) return KeyEvent.VK_LEFT_PARENTHESIS;
		if (s.equals ("VK_LESS")) return KeyEvent.VK_LESS;
		if (s.equals ("VK_M")) return KeyEvent.VK_M;
		if (s.equals ("VK_META")) return KeyEvent.VK_META;
		if (s.equals ("VK_MINUS")) return KeyEvent.VK_MINUS;
		if (s.equals ("VK_MODECHANGE")) return KeyEvent.VK_MODECHANGE;
		if (s.equals ("VK_MULTIPLY")) return KeyEvent.VK_MULTIPLY;
		if (s.equals ("VK_N")) return KeyEvent.VK_N;
		if (s.equals ("VK_NONCONVERT")) return KeyEvent.VK_NONCONVERT;
		if (s.equals ("VK_NUM_LOCK")) return KeyEvent.VK_NUM_LOCK;
		if (s.equals ("VK_NUMBER_SIGN")) return KeyEvent.VK_NUMBER_SIGN;
		if (s.equals ("VK_NUMPAD0")) return KeyEvent.VK_NUMPAD0;
		if (s.equals ("VK_NUMPAD1")) return KeyEvent.VK_NUMPAD1;
		if (s.equals ("VK_NUMPAD2")) return KeyEvent.VK_NUMPAD2;
		if (s.equals ("VK_NUMPAD3")) return KeyEvent.VK_NUMPAD3;
		if (s.equals ("VK_NUMPAD4")) return KeyEvent.VK_NUMPAD4;
		if (s.equals ("VK_NUMPAD5")) return KeyEvent.VK_NUMPAD5;
		if (s.equals ("VK_NUMPAD6")) return KeyEvent.VK_NUMPAD6;
		if (s.equals ("VK_NUMPAD7")) return KeyEvent.VK_NUMPAD7;
		if (s.equals ("VK_NUMPAD8")) return KeyEvent.VK_NUMPAD8;
		if (s.equals ("VK_NUMPAD9")) return KeyEvent.VK_NUMPAD9;
		if (s.equals ("VK_O")) return KeyEvent.VK_O;
		if (s.equals ("VK_OPEN_BRACKET")) return KeyEvent.VK_OPEN_BRACKET;
		if (s.equals ("VK_P")) return KeyEvent.VK_P;
		if (s.equals ("VK_PAGE_DOWN")) return KeyEvent.VK_PAGE_DOWN;
		if (s.equals ("VK_PAGE_UP")) return KeyEvent.VK_PAGE_UP;
		if (s.equals ("VK_PASTE")) return KeyEvent.VK_PASTE;
		if (s.equals ("VK_PAUSE")) return KeyEvent.VK_PAUSE;
		if (s.equals ("VK_PERIOD")) return KeyEvent.VK_PERIOD;
		if (s.equals ("VK_PLUS")) return KeyEvent.VK_PLUS;
		if (s.equals ("VK_PREVIOUS_CANDIDATE")) return KeyEvent.VK_PREVIOUS_CANDIDATE;
		if (s.equals ("VK_PRINTSCREEN")) return KeyEvent.VK_PRINTSCREEN;
		if (s.equals ("VK_PROPS")) return KeyEvent.VK_PROPS;
		if (s.equals ("VK_Q")) return KeyEvent.VK_Q;
		if (s.equals ("VK_QUOTE")) return KeyEvent.VK_QUOTE;
		if (s.equals ("VK_QUOTEDBL")) return KeyEvent.VK_QUOTEDBL;
		if (s.equals ("VK_R")) return KeyEvent.VK_R;
		if (s.equals ("VK_RIGHT")) return KeyEvent.VK_RIGHT;
		if (s.equals ("VK_RIGHT_PARENTHESIS")) return KeyEvent.VK_RIGHT_PARENTHESIS;
		if (s.equals ("VK_ROMAN_CHARACTERS")) return KeyEvent.VK_ROMAN_CHARACTERS;
		if (s.equals ("VK_S")) return KeyEvent.VK_S;
		if (s.equals ("VK_SCROLL_LOCK")) return KeyEvent.VK_SCROLL_LOCK;
		if (s.equals ("VK_SEMICOLON")) return KeyEvent.VK_SEMICOLON;
		if (s.equals ("VK_SEPARATER")) return KeyEvent.VK_SEPARATER;
		if (s.equals ("VK_SEPARATOR")) return KeyEvent.VK_SEPARATOR;
		if (s.equals ("VK_SHIFT")) return KeyEvent.VK_SHIFT;
		if (s.equals ("VK_SLASH")) return KeyEvent.VK_SLASH;
		if (s.equals ("VK_SPACE")) return KeyEvent.VK_SPACE;
		if (s.equals ("VK_STOP")) return KeyEvent.VK_STOP;
		if (s.equals ("VK_SUBTRACT")) return KeyEvent.VK_SUBTRACT;
		if (s.equals ("VK_T")) return KeyEvent.VK_T;
		if (s.equals ("VK_TAB")) return KeyEvent.VK_TAB;
		if (s.equals ("VK_U")) return KeyEvent.VK_U;
		if (s.equals ("VK_UNDEFINED")) return KeyEvent.VK_UNDEFINED;
		if (s.equals ("VK_UNDERSCORE")) return KeyEvent.VK_UNDERSCORE;
		if (s.equals ("VK_UNDO")) return KeyEvent.VK_UNDO;
		if (s.equals ("VK_UP")) return KeyEvent.VK_UP;
		if (s.equals ("VK_V")) return KeyEvent.VK_V;
		if (s.equals ("VK_W")) return KeyEvent.VK_W;
		if (s.equals ("VK_WINDOWS")) return KeyEvent.VK_WINDOWS;
		if (s.equals ("VK_X")) return KeyEvent.VK_X;
		if (s.equals ("VK_Y")) return KeyEvent.VK_Y;
		if (s.equals ("VK_Z ")) return KeyEvent.VK_Z;
		
		return iRet;
	}
	
	public int getModifiersEx (InputEvent ie, boolean bIgnoreAlt)
	{
		int		myMask = InputEvent.CTRL_DOWN_MASK;
		
		myMask = myMask | InputEvent.SHIFT_DOWN_MASK;
		
		if (!bIgnoreAlt)
			myMask = myMask | InputEvent.ALT_DOWN_MASK;
		
		return (ie.getModifiersEx () & myMask);
	}
	
	public boolean foundKeyAction (KeyEvent ke)
	{		
		if (iType != KEY)
			return false;
		
		if (getModifiersEx (ke, false) != iKeyModifiers)
			return false;
		
		if (ke.getKeyCode () != iItem)
			return false;
		
		return true;
	}
	
	public boolean foundMouseAction (MouseEvent me)
	{
		boolean		bIgnoreAlt = false;
		
		if (iType != MOUSE)
			return false;

		System.out.println ("foundMouseAction: 001");
		
		System.out.println ("foundMouseAction: 001.01 " + new Integer (me.getModifiersEx ()).toString ());
		System.out.println ("foundMouseAction: 001.02 " + new Integer (iKeyModifiers).toString ());
		System.out.println ("foundMouseAction: 001.03 ALT " + new Integer (InputEvent.ALT_DOWN_MASK).toString ());
		System.out.println ("foundMouseAction: 001.03 CTRL " + new Integer (InputEvent.CTRL_DOWN_MASK).toString ());
		System.out.println ("foundMouseAction: 001.03 SHIFT " + new Integer (InputEvent.SHIFT_DOWN_MASK).toString ());
		
		if (me.getButton () == MouseEvent.BUTTON2)
			bIgnoreAlt = true;
		
		int iMods = getModifiersEx (me, bIgnoreAlt);
		
		System.out.println ("foundMouseAction: 001.04 iMods " + new Integer (iMods).toString ());

		if (iMods != iKeyModifiers)
			return false;
		
		System.out.println ("foundMouseAction: 002.01 " + new Integer (me.getButton ()).toString ());
		System.out.println ("foundMouseAction: 002.02 " + new Integer (iItem).toString ());
		
		if (me.getButton () != iItem)
			return false;
		
		return true;
	}
	
	public boolean foundMouseWheelAction (MouseWheelEvent me)
	{
		if (iType != MOUSEWHEEL)
			return false;
		
		System.out.println ("foundMouseWheelAction: 001");
		
		if (getModifiersEx (me, false) != iKeyModifiers)
			return false;
		
		System.out.println ("foundMouseWheelAction: 002");
		System.out.println ("foundMouseWheelAction: 002.01 iItem " + new Integer (iItem).toString ());
		System.out.println ("foundMouseWheelAction: 002.02 Rotation " + new Integer (me.getWheelRotation ()).toString ());
		
    	if (iItem == FORWARD && me.getWheelRotation () < 0)
			return true;
    	else if (iItem == BACKWARD && me.getWheelRotation () > 0)
			return true;
    	
		System.out.println ("foundMouseWheelAction: 003");
		
		return false;
	}
	
	public void zoomin (String sAction, Thud thud, MUMapComponent map)
	{
		int		iFactor = 5;
		StringTokenizer		tk = new StringTokenizer (sAction, " ");
		
		tk.nextToken ();
		if (tk.hasMoreElements ())
		{
			String	sToken = tk.nextToken ();
			iFactor = Integer.parseInt(sToken);
		}
		
		map.setHexHeight (map.getHexHeight () + iFactor);
		map.repaint ();
	}
	
	public void zoomout (String sAction, Thud thud, MUMapComponent map)
	{
		int		iFactor = 5;
		StringTokenizer		tk = new StringTokenizer (sAction, " ");
		
		tk.nextToken ();
		if (tk.hasMoreElements ())
		{
			String	sToken = tk.nextToken ();
			iFactor = Integer.parseInt(sToken);
		}
		
		map.setHexHeight (map.getHexHeight () - iFactor);
		map.repaint ();
	}
	
	public void sendCommand (Thud thud, String sCommand)
	{
		System.out.println ("sendCommand :" + sCommand + ":");
		
		try {
			thud.getConn().sendCommand(new UserCommand (sCommand));
		} catch (Exception e1) {
			// TODO: Seems like it'd be more friendly to report
			// these errors in the main window, or in a modal
			// dialog.  Hiding things in the console is so like
			// 1990.
			System.err.println("Can't send: " + e1);
		}
	}
	
	public void setHeading (String sAction, Thud thud, MUMapComponent map, MouseEvent e)
	{
		if (e == null) return;
		
        //
        // OK, time for the real stuff.  For now, this is just a demonstration
        // of how to get the various kinds of coordinates we might be
        // interested in computing.
        //

        // Compute event's pixel coordinates relative to our unit.
        //
        // Useful for code needing to compute directions relative to our unit,
        // and distances which aren't directly connected to map distances.
        final Point offsetPt = new Point ();

        map.getScreenToOffset(offsetPt, e.getX(), e.getY());

        // Compute map coordinates (hex height normalized to 1, odd hexes start
        // at y of -0.5f).
        //
        // Useful for code needing to reason about actual game map coordinates,
        // such as code to compute map distances.
        //
        // Note that it's technically safe to pass the same source and
        // destination to this method.
        final Point2D.Float mapPt = new Point2D.Float ();

        map.getOffsetToMap(mapPt, offsetPt);

        // Compute hex from map coordinate.  We can do this directly using
        // MUPoint, but this convenience method avoids allocating new MUPoints
        // every time we need to make this calculation.  We may eventually just
        // move the computations into a static method of MUPoint.
        //
        // Useful for when you need to know what hex you clicked on.
        final Point hexPt = new Point ();

        map.getMapToHex(hexPt, mapPt);
        
        double theta = Math.atan2((mapPt.getY () - (double)map.data.myUnit.position.getFY ()), (mapPt.getX () - (double)map.data.myUnit.position.getFX ()));
        double thetaDegrees = Math.toDegrees (theta);
        
        // now convert the degrees to map degrees
        
        double mapDegrees = thetaDegrees + 90.0;
        
        System.out.println ("\tMyUnit X:" + new Float (map.data.myUnit.position.getCenterFX ()).toString () + ": Y:" + new Float (map.data.myUnit.position.getCenterFY ()).toString () + ": Theta :" + new Double (thetaDegrees).toString () + ":");
        System.out.println ("\tMap Degrees from unit :" + new Double(mapDegrees).toString () + ":");
        
        sendCommand (thud, ".h " + new Double (mapDegrees).toString ());
	}
	
	public void thudJumpTo (String sAction, Thud thud, MUMapComponent map, MouseEvent e)
	{
		if (e == null) return;
		
        //
        // OK, time for the real stuff.  For now, this is just a demonstration
        // of how to get the various kinds of coordinates we might be
        // interested in computing.
        //

        // Compute event's pixel coordinates relative to our unit.
        //
        // Useful for code needing to compute directions relative to our unit,
        // and distances which aren't directly connected to map distances.
        final Point offsetPt = new Point ();

        map.getScreenToOffset(offsetPt, e.getX(), e.getY());

        // Compute map coordinates (hex height normalized to 1, odd hexes start
        // at y of -0.5f).
        //
        // Useful for code needing to reason about actual game map coordinates,
        // such as code to compute map distances.
        //
        // Note that it's technically safe to pass the same source and
        // destination to this method.
        final Point2D.Float mapPt = new Point2D.Float ();

        map.getOffsetToMap(mapPt, offsetPt);

        // Compute hex from map coordinate.  We can do this directly using
        // MUPoint, but this convenience method avoids allocating new MUPoints
        // every time we need to make this calculation.  We may eventually just
        // move the computations into a static method of MUPoint.
        //
        // Useful for when you need to know what hex you clicked on.
        final Point hexPt = new Point ();

        map.getMapToHex(hexPt, mapPt);
        
        double theta = Math.atan2((mapPt.getY () - (double)map.data.myUnit.position.getFY ()), (mapPt.getX () - (double)map.data.myUnit.position.getFX ()));
        double thetaDegrees = Math.toDegrees (theta);
        
        // now convert the degrees to map degrees
        
        double mapDegrees = thetaDegrees + 90.0;
        
        System.out.println ("\tMyUnit X:" + new Float (map.data.myUnit.position.getCenterFX ()).toString () + ": Y:" + new Float (map.data.myUnit.position.getCenterFY ()).toString () + ": Theta :" + new Double (thetaDegrees).toString () + ":");
        System.out.println ("\tMap Degrees from unit :" + new Double(mapDegrees).toString () + ":");
        
        // now that we have bearing we need distance
        
        double dX;
        double dY;
        
        dX = mapPt.getX () - (double)map.data.myUnit.position.getFX ();
        dY = mapPt.getY () - (double)map.data.myUnit.position.getFY ();
        
        double dDist = Math.sqrt ((dX * dX) + (dY + dY));
        
        sendCommand (thud, "jump " + new Double (mapDegrees).toString () + " " + new Double (dDist).toString ());
	}
	
	public void setTarget (String sAction, Thud thud, MUMapComponent map, MouseEvent e)
	{
		if (e == null) return;

        //
        // OK, time for the real stuff.  For now, this is just a demonstration
        // of how to get the various kinds of coordinates we might be
        // interested in computing.
        //

        // Compute event's pixel coordinates relative to our unit.
        //
        // Useful for code needing to compute directions relative to our unit,
        // and distances which aren't directly connected to map distances.
        final Point offsetPt = new Point ();

        map.getScreenToOffset(offsetPt, e.getX(), e.getY());

        // Compute map coordinates (hex height normalized to 1, odd hexes start
        // at y of -0.5f).
        //
        // Useful for code needing to reason about actual game map coordinates,
        // such as code to compute map distances.
        //
        // Note that it's technically safe to pass the same source and
        // destination to this method.
        final Point2D.Float mapPt = new Point2D.Float ();

        map.getOffsetToMap(mapPt, offsetPt);

        // Compute hex from map coordinate.  We can do this directly using
        // MUPoint, but this convenience method avoids allocating new MUPoints
        // every time we need to make this calculation.  We may eventually just
        // move the computations into a static method of MUPoint.
        //
        // Useful for when you need to know what hex you clicked on.
        final Point hexPt = new Point ();

        map.getMapToHex(hexPt, mapPt);
        
    	System.out.println ("Review Contacts:");
    	
    	int i, iSize;
    	MUUnitInfo	uiUnit;
    	double		uX;
    	double		uY;
    	double		xX;
    	double		xY;
    	double		dX;
    	double		dY;
    	
    	double		dDist;
    	MUUnitInfo	selected = null;
    	double		selectedDist = 1000000.0;
    	
    	uX = (double)mapPt.getX ();
    	uY = (double)mapPt.getY ();;
    	
    	iSize = 0;
    	if (map.data.contacts.size () > 0)
    		iSize = map.data.contacts.size ();
    	
    	for (i = 0; i < iSize; ++i)
    	{
    		uiUnit = map.data.contacts.get(i);
    		uiUnit.target = false;
    		
    		xX = (double)uiUnit.getX ();
    		xY = (double)uiUnit.getY ();
    		
    		dX = uX - xX;
    		dY = uY - xY;
    		
    		dDist = dX * dX + dY * dY;
    		
    		if (dDist < selectedDist)
    		{
    			selectedDist = dDist;
    			selected = uiUnit;
    		}
    		
    		System.out.println ("uiUnit [" + new Integer(i).toString () + "] ID :" + uiUnit.id + ": Friend " +
    		    new Boolean (uiUnit.isFriend ()).toString () + " Target " +
    		    new Boolean (uiUnit.isTarget ()).toString ());
    		System.out.println ("U (" + new Double (uX).toString () + "," + new Double (uY).toString () + ")");
    		System.out.println ("X (" + new Double (xX).toString () + "," + new Double (xY).toString () + ")");
    		System.out.println ("Dist " + new Double (dDist).toString ());
    	}
    	
    	if (selected != null)
    	{
    		sendCommand (thud, "lock " + selected.id);
    	}
    	
        map.repaint ();
	}
	
	public void thudJumpDeath (String sAction, Thud thud, MUMapComponent map, MouseEvent e)
	{
		if (e == null) return;

        //
        // OK, time for the real stuff.  For now, this is just a demonstration
        // of how to get the various kinds of coordinates we might be
        // interested in computing.
        //

        // Compute event's pixel coordinates relative to our unit.
        //
        // Useful for code needing to compute directions relative to our unit,
        // and distances which aren't directly connected to map distances.
        final Point offsetPt = new Point ();

        map.getScreenToOffset(offsetPt, e.getX(), e.getY());

        // Compute map coordinates (hex height normalized to 1, odd hexes start
        // at y of -0.5f).
        //
        // Useful for code needing to reason about actual game map coordinates,
        // such as code to compute map distances.
        //
        // Note that it's technically safe to pass the same source and
        // destination to this method.
        final Point2D.Float mapPt = new Point2D.Float ();

        map.getOffsetToMap(mapPt, offsetPt);

        // Compute hex from map coordinate.  We can do this directly using
        // MUPoint, but this convenience method avoids allocating new MUPoints
        // every time we need to make this calculation.  We may eventually just
        // move the computations into a static method of MUPoint.
        //
        // Useful for when you need to know what hex you clicked on.
        final Point hexPt = new Point ();

        map.getMapToHex(hexPt, mapPt);
        
    	System.out.println ("Review Contacts:");
    	
    	int i, iSize;
    	MUUnitInfo	uiUnit;
    	double		uX;
    	double		uY;
    	double		xX;
    	double		xY;
    	double		dX;
    	double		dY;
    	
    	double		dDist;
    	MUUnitInfo	selected = null;
    	double		selectedDist = 1000000.0;
    	
    	uX = (double)mapPt.getX ();
    	uY = (double)mapPt.getY ();;
    	
    	iSize = 0;
    	if (map.data.contacts.size () > 0)
    		iSize = map.data.contacts.size ();
    	
    	for (i = 0; i < iSize; ++i)
    	{
    		uiUnit = map.data.contacts.get(i);
    		uiUnit.target = false;
    		
    		xX = (double)uiUnit.getX ();
    		xY = (double)uiUnit.getY ();
    		
    		dX = uX - xX;
    		dY = uY - xY;
    		
    		dDist = dX * dX + dY * dY;
    		
    		if (dDist < selectedDist)
    		{
    			selectedDist = dDist;
    			selected = uiUnit;
    		}
    		
    		System.out.println ("uiUnit [" + new Integer(i).toString () + "] ID :" + uiUnit.id + ": Friend " +
    		    new Boolean (uiUnit.isFriend ()).toString () + " Target " +
    		    new Boolean (uiUnit.isTarget ()).toString ());
    		System.out.println ("U (" + new Double (uX).toString () + "," + new Double (uY).toString () + ")");
    		System.out.println ("X (" + new Double (xX).toString () + "," + new Double (xY).toString () + ")");
    		System.out.println ("Dist " + new Double (dDist).toString ());
    	}
    	
    	if (selected != null)
    	{
    		sendCommand (thud, "jump " + selected.id);
    	}
    	
        map.repaint ();
	}
	
	public void thudMessage (String sAction, Thud thud, MUMapComponent map, MouseEvent e)
	{
		String sMessage;
		
		sMessage = sAction.substring(12);
		
        JOptionPane.showMessageDialog (null, sMessage, "Thud Message", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void executeActions (Thud thud, MUMapComponent map, MouseEvent e)
	{
		StringTokenizer		tk = new StringTokenizer (sActions, "^");
		String				sAction;
		
		System.out.println ("executeActions: " + sOriginal);
		
		while (tk.hasMoreTokens ())
		{
			// look for special actions
			sAction = tk.nextToken ();
			System.out.println ("EXECUTE ACTION :" + sAction);
			
			if (sAction.contains ("THUDZOOMIN"))
			{
				zoomin (sAction, thud, map);
			}
			else if (sAction.contains("THUDZOOMOUT"))
			{
				zoomout (sAction, thud, map);
			}
			else if (sAction.contains("THUDTARGET"))
			{
				setTarget (sAction, thud, map, e);
			}
			else if (sAction.contains("THUDHEADING"))
			{
				setHeading (sAction, thud, map, e);
			}
			else if (sAction.contains("THUDMESSAGE"))
			{
				thudMessage (sAction, thud, map, e);
			}
			else if (sAction.contains("THUDJUMPTO"))
			{
				thudJumpTo (sAction, thud, map, e);
			}
			else if (sAction.contains("THUDJUMPDEATH"))
			{
				thudJumpDeath (sAction, thud, map, e);
			}
			else
			{
				sendCommand (thud, sAction);
			}
		}
	}
	
	public void debug ()
	{
		System.out.println ("MUMapAction: iType " + new Integer (iType).toString ());
		System.out.println ("MUMapAction: iKeyModifiers " + new Integer (iKeyModifiers).toString ());
		System.out.println ("MUMapAction: iItem " + new Integer (iItem).toString ());
		System.out.println ("MUMapAction: sActions :" + sActions + ":");
		System.out.println ("MUMapAction: sOriginal :" + sOriginal + ":");
	}
}
