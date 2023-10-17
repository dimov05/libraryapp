package bg.libapp.libraryapp.exceptions.rent;

public class UserNotEligibleToRentException extends RuntimeException {
    public UserNotEligibleToRentException(String username, Long userId) {
        super("User with this username :'" + username + "' is not eligible to rent a book for this user id '" + userId + "'!");
    }

}
