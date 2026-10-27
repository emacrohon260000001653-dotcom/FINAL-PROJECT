package solara;

import java.math.BigDecimal;

/** One accommodation package (Standard, Deluxe, etc.). */
public class RoomPackage {
    private final String name;
    private BigDecimal nightlyRate;
    private final int maxGuests;
    private final int breakfastGuests;

    public RoomPackage(String name, BigDecimal nightlyRate, int maxGuests, int breakfastGuests) {
        this.name = name;
        this.nightlyRate = nightlyRate;
        this.maxGuests = maxGuests;
        this.breakfastGuests = breakfastGuests;
    }

    public String getName() { return name; }
    public BigDecimal getNightlyRate() { return nightlyRate; }
    public void setNightlyRate(BigDecimal rate) { this.nightlyRate = rate; }
    public int getMaxGuests() { return maxGuests; }
    public int getBreakfastGuests() { return breakfastGuests; }
}
