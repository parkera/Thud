//
//  ErrorHandler.java
//  Thump
//
//  Created by Anthony Parker on Sun Jan 12 2003.
//  Copyright (c) 2003 Anthony Parker. All rights reserved.
//

package btthud.ui;

import javax.swing.*;

public class ErrorHandler {

    public static final int			ERR_GENERIC = 0;
    public static final int			ERR_SAVE_FAIL = 1;
    public static final int			ERR_OPEN_FAIL = 2;
    public static final int			ERR_BAD_INPUT = 3;
    public static final int			ERR_PRINT_FAIL = 4;

    public static void displayError(String message, int err)
    {
        JOptionPane.showMessageDialog(null,
                                      message + "\n\nError Number: " + err,
                                      "Error",
                                      JOptionPane.ERROR_MESSAGE);
    }

    public static void displayError(String message)
    {
        displayError(message, 0);
    }

}
