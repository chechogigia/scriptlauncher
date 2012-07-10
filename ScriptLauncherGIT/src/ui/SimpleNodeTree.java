/* 
 * 1. Identificacion:
 *    Fichero        : CheckBoxNodeTree.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 14/12/2011
 *
 * 2. Proposito:
 *    <Escribir aqui una breve descripcion de lo que debe hacer esta clase>
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   14/12/2011 S.Ruiz   Primera implementacion.
 */
package ui;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;

public class SimpleNodeTree extends JTree {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3175638004004063343L;

	public SimpleNodeTree(Map<String, List<String>> taskMap) {
		super();
		
		setModel(createTaskMapTreeModel(taskMap));
		setRootVisible(false);
		
		setCellRenderer(new SimpleNodeRenderer());
	}
	
	public static TreeModel createTaskMapTreeModel (Map<String,List<String>> taskMap) {
		Set<String> keySet = taskMap.keySet();
		if(keySet.isEmpty()) return JTree.getDefaultTreeModel();
		
		Object rootNodes[] = new Object[keySet.size()];
		int x = 0;
		Iterator<String> authors = taskMap.keySet().iterator();
		while(authors.hasNext()) {
			String author = authors.next();
			List<String> tasks = taskMap.get(author);
			if(!tasks.isEmpty()) {
				String options [] = new String [tasks.size()];
				Iterator<String> taskIt = tasks.iterator();
				for(int i = 0; taskIt.hasNext();i++) 
					options[i] = taskIt.next();
				rootNodes [x++] = new NamedVector(author,options);
			}
		}
		return createTreeModel(new NamedVector("Incidencias", rootNodes));
	}
}

class SimpleNodeRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 3111930796319637081L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		JLabel cell = (JLabel)super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		if(leaf)
			cell.setIcon(null);
		return cell;
	}
	
}
