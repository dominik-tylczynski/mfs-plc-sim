package pl.sapusers.mfsplc.sim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class PlcDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtPlcName;
	private JLabel lblPlcName;
	private JLabel lblPlcColor;
	private JLabel lblExecutionDelayms;
	private JTextField txtPLCcolor;
	private JTextField txtExecutionDelay;

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
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("79px"),
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("114px"),},
			new RowSpec[] {
				FormSpecs.PARAGRAPH_GAP_ROWSPEC,
				RowSpec.decode("21px"),
				FormSpecs.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("21px"),
				FormSpecs.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("21px"),}));
		{
			lblPlcName = new JLabel("PLC Name:");
			lblPlcName.setAlignmentY(0.0f);
			lblPlcName.setHorizontalAlignment(SwingConstants.LEFT);
			contentPanel.add(lblPlcName, "2, 2, fill, center");
		}
		{
			txtPlcName = new JTextField();
			lblPlcName.setLabelFor(txtPlcName);
			txtPlcName.setSize(new Dimension(5, 21));
			txtPlcName.setMaximumSize(new Dimension(5, 21));
			contentPanel.add(txtPlcName, "4, 2, left, center");
			txtPlcName.setColumns(10);
		}
		{
			lblPlcColor = new JLabel("PLC Color:");
			lblPlcColor.setHorizontalAlignment(SwingConstants.LEFT);
			contentPanel.add(lblPlcColor, "2, 4, fill, center");
		}
		{
			txtPLCcolor = new JTextField();
			txtPLCcolor.setHorizontalAlignment(SwingConstants.LEFT);
			txtPLCcolor.setEditable(false);
			txtPLCcolor.setBackground(Color.RED);
			contentPanel.add(txtPLCcolor, "4, 4, left, center");
			txtPLCcolor.setColumns(5);
		}
		{
			lblExecutionDelayms = new JLabel("Step Delay:");
			lblExecutionDelayms.setToolTipText("");
			lblExecutionDelayms.setHorizontalAlignment(SwingConstants.LEFT);
			contentPanel.add(lblExecutionDelayms, "2, 6, fill, center");
		}
		{
			txtExecutionDelay = new JTextField();
			txtExecutionDelay.setToolTipText("in miliseconds");
			lblExecutionDelayms.setLabelFor(txtExecutionDelay);
			contentPanel.add(txtExecutionDelay, "4, 6, left, center");
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
