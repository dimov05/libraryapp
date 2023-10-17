package bg.libapp.libraryapp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Table
@Entity(name = "rent")
public class Rent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "rent_date", nullable = false)
    private LocalDate rentDate;
    @Column(name = "expected_return_date", nullable = false)
    private LocalDate expectedReturnDate;
    @Column(name = "actual_return_date", nullable = false)
    private LocalDate actualReturnDate;
    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "book_isbn", nullable = false)
    private Book book;

    public Rent() {
    }

    public Rent(LocalDate rentDate, LocalDate expectedReturnDate, LocalDate actualReturnDate, User user, Book book) {
        this.rentDate = rentDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.user = user;
        this.book = book;
    }

    public long getId() {
        return id;
    }

    public Rent setId(long id) {
        this.id = id;
        return this;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public Rent setRentDate(LocalDate rentDate) {
        this.rentDate = rentDate;
        return this;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public Rent setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
        return this;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public Rent setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Rent setUser(User user) {
        this.user = user;
        return this;
    }

    public Book getBook() {
        return book;
    }

    public Rent setBook(Book book) {
        this.book = book;
        return this;
    }
}
