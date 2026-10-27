package solara;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Loads and saves reservations in data/reservations.txt. */
public class ReservationRepository {
    private static final Path FILE = Paths.get("data", "reservations.txt");
    private final List<Reservation> reservations = new ArrayList<>();

    public ReservationRepository() {
        load();
    }

    private void load() {
        if (!Files.exists(FILE)) return;
        try {
            for (String line : Files.readAllLines(FILE)) {
                if (line.isBlank()) continue;
                try {
                    reservations.add(Reservation.fromFileLine(line));
                } catch (RuntimeException e) {
                    System.out.println("Skipped a damaged record in reservations.txt");
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read reservations: " + e.getMessage());
        }
    }

    public void save() {
        List<String> lines = new ArrayList<>();
        for (Reservation r : reservations) lines.add(r.toFileLine());
        try {
            Files.createDirectories(FILE.getParent());
            Files.write(FILE, lines);
        } catch (IOException e) {
            System.out.println("Error saving reservations: " + e.getMessage());
        }
    }

    public void add(Reservation r) {
        reservations.add(r);
        save();
    }

    public List<Reservation> getAll() { return reservations; }

    public Reservation findByReference(String ref) {
        for (Reservation r : reservations) {
            if (r.getReference().equalsIgnoreCase(ref.trim())) return r;
        }
        return null;
    }

    public List<Reservation> searchByName(String text) {
        List<Reservation> found = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getGuestName().toLowerCase().contains(text.toLowerCase().trim())) found.add(r);
        }
        return found;
    }

    public List<Reservation> findByStatus(ReservationStatus s) {
        List<Reservation> found = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getStatus() == s) found.add(r);
        }
        return found;
    }

    /** Creates a reference like SOL-20261027-A3F9 that is not used yet. */
    public String generateReference() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random rnd = new Random();
        String ref;
        do {
            StringBuilder sb = new StringBuilder("SOL-");
            sb.append(java.time.LocalDate.now().toString().replace("-", "")).append("-");
            for (int i = 0; i < 4; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
            ref = sb.toString();
        } while (findByReference(ref) != null);
        return ref;
    }
}
