package bg.libapp.libraryapp.exceptions.user;

import bg.libapp.libraryapp.model.enums.SubscriptionType;

import java.math.BigDecimal;

public class NotEnoughBalanceToStartSubscriptionException extends RuntimeException {
    public NotEnoughBalanceToStartSubscriptionException(long id, BigDecimal balance, SubscriptionType subscriptionType) {
        super("User with this id :'" + id + "' and his balance: '" + balance.toString() + "' is not enough to start subscription of type: '" + subscriptionType.toString() + "'!");
    }
}
