package apu.asc.manager;

import apu.asc.staff.Service;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

public class mgrSetPrices extends JFrame {

    private final ManagerService svc;
    private final mgrDashboard parent;

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField tfServiceId, tfServiceName, tfDuration, tfNewPrice;

    public mgrSetPrices(ManagerService svc, mgrDashboard parent) {
        this.svc = svc;
        this.parent = parent;
        initComponents();
        setLocationRelativeTo(null);
        loadServices();
    }

    private void initComponents() {
        setTitle("Set Service Prices");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(620, 460);
        setResizable(false);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Set Service Prices", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setOpaque(true);
        header.setBackground(new Color(40, 60, 100));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        add(header, BorderLayout.NORTH);

        //center: table on top, form on bottom
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(14, 16, 8, 16));

        tableModel = new DefaultTableModel(
                new String[]{"Service ID", "Name", "Duration", "Current Price (RM)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 160));
        center.add(scroll, BorderLayout.NORTH);

        center.add(buildForm(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        //bottom buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnUpdate = new JButton("Update Price");
        JButton btnBack = new JButton("Back");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpdate.setBackground(new Color(60, 130, 80));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.addActionListener(e -> doUpdate());
        btnBack.addActionListener(e -> doBack());
        buttons.add(btnBack);
        buttons.add(btnUpdate);
        add(buttons, BorderLayout.SOUTH);
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Selected Service"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        tfServiceId   = new JTextField(12); tfServiceId.setEditable(false);
        tfServiceName = new JTextField(20); tfServiceName.setEditable(false);
        tfDuration    = new JTextField(8);  tfDuration.setEditable(false);
        tfNewPrice    = new JTextField(10);

        int row = 0;
        addRow(form, c, row++, "Service ID:",     tfServiceId);
        addRow(form, c, row++, "Service Name:",   tfServiceName);
        addRow(form, c, row++, "Duration (hrs):", tfDuration);
        addRow(form, c, row++, "New Price (RM):", tfNewPrice);

        return form;
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

    private void loadServices() {
        tableModel.setRowCount(0);
        List<Service> services = svc.getAllServices();
        for (Service s : services) {
            tableModel.addRow(new Object[]{
                s.getServiceId(),
                s.getServiceName(),
                s.getDurationHours() + " hr(s)",
                String.format("%.2f", s.getPrice())
            });
        }
        if (table.getRowCount() > 0) table.setRowSelectionInterval(0, 0);
    }

    private void onRowSelected() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        tfServiceId.setText((String) tableModel.getValueAt(r, 0));
        tfServiceName.setText((String) tableModel.getValueAt(r, 1));
        tfDuration.setText((String) tableModel.getValueAt(r, 2));
        tfNewPrice.setText((String) tableModel.getValueAt(r, 3));
    }

    private void doUpdate() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a service from the table first.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String result = svc.updateServicePrice(tfServiceId.getText(), tfNewPrice.getText());
        if (result.startsWith("OK")) {
            JOptionPane.showMessageDialog(this, stripPrefix(result),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadServices();
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
