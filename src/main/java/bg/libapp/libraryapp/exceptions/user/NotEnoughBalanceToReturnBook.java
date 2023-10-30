package bg.libapp.libraryapp.exceptions.user;

import bg.libapp.libraryapp.model.enums.SubscriptionType;

import java.math.BigDecimal;

public class NotEnoughBalanceToReturnBook extends RuntimeException {
    public NotEnoughBalanceToReturnBook(long id, BigDecimal balance, String title) {
        super("User with this id :'" + id + "' and his balance: '" + balance.toString() + "' is not enough to return book: '" + title + "'!");
    }
}
