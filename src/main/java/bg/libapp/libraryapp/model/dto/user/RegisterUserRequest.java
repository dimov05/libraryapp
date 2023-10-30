package bg.libapp.libraryapp.model.dto.user;

import bg.libapp.libraryapp.model.validation.FieldMatch;
import bg.libapp.libraryapp.model.validation.UniqueUsername;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords must match")
public class RegisterUserRequest {
    @NotEmpty(message = "Username can not be empty")
    @Length(min = 4, max = 50, message = "Username should be between 4 and 50 symbols")
    @UniqueUsername()
    private String username;
    @NotEmpty(message = "First name can not be empty")
    @Length(min = 2, message = "First name should be at least 2 characters")
    private String firstName;
    @NotEmpty(message = "Last name can not be empty")
    @Length(min = 2, message = "Last name should be at least 2 characters")
    private String lastName;
    @NotEmpty(message = "Display name can not be empty")
    @Length(min = 3, message = "Display name should be at least 3 characters")
    private String displayName;
    @NotEmpty(message = "Password can not be empty")
    @Length(min = 6, max = 100, message = "Password should be at least 6 symbols")
    private String password;
    @NotEmpty(message = "Password can not be empty")
    @Length(min = 6, max = 100, message = "Password should be at least 6 symbols")
    private String confirmPassword;
    @Past(message = "Birthdate should be in the past")
    private LocalDate dateOfBirth;

    public RegisterUserRequest() {
    }

    public RegisterUserRequest(String username, String firstName, String lastName, String displayName, String password, String confirmPassword, LocalDate dateOfBirth) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.dateOfBirth = dateOfBirth;
    }

    public String getUsername() {
        return username;
    }

    public RegisterUserRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public RegisterUserRequest setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public RegisterUserRequest setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public RegisterUserRequest setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegisterUserRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public RegisterUserRequest setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        return this;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public RegisterUserRequest setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}