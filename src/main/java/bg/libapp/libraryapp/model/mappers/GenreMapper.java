package bg.libapp.libraryapp.model.mappers;

import bg.libapp.libraryapp.model.dto.genre.GenreDTO;
import bg.libapp.libraryapp.model.entity.Genre;
import bg.libapp.libraryapp.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenreMapper {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public static GenreDTO mapToGenreDTO(Genre genre) {
        logger.info("mapToGenreDTO mapper method called with params " + genre);
        return new GenreDTO()
                .setName(genre.getName());
    }
}