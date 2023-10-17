package bg.libapp.libraryapp.model.mappers;

import bg.libapp.libraryapp.model.dto.user.RegisterUserRequest;
import bg.libapp.libraryapp.model.dto.user.UserDTO;
import bg.libapp.libraryapp.model.dto.user.UserExtendedDTO;
import bg.libapp.libraryapp.model.entity.User;
import bg.libapp.libraryapp.model.enums.Role;
import bg.libapp.libraryapp.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserMapper {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public static User mapToUser(RegisterUserRequest registerUserRequest) {
        logger.info("mapToUser mapper method called with params " + registerUserRequest);
        return new User()
                .setUsername(registerUserRequest.getUsername())
                .setFirstName(registerUserRequest.getFirstName())
                .setLastName(registerUserRequest.getLastName())
                .setDisplayName(registerUserRequest.getDisplayName())
                .setPassword(registerUserRequest.getPassword()) // encoding in service
                .setDateOfBirth(registerUserRequest.getDateOfBirth())
                .setActive(true)
                .setRole(Role.USER.ordinal());
    }

    public static UserDTO mapToUserDTO(User user) {
        logger.info("mapToUserDTO mapper method called with params " + user);
        return new UserDTO()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setDisplayName(user.getDisplayName())
                .setDateOfBirth(user.getDateOfBirth())
                .setRole(Role.values()[user.getRole()].name())
                .setActive(user.isActive());
    }

    public static UserExtendedDTO mapToUserExtendedDTO(User user) {
        logger.info("mapToUserExtendedDTO mapper method called with params " + user);
        return new UserExtendedDTO()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setDisplayName(user.getDisplayName())
                .setDateOfBirth(user.getDateOfBirth())
                .setRole(Role.values()[user.getRole()].name())
                .setActive(user.isActive())
                .setRents(user.getRents()
                        .stream().map(RentMapper::mapToRentDTO).toList());
    }
}