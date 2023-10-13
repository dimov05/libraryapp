package bg.libapp.libraryapp.service;

import bg.libapp.libraryapp.event.SaveBookAuditEvent;
import bg.libapp.libraryapp.event.UpdateActiveStatusBookAuditEvent;
import bg.libapp.libraryapp.event.UpdateDeactivateReasonBookAuditEvent;
import bg.libapp.libraryapp.event.UpdatePublisherBookAuditEvent;
import bg.libapp.libraryapp.event.UpdateQuantityBookAuditEvent;
import bg.libapp.libraryapp.event.UpdateYearBookAuditEvent;
import bg.libapp.libraryapp.exceptions.CannotProcessJsonOfEntityException;
import bg.libapp.libraryapp.exceptions.book.BookAlreadyAddedException;
import bg.libapp.libraryapp.exceptions.book.BookIsActiveOnDeleteException;
import bg.libapp.libraryapp.exceptions.book.BookNotActiveException;
import bg.libapp.libraryapp.exceptions.book.BookNotFoundException;
import bg.libapp.libraryapp.exceptions.book.NoSuchDeactivateReasonException;
import bg.libapp.libraryapp.exceptions.genre.GenreNotFoundException;
import bg.libapp.libraryapp.exceptions.rent.InsufficientTotalQuantityException;
import bg.libapp.libraryapp.model.dto.book.BookAddRequest;
import bg.libapp.libraryapp.model.dto.book.BookChangeStatusRequest;
import bg.libapp.libraryapp.model.dto.book.BookDTO;
import bg.libapp.libraryapp.model.dto.book.BookExtendedDTO;
import bg.libapp.libraryapp.model.dto.book.BookFilterRequest;
import bg.libapp.libraryapp.model.dto.book.BookUpdatePublisherRequest;
import bg.libapp.libraryapp.model.dto.book.BookUpdateTotalQuantityRequest;
import bg.libapp.libraryapp.model.dto.book.BookUpdateYearRequest;
import bg.libapp.libraryapp.model.entity.Book;
import bg.libapp.libraryapp.model.enums.DeactivateReason;
import bg.libapp.libraryapp.model.mappers.BookMapper;
import bg.libapp.libraryapp.repository.BookRepository;
import bg.libapp.libraryapp.repository.GenreRepository;
import bg.libapp.libraryapp.specifications.BookSpecifications;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static bg.libapp.libraryapp.model.constants.ApplicationConstants.*;

@Service
@Transactional
public class BookService {

    private final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper mapper;

    private final AuthorService authorService;

