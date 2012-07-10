package common;

import java.util.Iterator;
import java.util.List;

import common.util.SessionUtil;


public class CommandExecuter {

	public CommandExecuter(Connection conn, List<String> commandList) {
		if ((commandList != null) && (commandList.size() > 0)) {

			for (Iterator<String> it = commandList.iterator(); it.hasNext();) {
				SessionUtil.execRemoteCommand(conn.getSession(), it.next());
			}
		}
	}

	public CommandExecuter(Connection conn, String command) {
		this(conn, command, false);
	}

	public CommandExecuter(Connection conn, String command, boolean showErr) {
		//if (!showErr)
			SessionUtil.execRemoteCommand(conn.getSession(), command);
		//else
		//	SessionUtil.execRemoteCommandShowErr(conn.getSession(), command);
	}

}
