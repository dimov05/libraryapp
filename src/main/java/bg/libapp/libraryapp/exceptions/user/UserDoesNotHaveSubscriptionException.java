package bg.libapp.libraryapp.exceptions.user;

public class UserDoesNotHaveSubscriptionException extends RuntimeException {
    public UserDoesNotHaveSubscriptionException(long id) {
        super("User with this id :'" + id + "' does not have subscription!");
    }
}
