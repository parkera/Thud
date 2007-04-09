//
//  DataStore.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Generic data storage facility for THUD.  Provides a mechanism to load and
 * store data from an application-specific namespace. (Generally, a directory
 * in the user's home directory called "THUD", but the interface is meant to be
 * fairly generic.  In particular, we may want to support the case in the
 * future where file system access is constrained because we're an untrusted
 * Web Start application, and use the javax.jnlp APIs instead.)
 *
 * This implementation will also fall back, in the case of input, to retrieving
 * resources from the original codebase.
 *
 * FIXME: The user should be able to set this path the first time, in case they
 * want a different one.  We need to think about moving one path to a new one,
 * too, in case the user changes path.  The latter feature, probably not.
 */
public class DataStore {
	// TODO: It'd be nice to put these in the right place for various
	// platforms, just as a convenience to the user.  They'll always be
	// able to change it in the preferences later.
	public final File defaultDir = new File (System.getProperty("user.home"), "THUD");

	private File backingDir = null;

	/**
	 * Construct data store.  Store location will be taken from the
	 * preferences.
	 */
	public DataStore () {
		// TODO: We want to be able to change this in preferences.
		try {
			setDir(defaultDir);
		} catch (IOException e) {
			System.err.println("Couldn't initialize data store: " + e);
		}
	}

	private void setDir (final File newDir) throws IOException {
		// Initialize backing store.
		if (newDir.exists()) {
			// Check if there's a usable existing directory.
			if (!newDir.isDirectory()) {
				throw new IOException ("Not a directory: " + newDir);
			}

			if (!newDir.canWrite() || !newDir.canRead()) {
				throw new IOException ("Access denied: " + newDir);
			}
		} else {
			// Create new directory (and any needed ancestors).
			if (!newDir.mkdirs()) {
				throw new IOException ("Can't create directory: " + newDir);
			}
		}

		// Update in-memory objects.
		backingDir = newDir;
	}

	/**
	 * Open stream for input.  Returns null if the InputStream couldn't be
	 * created, for whatever reason.
	 */
	public InputStream getInputStream (final String name) {
		if (backingDir != null) {
			try {
				return new FileInputStream (new File (backingDir, name));
			} catch (FileNotFoundException e) {
			}
		}

		// Fall back to retrieving as a resource.
		return getClass().getClassLoader().getResourceAsStream(name);
	}

	/**
	 * Open stream for output.  Returns null if the OutputStream couldn't
	 * be created, for whatever reason.
	 */
	public OutputStream getOutputStream (final String name) {
		if (backingDir != null) {
			try {
				return new FileOutputStream (new File (backingDir, name));
			} catch (FileNotFoundException e) {
			}
		}

		// No fall back mechanism.
		return null;
	}

	/**
	 * Convenience method to get a (buffered) Reader instead of an
	 * InputStream.
	 */
	public Reader getReader (final String name) {
		// TODO: Think about the buffering some more.
		final InputStream input = getInputStream(name);

		if (input == null) {
			return null;
		}

		return new BufferedReader (new InputStreamReader (input));
	}

	/**
	 * Convenience method to get a (buffered) Writer instead of an
	 * OutputStream.
	 */
	public Writer getWriter (final String name) {
		// TODO: Think about the buffering some more.
		final OutputStream output = getOutputStream(name);

		if (output == null) {
			return null;
		}

		return new BufferedWriter (new OutputStreamWriter (output));
	}
}
