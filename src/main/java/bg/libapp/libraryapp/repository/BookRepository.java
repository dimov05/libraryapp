package bg.libapp.libraryapp.repository;

import bg.libapp.libraryapp.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,Long>, JpaSpecificationExecutor<Book> {
    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);
}
