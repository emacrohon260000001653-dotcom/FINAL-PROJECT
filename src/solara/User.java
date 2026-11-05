package solara;

/** One system account. Role is ADMIN, STAFF or CUSTOMER. */
public class User {
    private String username;
    private String password;
    private String fullName;
    private String role;

    public User(String username, String password, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    /** One line in data/users.txt: username|password|fullName|role */
    public String toFileLine() {
        return username + "|" + password + "|" + fullName.replace("|", "/") + "|" + role;
    }

    public static User fromFileLine(String line) {
        String[] parts = line.split("\\|");
        return new User(parts[0], parts[1], parts[2], parts[3]);
    }
}
