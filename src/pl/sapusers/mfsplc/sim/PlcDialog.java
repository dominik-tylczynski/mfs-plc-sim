package pl.sapusers.mfsplc.sim;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class PlcDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtPlcName;
	private JLabel lblPlcName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			PlcDialog dialog = new PlcDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public PlcDialog() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		{
			lblPlcName = new JLabel("PLC Name:");
			lblPlcName.setAlignmentY(0.0f);
			lblPlcName.setHorizontalAlignment(SwingConstants.LEFT);
			contentPanel.add(lblPlcName);
		}
		{
			txtPlcName = new JTextField();
			lblPlcName.setLabelFor(txtPlcName);
			txtPlcName.setSize(new Dimension(5, 21));
			txtPlcName.setMaximumSize(new Dimension(5, 21));
			contentPanel.add(txtPlcName);
			txtPlcName.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
