package apu.asc.Test;

import apu.asc.manager.ManagerService;
import apu.asc.manager.mgrAddStaff;
import apu.asc.manager.mgrComments;
import apu.asc.manager.mgrDashboard;
import apu.asc.manager.mgrEditProfile;
import apu.asc.manager.mgrEditStaff;
import apu.asc.manager.mgrFeedbacks;
import apu.asc.manager.mgrManageStaff;
import apu.asc.manager.mgrReports;
import apu.asc.manager.mgrSetPrices;
import javax.swing.SwingUtilities;

//Smoke test that instantiates each Manager GUI frame to verify there are no runtime errors.
public class ManagerGuiTest {

    public static void main(String[] args) throws Exception {
        ManagerService svc = new ManagerService("M001");

        SwingUtilities.invokeAndWait(() -> {
            mgrDashboard d = new mgrDashboard("M001");
            d.setVisible(true);
            System.out.println("mgrDashboard       opened=" + d.isVisible() + " | title=" + d.getTitle());
            d.dispose();
        });

        SwingUtilities.invokeAndWait(() -> {
            mgrDashboard d = new mgrDashboard("M001");
            mgrEditProfile f = new mgrEditProfile(svc, d);
            f.setVisible(true);
            System.out.println("mgrEditProfile     opened=" + f.isVisible() + " | title=" + f.getTitle());
            f.dispose();
            d.dispose();
        });

        SwingUtilities.invokeAndWait(() -> {
            mgrDashboard d = new mgrDashboard("M001");
            mgrSetPrices f = new mgrSetPrices(svc, d);
            f.setVisible(true);
            System.out.println("mgrSetPrices       opened=" + f.isVisible() + " | title=" + f.getTitle());
            f.dispose();
            d.dispose();
        });

        for (String type : new String[]{"Manager", "CounterStaff", "Technician"}) {
            SwingUtilities.invokeAndWait(() -> {
                mgrDashboard d = new mgrDashboard("M001");
                mgrManageStaff f = new mgrManageStaff(svc, d, type);
                f.setVisible(true);
                System.out.println("mgrManageStaff[" + type + "] opened=" + f.isVisible());
                f.dispose();
                d.dispose();
            });
        }

        SwingUtilities.invokeAndWait(() -> {
            mgrDashboard d = new mgrDashboard("M001");
            mgrManageStaff m = new mgrManageStaff(svc, d, "Technician");
            mgrAddStaff f = new mgrAddStaff(svc, m, "Technician");
            f.setVisible(true);
            System.out.println("mgrAddStaff        opened=" + f.isVisible() + " | title=" + f.getTitle());
            f.dispose();
            m.dispose();
            d.dispose();
        });

        SwingUtilities.invokeAndWait(() -> {
            mgrDashboard d = new mgrDashboard("M001");
            mgrManageStaff m = new mgrManageStaff(svc, d, "Technician");
            mgrEditStaff f = new mgrEditStaff(svc, m, "Technician", "T001");
            f.setVisible(true);
            System.out.println("mgrEditStaff       opened=" + f.isVisible() + " | title=" + f.getTitle());
            f.dispose();
            m.dispose();
            d.dispose();
        });

        SwingUtilities.invokeAndWait(() -> {
            mgrDashboard d = new mgrDashboard("M001");
            mgrFeedbacks f = new mgrFeedbacks(svc, d);
            f.setVisible(true);
            System.out.println("mgrFeedbacks       opened=" + f.isVisible() + " | title=" + f.getTitle());
            f.dispose();
            d.dispose();
        });

        SwingUtilities.invokeAndWait(() -> {
            mgrDashboard d = new mgrDashboard("M001");
            mgrComments f = new mgrComments(svc, d);
            f.setVisible(true);
            System.out.println("mgrComments        opened=" + f.isVisible() + " | title=" + f.getTitle());
            f.dispose();
            d.dispose();
        });

        SwingUtilities.invokeAndWait(() -> {
            mgrDashboard d = new mgrDashboard("M001");
            mgrReports f = new mgrReports(svc, d);
            f.setVisible(true);
            System.out.println("mgrReports         opened=" + f.isVisible() + " | title=" + f.getTitle());
            f.dispose();
            d.dispose();
        });

        System.out.println("\nAll Manager GUI tests passed.");
        System.exit(0);
    }
}
