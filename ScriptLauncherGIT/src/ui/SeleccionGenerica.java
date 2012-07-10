package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import strings.Messages;

/**
 * 
 * @author sruizabad
 *
 */
public class SeleccionGenerica extends JDialog {

	private static final long serialVersionUID = 4327919085194685149L;

	JPanel jContentPane = null;

	private JList<?> lista = null;
	private JButton selectButton = null;

	private boolean cancel = true;

	public SeleccionGenerica(List<?> items, String windowTitle) {
		this(items, windowTitle, ListSelectionModel.SINGLE_SELECTION);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SeleccionGenerica(List<?> items, String windowTitle, int listSelectionMode) {
		super((Frame) null, true);

		lista = new JList(items.toArray());

		lista.setSelectionMode(listSelectionMode);
		lista.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				if (lista.getSelectedValue() != null) {
					selectButton.setEnabled(true);
				} else {
					selectButton.setEnabled(false);
				}

				if (event.getClickCount() == 2) {
					cancel = false;
					setVisible(false);
				}
			}
		});

		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setViewportView(lista);

		JPanel archivos = new JPanel();
		archivos.setLayout(new BorderLayout());
		archivos.add(jScrollPane, BorderLayout.CENTER);

		selectButton = new JButton(Messages.getString("SeleccionGenerica.0")); //$NON-NLS-1$
		selectButton.setEnabled(false);
		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel = false;
				setVisible(false);
			}
		});

		JButton canc = new JButton(Messages.getString("SeleccionGenerica.1")); //$NON-NLS-1$
		canc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		JPanel botones = new JPanel();
		botones.setLayout(new GridLayout(1, 2));
		botones.getInsets().set(15, 15, 15, 15);
		botones.add(selectButton);
		botones.add(canc);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(botones, BorderLayout.EAST);

		jContentPane = new JPanel();
		jContentPane.setLayout(new BorderLayout());
		jContentPane.add(archivos, BorderLayout.CENTER);
		jContentPane.add(panel, BorderLayout.SOUTH);

		this.setTitle(windowTitle); //$NON-NLS-1$
		this.setSize(new Dimension(600, 300));
		this.setContentPane(jContentPane);
		this.centrarPantalla();
		this.setVisible(true);
	}

	public boolean isCanceled() {
		return cancel;
	}

	public String getSelectedValue() {
		Object selected = lista.getSelectedValue();
		if (selected != null)
			return (String) selected;
		return "";
	}
	
	public Object getListSelectedValue() {
		return lista.getSelectedValue();
	}
	
	public List<String> getSelectedValues() {
		/*Object [] selected = lista.getSelectedValues();
		if (selected != null) {
			int size = selected.length;
			String [] result = new String [size];
			for(int i=0; i<size; i++)
				result[i] = (String)selected[i];
			return Arrays.asList(result);
		}*/
		List<String> returnedList = new ArrayList<String>();
		
		List<?> selected = lista.getSelectedValuesList();
		if (selected != null && !selected.isEmpty()) {
			for (Object item : selected) {
				returnedList.add(item.toString());
			}
		}
		
		return returnedList;
	}

	private void centrarPantalla() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
	}
}
