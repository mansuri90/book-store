package com.xyz.bookstore.controller;

import com.xyz.bookstore.dto.BookDto;
import com.xyz.bookstore.exception.BookNotFoundException;
import com.xyz.bookstore.model.Book;
import com.xyz.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by hadi on 2/8/20.
 */
@RestController
@RequestMapping("/api/v1/book-admin")
public class BookAdminController {

    @Autowired
    private BookService bookService;

    @PostMapping("")
    public BookDto createBook(@Valid @RequestBody BookDto bookDto) {
        Book book = new Book(bookDto.getIsbn(), bookDto.getName(), bookDto.getAuthor(), bookDto.getCategories());
        Book savedBook = bookService.save(book);
        return new BookDto(savedBook);
    }

    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable(value = "id") Long id,
                              @Valid @RequestBody BookDto bookDto) throws BookNotFoundException {
        Book book = bookService.findById(id);

        book.setISBN(bookDto.getIsbn());
        book.setName(bookDto.getName());
        book.setAuthor(bookDto.getAuthor());
        book.setCategories(bookDto.getCategories());

        final Book updatedBook = bookService.save(book);
        return new BookDto(updatedBook);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable(value = "id") Long id) throws BookNotFoundException {
        Book book = bookService.findById(id);
        bookService.delete(book);
    }

}
