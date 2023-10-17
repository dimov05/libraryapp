package bg.libapp.libraryapp.web;

import bg.libapp.libraryapp.LibraryAppBaseTest;
import bg.libapp.libraryapp.model.dto.author.AuthorExtendedDTO;
import bg.libapp.libraryapp.model.entity.User;
import bg.libapp.libraryapp.model.enums.Role;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class AuthorControllerTest extends LibraryAppBaseTest {
    @Test
    @Transactional
    void getAllAuthors_Succeed() throws Exception {
        User admin = insertAdmin();
        insertTestBook();
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/authors")
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        Set<AuthorExtendedDTO> authors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertNotNull(authors);
        Assertions.assertFalse(authors.isEmpty());
    }
}