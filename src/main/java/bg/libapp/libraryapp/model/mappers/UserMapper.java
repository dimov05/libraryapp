package bg.libapp.libraryapp.model.mappers;

import bg.libapp.libraryapp.model.dto.user.RegisterUserRequest;
import bg.libapp.libraryapp.model.dto.user.UserDTO;
import bg.libapp.libraryapp.model.dto.user.UserExtendedDTO;
import bg.libapp.libraryapp.model.entity.User;
import bg.libapp.libraryapp.model.enums.Role;
import bg.libapp.libraryapp.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static bg.libapp.libraryapp.model.constants.ApplicationConstants.MAP_TO_USER_ACCESSED;
import static bg.libapp.libraryapp.model.constants.ApplicationConstants.MAP_TO_USER_DTO_ACCESSED;
import static bg.libapp.libraryapp.model.constants.ApplicationConstants.MAP_TO_USER_EXTENDED_DTO_ACCESSED;

public class UserMapper {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public static User mapToUser(RegisterUserRequest registerUserRequest) {
        logger.info(MAP_TO_USER_ACCESSED, registerUserRequest);
        return new User()
                .setUsername(registerUserRequest.getUsername())
                .setFirstName(registerUserRequest.getFirstName())
                .setLastName(registerUserRequest.getLastName())
                .setDisplayName(registerUserRequest.getDisplayName())
                .setPassword(registerUserRequest.getPassword()) // encoding in service
                .setDateOfBirth(registerUserRequest.getDateOfBirth())
                .setActive(true)
                .setBalance(BigDecimal.ZERO)
                .setRole(Role.USER.ordinal());
    }

    public static UserDTO mapToUserDTO(User user) {
        logger.info(MAP_TO_USER_DTO_ACCESSED, user);
        return new UserDTO()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setDisplayName(user.getDisplayName())
                .setDateOfBirth(user.getDateOfBirth().toString())
                .setBalance(user.getBalance().setScale(2, RoundingMode.DOWN).toString())
                .setRole(Role.values()[user.getRole()].name())
                .setSubscription(user.getSubscription() != null ? user.getSubscription().getSubscriptionType().toString() : null)
                .setActive(user.isActive());
    }

    public static UserExtendedDTO mapToUserExtendedDTO(User user) {
        logger.info(MAP_TO_USER_EXTENDED_DTO_ACCESSED, user);
        return new UserExtendedDTO()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setDisplayName(user.getDisplayName())
                .setDateOfBirth(user.getDateOfBirth().toString())
                .setRole(Role.values()[user.getRole()].name())
                .setActive(user.isActive())
                .setBalance(user.getBalance().setScale(2, RoundingMode.DOWN).toString())
                .setSubscription(user.getSubscription() != null ? user.getSubscription().getSubscriptionType().toString() : null)
                .setRents(user.getRents()
                        .stream().map(RentMapper::mapToRentDTO).toList());
    }
}