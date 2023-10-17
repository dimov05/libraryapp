package bg.libapp.libraryapp.exceptions.book;

public class BookAlreadyAddedException extends RuntimeException {
    public BookAlreadyAddedException(String isbn) {
        super("Book with this isbn: '" + isbn + "' is already added!");
    }
}