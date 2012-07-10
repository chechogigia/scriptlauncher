/* 
 * 1. Identificacion:
 *    Fichero        : RedmineUtil.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 13/09/2011
 *
 * 2. Proposito:
 *    <Escribir aqui una breve descripcion de lo que debe hacer esta clase>
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   13/09/2011 S.Ruiz   Primera implementacion.
 */
package common.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.exolab.castor.xml.XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.taskadapter.redmineapi.NotAuthorizedException;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.bean.Version;

import ui.Display;

import common.properties.RedmineProperties;

public class RedmineUtil {

	private static RedmineManager mgr = null;

	// private static final Map<String, RMTask> taskMap = new HashMap<String,
	// RMTask>();
	private static final Map<Integer, RMTask> taskMap = new HashMap<Integer, RMTask>();

	public static Map<String, List<String>> getTaskMap(String projectId, String versionName) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();

		List<String> tasks = getTaskList(projectId, versionName);
		if (tasks.isEmpty())
			return map;

		Iterator<String> it = tasks.iterator();
		while (it.hasNext()) {
			String taskName = it.next();
			String author = getTaskAuthor(taskName);
			if (author == null || author.equals(""))
				author = "Sin asignación";

			List<String> list = (map.containsKey(author)) ? map.get(author) : new ArrayList<String>();
			list.add(taskName);
			map.put(author, list);
		}

