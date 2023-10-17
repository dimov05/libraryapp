package bg.libapp.libraryapp.service;

import bg.libapp.libraryapp.exceptions.rent.CannotRentBookTwiceException;
import bg.libapp.libraryapp.exceptions.rent.InsufficientAvailableQuantityException;
import bg.libapp.libraryapp.exceptions.rent.RentAlreadyReturnedException;
import bg.libapp.libraryapp.exceptions.rent.RentNotFoundException;
import bg.libapp.libraryapp.exceptions.rent.UserHasProlongedRentsException;
import bg.libapp.libraryapp.exceptions.rent.UserNotEligibleToRentException;
import bg.libapp.libraryapp.exceptions.rent.UserRentedMaximumAllowedBooksException;
import bg.libapp.libraryapp.model.dto.rent.RentAddRequest;
import bg.libapp.libraryapp.model.dto.rent.RentDTO;
import bg.libapp.libraryapp.model.entity.Book;
import bg.libapp.libraryapp.model.entity.Rent;
import bg.libapp.libraryapp.model.entity.User;
import bg.libapp.libraryapp.model.enums.Role;
import bg.libapp.libraryapp.model.mappers.RentMapper;
import bg.libapp.libraryapp.repository.BookRepository;
import bg.libapp.libraryapp.repository.RentRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static bg.libapp.libraryapp.model.constants.ApplicationConstants.ONE_MONTH;

@Transactional
@Service
public class RentService {
    private final Logger logger = LoggerFactory.getLogger(BookService.class);
    private final BookRepository bookRepository;
    private final RentRepository rentRepository;
    private final UserService userService;
    private final BookService bookService;

    @Autowired
    public RentService(BookRepository bookRepository, RentRepository rentRepository, UserService userService, BookService bookService) {
        this.bookRepository = bookRepository;
        this.rentRepository = rentRepository;
        this.userService = userService;
        this.bookService = bookService;
    }

    public RentDTO rentBook(RentAddRequest rentAddRequest, String isbn) {
        Book book = bookService.findBookByIsbn(isbn);
        hasEnoughAvailableQuantity(book);

        User borrower = userEligibleToRent(rentAddRequest);
        logger.info("rentBook method called with borrower username '" + borrower.getUsername()
                + "' for book with isbn '" + isbn
                + "' with requestParams: " + rentAddRequest);

        checkRequirementsForRentCreation(isbn, borrower);
        Rent rent = new Rent()
                .setRentDate(LocalDate.now())
                .setBook(book)
                .setUser(borrower)
                .setExpectedReturnDate(getExpectedDateToSet(rentAddRequest));
        rentRepository.saveAndFlush(rent);
        borrower.addRent(rent);
        bookRepository.saveAndFlush(book.setAvailableQuantity(book.getAvailableQuantity() - 1));
        logger.info("Created new rent with params: " + rent);
        return RentMapper.mapToRentDTO(rent);
    }

    private void hasEnoughAvailableQuantity(Book book) {
        logger.info("hasEnoughAvailableQuantity method called for book with isbn '" + book.getIsbn() + "'.");
        int availableQuantity = book.getAvailableQuantity();
        if (availableQuantity < 1) {
            logger.error("Insufficient available quantity for book with isbn '" + book.getIsbn()
                    + "', because available quantity is =" + availableQuantity);
            throw new InsufficientAvailableQuantityException(book.getIsbn(), availableQuantity);
        }
    }

    private User userEligibleToRent(RentAddRequest rentAddRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("userEligibleToRent method called with params: " + rentAddRequest + "\n" +
                " by authenticated user with username '" + username + "'.");
        User borrower = userService.getUserByUsername(username);
        String userRole = Role.values()[borrower.getRole()].toString();
        Long userIdToRent = rentAddRequest.getUserId();
        if (!userRole.equals(String.valueOf(Role.ADMIN))
                && !userRole.equals(String.valueOf(Role.MODERATOR))
                && userIdToRent != borrower.getId()) {
            logger.error("User is not eligible to rent - not ADMIN/MODERATOR or authenticated user != user for rent request");
            throw new UserNotEligibleToRentException(username, userIdToRent);
        }
        if (userIdToRent != null && (userRole.equals(String.valueOf(Role.ADMIN)) || userRole.equals(String.valueOf(Role.MODERATOR)))) {
            borrower = userService.getUserById(userIdToRent);
        }
        return borrower;
    }

