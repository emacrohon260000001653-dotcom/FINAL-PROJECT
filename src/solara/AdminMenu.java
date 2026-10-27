package solara;

import java.util.List;

public class AdminMenu {
    // Demo credentials (also listed in README.md)
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PIN = "solara123";

    private final ReservationRepository repo;
    private final PriceList prices;

    public AdminMenu(ReservationRepository repo, PriceList prices) {
        this.repo = repo;
        this.prices = prices;
    }

    public void show() {
        if (!login()) return;
        while (true) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. View All Reservations");
            System.out.println("2. Search Reservation (reference or guest name)");
            System.out.println("3. View Reservations by Status");
            System.out.println("4. Reservation Management (confirm / check in / ...)");
            System.out.println("5. Payments            [TODO - teammate]");
            System.out.println("6. Edit Prices         [TODO - teammate]");
            System.out.println("7. Statistics          [TODO - teammate]");
            System.out.println("8. Export to CSV       [TODO - teammate]");
            System.out.println("0. Log out");
            int choice = InputHelper.readInt("Choose: ", 0, 8);
            switch (choice) {
                case 1 -> { printTable(repo.getAll()); InputHelper.pause(); }
                case 2 -> search();
                case 3 -> viewByStatus();
                case 4 -> managementMenu();
                case 5, 6, 7, 8 -> { System.out.println("Coming soon (not built yet)."); }
                case 0 -> { System.out.println("Logged out."); return; }
            }
        }
    }

    private boolean login() {
        System.out.println("\n--- ADMIN LOGIN ---");
        for (int attempt = 1; attempt <= 3; attempt++) {
            String user = InputHelper.readText("Username: ");
            String pin = InputHelper.readText("Password/PIN: ");
            if (user.equals(ADMIN_USER) && pin.equals(ADMIN_PIN)) {
                System.out.println("Login successful.");
                return true;
            }
            System.out.println("Wrong username or password. Attempts left: " + (3 - attempt));
        }
        System.out.println("Too many failed attempts. Returning to main menu.");
        return false;
    }

    private void search() {
        String text = InputHelper.readText("\nEnter reference number or guest name: ");
        Reservation byRef = repo.findByReference(text);
        if (byRef != null) {
            CustomerMenu.printDetails(byRef);
        } else {
            List<Reservation> found = repo.searchByName(text);
            if (found.isEmpty()) System.out.println("No matching reservation.");
            else printTable(found);
        }
        InputHelper.pause();
    }

    private void viewByStatus() {
        ReservationStatus[] all = ReservationStatus.values();
        System.out.println();
        for (int i = 0; i < all.length; i++) System.out.println((i + 1) + ". " + all[i]);
        int pick = InputHelper.readInt("Choose status: ", 1, all.length);
        printTable(repo.findByStatus(all[pick - 1]));
        InputHelper.pause();
    }

    private void managementMenu() {
        while (true) {
            System.out.println("\n--- RESERVATION MANAGEMENT ---");
            System.out.println("1. Confirm a PENDING booking");
            System.out.println("2. Check In a CONFIRMED booking");
            System.out.println("3. Mark a booking COMPLETED   [TODO - teammate]");
            System.out.println("4. Cancel a booking           [TODO - teammate]");
            System.out.println("0. Back");
            int choice = InputHelper.readInt("Choose: ", 0, 4);
            switch (choice) {
                case 1 -> confirmBooking();
                case 2 -> checkInGuest();
                case 3 -> System.out.println("Coming soon (not built yet).");
                // TODO: only CHECKED IN bookings can become COMPLETED.
                case 4 -> System.out.println("Coming soon (not built yet).");
                // TODO: ask InputHelper.confirm(...) first, then set CANCELLED
                //       and store the reason with r.setNote(...).
                case 0 -> { return; }
            }
        }
    }

    private void confirmBooking() {
        Reservation r = askForReservation();
        if (r == null) return;
        if (r.getStatus() != ReservationStatus.PENDING) {
            System.out.println("Only PENDING bookings can be confirmed. Current status: " + r.getStatus());
            return;
        }
        r.setStatus(ReservationStatus.CONFIRMED);
        repo.save();
        System.out.println(r.getReference() + " is now CONFIRMED.");
    }

    /** MY PART: check in a guest when they arrive. */
    private void checkInGuest() {
        Reservation r = askForReservation();
        if (r == null) return;
        if (r.getStatus() != ReservationStatus.CONFIRMED) {
            System.out.println("Only CONFIRMED bookings can be checked in. Current status: " + r.getStatus());
            return;
        }
        CustomerMenu.printDetails(r);
        if (r.getBalance().signum() > 0) {
            System.out.printf("Note: guest still has an unpaid balance of P%,.2f.%n", r.getBalance());
        }
        if (InputHelper.confirm("Check in " + r.getGuestName() + " now?")) {
            r.setStatus(ReservationStatus.CHECKED_IN);
            repo.save();
            System.out.println("Guest checked in. Status is now CHECKED IN.");
        } else {
            System.out.println("Check-in cancelled.");
        }
    }

    private Reservation askForReservation() {
        String ref = InputHelper.readText("Enter booking reference: ");
        Reservation r = repo.findByReference(ref);
        if (r == null) System.out.println("No booking found with that reference.");
        return r;
    }

    private void printTable(List<Reservation> list) {
        if (list.isEmpty()) {
            System.out.println("\nNo reservations to show.");
            return;
        }
        String line = "-".repeat(118);
        System.out.println("\n" + line);
        System.out.printf("%-17s %-18s %-20s %-11s %-11s %-11s %-14s %-9s%n",
                "REFERENCE", "GUEST", "PACKAGE", "CHECK-IN", "STATUS", "TOTAL", "PAYMENT", "BALANCE");
        System.out.println(line);
        for (Reservation r : list) {
            System.out.printf("%-17s %-18s %-20s %-11s %-11s %-11s %-14s %-9s%n",
                    r.getReference(), cut(r.getGuestName(), 18), cut(r.getPackageName(), 20),
                    r.getCheckIn(), r.getStatus(),
                    String.format("%,.0f", r.getTotalCost()), r.getPaymentStatus(),
                    String.format("%,.0f", r.getBalance()));
        }
        System.out.println(line);
        System.out.println("Total: " + list.size() + " reservation(s)");
    }

    private String cut(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + ".";
    }
}
