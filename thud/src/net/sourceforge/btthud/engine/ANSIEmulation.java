//
//  ANSIEmulation.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine;

import java.util.List;
import java.util.ArrayList;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Emulates an ANSI terminal.  Generates a list of Style name/String pairs,
 * suitable for adding to a DefaultStyledDocument.
 *
 * The Style names conform to the following format (regexp-style):
 *
 * ANSI:f?h?u?i?[xrgybmcw]?[XRGYBMCW]?
 *
 * The characters after "ANSI:" have the following meanings:
 *
 * f - flash                       i - inverse
 * h - hilite                      n - normal
 * u - underline
 *
 * x - black foreground            X - black background
 * r - red foreground              R - red background
 * g - green foreground            G - green background
 * y - yellow foreground           Y - yellow background
 * b - blue foreground             B - blue background
 * m - magenta foreground          M - magenta background
 * c - cyan foreground             C - cyan background
 * w - white foreground            W - white background
 *
 * The "n" (normal) code has no explicit encoding; it's just the "ANSI:" name.
 *
 * This encoding assigns one unique ANSI:-prefixed name for any particular
 * emulator state.  The intent is that a cache will be checked for existing
 * Styles, and getStyle() used to generate attribute sets from names as needed.
 */
class ANSIEmulation {
	// Escape sequence state constants.
	static private enum EscapeState {
		UNESCAPED, // waiting for ESC
		ESCAPE_0, // waiting for [
		ESCAPED // in escape
	}

	// Container for style/text pair.
	static class StyledString {
		public final String style;
		public final String text;

		private StyledString (final String style, final String text) {
			this.style = style;
			this.text = text;
		}

		private StyledString (final ANSIState state,
		                      final CharSequence text) {
			this(state.toString(), text.toString());
		}
	}

	static private final Pattern stylePattern = Pattern.compile("ANSI:(f)?(h)?(u)?(i)?([xrgybmcw])?([XRGYBMCW])?");

	//
	// Parser.
	//

	private ANSIState termState = new ANSIState ();

	// All ANSI terminal codes are of the form
	//
	// 	ESC [ <value> ; <value> ; ... <letter>
	//
	// though the only ones we're interested in are when <letter> = 'm'.
	//
	// We'll also leave an ANSI sequence if we encounter a line boundary.
	// These should never occur anyway, and act as a safety mechanism.
	//
	// TODO: We could probably do this using regexps and some fancy regexp
	// class, rather than writing a character parser by hand.  A regexp
	// engine would probably do it faster, too.
	//
	// The regular expression would be something like:
	//
	// 	\u001B \[ [^[:alpha:]]
	List<StyledString> parseLine (final String line,
	                              final boolean discard) {
		// Initialize parser.
		final List<StyledString> parsed;

		final StringBuilder text;
		ANSIState textState = new ANSIState (termState);

		if (discard) {
			parsed = null;
			text = null;
		} else {
			parsed = new ArrayList<StyledString> ();
			text = new StringBuilder ();
		}

		EscapeState escaped = EscapeState.UNESCAPED;

		int start = 0; // start index of current parameters
		int textStart = 0; // start index of current text

		// Parse characters.
		for (int ii = 0; ii < line.length(); ii++) {
			final char ch = line.charAt(ii);

			switch (escaped) {
			case UNESCAPED: /* wait for ESC */
				if (ch == '\u001B') {
					escaped = EscapeState.ESCAPE_0;
				}
				break;

			case ESCAPE_0: /* wait for [ */
				if (ch == '[') {
					escaped = EscapeState.ESCAPED;
					start = ii + 1;
				} else {
					escaped = EscapeState.UNESCAPED;
				}
				break;

			case ESCAPED: /* collect parameters */
				if (!Character.isLetter(ch)) {
					// Not done yet.
					break;
				}

				if (!discard) {
					final int textEnd = start - 2;

					if (textStart != textEnd) {
						// Buffer last state text.
						if (!textState.equals(termState)) {
							// Flush buffered text.
							addStyledString(parsed, textState, text);
							textState = new ANSIState (termState);
						}

						text.append(line,
						            textStart, textEnd);
					}

					textStart = ii + 1;
				}

				escaped = EscapeState.UNESCAPED;

				if (ch != 'm') {
					// Only handle 'm' command.
					break;
				}

				updateFromParams(line.substring(start, ii));
				break;
			}
		}

		// Tidy up.
		if (!discard) {
			// Buffer last state text.
			if (!textState.equals(termState)) {
				// Flush buffered text.
				addStyledString(parsed, textState, text);
				textState = new ANSIState (termState);
			}

			text.append(line, textStart, line.length());
			addStyledString(parsed, textState, text);
		}

		return parsed;
	}

	private void updateFromParams (final String paramString) {
		final String[] params = paramString.split(";");

		for (final String param: params) {
			try {
				termState.update(Integer.parseInt(param));
			} catch (final NumberFormatException e) {
				// Be generous and ignore bad param.
			}
		}
	}

	static private void addStyledString (final List<StyledString> strings,
	                                     final ANSIState textState,
	                                     final StringBuilder text) {
		if (text.length() != 0) {
			strings.add(new StyledString (textState, text));
			text.setLength(0);
		}
	}

