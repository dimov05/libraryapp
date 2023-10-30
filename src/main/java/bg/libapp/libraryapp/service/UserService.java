package bg.libapp.libraryapp.service;

import bg.libapp.libraryapp.exceptions.user.NotEnoughBalanceToStartSubscriptionException;
import bg.libapp.libraryapp.exceptions.user.UserCannotDowngradeOnHavingMoreRentsThanAllowed;
import bg.libapp.libraryapp.exceptions.user.UserIsAlreadyActivatedException;
import bg.libapp.libraryapp.exceptions.user.UserIsAlreadyDeactivatedException;
import bg.libapp.libraryapp.exceptions.user.UserNotFoundException;
import bg.libapp.libraryapp.exceptions.user.UserWithThisUsernameAlreadyExistsException;
import bg.libapp.libraryapp.model.dto.user.AddBalanceRequest;
import bg.libapp.libraryapp.model.dto.user.AddSubscriptionRequest;
import bg.libapp.libraryapp.model.dto.user.ChangePasswordRequest;
import bg.libapp.libraryapp.model.dto.user.ChangeRoleRequest;
import bg.libapp.libraryapp.model.dto.user.RegisterUserRequest;
import bg.libapp.libraryapp.model.dto.user.UpdateUserRequest;
import bg.libapp.libraryapp.model.dto.user.UserDTO;
import bg.libapp.libraryapp.model.entity.Subscription;
import bg.libapp.libraryapp.model.entity.User;
import bg.libapp.libraryapp.model.enums.SubscriptionType;
import bg.libapp.libraryapp.model.mappers.UserMapper;
import bg.libapp.libraryapp.repository.SubscriptionRepository;
import bg.libapp.libraryapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static bg.libapp.libraryapp.model.constants.ApplicationConstants.*;

