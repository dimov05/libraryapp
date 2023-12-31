package bg.libapp.libraryapp.exceptions.genre;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException(String genre) {
        super("Genre with this name: '" + genre + "' is not present in the library!");
    }
}