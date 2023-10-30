package bg.libapp.libraryapp.service;

import bg.libapp.libraryapp.exceptions.rent.CannotRentBookTwiceException;
import bg.libapp.libraryapp.exceptions.rent.InsufficientAvailableQuantityException;
import bg.libapp.libraryapp.exceptions.rent.RentAlreadyReturnedException;
import bg.libapp.libraryapp.exceptions.rent.RentNotFoundException;
import bg.libapp.libraryapp.exceptions.rent.UserHasProlongedRentsException;
import bg.libapp.libraryapp.exceptions.rent.UserNotEligibleToRentException;
import bg.libapp.libraryapp.exceptions.rent.UserRentedMaximumAllowedBooksException;
import bg.libapp.libraryapp.exceptions.user.NotEnoughBalanceToReturnBook;
import bg.libapp.libraryapp.exceptions.user.UserDoesNotHaveSubscriptionException;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static bg.libapp.libraryapp.model.constants.ApplicationConstants.*;

@Transactional
@Service
public class RentService {
    private final Logger logger = LoggerFactory.getLogger(RentService.class);
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
        logger.info(RENT_BOOK_METHOD_CALLED_WITH_PARAMS_LOGGER, borrower.getUsername(), isbn, rentAddRequest);
        int daysAvailableToRent = borrower.getSubscription().getDaysAllowed();
        checkRequirementsForRentCreation(isbn, borrower);
        Rent rent = new Rent()
                .setRentDate(LocalDate.now())
                .setBook(book)
                .setUser(borrower)
                .setExpectedReturnDate(LocalDate.now().plusDays(daysAvailableToRent));
        rentRepository.saveAndFlush(rent);
        borrower.addRent(rent);
        bookRepository.saveAndFlush(book.setAvailableQuantity(book.getAvailableQuantity() - 1));
        logger.info(CREATED_NEW_RENT_WITH_PARAMS, rent);
        return RentMapper.mapToRentDTO(rent);
    }

    private void hasEnoughAvailableQuantity(Book book) {
        logger.info(HAS_ENOUGH_AVAILABLE_QUANTITY_METHOD_CALLED_FOR_BOOK_WITH_ISBN, book.getIsbn());
        int availableQuantity = book.getAvailableQuantity();
        if (availableQuantity < 1) {
            logger.error(INSUFFICIENT_AVAILABLE_QUANTITY_FOR_BOOK_WITH_ISBN, book.getIsbn(), availableQuantity);
            throw new InsufficientAvailableQuantityException(book.getIsbn(), availableQuantity);
        }
    }

    private User userEligibleToRent(RentAddRequest rentAddRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info(USER_ELIGIBLE_TO_RENT_METHOD_CALLED_WITH_PARAMS_LOGGER, rentAddRequest, username);
        User borrower = userService.getUserByUsername(username);
        String userRole = Role.values()[borrower.getRole()].toString();
        Long userIdToRent = rentAddRequest.getUserId();
        if (!userRole.equals(String.valueOf(Role.ADMIN))
                && !userRole.equals(String.valueOf(Role.MODERATOR))
                && userIdToRent != borrower.getId()) {
            logger.error(USER_NOT_ELIGIBLE_TO_RENT);
            throw new UserNotEligibleToRentException(username, userIdToRent);
        }
        if (userIdToRent != null && (userRole.equals(String.valueOf(Role.ADMIN)) || userRole.equals(String.valueOf(Role.MODERATOR)))) {
            borrower = userService.getUserById(userIdToRent);
        }
        if (borrower.getSubscription() == null) {
            throw new UserDoesNotHaveSubscriptionException(borrower.getId());
        }
        return borrower;
    }

    private void checkRequirementsForRentCreation(String isbn, User borrower) {
        int counterOfCurrentRents = 0;
        int maxAllowedRents = borrower.getSubscription().getRentsAllowed();
        for (Rent rent : borrower.getRents()) {
            isBookCurrentlyRentedByUser(isbn, rent);
            if (bookNotReturned(rent)) {
                hasUserProlongedRents(borrower, rent);
                if (hasEnoughTimeToReturnBook(rent)) {
                    counterOfCurrentRents = countBorrowedBookAndThrowExceptionWhenMoreThanTwo(borrower, counterOfCurrentRents, maxAllowedRents);
                }
            }
        }
    }

    private void isBookCurrentlyRentedByUser(String isbn, Rent rent) {
        if (rent.getBook().getIsbn().equals(isbn) && rent.getActualReturnDate() == null) {
            logger.error(CAN_NOT_RENT_BOOK_WITH_ISBN_FOR_USER_TWICE, rent.getBook().getIsbn(), rent.getUser().getId());
            throw new CannotRentBookTwiceException(isbn);
        }
    }

    private boolean bookNotReturned(Rent rent) {
        return rent.getActualReturnDate() == null;
    }

    private void hasUserProlongedRents(User borrower, Rent rent) {
        if (rent.getExpectedReturnDate().isBefore(LocalDate.now())) {
            logger.error(USER_WITH_ID_HAS_PROLONGED_RENT_WITH_ID, borrower.getId(), rent.getId());
            throw new UserHasProlongedRentsException(borrower.getId(), rent.getId());
        }
    }

    private boolean hasEnoughTimeToReturnBook(Rent rent) {
        return rent.getExpectedReturnDate().isAfter(LocalDate.now());
    }

    private int countBorrowedBookAndThrowExceptionWhenMoreThanTwo(User borrower, int counterOfCurrentRents, int maxAllowedRents) {
        counterOfCurrentRents++;
        if (counterOfCurrentRents == maxAllowedRents) {
            logger.error(USER_WITH_ID_HAS_ALREADY_RENTED_3_BOOKS, borrower.getId());
            throw new UserRentedMaximumAllowedBooksException(borrower.getId());
        }
        return counterOfCurrentRents;
    }

    public RentDTO returnBook(long rentId) {
        Rent rent = getRentById(rentId);
        isRentAlreadyReturned(rent);
        Book book = rent.getBook();
        User user = rent.getUser();
        logger.info(RETURN_BOOK_WITH_ISBN_METHOD_CALLED_FOR_RENT_WITH_ID, rentId, book.getIsbn());
        if (user.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughBalanceToReturnBook(user.getId(), user.getBalance(), book.getTitle());
        }
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        rent.setActualReturnDate(LocalDate.now());
        rentRepository.saveAndFlush(rent);
        bookRepository.saveAndFlush(book);
        return RentMapper.mapToRentDTO(rent);
    }

    private void isRentAlreadyReturned(Rent rent) {
        if (rent.getActualReturnDate() != null) {
            logger.error(RENT_WITH_ID_IS_ALREADY_RETURNED, rent.getId());
            throw new RentAlreadyReturnedException(rent.getId());
        }
    }

    public RentDTO getRentDTOById(long id) {
        Rent rent = getRentById(id);
        return RentMapper.mapToRentDTO(rent);
    }

    private Rent getRentById(long id) {
        logger.info(FIND_RENT_WITH_ID, id);
        return rentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(FIND_RENT_WITH_ID_WAS_NOT_FOUND, id);
                    return new RentNotFoundException(id);
                });
    }

    public Set<RentDTO> getAllRents() {
        logger.info(GET_ALL_RENTS_ACCESSED_LOGGER);
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