    private void checkRequirementsForRentCreation(String isbn, User borrower) {
        int counterOfCurrentRents = 0;
        for (Rent rent : borrower.getRents()) {
            isBookCurrentlyRentedByUser(isbn, rent);
            if (bookNotReturned(rent)) {
                hasUserProlongedRents(borrower, rent);
                if (hasEnoughTimeToReturnBook(rent)) {
                    counterOfCurrentRents = countBorrowedBookAndThrowExceptionWhenMoreThanTwo(borrower, counterOfCurrentRents);
                }
            }
        }
    }

    private void isBookCurrentlyRentedByUser(String isbn, Rent rent) {
        if (rent.getBook().getIsbn().equals(isbn) && rent.getActualReturnDate() == null) {
            logger.error("Can not rent book twice for book with isbn '" + rent.getBook().getIsbn()
                    + "' and user with id '" + rent.getUser().getId() + "'.");
            throw new CannotRentBookTwiceException(isbn);
        }
    }

    private boolean bookNotReturned(Rent rent) {
        return rent.getActualReturnDate() == null;
    }

    private void hasUserProlongedRents(User borrower, Rent rent) {
        if (rent.getExpectedReturnDate().isBefore(LocalDate.now())) {
            logger.error("User with id '" + borrower.getId() + "' has prolonged rent with id '" + rent.getId() + "'.");
            throw new UserHasProlongedRentsException(borrower.getId(), rent.getId());
        }
    }

    private boolean hasEnoughTimeToReturnBook(Rent rent) {
        return rent.getExpectedReturnDate().isAfter(LocalDate.now());
    }

    private int countBorrowedBookAndThrowExceptionWhenMoreThanTwo(User borrower, int counterOfCurrentRents) {
        counterOfCurrentRents++;
        if (counterOfCurrentRents == 3) {
            logger.error("User with id '" + borrower.getId() + "' has rented already 3 books!");
            throw new UserRentedMaximumAllowedBooksException(borrower.getId());
        }
        return counterOfCurrentRents;
    }

    private static LocalDate getExpectedDateToSet(RentAddRequest rentAddRequest) {
        return rentAddRequest.getExpectedReturnDate() != null
                ? rentAddRequest.getExpectedReturnDate()
                : LocalDate.now().plusMonths(ONE_MONTH);
    }

    public RentDTO returnBook(long rentId) {
        Rent rent = getRentById(rentId);
        isRentAlreadyReturned(rent);
        Book book = rent.getBook();
        logger.info("returnBook method called for rent with id '" + rentId
                + "' and book isbn '" + book.getIsbn() + "'.");
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        rent.setActualReturnDate(LocalDate.now());
        rentRepository.saveAndFlush(rent);
        bookRepository.saveAndFlush(book);
        return RentMapper.mapToRentDTO(rent);
    }

    private void isRentAlreadyReturned(Rent rent) {
        if (rent.getActualReturnDate() != null) {
            logger.error("Rent with id '" + rent.getId() + "' is already returned!");
            throw new RentAlreadyReturnedException(rent.getId());
        }
    }

    public RentDTO getRentDTOById(long id) {
        Rent rent = getRentById(id);
        return RentMapper.mapToRentDTO(rent);
    }

    private Rent getRentById(long id) {
        logger.info("Find rent with id '" + id + "'");
        return rentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Rent with this id '" + id + "' was not found!");
                    return new RentNotFoundException(id);
                });
    }

    public Set<RentDTO> getAllRents() {
        logger.info("getAllBooks method called");
        return rentRepository.findAll()
                .stream()
                .map(RentMapper::mapToRentDTO)
                .collect(Collectors.toSet());
    }

    public String getUsernameByRentId(long id) {
        Rent rent = getRentById(id);
        return rent.getUser().getUsername();
    }
}
