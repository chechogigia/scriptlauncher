package scripts.common;

import java.util.HashMap;
import java.util.Map;

import launcher.OptionManager;

import common.util.ScriptWrapper;

import strings.Messages;
import ui.Display;

import common.Connection;

public abstract class Script {

	protected Connection conn;
	private Map<String, Object> data;

	public Script() {
	}

	final public void terminate() {
		releaseResources();
		if (conn != null)
			conn.closeSession();
		Display.write("Script finalizado");
	}

	protected void releaseResources() {
	}

	public abstract boolean init(OptionManager om);

	final public void execute() {
		Display.write("Ejecutando script '%s'", getName());
		exec();
	}
	
	public abstract void exec();

	public abstract String getName();
	
	public boolean needsConnection() {
		return false;
	}

	public static Script create(OptionManager om) throws Exception {

		ScriptWrapper sw = om.getScriptWrapper();
		if (sw == null)
			throw new Exception("Sin script seleccionado");
		
		Script script = sw.getInstanceOfScript();
		if (script.needsConnection()) {
			Connection conn = new Connection(om.getEnvironment());
			if (!conn.initSession()) {
				script.releaseResources();
				throw new Exception("No se puede conectar con el entorno seleccionado");
			}
			else
				script.setConnection(conn);
		}
		script.init(om);

		return script;
	}

	protected static Map<String, Object> createParamsMap(String name1, Object value1, String name2, Object value2, String name3,
			Object value3, String name4, Object value4) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (name1 != null)
			map.put(name1, value1);
		if (name2 != null)
			map.put(name2, value2);
		if (name3 != null)
			map.put(name3, value3);
		if (name4 != null)
			map.put(name4, value4);
		return map;
	}

	protected static Map<String, Object> createParamsMap(String name1, Object value1, String name2, Object value2, String name3,
			Object value3) {
		return createParamsMap(name1, value1, name2, value2, name3, value3, null, null);
	}

	protected static Map<String, Object> createParamsMap(String name1, Object value1, String name2, Object value2) {
		return createParamsMap(name1, value1, name2, value2, null, null, null, null);
	}

	protected static Map<String, Object> createParamsMap(String name1, Object value1) {
		return createParamsMap(name1, value1, null, null, null, null, null, null);
	}

	protected static Map<String, Object> createParamsMap() {
		return createParamsMap(null, null, null, null, null, null, null, null);
	}

	protected void setParameters(Map<String, Object> params) {
		this.data = params;
	}

	@SuppressWarnings("rawtypes")
	protected Object getParameter(String key, final Class dataType, boolean nullAllowed) {
		if (key == null || dataType == null) {
			Display.write(Messages.getString("Script.0"), key, dataType); //$NON-NLS-1$
			System.exit(-1);
		}

		Object value = data.get(key);
		if (value == null && !nullAllowed) {
			Display.write(Messages.getString("Script.1"), key, dataType, value); //$NON-NLS-1$
			System.exit(-1);
		}

		if (value == null && nullAllowed)
			return null;

		if (!dataType.isInstance(value)) {
			Display.write(Messages.getString("Script.2"), key, dataType, value); //$NON-NLS-1$
			System.exit(-1);
		}

		return value;
	}

	@SuppressWarnings("rawtypes")
	protected Object getParameter(String key, final Class dataType) {
		return getParameter(key, dataType, false);
	}

	protected String getStringParameter(String key) {
		return (String) getParameter(key, String.class, false);
	}

	public void setParameter(String key, Object value) {
		if (key != null)
			data.put(key, value);
	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}
}
