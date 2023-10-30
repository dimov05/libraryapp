package bg.libapp.libraryapp.repository;

import bg.libapp.libraryapp.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findAllBySubscriptionNotNull();
    @Query("""
            SELECT DISTINCT u FROM user as u\s
            JOIN rent as r ON u.id = r.user.id\s
            WHERE r.actualReturnDate IS NULL\s
            AND r.expectedReturnDate < CURDATE()""")
    List<User> findAllByRentActualReturnDateNullAndExpectedReturnDateBeforeNow();

    boolean existsByUsername(String username);
}
