package pl.sapusers.mfsplc.sim;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoRecordField;
import com.sap.conn.jco.JCoRecordFieldIterator;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class TelegramDialog extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			new TelegramDialog(JCo.createStructure(JCoDestinationManager.getDestination("S4D").getRepository()
					.getStructureDefinition("ZMFS_TELETOTAL")).getRecordFieldIterator(), false, "Test title");

//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */

	private HashMap<String, JTextField> fieldsMap = new HashMap<>();

	public TelegramDialog(JCoRecordFieldIterator fieldsIterator, boolean displayOnly, String title) {
		setTitle(title);
		setModal(!displayOnly);
		SpringLayout springLayout = new SpringLayout();
		JPanel p = new JPanel(springLayout);

		setBounds(100, 100, 700, 800);
		getContentPane().setLayout(new BorderLayout());
		p.setBorder(new EmptyBorder(5, 5, 5, 5));

		JScrollPane sp = new JScrollPane();
		sp.setViewportBorder(null);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setViewportView(p);

		getContentPane().add(sp, BorderLayout.CENTER);

		Integer PADDING = 6;
		Component topAnchor = null;
		int offset = 1;
		
		while (fieldsIterator.hasNextField()) {

			JCoRecordField field = fieldsIterator.nextRecordField();

			JLabel label = new JLabel(field.getDescription() + " [" + String.format("%03d", offset) + "-"
					+ String.format("%03d", offset + field.getLength() - 1) + "]", JLabel.TRAILING);
			offset = offset + field.getLength();
			p.add(label);

			if (topAnchor == null) {
				springLayout.putConstraint(SpringLayout.NORTH, label, PADDING, SpringLayout.NORTH, p);
			} else {
				springLayout.putConstraint(SpringLayout.NORTH, label, PADDING, SpringLayout.SOUTH, topAnchor);
			}

			springLayout.putConstraint(SpringLayout.WEST, label, PADDING, SpringLayout.WEST, p);

			JTextField textField = new JTextField(field.getLength()+1);
			textField.setName(field.getName());
			textField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

			textField.setMaximumSize(textField.getPreferredSize());
			textField.setMinimumSize(textField.getPreferredSize());
			textField.setDocument(new JFieldTextValidator(field.getLength(), 0));
			textField.setEditable(!displayOnly);

			label.setLabelFor(textField);
			p.add(textField);
			textField.setText(field.getString());

			fieldsMap.put(textField.getName(), textField);

			if (topAnchor == null) {
				springLayout.putConstraint(SpringLayout.NORTH, textField, PADDING, SpringLayout.NORTH, p);
			} else {
				springLayout.putConstraint(SpringLayout.NORTH, textField, PADDING, SpringLayout.SOUTH, topAnchor);
			}
			springLayout.putConstraint(SpringLayout.WEST, textField, PADDING, SpringLayout.EAST, label);

			topAnchor = label;
		}

		// set labels width
		Spring maxWidth = Spring.constant(0);
		fieldsIterator.reset();
		int i = 0;
		while (fieldsIterator.hasNextField()) {
			fieldsIterator.nextField();
			maxWidth = Spring.max(maxWidth, springLayout.getConstraints(p.getComponent(i * 2)).getWidth());
			i++;
		}

		fieldsIterator.reset();
		i = 0;
		while (fieldsIterator.hasNextField()) {
			fieldsIterator.nextField();
			springLayout.getConstraints(p.getComponent(i * 2)).setWidth(maxWidth);
			i++;
		}

		// set size of spring layout panel
		Spring sizeX = Spring.constant(0);
		Spring sizeY = Spring.constant(0);
		fieldsIterator.reset();
		i = 0;
		while (fieldsIterator.hasNextField()) {
			fieldsIterator.nextField();

			Spring x = Spring.constant(0);
			x = Spring.sum(x, springLayout.getConstraints(p.getComponent(i * 2)).getWidth()); // label
			x = Spring.sum(x, springLayout.getConstraints(p.getComponent(i * 2 + 1)).getWidth()); // field
			x = Spring.sum(x, Spring.constant(PADDING)); // padding between
			sizeX = Spring.max(x, sizeX);

			sizeY = Spring.sum(sizeY, springLayout.getConstraints(p.getComponent(i * 2)).getHeight()); // label, field
			sizeY = Spring.sum(sizeY, Spring.constant(PADDING)); // padding between
			i++;
		}
		sizeX = Spring.sum(sizeX, Spring.constant(PADDING)); // padding before line
		sizeX = Spring.sum(sizeX, Spring.constant(PADDING)); // padding after line
		sizeY = Spring.sum(sizeY, Spring.constant(PADDING)); // padding at the top

		springLayout.getConstraints(p).setConstraint(SpringLayout.HEIGHT, sizeY);
		springLayout.getConstraints(p).setConstraint(SpringLayout.WIDTH, sizeX);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						fieldsIterator.reset();
						while (fieldsIterator.hasNextField()) {
							JCoField field = fieldsIterator.nextField();
							JTextField textField = fieldsMap.get(field.getName());
							field.setValue(textField.getText());
						}
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				if (displayOnly)
					okButton.setEnabled(false);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	public JTextField getFieldByName(String fieldName) {
		return fieldsMap.get(fieldName);
	}
}
