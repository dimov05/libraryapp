package bg.libapp.libraryapp.web;

import bg.libapp.libraryapp.model.dto.user.LoginUserRequest;
import bg.libapp.libraryapp.model.dto.user.RegisterUserRequest;
import bg.libapp.libraryapp.model.dto.user.UserDTO;
import bg.libapp.libraryapp.model.entity.User;
import bg.libapp.libraryapp.model.mappers.UserMapper;
import bg.libapp.libraryapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody @Valid LoginUserRequest loginUserRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginUserRequest.getUsername(), loginUserRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println(authentication.getName());
        System.out.println(authentication.getAuthorities().toString());
        return new ResponseEntity<>("User logged in successfully!", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterUserRequest registerUserRequest) {
        return new ResponseEntity<>(userService.save(registerUserRequest), HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        userService.logout();

        return new ResponseEntity<>("Successfully logged out!", HttpStatus.OK);
    }
}
