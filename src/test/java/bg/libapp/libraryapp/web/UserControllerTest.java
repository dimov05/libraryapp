package bg.libapp.libraryapp.web;

import bg.libapp.libraryapp.LibraryAppBaseTest;
import bg.libapp.libraryapp.model.dto.user.AddBalanceRequest;
import bg.libapp.libraryapp.model.dto.user.AddSubscriptionRequest;
import bg.libapp.libraryapp.model.dto.user.ChangePasswordRequest;
import bg.libapp.libraryapp.model.dto.user.ChangeRoleRequest;
import bg.libapp.libraryapp.model.dto.user.UpdateUserRequest;
import bg.libapp.libraryapp.model.dto.user.UserDTO;
import bg.libapp.libraryapp.model.dto.user.UserExtendedDTO;
import bg.libapp.libraryapp.model.entity.Book;
import bg.libapp.libraryapp.model.entity.Rent;
import bg.libapp.libraryapp.model.entity.Subscription;
import bg.libapp.libraryapp.model.entity.User;
import bg.libapp.libraryapp.model.enums.Role;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static bg.libapp.libraryapp.Constants.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

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
        Assertions.assertEquals(user.getDateOfBirth().toString(), userExtendedDTO.getDateOfBirth());
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
        Assertions.assertEquals(user.getDateOfBirth().toString(), userExtendedDTO.getDateOfBirth());
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
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
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

        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(Role.USER.name(), userDTO.getRole());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(updateUserRequest.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
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
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
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
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
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
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
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

    @Test
    @Transactional
    void subscribeUser_SucceedBronzeSubscription_AuthorizedAsAdmin() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        user.setSubscription(null);
        user.setBalance(BigDecimal.valueOf(100));
        userRepository.saveAndFlush(user);
        Assertions.assertNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(bronze.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal priceTillEndOfMonth = calculateAmountTillEndOfMount(daysTillEndOfMonth, bronze.getPrice());
        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertNotNull(userDTO);
        Assertions.assertNotNull(userDTO.getSubscription());
        Assertions.assertEquals(bronze.getSubscriptionType().toString(), userDTO.getSubscription());
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(BigDecimal.valueOf(100).subtract(priceTillEndOfMonth).setScale(2).toString(), userDTO.getBalance());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void subscribeUser_SucceedBronzeSubscription_AuthorizedAsUser() throws Exception {
        User user = insertUser();
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        user.setSubscription(null);
        user.setBalance(BigDecimal.valueOf(100));
        userRepository.saveAndFlush(user);
        Assertions.assertNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(bronze.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal priceTillEndOfMonth = calculateAmountTillEndOfMount(daysTillEndOfMonth, bronze.getPrice());
        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertNotNull(userDTO);
        Assertions.assertNotNull(userDTO.getSubscription());
        Assertions.assertEquals(bronze.getSubscriptionType().toString(), userDTO.getSubscription());
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(BigDecimal.valueOf(100).subtract(priceTillEndOfMonth).setScale(2).toString(), userDTO.getBalance());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void subscribeUser_SucceedSilverSubscription_AuthorizedAsAdmin() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        Subscription silver = subscriptionRepository.findById(2L).orElse(null);
        user.setSubscription(null);
        user.setBalance(BigDecimal.valueOf(100));
        userRepository.saveAndFlush(user);
        Assertions.assertNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(silver.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal priceTillEndOfMonth = calculateAmountTillEndOfMount(daysTillEndOfMonth, silver.getPrice());
        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertNotNull(userDTO);
        Assertions.assertNotNull(userDTO.getSubscription());
        Assertions.assertEquals(silver.getSubscriptionType().toString(), userDTO.getSubscription());
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(BigDecimal.valueOf(100).subtract(priceTillEndOfMonth).setScale(2).toString(), userDTO.getBalance());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void subscribeUser_SucceedGoldenSubscription_AuthorizedAsAdmin() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        Subscription golden = subscriptionRepository.findById(3L).orElse(null);
        user.setSubscription(null);
        user.setBalance(BigDecimal.valueOf(100));
        userRepository.saveAndFlush(user);
        Assertions.assertNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(golden.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal priceTillEndOfMonth = calculateAmountTillEndOfMount(daysTillEndOfMonth, golden.getPrice());
        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertNotNull(userDTO);
        Assertions.assertNotNull(userDTO.getSubscription());
        Assertions.assertEquals(golden.getSubscriptionType().toString(), userDTO.getSubscription());
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(BigDecimal.valueOf(100).subtract(priceTillEndOfMonth).setScale(2).toString(), userDTO.getBalance());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void subscribeUser_SucceedUpgradeToSilverFromBronzeSubscription_AuthorizedAsUser() throws Exception {
        User user = insertUser();
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        Subscription silver = subscriptionRepository.findById(2L).orElse(null);
        user.setSubscription(bronze);
        user.setBalance(BigDecimal.valueOf(100));
        userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(silver.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal priceTillEndOfMonthToReturn = calculateAmountTillEndOfMount(daysTillEndOfMonth, bronze.getPrice());
        BigDecimal priceTillEndOfMonthToPay = calculateAmountTillEndOfMount(daysTillEndOfMonth, silver.getPrice());
        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertNotNull(userDTO);
        Assertions.assertNotNull(userDTO.getSubscription());
        Assertions.assertEquals(silver.getSubscriptionType().toString(), userDTO.getSubscription());
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(BigDecimal.valueOf(100)
                .add(priceTillEndOfMonthToReturn)
                .subtract(priceTillEndOfMonthToPay).setScale(2).toString(), userDTO.getBalance());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void subscribeUser_SucceedUpgradeToGoldenFromBronzeSubscription_AuthorizedAsUser() throws Exception {
        User user = insertUser();
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        Subscription golden = subscriptionRepository.findById(3L).orElse(null);
        user.setSubscription(bronze);
        user.setBalance(BigDecimal.valueOf(100));
        userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(golden.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal priceTillEndOfMonthToReturn = calculateAmountTillEndOfMount(daysTillEndOfMonth, bronze.getPrice());
        BigDecimal priceTillEndOfMonthToPay = calculateAmountTillEndOfMount(daysTillEndOfMonth, golden.getPrice());
        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertNotNull(userDTO);
        Assertions.assertNotNull(userDTO.getSubscription());
        Assertions.assertEquals(golden.getSubscriptionType().toString(), userDTO.getSubscription());
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(BigDecimal.valueOf(100)
                .add(priceTillEndOfMonthToReturn)
                .subtract(priceTillEndOfMonthToPay).setScale(2).toString(), userDTO.getBalance());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void subscribeUser_SucceedUpgradeToGoldenFromSilverSubscription_AuthorizedAsUser() throws Exception {
        User user = insertUser();
        Subscription silver = subscriptionRepository.findById(2L).orElse(null);
        Subscription golden = subscriptionRepository.findById(3L).orElse(null);
        user.setSubscription(silver);
        user.setBalance(BigDecimal.valueOf(100));
        userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(golden.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal priceTillEndOfMonthToReturn = calculateAmountTillEndOfMount(daysTillEndOfMonth, silver.getPrice());
        BigDecimal priceTillEndOfMonthToPay = calculateAmountTillEndOfMount(daysTillEndOfMonth, golden.getPrice());
        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertNotNull(userDTO);
        Assertions.assertNotNull(userDTO.getSubscription());
        Assertions.assertEquals(golden.getSubscriptionType().toString(), userDTO.getSubscription());
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(BigDecimal.valueOf(100)
                .add(priceTillEndOfMonthToReturn)
                .subtract(priceTillEndOfMonthToPay).setScale(2).toString(), userDTO.getBalance());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void subscribeUser_Forbidden_OnUserSubscribingForAnotherUser() throws Exception {
        User user = insertUser();
        User user2 = insertUser2();
        Subscription silver = subscriptionRepository.findById(2L).orElse(null);
        user.setSubscription(null);
        user.setBalance(BigDecimal.valueOf(100));
        userRepository.saveAndFlush(user);
        Assertions.assertNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(silver.getId());
        this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(user2.getUsername()).password(user2.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void subscribeUser_Exception_OnUserWithThisIdNotFound() throws Exception {
        User admin = insertAdmin();
        Subscription silver = subscriptionRepository.findById(2L).orElse(null);
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(silver.getId());

        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + BAD_ID)
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(USER_WITH_ID_NOT_FOUND, BAD_ID), error);
    }

    @Test
    @Transactional
    void testSubscribe_Exception_OnBalanceNotEnoughForBronzeSubscription() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        user.setSubscription(null);
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal priceTillEndOfMonth = calculateAmountTillEndOfMount(daysTillEndOfMonth, bronze.getPrice());
        user.setBalance(priceTillEndOfMonth.subtract(BigDecimal.valueOf(1)));
        userRepository.saveAndFlush(user);
        Assertions.assertNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(bronze.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(NOT_ENOUGH_BALANCE_TO_SUBSCRIBE, user.getId(), user.getBalance().toString(), bronze.getSubscriptionType().toString()), error);
    }

    @Test
    @Transactional
    void testSubscribe_Exception_OnBalanceNotEnoughForSilverSubscription() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        Subscription silver = subscriptionRepository.findById(2L).orElse(null);
        user.setSubscription(null);
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal priceTillEndOfMonth = calculateAmountTillEndOfMount(daysTillEndOfMonth, silver.getPrice());
        user.setBalance(priceTillEndOfMonth.subtract(BigDecimal.valueOf(1)));
        userRepository.saveAndFlush(user);
        Assertions.assertNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(silver.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(NOT_ENOUGH_BALANCE_TO_SUBSCRIBE, user.getId(), user.getBalance().toString(), silver.getSubscriptionType().toString()), error);
    }

    @Test
    @Transactional
    void testSubscribe_Exception_OnBalanceNotEnoughForGoldenSubscription() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        Subscription golden = subscriptionRepository.findById(3L).orElse(null);
        user.setSubscription(null);
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal priceTillEndOfMonth = calculateAmountTillEndOfMount(daysTillEndOfMonth, golden.getPrice());
        user.setBalance(priceTillEndOfMonth.subtract(BigDecimal.valueOf(1)));
        userRepository.saveAndFlush(user);
        Assertions.assertNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(golden.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(NOT_ENOUGH_BALANCE_TO_SUBSCRIBE, user.getId(), user.getBalance().toString(), golden.getSubscriptionType().toString()), error);
    }

    @Test
    @Transactional
    void testSubscribe_Exception_OnBalanceNotEnoughForUpgradeFromBronzeToSilver() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        Subscription silver = subscriptionRepository.findById(2L).orElse(null);
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        user.setSubscription(bronze);
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal priceTillEndOfMonthToPay = calculateAmountTillEndOfMount(daysTillEndOfMonth, silver.getPrice());
        BigDecimal priceTillEndOfMonthToReturn = calculateAmountTillEndOfMount(daysTillEndOfMonth, bronze.getPrice());
        user.setBalance(priceTillEndOfMonthToPay.subtract(BigDecimal.valueOf(1)).subtract(priceTillEndOfMonthToReturn));
        userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(silver.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(NOT_ENOUGH_BALANCE_TO_SUBSCRIBE, user.getId(), user.getBalance().toString(), silver.getSubscriptionType().toString()), error);
    }

    @Test
    @Transactional
    void testSubscribe_Exception_OnBalanceNotEnoughForUpgradeFromBronzeToGolden() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        Subscription golden = subscriptionRepository.findById(3L).orElse(null);
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        user.setSubscription(bronze);
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal priceTillEndOfMonthToPay = calculateAmountTillEndOfMount(daysTillEndOfMonth, golden.getPrice());
        BigDecimal priceTillEndOfMonthToReturn = calculateAmountTillEndOfMount(daysTillEndOfMonth, bronze.getPrice());
        user.setBalance(priceTillEndOfMonthToPay.subtract(BigDecimal.valueOf(1)).subtract(priceTillEndOfMonthToReturn));
        userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(golden.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(NOT_ENOUGH_BALANCE_TO_SUBSCRIBE, user.getId(), user.getBalance().toString(), golden.getSubscriptionType().toString()), error);
    }

    @Test
    @Transactional
    void testSubscribe_Exception_OnBalanceNotEnoughForUpgradeFromSilverToGolden() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        Subscription golden = subscriptionRepository.findById(3L).orElse(null);
        Subscription silver = subscriptionRepository.findById(2L).orElse(null);
        user.setSubscription(silver);
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal priceTillEndOfMonthToPay = calculateAmountTillEndOfMount(daysTillEndOfMonth, silver.getPrice());
        BigDecimal priceTillEndOfMonthToReturn = calculateAmountTillEndOfMount(daysTillEndOfMonth, silver.getPrice());
        user.setBalance(priceTillEndOfMonthToPay.subtract(BigDecimal.valueOf(1)).subtract(priceTillEndOfMonthToReturn));
        userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(golden.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(NOT_ENOUGH_BALANCE_TO_SUBSCRIBE, user.getId(), user.getBalance().toString(), golden.getSubscriptionType().toString()), error);
    }

    @Test
    @Transactional
    void testSubscribe_Exception_CannotDowngradeOnMoreRentedBooksThanAllowedByDowngradedSubscription_FromSilverToBronze() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        List<Book> books = initFourBooks();
        List<Rent> rents = initRents(user, books);
        rentRepository.saveAllAndFlush(rents);
        rents.forEach(user::addRent);
        userRepository.saveAndFlush(user);
        Subscription silver = subscriptionRepository.findById(2L).orElse(null);
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        user.setSubscription(silver);
        userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(bronze.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(USER_CANNOT_DOWNGRADE_ON_MORE_RENTS_THAN_ALLOWED, user.getId()), error);
    }

    @Test
    @Transactional
    void testSubscribe_Exception_CannotDowngradeOnMoreRentedBooksThanAllowedByDowngradedSubscription_FromGoldenToBronze() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        List<Book> books = initFourBooks();
        List<Rent> rents = initRents(user, books);
        rentRepository.saveAllAndFlush(rents);
        rents.forEach(user::addRent);
        userRepository.saveAndFlush(user);
        Subscription golden = subscriptionRepository.findById(3L).orElse(null);
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        user.setSubscription(golden);
        userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(bronze.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(USER_CANNOT_DOWNGRADE_ON_MORE_RENTS_THAN_ALLOWED, user.getId()), error);
    }

    @Test
    @Transactional
    void testSubscribe_Exception_CannotDowngradeOnMoreRentedBooksThanAllowedByDowngradedSubscription_FromGoldenToSilver() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        List<Book> books = initFiveBooks();
        List<Rent> rents = initRents(user, books);
        rentRepository.saveAllAndFlush(rents);
        rents.forEach(user::addRent);
        userRepository.saveAndFlush(user);
        Subscription golden = subscriptionRepository.findById(3L).orElse(null);
        Subscription silver = subscriptionRepository.findById(1L).orElse(null);
        user.setSubscription(golden);
        userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(silver.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(USER_CANNOT_DOWNGRADE_ON_MORE_RENTS_THAN_ALLOWED, user.getId()), error);
    }

    @Test
    @Transactional
    void subscribeUser_SucceedDowngradeSubscriptionFromSilverToBronze() throws Exception {
        User user = insertUser();
        List<Book> books = new ArrayList<>();
        books.add(insertTestBook());
        books.add(insertSecondTestBook());
        List<Rent> rents = initRents(user, books);
        rentRepository.saveAllAndFlush(rents);
        rents.forEach(user::addRent);
        user.setBalance(BigDecimal.valueOf(100));
        userRepository.saveAndFlush(user);
        Subscription silver = subscriptionRepository.findById(2L).orElse(null);
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        user.setSubscription(silver);
        userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(bronze.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertNotNull(userDTO);
        Assertions.assertNotNull(userDTO.getSubscription());
        Assertions.assertEquals(bronze.getSubscriptionType().toString(), userDTO.getSubscription());
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(BigDecimal.valueOf(100).setScale(2).toString(), userDTO.getBalance());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void subscribeUser_SucceedDowngradeSubscriptionFromGoldenToBronze() throws Exception {
        User user = insertUser();
        List<Book> books = new ArrayList<>();
        books.add(insertTestBook());
        books.add(insertSecondTestBook());
        List<Rent> rents = initRents(user, books);
        rentRepository.saveAllAndFlush(rents);
        rents.forEach(user::addRent);
        user.setBalance(BigDecimal.valueOf(100));
        userRepository.saveAndFlush(user);
        Subscription golden = subscriptionRepository.findById(3L).orElse(null);
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        user.setSubscription(golden);
        userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(bronze.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertNotNull(userDTO);
        Assertions.assertNotNull(userDTO.getSubscription());
        Assertions.assertEquals(bronze.getSubscriptionType().toString(), userDTO.getSubscription());
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(BigDecimal.valueOf(100).setScale(2).toString(), userDTO.getBalance());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void subscribeUser_SucceedDowngradeSubscriptionFromGoldenToSilver() throws Exception {
        User user = insertUser();
        List<Book> books = new ArrayList<>();
        books.add(insertTestBook());
        books.add(insertSecondTestBook());
        List<Rent> rents = initRents(user, books);
        rentRepository.saveAllAndFlush(rents);
        rents.forEach(user::addRent);
        user.setBalance(BigDecimal.valueOf(100));
        userRepository.saveAndFlush(user);
        Subscription golden = subscriptionRepository.findById(3L).orElse(null);
        Subscription silver = subscriptionRepository.findById(2L).orElse(null);
        user.setSubscription(golden);
        userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getSubscription());
        AddSubscriptionRequest addSubscriptionRequest = new AddSubscriptionRequest().setSubscriptionType(silver.getId());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/subscribe/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSubscriptionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertNotNull(userDTO);
        Assertions.assertNotNull(userDTO.getSubscription());
        Assertions.assertEquals(silver.getSubscriptionType().toString(), userDTO.getSubscription());
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(BigDecimal.valueOf(100).setScale(2).toString(), userDTO.getBalance());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void unsubscribeUser_Succeed_AsAdmin() throws Exception {
        User admin = insertAdmin();
        User user = insertUser();
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        user.setSubscription(bronze);
        user.setBalance(BigDecimal.valueOf(100));
        userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getSubscription());
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/unsubscribe/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);
        Assertions.assertNotNull(userDTO);
        Assertions.assertNotNull(userDTO.getSubscription());
        Assertions.assertTrue(user.isHasUnsubscribed());
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(bronze.getSubscriptionType().toString(), userDTO.getSubscription());
        Assertions.assertEquals(BigDecimal.valueOf(100).setScale(2).toString(), userDTO.getBalance());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void unsubscribeUser_Forbidden_OnUserUnsubscribingForAnotherUser() throws Exception {
        User user = insertUser();
        User user2 = insertUser2();
        Assertions.assertNotNull(user.getSubscription());
        this.mockMvc.perform(put("/api/users/unsubscribe/" + user.getId())
                        .with(user(user2.getUsername()).password(user2.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void unsubscribeUser_Exception_OnUserWithThisIdNotFound() throws Exception {
        User admin = insertAdmin();
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/unsubscribe/" + BAD_ID)
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
    void addBalance_Succeed_OnAdminAddingBalanceForUser() throws Exception {
        User user = insertUser();
        User admin = insertAdmin();
        BigDecimal amountToAdd = BigDecimal.valueOf(50);
        AddBalanceRequest addBalanceRequest = new AddBalanceRequest().setBalance(amountToAdd);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/add-balance/" + user.getId())
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBalanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(amountToAdd.setScale(2).toString(), userDTO.getBalance());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void addBalance_Succeed_OnUserAddingBalanceForHimself() throws Exception {
        User user = insertUser();
        BigDecimal amountToAdd = BigDecimal.valueOf(50);
        AddBalanceRequest addBalanceRequest = new AddBalanceRequest().setBalance(amountToAdd);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/add-balance/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBalanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        UserDTO userDTO = objectMapper.readValue(response.getContentAsString(), UserDTO.class);

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(user.getId(), userDTO.getId());
        Assertions.assertEquals(amountToAdd.setScale(2).toString(), userDTO.getBalance());
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(user.getDisplayName(), userDTO.getDisplayName());
        Assertions.assertEquals(user.getDateOfBirth().toString(), userDTO.getDateOfBirth());
        Assertions.assertEquals(user.getFirstName(), userDTO.getFirstName());
        Assertions.assertEquals(user.getLastName(), userDTO.getLastName());
    }

    @Test
    @Transactional
    void addBalance_Forbidden_OnUserAddingBalanceForAnotherUser() throws Exception {
        User user = insertUser();
        User user2 = insertUser2();
        BigDecimal amountToAdd = BigDecimal.valueOf(50);
        AddBalanceRequest addBalanceRequest = new AddBalanceRequest().setBalance(amountToAdd);
        this.mockMvc.perform(put("/api/users/add-balance/" + user.getId())
                        .with(user(user2.getUsername()).password(user2.getPassword()).roles(String.valueOf(Role.USER)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBalanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn().getResponse();
    }

    @Test
    @Transactional
    void addBalance_Exception_OnUserNotFound() throws Exception {
        User admin = insertAdmin();
        BigDecimal amountToAdd = BigDecimal.valueOf(50);
        AddBalanceRequest addBalanceRequest = new AddBalanceRequest().setBalance(amountToAdd);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/add-balance/" + BAD_ID)
                        .with(user(admin.getUsername()).password(admin.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBalanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse();
        String error = response.getContentAsString();

        Assertions.assertEquals(String.format(USER_WITH_ID_NOT_FOUND, BAD_ID), error);
    }

    @Test
    @Transactional
    void addBalance_ValidationException_OnAddingZeroBalance() throws Exception {
        User user = insertUser();
        BigDecimal amountToAdd = BigDecimal.ZERO;
        AddBalanceRequest addBalanceRequest = new AddBalanceRequest().setBalance(amountToAdd);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/add-balance/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBalanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertTrue(errors.contains(AMOUNT_TO_ADD_TO_BALANCE_MUST_BE_MORE_THAN_ZERO));
    }

    @Test
    @Transactional
    void addBalance_ValidationException_OnAddingMoreThan1000ToBalance() throws Exception {
        User user = insertUser();
        BigDecimal amountToAdd = BigDecimal.valueOf(1001);
        AddBalanceRequest addBalanceRequest = new AddBalanceRequest().setBalance(amountToAdd);
        MockHttpServletResponse response = this.mockMvc.perform(put("/api/users/add-balance/" + user.getId())
                        .with(user(user.getUsername()).password(user.getPassword()).roles(String.valueOf(Role.ADMIN)))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBalanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse();
        List<String> errors = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertTrue(errors.contains(AMOUNT_TO_ADD_TO_BALANCE_MUST_BE_LESS_THAN_1000));
    }

    @Test
    @Transactional
    void taxUnsubscribedUsersForRentedBooks_Succeed_OnUserHaving2NotReturnedBooks() {
        User user = insertUser();
        BigDecimal balance = BigDecimal.valueOf(10);
        user.setBalance(balance);
        user.setSubscription(subscriptionRepository.findById(1L).orElse(null));
        Book book1 = insertTestBook();
        Book book2 = insertSecondTestBook();
        Rent notReturnedRent1 = new Rent()
                .setBook(book1)
                .setUser(user)
                .setRentDate(LocalDate.of(2023, 9, 10))
                .setActualReturnDate(null)
                .setExpectedReturnDate(LocalDate.now().minusDays(1));
        Rent notReturnedRent2 = new Rent()
                .setBook(book2)
                .setUser(user)
                .setRentDate(LocalDate.of(2023, 9, 12))
                .setActualReturnDate(null)
                .setExpectedReturnDate(LocalDate.now().minusDays(2));
        rentRepository.saveAllAndFlush(List.of(notReturnedRent1, notReturnedRent2));
        user.addRent(notReturnedRent1);
        user.addRent(notReturnedRent2);
        userRepository.saveAndFlush(user);
        userService.taxUnsubscribedUsersForRentedBooks();
        Assertions.assertEquals(balance.subtract(TAX_PER_BOOK_PER_DAY.multiply(BigDecimal.valueOf(2))).setScale(2), user.getBalance().setScale(2));
    }

    @Test
    @Transactional
    void taxUnsubscribedUsersForRentedBooks_Succeed_OnUserThatHaveReturnedAllOfHisBooks() {
        User user = insertUser();
        BigDecimal balance = BigDecimal.valueOf(10);
        user.setBalance(balance);
        user.setSubscription(subscriptionRepository.findById(1L).orElse(null));
        Book book1 = insertTestBook();
        Book book2 = insertSecondTestBook();
        Rent notReturnedRent1 = new Rent()
                .setBook(book1)
                .setUser(user)
                .setRentDate(LocalDate.of(2023, 9, 10))
                .setActualReturnDate(LocalDate.of(2023, 9, 11))
                .setExpectedReturnDate(LocalDate.now().minusDays(1));
        Rent notReturnedRent2 = new Rent()
                .setBook(book2)
                .setUser(user)
                .setRentDate(LocalDate.of(2023, 9, 12))
                .setActualReturnDate(LocalDate.of(2023, 9, 13))
                .setExpectedReturnDate(LocalDate.now().minusDays(2));
        rentRepository.saveAllAndFlush(List.of(notReturnedRent1, notReturnedRent2));
        user.addRent(notReturnedRent1);
        user.addRent(notReturnedRent2);
        userRepository.saveAndFlush(user);
        userService.taxUnsubscribedUsersForRentedBooks();
        Assertions.assertEquals(balance, user.getBalance());
    }

    @Test
    @Transactional
    void taxUsersOrRemoveSubscriptionAtStartOfMonth_Succeed_UserTaxedForBronzeSubscription() {
        User user = insertUser();
        BigDecimal balance = BigDecimal.valueOf(50);
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        user.setBalance(balance);
        user.setSubscription(bronze);
        userRepository.saveAndFlush(user);
        userService.taxUsersOrRemoveSubscriptionAtStartOfMonth();
        Assertions.assertEquals(balance.subtract(bronze.getPrice()), user.getBalance());
    }

    @Test
    @Transactional
    void taxUsersOrRemoveSubscriptionAtStartOfMonth_Succeed_UserTaxedForSilverSubscription() {
        User user = insertUser();
        BigDecimal balance = BigDecimal.valueOf(50);
        Subscription silver = subscriptionRepository.findById(2L).orElse(null);
        user.setBalance(balance);
        user.setSubscription(silver);
        userRepository.saveAndFlush(user);
        userService.taxUsersOrRemoveSubscriptionAtStartOfMonth();
        Assertions.assertEquals(balance.subtract(silver.getPrice()), user.getBalance());
    }

    @Test
    @Transactional
    void taxUsersOrRemoveSubscriptionAtStartOfMonth_Succeed_UserTaxedForGoldenSubscription() {
        User user = insertUser();
        BigDecimal balance = BigDecimal.valueOf(50);
        Subscription golden = subscriptionRepository.findById(3L).orElse(null);
        user.setBalance(balance);
        user.setSubscription(golden);
        userRepository.saveAndFlush(user);
        userService.taxUsersOrRemoveSubscriptionAtStartOfMonth();
        Assertions.assertEquals(balance.subtract(golden.getPrice()), user.getBalance());
        Assertions.assertEquals(golden,user.getSubscription());
    }

    @Test
    @Transactional
    void taxUsersOrRemoveSubscriptionAtStartOfMonth_Succeed_UserRemovedSubscription() {
        User user = insertUser();
        BigDecimal balance = BigDecimal.valueOf(50);
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        user.setBalance(balance);
        user.setSubscription(bronze);
        user.setHasUnsubscribed(true);
        userRepository.saveAndFlush(user);
        userService.taxUsersOrRemoveSubscriptionAtStartOfMonth();
        Assertions.assertEquals(balance, user.getBalance());
        Assertions.assertNull(user.getSubscription());
        Assertions.assertFalse(user.isHasUnsubscribed());
    }
    @Test
    @Transactional
    void taxUsersOrRemoveSubscriptionAtStartOfMonth_Succeed_RemoveSubscriptionIfBalanceNotEnough() {
        User user = insertUser();
        BigDecimal balance = BigDecimal.valueOf(0);
        Subscription bronze = subscriptionRepository.findById(1L).orElse(null);
        user.setBalance(balance);
        user.setSubscription(bronze);
        user.setHasUnsubscribed(false);
        userRepository.saveAndFlush(user);
        userService.taxUsersOrRemoveSubscriptionAtStartOfMonth();
        Assertions.assertEquals(balance, user.getBalance());
        Assertions.assertNull(user.getSubscription());
        Assertions.assertFalse(user.isHasUnsubscribed());
    }
}