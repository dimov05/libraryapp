package bg.libapp.libraryapp.model.constants;

import java.math.BigDecimal;

public class ApplicationConstants {
    public static final String PUBLISHER = "publisher";
    public static final String YEAR = "year";
    public static final String TOTAL_QUANTITY = "totalQuantity";
    public static final String AVAILABLE_QUANTITY = "availableQuantity";
    public static final String TITLE = "title";
    public static final String AUTHORS = "authors";
    public static final int DAYS_IN_MONTH = 30;
    public static final BigDecimal TAX_PER_BOOK_PER_DAY = BigDecimal.valueOf(0.25);
    public static final String GENRES = "genres";
    public static final String IS_ACTIVE = "isActive";
    public static final String FIRST_NAME = "firstName";
    public static final String USER_WITH_USERNAME_NOT_FOUND = "User with this username {} was not found!";
    public static final String USER_WITH_USERNAME_NOT_FOUND_STRING_FORMAT = "User with this username %s was not found!";
    public static final String USER_WITH_ID_NOT_FOUND = "User with this id {} was not found!";
    public static final String BOOK_WITH_ISBN_NOT_FOUND = "Book with this isbn '{}' was not found!";
    public static final String LAST_NAME = "lastName";
    public static final String ID = "id";
    public static final String DEACTIVATE_REASON = "deactivateReason";
    public static final String USER_LOGGED_IN_SUCCESSFULLY = "User logged in successfully!";
    public static final String USER_LOGGED_OUT_SUCCESSFULLY = "Successfully logged out!";
    public static final String GET_JSON_FORMAT_OF_BOOK_ENTITY = "Get json format of book entity.";
    public static final String CAN_NOT_GET_JSON_FORMAT_OF_BOOK_ENTITY = "Can not get json format of book entity!";
    public static final String FIND_BOOK_WITH_ISBN = "Find book with isbn '{}'";
    public static final String REGISTER_USER_WITH_DATA = "Register user with this data: '{}'";
    public static final String CREATING_EVENT_FOR_UPDATE_YEAR_OF_BOOK_LOGGER = "Creating an event for updating year of book with params: {}";
    public static final String CREATING_EVENT_FOR_UPDATE_PUBLISHER_OF_BOOK_LOGGER = "Creating an event for updating publisher of book with params: {}";
    public static final String CREATING_EVENT_FOR_UPDATE_STATUS_OF_BOOK_LOGGER = "Creating an event for updating status of book with params: {}";
    public static final String CREATING_EVENT_FOR_UPDATE_DEACTIVATION_REASON_OF_BOOK_LOGGER = "Creating an event for updating deactivation reason of book with params: {}";
    public static final String CREATING_EVENT_FOR_SAVING_NEW_BOOK_LOGGER = "Creating an event for saving a new book with params: {}";
    public static final String CREATING_EVENT_FOR_UPDATE_QUANTITY_OF_BOOK_LOGGER = "Creating an event for updating '{}' quantity of book with params: {}";
    public static final String UPDATE_TOTAL_QUANTITY_OF_BOOK_WITH_ISBN = "Updated total quantity of book with isbn '{}' and params: {}";
    public static final String THERE_IS_NO_SUCH_DEACTIVATE_REASON = "There is no such a deactivate reason '{}'";
    public static final String DEACTIVATED_BOOK_WITH_ISBN = "Deactivated book with isbn '{}' and params: {}";
    public static final String DELETE_BOOK_WITH_ISBN = "Delete book with isbn '{}'";
    public static final String ACTIVATED_BOOK_WITH_ISBN = "Activated book with isbn '{}' and params: {}";
    public static final String UPDATED_PUBLISHER_OF_BOOK_WITH_ISBN = "Updated publisher of book with isbn '{}' and params: {}";
    public static final String UPDATED_YEAR_OF_BOOK_WITH_ISBN = "Updated year of book with isbn '{}' and params: {}";
    public static final String BOOK_WITH_ISBN_IS_ACTIVE_AND_CAN_NOT_BE_DELETED = "Book with isbn '{}' is active and can not be deleted!";
    public static final String BOOK_WITH_ISBN_IS_NOT_ACTIVE = "Book with isbn '{}' is not active!";
    public static final String BOOK_WITH_ISBN_IS_ALREADY_ADDED = "Book with this isbn is already added";
    public static final String SAVE_NEW_BOOK_WITH_ISBN = "Save a new book with isbn '{}' and params: {}";
    public static final String NO_GENRES_WITH_THIS_NAME = "There are no genres with this name '{}'";
    public static final String CREATED_NEW_RENT_WITH_PARAMS = "Created new rent with params: {}";
    public static final String INSUFFICIENT_AVAILABLE_QUANTITY_FOR_BOOK_WITH_ISBN = "Insufficient available quantity for book with isbn '{}', because available quantity is ={}";
    public static final String USER_ELIGIBLE_TO_RENT_METHOD_CALLED_WITH_PARAMS_LOGGER = "userEligibleToRent method accessed with params: {}\n by authenticated user with username '{}'.";
    public static final String USER_NOT_ELIGIBLE_TO_RENT = "User is not eligible to rent - not ADMIN/MODERATOR or authenticated user != user for rent request";
    public static final String CAN_NOT_RENT_BOOK_WITH_ISBN_FOR_USER_TWICE = "Can not rent book twice for book with isbn '{}' and user with id '{}'.";
    public static final String USER_WITH_ID_HAS_PROLONGED_RENT_WITH_ID = "User with id '{}' has prolonged rent with id '{}'.";
    public static final String USER_WITH_ID_HAS_ALREADY_RENTED_3_BOOKS = "User with id '{}' has rented already 3 books!";
    public static final String RETURN_BOOK_WITH_ISBN_METHOD_CALLED_FOR_RENT_WITH_ID = "returnBook method called for rent with id '{}' and book isbn '{}'.";
    public static final String RENT_WITH_ID_IS_ALREADY_RETURNED = "Rent with id '{}' is already returned!";
    public static final String FIND_RENT_WITH_ID = "Find rent with id '{}'";
    public static final String FIND_RENT_WITH_ID_WAS_NOT_FOUND = "Rent with this id '{}' was not found!";
    public static final String LOGGING_USER_WITH_USERNAME = "Logging user with this username '{}'";
    public static final String TAX_USERS_FOR_PROLONGED_RENTS_CRON_JOB = "taxUnsubscribedUsersForRentedBooks CRON JOB";
    public static final String TAX_USERS_OR_REMOVE_SUBSCRIPTION_CRON_JOB = "taxUsersOrRemoveSubscriptionAtStartOfMonth CRON JOB";
    public static final String USER_MUST_BE_INACTIVE_IN_ORDER_TO_BE_ACTIVATED = "User account must be inactive in order to be activated";
    public static final String USER_MUST_BE_ACTIVE_IN_ORDER_TO_BE_DEACTIVATED = "User account must be active in order to be deactivated";
    public static final String ACTIVATE_USER_WITH_ID = "Activate user with id '{}'";
    public static final String DEACTIVATE_USER_WITH_ID = "Deactivate user with id '{}'";
    public static final String LOGOUT_CURRENT_LOGGED_IN_USER = "Logout current logged in user";
    public static final String GET_ALL_RENTS_ACCESSED_LOGGER = "getAllRents method accessed";
    public static final String DELETE_USER_BY_ID_METHOD_ACCESSED_WITH_ID = "deleteUserById method accessed with id '{}'";
    public static final String RENT_BOOK_METHOD_CALLED_WITH_PARAMS_LOGGER = "rentBook method accessed with borrower username '{}' for book with isbn '{}' with requestParams: {}";
    public static final String CHANGE_STATUS_OF_BOOK_METHOD_LOGGER = "changeStatus of Book method accessed with params: {}";
    public static final String HAS_ENOUGH_AVAILABLE_QUANTITY_METHOD_CALLED_FOR_BOOK_WITH_ISBN = "hasEnoughAvailableQuantity method accessed for book with isbn '{}'.";
    public static final String EDIT_USER_METHOD_ACCESSED_WITH_ID = "editUserAndSave method accessed with user with id '{}' and params: {}";
    public static final String GET_USER_EXTENDED_DTO_METHOD_ACCESSED_WITH_ID = "getUserExtendedDTOById method accessed with id: '{}'";
    public static final String EXISTS_BY_USERNAME_METHOD_ACCESSED_WITH_USERNAME = "existsByUsername method accessed with username: '{}'";
    public static final String GET_ALL_AUTHORS_ACCESSED_LOGGER = "getAllAuthors method accessed";
    public static final String GET_ALL_GENRES_ACCESSED_LOGGER = "getAllGenres method accessed";
    public static final String GET_ALL_USERS_ACCESSED_LOGGER = "getAllUsers method accessed";
    public static final String GET_USER_BY_ID_METHOD_ACCESSED_WITH_ID = "getUserById method accessed with id: '{}'";
    public static final String GET_ALL_BOOKS_ACCESSED_LOGGER = "getAllBooks method accessed with params: {}";
    public static final String GET_USERNAME_BY_ID_METHOD_ACCESSED_WITH_ID = "getUsernameById method accessed with id '{}'";
    public static final String CHANGE_PASSWORD_METHOD_ACCESSED_WITH_USER_ID = "changePasswordAndSave method accessed with user with id '{}'";
    public static final String SUBSCRIBE_METHOD_ACCESSED_WITH_USER_ID = "subscribe method accessed with user with id '{}'";
    public static final String CHANGE_ROLE_METHOD_ACCESSED_WITH_USER_ID = "changeRoleAndSave method accessed with user with id '{}'";
    public static final String FIND_OR_CREATE_AUTHOR_ACCESSED_LOGGER = "findOrCreate Author method accessed by AuthorService with params: {}";
    public static final String UNSUBSCRIBE_METHOD_ACCESSED_FOR_USER_WITH_ID = "unsubscribe method accessed with user with id '{}'";
    public static final String ADD_BALANCE_ACCESSED_FOR_USER_WITH_ID = "addBalanceToUser method accessed with user with id '{}'";
    public static final String GET_USER_BY_USERNAME_ACCESSED_FOR_USER_WITH_USERNAME = "getUserByUsername method accessed with username '{}'";
    public static final String MAP_TO_AUTHOR_DTO_ACCESSED = "mapToAuthorDTO mapper method called with params {}";
    public static final String MAP_TO_AUTHOR_EXTENDED_DTO_ACCESSED = "mapToAuthorExtendedDTO mapper method called with params {}";
    public static final String MAP_TO_USER_ACCESSED = "mapToUser mapper method called with params {}";
    public static final String MAP_TO_USER_DTO_ACCESSED = "mapToUserDTO mapper method called with params {}";
    public static final String MAP_TO_USER_EXTENDED_DTO_ACCESSED = "mapToUserExtendedDTO mapper method called with params {}";
    public static final String MAP_TO_RENT_DTO_ACCESSED = "mapToRentDTO mapper method called with params {}";
    public static final String MAP_TO_GENRE_DTO_ACCESSED = "mapToGenreDTO mapper method called with params {}";
    public static final String MAP_TO_BOOK_AUDIT_ACCESSED = "mapToBookAudit mapper method called with params {}";
    public static final String MAP_TO_BOOK_ACCESSED = "mapToBook mapper method called with params {}";
    public static final String MAP_TO_BOOK_DTO_ACCESSED = "mapToBookDTO mapper method called with params {}";
    public static final String MAP_TO_BOOK_EXTENDED_DTO_ACCESSED = "mapToBookExtendedDTO mapper method called with params {}";
    public static final String UPDATE_TOTAL_QUANTITY_METHOD_ACCESSED = "updateTotalQuantity method called with params: isbn={}, {}";
}

