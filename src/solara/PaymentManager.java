public class PaymentManager {
    private BookingRepository repo;

    public PaymentManager(BookingRepository repo) {
        this.repo = repo;
    }

    public void showAndProcessPayment(Booking booking) {
        double total = booking.getTotalAmount();
        double paid = booking.getPaidAmount();
        double balance = total - paid;

        System.out.println("Total: " + total);
        System.out.println("Paid: " + paid);
        System.out.println("Balance: " + balance);
        System.out.println("Payment Status: " + booking.getPaymentStatus());

        System.out.print("Enter payment amount: ");
        double payment = InputHelper.readMoney();

        if (payment > balance) {
            System.out.println("Error: Payment amount cannot be above the balance.");
            return;
        }

        booking.setPaidAmount(paid + payment);
        repo.save(booking);
        System.out.println("Payment saved successfully.");
    }
}