    @Autowired
    public BookService(BookRepository bookRepository, GenreRepository genreRepository, ApplicationEventPublisher eventPublisher, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.eventPublisher = eventPublisher;
        this.authorService = authorService;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public BookDTO saveNewBook(BookAddRequest bookAddRequest) {
        String isbnOfBook = bookAddRequest.getIsbn();
        if (bookRepository.existsByIsbn(isbnOfBook)) {
            logger.error("Book with this isbn is already added");
            throw new BookAlreadyAddedException(isbnOfBook);
        }
        Book book = BookMapper.mapToBook(bookAddRequest);
        book.setActive(true);
        book.setAvailableQuantity(book.getTotalQuantity());
        book.setGenres(bookAddRequest.getGenres()
                .stream()
                .map(genre -> this.genreRepository.findByName(genre.getName())
                        .orElseThrow(() -> {
                            logger.error("There are no genres with this name '" + genre.getName() + "'");
                            return new GenreNotFoundException(genre.getName());
                        }))
                .collect(Collectors.toSet()));
        book.setAuthors(bookAddRequest.getAuthors()
                .stream()
                .map(authorService::findOrCreate)
                .collect(Collectors.toSet()));
        bookRepository.saveAndFlush(book);
        logger.info("Save a new book with isbn '" + bookAddRequest.getIsbn() + "' and params: " + bookAddRequest);
        eventPublisher.publishEvent(new SaveBookAuditEvent(book, getJsonOfBook(book)));
        return BookMapper.mapToBookDTO(book);
    }

    public BookDTO deleteByIsbn(String isbn) {
        Book bookToDelete = getBookByIsbnOrThrowException(isbn);
        isBookInActive(bookToDelete);
        BookDTO bookToReturn = BookMapper.mapToBookDTO(bookToDelete);
        bookRepository.delete(bookToDelete);
        logger.info("Delete book with isbn '" + isbn + "'");

        return bookToReturn;
    }

    public BookDTO updateYear(String isbn, BookUpdateYearRequest bookUpdateYearRequest) {
        Book bookToEdit = getBookByIsbnOrThrowException(isbn);
        String oldValueYear = String.valueOf(bookToEdit.getYear());
        String newValueYear = String.valueOf(bookUpdateYearRequest.getYear());
        if (!oldValueYear.equals(newValueYear)) {
            bookToEdit.setYear(bookUpdateYearRequest.getYear());
            bookRepository.saveAndFlush(bookToEdit);
            eventPublisher.publishEvent(new UpdateYearBookAuditEvent(bookToEdit, oldValueYear));
            logger.info("Updated year of book with isbn '" + isbn + "' and params: " + bookUpdateYearRequest);
        }
        return BookMapper.mapToBookDTO(bookToEdit);
    }

    private void isBookActive(Book bookToEdit) {
        if (!bookToEdit.isActive()) {
            logger.error("Book with isbn '" + bookToEdit.getIsbn() + "' is active!");
            throw new BookNotActiveException(bookToEdit.getIsbn());
        }
    }

    private void isBookInActive(Book bookToEdit) {
        if (bookToEdit.isActive()) {
            logger.error("Book with isbn '" + bookToEdit.getIsbn() + "' is not active!");
            throw new BookIsActiveOnDeleteException(bookToEdit.getIsbn());
        }
    }

    public BookDTO updatePublisher(String isbn, BookUpdatePublisherRequest bookUpdatePublisherRequest) {
        Book bookToEdit = getBookByIsbnOrThrowException(isbn);
        String oldValuePublisher = bookToEdit.getPublisher();
        String newValuePublisher = bookUpdatePublisherRequest.getPublisher();
        if (!oldValuePublisher.equals(newValuePublisher)) {
            bookToEdit.setPublisher(bookUpdatePublisherRequest.getPublisher());
            bookRepository.saveAndFlush(bookToEdit);
            eventPublisher.publishEvent(new UpdatePublisherBookAuditEvent(bookToEdit, oldValuePublisher));
            logger.info("Updated publisher of book with isbn '" + isbn + "' and params: " + bookUpdatePublisherRequest);
        }
        return BookMapper.mapToBookDTO(bookToEdit);
    }

    public BookExtendedDTO findBookExtendedDTOByIsbn(String isbn) {
        Book book = getBookByIsbnOrThrowException(isbn);
        isBookActive(book);
        logger.info("Find book with isbn '" + isbn + "'");
        return BookMapper.mapToBookExtendedDTO(book);
    }

    public Book findBookByIsbn(String isbn) {
        Book book = getBookByIsbnOrThrowException(isbn);
        isBookActive(book);
        logger.info("Find book with isbn '" + isbn + "'");
        return book;
    }

    private String getJsonOfBook(Book book) {
        String json;
        try {
            json = mapper.writeValueAsString(BookMapper.mapToBookDTO(book));
        } catch (JsonProcessingException e) {
            logger.error("Can not get json format of book entity!");
            throw new CannotProcessJsonOfEntityException(book);
        }
        logger.info("Get json format of book entity.");
        return json;
    }

    private static String getBookNotFoundMessage(String isbn) {
        return "Book with this isbn '" + isbn + "' was not found!";
    }

    public Set<BookExtendedDTO> getAllBooks(BookFilterRequest bookFilterRequest) {
        logger.info("getAllBooks method called with params: " + bookFilterRequest);

        List<Book> books = bookFilterRequest == null
                ? bookRepository.findAll()
                : bookRepository.findAll(getBookSpecifications(bookFilterRequest));
        return books
                .stream()
                .map(BookMapper::mapToBookExtendedDTO)
                .collect(Collectors.toSet());
    }

    private static Specification<Book> getBookSpecifications(BookFilterRequest bookFilterRequest) {
        Specification<Book> specification = null;

        if (bookFilterRequest.getIsActive() != null) {
            specification = BookSpecifications.isActive(bookFilterRequest.getIsActive()).and(Specification.where(specification));
        }
        if (bookFilterRequest.getTitle() != null) {
            specification = BookSpecifications.fieldLike(TITLE, bookFilterRequest.getTitle()).and(Specification.where(specification));
        }
        if (bookFilterRequest.getPublisher() != null) {
            specification = BookSpecifications.fieldLike(PUBLISHER, bookFilterRequest.getPublisher()).and(Specification.where(specification));
        }
        if (bookFilterRequest.getYearFrom() != null) {
            specification = BookSpecifications.fieldGreaterThanOrEqual(YEAR, bookFilterRequest.getYearFrom()).and(Specification.where(specification));
        }
        if (bookFilterRequest.getYearTo() != null) {
            specification = BookSpecifications.fieldLowerThanOrEqual(YEAR, bookFilterRequest.getYearTo()).and(Specification.where(specification));
        }
        if (bookFilterRequest.getAvailableQuantity() != null) {
            String[] comparisons = bookFilterRequest.getAvailableQuantity().split(",");
            specification = getFieldCompareSpecification(specification, comparisons, AVAILABLE_QUANTITY);
        }
        if (bookFilterRequest.getYear() != null) {
            String[] comparisons = bookFilterRequest.getYear().split(",");
            specification = getFieldCompareSpecification(specification, comparisons, YEAR);
        }
        if (bookFilterRequest.getTotalQuantity() != null) {
            String[] comparisons = bookFilterRequest.getTotalQuantity().split(",");
            specification = getFieldCompareSpecification(specification, comparisons, TOTAL_QUANTITY);
        }
        if (bookFilterRequest.getGenres() != null) {
            for (Integer genre : bookFilterRequest.getGenres()) {
                specification = BookSpecifications.genreEquals(genre).and(Specification.where(specification));
            }
        }
        if (bookFilterRequest.getAuthorsFirstName() != null) {
            for (String authorFirstName : bookFilterRequest.getAuthorsFirstName()) {
                specification = BookSpecifications.authorNameEquals(FIRST_NAME, authorFirstName).and(Specification.where(specification));
            }
        }
        if (bookFilterRequest.getAuthorsLastName() != null) {
            for (String authorLastName : bookFilterRequest.getAuthorsLastName()) {
                specification = BookSpecifications.authorNameEquals(LAST_NAME, authorLastName).and(Specification.where(specification));
            }
        }
        return specification;
    }

    private static Specification<Book> getFieldCompareSpecification(Specification<Book> specification, String[] comparisons, String totalQuantity) {
        for (String comparison : comparisons) {
            String[] tokens = comparison.split(":");
            String operation = tokens[0];
            int value = Integer.parseInt(tokens[1]);
            specification = BookSpecifications.fieldCompareValue(totalQuantity, value, operation).and(specification);
        }
        return specification;
    }

    @Transactional
    public BookDTO changeStatus(String isbn, BookChangeStatusRequest bookChangeStatusRequest) {
        logger.info("changeStatus of Book method called with params: " + bookChangeStatusRequest);

        Book book = getBookByIsbnOrThrowException(isbn);
        boolean newStatus = bookChangeStatusRequest.getIsActive();
        boolean oldStatus = book.isActive();
        String newReason = bookChangeStatusRequest.getDeactivateReason();
        String oldReason = book.getDeactivateReason();
        String oldStatusString = String.valueOf(oldStatus);

        if (newStatus && !oldStatus) {
            book.setActive(true);
            book.setDeactivateReason(null);
            eventPublisher.publishEvent(new UpdateActiveStatusBookAuditEvent(book, oldStatusString));
            eventPublisher.publishEvent(new UpdateDeactivateReasonBookAuditEvent(book, oldReason));
            logger.info("Activated book with isbn '" + book.getIsbn() + "' and params: " + book);
        } else if (!newStatus) {
            isValidDeactivateReason(newReason.toUpperCase());
            if (oldStatus) {
                book.setActive(false);
                eventPublisher.publishEvent(new UpdateActiveStatusBookAuditEvent(book, oldStatusString));
            }
            if (!StringUtils.equals(oldReason, newReason)) {
                book.setDeactivateReason(newReason);
                eventPublisher.publishEvent(new UpdateDeactivateReasonBookAuditEvent(book, oldReason));
            }
            logger.info("Deactivated book with isbn '" + book.getIsbn() + "' and params: " + book);
        }
        return BookMapper.mapToBookDTO(book);
    }

    private void isValidDeactivateReason(String deactivateReason) {
        boolean isValid = EnumUtils.isValidEnum(DeactivateReason.class, deactivateReason);
        if (!isValid) {
            logger.error("There is no such a deactivate reason '" + deactivateReason + "'");
            throw new NoSuchDeactivateReasonException(deactivateReason);
        }
    }

    private Book getBookByIsbnOrThrowException(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> {
                    logger.error(getBookNotFoundMessage(isbn));
                    return new BookNotFoundException(isbn);
                });
    }

