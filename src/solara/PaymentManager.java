package solara;

import java.math.BigDecimal;

/** Admin payment updates (simulated). Used from the admin "Payments" menu. */
public class PaymentManager {
    private final ReservationRepository repo;

    public PaymentManager(ReservationRepository repo) {
        this.repo = repo;
    }

    public void show() {
        while (true) {
            System.out.println("\n--- PAYMENTS ---");
            System.out.println("1. Record a payment");
            System.out.println("2. Mark a booking as fully paid");
            System.out.println("0. Back");
            int choice = InputHelper.readInt("Choose: ", 0, 2);
            switch (choice) {
                case 1 -> recordPayment();
                case 2 -> markFullyPaid();
                case 0 -> { return; }
            }
        }
    }

    private void recordPayment() {
        Reservation r = askForPayableReservation();
        if (r == null) return;

        BigDecimal amount = InputHelper.readMoney("Payment amount received (SIMULATED): P");
        if (amount.compareTo(r.getBalance()) > 0) {
            System.out.println("  Error: payment cannot be more than the balance.");
            InputHelper.pause();
            return;
        }
        r.setAmountPaid(r.getAmountPaid().add(amount));
        repo.save();
        System.out.printf("Payment recorded. Remaining balance: P%,.2f%n", r.getBalance());
        System.out.println("Payment status: " + r.getPaymentStatus());
        InputHelper.pause();
    }

    private void markFullyPaid() {
        Reservation r = askForPayableReservation();
        if (r == null) return;

        if (InputHelper.confirm(String.format("Record the remaining P%,.2f as paid?", r.getBalance()))) {
            r.setAmountPaid(r.getTotalCost());
            repo.save();
            System.out.println("Booking is now fully paid. Payment status: " + r.getPaymentStatus());
        } else {
            System.out.println("No changes made.");
        }
        InputHelper.pause();
    }

    /** Finds a booking, shows its payment summary, and returns it only if it can still accept payments. */
    private Reservation askForPayableReservation() {
        String ref = InputHelper.readText("\nEnter booking reference: ");
        Reservation r = repo.findByReference(ref);
        if (r == null) {
            System.out.println("No booking found with that reference.");
            InputHelper.pause();
            return null;
        }
        if (r.getStatus() == ReservationStatus.CANCELLED) {
            System.out.println("This booking is CANCELLED. Payments cannot be recorded.");
            InputHelper.pause();
            return null;
        }
        System.out.println("\nGuest          : " + r.getGuestName());
        System.out.printf("Total          : P%,.2f%n", r.getTotalCost());
        System.out.printf("Paid           : P%,.2f%n", r.getAmountPaid());
        System.out.printf("Balance        : P%,.2f%n", r.getBalance());
        System.out.println("Payment status : " + r.getPaymentStatus());
        if (r.getBalance().signum() == 0) {
            System.out.println("This booking is already fully paid.");
            InputHelper.pause();
            return null;
        }
        return r;
    }
}
