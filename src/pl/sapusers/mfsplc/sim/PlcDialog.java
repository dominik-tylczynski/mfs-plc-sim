package pl.sapusers.mfsplc.sim;

import javax.swing.*;
import java.awt.*;

public class PlcDialog {

    public static void main(String[] args) {
        // Create a panel to hold all the fields and labels
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adds padding between elements
        gbc.anchor = GridBagConstraints.WEST; // Align labels and fields to the left

        // Add a label and text field for "Name"
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0; // Prevent horizontal stretching
        JTextField nameField = new JTextField(10); // Shorter field (10 columns)
        panel.add(nameField, gbc);

        // Add a label and text field for "Email"
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(20); // Longer field (20 columns)
        panel.add(emailField, gbc);

        // Add a label and text field for "Phone"
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        JTextField phoneField = new JTextField(15); // Medium field (15 columns)
        panel.add(phoneField, gbc);

     // Show dialog
        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Information", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Do something with the input
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();

            System.out.println("Name: " + name);
            System.out.println("Email: " + email);
            System.out.println("Phone: " + phone);
        }
    }
}