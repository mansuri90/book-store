package com.xyz.bookstore.service;

import com.xyz.bookstore.exception.BookNotFoundException;
import com.xyz.bookstore.model.Book;
import com.xyz.bookstore.model.SimpleBookView;
import com.xyz.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Created by hadi on 2/8/20.
 */
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Slice<SimpleBookView> getSimpleBookViewList(Pageable pageable) {
        return bookRepository.getSimpleBookViewSliceBy(pageable);
    }

    public Page<SimpleBookView> getSimpleBookViewPage(Pageable pageable) {
        return bookRepository.getSimpleBookViewPageBy(pageable);
    }

    public Book findById(Long id) throws BookNotFoundException {
        Assert.notNull(id, "id must not be null");
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }


    public void delete(Book book) {
        bookRepository.delete(book);
    }
}
