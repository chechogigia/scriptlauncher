package scripts.obsoletos;

import java.util.List;

import scripts.common.Script;

import launcher.OptionManager;

import common.CommandExecuter;
import common.properties.PropertiesManager;

public class RemoteCommands extends Script {
	
	@SuppressWarnings("unchecked")
	public void exec() {
		List<String> commandList = (List<String>)getParameter("CommandList", List.class);
		
		if (commandList.contains("cucust")) {
			System.out.println("Compilado upstream custom");
			new CommandExecuter(conn, PropertiesManager.getProperty(conn.getEnvironment(), "compileUpstreamCommand"), true);
		}

		if (commandList.contains("cucore")) {
			System.out.println("Compilado upstream core");
			new CommandExecuter(conn, PropertiesManager.getProperty(conn.getEnvironment(), "compileUpstreamCoreCommand"), true);
		}

		if (commandList.contains("chu")) {
			System.out.println("Checkmeta en upstream");
			new CommandExecuter(conn, PropertiesManager.getProperty(conn.getEnvironment(), "upstreamcommand"));
		}

		if (commandList.contains("chb")) {
			System.out.println("Checkmeta en buyer");
			new CommandExecuter(conn, PropertiesManager.getProperty(conn.getEnvironment(), "buyercommand"));
		}

		if (commandList.contains("iu")) {
			System.out.println("Initdb en upstream");
			new CommandExecuter(conn, PropertiesManager.getProperty(conn.getEnvironment(), "initdbUpstreamCommand"));
		}

		if (commandList.contains("ib")) {
			System.out.println("Initdb en buyer");
			new CommandExecuter(conn, PropertiesManager.getProperty(conn.getEnvironment(), "initdbBuyerCommand"));
		}
	}

	@Override
	public String getName() {
		return "Comandos en remoto";
	}

	@Override
	public boolean init(OptionManager om) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean needsConnection() {
		return true;
	}
}
