package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import strings.Messages;

public class LauncherConf extends JDialog {

	private static final long serialVersionUID = 4327919085194685149L;

	JPanel jContentPane = null;

	private CheckListItem[] cli = null;

	private JCheckBox check = null;
	private JCheckBox comp = null;

	private boolean cancel = true;

	public LauncherConf(List<String> list, List<String> selectedFiles) {
		super((JFrame)null, true);
		//super();

		if (list == null)
			return;
		
		cli = new CheckListItem[list.size()];
		for (int i = 0, s = list.size(); i < s; i++) {
			String item = list.get(i);
			cli[i] = new CheckListItem(item);
			cli[i].setSelected(selectedFiles.contains(item));
		}

		final JList<CheckListItem> lista = new JList<CheckListItem>(cli);
		final JCheckBox markAll = new JCheckBox(Messages.getString("LauncherConf.0")); //$NON-NLS-1$
		
		lista.setCellRenderer(new CheckListRenderer());
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lista.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				@SuppressWarnings("unchecked")
				JList<CheckListItem> list = (JList<CheckListItem>) event.getSource();
				int index = list.locationToIndex(event.getPoint());
				CheckListItem item = list.getModel().getElementAt(index);
				item.setSelected(!item.isSelected());
				
				// Not all items checked
				boolean allSelected = true;
				for (int i = 0, s = cli.length; i < s; i++) {
					if(!cli[i].isSelected()){
						markAll.setSelected(false);
						allSelected = false;
						break;
					}
				}				
				if(allSelected == true){
					markAll.setSelected(true);
				}
				
				list.repaint(list.getCellBounds(index, index));
			}
		});
		
		markAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JCheckBox source = (JCheckBox)e.getSource();
				boolean selected = source.isSelected();
				
				for(int i=0, s=cli.length;i<s;i++){
					cli[i].setSelected(selected);
					lista.repaint();
				}
			}
		});
		
		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setViewportView(lista);

		JPanel archivos = new JPanel();
		archivos.setBorder(BorderFactory.createTitledBorder(Messages.getString("LauncherConf.1"))); //$NON-NLS-1$
		archivos.setLayout(new BorderLayout());
		archivos.add(markAll, BorderLayout.NORTH);
		archivos.add(jScrollPane, BorderLayout.CENTER);

		check = new JCheckBox(Messages.getString("LauncherConf.2"), true); //$NON-NLS-1$
		comp = new JCheckBox(Messages.getString("LauncherConf.3"), false); //$NON-NLS-1$

		JPanel opciones = new JPanel();
		opciones.setBorder(BorderFactory.createTitledBorder(Messages.getString("LauncherConf.4"))); //$NON-NLS-1$
		opciones.setLayout(new GridLayout(2, 1));
		opciones.add(check);
		opciones.add(comp);

		JButton sinc = new JButton(Messages.getString("LauncherConf.5")); //$NON-NLS-1$
		sinc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel = false;
				setVisible(false);
			}
		});

		JButton canc = new JButton(Messages.getString("LauncherConf.6")); //$NON-NLS-1$
		canc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		JPanel botones = new JPanel();
		botones.setLayout(new GridLayout(2, 1));
		botones.getInsets().set(5, 5, 5, 5);
		botones.add(sinc);
		botones.add(canc);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(opciones, BorderLayout.CENTER);
		panel.add(botones, BorderLayout.EAST);

		jContentPane = new JPanel();
		jContentPane.setLayout(new BorderLayout());
		jContentPane.add(archivos, BorderLayout.CENTER);
		jContentPane.add(panel, BorderLayout.SOUTH);

		this.setSize(new Dimension(600, 300));
		this.setContentPane(jContentPane);
		this.pack();
		this.setVisible(true);
	}

	public boolean getCheck() {
		return check.isSelected();
	}

	public boolean getComp() {
		return comp.isSelected();
	}

	public boolean isCanceled() {
		return cancel;
	}

	public List<String> getItems() {
		List<String> list = new ArrayList<String>();
		for (int i = 0, s = cli.length; i < s; i++) {
			if (cli[i].isSelected())
				list.add(cli[i].toString());
		}
		return list;
	}
}

class CheckListItem {
	private String label;
	private boolean isSelected = true;

	public CheckListItem(String label) {
		this.label = label;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String toString() {
		return label;
	}
}

class CheckListRenderer extends JCheckBox implements ListCellRenderer<CheckListItem> {
	private static final long serialVersionUID = -6107946064812274076L;

	/*public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
		setEnabled(list.isEnabled());
		setSelected(((CheckListItem) value).isSelected());
		setFont(list.getFont());
		setBackground(list.getBackground());
		setForeground(list.getForeground());
		setText(value.toString());
		return this;
	}*/

	public Component getListCellRendererComponent(JList<? extends CheckListItem> list, CheckListItem value, int index, boolean isSelected, boolean cellHasFocus) {
		setEnabled(list.isEnabled());
		setSelected(value.isSelected());
		setFont(list.getFont());
		setBackground(list.getBackground());
		setForeground(list.getForeground());
		setText(value.toString());
		return this;
	}
}
