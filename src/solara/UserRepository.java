package solara;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Loads and saves accounts in data/users.txt. */
public class UserRepository {
    private List<User> users = new ArrayList<>();

    public UserRepository() {
        load();
        if (users.isEmpty()) {
            // First run: create the demo accounts.
            users.add(new User("admin", "solara123", "Resort Admin", "ADMIN"));
            users.add(new User("staff", "staff123", "Front Desk Staff", "STAFF"));
            users.add(new User("guest", "guest123", "Demo Guest", "CUSTOMER"));
            save();
        }
    }

    private void load() {
        File file = new File("data/users.txt");
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
                    users.add(User.fromFileLine(line));
                } catch (RuntimeException e) {
                    System.out.println("Skipped a damaged record in users.txt");
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read users: " + e.getMessage());
        }
    }

    public void save() {
        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        try (FileWriter writer = new FileWriter("data/users.txt")) {
            for (int i = 0; i < users.size(); i++) {
                writer.write(users.get(i).toFileLine());
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public List<User> getAll() {
        return users;
    }

    public User findByUsername(String username) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equalsIgnoreCase(username.trim())) {
                return users.get(i);
            }
        }
        return null;
    }

    /** Returns the user if username and password match, otherwise null. */
    public User login(String username, String password) {
        User user = findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void add(User user) {
        users.add(user);
        save();
    }

    public void delete(User user) {
        users.remove(user);
        save();
    }
}