    public BookDTO updateTotalQuantity(String isbn, BookUpdateTotalQuantityRequest bookUpdateTotalQuantityRequest) {
        logger.info("updateTotalQuantity method called with params: isbn=" + isbn + ", " + bookUpdateTotalQuantityRequest);
        Book bookToEdit = getBookByIsbnOrThrowException(isbn);
        int oldTotalQuantity = bookToEdit.getTotalQuantity();
        int oldAvailableQuantity = bookToEdit.getAvailableQuantity();
        int newTotalQuantity = bookUpdateTotalQuantityRequest.getTotalQuantity();

        if (oldTotalQuantity != newTotalQuantity) {
            checkIfNewTotalQuantityIsLessThanRentedQuantity(newTotalQuantity, bookToEdit);
            int difference = Math.abs(newTotalQuantity - oldTotalQuantity);
            int newAvailableQuantity = getBookNewAvailableQuantity(oldTotalQuantity, newTotalQuantity, bookToEdit, difference);
            bookToEdit.setTotalQuantity(newTotalQuantity);
            bookToEdit.setAvailableQuantity(newAvailableQuantity);

            bookRepository.saveAndFlush(bookToEdit);
            eventPublisher.publishEvent(new UpdateQuantityBookAuditEvent(bookToEdit, oldTotalQuantity, TOTAL_QUANTITY));
            eventPublisher.publishEvent(new UpdateQuantityBookAuditEvent(bookToEdit, oldAvailableQuantity, AVAILABLE_QUANTITY));
            logger.info("Updated total quantity of book with isbn '" + isbn + "' and params: " + bookUpdateTotalQuantityRequest);
        }
        return BookMapper.mapToBookDTO(bookToEdit);
    }

    private int getBookNewAvailableQuantity(int oldTotalQuantity, int newTotalQuantity, Book bookToEdit, int difference) {
        return oldTotalQuantity > newTotalQuantity
                ? bookToEdit.getAvailableQuantity() - difference
                : bookToEdit.getAvailableQuantity() + difference;

    }

    private void checkIfNewTotalQuantityIsLessThanRentedQuantity(int newValueTotalQuantity, Book bookToEdit) {
        int rentedQuantity = bookToEdit.getTotalQuantity() - bookToEdit.getAvailableQuantity();
        if (newValueTotalQuantity < rentedQuantity) {
            throw new InsufficientTotalQuantityException(bookToEdit.getIsbn(), newValueTotalQuantity);
        }
    }
}
