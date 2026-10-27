package solara;

public enum PaymentStatus {
    UNPAID("UNPAID"),
    PARTIALLY_PAID("PARTIALLY PAID"),
    PAID("PAID");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
