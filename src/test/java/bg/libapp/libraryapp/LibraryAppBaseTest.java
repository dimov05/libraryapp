package bg.libapp.libraryapp;

import bg.libapp.libraryapp.model.entity.Author;
import bg.libapp.libraryapp.model.entity.Book;
import bg.libapp.libraryapp.model.entity.Genre;
import bg.libapp.libraryapp.model.entity.Rent;
import bg.libapp.libraryapp.model.entity.User;
import bg.libapp.libraryapp.repository.AuthorRepository;
import bg.libapp.libraryapp.repository.BookRepository;
import bg.libapp.libraryapp.repository.GenreRepository;
import bg.libapp.libraryapp.repository.RentRepository;
import bg.libapp.libraryapp.repository.UserRepository;
import bg.libapp.libraryapp.service.AuthorService;
import bg.libapp.libraryapp.service.BookService;
import bg.libapp.libraryapp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static bg.libapp.libraryapp.Constants.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class LibraryAppBaseTest {
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected BookService bookService;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    protected AuthorRepository authorRepository;
    @Autowired
    protected AuthorService authorService;
    @Autowired
    protected BookRepository bookRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserService userService;
    @Autowired
    protected RentRepository rentRepository;

    protected Set<Author> insertSetOfTestAuthors() {
        return new HashSet<>(Set.of(new Author().setId(1L).setFirstName("First").setLastName("Last"),
                new Author().setId(2L).setFirstName("Second").setLastName("Second last")));
    }


    protected Book insertTestBook() {
        return bookRepository.saveAndFlush(new Book()
                .setIsbn("978-1-6759-2145-6")
                .setActive(true)
                .setAvailableQuantity(QUANTITY)
                .setTotalQuantity(QUANTITY)
                .setTitle("Test book 1")
                .setAuthors(Set.of(new Author().setId(1L).setFirstName("First").setLastName("Last")))
                .setDateAdded(DATETIME_BOOK)
                .setGenres(getGenresFromDatabase())
                .setPublisher("Ciela")
                .setYear(YEAR));
    }

    protected Book insertTestBookWithNoAvailableQuantity() {
        return bookRepository.saveAndFlush(new Book()
                .setIsbn("978-1-6759-2145-6")
                .setActive(true)
                .setAvailableQuantity(0)
                .setTotalQuantity(1)
                .setTitle("Test book 1")
                .setAuthors(Set.of(new Author().setId(1L).setFirstName("First").setLastName("Last")))
                .setDateAdded(DATETIME_BOOK)
                .setGenres(getGenresFromDatabase())
                .setPublisher("Ciela")
                .setYear(YEAR));
    }

    protected Rent insertRentForBookAndUser(Book book, User user) {
        return rentRepository.saveAndFlush(
                new Rent()
                        .setUser(user)
                        .setBook(book)
                        .setRentDate(LocalDate.now())
                        .setExpectedReturnDate(LocalDate.now().plusMonths(2))
                        .setActualReturnDate(null)
        );
    }

    protected Book insertTestBook(boolean status) {
        return bookRepository.saveAndFlush(new Book()
                .setIsbn("978-1-6759-2145-6")
                .setActive(status)
                .setAvailableQuantity(QUANTITY)
                .setTotalQuantity(QUANTITY)
                .setTitle("Test book 1")
                .setAuthors(Set.of(new Author().setId(1L).setFirstName("First").setLastName("Last")))
                .setDateAdded(DATETIME_BOOK)
                .setGenres(getGenresFromDatabase())
                .setPublisher("Ciela")
                .setYear(YEAR));
    }

    protected Book insertSecondTestBook() {
        return bookRepository.save(new Book()
                .setIsbn("978-4-4560-8944-4")
                .setActive(true)
                .setAvailableQuantity(QUANTITY)
                .setTotalQuantity(QUANTITY)
                .setTitle("Test book 2")
                .setAuthors(insertSetOfTestAuthors())
                .setDateAdded(DATETIME_BOOK)
                .setGenres(getGenresFromDatabase())
                .setPublisher("Hermes")
                .setYear(YEAR));
    }

    protected Book insertThirdTestBook() {
        return bookRepository.save(new Book()
                .setIsbn("978-9-5106-5309-8")
                .setActive(true)
                .setAvailableQuantity(QUANTITY)
                .setTotalQuantity(QUANTITY)
                .setTitle("Test book 3")
                .setAuthors(insertSetOfTestAuthors())
                .setDateAdded(DATETIME_BOOK)
                .setGenres(getGenresFromDatabase())
                .setPublisher("Bulgaria")
                .setYear(YEAR));
    }

    protected Book insertFourthTestBook() {
        return bookRepository.save(new Book()
                .setIsbn("978-6-0066-4601-5")
                .setActive(true)
                .setAvailableQuantity(QUANTITY)
                .setTotalQuantity(QUANTITY)
                .setTitle("Test book 4")
                .setAuthors(insertSetOfTestAuthors())
                .setDateAdded(DATETIME_BOOK)
                .setGenres(getGenresFromDatabase())
                .setPublisher("BookTrading")
                .setYear(YEAR));
    }

    private Set<Genre> getGenresFromDatabase() {
        Set<Genre> genres = new HashSet<>();
        Genre genreOne = genreRepository.findById(1L).orElse(null);
        Genre genreTwo = genreRepository.findById(2L).orElse(null);
        genres.add(genreOne);
        genres.add(genreTwo);
        return genres;
    }

    protected User insertUser() {
        return userRepository.save(
                new User()
                        .setActive(true)
                        .setFirstName("User")
                        .setLastName("Userov")
                        .setUsername("userUser")
                        .setPassword(passwordEncoder.encode(USER_PASSWORD))
                        .setDisplayName("UserUserov")
                        .setRole(0)
                        .setDateOfBirth(LocalDate.of(2000, 5, 6))
                        .setRents(new ArrayList<>())
        );
    }

    protected User insertDeactivatedUser() {
        return userRepository.save(
                new User()
                        .setActive(false)
                        .setFirstName("User")
                        .setLastName("Userov")
                        .setUsername("userUser")
                        .setPassword(passwordEncoder.encode(USER_PASSWORD))
                        .setDisplayName("UserUserov")
                        .setRole(0)
                        .setDateOfBirth(LocalDate.of(2000, 5, 6))
                        .setRents(new ArrayList<>())
        );
    }

    protected User insertUser2() {
        return userRepository.save(
                new User()
                        .setActive(true)
                        .setFirstName("User2")
                        .setLastName("Userov2")
                        .setUsername("userUser2")
                        .setPassword(passwordEncoder.encode(USER_PASSWORD))
                        .setDisplayName("UserUserov2")
                        .setRole(0)
                        .setDateOfBirth(LocalDate.of(2000, 5, 6))
                        .setRents(new ArrayList<>())
        );
    }

    protected User insertAdmin() {
        return userRepository.save(
                new User()
                        .setActive(true)
                        .setFirstName("Admin")
                        .setLastName("Adminov")
                        .setUsername("adminAdmin")
                        .setPassword(passwordEncoder.encode(ADMIN_PASSWORD))
                        .setDisplayName("AdminAdminov")
                        .setRole(2)
                        .setDateOfBirth(LocalDate.of(2000, 5, 6))
                        .setRents(new ArrayList<>())
        );
    }

    protected Book createBook() {
        return new Book()
                .setIsbn("978-1-6759-2145-6")
                .setActive(true)
                .setAvailableQuantity(QUANTITY)
                .setTotalQuantity(QUANTITY)
                .setTitle("Test book 1")
                .setDateAdded(DATETIME_BOOK)
                .setGenres(getGenresFromDatabase())
                .setPublisher("Ciela")
                .setYear(YEAR);
    }
}
