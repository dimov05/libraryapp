package bg.libapp.libraryapp.web;

import bg.libapp.libraryapp.model.dto.user.AddBalanceRequest;
import bg.libapp.libraryapp.model.dto.user.AddSubscriptionRequest;
import bg.libapp.libraryapp.model.dto.user.ChangePasswordRequest;
import bg.libapp.libraryapp.model.dto.user.ChangeRoleRequest;
import bg.libapp.libraryapp.model.dto.user.UpdateUserRequest;
import bg.libapp.libraryapp.model.dto.user.UserDTO;
import bg.libapp.libraryapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MODERATOR') or authentication.name == @userService.getUsernameById(#id)")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.getUserExtendedDTO(id), HttpStatus.OK);
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MODERATOR')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }


    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MODERATOR') or @userService.getUsernameById(#id) == authentication.name")
    public ResponseEntity<UserDTO> editUser(@Valid @RequestBody UpdateUserRequest updateUserRequest, @PathVariable("id") long id) {
        return new ResponseEntity<>(userService.editUserAndSave(updateUserRequest, id), HttpStatus.OK);
    }

    @PutMapping("/change-role/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> changeRole(@Valid @RequestBody ChangeRoleRequest changeRoleRequest, @PathVariable("id") long id) {
        return new ResponseEntity<>(userService.changeRoleAndSave(changeRoleRequest, id), HttpStatus.OK);
    }

    @PutMapping("/subscribe/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')or @userService.getUsernameById(#id) == authentication.name")
    public ResponseEntity<UserDTO> subscribe(@Valid @RequestBody AddSubscriptionRequest addSubscriptionRequest, @PathVariable("id") long id) {
        return new ResponseEntity<>(userService.addSubscription(addSubscriptionRequest, id), HttpStatus.OK);
    }

    @PutMapping("/unsubscribe/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')or @userService.getUsernameById(#id) == authentication.name")
    public ResponseEntity<UserDTO> unsubscribe(@PathVariable("id") long id) {
        return new ResponseEntity<>(userService.unsubscribe(id), HttpStatus.OK);
    }

    @PutMapping("/add-balance/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')or @userService.getUsernameById(#id) == authentication.name")
    public ResponseEntity<UserDTO> addBalance(@Valid @RequestBody AddBalanceRequest addBalanceRequest, @PathVariable("id") long id) {
        return new ResponseEntity<>(userService.addBalanceToUser(addBalanceRequest, id), HttpStatus.OK);
    }

    @PutMapping("/change-password/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userService.getUsernameById(#id) == authentication.name")
    public ResponseEntity<UserDTO> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest, @PathVariable("id") long id) {
        return new ResponseEntity<>(userService.changePasswordAndSave(changePasswordRequest, id), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MODERATOR')")
    public ResponseEntity<UserDTO> deleteUserById(@PathVariable("id") long id) {
        return new ResponseEntity<>(userService.deleteUserById(id), HttpStatus.OK);
    }

    @PutMapping("/deactivate/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userService.getUsernameById(#id) == authentication.name")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable("id") long id) {
        return new ResponseEntity<>(userService.deactivateUser(id), HttpStatus.OK);
    }

    @PutMapping("/activate/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userService.getUsernameById(#id) == authentication.name")
    public ResponseEntity<UserDTO> activateUser(@PathVariable("id") long id) {
        return new ResponseEntity<>(userService.activateUser(id), HttpStatus.OK);
    }
}
