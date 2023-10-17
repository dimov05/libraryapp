package bg.libapp.libraryapp.web;

import bg.libapp.libraryapp.LibraryAppBaseTest;
import bg.libapp.libraryapp.model.dto.author.AuthorRequest;
import bg.libapp.libraryapp.model.dto.book.BookAddRequest;
import bg.libapp.libraryapp.model.dto.book.BookChangeStatusRequest;
import bg.libapp.libraryapp.model.dto.book.BookDTO;
import bg.libapp.libraryapp.model.dto.book.BookExtendedDTO;
import bg.libapp.libraryapp.model.dto.book.BookUpdatePublisherRequest;
import bg.libapp.libraryapp.model.dto.book.BookUpdateTotalQuantityRequest;
import bg.libapp.libraryapp.model.dto.book.BookUpdateYearRequest;
import bg.libapp.libraryapp.model.dto.genre.GenreDTO;
import bg.libapp.libraryapp.model.dto.genre.GenreRequest;
import bg.libapp.libraryapp.model.entity.Book;
import bg.libapp.libraryapp.model.entity.Genre;
import bg.libapp.libraryapp.model.entity.User;
import bg.libapp.libraryapp.model.enums.Role;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static bg.libapp.libraryapp.Constants.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class BookControllerTest extends LibraryAppBaseTest {
    @Test
    @Transactional
    void getBookByIsbn_Succeed() throws Exception {
        Book book = insertTestBook();

        MockHttpServletResponse response = this.mockMvc.perform(get("/api/books/" + book.getIsbn())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        BookExtendedDTO bookExtendedDTO = objectMapper.readValue(response.getContentAsString(), BookExtendedDTO.class);

        Assertions.assertNotNull(bookExtendedDTO);
        Assertions.assertEquals(book.getIsbn(), bookExtendedDTO.getIsbn());
        Assertions.assertEquals(book.getYear(), bookExtendedDTO.getYear());
        Assertions.assertEquals(book.getTitle(), bookExtendedDTO.getTitle());
        Assertions.assertEquals(book.getAvailableQuantity(), bookExtendedDTO.getAvailableQuantity());
        Assertions.assertEquals(book.getTotalQuantity(), bookExtendedDTO.getTotalQuantity());
        Assertions.assertEquals(book.getDeactivateReason(), bookExtendedDTO.getDeactivateReason());
        Assertions.assertEquals(book.getDateAdded().toLocalDate(), LocalDate.parse(bookExtendedDTO.getDateAdded()));
        Assertions.assertEquals(book.getGenres().size(), bookExtendedDTO.getGenres().size());
        Assertions.assertTrue(book.getGenres().stream().map(Genre::getName).toList().containsAll(
                bookExtendedDTO.getGenres().stream().map(GenreDTO::getName).toList()));
        Assertions.assertEquals(book.getAuthors().size(), bookExtendedDTO.getAuthors().size());
        Assertions.assertTrue(book.getAuthors().stream().map(a -> a.getFirstName() + a.getLastName()).toList().containsAll(
                bookExtendedDTO.getAuthors().stream().map(a -> a.getFirstName() + a.getLastName()).toList()));
    }

    @Test
    @Transactional
    void getBookByIsbn_BookNotFoundException_WithValidIsbn() throws Exception {
        String isbn = VALID_ISBN;

        MockHttpServletResponse response = this.mockMvc.perform(get("/api/books/" + isbn)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(BOOK_NOT_PRESENT, isbn), error);
    }

    @Test
    @Transactional
    void getBookByIsbn_BookNotFoundException_WithInvalidIsbn() throws Exception {
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/books/" + INVALID_ISBN)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(INVALID_ISBN_EXCEPTION, error);
    }

    @Test
    @Transactional
    void updateYear_ShouldUpdateYearWithCorrectData() throws Exception {
        Book book = insertTestBook();
        BookUpdateYearRequest updateYearRequest = new BookUpdateYearRequest()
                .setYear(book.getYear() - 1);
        User admin = insertAdmin();

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/year/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateYearRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        BookDTO editedBook = objectMapper.readValue(response.getContentAsString(), BookDTO.class);

        Assertions.assertNotNull(editedBook);
        Assertions.assertEquals(updateYearRequest.getYear(), editedBook.getYear());
        Assertions.assertEquals(book.getIsbn(), editedBook.getIsbn());
        Assertions.assertEquals(book.getTitle(), editedBook.getTitle());
        Assertions.assertEquals(book.getAvailableQuantity(), editedBook.getAvailableQuantity());
        Assertions.assertEquals(book.getTotalQuantity(), editedBook.getTotalQuantity());
        Assertions.assertEquals(book.getDeactivateReason(), editedBook.getDeactivateReason());
        Assertions.assertEquals(book.getGenres().size(), editedBook.getGenres().size());
        Assertions.assertTrue(book.getGenres().stream().map(Genre::getName).toList().containsAll(
                editedBook.getGenres().stream().map(GenreDTO::getName).toList()));

    }

    @Test
    @Transactional
    void updateYear_ShouldBeForbidden_IfNotAuthorized() throws Exception {
        Book book = insertTestBook();
        BookUpdateYearRequest updateYearRequest = new BookUpdateYearRequest()
                .setYear(YEAR - 1);
        User admin = insertAdmin();

        this.mockMvc.perform(put("/api/books/year/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.USER))) // only Admin and Moderator
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateYearRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void updateYear_ShouldThrowInvalidIsbn_IfBookIsWithInvalidIsbn() throws Exception {
        BookUpdateYearRequest updateYearRequest = new BookUpdateYearRequest()
                .setYear(YEAR - 1);
        User admin = insertAdmin();
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/year/" + INVALID_ISBN)
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateYearRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(INVALID_ISBN_EXCEPTION, error);
    }

    @Test
    @Transactional
    void updateYear_ShouldThrowBookNotFound_IfBookWithThisIsbnIsNotFound() throws Exception {
        String isbn = VALID_ISBN;
        BookUpdateYearRequest updateYearRequest = new BookUpdateYearRequest()
                .setYear(YEAR - 1);
        User admin = insertAdmin();
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/year/" + isbn).with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateYearRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(BOOK_NOT_PRESENT, isbn), error);
    }

    @Test
    @Transactional
    void updateYear_ShouldThrowValidationException_IfYearIsLessThan1000() throws Exception {
        Book book = insertTestBook();
        BookUpdateYearRequest updateYearRequest = new BookUpdateYearRequest()
                .setYear(999);
        User admin = insertAdmin();

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/year/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateYearRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertTrue(errors.contains(YEAR_RANGE_EXCEPTION));
    }

    @Test
    @Transactional
    void updateYear_ShouldThrowValidationException_IfYearIsLargerThan2100() throws Exception {
        Book book = insertTestBook();
        BookUpdateYearRequest updateYearRequest = new BookUpdateYearRequest()
                .setYear(2101);
        User admin = insertAdmin();

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/year/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateYearRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertTrue(errors.contains(YEAR_RANGE_EXCEPTION));
    }

    @Test
    @Transactional
    void updateYear_ShouldNotUpdateYeaR_IfOldYearIsEqualWithNewYear() throws Exception {
        Book book = insertTestBook();
        BookUpdateYearRequest updateYearRequest = new BookUpdateYearRequest()
                .setYear(book.getYear());
        User admin = insertAdmin();

        this.mockMvc.perform(put("/api/books/year/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateYearRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void updatePublisher_ShouldUpdatePublisherWithCorrectData() throws Exception {
        Book book = insertTestBook();
        BookUpdatePublisherRequest bookUpdatePublisherRequest = new BookUpdatePublisherRequest()
                .setPublisher("New publisher");
        User admin = insertAdmin();

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/publisher/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookUpdatePublisherRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        BookDTO editedBook = objectMapper.readValue(response.getContentAsString(), BookDTO.class);

        Assertions.assertNotNull(editedBook);

        Assertions.assertEquals("New publisher", editedBook.getPublisher());
        Assertions.assertEquals(book.getYear(), editedBook.getYear());
        Assertions.assertEquals(book.getIsbn(), editedBook.getIsbn());
        Assertions.assertEquals(book.getTitle(), editedBook.getTitle());
        Assertions.assertEquals(book.getAvailableQuantity(), editedBook.getAvailableQuantity());
        Assertions.assertEquals(book.getTotalQuantity(), editedBook.getTotalQuantity());
        Assertions.assertEquals(book.getDeactivateReason(), editedBook.getDeactivateReason());
        Assertions.assertEquals(book.getGenres().size(), editedBook.getGenres().size());
        Assertions.assertTrue(book.getGenres().stream().map(Genre::getName).toList().containsAll(
                editedBook.getGenres().stream().map(GenreDTO::getName).toList()));

    }

    @Test
    @Transactional
    void updatePublisher_ShouldBeForbidden_IfNotAuthorized() throws Exception {
        Book book = insertTestBook();
        BookUpdatePublisherRequest bookUpdatePublisherRequest = new BookUpdatePublisherRequest()
                .setPublisher("New publisher");
        User admin = insertAdmin();

        this.mockMvc.perform(put("/api/books/publisher/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.USER))) // only Admin and Moderator
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookUpdatePublisherRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void updatePublisher_ShouldThrowValidationException_IfPublisherIsBlank() throws Exception {
        Book book = insertTestBook();
        BookUpdatePublisherRequest bookUpdatePublisherRequest = new BookUpdatePublisherRequest()
                .setPublisher("     ");
        User admin = insertAdmin();

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/publisher/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookUpdatePublisherRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertTrue(errors.contains(PUBLISHER_NOT_BLANK_EXCEPTION));
    }

    @Test
    @Transactional
    void updatePublisher_ShouldThrowValidationException_IfPublisherIsLessThan1Symbol() throws Exception {
        Book book = insertTestBook();
        BookUpdatePublisherRequest bookUpdatePublisherRequest = new BookUpdatePublisherRequest()
                .setPublisher("");
        User admin = insertAdmin();

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/publisher/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookUpdatePublisherRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        System.out.println(errors);
        List<String> err = List.of(PUBLISHER_SIZE_EXCEPTION, PUBLISHER_NOT_BLANK_EXCEPTION);
        Assertions.assertTrue(errors.containsAll(err));
    }

    @Test
    @Transactional
    void updatePublisher_ShouldThrowValidationException_IfPublisherIsMoreThan100Symbol() throws Exception {
        Book book = insertTestBook();
        BookUpdatePublisherRequest bookUpdatePublisherRequest = new BookUpdatePublisherRequest()
                .setPublisher("asdasdas10asdasdasd20asdasdas10asdasdasd20asdasdas10asdasdasd20asdasdas10asdasdasd20asdasdas10asdasdasd20");
        User admin = insertAdmin();

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/publisher/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookUpdatePublisherRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertTrue(errors.contains(PUBLISHER_SIZE_EXCEPTION));
    }


    @Test
    @Transactional
    void updatePublisher_ShouldNotUpdatePublisher_IfOldPublisherEqualsNewPublisher() throws Exception {
        Book book = insertTestBook();
        BookUpdatePublisherRequest bookUpdatePublisherRequest = new BookUpdatePublisherRequest()
                .setPublisher(book.getPublisher());
        User admin = insertAdmin();

        this.mockMvc.perform(put("/api/books/publisher/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookUpdatePublisherRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void saveNewBook_Succeed() throws Exception {
        Book book = createBook();
        User admin = insertAdmin();
        BookAddRequest bookAddRequest = new BookAddRequest()
                .setIsbn(book.getIsbn())
                .setTitle(book.getTitle())
                .setYear(book.getYear())
                .setPublisher(book.getPublisher())
                .setTotalQuantity(book.getTotalQuantity())
                .setGenres(book.getGenres()
                        .stream()
                        .map(this::mapToGenreRequest).collect(Collectors.toSet()))
                .setAuthors(Set.of(new AuthorRequest("First", "Last")));
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/books")
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();
        BookDTO bookDTO = objectMapper.readValue(response.getContentAsString(), BookDTO.class);

        Assertions.assertNotNull(bookDTO);
        Assertions.assertEquals(book.getIsbn(), bookDTO.getIsbn());
        Assertions.assertEquals(book.getYear(), bookDTO.getYear());
        Assertions.assertEquals(book.getTitle(), bookDTO.getTitle());
        Assertions.assertEquals(book.getAvailableQuantity(), bookDTO.getAvailableQuantity());
        Assertions.assertEquals(book.getTotalQuantity(), bookDTO.getTotalQuantity());
        Assertions.assertEquals(book.getDeactivateReason(), bookDTO.getDeactivateReason());
        Assertions.assertEquals(book.getGenres().size(), bookDTO.getGenres().size());
        Assertions.assertTrue(book.getGenres().stream().map(Genre::getName).toList().containsAll(
                bookDTO.getGenres().stream().map(GenreDTO::getName).toList()));
    }

    private GenreRequest mapToGenreRequest(Genre g) {
        GenreRequest result = new GenreRequest();
        result.setName(g.getName());
        return result;
    }

    @Test
    @Transactional
    void saveNewBook_ShouldThrowBookAlreadyAddedException_OnAlreadyAddedBook() throws Exception {
        Book book = insertTestBook();
        User admin = insertAdmin();
        BookAddRequest bookAddRequest = new BookAddRequest()
                .setIsbn(book.getIsbn())
                .setTitle(book.getTitle())
                .setYear(book.getYear())
                .setPublisher(book.getPublisher())
                .setTotalQuantity(book.getTotalQuantity())
                .setGenres(book.getGenres()
                        .stream()
                        .map(this::mapToGenreRequest).collect(Collectors.toSet()))
                .setAuthors(Set.of(new AuthorRequest("First", "Last")));

        MockHttpServletResponse response = this.mockMvc.perform(post("/api/books")
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        String errors = response.getContentAsString();

        Assertions.assertEquals(errors, String.format(BOOK_ALREADY_ADDED, book.getIsbn()));
    }

    @Test
    @Transactional
    void saveNewBook_ShouldThrowGenresNotFound_OnMissingGenres() throws Exception {
        Book book = createBook();
        User admin = insertAdmin();
        BookAddRequest bookAddRequest = new BookAddRequest()
                .setIsbn(book.getIsbn())
                .setTitle(book.getTitle())
                .setYear(book.getYear())
                .setPublisher(book.getPublisher())
                .setTotalQuantity(book.getTotalQuantity())
                .setGenres(Set.of(new GenreRequest(RANDOM_NAME)))
                .setAuthors(Set.of(new AuthorRequest("First", "Last")));
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/books")
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        String errors = response.getContentAsString();

        Assertions.assertEquals(errors, GENRE_MISSING_EXCEPTION);
    }

    @Test
    @Transactional
    void saveNewBook_ShouldThrowExceptions_OnIncorrectInput() throws Exception {
        User admin = insertAdmin();
        BookAddRequest bookAddRequest = new BookAddRequest()
                .setIsbn("")
                .setTitle("")
                .setYear(999)
                .setPublisher("")
                .setTotalQuantity(-1)
                .setGenres(Set.of(new GenreRequest("")))
                .setAuthors(Set.of(new AuthorRequest("", "")));
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/books")
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();

        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        List<String> errorMessages = List.of(ISBN_INCORRECT_VALUE, ISBN_NOT_BLANK, PUBLISHER_NOT_BLANK_EXCEPTION,
                YEAR_RANGE_EXCEPTION, PUBLISHER_SIZE_EXCEPTION, TITLE_NOT_BLANK, TITLE_LENGTH_VALIDATION, TOTAL_QUANTITY_MORE_THAN_0,
                GENRE_NOT_BLANK, AUTHOR_FIRST_NAME_LENGTH_VALIDATION, AUTHOR_FIRST_NAME_NOT_EMPTY,
                AUTHOR_LAST_NAME_LENGTH_VALIDATION, AUTHOR_LAST_NAME_NOT_EMPTY, GENRE_LENGTH_VALIDATION);
        Assertions.assertEquals(14, errors.size());
        Assertions.assertTrue(errors.containsAll(errorMessages));
    }

    @Test
    @Transactional
    void saveNewBook_ShouldBeForbidden_IfNotAuthorized() throws Exception {
        Book book = createBook();
        User admin = insertAdmin();
        BookAddRequest bookAddRequest = new BookAddRequest()
                .setIsbn(book.getIsbn())
                .setTitle(book.getTitle())
                .setYear(book.getYear())
                .setPublisher(book.getPublisher())
                .setTotalQuantity(book.getTotalQuantity())
                .setGenres(book.getGenres()
                        .stream()
                        .map(this::mapToGenreRequest).collect(Collectors.toSet()))
                .setAuthors(Set.of(new AuthorRequest("First", "Last")));
        this.mockMvc.perform(post("/api/books")
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void deleteBookByIsbn_Succeed() throws Exception {
        Book book = insertTestBook(false);
        User admin = insertAdmin();
        Assertions.assertNotNull(bookRepository.findByIsbn(book.getIsbn()));
        this.mockMvc.perform(delete("/api/books/delete/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertNull(bookRepository.findByIsbn(book.getIsbn()).orElse(null));
    }

    @Test
    @Transactional
    void deleteBookByIsbn_ShouldBeForbidden_IfNotAuthorized() throws Exception {
        Book book = insertTestBook(false);
        User admin = insertAdmin();
        this.mockMvc.perform(delete("/api/books/delete/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void deleteBookByIsbn_ShouldThrowException_IfBookIsActive() throws Exception {
        Book book = insertTestBook(true);
        User admin = insertAdmin();
        MockHttpServletResponse response = this.mockMvc.perform(delete("/api/books/delete/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        String errorMessage = response.getContentAsString();
        Assertions.assertEquals(errorMessage, "Book with this isbn: '" + book.getIsbn() + "' is with active status and can not be deleted from library!");
    }

    @Test
    @Transactional
    void updateTotalQuantity_Succeed() throws Exception {
        Book book = insertTestBook();
        User admin = insertAdmin();
        BookUpdateTotalQuantityRequest updateTotalQuantityRequest =
                new BookUpdateTotalQuantityRequest().setTotalQuantity(10);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/total-quantity/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTotalQuantityRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        BookDTO editedBook = objectMapper.readValue(response.getContentAsString(), BookDTO.class);
        Assertions.assertNotNull(editedBook);

        Assertions.assertEquals(book.getPublisher(), editedBook.getPublisher());
        Assertions.assertEquals(book.getYear(), editedBook.getYear());
        Assertions.assertEquals(book.getIsbn(), editedBook.getIsbn());
        Assertions.assertEquals(book.getTitle(), editedBook.getTitle());
        Assertions.assertEquals(book.getAvailableQuantity() + (10 - book.getTotalQuantity()), editedBook.getAvailableQuantity());
        Assertions.assertEquals(10, editedBook.getTotalQuantity());
        Assertions.assertEquals(book.getDeactivateReason(), editedBook.getDeactivateReason());
        Assertions.assertEquals(book.getGenres().size(), editedBook.getGenres().size());
        Assertions.assertTrue(book.getGenres().stream().map(Genre::getName).toList().containsAll(
                editedBook.getGenres().stream().map(GenreDTO::getName).toList()));


    }

    @Test
    @Transactional
    void updateTotalQuantity_ShouldThrowInsufficientNewTotalQuantityException_IfNewTotalQuantityIsLessThanRentedQuantity() throws Exception {
        Book book = createBook();
        book.setAvailableQuantity(3);
        book.setTotalQuantity(6);
        bookRepository.saveAndFlush(book);
        int rentedQuantity = book.getTotalQuantity() - book.getAvailableQuantity();
        User admin = insertAdmin();
        BookUpdateTotalQuantityRequest updateTotalQuantityRequest =
                new BookUpdateTotalQuantityRequest().setTotalQuantity(rentedQuantity - 1);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/total-quantity/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTotalQuantityRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        String error = response.getContentAsString();
        Assertions.assertEquals("Total quantity '" + (rentedQuantity - 1) + "' is not enough for book with isbn '" + book.getIsbn() + "'! There are more rented books", error);
    }

    @Test
    @Transactional
    void updateTotalQuantity_ShouldThrowExceptions_OnInvalidInput() throws Exception {
        Book book = insertTestBook();
        User admin = insertAdmin();
        BookUpdateTotalQuantityRequest updateTotalQuantityRequest =
                new BookUpdateTotalQuantityRequest().setTotalQuantity(-1);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/total-quantity/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTotalQuantityRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        List<String> errorMessages = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        List<String> errors = List.of(BOOK_QUANTITY_LESS_THAN_0);
        Assertions.assertEquals(1, errorMessages.size());
        Assertions.assertTrue(errorMessages.containsAll(errors));
    }

    @Test
    @Transactional
    void updateTotalQuantity_ShouldBeForbidden_IfNotAuthorized() throws Exception {
        Book book = insertTestBook();
        User admin = insertAdmin();
        BookUpdateTotalQuantityRequest bookUpdateTotalQuantityRequest = new BookUpdateTotalQuantityRequest().setTotalQuantity(10);
        this.mockMvc.perform(put("/api/books/total-quantity/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.USER)))
                        .content(objectMapper.writeValueAsString(bookUpdateTotalQuantityRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void changeStatusOfBook_Succeed_FromInActiveToActive() throws Exception {
        User admin = insertAdmin();
        Book book = createBook()
                .setActive(false)
                .setDeactivateReason(COPYRIGHT_REASON);
        bookRepository.saveAndFlush(book);
        BookChangeStatusRequest bookChangeStatusRequest = new BookChangeStatusRequest(true, null);

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/change-status/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .content(objectMapper.writeValueAsString(bookChangeStatusRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        BookDTO bookDTO = objectMapper.readValue(response.getContentAsString(), BookDTO.class);

        Assertions.assertNull(bookDTO.getDeactivateReason());
        Assertions.assertTrue(bookDTO.getIsActive());
    }

    @Test
    @Transactional
    void changeStatusOfBook_Succeed_FromActiveToInActive() throws Exception {
        User admin = insertAdmin();
        Book book = createBook()
                .setActive(true);
        bookRepository.saveAndFlush(book);
        BookChangeStatusRequest bookChangeStatusRequest = new BookChangeStatusRequest(false, COPYRIGHT_REASON);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/change-status/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .content(objectMapper.writeValueAsString(bookChangeStatusRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        BookDTO bookDTO = objectMapper.readValue(response.getContentAsString(), BookDTO.class);

        Assertions.assertEquals(COPYRIGHT_REASON, bookDTO.getDeactivateReason());
        Assertions.assertFalse(bookDTO.getIsActive());
    }

    @Test
    @Transactional
    void changeStatusOfBook_Succeed_FromInActiveToInActiveWithDifferentReason() throws Exception {
        User admin = insertAdmin();
        Book book = createBook()
                .setActive(false)
                .setDeactivateReason(BANNED_REASON);
        bookRepository.saveAndFlush(book);
        BookChangeStatusRequest bookChangeStatusRequest = new BookChangeStatusRequest(false, COPYRIGHT_REASON);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/books/change-status/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .content(objectMapper.writeValueAsString(bookChangeStatusRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        BookDTO bookDTO = objectMapper.readValue(response.getContentAsString(), BookDTO.class);

        Assertions.assertEquals(COPYRIGHT_REASON, bookDTO.getDeactivateReason());
        Assertions.assertFalse(bookDTO.getIsActive());
    }

    @Test
    @Transactional
    void changeStatusOfBook_ThrowException_IfNotAuthorized() throws Exception {
        Book book = insertTestBook();
        User admin = insertAdmin();
        BookChangeStatusRequest bookChangeStatusRequest = new BookChangeStatusRequest(true, COPYRIGHT_REASON);
        this.mockMvc.perform(put("/api/books/change-status/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.USER)))
                        .content(objectMapper.writeValueAsString(bookChangeStatusRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void changeStatusOfBook_ThrowException_IfDeactivateReasonIsInvalidAndChangeStatusToInActive() throws Exception {
        Book book = insertTestBook();
        User admin = insertAdmin();
        BookChangeStatusRequest bookChangeStatusRequest = new BookChangeStatusRequest(false, BAD_DEACTIVATE_REASON);

        String response = this.mockMvc.perform(put("/api/books/change-status/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(ADMIN_PASSWORD).roles(String.valueOf(Role.ADMIN)))
                        .content(objectMapper.writeValueAsString(bookChangeStatusRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(String.format(DEACTIVATE_REASON_IS_INVALID, BAD_DEACTIVATE_REASON), response);
    }

    @Test
    @Transactional
    void getAllBooks_Succeed() throws Exception {
        User admin = insertAdmin();
        Book book = insertTestBook();
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/books")
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        List<BookExtendedDTO> books = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertNotNull(books);
        Assertions.assertFalse(books.isEmpty());
        Assertions.assertTrue(books.stream().map(BookExtendedDTO::getIsbn).collect(Collectors.toSet())
                .contains(book.getIsbn()));
    }
}