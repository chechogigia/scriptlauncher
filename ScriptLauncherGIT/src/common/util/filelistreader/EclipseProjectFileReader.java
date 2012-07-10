package common.util.filelistreader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import common.util.LocalUtil;

import ui.Display;

public class EclipseProjectFileReader implements FileListReader {

	private static Map<String, List<String>> index; 
	
	/**
	 * No usar.
	 * @param filePath
	 * @return null
	 */
	public List<String> readFileList(String filePath)
	{	
		return null;
	}
	/**
	 * Obtiene el valor de una etiqueta de un nodo de un fichero xml
	 * @param sTag Id del tag
	 * @param eElement Elemento xml
	 * @return Valor de la etiqueta
	 */
	private String getTagValue(String sTag, Element eElement){
		NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0); 

		return nValue.getNodeValue();    
	}

	public List<String> readFileList(String filePath, String rfcName)
	{
		List<String> result = null;
		
		if(index != null)
		{
			return index.get(rfcName);			
		}
		
		return result;
	}

	public List<String> readRFCList(String filePath)
	{	
		List<String> result = null;
		index = getFilesPerRFC(filePath + "/.project");
		if(index != null)
		{
			result = new ArrayList<String>(index.keySet());
		}
		
		return result;
	}

	private Map<String, List<String>> getFilesPerRFC(String filePath)
	{
		Map<String, List<String>> index = new HashMap<String, List<String>>();

		try
		{
			File prjFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(prjFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("link");
			for (int temp = 0; temp < nList.getLength(); temp++)
			{
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					String name = getTagValue("name", eElement);
					String rfcName = name.substring(0, name.indexOf('/'));
					String path = getTagValue("location", eElement);

					List<String> files = index.get(rfcName);
					
					if(files == null)
					{
						files = new ArrayList<String>();
						index.put(rfcName, files);
					}
					
					files.add(LocalUtil.getRelativePath(path));
				}
			}
		}
		catch(Exception e)
		{
			Display.log("Error durante la composición de la lista de ficheros: " + e.getMessage());
			e.printStackTrace(System.err);
		}

		return index;
	}
}

