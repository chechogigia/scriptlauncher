package ui;

import java.util.List;

import strings.Messages;

/**
 * 
 * @author sruizabad
 *
 */
@SuppressWarnings("serial")
public class SeleccionEntorno extends SeleccionGenerica {

	public SeleccionEntorno(List<String> items) {
		super(items, Messages.getString("SeleccionEntorno.0"));
	}
	
}
