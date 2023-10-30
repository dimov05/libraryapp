package bg.libapp.libraryapp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_audit")
public class BookAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @Column(name = "operation_type", nullable = false)
    private String operationType;
    @Column(name = "field_name")
    private String fieldName;
    @Column(name = "old_value")
    private String oldValue;
    @Column(name = "new_value")
    private String newValue;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "book_isbn", nullable = false)
    private Book book;

    public BookAudit() {
    }

    public BookAudit(LocalDateTime eventDate, String operationType, String fieldName, String oldValue, String newValue, User user, Book book) {
        this.eventDate = eventDate;
        this.operationType = operationType;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.user = user;
        this.book = book;
    }

    public BookAudit(Long id, LocalDateTime eventDate, String operationType, String fieldName, String oldValue, String newValue, User user, Book book) {
        this.id = id;
        this.eventDate = eventDate;
        this.operationType = operationType;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.user = user;
        this.book = book;
    }

    public Long getId() {
        return id;
    }

    public BookAudit setId(Long id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public BookAudit setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public String getOperationType() {
        return operationType;
    }

    public BookAudit setOperationType(String operationType) {
        this.operationType = operationType;
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public BookAudit setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public String getOldValue() {
        return oldValue;
    }

    public BookAudit setOldValue(String oldValue) {
        this.oldValue = oldValue;
        return this;
    }

    public String getNewValue() {
        return newValue;
    }

    public BookAudit setNewValue(String newValue) {
        this.newValue = newValue;
        return this;
    }

    public User getUser() {
        return user;
    }

    public BookAudit setUser(User user) {
        this.user = user;
        return this;
    }

    public Book getBook() {
        return book;
    }

    public BookAudit setBook(Book book) {
        this.book = book;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", eventDate=" + eventDate +
                ", operationType='" + operationType + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", oldValue='" + oldValue + '\'' +
                ", newValue='" + newValue + '\'' +
                ", user=" + user +
                ", book=" + book +
                '}';
    }
}
