import java.time.LocalDate;

public class PaymentManager {
    private BookingRepository repo;

    public PaymentManager(BookingRepository repo) {
        this.repo = repo;
    }

    public void managePayment(Booking booking) {
        System.out.println("\n--- Payment Details ---");
        System.out.println("Total Amount: " + booking.getTotalAmount());
        System.out.println("Amount Paid: " + booking.getPaidAmount());
        double balance = booking.getTotalAmount() - booking.getPaidAmount();
        System.out.println("Balance: " + balance);
        System.out.println("Payment Status: " + booking.getPaymentStatus());

        if (balance <= 0) {
            System.out.println("This booking is fully paid.");
            return;
        }

        System.out.print("Enter payment amount to simulate: ");
        double payment = InputHelper.readMoney();

        if (payment > balance) {
            System.out.println("Error: Payment amount cannot exceed the remaining balance (" + balance + ").");
            return;
        }

        booking.setPaidAmount(booking.getPaidAmount() + payment);
        
        if (booking.getPaidAmount() >= booking.getTotalAmount()) {
            booking.setPaymentStatus("PAID");
        } else {
            booking.setPaymentStatus("PARTIALLY_PAID");
        }

        repo.save(booking);
        System.out.println("Payment successfully recorded. New balance: " + (booking.getTotalAmount() - booking.getPaidAmount()));
    }
}
