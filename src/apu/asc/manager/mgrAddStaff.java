package apu.asc.manager;

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

public class mgrAddStaff extends JFrame {

    private final ManagerService svc;
    private final mgrManageStaff parent;
    private final String staffType;

    private JTextField tfName, tfUsername, tfAge, tfPhone, tfEmail, tfAddress, tfNationality;
    private JComboBox<String> cbGender;
    private JPasswordField pfPassword, pfConfirm;

    public mgrAddStaff(ManagerService svc, mgrManageStaff parent, String staffType) {
        this.svc = svc;
        this.parent = parent;
        this.staffType = staffType;
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Add " + prettyType());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setResizable(false);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Add New " + prettyType(), SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setOpaque(true);
        header.setBackground(new Color(40, 60, 100));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        tfName        = new JTextField(18);
        tfUsername    = new JTextField(18);
        pfPassword    = new JPasswordField(18);
        pfConfirm     = new JPasswordField(18);
        cbGender      = new JComboBox<>(new String[]{"Male", "Female"});
        tfAge         = new JTextField(18);
        tfPhone       = new JTextField(18);
        tfEmail       = new JTextField(18);
        tfAddress     = new JTextField(18);
        tfNationality = new JTextField(18);

        int row = 0;
        addRow(form, c, row++, "Name:",         tfName);
        addRow(form, c, row++, "Username:",     tfUsername);
        addRow(form, c, row++, "Password:",     pfPassword);
        addRow(form, c, row++, "Confirm:",      pfConfirm);
        addRow(form, c, row++, "Gender:",       cbGender);
        addRow(form, c, row++, "Age:",          tfAge);
        addRow(form, c, row++, "Phone:",        tfPhone);
        addRow(form, c, row++, "Email:",        tfEmail);
        addRow(form, c, row++, "Address:",      tfAddress);
        addRow(form, c, row++, "Nationality:",  tfNationality);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnSave = new JButton("Add " + prettyType());
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
        lbl.setPreferredSize(new Dimension(110, 24));
        form.add(lbl, c);
        c.gridx = 1; c.weightx = 1;
        form.add(field, c);
    }

    private String prettyType() {
        if (staffType.equals("CounterStaff")) return "Counter Staff";
        return staffType;
    }

    private void doSave() {
        String pass    = new String(pfPassword.getPassword());
        String confirm = new String(pfConfirm.getPassword());
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Password and Confirm do not match.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String result;
        switch (staffType) {
            case "Manager":
                result = svc.addManager(tfName.getText(), tfUsername.getText(), pass,
                        (String) cbGender.getSelectedItem(), tfAge.getText(),
                        tfPhone.getText(), tfEmail.getText(),
                        tfAddress.getText(), tfNationality.getText());
                break;
            case "CounterStaff":
                result = svc.addCounterStaff(tfName.getText(), tfUsername.getText(), pass,
                        (String) cbGender.getSelectedItem(), tfAge.getText(),
                        tfPhone.getText(), tfEmail.getText(),
                        tfAddress.getText(), tfNationality.getText());
                break;
            case "Technician":
                result = svc.addTechnician(tfName.getText(), tfUsername.getText(), pass,
                        (String) cbGender.getSelectedItem(), tfAge.getText(),
                        tfPhone.getText(), tfEmail.getText(),
                        tfAddress.getText(), tfNationality.getText());
                break;
            default:
                result = "ERR: Unknown staff type.";
        }

        if (result.startsWith("OK")) {
            JOptionPane.showMessageDialog(this, stripPrefix(result),
                    "Added", JOptionPane.INFORMATION_MESSAGE);
            parent.refresh();
            dispose();
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

    private void doCancel() {
        parent.refresh();
        dispose();
    }
}
