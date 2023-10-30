package bg.libapp.libraryapp.event;

import bg.libapp.libraryapp.model.entity.Book;

public abstract class BaseUpdateBookAuditEvent extends BaseBookAuditEvent {
    private String fieldName;
    private String oldValue;
    private String newValue;

    public BaseUpdateBookAuditEvent(Book book, String oldValue) {
        super(book);
        this.oldValue = oldValue;
    }

    public BaseUpdateBookAuditEvent(Book book, String fieldName, String oldValue) {
        super(book);
        this.fieldName = fieldName;
        this.oldValue = oldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
