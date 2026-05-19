package apu.asc.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class mgrComments extends JFrame {

    private final ManagerService svc;
    private final mgrDashboard parent;

    private JTextField tfSearch;
    private DefaultTableModel tableModel;
    private JLabel lblCount;

    public mgrComments(ManagerService svc, mgrDashboard parent) {
        this.svc = svc;
        this.parent = parent;
        initComponents();
        setLocationRelativeTo(null);
        loadData(null);
    }

    private void initComponents() {
        setTitle("All Comments");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 480);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("All Comments from Customers", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setOpaque(true);
        header.setBackground(new Color(40, 60, 100));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));

        //search
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
        tableModel = new DefaultTableModel(
                new String[]{"Comment ID", "Appointment", "From (Customer)", "Target", "Comment"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(22);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getColumnModel().getColumn(4).setPreferredWidth(360);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        lblCount = new JLabel("0 records");
        lblCount.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        center.add(lblCount, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.addActionListener(e -> doBack());
        buttons.add(btnBack);
        add(buttons, BorderLayout.SOUTH);
    }

    private void loadData(String search) {
        tableModel.setRowCount(0);
        List<String[]> rows = svc.getCommentRows(search);
        for (String[] r : rows) tableModel.addRow(r);
        lblCount.setText(rows.size() + " record(s)");
    }

    private void doBack() {
        parent.refreshStats();
        parent.setVisible(true);
        dispose();
    }
}
