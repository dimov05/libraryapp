package bg.libapp.libraryapp.repository;

import bg.libapp.libraryapp.model.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
