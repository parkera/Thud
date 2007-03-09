/**
 * This Ant task extracts the current SVN revision number, and sets a
 * corresponding svn.revision property.
 */

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class MySVNInfo extends Task {
	private String revisionProperty;

	public void setRevisionproperty (final String property) {
		this.revisionProperty = property;
	}


	public void execute () throws BuildException {
		if (revisionProperty == null)
			return;

		String revision;

		try {
			revision = getInfo().getCommittedRevision().toString();
		} catch (SVNException e) {
			revision = "UNKNOWN";
		}

		getProject().setProperty(revisionProperty, revision);
	}


	private SVNInfo rootInfo;

	private SVNInfo getInfo () throws SVNException {
		if (rootInfo == null) {
			final SVNWCClient client = new SVNWCClient (null, null);

			rootInfo = client.doInfo(getProject().getBaseDir(),
			                         SVNRevision.WORKING);
		}

		return rootInfo;
	}
}
