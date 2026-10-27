package solara;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;

/** Holds all current prices and saves/loads them from data/prices.txt. */
public class PriceList {
    private static final Path FILE = Paths.get("data", "prices.txt");

    private final List<RoomPackage> packages = new ArrayList<>();
    private final Map<String, BigDecimal> addOns = new LinkedHashMap<>();
    private BigDecimal extraAdultFee = new BigDecimal("500");
    private BigDecimal extraChildFee = new BigDecimal("250");
    private BigDecimal extraBreakfastFee = new BigDecimal("350");

    public PriceList() {
        if (Files.exists(FILE)) {
            load();
        } else {
            loadDefaults();
            save();
        }
    }

    private void loadDefaults() {
        packages.add(new RoomPackage("Standard Room", new BigDecimal("1800"), 2, 0));
        packages.add(new RoomPackage("Deluxe Room", new BigDecimal("2800"), 3, 2));
        packages.add(new RoomPackage("Family Cottage", new BigDecimal("4500"), 6, 4));
        packages.add(new RoomPackage("Private Luxury Villa", new BigDecimal("7500"), 10, 6));

        addOns.put("Spa Treatment", new BigDecimal("2500"));
        addOns.put("Private Dinner", new BigDecimal("4000"));
        addOns.put("Beach Cabana", new BigDecimal("1500"));
        addOns.put("Airport Transfer", new BigDecimal("1800"));
        addOns.put("Celebration Setup", new BigDecimal("3500"));
        addOns.put("Island Hopping Tour", new BigDecimal("3000"));
        addOns.put("Snorkeling Set", new BigDecimal("800"));
    }

    private void load() {
        try {
            for (String line : Files.readAllLines(FILE)) {
                if (line.isBlank()) continue;
                String[] p = line.split("\\|");
                switch (p[0]) {
                    case "ROOM" -> packages.add(new RoomPackage(p[1], new BigDecimal(p[2]),
                            Integer.parseInt(p[3]), Integer.parseInt(p[4])));
                    case "ADDON" -> addOns.put(p[1], new BigDecimal(p[2]));
                    case "ADULT" -> extraAdultFee = new BigDecimal(p[1]);
                    case "CHILD" -> extraChildFee = new BigDecimal(p[1]);
                    case "BREAKFAST" -> extraBreakfastFee = new BigDecimal(p[1]);
                    default -> { }
                }
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not read prices file (" + e.getMessage() + "). Using defaults.");
            packages.clear();
            addOns.clear();
            loadDefaults();
        }
    }

    public void save() {
        List<String> lines = new ArrayList<>();
        for (RoomPackage r : packages) {
            lines.add("ROOM|" + r.getName() + "|" + r.getNightlyRate().toPlainString()
                    + "|" + r.getMaxGuests() + "|" + r.getBreakfastGuests());
        }
        for (Map.Entry<String, BigDecimal> e : addOns.entrySet()) {
            lines.add("ADDON|" + e.getKey() + "|" + e.getValue().toPlainString());
        }
        lines.add("ADULT|" + extraAdultFee.toPlainString());
        lines.add("CHILD|" + extraChildFee.toPlainString());
        lines.add("BREAKFAST|" + extraBreakfastFee.toPlainString());
        try {
            Files.createDirectories(FILE.getParent());
            Files.write(FILE, lines);
        } catch (IOException e) {
            System.out.println("Error saving prices: " + e.getMessage());
        }
    }

    public List<RoomPackage> getPackages() { return packages; }
    public Map<String, BigDecimal> getAddOns() { return addOns; }
    public BigDecimal getExtraAdultFee() { return extraAdultFee; }
    public BigDecimal getExtraChildFee() { return extraChildFee; }
    public BigDecimal getExtraBreakfastFee() { return extraBreakfastFee; }
    public void setExtraAdultFee(BigDecimal v) { extraAdultFee = v; }
    public void setExtraChildFee(BigDecimal v) { extraChildFee = v; }
    public void setExtraBreakfastFee(BigDecimal v) { extraBreakfastFee = v; }
}
