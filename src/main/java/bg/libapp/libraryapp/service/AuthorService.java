package bg.libapp.libraryapp.service;

import bg.libapp.libraryapp.model.dto.author.AuthorExtendedDTO;
import bg.libapp.libraryapp.model.mappers.AuthorMapper;
import bg.libapp.libraryapp.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Set<AuthorExtendedDTO> getAllAuthors() {
        return authorRepository.findAll()
                .stream().map(AuthorMapper::mapToAuthorExtendedDTO)
                .collect(Collectors.toSet());
    }
}