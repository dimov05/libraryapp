package bg.libapp.libraryapp.model.dto.user;

public class UserDTO {
    private long id;
    private String username;

    private String firstName;

    private String lastName;
    private String displayName;
    private String dateOfBirth;

    private String role;

    private boolean isActive;
    private String subscription;
    private String balance;

    public UserDTO() {
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getRole() {
        return role;
    }

    public UserDTO setId(long id) {
        this.id = id;
        return this;
    }

    public UserDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserDTO setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserDTO setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserDTO setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public UserDTO setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public UserDTO setRole(String role) {
        this.role = role;
        return this;
    }

    public boolean isActive() {
        return isActive;
    }

    public UserDTO setActive(boolean active) {
        isActive = active;
        return this;
    }

    public String getSubscription() {
        return subscription;
    }

    public UserDTO setSubscription(String subscription) {
        this.subscription = subscription;
        return this;
    }

    public String getBalance() {
        return balance;
    }

    public UserDTO setBalance(String balance) {
        this.balance = balance;
        return this;
    }

    @Override
    public String toString() {
        return "{" + "id=" + id + ", username='" + username + '\'' + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", displayName='" + displayName + '\'' + ", dateOfBirth=" + dateOfBirth + ", role='" + role + '\'' + ", isActive=" + isActive + ", subscription='" + subscription + '\'' + ", balance='" + balance + '\'' + '}';
    }
}