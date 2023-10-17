package bg.libapp.libraryapp.model.dto.user;

import bg.libapp.libraryapp.model.dto.rent.RentDTO;

import java.time.LocalDate;
import java.util.List;

public class UserExtendedDTO extends UserDTO {
    private List<RentDTO> rents;

    public UserExtendedDTO() {
    }

    public UserExtendedDTO(List<RentDTO> rents) {
        this.rents = rents;
    }

    public List<RentDTO> getRents() {
        return rents;
    }

    public UserExtendedDTO setRents(List<RentDTO> rents) {
        this.rents = rents;
        return this;
    }

    public UserExtendedDTO setId(long id) {
        super.setId(id);
        return this;
    }

    public UserExtendedDTO setUsername(String username) {
        super.setUsername(username);
        return this;
    }

    public UserExtendedDTO setFirstName(String firstName) {
        super.setFirstName(firstName);
        return this;
    }

    public UserExtendedDTO setLastName(String lastName) {
        super.setLastName(lastName);
        return this;
    }

    public UserExtendedDTO setDisplayName(String displayName) {
        super.setDisplayName(displayName);
        return this;
    }

    public UserExtendedDTO setDateOfBirth(LocalDate dateOfBirth) {
        super.setDateOfBirth(dateOfBirth);
        return this;
    }

    public UserExtendedDTO setRole(String role) {
        super.setRole(role);
        return this;
    }

    public UserExtendedDTO setActive(boolean active) {
        super.setActive(active);
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "rents=" + rents +
                '}';
    }
}
