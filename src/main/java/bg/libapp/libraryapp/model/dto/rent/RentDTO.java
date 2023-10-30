package bg.libapp.libraryapp.model.dto.rent;

import bg.libapp.libraryapp.model.dto.book.BookDTO;
import bg.libapp.libraryapp.model.dto.user.UserDTO;

public class RentDTO {
    private long id;
    private String rentDate;
    private String expectedReturnDate;
    private String actualReturnDate;
    private UserDTO user;
    private BookDTO book;

    public RentDTO() {
    }

    public RentDTO(String rentDate, String expectedReturnDate, String actualReturnDate, UserDTO user, BookDTO book) {
        this.rentDate = rentDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.user = user;
        this.book = book;
    }

    public long getId() {
        return id;
    }

    public RentDTO setId(long id) {
        this.id = id;
        return this;
    }

    public String getRentDate() {
        return rentDate;
    }

    public RentDTO setRentDate(String rentDate) {
        this.rentDate = rentDate;
        return this;
    }

    public String getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public RentDTO setExpectedReturnDate(String expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
        return this;
    }

    public String getActualReturnDate() {
        return actualReturnDate;
    }

    public RentDTO setActualReturnDate(String actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
        return this;
    }

    public UserDTO getUser() {
        return user;
    }

    public RentDTO setUser(UserDTO user) {
        this.user = user;
        return this;
    }

    public BookDTO getBook() {
        return book;
    }

    public RentDTO setBook(BookDTO book) {
        this.book = book;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", rentDate='" + rentDate + '\'' +
                ", expectedReturnDate='" + expectedReturnDate + '\'' +
                ", actualReturnDate='" + actualReturnDate + '\'' +
                ", user=" + user +
                ", book=" + book +
                '}';
    }
}
