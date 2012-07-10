/* 
 * 1. Identificacion:
 *    Fichero        : CustomFileChooser.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 05/10/2011
 *
 * 2. Proposito:
 *    <Escribir aqui una breve descripcion de lo que debe hacer esta clase>
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   05/10/2011 S.Ruiz   Primera implementacion.
 */
package ui;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

public class CustomFileChooser extends JFileChooser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8784268250634313604L;

	public CustomFileChooser() {
		super();
	}
	
	public CustomFileChooser(String currentPackageDirectory, String title) {
		super(currentPackageDirectory);
		setDialogTitle(title);
	}

	@Override
	 public int showSaveDialog(Component parent) throws HeadlessException {
		JDialog d = new JDialog();
		int ret = super.showSaveDialog(d);
		d.removeAll();
		d.dispose();
		d = null;
		return ret;
	}
}
