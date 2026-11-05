package solara;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/** Loads and saves reservations in data/reservations.txt. */
public class ReservationRepository {
    private List<Reservation> reservations = new ArrayList<>();

    public ReservationRepository() {
        load();
    }

    private void load() {
        File file = new File("data/reservations.txt");

        if (!file.exists()) {
            return;
        }

        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    Reservation reservation = Reservation.fromFileLine(line);
                    reservations.add(reservation);
                } catch (RuntimeException e) {
                    System.out.println("Skipped a damaged record in reservations.txt");
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read reservations: " + e.getMessage());
        }
    }

    public void save() {
        File folder = new File("data");

        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                System.out.println("Could not create the data folder.");
                return;
            }
        }

        try (FileWriter writer = new FileWriter("data/reservations.txt")) {
            for (int i = 0; i < reservations.size(); i++) {
                Reservation reservation = reservations.get(i);
                writer.write(reservation.toFileLine());
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error saving reservations: " + e.getMessage());
        }
    }

    public void add(Reservation reservation) {
        reservations.add(reservation);
        save();
    }

    public void delete(Reservation reservation) {
        reservations.remove(reservation);
        save();
    }

    public List<Reservation> getAll() {
        return reservations;
    }

    public Reservation findByReference(String reference) {
        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);

            if (reservation.getReference().equalsIgnoreCase(reference.trim())) {
                return reservation;
            }
        }

        return null;
    }

    public List<Reservation> searchByName(String name) {
        List<Reservation> results = new ArrayList<>();
        String searchText = name.trim().toLowerCase();

        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);
            String guestName = reservation.getGuestName().toLowerCase();

            if (guestName.contains(searchText)) {
                results.add(reservation);
            }
        }

        return results;
    }

    public List<Reservation> findByStatus(ReservationStatus status) {
        List<Reservation> results = new ArrayList<>();

        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);

            if (reservation.getStatus() == status) {
                results.add(reservation);
            }
        }

        return results;
    }

    /** Creates a reference like SOL-20261027-A3F9 that is not used yet. */
    public String generateReference() {
        String characters = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random random = new Random();
        String reference;

        do {
            String date = java.time.LocalDate.now().toString();
            date = date.replace("-", "");

            reference = "SOL-" + date + "-";

            for (int i = 0; i < 4; i++) {
                int position = random.nextInt(characters.length());
                reference = reference + characters.charAt(position);
            }
        } while (findByReference(reference) != null);

        return reference;
    }
}
