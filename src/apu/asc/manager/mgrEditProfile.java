package apu.asc.manager;

import apu.asc.staff.Manager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class mgrEditProfile extends JFrame {

    private final ManagerService svc;
    private final mgrDashboard parent;

    private JTextField tfName, tfAge, tfPhone, tfEmail, tfAddress, tfNationality, tfUsername;
    private JComboBox<String> cbGender;
    private JPasswordField pfNewPassword, pfConfirmPassword;

    public mgrEditProfile(ManagerService svc, mgrDashboard parent) {
        this.svc = svc;
        this.parent = parent;
        initComponents();
        setLocationRelativeTo(null);
        loadProfile();
    }

    private void initComponents() {
        setTitle("Edit My Profile");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 560);
        setResizable(false);
        setLayout(new BorderLayout());

        //header
        JLabel header = new JLabel("Edit My Profile", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setOpaque(true);
        header.setBackground(new Color(40, 60, 100));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        add(header, BorderLayout.NORTH);

        //form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        tfName        = new JTextField(18);
        tfAge         = new JTextField(18);
        tfPhone       = new JTextField(18);
        tfEmail       = new JTextField(18);
        tfAddress     = new JTextField(18);
        tfNationality = new JTextField(18);
        tfUsername    = new JTextField(18);
        cbGender      = new JComboBox<>(new String[]{"Male", "Female"});
        pfNewPassword     = new JPasswordField(18);
        pfConfirmPassword = new JPasswordField(18);

        int row = 0;
        addRow(form, c, row++, "Name:",        tfName);
        addRow(form, c, row++, "Gender:",      cbGender);
        addRow(form, c, row++, "Age:",         tfAge);
        addRow(form, c, row++, "Phone:",       tfPhone);
        addRow(form, c, row++, "Email:",       tfEmail);
        addRow(form, c, row++, "Address:",     tfAddress);
        addRow(form, c, row++, "Nationality:", tfNationality);
        addRow(form, c, row++, "Username:",    tfUsername);
        addRow(form, c, row++, "New Password:",       pfNewPassword);
        addRow(form, c, row++, "Confirm Password:",   pfConfirmPassword);

        JLabel hint = new JLabel("(Leave password blank to keep current.)");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(120, 120, 120));
        c.gridx = 1; c.gridy = row;
        form.add(hint, c);

        add(form, BorderLayout.CENTER);

        //buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnSave = new JButton("Save Changes");
        JButton btnBack = new JButton("Back");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(60, 130, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> doSave());
        btnBack.addActionListener(e -> doBack());
        buttons.add(btnBack);
        buttons.add(btnSave);
        add(buttons, BorderLayout.SOUTH);
    }

    private void addRow(JPanel form, GridBagConstraints c, int row, String label, java.awt.Component field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(120, 24));
        form.add(lbl, c);
        c.gridx = 1; c.weightx = 1;
        form.add(field, c);
    }

    private void loadProfile() {
        Manager m = svc.getProfile();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Profile not found.", "Error", JOptionPane.ERROR_MESSAGE);
            doBack();
            return;
        }
        tfName.setText(m.getName());
        cbGender.setSelectedItem(m.getGender());
        tfAge.setText(String.valueOf(m.getAge()));
        tfPhone.setText(m.getPhone());
        tfEmail.setText(m.getEmail());
        tfAddress.setText(m.getAddress());
        tfNationality.setText(m.getNationality());
        tfUsername.setText(svc.getMyUsername());
    }

    private void doSave() {
        String newPass = new String(pfNewPassword.getPassword());
        String confirm = new String(pfConfirmPassword.getPassword());
        if (!newPass.isEmpty() && !newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String result = svc.updateProfile(
                tfName.getText(), (String) cbGender.getSelectedItem(), tfAge.getText(),
                tfPhone.getText(), tfEmail.getText(), tfAddress.getText(), tfNationality.getText(),
                tfUsername.getText(),
                newPass);

        if (result.startsWith("OK")) {
            JOptionPane.showMessageDialog(this, stripPrefix(result),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            pfNewPassword.setText("");
            pfConfirmPassword.setText("");
        } else {
            JOptionPane.showMessageDialog(this, stripPrefix(result),
                    "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static String stripPrefix(String result) {
        if (result == null) return "";
        int idx = result.indexOf(':');
        return (idx >= 0) ? result.substring(idx + 1).trim() : result;
    }

    private void doBack() {
        parent.refreshStats();
        parent.setVisible(true);
        dispose();
    }
}
