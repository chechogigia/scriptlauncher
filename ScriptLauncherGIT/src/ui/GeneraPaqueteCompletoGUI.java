package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import common.util.LocalUtil;
import common.util.filelistreader.FileListReader;

import strings.Messages;
import ui.util.DirectoryFileFilter;

public class GeneraPaqueteCompletoGUI extends JDialog {

	private static final long serialVersionUID = 4327919085194685149L;

	JPanel jContentPane = null;

	private DirectoriesCheckItem[] cli = null;

	private boolean cancel = true;

	public GeneraPaqueteCompletoGUI(File rootDirectory) {
		super((Frame)null, true);

		if (rootDirectory == null)
			return;

		FileListReader reader = LocalUtil.getDefaultReader();
		List<String> rfcs = reader.readRFCList(rootDirectory.getAbsolutePath());
		File[] dirNames = rootDirectory.listFiles(new DirectoryFileFilter());

		if ((dirNames == null) || (dirNames.length == 0))
			return;

		cli = new DirectoriesCheckItem[rfcs.size()];
		for (int i = 0, s = rfcs.size(); i < s; i++) {
			String item = rfcs.get(i);
			cli[i] = new DirectoriesCheckItem(item);
			cli[i].setSelected(true);
		}

		final JList<DirectoriesCheckItem> lista = new JList<DirectoriesCheckItem>(cli);
		final JCheckBox markAll = new JCheckBox(Messages.getString("GeneraPaqueteCompletoGUI.0")); //$NON-NLS-1$
		markAll.setSelected(true);

		lista.setCellRenderer(new DirectoriesCheckRenderer());
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lista.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				@SuppressWarnings("unchecked")
				JList<DirectoriesCheckItem> list = (JList<DirectoriesCheckItem>) event.getSource();
				int index = list.locationToIndex(event.getPoint());
				DirectoriesCheckItem item = list.getModel().getElementAt(index);
				item.setSelected(!item.isSelected());

				// Not all items checked
				boolean allSelected = true;
				for (int i = 0, s = cli.length; i < s; i++) {
					if (!cli[i].isSelected()) {
						markAll.setSelected(false);
						allSelected = false;
						break;
					}
				}
				if (allSelected == true) {
					markAll.setSelected(true);
				}

				list.repaint(list.getCellBounds(index, index));
			}
		});

		markAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();
				boolean selected = source.isSelected();

				for (int i = 0, s = cli.length; i < s; i++) {
					cli[i].setSelected(selected);
					lista.repaint();
				}
			}
		});

		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setViewportView(lista);

		JPanel archivos = new JPanel();
		archivos.setBorder(BorderFactory.createTitledBorder(Messages.getString("GeneraPaqueteCompletoGUI.1"))); //$NON-NLS-1$
		archivos.setLayout(new BorderLayout());
		archivos.add(markAll, BorderLayout.NORTH);
		archivos.add(jScrollPane, BorderLayout.CENTER);

		JButton sinc = new JButton(Messages.getString("GeneraPaqueteCompletoGUI.2")); //$NON-NLS-1$
		sinc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel = false;
				setVisible(false);
			}
		});

		JButton canc = new JButton(Messages.getString("GeneraPaqueteCompletoGUI.3")); //$NON-NLS-1$
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
		panel.add(botones, BorderLayout.EAST);

		jContentPane = new JPanel();
		jContentPane.setLayout(new BorderLayout());
		jContentPane.add(archivos, BorderLayout.CENTER);
		jContentPane.add(panel, BorderLayout.SOUTH);

		this.setSize(new Dimension(600, 300));
		this.setContentPane(jContentPane);
		this.setVisible(true);
	}

	public boolean isCanceled() {
		return cancel;
	}

	public String[] getSelectedFiles() {
		List<String> list = new ArrayList<String>();
		for (int i = 0, s = cli.length; i < s; i++) {
			if (cli[i].isSelected())
				list.add(cli[i].toString());
		}
		String[] result = new String[1];
		list.toArray(result);
		return result;
	}
}

class DirectoriesCheckItem {
	private String label;
	private boolean isSelected = true;

	public DirectoriesCheckItem(String label) {
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

class DirectoriesCheckRenderer extends JCheckBox implements ListCellRenderer<DirectoriesCheckItem> {
	private static final long serialVersionUID = -6107946064812274076L;

	/*public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
		setEnabled(list.isEnabled());
		setSelected(((DirectoriesCheckItem) value).isSelected());
		setFont(list.getFont());
		setBackground(list.getBackground());
		setForeground(list.getForeground());
		setText(value.toString());
		return this;
	}*/

	public Component getListCellRendererComponent(JList<? extends DirectoriesCheckItem> list, DirectoriesCheckItem value, int index, boolean isSelected,
			boolean cellHasFocus) {
		setEnabled(list.isEnabled());
		setSelected(value.isSelected());
		setFont(list.getFont());
		setBackground(list.getBackground());
		setForeground(list.getForeground());
		setText(value.toString());
		return this;
	}
}