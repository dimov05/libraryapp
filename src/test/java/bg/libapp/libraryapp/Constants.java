package bg.libapp.libraryapp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Constants {
    public static final int YEAR = 2022;
    public static final long BRONZE_ID = 1L;
    public static final long SILVER_ID = 2L;
    public static final long GOLDEN_ID = 3L;

    public static final LocalDateTime DATETIME_BOOK = LocalDateTime.of(2023, 10, 5, 12, 30, 20);

    public static final String PUBLISHER_SIZE_EXCEPTION = "Publisher name should be between 1 and 100 symbols";
    public static final String PUBLISHER_NOT_BLANK_EXCEPTION = "Publisher should not be blank";
    public static final String AMOUNT_TO_ADD_TO_BALANCE_MUST_BE_MORE_THAN_ZERO = "Amount to add to balance must be more than 0.00";
    public static final String AMOUNT_TO_ADD_TO_BALANCE_MUST_BE_LESS_THAN_1000 = "Amount should be less than 1000";
    public static final String YEAR_RANGE_EXCEPTION = "Year should be between 1000 and now";
    public static final String RANDOM_NAME = "RandomName";
    public static final BigDecimal TAX_PER_BOOK_PER_DAY = BigDecimal.valueOf(0.25);

    public static final String BAD_DEACTIVATE_REASON = "BADREASON";
    public static final String INVALID_ISBN_EXCEPTION = "Invalid isbn";
    public static final String INVALID_ISBN = "9asdad23123123dasda-6";
    public static final String VALID_ISBN = "978-2-9006-3774-6";
    public static final String FIRST_NAME_EMPTY_EXCEPTION = "First name can not be empty";
    public static final String LAST_NAME_EMPTY_EXCEPTION = "Last name can not be empty";
    public static final String DISPLAY_NAME_EMPTY_EXCEPTION = "Display name can not be empty";
    public static final String FIRST_NAME_TOO_SHORT = "First name should be at least 2 characters";
    public static final String LAST_NAME_TOO_SHORT = "Last name should be at least 2 characters";
    public static final String DISPLAY_NAME_TOO_SHORT = "Display name should be at least 3 characters";
    public static final String NOT_ENOUGH_BALANCE_TO_SUBSCRIBE = "User with this id :'%d' and his balance: '%s' is not enough to start subscription of type: '%s'!";
    public static final String USER_ALREADY_WITH_THIS_SUBSCRIPTION = "User with this id :'%d' is already with this subscription '%s'!";
    public static final String USER_CANNOT_DOWNGRADE_ON_MORE_RENTS_THAN_ALLOWED = "User with this id :'%d' can not downgrade because he has more rents than allowed for new subscription";
    public static final String INVALID_ROLE_ORDINAL = "Role must be between 0 and 2 (0->USER, 1->MODERATOR,2->ADMIN)";
    public static final long BAD_ID = 651257412;
    public static final String USER_WITH_ID_NOT_FOUND = "User with this id :'%s' is not found!";
    public static final String USER_WITH_ID_DOES_NOT_HAVE_SUBSCRIPTION = "User with this id :'%d' does not have subscription!";
    public static final String PASSWORD_MUST_NOT_BE_EMPTY = "Password can not be empty";
    public static final String PASSWORD_SHOULD_BE_AT_LEAST_6_SYMBOLS = "Password should be at least 6 symbols";
    public static final String PASSWORDS_MUST_MATCH = "Passwords must match";
    public static final String USER_NOT_ELIGIBLE_TO_RENT = "User with this username :'%s' is not eligible to rent a book for this user id '%s'!";
    public static final String BOOK_ALREADY_ADDED = "Book with this isbn: '%s' is already added!";
    public static final String BOOK_NOT_PRESENT = "Book with this isbn: '%s' is not present in library!";
    public static final String BOOK_NOT_ACTIVE_EXCEPTION = "Book with this isbn: '%s' is not with active status in library!";
    public static final String DEACTIVATE_REASON_IS_INVALID = "This reason '%s' is not a valid reason to deactivate a book!";
    public static final String BOOK_QUANTITY_LESS_THAN_0 = "Book's total quantity should be equal or more than 0";
    public static final String INSUFFICIENT_AVAILABLE_QUANTITY = "Book with isbn '%s' + has insufficient available quantity '%d' to be rented.";
    public static final String GENRE_MISSING_EXCEPTION = "Genre with this name: 'RandomName' is not present in the library!";
    public static final String ISBN_NOT_BLANK = "isbn should not be blank";
    public static final String ISBN_INCORRECT_VALUE = "ISBN is invalid";
    public static final String CANNOT_RENT_BOOK_TWICE = "Cannot rent book with this isbn: '%s' twice!";
    public static final String USER_HAS_PROLONGED_RENT = "User with this id: '%d' has prolonged rent with id '%d'!";
    public static final String USER_RENTED_MAXIMUM_ALLOWED_BOOKS = "User with this id :'%d' has rented maximum allowed books!";
    public static final String RENT_ALREADY_RETURNED = "Rent with this id :'%s' is already returned!";
    public static final String RENT_WITH_ID_NOT_FOUND = "Rent with this id: '%d' is not present in library!";
    public static final String COPYRIGHT_REASON = "copyright";
    public static final String BANNED_REASON = "banned";
    public static final String TITLE_NOT_BLANK = "Title should not be blank";
    public static final String TITLE_LENGTH_VALIDATION = "Title should be between 1 and 150 symbols";
    public static final String TOTAL_QUANTITY_MORE_THAN_0 = "Book's total quantity should be equal or more than 0";
    public static final String GENRE_NOT_BLANK = "Genre must not be blank";
    public static final String GENRE_LENGTH_VALIDATION = "Genre length must be at least 2 symbols";
    public static final String AUTHOR_FIRST_NAME_NOT_EMPTY = "First name can not be empty";
    public static final String AUTHOR_LAST_NAME_NOT_EMPTY = "Last name can not be empty";
    public static final String AUTHOR_FIRST_NAME_LENGTH_VALIDATION = "First name should be at least 2 characters";
    public static final String AUTHOR_LAST_NAME_LENGTH_VALIDATION = "Last name should be at least 2 characters";
    public static final int QUANTITY = 5;
    public static final String ADMIN_USERNAME = "dimo";
    public static final String ADMIN_PASSWORD = "123456";
    public static final String USER_PASSWORD = "123456";
}
