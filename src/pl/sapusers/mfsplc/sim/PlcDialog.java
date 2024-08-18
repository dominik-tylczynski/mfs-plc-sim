package pl.sapusers.mfsplc.sim;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;

public class PlcDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtPlcName;
	private JTextField txtExecutionDelay;
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
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{215, 215, 0};
		gbl_contentPanel.rowHeights = new int[]{74, 74, 74, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			lblPlcName = new JLabel("PLC Name:");
			lblPlcName.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gbc_lblPlcName = new GridBagConstraints();
			gbc_lblPlcName.ipady = 5;
			gbc_lblPlcName.ipadx = 5;
			gbc_lblPlcName.anchor = GridBagConstraints.NORTHWEST;
			gbc_lblPlcName.insets = new Insets(1, 1, 1, 1);
			gbc_lblPlcName.gridx = 0;
			gbc_lblPlcName.gridy = 0;
			contentPanel.add(lblPlcName, gbc_lblPlcName);
		}
		{
			txtPlcName = new JTextField();
			txtPlcName.setSize(new Dimension(5, 21));
			txtPlcName.setMaximumSize(new Dimension(5, 21));
			GridBagConstraints gbc_txtPlcName = new GridBagConstraints();
			gbc_txtPlcName.anchor = GridBagConstraints.NORTHWEST;
			gbc_txtPlcName.gridx = 1;
			gbc_txtPlcName.gridy = 0;
			contentPanel.add(txtPlcName, gbc_txtPlcName);
			txtPlcName.setColumns(10);
		}
		{
			JLabel lblPlcColor = new JLabel("PLC Color:");
			lblPlcColor.setHorizontalAlignment(SwingConstants.RIGHT);
			GridBagConstraints gbc_lblPlcColor = new GridBagConstraints();
			gbc_lblPlcColor.anchor = GridBagConstraints.NORTHWEST;
			gbc_lblPlcColor.insets = new Insets(0, 0, 5, 5);
			gbc_lblPlcColor.gridx = 0;
			gbc_lblPlcColor.gridy = 1;
			contentPanel.add(lblPlcColor, gbc_lblPlcColor);
		}
		{
			JButton btnPlcColor = new JButton("PLC Color");
			GridBagConstraints gbc_btnPlcColor = new GridBagConstraints();
			gbc_btnPlcColor.anchor = GridBagConstraints.NORTHWEST;
			gbc_btnPlcColor.insets = new Insets(0, 0, 5, 0);
			gbc_btnPlcColor.gridx = 1;
			gbc_btnPlcColor.gridy = 1;
			contentPanel.add(btnPlcColor, gbc_btnPlcColor);
		}
		{
			JLabel lblExecutionDelayms = new JLabel("Execution Delay [ms]:");
			lblExecutionDelayms.setHorizontalAlignment(SwingConstants.RIGHT);
			GridBagConstraints gbc_lblExecutionDelayms = new GridBagConstraints();
			gbc_lblExecutionDelayms.anchor = GridBagConstraints.NORTHWEST;
			gbc_lblExecutionDelayms.insets = new Insets(0, 0, 0, 5);
			gbc_lblExecutionDelayms.gridx = 0;
			gbc_lblExecutionDelayms.gridy = 2;
			contentPanel.add(lblExecutionDelayms, gbc_lblExecutionDelayms);
		}
		{
			txtExecutionDelay = new JTextField();
			GridBagConstraints gbc_txtExecutionDelay = new GridBagConstraints();
			gbc_txtExecutionDelay.anchor = GridBagConstraints.NORTHWEST;
			gbc_txtExecutionDelay.gridx = 1;
			gbc_txtExecutionDelay.gridy = 2;
			contentPanel.add(txtExecutionDelay, gbc_txtExecutionDelay);
			txtExecutionDelay.setColumns(4);
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
