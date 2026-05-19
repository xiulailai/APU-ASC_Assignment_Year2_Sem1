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
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class mgrManageStaff extends JFrame {

    private final ManagerService svc;
    private final mgrDashboard parent;
    private final String staffType;   // "Manager" | "CounterStaff" | "Technician"

    private JTextField tfSearch;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblCount;

    public mgrManageStaff(ManagerService svc, mgrDashboard parent, String staffType) {
        this.svc = svc;
        this.parent = parent;
        this.staffType = staffType;
        initComponents();
        setLocationRelativeTo(null);
        loadData(null);
    }

    public String getStaffType() { return staffType; }

    private void initComponents() {
        setTitle("Manage " + prettyType());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(820, 520);
        setLayout(new BorderLayout());

        //header
        JLabel header = new JLabel("Manage " + prettyType(), SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setOpaque(true);
        header.setBackground(new Color(40, 60, 100));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        add(header, BorderLayout.NORTH);

        //center
        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));

        //search bar
        JPanel topBar = new JPanel(new BorderLayout(8, 0));
        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tfSearch = new JTextField();
        JButton btnSearch = new JButton("Search");
        JButton btnClear  = new JButton("Clear");
        btnSearch.addActionListener(e -> loadData(tfSearch.getText()));
        btnClear.addActionListener(e -> { tfSearch.setText(""); loadData(null); });
        JPanel searchBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchBtns.add(btnSearch);
        searchBtns.add(btnClear);
        topBar.add(lblSearch, BorderLayout.WEST);
        topBar.add(tfSearch,  BorderLayout.CENTER);
        topBar.add(searchBtns, BorderLayout.EAST);
        center.add(topBar, BorderLayout.NORTH);

        //table
        String idHeader = staffType.equals("CounterStaff") ? "CS ID"
                          : staffType.equals("Technician") ? "Technician ID"
                          : "Manager ID";
        tableModel = new DefaultTableModel(
                new String[]{idHeader, "Name", "Gender", "Age", "Phone", "Email", "Nationality"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        //footer line: count
        lblCount = new JLabel("0 records");
        lblCount.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        center.add(lblCount, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        //action buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnAdd    = new JButton("Add");
        JButton btnEdit   = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnBack   = new JButton("Back");

        btnAdd.setBackground(new Color(60, 130, 80));    btnAdd.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(70, 110, 180));  btnEdit.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(180, 60, 60)); btnDelete.setForeground(Color.WHITE);
        for (JButton b : new JButton[]{btnAdd, btnEdit, btnDelete, btnBack}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setFocusPainted(false);
        }

        btnAdd.addActionListener(e -> doAdd());
        btnEdit.addActionListener(e -> doEdit());
        btnDelete.addActionListener(e -> doDelete());
        btnBack.addActionListener(e -> doBack());

        buttons.add(btnBack);
        buttons.add(btnAdd);
        buttons.add(btnEdit);
        buttons.add(btnDelete);
        add(buttons, BorderLayout.SOUTH);
    }

    private String prettyType() {
        if (staffType.equals("CounterStaff")) return "Counter Staff";
        return staffType + "s";
    }

    private void loadData(String search) {
        tableModel.setRowCount(0);
        List<User> records = fetchRecords(search);
        for (User u : records) {
            tableModel.addRow(new Object[]{
                getId(u),
                u.getName(),
                u.getGender(),
                u.getAge(),
                u.getPhone(),
                u.getEmail(),
                u.getNationality()
            });
        }
        lblCount.setText(records.size() + " record(s)");
    }

    //Polymorphic load — collects User references across the three subclass lists.
    private List<User> fetchRecords(String search) {
        List<User> result = new ArrayList<>();
        switch (staffType) {
            case "Manager":
                result.addAll(svc.searchManagers(search));
                break;
            case "CounterStaff":
                result.addAll(svc.searchCounterStaff(search));
                break;
            case "Technician":
                result.addAll(svc.searchTechnicians(search));
                break;
        }
        return result;
    }

    private String getId(User u) {
        if (u instanceof Manager)      return ((Manager) u).getManagerId();
        if (u instanceof CounterStaff) return ((CounterStaff) u).getCounterStaffId();
        if (u instanceof Technician)   return ((Technician) u).getTechnicianId();
        return "?";
    }

    private void doAdd() {
        new mgrAddStaff(svc, this, staffType).setVisible(true);
        setVisible(false);
    }

    private void doEdit() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a record to edit.",
                    "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(r, 0);
        new mgrEditStaff(svc, this, staffType, id).setVisible(true);
        setVisible(false);
    }

    private void doDelete() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a record to delete.",
                    "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(r, 0);
        String name = (String) tableModel.getValueAt(r, 1);
        int ok = JOptionPane.showConfirmDialog(this,
                "Delete " + prettyType() + " " + id + " (" + name + ")?\n"
                + "This will also remove their login account.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        String result;
        switch (staffType) {
            case "Manager":      result = svc.deleteManager(id);      break;
            case "CounterStaff": result = svc.deleteCounterStaff(id); break;
            case "Technician":   result = svc.deleteTechnician(id);   break;
            default:             result = "ERR: Unknown staff type."; break;
        }
        if (result.startsWith("OK")) {
            JOptionPane.showMessageDialog(this, result.substring(3).trim(),
                    "Deleted", JOptionPane.INFORMATION_MESSAGE);
            loadData(tfSearch.getText());
        } else {
            JOptionPane.showMessageDialog(this, result.substring(3).trim(),
                    "Cannot Delete", JOptionPane.WARNING_MESSAGE);
        }
    }

    //Called by child add/edit frames after success to refresh.
    public void refresh() {
        loadData(tfSearch.getText());
        setVisible(true);
    }

    private void doBack() {
        parent.refreshStats();
        parent.setVisible(true);
        dispose();
    }
}
