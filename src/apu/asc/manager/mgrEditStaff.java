package apu.asc.manager;

import apu.asc.staff.CounterStaff;
import apu.asc.staff.Manager;
import apu.asc.staff.Technician;
import apu.asc.staff.User;
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

public class mgrEditStaff extends JFrame {

    private final ManagerService svc;
    private final mgrManageStaff parent;
    private final String staffType;
    private final String staffId;

    private JTextField tfId, tfName, tfAge, tfPhone, tfEmail, tfAddress, tfNationality;
    private JTextField tfUsernameCurrent, tfUsernameNew;
    private JPasswordField pfNewPassword, pfConfirmPassword;
    private JComboBox<String> cbGender;

    public mgrEditStaff(ManagerService svc, mgrManageStaff parent, String staffType, String staffId) {
        this.svc = svc;
        this.parent = parent;
        this.staffType = staffType;
        this.staffId = staffId;
        initComponents();
        setLocationRelativeTo(null);
        loadRecord();
    }

    private void initComponents() {
        setTitle("Edit " + prettyType());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 660);
        setResizable(false);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Edit " + prettyType(), SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setOpaque(true);
        header.setBackground(new Color(40, 60, 100));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 5, 4, 5);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        tfId          = new JTextField(18); tfId.setEditable(false);
        tfName        = new JTextField(18);
        tfAge         = new JTextField(18);
        tfPhone       = new JTextField(18);
        tfEmail       = new JTextField(18);
        tfAddress     = new JTextField(18);
        tfNationality = new JTextField(18);
        cbGender      = new JComboBox<>(new String[]{"Male", "Female"});

        tfUsernameCurrent = new JTextField(18); tfUsernameCurrent.setEditable(false);
        tfUsernameNew     = new JTextField(18);
        pfNewPassword     = new JPasswordField(18);
        pfConfirmPassword = new JPasswordField(18);

        int row = 0;
        addRow(form, c, row++, "ID:",          tfId);
        addRow(form, c, row++, "Name:",        tfName);
        addRow(form, c, row++, "Gender:",      cbGender);
        addRow(form, c, row++, "Age:",         tfAge);
        addRow(form, c, row++, "Phone:",       tfPhone);
        addRow(form, c, row++, "Email:",       tfEmail);
        addRow(form, c, row++, "Address:",     tfAddress);
        addRow(form, c, row++, "Nationality:", tfNationality);
        addRow(form, c, row++, "Current Username:", tfUsernameCurrent);
        addRow(form, c, row++, "New Username:",     tfUsernameNew);
        addRow(form, c, row++, "New Password:",     pfNewPassword);
        addRow(form, c, row++, "Confirm Password:", pfConfirmPassword);

        JLabel hint = new JLabel("(Leave username/password blank to keep them unchanged.)");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(120, 120, 120));
        c.gridx = 1; c.gridy = row;
        form.add(hint, c);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnSave = new JButton("Save Changes");
        JButton btnCancel = new JButton("Cancel");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(60, 130, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> doSave());
        btnCancel.addActionListener(e -> doCancel());
        buttons.add(btnCancel);
        buttons.add(btnSave);
        add(buttons, BorderLayout.SOUTH);
    }

    private void addRow(JPanel form, GridBagConstraints c, int row, String label, java.awt.Component field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(140, 24));
        form.add(lbl, c);
        c.gridx = 1; c.weightx = 1;
        form.add(field, c);
    }

    private String prettyType() {
        if (staffType.equals("CounterStaff")) return "Counter Staff";
        return staffType;
    }

    private void loadRecord() {
        User u = fetch();
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Record not found: " + staffId,
                    "Error", JOptionPane.ERROR_MESSAGE);
            doCancel();
            return;
        }
        tfId.setText(staffId);
        tfName.setText(u.getName());
        cbGender.setSelectedItem(u.getGender());
        tfAge.setText(String.valueOf(u.getAge()));
        tfPhone.setText(u.getPhone());
        tfEmail.setText(u.getEmail());
        tfAddress.setText(u.getAddress());
        tfNationality.setText(u.getNationality());
        tfUsernameCurrent.setText(svc.lookupUsername(staffId));
    }

    //Polymorphic fetch — returns a User reference regardless of subclass.
    private User fetch() {
        switch (staffType) {
            case "Manager":      return svc.getManagerById(staffId);
            case "CounterStaff": return svc.getCounterStaffById(staffId);
            case "Technician":   return svc.getTechnicianById(staffId);
            default:             return null;
        }
    }

    private void doSave() {
        //1) update profile fields
        String result;
        switch (staffType) {
            case "Manager":
                result = svc.updateManager(staffId, tfName.getText(),
                        (String) cbGender.getSelectedItem(), tfAge.getText(),
                        tfPhone.getText(), tfEmail.getText(),
                        tfAddress.getText(), tfNationality.getText());
                break;
            case "CounterStaff":
                result = svc.updateCounterStaff(staffId, tfName.getText(),
                        (String) cbGender.getSelectedItem(), tfAge.getText(),
                        tfPhone.getText(), tfEmail.getText(),
                        tfAddress.getText(), tfNationality.getText());
                break;
            case "Technician":
                result = svc.updateTechnician(staffId, tfName.getText(),
                        (String) cbGender.getSelectedItem(), tfAge.getText(),
                        tfPhone.getText(), tfEmail.getText(),
                        tfAddress.getText(), tfNationality.getText());
                break;
            default:
                result = "ERR: Unknown staff type.";
        }
        if (!result.startsWith("OK")) {
            JOptionPane.showMessageDialog(this, stripPrefix(result),
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //2) optional credential reset
        String newPass = new String(pfNewPassword.getPassword());
        String confirm = new String(pfConfirmPassword.getPassword());
        if (!newPass.isEmpty() && !newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this,
                    "Profile saved, but passwords do not match — credentials not changed.",
                    "Partial Save", JOptionPane.WARNING_MESSAGE);
            parent.refresh();
            dispose();
            return;
        }
        String accResult = svc.resetStaffAccount(staffId, tfUsernameNew.getText(), newPass);
        if (accResult != null && !accResult.startsWith("OK")) {
            JOptionPane.showMessageDialog(this,
                    "Profile saved.\nBut credentials update failed: " + stripPrefix(accResult),
                    "Partial Save", JOptionPane.WARNING_MESSAGE);
            parent.refresh();
            dispose();
            return;
        }

        JOptionPane.showMessageDialog(this, stripPrefix(result),
                "Saved", JOptionPane.INFORMATION_MESSAGE);
        parent.refresh();
        dispose();
    }

    private void doCancel() {
        parent.refresh();
        dispose();
    }

    private static String stripPrefix(String result) {
        if (result == null) return "";
        int idx = result.indexOf(':');
        return (idx >= 0) ? result.substring(idx + 1).trim() : result;
    }
}