		return map;
	}

	public static List<String> getTaskList(String projectId, String versionName) {
		return getTaskList(projectId, versionName, null);
	}

	public static List<String> getTaskList(String projectId, String versionName, List<String> validStatus) {
		List<String> list = new ArrayList<String>();
		// for (String name : taskMap.keySet()) {
		// RMTask task = taskMap.get(name);
		for (Integer id : taskMap.keySet()) {
			RMTask task = taskMap.get(id);
			if ((projectId == null || projectId.equals(task.projectId)) && (versionName == null || versionName.equals(task.versionName))
					&& (validStatus == null || validStatus.contains(task.status)))
				// list.add(name);
				list.add(task.name);
		}
		Collections.sort(list);
		return list;
	}

	public static Long getTaskId(String taskName) {
		/*
		 * RMTask t = taskMap.get(taskName); if (t == null) return null; else
		 * return new Long(t.id);
		 */

		if (taskName == null || taskName.isEmpty())
			return new Long(0);

		for (Entry<Integer, RMTask> entry : taskMap.entrySet()) {
			if (taskName.equals(entry.getValue().name)) {
				RMTask task = entry.getValue();
				return new Long(task.id);
			}
		}
		return new Long(0);
	}

	public static String getTaskAuthor(String taskName) {
		Long id = getTaskId(taskName);
		if (id.longValue() == 0)
			return null;

		// RMTask t = taskMap.get(taskName);
		RMTask t = taskMap.get(new Integer(id.intValue()));
		if (t == null)
			return null;
		else
			return t.author;
	}

	public static void initRedmine() {
		Display.debug("Init redmine");
		mgr = new RedmineManager(RedmineProperties.getServerURL(), RedmineProperties.getQueryAPI());
		initRedmineCache();
	}

	public static void closeRedmine() {
		if (mgr == null)
			mgr = null;
	}

	private static void initRedmineCache() {
		Display.debug("Init redmine cache");
		try {
			readXMLFile();
		} catch (Exception e) {
			Display.debug("Excepcion al leer la cache del redmine: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void updateRedmineCache() {
		if (mgr == null)
			initRedmine();

		Display.debug("Updating redmine cache ...");

		String projectId = RedmineProperties.getProjectId();
		if (projectId == null || projectId.isEmpty()) {
			Display.write("Property ProjectId missing ... please specify this property");
			Display.write("Redmine cache not updated");
			return;
		}

		try {
			Display.debug("Getting redmine project '%s' ...", projectId);
			List<Project> projects = mgr.getProjects();
			// TODO: En estas lineas voy a escribir como debería ser el codigo
			// si el Redmine respondiese bien a la llamada JSON para los
			// projectos (cosa que con la version 1.1.0 devel que tenemos no
			// pasa)
			// TODO: Project project = mgr.getProjectByKey(projectId);

			// TODO: comentar la siguiente linea
			if (projects != null && !projects.isEmpty()) {
				// TODO: if (project != null) {
				// TODO: Display.debug("Project encountered");
				Display.debug("Clearing redmine cache ...");
				taskMap.clear();

				// TODO: comentar las dos lineas siguientes
				for (Project project : projects) {
					if (RedmineProperties.getProjectId().equals(project.getIdentifier())) {
						Display.debug("Getting redmine issues for '%s' ...", project.getName());
						List<Issue> list = getIssuesForProject(project);
						if (list != null && !list.isEmpty()) {
							for (Issue issue : list) {
								RMTask t = new RMTask();
								t.name = issue.getSubject();
								t.id = issue.getId().longValue();
								t.projectId = project.getIdentifier();
								Version version = issue.getTargetVersion();
								t.versionName = (version != null) ? version.getName() : "";
								t.status = issue.getStatusName();
								t.idsalesforce = issue.getCustomField("Id. Salesforce");
								User assignee = issue.getAssignee();
								if (assignee != null)
									t.author = assignee.getFullName();
								// taskMap.put(issue.getSubject(), t);
								taskMap.put(issue.getId(), t);
								Display.debug("Task " + t + " added to redmine cache");
							}
						}
						// TODO: comentar las dos lineas siguientes
					}
				}
			}

			writeToFile();
			Display.debug("Redmine cache updated");
		} catch (Exception e) {
			Display.debug("Excepcion al actualizar la cache del redmine: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static List<Issue> getIssuesForProject(Project project) throws RedmineException {
		Integer queryId = RedmineProperties.getQueryId();

		try {
			return mgr.getIssues(project.getIdentifier(), queryId);
		} catch (NotAuthorizedException e) {
			Display.debug("Unable to get issues for project '%s' - Error: %s ... skiping project", project.getName(), e.getMessage());
			return new ArrayList<Issue>();
		}
	}

	public static void readXMLFile() throws ParserConfigurationException, SAXException, IOException {
		File file = new File(RedmineProperties.getCacheFilePath());
		if (!(file.exists() && file.canRead())) {
			return;
		}

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbFactory.newDocumentBuilder();
		Document doc = builder.parse(file);

		Node rootNode = doc.getFirstChild();
		NodeList nodes = rootNode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = (Node) nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && "task".equals(node.getNodeName())) {
				// projects.add(getProjectFromNode(node));
				RMTask task = getTaskFromNode(node);
				// taskMap.put(task.name, task);
				taskMap.put(new Integer((int) task.id), task);
			}
		}
	}

	private static RMTask getTaskFromNode(Node node) {
		RMTask t = new RMTask();

		NamedNodeMap nodeMap = node.getAttributes();

		if (nodeMap == null)
			return null;

		for (int i = 0; i < nodeMap.getLength(); i++) {
			Node item = nodeMap.item(i);
			String name = item.getNodeName();
			String value = item.getNodeValue();
			if ("id".equals(name))
				t.id = Long.parseLong(value);
			if ("name".equals(name))
				t.name = value;
			if ("projectId".equals(name))
				t.projectId = value;
			if ("versionName".equals(name))
				t.versionName = value;
			if ("status".equals(name))
				t.status = value;
			if ("idsalesforce".equals(name))
				t.idsalesforce = value;
			if ("author".equals(name)) {
				t.author = value;
			}
		}

		return t;
	}

	public static void writeToFile() throws XMLException {
		// Primero se hace un backup de lo que ya hay
		LocalUtil.copyfile(RedmineProperties.getCacheFilePath(), RedmineProperties.getCacheFilePath().concat(".bak"));

		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		} catch (TransformerConfigurationException e) {
			throw new XMLException("Could not create a transformer.", e);
		}
		try {
			transformer.transform(new DOMSource(createDocument()), new StreamResult(new File(RedmineProperties.getCacheFilePath())));
		} catch (TransformerException e) {
			throw new XMLException("Could not transform document.", e);
		}
	}

	private static Document createDocument() throws XMLException {
		try {
			DocumentBuilderFactory factory;
			DocumentBuilder builder;
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			Element node = doc.createElement("RedmineCache");
			// for (String name : taskMap.keySet())
			// addTaskNode(node, taskMap.get(name));
			for (Integer id : taskMap.keySet())
				addTaskNode(node, taskMap.get(id));
			doc.appendChild(node);

			return doc;
		} catch (ParserConfigurationException e) {
			throw new XMLException("Error creating new document.", e);
		}
	}

	private static void addTaskNode(Element parent, RMTask t) throws XMLException {
		Element node = parent.getOwnerDocument().createElement("task");
		node.setAttribute("name", t.name);
		node.setAttribute("id", Long.toString(t.id));
		node.setAttribute("projectId", t.projectId);
		node.setAttribute("versionName", t.versionName);
		node.setAttribute("status", t.status);
		node.setAttribute("idsalesforce", t.idsalesforce);
		node.setAttribute("author", t.author);
		parent.appendChild(node);
	}

	public static List<Project> getProjects() {
		if (mgr == null)
			mgr = new RedmineManager(RedmineProperties.getServerURL(), RedmineProperties.getQueryAPI());

		try {
			return mgr.getProjects();
		} catch (Exception e) {
			Display.debug("Error accediendo a los proyectos de Redmine: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public static List<String> getVersionsForProject(Project project) {
		List<String> versions = new ArrayList<String>();

		if (project == null || project.getIdentifier() == null)
			return versions;

		String projectVersion = project.getIdentifier();

		// Iterator<Entry<String, RMTask>> it = taskMap.entrySet().iterator();
		Iterator<Entry<Integer, RMTask>> it = taskMap.entrySet().iterator();
		while (it.hasNext()) {
			RMTask task = it.next().getValue();
			if (projectVersion.equals(task.projectId) && !versions.contains(task.versionName))
				versions.add(task.versionName);
		}

		return versions;
	}

	public static List<String> getStatusForProject(Project project) {
		List<String> status = new ArrayList<String>();

		if (project == null || project.getIdentifier() == null)
			return status;

		String projectVersion = project.getIdentifier();

		// Iterator<Entry<String, RMTask>> it = taskMap.entrySet().iterator();
		Iterator<Entry<Integer, RMTask>> it = taskMap.entrySet().iterator();
		while (it.hasNext()) {
			RMTask task = it.next().getValue();
			if (projectVersion.equals(task.projectId) && !status.contains(task.status))
				status.add(task.status);
		}

		return status;
	}

	public static String getTaskInfo(long id) {
		// for (Entry<String, RMTask> entry : taskMap.entrySet()) {
		for (Entry<Integer, RMTask> entry : taskMap.entrySet()) {
			if (entry.getValue().id == id) {
				RMTask task = entry.getValue();
				String ret = "Task #" + id + ": " + task.name + " (" + task.status + ")";
				ret += "from project " + task.projectId;
				if (!task.versionName.equals(""))
					ret += " (" + task.versionName + ")";
				return ret;
			}
		}
		return "";
	}

	public static String getSalesforceIDFromTaskId(long id) {
		// for (Entry<String, RMTask> entry : taskMap.entrySet()) {
		for (Entry<Integer, RMTask> entry : taskMap.entrySet()) {
			if (entry.getValue().id == id) {
				RMTask task = entry.getValue();
				return task.idsalesforce;
			}
		}
		return "";
	}

	public static String getNameFromTaskId(long id) {
		// for (Entry<String, RMTask> entry : taskMap.entrySet()) {
		for (Entry<Integer, RMTask> entry : taskMap.entrySet()) {
			if (entry.getValue().id == id) {
				RMTask task = entry.getValue();
				return task.name;
			}
		}
		return "";
	}
}

class RMTask {
	String name = "";
	long id = 0;
	String projectId = "";
	String versionName = "";
	String status = "";
	String idsalesforce = "";
	String author = "";

	@Override
	public String toString() {
		return "#" + id + " (" + projectId + "," + versionName + ")";
	}
}