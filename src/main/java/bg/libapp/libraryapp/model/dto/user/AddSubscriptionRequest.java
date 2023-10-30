package bg.libapp.libraryapp.model.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class AddSubscriptionRequest {
    @Min(value = 1, message = "Subscription type must be between 1 and 3 (1->BRONZE, 2->SILVER,3->GOLDEN)")
    @Max(value = 3, message = "Subscription type must be between 1 and 3 (1->BRONZE, 2->SILVER,3->GOLDEN)")
    private long subscriptionType;

    public AddSubscriptionRequest() {
    }

    public AddSubscriptionRequest(int subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public long getSubscriptionType() {
        return subscriptionType;
    }

    public AddSubscriptionRequest setSubscriptionType(long subscriptionType) {
        this.subscriptionType = subscriptionType;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "subscriptionType=" + subscriptionType +
                '}';
    }
}