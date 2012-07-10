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
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import common.properties.RedmineProperties;
import common.util.RedmineUtil;

import strings.Messages;

/**
 * 
 * @author sruizabad
 * 
 */
public class SeleccionIncidenciaRedmine extends JDialog {

	private static final long serialVersionUID = 4327919085194685149L;

	JPanel jContentPane = null;

	private JList<String> lista = null;
	private JTree tree = null;
	private JButton selectButton = null;
	private JCheckBox extraValueCheckBox = null;

	private boolean cancel = true;

	public SeleccionIncidenciaRedmine(List<String> items) {
		this(items, ListSelectionModel.SINGLE_SELECTION);
	}

	public SeleccionIncidenciaRedmine(Map<String, List<String>> taskMap, final boolean multipleSelection) {
		this(taskMap, multipleSelection, false);
	}

	public SeleccionIncidenciaRedmine(Map<String, List<String>> taskMap, final boolean multipleSelection, final boolean useExtraValue) {
		super((Frame) null, true);

		tree = (multipleSelection) ? new CheckBoxNodeTree(taskMap) : new SimpleNodeTree(taskMap);

		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				if (tree.getSelectionPath() != null && tree.getSelectionPath().getPathCount() == 3) {
					selectButton.setEnabled(true);
				} else {
					selectButton.setEnabled(false);
				}

				if (event.getClickCount() == 2 && tree.getSelectionPath().getPathCount() == 3) {
					cancel = false;
					setVisible(false);
				}
			}
		});

		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setViewportView(tree);

		JPanel archivos = new JPanel();
		archivos.setLayout(new BorderLayout());
		archivos.add(jScrollPane, BorderLayout.CENTER);

		JButton updateButton = new JButton("Update List"); //$NON-NLS-1$
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RedmineUtil.updateRedmineCache();
				TreeModel treeModel = (multipleSelection) ? CheckBoxNodeTree.createTaskMapTreeModel(RedmineUtil.getTaskMap(RedmineProperties.getProjectId(),
						RedmineProperties.getCurrentVersion())) : SimpleNodeTree.createTaskMapTreeModel(RedmineUtil.getTaskMap(
						RedmineProperties.getProjectId(), RedmineProperties.getCurrentVersion()));
				tree.setModel(treeModel);
				tree.setRootVisible(false);
				repaint();
			}
		});

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
		botones.setLayout(new GridLayout(1, 3));
		botones.getInsets().set(15, 15, 15, 15);
		botones.add(updateButton);
		botones.add(selectButton);
		botones.add(canc);

		extraValueCheckBox = new JCheckBox("Crear como rider");

		JPanel extraValuePanel = new JPanel();
		extraValuePanel.setLayout(new GridLayout(1, 1));
		extraValuePanel.getInsets().set(15, 15, 15, 15);
		extraValuePanel.add(extraValueCheckBox);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(botones, BorderLayout.EAST);
		if (useExtraValue)
			panel.add(extraValuePanel, BorderLayout.WEST);

		jContentPane = new JPanel();
		jContentPane.setLayout(new BorderLayout());
		jContentPane.add(archivos, BorderLayout.CENTER);
		jContentPane.add(panel, BorderLayout.SOUTH);

		this.setTitle(Messages.getString("SeleccionIncidencia.0")); //$NON-NLS-1$
		this.setSize(new Dimension(600, 300));
		this.setContentPane(jContentPane);
		this.centrarPantalla();
		this.setVisible(true);
	}

	public SeleccionIncidenciaRedmine(List<String> items, int selectionMode) {
		super((Frame) null, true);

		lista = new JList<String>(items.toArray(new String[0]));

		lista.setSelectionMode(selectionMode);
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

		JButton updateButton = new JButton("Update List"); //$NON-NLS-1$
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RedmineUtil.updateRedmineCache();
				DefaultListModel<String> model = new DefaultListModel<String>();
				for (String taskName : RedmineUtil.getTaskList(RedmineProperties.getProjectId(), RedmineProperties.getCurrentVersion()))
					model.addElement(taskName);
				lista.setModel(model);
				repaint();
			}
		});

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
		botones.setLayout(new GridLayout(1, 3));
		botones.getInsets().set(15, 15, 15, 15);
		botones.add(updateButton);
		botones.add(selectButton);
		botones.add(canc);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(botones, BorderLayout.EAST);

		jContentPane = new JPanel();
		jContentPane.setLayout(new BorderLayout());
		jContentPane.add(archivos, BorderLayout.CENTER);
		jContentPane.add(panel, BorderLayout.SOUTH);

		this.setTitle(Messages.getString("SeleccionIncidencia.0")); //$NON-NLS-1$
		this.setSize(new Dimension(600, 300));
		this.setContentPane(jContentPane);
		this.centrarPantalla();
		this.setVisible(true);
	}

	public boolean isCanceled() {
		return cancel;
	}

	public String getSelectedValue() {
		Object selected = (lista != null) ? lista.getSelectedValue() : tree.getSelectionPath().getLastPathComponent().toString();
		if (selected != null)
			return (String) selected;
		return "";
	}

	public String[] getSelectedValues() {
		if (lista != null) {
			/*
			 * Object[] selected = lista.getSelectedValues(); if (selected !=
			 * null) { int s = selected.length; String[] ret = new String[s];
			 * for (int i = 0; i < s; i++) ret[i] = (String) selected[i]; return
			 * ret; }
			 */
			List<String> selected = lista.getSelectedValuesList();
			if (selected != null && !selected.isEmpty())
				return selected.toArray(new String[0]);
		} else if (tree != null) {
			if (tree instanceof CheckBoxNodeTree) {
				CheckBoxNodeTree cbnt = (CheckBoxNodeTree) tree;
				return cbnt.getSelectedValues();
			} else {
				TreePath[] paths = tree.getSelectionPaths();
				if (paths != null) {
					int s = paths.length;
					String[] ret = new String[s];
					for (int i = 0; i < s; i++)
						ret[i] = (String) paths[i].getLastPathComponent().toString();
					return ret;
				}
			}
		}
		return new String[0];
	}

	public boolean getExtendedValue() {
		return extraValueCheckBox.isSelected();
	}

	private void centrarPantalla() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
	}
}