@Service
@Transactional
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, SubscriptionRepository subscriptionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO save(RegisterUserRequest registerUserRequest) {
        logger.info(REGISTER_USER_WITH_DATA, registerUserRequest);
        existsByUsername(registerUserRequest.getUsername());
        User userToSave = UserMapper.mapToUser(registerUserRequest);
        userToSave.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
        userRepository.saveAndFlush(userToSave);
        return UserMapper.mapToUserDTO(userToSave);
    }

    public void existsByUsername(String username) {
        logger.info(EXISTS_BY_USERNAME_METHOD_ACCESSED_WITH_USERNAME, username);
        if (userRepository.existsByUsername(username)) {
            logger.error(USER_WITH_USERNAME_NOT_FOUND, username);
            throw new UserWithThisUsernameAlreadyExistsException(username);
        }
    }

    public UserDTO getUserExtendedDTO(long id) {
        logger.info(GET_USER_EXTENDED_DTO_METHOD_ACCESSED_WITH_ID, id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> logAndThrowExceptionForUserNotFound(id));
        return UserMapper.mapToUserExtendedDTO(user);
    }

    public User getUserById(long id) {
        logger.info(GET_USER_BY_ID_METHOD_ACCESSED_WITH_ID, id);
        return userRepository.findById(id)
                .orElseThrow(() -> logAndThrowExceptionForUserNotFound(id));
    }

    public List<UserDTO> getAllUsers() {
        logger.info(GET_ALL_USERS_ACCESSED_LOGGER);
        List<User> users = userRepository.findAll();
        return users
                .stream()
                .map(UserMapper::mapToUserDTO)
                .toList();
    }

    public UserDTO editUserAndSave(UpdateUserRequest updateUserRequest, long id) {
        logger.info(EDIT_USER_METHOD_ACCESSED_WITH_ID, id, updateUserRequest);
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> logAndThrowExceptionForUserNotFound(id));
        editUserWithUpdateUserRequestData(updateUserRequest, oldUser);
        userRepository.saveAndFlush(oldUser);
        return UserMapper.mapToUserDTO(oldUser);
    }

    private UserNotFoundException logAndThrowExceptionForUserNotFound(long id) {
        logger.info(USER_WITH_ID_NOT_FOUND, id);
        return new UserNotFoundException(id);
    }

    private UserNotFoundException logAndThrowExceptionForUserNotFound(String username) {
        logger.info(USER_WITH_USERNAME_NOT_FOUND, username);
        return new UserNotFoundException(username);
    }

    public UserDTO changeRoleAndSave(ChangeRoleRequest changeRoleRequest, long id) {
        logger.info(CHANGE_ROLE_METHOD_ACCESSED_WITH_USER_ID, id);
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> logAndThrowExceptionForUserNotFound(id));
        oldUser.setRole(changeRoleRequest.getRole());
        userRepository.saveAndFlush(oldUser);
        return UserMapper.mapToUserDTO(oldUser);
    }

    public UserDTO addSubscription(AddSubscriptionRequest addSubscriptionRequest, long id) {
        logger.info(SUBSCRIBE_METHOD_ACCESSED_WITH_USER_ID, id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> logAndThrowExceptionForUserNotFound(id));

        Subscription currentSubscription = user.getSubscription();
        Subscription subscriptionToSet = subscriptionRepository.findById(addSubscriptionRequest.getSubscriptionType())
                .orElseThrow();
        int daysTillEndOfMonth = getDaysTillEndOfMonth();
        BigDecimal amountToPayForNewSubscriptionTillEndOfMonth = BigDecimal.ZERO;
        //check if there is current subscription and if user wants to upgrade,
        // if upgrading -> return paid money for current subscription to balance,
        // if downgrading -> throw exception
        if (currentSubscription == null) {
            amountToPayForNewSubscriptionTillEndOfMonth = calculateAmountTillEndOfMount(daysTillEndOfMonth, subscriptionToSet.getPrice());
        } else {
            if (userIsUpgrading(currentSubscription, subscriptionToSet)) {
                amountToPayForNewSubscriptionTillEndOfMonth = calculateExtraPayForUpgrade(daysTillEndOfMonth, currentSubscription, subscriptionToSet);
            } else if (userIsDowngrading(currentSubscription, subscriptionToSet) && userRentedMoreThanAllowedBooksForDowngrading(user, subscriptionToSet)) {
                throw new UserCannotDowngradeOnHavingMoreRentsThanAllowed(user.getId());
            }
        }
        checkIfBalanceIsEnoughForNewSubscription(amountToPayForNewSubscriptionTillEndOfMonth, user, subscriptionToSet.getSubscriptionType());
        user.setBalance(user.getBalance().subtract(amountToPayForNewSubscriptionTillEndOfMonth));
        user.setSubscription(subscriptionToSet);
        user.setHasUnsubscribed(false);
        userRepository.saveAndFlush(user);
        return UserMapper.mapToUserDTO(user);
    }

    private BigDecimal calculateExtraPayForUpgrade(int daysTillEndOfMonth, Subscription currentSubscription, Subscription subscriptionToSet) {
        return BigDecimal.valueOf(daysTillEndOfMonth)
                .divide(BigDecimal.valueOf(DAYS_IN_MONTH), 3, RoundingMode.CEILING)
                .multiply(subscriptionToSet.getPrice().subtract(currentSubscription.getPrice()));
    }

    private static boolean userRentedMoreThanAllowedBooksForDowngrading(User user, Subscription subscriptionToSet) {
        return user.getRents().stream().filter(r -> r.getActualReturnDate() == null).count() > subscriptionToSet.getRentsAllowed();
    }

    private static void checkIfBalanceIsEnoughForNewSubscription(BigDecimal amountToPayForNewSubscriptionTillEndOfMonth, User user, SubscriptionType subscriptionType) {
        if (amountToPayForNewSubscriptionTillEndOfMonth.compareTo(user.getBalance()) > 0) {
            throw new NotEnoughBalanceToStartSubscriptionException(user.getId(), user.getBalance(), subscriptionType);
        }
    }

    private static boolean userIsUpgrading(Subscription currentSubscription, Subscription subscriptionToSet) {
        return currentSubscription.getPrice().compareTo(subscriptionToSet.getPrice()) < 0;
    }

    private static boolean userIsDowngrading(Subscription currentSubscription, Subscription subscriptionToSet) {
        return currentSubscription.getPrice().compareTo(subscriptionToSet.getPrice()) > 0;
    }

    private BigDecimal calculateAmountTillEndOfMount(int daysTillEndOfMonth, BigDecimal price) {
        return BigDecimal.valueOf(daysTillEndOfMonth)
                .divide(BigDecimal.valueOf(DAYS_IN_MONTH), 3, RoundingMode.CEILING)
                .multiply(price);
    }

    private int getDaysTillEndOfMonth() {
        LocalDate currentDate = LocalDate.now();
        LocalDate lastDayOfMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth());
        return currentDate.until(lastDayOfMonth).getDays();
    }

    public UserDTO changePasswordAndSave(ChangePasswordRequest changePasswordRequest, long id) {
        logger.info(CHANGE_PASSWORD_METHOD_ACCESSED_WITH_USER_ID, id);
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> logAndThrowExceptionForUserNotFound(id));
        oldUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.saveAndFlush(oldUser);
        return UserMapper.mapToUserDTO(oldUser);
    }

    public String getUsernameById(long id) {
        logger.info(GET_USERNAME_BY_ID_METHOD_ACCESSED_WITH_ID, id);
        return userRepository.findById(id)
                .orElseThrow(() -> logAndThrowExceptionForUserNotFound(id))
                .getUsername();
    }

    private static void editUserWithUpdateUserRequestData(UpdateUserRequest updateUserRequest, User oldUser) {
        oldUser
                .setFirstName(updateUserRequest.getFirstName())
                .setLastName(updateUserRequest.getLastName())
                .setDisplayName(updateUserRequest.getDisplayName());
    }

    public UserDTO deleteUserById(long id) {
        logger.info(DELETE_USER_BY_ID_METHOD_ACCESSED_WITH_ID, id);
        User toDelete = userRepository.findById(id)
                .orElseThrow(() -> logAndThrowExceptionForUserNotFound(id));
        userRepository.deleteById(id);
        return UserMapper.mapToUserDTO(toDelete);
    }

    public void logout() {
        logger.info(LOGOUT_CURRENT_LOGGED_IN_USER);
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        SecurityContextHolder.clearContext();
    }

    public UserDTO deactivateUser(long id) {
        logger.info(DEACTIVATE_USER_WITH_ID, id);
        User userToEdit = userRepository.findById(id)
                .orElseThrow(() -> logAndThrowExceptionForUserNotFound(id));
        if (!userToEdit.isActive()) {
            logger.error(USER_MUST_BE_ACTIVE_IN_ORDER_TO_BE_DEACTIVATED);
            throw new UserIsAlreadyDeactivatedException(id);
        }
        userToEdit.setActive(false);
        userRepository.saveAndFlush(userToEdit);
        return UserMapper.mapToUserDTO(userToEdit);
    }

    public UserDTO activateUser(long id) {
        logger.info(ACTIVATE_USER_WITH_ID, id);
        User userToEdit = userRepository.findById(id)
                .orElseThrow(() -> logAndThrowExceptionForUserNotFound(id));
        if (userToEdit.isActive()) {
            logger.error(USER_MUST_BE_INACTIVE_IN_ORDER_TO_BE_ACTIVATED);
            throw new UserIsAlreadyActivatedException(id);
        }
        userToEdit.setActive(true);
        userRepository.saveAndFlush(userToEdit);
        return UserMapper.mapToUserDTO(userToEdit);
    }

    public User getUserByUsername(String username) {
        logger.info(GET_USER_BY_USERNAME_ACCESSED_FOR_USER_WITH_USERNAME, username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> logAndThrowExceptionForUserNotFound(username));
    }

    public UserDTO addBalanceToUser(AddBalanceRequest addBalanceRequest, long id) {
        logger.info(ADD_BALANCE_ACCESSED_FOR_USER_WITH_ID, id);
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> logAndThrowExceptionForUserNotFound(id));
        oldUser.setBalance(addBalanceRequest.getBalance());
        userRepository.saveAndFlush(oldUser);
        return UserMapper.mapToUserDTO(oldUser);
    }

    public UserDTO unsubscribe(long id) {
        logger.info(UNSUBSCRIBE_METHOD_ACCESSED_FOR_USER_WITH_ID, id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> logAndThrowExceptionForUserNotFound(id));
        user.setHasUnsubscribed(true);
        userRepository.saveAndFlush(user);
        return UserMapper.mapToUserDTO(user);
    }

    public void taxUsersOrRemoveSubscriptionAtStartOfMonth() {
        logger.info(TAX_USERS_OR_REMOVE_SUBSCRIPTION_CRON_JOB);
        List<User> users = userRepository.findAllBySubscriptionNotNull();
        for (User user : users) {
            if (user.isHasUnsubscribed()) {
                user.setSubscription(null);
                user.setHasUnsubscribed(false);
            } else {
                BigDecimal currentSubscriptionPrice = user.getSubscription().getPrice();
                userRepository.saveAndFlush(user.getBalance().compareTo(currentSubscriptionPrice) >= 0
                        ? user.setBalance(user.getBalance().subtract(currentSubscriptionPrice))
                        : user.setSubscription(null));
            }
        }
    }

    public void taxUsersForProlongedRents() {
        logger.info(TAX_USERS_FOR_PROLONGED_RENTS_CRON_JOB);
        List<User> users = userRepository.findAllByRentActualReturnDateNullAndExpectedReturnDateBeforeNow();
        BigDecimal amountToTax;
        for (User user : users) {
            long countOfNotReturnedBooks = user.getRents().stream()
                    .filter(r -> r.getActualReturnDate() == null && r.getExpectedReturnDate().isBefore(LocalDate.now())).count();
            amountToTax = TAX_PER_BOOK_PER_DAY.multiply(BigDecimal.valueOf(countOfNotReturnedBooks));
            user.setBalance(user.getBalance().subtract(amountToTax));
            userRepository.saveAndFlush(user);
        }
    }
}
