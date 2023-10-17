package bg.libapp.libraryapp.model.dto.book;

import bg.libapp.libraryapp.model.dto.genre.GenreDTO;

import java.util.Set;

public class BookDTO {
    private String isbn;
    private String title;
    private int year;
    private Boolean isActive;
    private int availableQuantity;
    private int totalQuantity;
    private String deactivateReason;
    private String publisher;
    private Set<GenreDTO> genres;

    public BookDTO() {
    }

    public BookDTO(String isbn, String title, int year, boolean isActive, int availableQuantity, int totalQuantity, String deactivateReason, String publisher, Set<GenreDTO> genres) {
        this.isbn = isbn;
        this.title = title;
        this.year = year;
        this.isActive = isActive;
        this.availableQuantity = availableQuantity;
        this.totalQuantity = totalQuantity;
        this.deactivateReason = deactivateReason;
        this.publisher = publisher;
        this.genres = genres;
    }

    public String getIsbn() {
        return isbn;
    }

    public BookDTO setIsbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public BookDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getYear() {
        return year;
    }

    public BookDTO setYear(int year) {
        this.year = year;
        return this;
    }

    public String getPublisher() {
        return publisher;
    }

    public BookDTO setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }


    public Set<GenreDTO> getGenres() {
        return genres;
    }

    public BookDTO setGenres(Set<GenreDTO> genres) {
        this.genres = genres;
        return this;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public BookDTO setIsActive(boolean active) {
        isActive = active;
        return this;
    }

    public String isDeactivateReason() {
        return deactivateReason;
    }

    public BookDTO setDeactivateReason(String deactivateReason) {
        this.deactivateReason = deactivateReason;
        return this;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public BookDTO setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
        return this;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public BookDTO setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
        return this;
    }

    public String getDeactivateReason() {
        return deactivateReason;
    }

    @Override
    public String toString() {
        return "{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", isActive=" + isActive +
                ", availableQuantity=" + availableQuantity +
                ", totalQuantity=" + totalQuantity +
                ", deactivateReason='" + deactivateReason + '\'' +
                ", publisher='" + publisher + '\'' +
                ", genres=" + genres +
                '}';
    }
}