package apu.asc.manager;

import apu.asc.main.LoginFrame;
import apu.asc.staff.Manager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class mgrDashboard extends JFrame {

    private final ManagerService svc;

    private JLabel lblWelcome;
    private JLabel lblMgrCount, lblCsCount, lblTechCount, lblCustCount, lblRevenue;

    public mgrDashboard(String managerId) {
        this.svc = new ManagerService(managerId);
        initComponents();
        setLocationRelativeTo(null);
        refreshStats();
    }

    private void initComponents() {
        setTitle("APU - ASC | Manager Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 560);
        setResizable(false);
        setLayout(new BorderLayout());

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildFooter(),    BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(40, 60, 100));
        header.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel lblTitle = new JLabel("APU - ASC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Manager Dashboard", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(220, 230, 245));

        JPanel titleStack = new JPanel(new GridLayout(2, 1));
        titleStack.setOpaque(false);
        titleStack.add(lblTitle);
        titleStack.add(lblSub);

        lblWelcome = new JLabel("Welcome", SwingConstants.RIGHT);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblWelcome.setForeground(Color.WHITE);

        header.add(titleStack, BorderLayout.CENTER);
        header.add(lblWelcome, BorderLayout.EAST);
        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));

        //stats row
        JPanel stats = new JPanel(new GridLayout(1, 5, 10, 0));
        stats.setPreferredSize(new Dimension(0, 80));
        lblMgrCount  = makeStatBox(stats, "Managers",       "0");
        lblCsCount   = makeStatBox(stats, "Counter Staff",  "0");
        lblTechCount = makeStatBox(stats, "Technicians",    "0");
        lblCustCount = makeStatBox(stats, "Customers",      "0");
        lblRevenue   = makeStatBox(stats, "This Month",     "RM 0.00");

        //menu buttons
        JPanel menu = new JPanel(new GridLayout(4, 2, 12, 12));
        menu.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        menu.add(makeMenuButton("Edit My Profile",      this::openEditProfile));
        menu.add(makeMenuButton("Set Service Prices",   this::openSetPrices));
        menu.add(makeMenuButton("Manage Managers",      () -> openManageStaff("Manager")));
        menu.add(makeMenuButton("Manage Counter Staff", () -> openManageStaff("CounterStaff")));
        menu.add(makeMenuButton("Manage Technicians",   () -> openManageStaff("Technician")));
        menu.add(makeMenuButton("View Feedbacks",       this::openFeedbacks));
        menu.add(makeMenuButton("View Comments",        this::openComments));
        menu.add(makeMenuButton("Reports",              this::openReports));

        center.add(stats, BorderLayout.NORTH);
        center.add(menu,  BorderLayout.CENTER);
        return center;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(8, 20, 14, 20));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setBackground(new Color(180, 60, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setMargin(new Insets(6, 18, 6, 18));
        btnLogout.addActionListener(e -> doLogout());

        footer.add(btnLogout, BorderLayout.EAST);
        return footer;
    }

    private JLabel makeStatBox(JPanel parent, String caption, String value) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(new Color(245, 247, 252));
        box.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 225)));

        JLabel lblCap = new JLabel(caption, SwingConstants.CENTER);
        lblCap.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCap.setForeground(new Color(80, 90, 110));

        JLabel lblVal = new JLabel(value, SwingConstants.CENTER);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblVal.setForeground(new Color(30, 50, 90));

        box.add(lblCap, BorderLayout.NORTH);
        box.add(lblVal, BorderLayout.CENTER);
        parent.add(box);
        return lblVal;
    }

    private JButton makeMenuButton(String text, Runnable onClick) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(70, 110, 180));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(0, 50));
        btn.addActionListener(e -> onClick.run());
        return btn;
    }

    //Refreshes the top stats. Called on construction and whenever returning to dashboard.
    public void refreshStats() {
        Manager me = svc.getProfile();
        lblWelcome.setText(me != null
                ? "  " + me.getName() + " (" + me.getManagerId() + ")"
                : "  " + svc.getManagerId());
        lblMgrCount.setText (String.valueOf(svc.countManagers()));
        lblCsCount.setText  (String.valueOf(svc.countCounterStaff()));
        lblTechCount.setText(String.valueOf(svc.countTechnicians()));
        lblCustCount.setText(String.valueOf(svc.countCustomers()));
        lblRevenue.setText(String.format("RM %.2f", svc.getCurrentMonthRevenue()));
    }

    // ------------- navigation -------------

    private void openEditProfile() {
        new mgrEditProfile(svc, this).setVisible(true);
        setVisible(false);
    }

    private void openSetPrices() {
        new mgrSetPrices(svc, this).setVisible(true);
        setVisible(false);
    }

    private void openManageStaff(String type) {
        new mgrManageStaff(svc, this, type).setVisible(true);
        setVisible(false);
    }

    private void openFeedbacks() {
        new mgrFeedbacks(svc, this).setVisible(true);
        setVisible(false);
    }

    private void openComments() {
        new mgrComments(svc, this).setVisible(true);
        setVisible(false);
    }

    private void openReports() {
        new mgrReports(svc, this).setVisible(true);
        setVisible(false);
    }

    private void doLogout() {
        int ok = JOptionPane.showConfirmDialog(this,
                "Logout and return to login screen?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        dispose();
        new LoginFrame().setVisible(true);
    }
}
