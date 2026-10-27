package solara;

public enum ReservationStatus {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    CHECKED_IN("CHECKED IN"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
