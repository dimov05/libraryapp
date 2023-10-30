package bg.libapp.libraryapp.web;

import bg.libapp.libraryapp.model.dto.rent.RentAddRequest;
import bg.libapp.libraryapp.model.dto.rent.RentDTO;
import bg.libapp.libraryapp.service.RentService;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/rents")
public class RentController {
    private final RentService rentService;

    @Autowired
    public RentController(RentService rentService) {
        this.rentService = rentService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MODERATOR') or @rentService.getUsernameByRentId(#id) == authentication.name")
    public ResponseEntity<RentDTO> getRentById(@PathVariable("id") long id) {
        return new ResponseEntity<>(rentService.getRentDTOById(id), HttpStatus.OK);
    }

    @PostMapping("/{isbn}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MODERATOR','ROLE_USER')")
    public ResponseEntity<RentDTO> rentBook(@Valid @RequestBody RentAddRequest rentAddRequest, @PathVariable @ISBN(type = ISBN.Type.ANY) String isbn) {
        return new ResponseEntity<>(this.rentService.rentBook(rentAddRequest, isbn), HttpStatus.CREATED);
    }

    @PutMapping("/return/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MODERATOR') or @rentService.getUsernameByRentId(#rentId) == authentication.name")
    public ResponseEntity<RentDTO> returnBook(@PathVariable("id") long rentId) {
        return new ResponseEntity<>(rentService.returnBook(rentId), HttpStatus.OK);
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MODERATOR')")
    public ResponseEntity<Set<RentDTO>> getAllRents() {
        return new ResponseEntity<>(rentService.getAllRents(), HttpStatus.OK);
    }
}
