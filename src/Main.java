
import services.RegistrationService;
import dao.PreRegistrationDAO;
import model.PreRegistration;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // ====== HARD-CODED SANITY DATA ======
        int studentId = 1;
        String courseCode = "BICS2301";
        int sectionId = 1;
        int semester = 1;
        int year = 2025;

        RegistrationService service = new RegistrationService();
        PreRegistrationDAO preregDAO = new PreRegistrationDAO();

        System.out.println("=== SANITY CHECK: PRE-REGISTRATION ===");

        // 1️⃣ Try to preregister
        System.out.println("\n[1] Adding preregistration...");
        boolean added = preregDAO.addPreRegistration(
                studentId, courseCode, sectionId, semester, year
        );

        System.out.println(added
                ? "✔ Preregistration SUCCESS"
                : "✖ Preregistration FAILED");

        // 2️⃣ Try duplicate preregistration
        System.out.println("\n[2] Attempt duplicate preregistration...");
        boolean duplicate = preregDAO.addPreRegistration(
                studentId, courseCode, sectionId, semester, year
        );

        System.out.println(!duplicate
                ? "✔ Duplicate correctly BLOCKED"
                : "✖ Duplicate ERROR");

        // 3️⃣ Fetch preregistrations
        System.out.println("\n[3] Fetch preregistrations...");
        List<PreRegistration> list =
                preregDAO.getCurrentPreRegistrations(studentId, semester, year);

        if (list.isEmpty()) {
            System.out.println("✖ No preregistrations found");
        } else {
            for (PreRegistration p : list) {
                System.out.println(
                        "Course: " + p.getCourseCode() +
                        " | Section: " + p.getSectionId() +
                        " | Status: " + p.getStatus()
                );
            }
        }

        System.out.println("\n=== SANITY CHECK COMPLETED ===");
    }
}
