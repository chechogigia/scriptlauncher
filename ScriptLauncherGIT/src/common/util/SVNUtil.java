/* 
 * 1. Identificacion:
 *    Fichero        : SVNUtil.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 07/09/2011
 *
 * 2. Proposito:
 *    <Escribir aqui una breve descripcion de lo que debe hacer esta clase>
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   07/09/2011 S.Ruiz   Primera implementacion.
 */
package common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ui.Display;

import common.properties.SVNProperties;

public class SVNUtil {

	private static long lastRevision = 0;

	private static List<Revision> revisions = new ArrayList<Revision>();

	public static long getLastRevision() {
		return lastRevision;
	}
	
	private static SVNRepository repository;
	
	public static List<SVNChange> getChangesForTask(String taskId) {
		Display.debug("getChangesForTask - entra con taskId %s", taskId);
		List<SVNChange> changes = new ArrayList<SVNChange>();
		
		if(taskId == null || taskId.isEmpty())
			return changes;		
		
		for(Revision r: revisions) {
			if(taskId.equals(r.taskId)) {
				Display.debug("getChangesForTask - +revision %s -> num changes %s", r.name, r.changes.size());
				changes.addAll(r.changes);
			}
		}
		
		return changes;
	}

	public static SVNRepository initRepository() {
		DAVRepositoryFactory.setup();
		try {
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(SVNProperties.getRepositoryURL()));
			repository
					.setAuthenticationManager(new BasicAuthenticationManager(SVNProperties.getUserName(), SVNProperties.getUserPassword()));
		} catch (Exception ex) {
			return null;
		}

		initSVNCache(repository);

		return repository;
	}

	public static void initSVNCache(SVNRepository repository) {
		Display.debug("Init SVNCache");
		try {
			readXMLFile();

			Display.debug("SVNCache last revision: " + lastRevision);
			
			long head = repository.getLatestRevision();
			Display.debug("Repository last revision: " + head);
			if (head > lastRevision) {
				Display.debug("SVNCache update required");
				@SuppressWarnings("unchecked")
				Collection<SVNLogEntry> logEntries = repository.log(new String[] { "" }, null, lastRevision, head, true, true);
				for (Iterator<SVNLogEntry> entries = logEntries.iterator(); entries.hasNext();) {
					SVNLogEntry logEntry = entries.next();
					String message = logEntry.getMessage();
					if (message.contains("refs #")) {
						Revision r = new Revision();
						int index = message.indexOf("refs #");
						if (index > 0) {
							int end = index + "refs #".length();
							while(end < message.length() && !Character.isWhitespace(message.charAt(end)))
								end++;
							if (end > 0 && end > index + 6) {
								String taskid = message.substring(index + 6, end);
								Display.debug("Adding to SVNCache file revision " + logEntry.getRevision() + " for task = " + taskid);
								if (taskid != null) {
									r.taskId = taskid;
								}
							}
						}
						r.name = logEntry.getRevision();
						if (logEntry.getChangedPaths().size() > 0) {
							Set<?> changedPathsSet = logEntry.getChangedPaths().keySet();
							for (Iterator<?> changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
								SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
								SVNChange c = new SVNChange();
								c.type = entryPath.getType();
								c.path = entryPath.getPath();
								r.changes.add(c);
							}
						}
						revisions.add(r);
					}					
				}
				lastRevision = head;
				writeToFile();
			}

		} catch (Exception ex) {
			return;
		}
	}

	public static void writeToFile() throws XMLException {
		// Primero se hace un backup de lo que ya hay
		LocalUtil.copyfile(SVNProperties.getCacheFilePath(), SVNProperties.getCacheFilePath().concat(".bak"));
		
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		} catch (TransformerConfigurationException e) {
			throw new XMLException("Could not create a transformer.", e);
		}
		try {
			transformer.transform(new DOMSource(createDocument()), new StreamResult(new File(SVNProperties.getCacheFilePath())));
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

			Element node = doc.createElement("SVNCache");
			node.setAttribute("lastrevision", Long.toString(lastRevision));
			for (Revision rev : revisions)
				addRevisionNode(node, rev);
			doc.appendChild(node);

			return doc;
		} catch (ParserConfigurationException e) {
			throw new XMLException("Error creating new document.", e);
		}
	}

	private static void addRevisionNode(Element parent, Revision revision) throws XMLException {
		Element node = parent.getOwnerDocument().createElement("revision");
		node.setAttribute("name", Long.toString(revision.name));
		node.setAttribute("taskid", revision.taskId);
		for (SVNChange c : revision.changes) {
			addChangeNode(node, c);
		}
		parent.appendChild(node);
	}

	private static void addChangeNode(Element parent, SVNChange change) throws XMLException {
		Element node = parent.getOwnerDocument().createElement("file");
		node.setAttribute("type", Character.toString(change.type));
		node.setAttribute("path", change.path);
		parent.appendChild(node);
	}

	public static void readXMLFile() throws Exception {
		File file = new File(SVNProperties.getCacheFilePath());
		if (!(file.exists() && file.canRead())) {
			return;
		}

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbFactory.newDocumentBuilder();
		Document doc = builder.parse(file);

		Node rootNode = doc.getFirstChild();

		NamedNodeMap nodeMap = rootNode.getAttributes();
		for (int i = 0; i < nodeMap.getLength(); i++) {
			Node node = nodeMap.item(i);
			String name = node.getNodeName();
			String value = node.getNodeValue();
			if ("lastrevision".equals(name))
				lastRevision = Long.parseLong(value);
		}

		NodeList nodes = rootNode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = (Node) nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				revisions.add(getRevisionFromNode(node));
			}
		}

		/*for (Revision rev : revisions) {
			System.out.println("Revision: " + rev.name + " para la tarea " + rev.taskId);
			for (SVNChange c : rev.changes) {
				System.out.println(" · " + c.type + " " + c.path);
			}
		}*/
	}

	private static Revision getRevisionFromNode(Node node) {
		Revision r = new Revision();

		NamedNodeMap nodeMap = node.getAttributes();
		for (int i = 0; i < nodeMap.getLength(); i++) {
			Node item = nodeMap.item(i);
			String name = item.getNodeName();
			String value = item.getNodeValue();
			if ("name".equals(name))
				r.name = Long.parseLong(value);
			if ("taskid".equals(name))
				r.taskId = value;
		}

		NodeList nodes = node.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node change = (Node) nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				SVNChange c = getChangeFromNode(change);
				if (c != null)
					r.changes.add(c);
			}
		}

		return r;
	}

	private static SVNChange getChangeFromNode(Node node) {
		SVNChange c = new SVNChange();

		NamedNodeMap nodeMap = node.getAttributes();

		if (nodeMap == null)
			return null;

		for (int i = 0; i < nodeMap.getLength(); i++) {
			Node item = nodeMap.item(i);
			String name = item.getNodeName();
			String value = item.getNodeValue();
			if ("type".equals(name))
				c.type = value.charAt(0);
			if ("path".equals(name))
				c.path = value;
		}

		return c;
	}

	
	public static String getFileFromSVN(String fileName) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();	
			repository.getFile(fileName, getLastRevision(), null, os);
			return os.toString();
		} catch (SVNException e) {
			e.printStackTrace();
		}
		return "";
	}
}

class Revision {
	long name = 0;
	String taskId = "";
	List<SVNChange> changes = new ArrayList<SVNChange>();
}
