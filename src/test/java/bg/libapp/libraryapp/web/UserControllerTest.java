package bg.libapp.libraryapp.web;

import bg.libapp.libraryapp.LibraryAppBaseTest;
import bg.libapp.libraryapp.model.dto.user.ChangePasswordRequest;
import bg.libapp.libraryapp.model.dto.user.ChangeRoleRequest;
import bg.libapp.libraryapp.model.dto.user.UpdateUserRequest;
import bg.libapp.libraryapp.model.dto.user.UserDTO;
import bg.libapp.libraryapp.model.dto.user.UserExtendedDTO;
import bg.libapp.libraryapp.model.entity.User;
import bg.libapp.libraryapp.model.enums.Role;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Collectors;

import static bg.libapp.libraryapp.Constants.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class UserControllerTest extends LibraryAppBaseTest {
    @Test
    @Transactional
    void getUserById_Succeed_WhenAdminCallsEndpoint() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/users/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        UserExtendedDTO userExtendedDTO = objectMapper.readValue(response.getContentAsString(), UserExtendedDTO.class);

        Assertions.assertNotNull(userExtendedDTO);
        Assertions.assertEquals(user.getId(), userExtendedDTO.getId());
        Assertions.assertEquals(Role.USER.name(), userExtendedDTO.getRole());
        Assertions.assertEquals(user.getUsername(), userExtendedDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userExtendedDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth(), userExtendedDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userExtendedDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userExtendedDTO.getLastName());
    }

    @Test
    @Transactional
    void getUserById_Succeed_WhenUserIdEqualsAuthenticatedUser() throws Exception {
        User user = insertUser();
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/users/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        UserExtendedDTO userExtendedDTO = objectMapper.readValue(response.getContentAsString(), UserExtendedDTO.class);

        Assertions.assertNotNull(userExtendedDTO);
        Assertions.assertEquals(user.getId(), userExtendedDTO.getId());
        Assertions.assertEquals(Role.USER.name(), userExtendedDTO.getRole());
        Assertions.assertEquals(user.getUsername(), userExtendedDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userExtendedDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth(), userExtendedDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userExtendedDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userExtendedDTO.getLastName());
    }

    @Test
    @Transactional
    void getUserById_Forbidden_WhenUserIdIsNotEqualToAuthenticatedUser() throws Exception {
        User user = insertUser();
        User userToGet = insertUser2();
        this.mockMvc.perform(get("/api/users/" + userToGet.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn().getResponse();
    }

    @Test
    @Transactional
    void getUserById_UserNotFound_OnNoUserWithThisIdInDatabase() throws Exception {
        User admin = insertAdmin();
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/users/" + BAD_ID)
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(USER_WITH_ID_NOT_FOUND, BAD_ID), error);
    }

    @Test
    @Transactional
    void editUserAndSave_Succeed_WhenAdminCallsEndpoint() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        UpdateUserRequest updateUserRequest = new UpdateUserRequest()
                .setFirstName("Updated First Name")
                .setLastName("Updated Last Name")
                .setDisplayName("Updated Display Name");
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/edit/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
//        objectMapper.registerModule(new JavaTimeModule());

        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(Role.USER.name(), userDTO.getRole());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(updateUserRequest.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth(), userDTO.getDateOfBirth());
        Assertions.assertEquals(updateUserRequest.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(updateUserRequest.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void editUserAndSave_Succeed_WhenUserIdEqualsAuthenticatedUser() throws Exception {
        User user = insertUser();
        UpdateUserRequest updateUserRequest = new UpdateUserRequest()
                .setFirstName("Updated First Name")
                .setLastName("Updated Last Name")
                .setDisplayName("Updated Display Name");
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/edit/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
//        objectMapper.registerModule(new JavaTimeModule());

        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(Role.USER.name(), userDTO.getRole());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(updateUserRequest.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth(), userDTO.getDateOfBirth());
        Assertions.assertEquals(updateUserRequest.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(updateUserRequest.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void editUserAndSave_Forbidden_WhenUserIdIsNotEqualToAuthenticatedUser() throws Exception {
        User user = insertUser();
        User user2 = insertUser2();
        UpdateUserRequest updateUserRequest = new UpdateUserRequest()
                .setFirstName("Updated First Name")
                .setLastName("Updated Last Name")
                .setDisplayName("Updated Display Name");
        this.mockMvc.perform(put("/api/users/edit/" + user.getId())
                        .with(user(user2.getUsername()).password(user2.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn().getResponse();
    }

    @Test
    @Transactional
    void editUserAndSave_UserNotFound_OnNoUserWithThisIdInDatabase() throws Exception {
        User admin = insertAdmin();
        UpdateUserRequest updateUserRequest = new UpdateUserRequest()
                .setFirstName("Updated First Name")
                .setLastName("Updated Last Name")
                .setDisplayName("Updated Display Name");
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/edit/" + BAD_ID)
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(USER_WITH_ID_NOT_FOUND, BAD_ID), error);
    }

    @Test
    @Transactional
    void editUserAndSave_ShouldThrowValidationException_OnBadInput() throws Exception {
        User user = insertUser();
        UpdateUserRequest updateUserRequest = new UpdateUserRequest()
                .setFirstName("")
                .setLastName("")
                .setDisplayName("");
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/edit/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertTrue(errors.containsAll(List.of(FIRST_NAME_EMPTY_EXCEPTION, LAST_NAME_EMPTY_EXCEPTION, DISPLAY_NAME_EMPTY_EXCEPTION,
                FIRST_NAME_TOO_SHORT, LAST_NAME_TOO_SHORT, DISPLAY_NAME_TOO_SHORT)));
    }

    @Test
    @Transactional
    void changeRole_Succeed_WhenAdminCallsEndpoint() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(1);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/change-role/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeRoleRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
//        objectMapper.registerModule(new JavaTimeModule());

        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(Role.MODERATOR.name(), userDTO.getRole());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void changeRole_Forbidden_WhenUserIdIsNotEqualToAuthenticatedUser() throws Exception {
        User user = insertUser();
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(1);

        this.mockMvc.perform(put("/api/users/change-role/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeRoleRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn().getResponse();
    }

    @Test
    @Transactional
    void changeRole_UserNotFound_OnNoUserWithThisIdInDatabase() throws Exception {
        User admin = insertAdmin();
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(1);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/change-role/" + BAD_ID)
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeRoleRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(USER_WITH_ID_NOT_FOUND, BAD_ID), error);
    }

    @Test
    @Transactional
    void changeRole_ShouldThrowValidationException_OnBadInputLower() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(-1);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/change-role/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeRoleRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertTrue(errors.contains(INVALID_ROLE_ORDINAL));
    }

    @Test
    @Transactional
    void changeRole_ShouldThrowValidationException_OnBadInputHigher() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(3);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/change-role/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeRoleRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertTrue(errors.contains(INVALID_ROLE_ORDINAL));
    }

    @Test
    @Transactional
    void changePassword_Succeed_WhenAdminCallsEndpoint() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest()
                .setNewPassword("New Password")
                .setOldPassword(user.getPassword())
                .setConfirmNewPassword("New Password");
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/change-password/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        User updatedUser = userRepository.findById(user.getId()).get();

        Assertions.assertNotNull(userDTO);
        Assertions.assertTrue(passwordEncoder.matches(changePasswordRequest.getNewPassword(), updatedUser.getPassword()));
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(Role.USER.name(), userDTO.getRole());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
        // cannot compare hashed passwords
    void changePassword_Succeed_WhenUserIdEqualsAuthenticatedUser() throws Exception {
        User user = insertUser();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest()
                .setNewPassword("New Password")
                .setOldPassword(user.getPassword())
                .setConfirmNewPassword("New Password");
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/change-password/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        User updatedUser = userRepository.findById(user.getId()).get();

        Assertions.assertNotNull(userDTO);
        Assertions.assertTrue(passwordEncoder.matches(changePasswordRequest.getNewPassword(), updatedUser.getPassword()));
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(Role.USER.name(), userDTO.getRole());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void changePassword_Forbidden_WhenUserIdIsNotEqualToAuthenticatedUser() throws Exception {
        User user = insertUser();
        User user2 = insertUser2();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest()
                .setNewPassword("New Password")
                .setOldPassword(user.getPassword())
                .setConfirmNewPassword("New Password");
        this.mockMvc.perform(put("/api/users/change-password/" + user.getId())
                        .with(user(user2.getUsername()).password(user2.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn().getResponse();
    }

    @Test
    @Transactional
    void changePassword_UserNotFound_OnNoUserWithThisIdInDatabase() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest()
                .setNewPassword("New Password")
                .setOldPassword(user.getPassword())
                .setConfirmNewPassword("New Password");
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/change-password/" + BAD_ID)
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(USER_WITH_ID_NOT_FOUND, BAD_ID), error);
    }

    @Test
    @Transactional
    void changePassword_ShouldThrowValidationException_OnBadInput() throws Exception {
        User user = insertUser();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest()
                .setNewPassword("")
                .setOldPassword("")
                .setConfirmNewPassword("");
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/change-password/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertTrue(errors.containsAll(List.of(PASSWORD_SHOULD_BE_AT_LEAST_6_SYMBOLS, PASSWORD_MUST_NOT_BE_EMPTY)));
    }

    @Test
    @Transactional
    void changePassword_ShouldThrowValidationException_OnBadInputAndNewAndConfirmPasswordsNotMatching() throws Exception {
        User user = insertUser();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest()
                .setNewPassword("1")
                .setOldPassword("2")
                .setConfirmNewPassword("3");
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/change-password/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertTrue(errors.containsAll(List.of(PASSWORD_SHOULD_BE_AT_LEAST_6_SYMBOLS, PASSWORDS_MUST_MATCH)));
    }

    @Test
    @Transactional
    void deleteUserById_Succeed() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        Assertions.assertNotNull(userRepository.findById(user.getId()));
        this.mockMvc.perform(delete("/api/users/delete/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        Assertions.assertNull(userRepository.findById(user.getId()).orElse(null));

    }

    @Test
    @Transactional
    void deleteUserById_Forbidden_IfNotAuthorized() throws Exception {
        User user = insertUser();
        this.mockMvc.perform(delete("/api/users/delete/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn().getResponse();
    }

    @Test
    @Transactional
    void deleteUserById_UserNotFound_OnNoUserWithThisIdInDatabase() throws Exception {
        User admin = insertAdmin();
        MockHttpServletResponse response = this.mockMvc.perform(delete("/api/users/delete/" + BAD_ID)
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(USER_WITH_ID_NOT_FOUND, BAD_ID), error);
    }

    @Test
    @Transactional
    void deactivateUser_Succeed_AuthorizedAsAdmin() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        Assertions.assertTrue(user.isActive());

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/deactivate/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
//        objectMapper.registerModule(new JavaTimeModule());

        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertFalse(userDTO.isActive());
    }

    @Test
    @Transactional
    void deactivateUser_Succeed_OnAuthorizedAsUserWithSameId() throws Exception {
        User user = insertUser();
        Assertions.assertTrue(user.isActive());

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/deactivate/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
//        objectMapper.registerModule(new JavaTimeModule());

        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertFalse(userDTO.isActive());

    }

    @Test
    @Transactional
    void deactivateUser_Forbidden_IfNotAuthorized() throws Exception {
        User user = insertUser();
        User user2 = insertUser2();
        this.mockMvc.perform(put("/api/users/deactivate/" + user2.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn().getResponse();
    }

    @Test
    @Transactional
    void deactivateUser_UserNotFound_OnNoUserWithThisIdInDatabase() throws Exception {
        User admin = insertAdmin();
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/deactivate/" + BAD_ID)
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(USER_WITH_ID_NOT_FOUND, BAD_ID), error);
    }

    @Test
    @Transactional
    void activateUser_Succeed_AuthorizedAsAdmin() throws Exception {
        User admin = insertAdmin();
        User user = insertDeactivatedUser();
        Assertions.assertFalse(user.isActive());

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/activate/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
//        objectMapper.registerModule(new JavaTimeModule());

        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertTrue(userDTO.isActive());
    }

    @Test
    @Transactional
    void activateUser_Succeed_OnAuthorizedAsUserWithSameId() throws Exception {
        User user = insertDeactivatedUser();
        Assertions.assertFalse(user.isActive());

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/activate/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
//        objectMapper.registerModule(new JavaTimeModule());

        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertTrue(userDTO.isActive());

    }

    @Test
    @Transactional
    void activateUser_Forbidden_IfNotAuthorized() throws Exception {
        User user = insertDeactivatedUser();
        User user2 = insertUser2();
        this.mockMvc.perform(put("/api/users/deactivate/" + user2.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn().getResponse();
    }

    @Test
    @Transactional
    void activateUser_UserNotFound_OnNoUserWithThisIdInDatabase() throws Exception {
        User admin = insertAdmin();
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/deactivate/" + BAD_ID)
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(USER_WITH_ID_NOT_FOUND, BAD_ID), error);
    }

    @Test
    @Transactional
    void getAllUsers_Succeed() throws Exception {
        User admin = insertAdmin();
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/users")
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        List<UserDTO> users = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertNotNull(users);
        Assertions.assertFalse(users.isEmpty());
        Assertions.assertTrue(users.stream().map(UserDTO::getId).collect(Collectors.toSet())
                .contains(admin.getId()));
    }

    @Test
    @Transactional
    void getAllUsers_Forbidden() throws Exception {
        User user = insertUser();
        this.mockMvc.perform(get("/api/users")
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn().getResponse();
    }
}