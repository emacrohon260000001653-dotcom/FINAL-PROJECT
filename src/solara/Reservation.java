package solara;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * One booking. The price breakdown (roomCost, extraAdultCost, etc.) is stored
 * inside the reservation, so later price changes by the admin will NOT affect it.
 */
public class Reservation {
    private String reference;
    private String guestName;
    private String phone;
    private String email;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int adults;
    private int children6to12;
    private int childrenUnder6;
    private String packageName;
    private int nights;

    // Price breakdown snapshot
    private BigDecimal roomCost = BigDecimal.ZERO;
    private BigDecimal extraAdultCost = BigDecimal.ZERO;
    private BigDecimal extraChildCost = BigDecimal.ZERO;
    private BigDecimal extraBreakfastCost = BigDecimal.ZERO;
    private BigDecimal addOnCost = BigDecimal.ZERO;
    private String addOnSummary = "None";

    private BigDecimal amountPaid = BigDecimal.ZERO;
    private ReservationStatus status = ReservationStatus.PENDING;
    private LocalDate createdOn = LocalDate.now();
    private String note = "";

    public Reservation() { }

    public BigDecimal getTotalCost() {
        return roomCost.add(extraAdultCost).add(extraChildCost)
                .add(extraBreakfastCost).add(addOnCost);
    }

    public BigDecimal getBalance() {
        BigDecimal balance = getTotalCost().subtract(amountPaid);
        return balance.max(BigDecimal.ZERO);
    }

    public PaymentStatus getPaymentStatus() {
        if (amountPaid.signum() <= 0) return PaymentStatus.UNPAID;
        if (amountPaid.compareTo(getTotalCost()) >= 0) return PaymentStatus.PAID;
        return PaymentStatus.PARTIALLY_PAID;
    }

    // ---------- File conversion ----------
    private static String clean(String s) {
        return s == null ? "" : s.replace("|", "/").replace("\n", " ").trim();
    }

    public String toFileLine() {
        return String.join("|",
                reference, clean(guestName), clean(phone), clean(email),
                checkIn.toString(), checkOut.toString(),
                String.valueOf(adults), String.valueOf(children6to12), String.valueOf(childrenUnder6),
                clean(packageName), String.valueOf(nights),
                roomCost.toPlainString(), extraAdultCost.toPlainString(),
                extraChildCost.toPlainString(), extraBreakfastCost.toPlainString(),
                addOnCost.toPlainString(), clean(addOnSummary),
                amountPaid.toPlainString(), status.name(),
                createdOn.toString(), clean(note));
    }

    public static Reservation fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        Reservation r = new Reservation();
        r.reference = p[0];
        r.guestName = p[1];
        r.phone = p[2];
        r.email = p[3];
        r.checkIn = LocalDate.parse(p[4]);
        r.checkOut = LocalDate.parse(p[5]);
        r.adults = Integer.parseInt(p[6]);
        r.children6to12 = Integer.parseInt(p[7]);
        r.childrenUnder6 = Integer.parseInt(p[8]);
        r.packageName = p[9];
        r.nights = Integer.parseInt(p[10]);
        r.roomCost = new BigDecimal(p[11]);
        r.extraAdultCost = new BigDecimal(p[12]);
        r.extraChildCost = new BigDecimal(p[13]);
        r.extraBreakfastCost = new BigDecimal(p[14]);
        r.addOnCost = new BigDecimal(p[15]);
        r.addOnSummary = p[16];
        r.amountPaid = new BigDecimal(p[17]);
        r.status = ReservationStatus.valueOf(p[18]);
        r.createdOn = LocalDate.parse(p[19]);
        r.note = p.length > 20 ? p[20] : "";
        return r;
    }

    // ---------- Getters and setters ----------
    public String getReference() { return reference; }
    public void setReference(String v) { reference = v; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String v) { guestName = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { phone = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate v) { checkIn = v; }
    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate v) { checkOut = v; }
    public int getAdults() { return adults; }
    public void setAdults(int v) { adults = v; }
    public int getChildren6to12() { return children6to12; }
    public void setChildren6to12(int v) { children6to12 = v; }
    public int getChildrenUnder6() { return childrenUnder6; }
    public void setChildrenUnder6(int v) { childrenUnder6 = v; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String v) { packageName = v; }
    public int getNights() { return nights; }
    public void setNights(int v) { nights = v; }
    public BigDecimal getRoomCost() { return roomCost; }
    public void setRoomCost(BigDecimal v) { roomCost = v; }
    public BigDecimal getExtraAdultCost() { return extraAdultCost; }
    public void setExtraAdultCost(BigDecimal v) { extraAdultCost = v; }
    public BigDecimal getExtraChildCost() { return extraChildCost; }
    public void setExtraChildCost(BigDecimal v) { extraChildCost = v; }
    public BigDecimal getExtraBreakfastCost() { return extraBreakfastCost; }
    public void setExtraBreakfastCost(BigDecimal v) { extraBreakfastCost = v; }
    public BigDecimal getAddOnCost() { return addOnCost; }
    public void setAddOnCost(BigDecimal v) { addOnCost = v; }
    public String getAddOnSummary() { return addOnSummary; }
    public void setAddOnSummary(String v) { addOnSummary = v; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal v) { amountPaid = v; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus v) { status = v; }
    public LocalDate getCreatedOn() { return createdOn; }
    public String getNote() { return note; }
    public void setNote(String v) { note = v; }
}
