package bg.libapp.libraryapp.web;

import bg.libapp.libraryapp.LibraryAppBaseTest;
import bg.libapp.libraryapp.model.dto.rent.RentAddRequest;
import bg.libapp.libraryapp.model.dto.rent.RentDTO;
import bg.libapp.libraryapp.model.entity.Book;
import bg.libapp.libraryapp.model.entity.Rent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static bg.libapp.libraryapp.Constants.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


class RentControllerTest extends LibraryAppBaseTest {
    @Test
    @Transactional
    void rentBook_Succeed_BronzeSubscription() throws Exception {
        User user = insertUser();
        Book book = insertTestBook();
        int bookAvailableQuantity = book.getAvailableQuantity();
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book.getIsbn())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();
        RentDTO rentDTO = objectMapper.readValue(response.getContentAsString(), RentDTO.class);
        user = userRepository.findByUsername(user.getUsername()).orElse(null);
        Assertions.assertTrue(user.getRents().stream().map(Rent::getId).collect(Collectors.toSet())
                .contains(rentDTO.getId()));
        Assertions.assertEquals(bookAvailableQuantity - 1, book.getAvailableQuantity());
        Assertions.assertEquals(user.getId(), rentDTO.getUser().getId());
        Assertions.assertEquals(book.getIsbn(), rentDTO.getBook().getIsbn());
        Assertions.assertEquals(LocalDate.now().plusDays(30).toString(), rentDTO.getExpectedReturnDate());
        Assertions.assertEquals(LocalDate.now().toString(), rentDTO.getRentDate());
        Assertions.assertEquals("null", rentDTO.getActualReturnDate());
    }

    @Test
    @Transactional
    void rentBook_Succeed_SilverSubscription() throws Exception {
        User user = insertUser();
        user.setSubscription(subscriptionRepository.findById(SILVER_ID).orElse(null));
        userRepository.saveAndFlush(user);
        Book book = insertTestBook();
        int bookAvailableQuantity = book.getAvailableQuantity();
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book.getIsbn())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();
        RentDTO rentDTO = objectMapper.readValue(response.getContentAsString(), RentDTO.class);
        user = userRepository.findByUsername(user.getUsername()).orElse(null);
        Assertions.assertTrue(user.getRents().stream().map(Rent::getId).collect(Collectors.toSet())
                .contains(rentDTO.getId()));
        Assertions.assertEquals(bookAvailableQuantity - 1, book.getAvailableQuantity());
        Assertions.assertEquals(user.getId(), rentDTO.getUser().getId());
        Assertions.assertEquals(book.getIsbn(), rentDTO.getBook().getIsbn());
        Assertions.assertEquals(LocalDate.now().plusDays(45).toString(), rentDTO.getExpectedReturnDate());
        Assertions.assertEquals(LocalDate.now().toString(), rentDTO.getRentDate());
        Assertions.assertEquals("null", rentDTO.getActualReturnDate());
    }

    @Test
    @Transactional
    void rentBook_Succeed_GoldSubscription() throws Exception {
        User user = insertUser();
        user.setSubscription(subscriptionRepository.findById(GOLDEN_ID).orElse(null));
        userRepository.saveAndFlush(user);
        Book book = insertTestBook();
        int bookAvailableQuantity = book.getAvailableQuantity();
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book.getIsbn())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();
        RentDTO rentDTO = objectMapper.readValue(response.getContentAsString(), RentDTO.class);
        user = userRepository.findByUsername(user.getUsername()).orElse(null);
        Assertions.assertTrue(user.getRents().stream().map(Rent::getId).collect(Collectors.toSet())
                .contains(rentDTO.getId()));
        Assertions.assertEquals(bookAvailableQuantity - 1, book.getAvailableQuantity());
        Assertions.assertEquals(user.getId(), rentDTO.getUser().getId());
        Assertions.assertEquals(book.getIsbn(), rentDTO.getBook().getIsbn());
        Assertions.assertEquals(LocalDate.now().plusDays(60).toString(), rentDTO.getExpectedReturnDate());
        Assertions.assertEquals(LocalDate.now().toString(), rentDTO.getRentDate());
        Assertions.assertEquals("null", rentDTO.getActualReturnDate());
    }


    @Test
    @Transactional
    void rentBook_Succeed_OnAdminAddingRentForUser() throws Exception {
        User user = insertUser();
        User admin = insertAdmin();
        Book book = insertTestBook();
        int bookAvailableQuantity = book.getAvailableQuantity();
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();
        RentDTO rentDTO = objectMapper.readValue(response.getContentAsString(), RentDTO.class);
        Assertions.assertTrue(user.getRents().stream().map(Rent::getId).collect(Collectors.toSet())
                .contains(rentDTO.getId()));
        Assertions.assertEquals(bookAvailableQuantity - 1, book.getAvailableQuantity());
        Assertions.assertEquals(user.getId(), rentDTO.getUser().getId());
        Assertions.assertEquals(book.getIsbn(), rentDTO.getBook().getIsbn());
        Assertions.assertEquals(LocalDate.now().plusDays(30).toString(), rentDTO.getExpectedReturnDate());
        Assertions.assertEquals(LocalDate.now().toString(), rentDTO.getRentDate());
        Assertions.assertEquals("null", rentDTO.getActualReturnDate());
    }

    @Test
    @Transactional
    void rentBook_Forbidden_IfNotHavingRightAuthority() throws Exception {
        User user = insertUser();
        User user2 = insertUser2();
        Book book = insertTestBook();
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        this.mockMvc.perform(post("/api/rents/" + book.getIsbn())
                        .with(user(user2.getUsername()).password(user2.getPassword()).roles("NOBODY"))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn().getResponse();
    }

    @Test
    @Transactional
    void rentBook_Forbidden_OnUserAddingRentForAnotherUser() throws Exception {
        User user = insertUser();
        User user2 = insertUser2();
        Book book = insertTestBook();
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book.getIsbn())
                        .with(user(user2.getUsername()).password(user2.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();

        Assertions.assertEquals(String.format(USER_NOT_ELIGIBLE_TO_RENT, user2.getUsername(), user.getId()), response.getContentAsString());
    }

    @Test
    @Transactional
    void rentBook_Exception_OnBookWithSentIsbnNotFound() throws Exception {
        User user = insertUser();
        Book book = createBook();
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book.getIsbn())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();

        Assertions.assertEquals(String.format(BOOK_NOT_PRESENT, book.getIsbn()), response.getContentAsString());
    }

    @Test
    @Transactional
    void rentBook_Exception_OnUserHavingNoSubscription() throws Exception {
        User user = insertUser();
        user.setSubscription(null);
        userRepository.saveAndFlush(user);
        User admin = insertAdmin();
        Book book = insertTestBook();
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        Assertions.assertEquals(String.format(USER_WITH_ID_DOES_NOT_HAVE_SUBSCRIPTION, user.getId()), response.getContentAsString());
    }

    @Test
    @Transactional
    void rentBook_Exception_OnBookWithSentIsbnNotActive() throws Exception {
        User user = insertUser();
        Book book = insertTestBook(false);
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book.getIsbn())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();

        Assertions.assertEquals(String.format(BOOK_NOT_ACTIVE_EXCEPTION, book.getIsbn()), response.getContentAsString());
    }

    @Test
    @Transactional
    void rentBook_Exception_OnInsufficientAvailableQuantity() throws Exception {
        User user = insertUser();
        Book book = insertTestBookWithNoAvailableQuantity();
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book.getIsbn())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();

        Assertions.assertEquals(String.format(INSUFFICIENT_AVAILABLE_QUANTITY, book.getIsbn(), book.getAvailableQuantity()), response.getContentAsString());
    }

    @Test
    @Transactional
    void rentBook_Exception_OnUserToAddRentNotFoundById() throws Exception {
        User admin = insertAdmin();
        Book book = insertTestBook();
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(-1L);
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book.getIsbn())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();

        Assertions.assertEquals(String.format(USER_WITH_ID_NOT_FOUND, -1L), response.getContentAsString());
    }

    @Test
    @Transactional
    void rentBook_Exception_OnRentingBookTwiceForSameUser() throws Exception {
        Book book = insertTestBook();
        User user = insertUser();
        Rent rent = insertRentForBookAndUser(book, user);
        rentRepository.save(rent);
        user.getRents().add(rent);
        userRepository.save(user);
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book.getIsbn())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();

        Assertions.assertEquals(String.format(CANNOT_RENT_BOOK_TWICE, book.getIsbn()), response.getContentAsString());
    }

    @Test
    @Transactional
    void rentBook_Exception_OnUserHasProlongedRents() throws Exception {
        Book book = insertTestBook();
        Book book2 = insertSecondTestBook();
        User user = insertUser();
        Rent rent = new Rent()
                .setBook(book)
                .setRentDate(LocalDate.now())
                .setExpectedReturnDate(LocalDate.now().minusMonths(1))
                .setUser(user)
                .setActualReturnDate(null);
        rentRepository.save(rent);
        user.getRents().add(rent);
        userRepository.save(user);
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book2.getIsbn())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();

        Assertions.assertEquals(String.format(USER_HAS_PROLONGED_RENT, user.getId(), rent.getId()), response.getContentAsString());
    }

    @Test
    @Transactional
    void rentBook_Exception_OnUserRentedMaximumAllowedBooks_OnBronzeSubscription() throws Exception {
        List<Book> books = new ArrayList<>();
        books.add(insertTestBook());
        books.add(insertSecondTestBook());
        books.add(insertThirdTestBook());
        Book book4 = insertFourthTestBook();
        User user = insertUser();
        List<Rent> rents = initRents(user, books);
        rentRepository.saveAll(rents);
        rents.forEach(user::addRent);
        userRepository.save(user);
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book4.getIsbn())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        Assertions.assertEquals(String.format(USER_RENTED_MAXIMUM_ALLOWED_BOOKS, user.getId()), response.getContentAsString());
    }

    @Test
    @Transactional
    void rentBook_Exception_OnUserRentedMaximumAllowedBooks_OnSilverSubscription() throws Exception {
        List<Book> books = new ArrayList<>();
        books.add(insertTestBook());
        books.add(insertSecondTestBook());
        books.add(insertThirdTestBook());
        books.add(insertFifthTestBook());
        Book book4 = insertFourthTestBook();
        User user = insertUser();
        user.setSubscription(subscriptionRepository.findById(SILVER_ID).orElse(null));
        List<Rent> rents = initRents(user, books);
        rentRepository.saveAll(rents);
        rents.forEach(user::addRent);
        userRepository.save(user);
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book4.getIsbn())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        Assertions.assertEquals(String.format(USER_RENTED_MAXIMUM_ALLOWED_BOOKS, user.getId()), response.getContentAsString());
    }

    @Test
    @Transactional
    void rentBook_Exception_OnUserRentedMaximumAllowedBooks_OnGoldenSubscription() throws Exception {
        List<Book> books = new ArrayList<>();
        books.add(insertTestBook());
        books.add(insertSecondTestBook());
        books.add(insertThirdTestBook());
        books.add(insertFifthTestBook());
        books.add(insertSixthTestBook());
        Book book4 = insertFourthTestBook();
        User user = insertUser();
        user.setSubscription(subscriptionRepository.findById(GOLDEN_ID).orElse(null));
        List<Rent> rents = initRents(user, books);
        rentRepository.saveAll(rents);
        rents.forEach(user::addRent);
        userRepository.save(user);
        RentAddRequest rentAddRequest = new RentAddRequest()
                .setUserId(user.getId());
        MockHttpServletResponse response = this.mockMvc.perform(post("/api/rents/" + book4.getIsbn())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        Assertions.assertEquals(String.format(USER_RENTED_MAXIMUM_ALLOWED_BOOKS, user.getId()), response.getContentAsString());
    }



    @Test
    @Transactional
    void returnBook_Succeed() throws Exception {
        User user = insertUser();
        Book book = insertTestBook();
        Rent rent = insertRentForBookAndUser(book, user);
        int bookAvailableQuantity = book.getAvailableQuantity();

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/rents/return/" + rent.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        RentDTO rentDTO = objectMapper.readValue(response.getContentAsString(), RentDTO.class);
        Assertions.assertEquals(bookAvailableQuantity + 1, book.getAvailableQuantity());
        Assertions.assertEquals(LocalDate.now().toString(), rentDTO.getActualReturnDate());

    }

    @Test
    @Transactional
    void returnBook_Succeed_OnAdminReturningBook() throws Exception {
        User user = insertUser();
        User admin = insertAdmin();
        Book book = insertTestBook();
        Rent rent = insertRentForBookAndUser(book, user);
        int bookAvailableQuantity = book.getAvailableQuantity();

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/rents/return/" + rent.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        RentDTO rentDTO = objectMapper.readValue(response.getContentAsString(), RentDTO.class);
        Assertions.assertEquals(bookAvailableQuantity + 1, book.getAvailableQuantity());
        Assertions.assertEquals(LocalDate.now().toString(), rentDTO.getActualReturnDate());
    }

    @Test
    @Transactional
    void returnBook_Forbidden_IfUserTryToReturnBookForOtherUser() throws Exception {
        User user = insertUser();
        User user2 = insertUser2();
        Book book = insertTestBook();
        Rent rent = insertRentForBookAndUser(book, user);
        this.mockMvc.perform(put("/api/rents/return/" + rent.getId())
                        .with(user(user2.getUsername()).password(user2.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn().getResponse();
    }

    @Test
    @Transactional
    void returnBook_Exception_OnRentAlreadyReturned() throws Exception {
        User user = insertUser();
        Book book = insertTestBook();
        Rent rent = insertRentForBookAndUser(book, user);
        rent.setActualReturnDate(LocalDate.now().plusDays(1));
        rentRepository.saveAndFlush(rent);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/rents/return/" + rent.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();

        Assertions.assertEquals(String.format(RENT_ALREADY_RETURNED, rent.getId()), response.getContentAsString());
    }

    @Test
    @Transactional
    void getRentById_Succeed_WhenAdminCallsEndpoint() throws Exception {
        User admin = insertAdmin();
        Book book = insertTestBook();
        User user = insertUser();
        Rent rent = insertRentForBookAndUser(book, user);
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/rents/" + rent.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        RentDTO rentDTO = objectMapper.readValue(response.getContentAsString(), RentDTO.class);

        Assertions.assertNotNull(rentDTO);
        Assertions.assertEquals(rent.getId(), rentDTO.getId());
        Assertions.assertEquals(rent.getRentDate().toString(), rentDTO.getRentDate());
        Assertions.assertEquals(rent.getBook().getIsbn(), rentDTO.getBook().getIsbn());
        Assertions.assertEquals(rent.getUser().getId(), rentDTO.getUser().getId());
        Assertions.assertEquals(rent.getExpectedReturnDate().toString(), rentDTO.getExpectedReturnDate());
    }

    @Test
    @Transactional
    void getRentById_Succeed_WhenRentIsBelongsToUser() throws Exception {
        Book book = insertTestBook();
        User user = insertUser();
        Rent rent = insertRentForBookAndUser(book, user);
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/rents/" + rent.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        RentDTO rentDTO = objectMapper.readValue(response.getContentAsString(), RentDTO.class);

        Assertions.assertNotNull(rentDTO);
        Assertions.assertEquals(rent.getId(), rentDTO.getId());
        Assertions.assertEquals(rent.getRentDate().toString(), rentDTO.getRentDate());
        Assertions.assertEquals(rent.getBook().getIsbn(), rentDTO.getBook().getIsbn());
        Assertions.assertEquals(rent.getUser().getId(), rentDTO.getUser().getId());
        Assertions.assertEquals(rent.getExpectedReturnDate().toString(), rentDTO.getExpectedReturnDate());
    }

    @Test
    @Transactional
    void getRentById_Forbidden_WhenRentDoesNotBelongToUser() throws Exception {
        Book book = insertTestBook();
        User user = insertUser();
        User user2 = insertUser2();
        Rent rent = insertRentForBookAndUser(book, user);
        this.mockMvc.perform(get("/api/rents/" + rent.getId())
                        .with(user(user2.getUsername()).password(user2.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn().getResponse();
    }

    @Test
    @Transactional
    void getRentById_RentNotFound_OnNoRentWithThisIdInDatabase() throws Exception {
        User user = insertUser();
        long rentId = 9922992299L;
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/rents/" + rentId)
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();

        Assertions.assertEquals(String.format(RENT_WITH_ID_NOT_FOUND, rentId), response.getContentAsString());
    }

    @Test
    @Transactional
    void getAllRents_Succeed() throws Exception {
        User admin = insertAdmin();
        Book book = insertTestBook();
        User user = insertUser();
        Rent rent = insertRentForBookAndUser(book, user);
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/rents")
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        Set<RentDTO> rents = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertNotNull(rents);
        Assertions.assertFalse(rents.isEmpty());
        Assertions.assertTrue(rents.stream().map(RentDTO::getId).collect(Collectors.toSet())
                .contains(rent.getId()));
    }

    @Test
    @Transactional
    void getAllRents_Forbidden() throws Exception {
        User user = insertUser();
        this.mockMvc.perform(get("/api/rents")
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn().getResponse();
    }
}