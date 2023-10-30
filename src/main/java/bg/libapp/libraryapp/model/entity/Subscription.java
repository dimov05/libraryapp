package bg.libapp.libraryapp.model.entity;

import bg.libapp.libraryapp.model.enums.SubscriptionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "subscription_type")
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;
    @Column(name = "days_allowed")
    private int daysAllowed;
    @Column(name = "rents_allowed")
    private int rentsAllowed;
    @Column(name = "price")
    private BigDecimal price;

    public Subscription() {
    }

    public Subscription(long id, SubscriptionType subscriptionType, int daysAllowed, int rentsAllowed, BigDecimal price) {
        this.id = id;
        this.subscriptionType = subscriptionType;
        this.daysAllowed = daysAllowed;
        this.rentsAllowed = rentsAllowed;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public Subscription setId(long id) {
        this.id = id;
        return this;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public Subscription setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
        return this;
    }

    public int getDaysAllowed() {
        return daysAllowed;
    }

    public Subscription setDaysAllowed(int daysAllowed) {
        this.daysAllowed = daysAllowed;
        return this;
    }

    public int getRentsAllowed() {
        return rentsAllowed;
    }

    public Subscription setRentsAllowed(int rentsAllowed) {
        this.rentsAllowed = rentsAllowed;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Subscription setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }
}
