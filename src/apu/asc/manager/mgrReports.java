package apu.asc.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class mgrReports extends JFrame {

    private final ManagerService svc;
    private final mgrDashboard parent;

    public mgrReports(ManagerService svc, mgrDashboard parent) {
        this.svc = svc;
        this.parent = parent;
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Reports");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Analytical Reports", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setOpaque(true);
        header.setBackground(new Color(40, 60, 100));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.addTab("1. Revenue",             buildRevenueTab());
        tabs.addTab("2. Technician Perf.",    buildTechnicianTab());
        tabs.addTab("3. Counter Staff Perf.", buildCounterStaffTab());
        tabs.addTab("4. Service Popularity",  buildServicePopularityTab());
        tabs.addTab("5. Customer Activity",   buildCustomerActivityTab());
        tabs.addTab("6. Appointment Status",  buildAppointmentStatusTab());
        tabs.addTab("7. Monthly Revenue",     buildMonthlyRevenueTab());
        add(tabs, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.addActionListener(e -> doBack());
        footer.add(btnBack);
        add(footer, BorderLayout.SOUTH);
    }

    // ---------- TAB 1: REVENUE ----------
    private JPanel buildRevenueTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        DefaultTableModel m = new DefaultTableModel(
                new String[]{"Service", "Payments", "Total Revenue"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String[] row : svc.getRevenueReport()) m.addRow(row);

        JTable table = makeTable(m);
        panel.add(buildTitleLabel("Revenue Breakdown by Service"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel total = new JLabel(String.format("Total Revenue: RM %.2f   |   This Month: RM %.2f",
                svc.getTotalRevenue(), svc.getCurrentMonthRevenue()), SwingConstants.RIGHT);
        total.setFont(new Font("Segoe UI", Font.BOLD, 13));
        total.setForeground(new Color(30, 80, 30));
        panel.add(total, BorderLayout.SOUTH);
        return panel;
    }

    // ---------- TAB 2: TECHNICIAN PERFORMANCE ----------
    private JPanel buildTechnicianTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        DefaultTableModel m = new DefaultTableModel(
                new String[]{"Tech ID", "Name", "Assigned", "Completed", "Feedbacks Given"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String[] row : svc.getTechnicianPerformanceReport()) m.addRow(row);

        panel.add(buildTitleLabel("Technician Performance"), BorderLayout.NORTH);
        panel.add(new JScrollPane(makeTable(m)), BorderLayout.CENTER);
        return panel;
    }

    // ---------- TAB 3: COUNTER STAFF PERFORMANCE ----------
    private JPanel buildCounterStaffTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        DefaultTableModel m = new DefaultTableModel(
                new String[]{"CS ID", "Name", "Appointments Created", "Payments Collected", "Revenue Collected"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String[] row : svc.getCounterStaffPerformanceReport()) m.addRow(row);

        panel.add(buildTitleLabel("Counter Staff Performance"), BorderLayout.NORTH);
        panel.add(new JScrollPane(makeTable(m)), BorderLayout.CENTER);
        return panel;
    }

    // ---------- TAB 4: SERVICE POPULARITY ----------
    private JPanel buildServicePopularityTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        DefaultTableModel m = new DefaultTableModel(
                new String[]{"Service ID", "Name", "Duration", "Total Appointments", "Completed"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String[] row : svc.getServicePopularityReport()) m.addRow(row);

        panel.add(buildTitleLabel("Service Popularity"), BorderLayout.NORTH);
        panel.add(new JScrollPane(makeTable(m)), BorderLayout.CENTER);
        return panel;
    }

    // ---------- TAB 5: CUSTOMER ACTIVITY ----------
    private JPanel buildCustomerActivityTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        DefaultTableModel m = new DefaultTableModel(
                new String[]{"Customer ID", "Name", "Total Appointments", "Total Spent"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String[] row : svc.getCustomerActivityReport()) m.addRow(row);

        panel.add(buildTitleLabel("Customer Activity (sorted by spend)"), BorderLayout.NORTH);
        panel.add(new JScrollPane(makeTable(m)), BorderLayout.CENTER);
        return panel;
    }

    // ---------- TAB 6: APPOINTMENT STATUS (with bar chart) ----------
    private JPanel buildAppointmentStatusTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        int[] counts = svc.getAppointmentStatusCounts(); //pending, assigned, completed, total

        JPanel summary = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 8));
        summary.add(makeStatLabel("Pending",   counts[0], new Color(190, 130, 0)));
        summary.add(makeStatLabel("Assigned",  counts[1], new Color(40, 90, 180)));
        summary.add(makeStatLabel("Completed", counts[2], new Color(40, 130, 60)));
        summary.add(makeStatLabel("Total",     counts[3], new Color(60, 60, 60)));

        BarChartPanel chart = new BarChartPanel(
                new String[]{"Pending", "Assigned", "Completed"},
                new int[]{counts[0], counts[1], counts[2]},
                new Color[]{
                    new Color(230, 170, 60),
                    new Color(80,  130, 220),
                    new Color(80,  180, 110)
                });

        panel.add(buildTitleLabel("Appointment Status Summary"), BorderLayout.NORTH);
        panel.add(summary, BorderLayout.SOUTH);
        panel.add(chart, BorderLayout.CENTER);
        return panel;
    }

    // ---------- TAB 7: MONTHLY REVENUE ----------
    private JPanel buildMonthlyRevenueTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        DefaultTableModel m = new DefaultTableModel(
                new String[]{"Month", "Payment Count", "Total Revenue"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        List<String[]> rows = svc.getMonthlyRevenueReport();
        for (String[] row : rows) m.addRow(row);

        panel.add(buildTitleLabel("Monthly Revenue"), BorderLayout.NORTH);
        panel.add(new JScrollPane(makeTable(m)), BorderLayout.CENTER);
        return panel;
    }

    // ---------- helpers ----------
    private JTable makeTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setRowHeight(24);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        return t;
    }

    private JLabel buildTitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(new Color(30, 50, 90));
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return l;
    }

    private JLabel makeStatLabel(String name, int value, Color color) {
        JLabel l = new JLabel(name + ": " + value);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(color);
        return l;
    }

    private void doBack() {
        parent.refreshStats();
        parent.setVisible(true);
        dispose();
    }

    //Simple in-house bar chart so we don't pull in third-party libs.
    private static class BarChartPanel extends JPanel {
        private final String[] labels;
        private final int[] values;
        private final Color[] colors;

        BarChartPanel(String[] labels, int[] values, Color[] colors) {
            this.labels = labels;
            this.values = values;
            this.colors = colors;
            setPreferredSize(new Dimension(0, 340));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int marginX = 40, marginTop = 30, marginBottom = 40;
            int chartH = h - marginTop - marginBottom;

            int max = 1;
            for (int v : values) if (v > max) max = v;

            int barCount = values.length;
            int gap = 24;
            int totalGap = gap * (barCount + 1);
            int barW = Math.max(40, (w - 2 * marginX - totalGap) / barCount);

            g2.setColor(new Color(180, 180, 180));
            g2.drawLine(marginX, h - marginBottom, w - marginX, h - marginBottom);

            int x = marginX + gap;
            for (int i = 0; i < barCount; i++) {
                int barH = (int) (chartH * (values[i] / (double) max));
                int y = h - marginBottom - barH;
                g2.setColor(colors[i % colors.length]);
                g2.fillRect(x, y, barW, barH);
                g2.setColor(Color.DARK_GRAY);
                g2.drawRect(x, y, barW, barH);
                //value label above bar
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String valStr = String.valueOf(values[i]);
                int valW = g2.getFontMetrics().stringWidth(valStr);
                g2.drawString(valStr, x + (barW - valW) / 2, y - 6);
                //category label below
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                int labW = g2.getFontMetrics().stringWidth(labels[i]);
                g2.drawString(labels[i], x + (barW - labW) / 2, h - marginBottom + 18);
                x += barW + gap;
            }
        }
    }
}
