public class AdminOption {
    public void managementMenu(int choice, Booking selectedBooking, BookingRepository repo) {
        switch (choice) {
            case 3:
                if (selectedBooking.getStatus() != BookingStatus.CHECKED_IN) {
                    System.out.println("Error: Only a CHECKED_IN booking can become COMPLETED.");
                    break;
                }
                double balance = selectedBooking.getTotalAmount() - selectedBooking.getPaidAmount();
                if (balance > 0) {
                    System.out.println("Warning: The booking balance is above 0.");
                }
                selectedBooking.setStatus(BookingStatus.COMPLETED);
                repo.save(selectedBooking);
                System.out.println("Booking marked as COMPLETED.");
                break;

            case 4:
                if (selectedBooking.getStatus() == BookingStatus.COMPLETED || selectedBooking.getStatus() == BookingStatus.CANCELLED) {
                    System.out.println("Error: Booking is already COMPLETED or CANCELLED.");
                    break;
                }
                System.out.print("Enter cancellation reason: ");
                String reason = InputHelper.readText();

                if (InputHelper.confirm("Cancel this booking?")) {
                    selectedBooking.setStatus(BookingStatus.CANCELLED);
                    selectedBooking.setNote("Cancelled on " + java.time.LocalDate.now() + ": " + reason);
                    repo.save(selectedBooking);
                    System.out.println("Booking cancelled and saved.");
                } else {
                    System.out.println("Cancellation aborted.");
                }
                break;

            case 5:
                PaymentManager paymentManager = new PaymentManager(repo);
                paymentManager.showAndProcessPayment(selectedBooking);
                break;

            default:
                System.out.println("Invalid option.");
                break;
        }
    }
}
