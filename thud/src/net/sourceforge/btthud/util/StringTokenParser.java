package net.sourceforge.btthud.util;

import java.util.StringTokenizer;
import java.util.Vector;

public class StringTokenParser
{
	protected Vector<String>	myTokens;
	protected int				iCursor;
	
	public StringTokenParser (String myString, char delim)
	{
		iCursor = 0;
		
		char			cDelim;
		StringTokenizer tk;
		
		cDelim = delim;
		char [] myChars = new char [1];
		
		myChars [0] = cDelim;
		
		tk = new StringTokenizer (myString, new String (myChars), true);
		
		String myToken;
		myTokens = new Vector<String> ();
		
		while (true)
		{
			if (tk.hasMoreTokens ())
				myToken = tk.nextToken ();
			else
				break;
			
			if (myToken.charAt (0) == cDelim)
				myTokens.add("");
			else
			{
				myTokens.add (myToken);
				
				// This should be the delimiter
				
				if (tk.hasMoreTokens ())
					tk.nextToken ();
				else
					break;
			}
		}
	}
	
	public int numTokens ()
	{
		return myTokens.size ();
	}
	
	public String getToken (int iIdx)
	{
		return (String)myTokens.get(iIdx);
	}
	
	public void rewind ()
	{
		iCursor = 0;
	}
	
	public String nextToken ()
	{
		String sToken = getToken (iCursor);
		iCursor ++;
		
		return sToken;
	}
}
