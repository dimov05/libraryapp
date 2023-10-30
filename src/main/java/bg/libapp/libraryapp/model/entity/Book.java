package bg.libapp.libraryapp.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Table
@Entity(name = "book")
public class Book {
    @Id
    @Column(name = "isbn", length = 17)
    private String isbn;
    @Column(name = "title", length = 150, nullable = false)
    private String title;
    @Column(name = "year", nullable = false)
    private int year;
    @Column(name = "publisher", length = 100, nullable = false)
    private String publisher;
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;
    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;
    @Column(name = "deactivate_reason")
    private String deactivateReason;
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime dateAdded;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "book_genre",
            joinColumns = @JoinColumn(name = "isbn"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres;
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "book_author",
            joinColumns = @JoinColumn(name = "isbn"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<Author> authors;
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<BookAudit> audits;

    public Book() {
    }

    public Book(String isbn, String title, int year, String publisher, boolean isActive, int availableQuantity, int totalQuantity, String deactivateReason, LocalDateTime dateAdded, Set<Genre> genres, Set<Author> authors, Set<BookAudit> audits) {
        this.isbn = isbn;
        this.title = title;
        this.year = year;
        this.publisher = publisher;
        this.isActive = isActive;
        this.availableQuantity = availableQuantity;
        this.totalQuantity = totalQuantity;
        this.deactivateReason = deactivateReason;
        this.dateAdded = dateAdded;
        this.genres = genres;
        this.authors = authors;
        this.audits = audits;
    }

    public String getIsbn() {
        return isbn;
    }

    public Book setIsbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Book setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getYear() {
        return year;
    }

    public Book setYear(int year) {
        this.year = year;
        return this;
    }

    public String getPublisher() {
        return publisher;
    }

    public Book setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public Book setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
        return this;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public Book setGenres(Set<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public Book setAuthors(Set<Author> authors) {
        this.authors = authors;
        return this;
    }

    public void addAuthor(Author author) {
        if (this.authors == null) {
            this.authors = new HashSet<>();
        }
        this.authors.add(author);
    }

    public void removeAuthor(Author author) {
        this.authors.remove(author);
    }

    public void addGenre(Genre genre) {
        if (this.genres == null) {
            this.genres = new HashSet<>();
        }
        this.genres.add(genre);
    }

    public void removeGenre(Genre genre) {
        this.genres.remove(genre);
    }

    public Set<BookAudit> getAudits() {
        return audits;
    }

    public Book setAudits(Set<BookAudit> audits) {
        this.audits = audits;
        return this;
    }

    public void addAudit(BookAudit audit) {
        if (this.audits == null) {
            this.audits = new HashSet<>();
        }
        this.audits.add(audit);
    }

    public void removeAudit(BookAudit audit) {
        this.audits.remove(audit);
    }

    public boolean isActive() {
        return isActive;
    }

    public Book setActive(boolean active) {
        isActive = active;
        return this;
    }

    public String getDeactivateReason() {
        return deactivateReason;
    }

    public Book setDeactivateReason(String deactivateReason) {
        this.deactivateReason = deactivateReason;
        return this;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public Book setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
        return this;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public Book setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
        return this;
    }
}
