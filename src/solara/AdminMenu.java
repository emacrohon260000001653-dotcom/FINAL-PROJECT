package solara;

import java.util.List;

public class AdminMenu {
    private final ReservationRepository repo;
    private final PriceList prices;
    private final UserRepository users;
    private final PaymentManager payments;
    private User currentUser;
    private boolean isAdmin;

    public AdminMenu(ReservationRepository repo, PriceList prices, UserRepository users) {
        this.repo = repo;
        this.prices = prices;
        this.users = users;
        this.payments = new PaymentManager(repo);
    }

    /** Shows the menu for the logged-in ADMIN or STAFF user. Returns when they log out. */
    public void show(User user) {
        this.currentUser = user;
        this.isAdmin = user.getRole().equals("ADMIN");

        while (true) {
            System.out.println("\n===== " + user.getRole() + " MENU =====");
            System.out.println("1. View All Reservations");
            System.out.println("2. Search Reservation (reference or guest name)");
            System.out.println("3. View Reservations by Status");
            System.out.println("4. Reservation Management");
            System.out.println("5. Payments");
            if (isAdmin) {
                System.out.println("6. Manage User Accounts");
            }
            System.out.println("0. Log out");
            int choice = InputHelper.readInt("Choose: ", 0, isAdmin ? 6 : 5);
            switch (choice) {
                case 1 -> { printTable(repo.getAll()); InputHelper.pause(); }
                case 2 -> search();
                case 3 -> viewByStatus();
                case 4 -> managementMenu();
                case 5 -> payments.show();
                case 6 -> manageUsers();
                case 0 -> { System.out.println("Logged out."); return; }
            }
        }
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
            if (isAdmin) {
                System.out.println("3. Mark a CHECKED IN booking COMPLETED");
                System.out.println("4. Cancel a booking");
                System.out.println("5. Delete a booking record");
            }
            System.out.println("0. Back");
            int choice = InputHelper.readInt("Choose: ", 0, isAdmin ? 5 : 2);
            switch (choice) {
                case 1 -> confirmBooking();
                case 2 -> checkInGuest();
                case 3 -> completeBooking();
                case 4 -> cancelBooking();
                case 5 -> deleteBooking();
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

    private void completeBooking() {
        Reservation r = askForReservation();
        if (r == null) return;
        if (r.getStatus() != ReservationStatus.CHECKED_IN) {
            System.out.println("Only CHECKED IN bookings can be marked COMPLETED. Current status: " + r.getStatus());
            return;
        }
        CustomerMenu.printDetails(r);
        if (r.getBalance().signum() > 0) {
            System.out.printf("Warning: guest still has an unpaid balance of P%,.2f.%n", r.getBalance());
        }
        if (InputHelper.confirm("Mark " + r.getGuestName() + "'s stay as COMPLETED?")) {
            r.setStatus(ReservationStatus.COMPLETED);
            repo.save();
            System.out.println(r.getReference() + " is now COMPLETED.");
        } else {
            System.out.println("No changes made.");
        }
    }

    private void cancelBooking() {
        Reservation r = askForReservation();
        if (r == null) return;
        if (r.getStatus() == ReservationStatus.CANCELLED || r.getStatus() == ReservationStatus.COMPLETED) {
            System.out.println("This booking is already " + r.getStatus() + " and cannot be cancelled.");
            return;
        }
        if (r.getStatus() == ReservationStatus.CHECKED_IN) {
            System.out.println("The guest is already checked in. Mark the stay COMPLETED instead.");
            return;
        }
        CustomerMenu.printDetails(r);
        String reason = InputHelper.readText("Cancellation reason: ");
        if (InputHelper.confirm("Cancel booking " + r.getReference() + "?")) {
            r.setStatus(ReservationStatus.CANCELLED);
            r.setNote("Cancelled on " + java.time.LocalDate.now() + ": " + reason);
            repo.save();
            System.out.println(r.getReference() + " is now CANCELLED.");
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    /** DELETE: permanently removes a booking. Only CANCELLED or COMPLETED bookings can be deleted. */
    private void deleteBooking() {
        Reservation r = askForReservation();
        if (r == null) return;
        if (r.getStatus() != ReservationStatus.CANCELLED && r.getStatus() != ReservationStatus.COMPLETED) {
            System.out.println("Only CANCELLED or COMPLETED bookings can be deleted. Current status: " + r.getStatus());
            return;
        }
        CustomerMenu.printDetails(r);
        if (InputHelper.confirm("Permanently delete " + r.getReference() + "? This cannot be undone")) {
            repo.delete(r);
            System.out.println("Booking record deleted.");
        } else {
            System.out.println("Nothing was deleted.");
        }
    }

    // ---------- Manage user accounts (ADMIN only) ----------
    private void manageUsers() {
        while (true) {
            System.out.println("\n--- MANAGE USER ACCOUNTS ---");
            System.out.println("1. View all accounts");
            System.out.println("2. Create an account");
            System.out.println("3. Update an account (password / role)");
            System.out.println("4. Delete an account");
            System.out.println("0. Back");
            int choice = InputHelper.readInt("Choose: ", 0, 4);
            switch (choice) {
                case 1 -> listUsers();
                case 2 -> createUser();
                case 3 -> updateUser();
                case 4 -> deleteUser();
                case 0 -> { return; }
            }
        }
    }

    private void listUsers() {
        System.out.println("\n" + String.format("%-16s %-24s %-10s", "USERNAME", "FULL NAME", "ROLE"));
        System.out.println("-".repeat(52));
        for (int i = 0; i < users.getAll().size(); i++) {
            User u = users.getAll().get(i);
            System.out.println(String.format("%-16s %-24s %-10s", u.getUsername(), u.getFullName(), u.getRole()));
        }
    }

    private String askRole() {
        System.out.println("1. ADMIN\n2. STAFF\n3. CUSTOMER");
        int pick = InputHelper.readInt("Choose role: ", 1, 3);
        if (pick == 1) return "ADMIN";
        if (pick == 2) return "STAFF";
        return "CUSTOMER";
    }

    private void createUser() {
        String username = InputHelper.readText("New username: ");
        if (username.contains("|") || username.contains(" ")) {
            System.out.println("  Error: username cannot contain spaces or the | symbol.");
            return;
        }
        if (users.findByUsername(username) != null) {
            System.out.println("  Error: that username already exists.");
            return;
        }
        String fullName = InputHelper.readText("Full name: ");
        String password = InputHelper.readText("Password: ");
        if (password.contains("|")) {
            System.out.println("  Error: password cannot contain the | symbol.");
            return;
        }
        String role = askRole();
        users.add(new User(username, password, fullName, role));
        System.out.println("Account created: " + username + " (" + role + ")");
    }

    private void updateUser() {
        User u = users.findByUsername(InputHelper.readText("Username to update: "));
        if (u == null) {
            System.out.println("No account with that username.");
            return;
        }
        System.out.println("1. Change password\n2. Change role");
        int pick = InputHelper.readInt("Choose: ", 1, 2);
        if (pick == 1) {
            String password = InputHelper.readText("New password: ");
            if (password.contains("|")) {
                System.out.println("  Error: password cannot contain the | symbol.");
                return;
            }
            u.setPassword(password);
        } else {
            if (u == currentUser) {
                System.out.println("You cannot change your own role.");
                return;
            }
            u.setRole(askRole());
        }
        users.save();
        System.out.println("Account updated.");
    }

    private void deleteUser() {
        User u = users.findByUsername(InputHelper.readText("Username to delete: "));
        if (u == null) {
            System.out.println("No account with that username.");
            return;
        }
        if (u == currentUser) {
            System.out.println("You cannot delete the account you are logged in with.");
            return;
        }
        if (InputHelper.confirm("Delete account " + u.getUsername() + "?")) {
            users.delete(u);
            System.out.println("Account deleted.");
        } else {
            System.out.println("Nothing was deleted.");
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
