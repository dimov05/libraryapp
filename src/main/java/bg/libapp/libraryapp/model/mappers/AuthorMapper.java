package bg.libapp.libraryapp.model.mappers;


import bg.libapp.libraryapp.model.dto.author.AuthorDTO;
import bg.libapp.libraryapp.model.dto.author.AuthorExtendedDTO;
import bg.libapp.libraryapp.model.entity.Author;
import bg.libapp.libraryapp.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

import static bg.libapp.libraryapp.model.constants.ApplicationConstants.MAP_TO_AUTHOR_DTO_ACCESSED;
import static bg.libapp.libraryapp.model.constants.ApplicationConstants.MAP_TO_AUTHOR_EXTENDED_DTO_ACCESSED;

public class AuthorMapper {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public static AuthorExtendedDTO mapToAuthorExtendedDTO(Author author) {
        logger.info(MAP_TO_AUTHOR_EXTENDED_DTO_ACCESSED, author);
        return new AuthorExtendedDTO()
                .setFirstName(author.getFirstName())
                .setLastName(author.getLastName())
                .setBooks(author.getBooks()
                        .stream()
                        .map(BookMapper::mapToBookDTO)
                        .collect(Collectors.toSet()));
    }

    public static AuthorDTO mapToAuthorDTO(Author author) {
        logger.info(MAP_TO_AUTHOR_DTO_ACCESSED, author);
        return new AuthorDTO()
                .setFirstName(author.getFirstName())
                .setLastName(author.getLastName());
    }
}