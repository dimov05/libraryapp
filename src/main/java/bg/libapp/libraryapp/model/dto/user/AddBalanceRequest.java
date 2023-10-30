package bg.libapp.libraryapp.model.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class AddBalanceRequest {
    @Positive(message = "Amount to add to balance must be more than 0.00")
    @Max(value = 1000,message = "Amount should be less than 1000")
    private BigDecimal balance;

    public AddBalanceRequest() {
    }

    public AddBalanceRequest(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AddBalanceRequest setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }
}
