package bg.libapp.libraryapp.model.mappers;

import bg.libapp.libraryapp.model.dto.book.BookAddRequest;
import bg.libapp.libraryapp.model.dto.book.BookDTO;
import bg.libapp.libraryapp.model.dto.book.BookExtendedDTO;
import bg.libapp.libraryapp.model.entity.Book;
import bg.libapp.libraryapp.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.stream.Collectors;

import static bg.libapp.libraryapp.model.constants.ApplicationConstants.MAP_TO_BOOK_ACCESSED;
import static bg.libapp.libraryapp.model.constants.ApplicationConstants.MAP_TO_BOOK_DTO_ACCESSED;
import static bg.libapp.libraryapp.model.constants.ApplicationConstants.MAP_TO_BOOK_EXTENDED_DTO_ACCESSED;

public class BookMapper {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public static BookDTO mapToBookDTO(Book book) {
        logger.info(MAP_TO_BOOK_DTO_ACCESSED, book);
        return new BookDTO()
                .setIsbn(book.getIsbn())
                .setYear(book.getYear())
                .setTitle(book.getTitle())
                .setPublisher(book.getPublisher())
                .setIsActive(book.isActive())
                .setAvailableQuantity(book.getAvailableQuantity())
                .setTotalQuantity(book.getTotalQuantity())
                .setDeactivateReason(book.getDeactivateReason())
                .setGenres(book.getGenres()
                        .stream()
                        .map(GenreMapper::mapToGenreDTO)
                        .collect(Collectors.toSet()));
    }

    public static Book mapToBook(BookAddRequest bookAddRequest) {
        logger.info(MAP_TO_BOOK_ACCESSED, bookAddRequest);
        return new Book()
                .setIsbn(bookAddRequest.getIsbn())
                .setTitle(bookAddRequest.getTitle())
                .setYear(bookAddRequest.getYear())
                .setTotalQuantity(bookAddRequest.getTotalQuantity())
                .setDateAdded(LocalDateTime.now())
                .setPublisher(bookAddRequest.getPublisher())
                .setGenres(new HashSet<>())
                .setAuthors(new HashSet<>());
    }

    public static BookExtendedDTO mapToBookExtendedDTO(Book book) {
        logger.info(MAP_TO_BOOK_EXTENDED_DTO_ACCESSED, book);
        return new BookExtendedDTO()
                .setIsbn(book.getIsbn())
                .setTitle(book.getTitle())
                .setPublisher(book.getPublisher())
                .setYear(book.getYear())
                .setDateAdded(book.getDateAdded().toLocalDate().toString())
                .setIsActive(book.isActive())
                .setAvailableQuantity(book.getAvailableQuantity())
                .setTotalQuantity(book.getTotalQuantity())
                .setDeactivateReason(book.getDeactivateReason())
                .setGenres(book.getGenres()
                        .stream()
                        .map(GenreMapper::mapToGenreDTO)
                        .collect(Collectors.toSet()))
                .setAuthors(book.getAuthors()
                        .stream()
                        .map(AuthorMapper::mapToAuthorDTO)
                        .collect(Collectors.toSet()));
    }
}