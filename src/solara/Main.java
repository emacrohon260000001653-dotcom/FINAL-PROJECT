package solara;

public class Main {
    public static void main(String[] args) {
        PriceList prices = new PriceList();
        ReservationRepository repo = new ReservationRepository();
        CustomerMenu customer = new CustomerMenu(repo, prices);
        AdminMenu admin = new AdminMenu(repo, prices);

        System.out.println("=========================================");
        System.out.println("   SOLARA RESORT BOOKING & MANAGEMENT");
        System.out.println("   (Academic simulation - no real money)");
        System.out.println("=========================================");

        while (true) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Customer Menu");
            System.out.println("2. Admin Access");
            System.out.println("0. Exit");
            int choice = InputHelper.readInt("Choose: ", 0, 2);
            switch (choice) {
                case 1 -> customer.show();
                case 2 -> admin.show();
                case 0 -> {
                    System.out.println("Thank you for choosing Solara Resort. Goodbye!");
                    return;
                }
            }
        }
    }
}
