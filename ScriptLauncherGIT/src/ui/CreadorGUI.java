package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import scripts.obsoletos.Creador;

import common.properties.PropertiesManager;

/**
 * 
 * @author sruizabad
 *
 */
public class CreadorGUI extends JDialog {
	
	private static final long serialVersionUID = 6317995643181353849L;
	
	private JComboBox<String> comboPaquete = null;
	private JTextField textIdent = null;
	private JTextField textNombre = null;
	
	public CreadorGUI ( Creador creador){
		super();
		init(creador);		
	}
	
	private void init(final Creador creador){
		String packages = PropertiesManager.getProperty("Local", "Packages");
		String [] splittedPackages = packages.split(",");
		for(int i=0, s=splittedPackages.length;i<s;i++)
			splittedPackages[i] = splittedPackages[i].trim();
		
		comboPaquete = new JComboBox<String>(splittedPackages);
		
		textIdent = new JTextField();
		
		textNombre = new JTextField();
		
		JPanel datos = new JPanel();
		datos.setBorder(BorderFactory.createTitledBorder("Datos de la incidencia"));
		datos.setLayout(new GridLayout(3,2));
		datos.add(new JLabel("Paquete:"));
		datos.add(comboPaquete);
		datos.add(new JLabel("Id. Salesforce:"));
		datos.add(textIdent);
		datos.add(new JLabel("Nombre:"));
		datos.add(textNombre);

		JButton sinc = new JButton("Crear");
		sinc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(creador.generaIncidencia((String)comboPaquete.getSelectedItem(), textIdent.getText(), textNombre.getText()))
					setVisible(false);
			}
		});

		JButton canc = new JButton("Cancelar");
		canc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		JPanel botones = new JPanel();
		botones.setLayout(new FlowLayout());
		botones.getInsets().set(5, 5, 5, 5);
		botones.add(sinc);
		botones.add(canc);

		JPanel jContentPane = new JPanel();
		jContentPane.setLayout(new BorderLayout());
		jContentPane.add(datos, BorderLayout.CENTER);
		jContentPane.add(botones, BorderLayout.SOUTH);

		this.setSize(new Dimension(400, 150));
		this.setContentPane(jContentPane);
		this.setTitle("Sincronizador");
		this.centrarPantalla();
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setVisible(true);
	}
	
	private void centrarPantalla() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
	}
}