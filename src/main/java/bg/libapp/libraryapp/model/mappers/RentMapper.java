package bg.libapp.libraryapp.model.mappers;

import bg.libapp.libraryapp.model.dto.rent.RentDTO;
import bg.libapp.libraryapp.model.entity.Rent;
import bg.libapp.libraryapp.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static bg.libapp.libraryapp.model.constants.ApplicationConstants.MAP_TO_RENT_DTO_ACCESSED;

public class RentMapper {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public static RentDTO mapToRentDTO(Rent rent) {
        logger.info(MAP_TO_RENT_DTO_ACCESSED, rent);
        return new RentDTO()
                .setId(rent.getId())
                .setRentDate(rent.getRentDate().toString())
                .setUser(UserMapper.mapToUserDTO(rent.getUser()))
                .setBook(BookMapper.mapToBookDTO(rent.getBook()))
                .setExpectedReturnDate(String.valueOf(rent.getExpectedReturnDate()))
                .setActualReturnDate(String.valueOf(rent.getActualReturnDate()));
    }
}
