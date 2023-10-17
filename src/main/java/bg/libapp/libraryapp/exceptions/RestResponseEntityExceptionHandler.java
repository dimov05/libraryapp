package bg.libapp.libraryapp.exceptions;

import bg.libapp.libraryapp.exceptions.author.AuthorNotFoundException;
import bg.libapp.libraryapp.exceptions.book.BookAlreadyAddedException;
import bg.libapp.libraryapp.exceptions.book.BookIsActiveOnDeleteException;
import bg.libapp.libraryapp.exceptions.book.BookNotActiveException;
import bg.libapp.libraryapp.exceptions.book.BookNotFoundException;
import bg.libapp.libraryapp.exceptions.book.IsbnNotValidException;
import bg.libapp.libraryapp.exceptions.book.NoSuchDeactivateReasonException;
import bg.libapp.libraryapp.exceptions.genre.GenreNotFoundException;
import bg.libapp.libraryapp.exceptions.rent.CannotRentBookTwiceException;
import bg.libapp.libraryapp.exceptions.rent.InsufficientAvailableQuantityException;
import bg.libapp.libraryapp.exceptions.rent.InsufficientTotalQuantityException;
import bg.libapp.libraryapp.exceptions.rent.RentAlreadyReturnedException;
import bg.libapp.libraryapp.exceptions.rent.RentNotFoundException;
import bg.libapp.libraryapp.exceptions.rent.UserHasProlongedRentsException;
import bg.libapp.libraryapp.exceptions.rent.UserNotEligibleToRentException;
import bg.libapp.libraryapp.exceptions.rent.UserRentedMaximumAllowedBooksException;
import bg.libapp.libraryapp.exceptions.user.UserIsAlreadyActivatedException;
import bg.libapp.libraryapp.exceptions.user.UserIsAlreadyDeactivatedException;
import bg.libapp.libraryapp.exceptions.user.UserNotFoundException;
import bg.libapp.libraryapp.exceptions.user.UserWithThisUsernameAlreadyExistsException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = {IllegalArgumentException.class, IsbnNotValidException.class, BookIsActiveOnDeleteException.class, IllegalStateException.class, UserRentedMaximumAllowedBooksException.class, RentAlreadyReturnedException.class, UserHasProlongedRentsException.class, UserNotEligibleToRentException.class, CannotRentBookTwiceException.class, InsufficientAvailableQuantityException.class, InsufficientTotalQuantityException.class, NoSuchDeactivateReasonException.class, BookNotActiveException.class, UserWithThisUsernameAlreadyExistsException.class, BookAlreadyAddedException.class, UserIsAlreadyActivatedException.class, UserIsAlreadyDeactivatedException.class})
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value
            = {BookNotFoundException.class, RentNotFoundException.class, AuthorNotFoundException.class, FieldNotFoundException.class, GenreNotFoundException.class, UserNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value
            = {CannotProcessJsonOfEntityException.class})
    protected ResponseEntity<Object> handleUnprocessable(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }
}