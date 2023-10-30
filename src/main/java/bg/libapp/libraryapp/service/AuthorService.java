package bg.libapp.libraryapp.service;

import bg.libapp.libraryapp.model.dto.author.AuthorExtendedDTO;
import bg.libapp.libraryapp.model.dto.author.AuthorRequest;
import bg.libapp.libraryapp.model.entity.Author;
import bg.libapp.libraryapp.model.mappers.AuthorMapper;
import bg.libapp.libraryapp.repository.AuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static bg.libapp.libraryapp.model.constants.ApplicationConstants.FIND_OR_CREATE_AUTHOR_ACCESSED_LOGGER;
import static bg.libapp.libraryapp.model.constants.ApplicationConstants.GET_ALL_AUTHORS_ACCESSED_LOGGER;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final Logger logger = LoggerFactory.getLogger(AuthorService.class);

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Set<AuthorExtendedDTO> getAllAuthors() {
        logger.info(GET_ALL_AUTHORS_ACCESSED_LOGGER);
        return authorRepository.findAll()
                .stream().map(AuthorMapper::mapToAuthorExtendedDTO)
                .collect(Collectors.toSet());
    }

    public Author findOrCreate(AuthorRequest author) {
        logger.info(FIND_OR_CREATE_AUTHOR_ACCESSED_LOGGER, author);
        return this.authorRepository.findAuthorByFirstNameAndLastName(author.getFirstName(), author.getLastName())
                .orElse(authorRepository.saveAndFlush(
                        new Author()
                                .setFirstName(author.getFirstName())
                                .setLastName(author.getLastName())));
    }
}