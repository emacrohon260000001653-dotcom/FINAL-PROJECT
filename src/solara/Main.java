package solara;

public class Main {
    public static void main(String[] args) {
        PriceList prices = new PriceList();
        ReservationRepository repo = new ReservationRepository();
        UserRepository users = new UserRepository();
        CustomerMenu customerMenu = new CustomerMenu(repo, prices);
        AdminMenu adminMenu = new AdminMenu(repo, prices, users);

        System.out.println("=========================================");
        System.out.println("   SOLARA RESORT BOOKING & MANAGEMENT");
        System.out.println("   (Academic simulation - no real money)");
        System.out.println("=========================================");

        while (true) {
            System.out.println("\n===== WELCOME =====");
            System.out.println("1. Log in");
            System.out.println("2. Create a customer account");
            System.out.println("0. Exit");
            int choice = InputHelper.readInt("Choose: ", 0, 2);

            if (choice == 1) {
                User user = login(users);
                if (user != null) {
                    System.out.println("\nWelcome, " + user.getFullName()
                            + " (" + user.getRole() + ")");
                    if (user.getRole().equals("CUSTOMER")) {
                        customerMenu.show();
                    } else {
                        adminMenu.show(user);
                    }
                    System.out.println("You have been logged out.");
                }
            } else if (choice == 2) {
                register(users);
            } else {
                System.out.println("Thank you for choosing Solara Resort. Goodbye!");
                return;
            }
        }
    }

    /** Gives the user 3 tries to log in. Returns null if all fail. */
    private static User login(UserRepository users) {
        System.out.println("\n--- LOG IN ---");
        for (int attempt = 1; attempt <= 3; attempt++) {
            String username = InputHelper.readText("Username: ");
            String password = InputHelper.readText("Password: ");
            User user = users.login(username, password);
            if (user != null) {
                return user;
            }
            System.out.println("Wrong username or password. Attempts left: " + (3 - attempt));
        }
        System.out.println("Too many failed attempts.");
        return null;
    }

    /** Anyone can create a CUSTOMER account. Staff and admin accounts are made by the admin. */
    private static void register(UserRepository users) {
        System.out.println("\n--- CREATE CUSTOMER ACCOUNT ---");
        String fullName = InputHelper.readText("Full name: ");
        String username = InputHelper.readText("Choose a username: ");
        if (username.contains("|") || username.contains(" ")) {
            System.out.println("  Error: username cannot contain spaces or the | symbol.");
            return;
        }
        if (users.findByUsername(username) != null) {
            System.out.println("  Error: that username is already taken.");
            return;
        }
        String password = InputHelper.readText("Choose a password: ");
        if (password.contains("|")) {
            System.out.println("  Error: password cannot contain the | symbol.");
            return;
        }
        users.add(new User(username, password, fullName, "CUSTOMER"));
        System.out.println("Account created. You can now log in.");
    }
}
