package bg.libapp.libraryapp.web;

import bg.libapp.libraryapp.LibraryAppBaseTest;
import bg.libapp.libraryapp.model.dto.genre.GenreDTO;
import bg.libapp.libraryapp.model.entity.User;
import bg.libapp.libraryapp.model.enums.Role;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class GenreControllerTest extends LibraryAppBaseTest {
    @Test
    @Transactional
    void getAllGenres_Succeed() throws Exception {
        User admin = insertAdmin();
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/genres")
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        Set<GenreDTO> genres = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertNotNull(genres);
        Assertions.assertFalse(genres.isEmpty());
    }
}