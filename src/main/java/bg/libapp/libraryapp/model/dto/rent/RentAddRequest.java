package bg.libapp.libraryapp.model.dto.rent;

public class RentAddRequest {
    private Long userId;

    public RentAddRequest() {
    }

    public RentAddRequest(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public RentAddRequest setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "userId=" + userId +
                '}';
    }
}