	/**
	 * Style factory.  Computes the attributes for a given ANSI style.
	 */
	// TODO: Actually set attributes.
	static AttributeSet getStyle (final String name) {
		// Parse style name.
		final Matcher styleMatcher = stylePattern.matcher(name);

		if (!styleMatcher.matches()) {
			throw new IllegalArgumentException ("Bad ANSI style");
		}

		// Build up attribute set.
		final MutableAttributeSet attrs = new SimpleAttributeSet ();

		if (styleMatcher.group(1) != null) {
			// Flash.  Using italics instead of blinking.
			StyleConstants.setItalic(attrs, true);
		}

		if (styleMatcher.group(2) != null) {
			// Highlight.
			StyleConstants.setBold(attrs, true);
		}

		if (styleMatcher.group(3) != null) {
			// Underline.
			StyleConstants.setUnderline(attrs, true);
		}

		// Handle foreground color.
		Color fg = null;

		if (styleMatcher.group(5) != null) {
			fg = getColor(styleMatcher.group(5).charAt(0));

			if (fg == Color.BLACK) {
				fg = Color.GRAY;
			}
		}

		// Handle background color.
		Color bg = null;

		if (styleMatcher.group(6) != null) {
			final char bgChar = styleMatcher.group(6).charAt(0);

			bg = getColor(Character.toLowerCase(bgChar));
		}

		// Invert colors, if necessary.
		if (styleMatcher.group(4) != null) {
			// Invert.
			final Color fgOld = fg;
			fg = bg;
			bg = fgOld;

			if (fg == null) {
				// FIXME: Doing this correctly requires knowing
				// what the current default background color
				// is.  For now, it's always black.
				fg = Color.BLACK;
			}

			if (bg == null) {
				// FIXME: Doing this correctly requires knowing
				// what the current default foreground color
				// is.  For now, it's always white.
				bg = Color.WHITE;
			}
		}

		if (fg != null) {
			StyleConstants.setForeground(attrs, fg);
		}

		if (bg != null) {
			StyleConstants.setBackground(attrs, bg);
		}

		return attrs;
	}

	// Translate style character to color.
	static private Color getColor (final char ch) {
		switch (ch) {
		case 'x': // black
			return Color.BLACK;

		case 'r': // red
			return Color.RED;

		case 'g': // green
			return Color.GREEN;

		case 'y': // yellow
			return Color.YELLOW;

		case 'b': // blue
			return Color.BLUE;

		case 'm': // magenta
			return Color.MAGENTA;

		case 'c': // cyan
			return Color.CYAN;

		case 'w': // white
			return Color.WHITE;

		default: // ???
			throw new IllegalArgumentException ("Bad ANSI style");
		}
	}

	// ANSI terminal state.
	static private class ANSIState {
		private boolean flash;
		private boolean hilite;
		private boolean underline;
		private boolean inverse;

		private char fg;
		private char bg;

		// Construct default ANSI state.
		private ANSIState () {
			reset();
		}

		// Construct a copy of an existing ANSI state.
		private ANSIState (final ANSIState src) {
			flash = src.flash;
			hilite = src.hilite;
			underline = src.underline;
			inverse = src.inverse;

			fg = src.fg;
			bg = src.bg;
		}

		// Two ANSIStates are only equal if all values match.
		public boolean equals (final Object o) {
			if (!(o instanceof ANSIState)) {
				return false;
			}

			final ANSIState that = (ANSIState)o;

			if (flash != that.flash) return false;
			if (hilite != that.hilite) return false;
			if (underline != that.underline) return false;
			if (inverse != that.inverse) return false;

			if (fg != that.fg) return false;
			if (bg != that.bg) return false;

			return true;
		}

		// TODO: Implement hashCode(), if needed.

		// Style name corresponding to this state.
		public String toString () {
			String name = "ANSI:";

			if (flash) name += 'f';
			if (hilite) name += 'h';
			if (underline) name += 'u';
			if (inverse) name += 'i';

			if (fg != '?') name += fg;
			if (bg != '?') name += bg;

			return name;
		}

		// Update this state given an ANSI parameter value.
		private void update (final int param) {
			switch (param) {
			case 0: // normal
				reset();
				break;

			case 1: // highlight
				hilite = true;
				break;

			case 4: // underline
				underline = true;
				break;

			case 5: // flash
				flash = true;
				break;

			case 7: // inverse
				inverse = true;
				break;

			default:
				if (param >= 30 && param <= 37) {
					// Foreground color.
					fg = getChar(param - 30);
				} else if (param >= 40 && param <= 47) {
					// Background color.
					bg = getChar(param - 40);
					bg = Character.toUpperCase(bg);
				} else {
					// Be generous and ignore bad param.
				}
				break;
			}
		}

		private void reset () {
			flash = false;
			hilite = false;
			underline = false;
			inverse = false;

			fg = '?';
			bg = '?';
		}

		// Translate parameter to style character.
		static private char getChar (final int param) {
			switch (param) {
			case 0: // black
				return 'x';

			case 1: // red
				return 'r';

			case 2: // green
				return 'g';

			case 3: // yellow
				return 'y';

			case 4: // blue
				return 'b';

			case 5: // magenta
				return 'm';

			case 6: // cyan
				return 'c';

			case 7: // white
				return 'w';

			default:
				throw new IllegalArgumentException ("Bad color code");
			}
		}
	}
}
