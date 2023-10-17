package bg.libapp.libraryapp.model.dto.genre;

public class GenreDTO {
    private String name;

    public GenreDTO() {
    }

    public GenreDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public GenreDTO setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "GenreDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}