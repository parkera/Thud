//
//  StyleRunHolder.java
//  Thud
//
//  Created by Anthony Parker on Sun Dec 30 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//


public class StyleRunHolder {

    public String		style;
    public int			start;
    public int			length;
    
    public StyleRunHolder()
    {
        style = "regular";
        start = 0;
        length = 0;
    }

    public StyleRunHolder(String style, int start, int length)
    {
        this.style = style;
        this.start = start;
        this.length = length;
    }
}
