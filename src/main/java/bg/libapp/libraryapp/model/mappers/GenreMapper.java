package bg.libapp.libraryapp.model.mappers;

import bg.libapp.libraryapp.model.dto.genre.GenreDTO;
import bg.libapp.libraryapp.model.entity.Genre;
import bg.libapp.libraryapp.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static bg.libapp.libraryapp.model.constants.ApplicationConstants.MAP_TO_GENRE_DTO_ACCESSED;

public class GenreMapper {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public static GenreDTO mapToGenreDTO(Genre genre) {
        logger.info(MAP_TO_GENRE_DTO_ACCESSED, genre);
        return new GenreDTO()
                .setName(genre.getName());
    }
}