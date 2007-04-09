//
//  JTextPaneWriter.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team.
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.util;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleContext;
import javax.swing.text.Style;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import java.io.PrintWriter;
import java.io.CharArrayWriter;

/**
 * Implements a PrintWriter-style interface to a JTextPane.
 */
public class JTextPaneWriter extends PrintWriter {
	private final CharArrayWriter buffer;

	private final JTextPane textPane;

	private String currStyle;
	private String oldStyle = null;

	public JTextPaneWriter (final JTextPane textPane) {
		super(new CharArrayWriter (), true);
		buffer = (CharArrayWriter)out;

		this.textPane = textPane;
		this.currStyle = StyleContext.DEFAULT_STYLE;
	}

	//
	// Style state setting.
	//

	// beginStyle() currently doesn't support recursive locking, so it
	// can't be nested; be careful of deadlock.
	public void beginStyle (final String newStyle) {
		synchronized (lock) {
			while (oldStyle != null) {
				try {
					lock.wait();
				} catch (final InterruptedException e) {
					// Nothing to do.
				}
			}

			oldStyle = getStyle ();
			setStyle(newStyle);
		}
	}

	public void endStyle () {
		synchronized (lock) {
			// TODO: Assert oldStyle != null?
			setStyle(oldStyle);
			oldStyle = null;

			lock.notify();
		}
	}

	public String getStyle () {
		return currStyle;
	}

	public void setStyle (final String newStyle) {
		synchronized (lock) {
			if (currStyle == newStyle) {
				return;
			}

			flush();

			if (newStyle == null) {
				currStyle = StyleContext.DEFAULT_STYLE;
			} else {
				currStyle = newStyle;
			}
		}
	}

	//
	// Document management.
	//

	public StyledDocument getDocument () {
		return textPane.getStyledDocument ();
	}

	public boolean hasStyle (final String name) {
		if (name == null) {
			// We treat null like the DEFAULT_STYLE.
			return true;
		} else {
			return textPane.getStyle(name) != null;
		}
	}

	public void addStyle (final String name, final AttributeSet attrs) {
		final Style styleBase = textPane.getStyle(StyleContext.DEFAULT_STYLE);

		final Style style = textPane.addStyle(name, styleBase);

		style.addAttributes(attrs);
	}

	public void reset () {
		synchronized (lock) {
			final StyledDocument doc = getDocument();

			try {
				doc.remove(0, doc.getLength());
			} catch (final BadLocationException e) {
				// TODO: We need to worry about concurrent
				// modification of the document.  getLength()
				// could be out of date by the time we call
				// remove(), for example.  If we synchronize
				// all mutation through this Writer, though,
				// then that obviously solves the problem.
				System.err.println("Reset failed: " + e);
			}
		}
	}

	//
	// Convenience methods.
	//

	public void print (final String str, final String style) {
		synchronized (lock) {
			final String styleTemp = getStyle();

			try {
				setStyle(style);
				print(str);
			} finally {
				setStyle(styleTemp);
			}
		}
	}

	public void println (final String str, final String style) {
		synchronized (lock) {
			final String styleTemp = getStyle();

			try {
				setStyle(style);
				println(str);
			} finally {
				setStyle(styleTemp);
			}
		}
	}

	//
	// PrintWriter extensions.
	//

	public void println () {
		// PrintWriter outputs line.separator, while JTextPane
		// expects the single character \n.
		//
		// This is the only place we need to change this
		// behavior, as far as we know, but it depends on the
		// details of the PrintWriter implementation.
		//
		// A better implementation would be to scan the input
		// for line.separator, and convert it to '\n'.  This
		// would add another level of indirection, though.
		synchronized (lock) {
			write('\n');
			flush();
		}
	}

	public void flush () {
		//super.flush()?

		synchronized (lock) {
			final StyledDocument doc = getDocument();

			if (buffer.size() == 0) {
				// FIXME: This is a hack to force scroll until
				// all writes go through this writer.
				// TODO: Add a flag to turn scrolling on/off.
				textPane.setCaretPosition(doc.getLength());
				return;
			}

			try {
				doc.insertString(doc.getLength(),
				                 buffer.toString(),
				                 textPane.getStyle(currStyle));

				// TODO: Add a flag to turn scrolling on/off.
				textPane.setCaretPosition(doc.getLength());
			} catch (final BadLocationException e) {
				// TODO: We need to worry about concurrent
				// modification of the document.  getLength()
				// could be out of date by the time we call
				// remove(), for example.  If we synchronize
				// all mutation through this Writer, though,
				// then that obviously solves the problem.
				System.err.println("Flush failed: " + e);
			}

			buffer.reset();
		}
	}
}
