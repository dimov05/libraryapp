package bg.libapp.libraryapp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table
@Entity(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;
    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;
    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;
    @Column(name = "display_name", length = 50, nullable = false)
    private String displayName;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    @Column(name = "role", nullable = false)
    private int role;
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;
    @OneToMany(mappedBy = "user")
    private List<Rent> rents;
    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;
    @Column(name = "has_unsubscribed")
    private boolean hasUnsubscribed;

    public User() {
    }

    public User(long id, String username, String firstName, String lastName, String displayName, String password, LocalDate dateOfBirth, int role, boolean isActive, BigDecimal balance, List<Rent> rents, Subscription subscription, boolean hasUnsubscribed) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
        this.isActive = isActive;
        this.balance = balance;
        this.rents = rents;
        this.subscription = subscription;
        this.hasUnsubscribed = hasUnsubscribed;
    }

    public long getId() {
        return id;
    }

    public User setId(long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public User setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public User setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public int getRole() {
        return role;
    }

    public User setRole(int role) {
        this.role = role;
        return this;
    }

    public boolean isActive() {
        return isActive;
    }

    public User setActive(boolean active) {
        isActive = active;
        return this;
    }

    public List<Rent> getRents() {
        return rents;
    }

    public User setRents(List<Rent> rents) {
        this.rents = rents;
        return this;
    }

    public void addRent(Rent rent) {
        if (this.rents == null) this.rents = new ArrayList<>();
        this.rents.add(rent);
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public User setSubscription(Subscription subscription) {
        this.subscription = subscription;
        return this;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public User setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public boolean isHasUnsubscribed() {
        return hasUnsubscribed;
    }

    public User setHasUnsubscribed(boolean hasUnsubscribed) {
        this.hasUnsubscribed = hasUnsubscribed;
        return this;
    }
}
