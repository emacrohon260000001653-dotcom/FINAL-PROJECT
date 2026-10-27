package solara;

import java.math.BigDecimal;
import java.util.Map;

/** Fills in the price breakdown of a Reservation using the current PriceList. */
public class PricingCalculator {

    /**
     * Rules:
     * - Adults fill the room's guest capacity first, then children aged 6-12.
     * - Adults above capacity pay the extra adult fee per night.
     * - Children 6-12 above remaining capacity pay the extra child fee per night.
     * - Children under 6 are free and don't use capacity.
     */
    public static void applyPrices(Reservation r, RoomPackage room, PriceList prices,
                                   int extraBreakfastPeople, Map<String, Integer> chosenAddOns) {
        int nights = r.getNights();
        BigDecimal n = BigDecimal.valueOf(nights);

        r.setRoomCost(room.getNightlyRate().multiply(n));

        int extraAdults = Math.max(0, r.getAdults() - room.getMaxGuests());
        int spotsLeft = Math.max(0, room.getMaxGuests() - r.getAdults());
        int extraChildren = Math.max(0, r.getChildren6to12() - spotsLeft);

        r.setExtraAdultCost(prices.getExtraAdultFee()
                .multiply(BigDecimal.valueOf(extraAdults)).multiply(n));
        r.setExtraChildCost(prices.getExtraChildFee()
                .multiply(BigDecimal.valueOf(extraChildren)).multiply(n));
        r.setExtraBreakfastCost(prices.getExtraBreakfastFee()
                .multiply(BigDecimal.valueOf(extraBreakfastPeople)).multiply(n));

        BigDecimal addOnTotal = BigDecimal.ZERO;
        StringBuilder summary = new StringBuilder();
        for (Map.Entry<String, Integer> e : chosenAddOns.entrySet()) {
            BigDecimal unit = prices.getAddOns().get(e.getKey());
            BigDecimal line = unit.multiply(BigDecimal.valueOf(e.getValue()));
            addOnTotal = addOnTotal.add(line);
            if (summary.length() > 0) summary.append("; ");
            summary.append(e.getKey()).append(" x").append(e.getValue())
                   .append(" (P").append(line.toPlainString()).append(")");
        }
        r.setAddOnCost(addOnTotal);
        r.setAddOnSummary(summary.length() == 0 ? "None" : summary.toString());
    }
}
