package bg.libapp.libraryapp.exceptions.user;

public class UserCannotDowngradeOnHavingMoreRentsThanAllowed extends RuntimeException {
    public UserCannotDowngradeOnHavingMoreRentsThanAllowed(long id) {
        super("User with this id :'" + id + "' can not downgrade because he has more rents than allowed for new subscription");
    }
}
