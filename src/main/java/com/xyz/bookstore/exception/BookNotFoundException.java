package com.xyz.bookstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by hadi on 2/8/20.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BookNotFoundException extends Exception {
    private Long id;

    public BookNotFoundException(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "BOOK_NOT_FOUND. id= "+id;
    }
}
