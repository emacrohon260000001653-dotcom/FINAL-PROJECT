import java.time.LocalDate;

public class AdminMenu {

    public void managementMenu(int choice, Booking selectedBooking, BookingRepository repo, PricingService pricingService) {
        switch (choice) {
            case 1:
                System.out.println("Option 1 implementation goes here.");
                break;
            case 2:
                System.out.println("Option 2 implementation goes here.");
                break;
            case 3:
                if (selectedBooking.getStatus() != BookingStatus.CHECKED_IN) {
                    System.out.println("Error: Only a CHECKED_IN booking can become COMPLETED.");
                    break;
                }
                double balance3 = selectedBooking.getTotalAmount() - selectedBooking.getPaidAmount();
                if (balance3 > 0) {
                    System.out.println("WARNING: This booking still has an outstanding balance of " + balance3 + ".");
                }
                selectedBooking.setStatus(BookingStatus.COMPLETED);
                repo.save(selectedBooking);
                System.out.println("Booking marked as COMPLETED.");
                break;

            case 4:
                if (selectedBooking.getStatus() == BookingStatus.COMPLETED || selectedBooking.getStatus() == BookingStatus.CANCELLED) {
                    System.out.println("Error: Cannot cancel a booking that is already COMPLETED or CANCELLED.");
                    break;
                }
                System.out.print("Enter reason for cancellation: ");
                String reason = InputHelper.readText();
                
                if (InputHelper.confirm("Cancel this booking?")) {
                    selectedBooking.setStatus(BookingStatus.CANCELLED);
                    selectedBooking.setNote("Cancelled on " + LocalDate.now() + ": " + reason);
                    repo.save(selectedBooking);
                    System.out.println("Booking successfully cancelled.");
                } else {
                    System.out.println("Cancellation aborted.");
                }
                break;

            case 5:
                PaymentManager paymentManager = new PaymentManager(repo);
                paymentManager.managePayment(selectedBooking);
                break;

            case 6:
                PriceEditor priceEditor = new PriceEditor(pricingService);
                priceEditor.showPriceMenu();
                break;

            default:
                System.out.println("Invalid option.");
                break;
        }
    }
}
