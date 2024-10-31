package eshop.backend.exception;

public class DetailSectionNotFoundException extends Exception {
    public DetailSectionNotFoundException(Long detailSectionId) {
        super(String.format("Detail section with ID %d was not found.", detailSectionId));
    }
}