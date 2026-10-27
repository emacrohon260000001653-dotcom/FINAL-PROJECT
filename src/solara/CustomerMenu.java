package solara;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CustomerMenu {
    private final ReservationRepository repo;
    private final PriceList prices;

    public CustomerMenu(ReservationRepository repo, PriceList prices) {
        this.repo = repo;
        this.prices = prices;
    }

    public void show() {
        while (true) {
            System.out.println("\n===== CUSTOMER MENU =====");
            System.out.println("1. Make a Reservation");
            System.out.println("2. Track / View My Booking");
            System.out.println("3. Make a Payment (simulated)");
            System.out.println("0. Back");
            int choice = InputHelper.readInt("Choose: ", 0, 3);
            switch (choice) {
                case 1 -> makeReservation();
                case 2 -> trackBooking();
                case 3 -> payBooking();
                case 0 -> { return; }
            }
        }
    }

    private void makeReservation() {
        System.out.println("\n--- NEW RESERVATION ---");
        Reservation r = new Reservation();

        r.setGuestName(InputHelper.readText("Guest full name: "));
        r.setPhone(InputHelper.readText("Phone number: "));
        r.setEmail(InputHelper.readText("Email: "));

        // Dates
        LocalDate checkIn;
        while (true) {
            checkIn = InputHelper.readDate("Check-in date");
            if (!checkIn.isBefore(LocalDate.now())) break;
            System.out.println("  Error: check-in cannot be in the past.");
        }
        LocalDate checkOut;
        while (true) {
            checkOut = InputHelper.readDate("Check-out date");
            if (checkOut.isAfter(checkIn)) break;
            System.out.println("  Error: check-out must be after check-in.");
        }
        r.setCheckIn(checkIn);
        r.setCheckOut(checkOut);
        r.setNights((int) ChronoUnit.DAYS.between(checkIn, checkOut));

        // Guests
        r.setAdults(InputHelper.readInt("Number of adults (1-20): ", 1, 20));
        r.setChildren6to12(InputHelper.readInt("Number of children aged 6-12: ", 0, 20));
        r.setChildrenUnder6(InputHelper.readInt("Number of children under 6 (free): ", 0, 20));

        // Package
        System.out.println("\nAvailable packages (all include beach and pool access):");
        List<RoomPackage> list = prices.getPackages();
        for (int i = 0; i < list.size(); i++) {
            RoomPackage p = list.get(i);
            System.out.printf("%d. %-22s P%,9.2f/night | up to %d guests | breakfast for %d%n",
                    i + 1, p.getName(), p.getNightlyRate(), p.getMaxGuests(), p.getBreakfastGuests());
        }
        RoomPackage room = list.get(InputHelper.readInt("Choose package: ", 1, list.size()) - 1);
        r.setPackageName(room.getName());

        // Extra breakfast
        int totalPeople = r.getAdults() + r.getChildren6to12();
        int needBreakfast = Math.max(0, totalPeople - room.getBreakfastGuests());
        int extraBreakfast = 0;
        if (needBreakfast > 0) {
            System.out.printf("%nExtra breakfast costs P%,.2f per person per morning.%n",
                    prices.getExtraBreakfastFee());
            extraBreakfast = InputHelper.readInt(
                    "How many people want extra breakfast (0-" + needBreakfast + "): ", 0, needBreakfast);
        }

        // Add-ons
        Map<String, Integer> chosen = new LinkedHashMap<>();
        System.out.println("\nOptional luxury add-ons:");
        List<String> addOnNames = new ArrayList<>(prices.getAddOns().keySet());
        while (true) {
            for (int i = 0; i < addOnNames.size(); i++) {
                String name = addOnNames.get(i);
                System.out.printf("%d. %-22s P%,.2f%n", i + 1, name, prices.getAddOns().get(name));
            }
            System.out.println("0. Done");
            int pick = InputHelper.readInt("Add-on number: ", 0, addOnNames.size());
            if (pick == 0) break;
            String name = addOnNames.get(pick - 1);
            int qty = InputHelper.readInt("Quantity: ", 1, 20);
            chosen.merge(name, qty, Integer::sum);
            System.out.println("  Added " + name + " x" + qty);
        }

        PricingCalculator.applyPrices(r, room, prices, extraBreakfast, chosen);
        r.setReference(repo.generateReference());

        System.out.println("\n--- SUMMARY ---");
        printDetails(r);
        if (InputHelper.confirm("Confirm this reservation?")) {
            repo.add(r);
            System.out.println("\nReservation saved! Your booking reference is: " + r.getReference());
            System.out.println("Keep it safe. Status is PENDING until the resort confirms it.");
        } else {
            System.out.println("Reservation discarded.");
        }
        InputHelper.pause();
    }

    private void trackBooking() {
        String ref = InputHelper.readText("\nEnter booking reference: ");
        Reservation r = repo.findByReference(ref);
        if (r == null) {
            System.out.println("No booking found with reference " + ref + ".");
        } else {
            printDetails(r);
        }
        InputHelper.pause();
    }

    private void payBooking() {
        String ref = InputHelper.readText("\nEnter booking reference: ");
        Reservation r = repo.findByReference(ref);
        if (r == null) {
            System.out.println("No booking found.");
        } else if (r.getStatus() == ReservationStatus.CANCELLED) {
            System.out.println("This booking is cancelled. Payments are not accepted.");
        } else if (r.getBalance().signum() == 0) {
            System.out.println("This booking is already fully paid.");
        } else {
            System.out.printf("Total: P%,.2f | Paid: P%,.2f | Balance: P%,.2f%n",
                    r.getTotalCost(), r.getAmountPaid(), r.getBalance());
            BigDecimal amount = InputHelper.readMoney("Amount to pay (SIMULATED): P");
            if (amount.compareTo(r.getBalance()) > 0) {
                System.out.println("  Error: amount is more than the balance.");
            } else {
                r.setAmountPaid(r.getAmountPaid().add(amount));
                repo.save();
                System.out.printf("Payment recorded (simulation). Remaining balance: P%,.2f%n", r.getBalance());
                System.out.println("Payment status: " + r.getPaymentStatus());
            }
        }
        InputHelper.pause();
    }

    /** Shared by customer and admin menus. */
    public static void printDetails(Reservation r) {
        System.out.println("=========================================");
        System.out.println("Reference      : " + r.getReference());
        System.out.println("Guest          : " + r.getGuestName());
        System.out.println("Contact        : " + r.getPhone() + " / " + r.getEmail());
        System.out.println("Stay           : " + r.getCheckIn() + " to " + r.getCheckOut()
                + " (" + r.getNights() + " nights)");
        System.out.println("Guests         : " + r.getAdults() + " adults, " + r.getChildren6to12()
                + " children (6-12), " + r.getChildrenUnder6() + " under 6");
        System.out.println("Package        : " + r.getPackageName());
        System.out.println("-----------------------------------------");
        System.out.printf("Room cost      : P%,12.2f%n", r.getRoomCost());
        System.out.printf("Extra adults   : P%,12.2f%n", r.getExtraAdultCost());
        System.out.printf("Extra children : P%,12.2f%n", r.getExtraChildCost());
        System.out.printf("Extra breakfast: P%,12.2f%n", r.getExtraBreakfastCost());
        System.out.printf("Add-ons        : P%,12.2f  [%s]%n", r.getAddOnCost(), r.getAddOnSummary());
        System.out.println("-----------------------------------------");
        System.out.printf("TOTAL COST     : P%,12.2f%n", r.getTotalCost());
        System.out.printf("Amount paid    : P%,12.2f%n", r.getAmountPaid());
        System.out.printf("Balance        : P%,12.2f%n", r.getBalance());
        System.out.println("Payment status : " + r.getPaymentStatus());
        System.out.println("Booking status : " + r.getStatus());
        System.out.println("=========================================");
    }
}
