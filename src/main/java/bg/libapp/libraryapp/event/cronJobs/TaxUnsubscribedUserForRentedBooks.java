package bg.libapp.libraryapp.event.cronJobs;

import bg.libapp.libraryapp.service.UserService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
public class TaxUnsubscribedUserForRentedBooks {
    private final UserService userService;

    public TaxUnsubscribedUserForRentedBooks(UserService userService) {
        this.userService = userService;
    }

    // Everyday of month at 02:00 am TODO: every day at 02:00 AM
    @Scheduled(cron = "0 0 2 * * *")
    public void taxUnsubscribedUsersForRentedBooks() {
        userService.taxUsersForProlongedRents();
    }
}
