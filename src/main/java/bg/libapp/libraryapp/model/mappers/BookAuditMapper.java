package bg.libapp.libraryapp.model.mappers;

import bg.libapp.libraryapp.event.BaseUpdateBookAuditEvent;
import bg.libapp.libraryapp.event.SaveBookAuditEvent;
import bg.libapp.libraryapp.model.entity.BookAudit;
import bg.libapp.libraryapp.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static bg.libapp.libraryapp.model.constants.ApplicationConstants.MAP_TO_BOOK_AUDIT_ACCESSED;

public class BookAuditMapper {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public static BookAudit mapToBookAudit(BaseUpdateBookAuditEvent event) {
        logger.info(MAP_TO_BOOK_AUDIT_ACCESSED, event);
        return new BookAudit()
                .setEventDate(LocalDateTime.now())
                .setOperationType(event.getOperationType().name())
                .setFieldName(event.getFieldName())
                .setOldValue(event.getOldValue())
                .setNewValue(event.getNewValue())
                .setUser(event.getUser())
                .setBook(event.getBook());
    }

    public static BookAudit mapToBookAudit(SaveBookAuditEvent event) {
        logger.info(MAP_TO_BOOK_AUDIT_ACCESSED, event);
        return new BookAudit()
                .setEventDate(LocalDateTime.now())
                .setOperationType(event.getOperationType().name())
                .setNewValue(event.getNewValue())
                .setUser(event.getUser())
                .setBook(event.getBook());
    }
}
