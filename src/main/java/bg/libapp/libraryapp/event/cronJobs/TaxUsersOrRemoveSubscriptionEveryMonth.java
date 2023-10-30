package bg.libapp.libraryapp.event.cronJobs;

import bg.libapp.libraryapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
public class TaxUsersOrRemoveSubscriptionEveryMonth {
    private final UserService userService;

    @Autowired
    public TaxUsersOrRemoveSubscriptionEveryMonth(UserService userService) {
        this.userService = userService;
    }

    // Every 1st day of month at 01:00 am
    @Scheduled(cron = "0 0 1 1 * ?")
    public void taxForSubscriptionOrRemoveSubscription() {
        userService.taxUsersOrRemoveSubscriptionAtStartOfMonth();
    }
}

