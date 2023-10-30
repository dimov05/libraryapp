package bg.libapp.libraryapp.exceptions.rent;

public class InsufficientAvailableQuantityException extends RuntimeException {
    public InsufficientAvailableQuantityException(String isbn, int availableQuantity) {
        super("Book with isbn '" + isbn + "' + has insufficient available quantity '" + availableQuantity + "' to be rented.");
    }
}
