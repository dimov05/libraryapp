package bg.libapp.libraryapp.exceptions.user;

import bg.libapp.libraryapp.model.enums.SubscriptionType;

public class UserIsAlreadyWithThisSubscription extends RuntimeException {
    public UserIsAlreadyWithThisSubscription(long id, SubscriptionType current) {
        super("User with this id :'" + id + "' is already with this subscription '" + current.toString() + "'!");
    }
}
