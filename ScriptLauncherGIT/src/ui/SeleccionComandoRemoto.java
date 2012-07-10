package ui;

import java.util.List;

import strings.Messages;

/**
 * 
 * @author sruizabad
 *
 */
@SuppressWarnings("serial")
public class SeleccionComandoRemoto extends SeleccionGenerica {

	public SeleccionComandoRemoto(List<String> items) {
		super(items, Messages.getString("SeleccionComandoRemoto.0"));
	}

}
