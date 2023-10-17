package bg.libapp.libraryapp.service;

import bg.libapp.libraryapp.event.SaveBookAuditEvent;
import bg.libapp.libraryapp.event.UpdateActiveStatusBookAuditEvent;
import bg.libapp.libraryapp.event.UpdateDeactivateReasonBookAuditEvent;
import bg.libapp.libraryapp.event.UpdatePublisherBookAuditEvent;
import bg.libapp.libraryapp.event.UpdateQuantityBookAuditEvent;
import bg.libapp.libraryapp.event.UpdateYearBookAuditEvent;
import bg.libapp.libraryapp.model.entity.User;
import bg.libapp.libraryapp.model.enums.AuditEnum;
import bg.libapp.libraryapp.model.mappers.BookAuditMapper;
import bg.libapp.libraryapp.repository.BookAuditRepository;
import bg.libapp.libraryapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static bg.libapp.libraryapp.model.constants.ApplicationConstants.*;

@Service
@Transactional
public class BookAuditService {
    private final Logger logger = LoggerFactory.getLogger(BookAuditService.class);
    private final UserRepository userRepository;
    private final BookAuditRepository bookAuditRepository;

    @Autowired
    public BookAuditService(UserRepository userRepository, BookAuditRepository bookAuditRepository) {
        this.userRepository = userRepository;
        this.bookAuditRepository = bookAuditRepository;
    }

    public void updateYearOfBook(UpdateYearBookAuditEvent event) {
        event.setUser(getUserForAudit());
        event.setNewValue(String.valueOf(event.getBook().getYear()));
        event.setOperationType(AuditEnum.UPDATE);
        event.setFieldName(YEAR);
        logger.info("Creating an event for updating year of book with params: " + event);
        bookAuditRepository.saveAndFlush(BookAuditMapper.mapToBookAudit(event));
    }

    public void updateQuantityOfBook(UpdateQuantityBookAuditEvent event) {
        event.setUser(getUserForAudit());
        if (event.getFieldName().equals(TOTAL_QUANTITY)) {
            event.setNewValue(String.valueOf(event.getBook().getTotalQuantity()));
        } else {
            event.setNewValue(String.valueOf(event.getBook().getAvailableQuantity()));
        }
        event.setOperationType(AuditEnum.UPDATE);
        logger.info("Creating an event for updating '" + event.getFieldName() + "' quantity of book with params: " + event);
        bookAuditRepository.saveAndFlush(BookAuditMapper.mapToBookAudit(event));
    }

    public void updatePublisherOfBook(UpdatePublisherBookAuditEvent event) {
        event.setUser(getUserForAudit());
        event.setNewValue(String.valueOf(event.getBook().getPublisher()));
        event.setOperationType(AuditEnum.UPDATE);
        event.setFieldName(PUBLISHER);
        logger.info("Creating an event for updating publisher of book with params: " + event);
        bookAuditRepository.saveAndFlush(BookAuditMapper.mapToBookAudit(event));
    }

    public void saveBook(SaveBookAuditEvent event) {
        event.setUser(getUserForAudit());
        event.setNewValue(event.getNewValue());
        logger.info("Creating an event for saving a new book with params: " + event);
        event.setOperationType(AuditEnum.ADD);
        bookAuditRepository.saveAndFlush(BookAuditMapper.mapToBookAudit(event));
    }

    public void updateStatusOfBook(UpdateActiveStatusBookAuditEvent event) {
        event.setUser(getUserForAudit());
        event.setNewValue(String.valueOf(event.getBook().isActive()));
        event.setOperationType(AuditEnum.UPDATE);
        event.setFieldName(IS_ACTIVE);
        logger.info("Creating an event for updating status of book with params: " + event);
        bookAuditRepository.saveAndFlush(BookAuditMapper.mapToBookAudit(event));
    }

    public void updateDeactivationStatusOfBook(UpdateDeactivateReasonBookAuditEvent event) {
        event.setUser(getUserForAudit());
        event.setNewValue(String.valueOf(event.getBook().getDeactivateReason()));
        event.setOperationType(AuditEnum.UPDATE);
        event.setFieldName(DEACTIVATE_REASON);
        logger.info("Creating an event for updating status of book with params: " + event);
        bookAuditRepository.saveAndFlush(BookAuditMapper.mapToBookAudit(event));
    }

    private User getUserForAudit() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> {
            logger.info("User with username '" + username + "' was not found!");
            return new UsernameNotFoundException(username);
        });
    }
}
