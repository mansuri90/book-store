package com.xyz.bookstore.controller;

import com.xyz.bookstore.dto.BookDto;
import com.xyz.bookstore.dto.SimpleBookDto;
import com.xyz.bookstore.exception.BookNotFoundException;
import com.xyz.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hadi on 2/8/20.
 */
@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("")
    public Page<SimpleBookDto> getSimpleBookViewsWithCount(Pageable pageable){
        return bookService.getSimpleBookViewPage(pageable).map(SimpleBookDto::new);
    }

    @GetMapping("/slice")
    public Slice<SimpleBookDto> getSimpleBookViewsList(Pageable pageable){
        return bookService.getSimpleBookViewList(pageable).map(SimpleBookDto::new);
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable("id") Long id) throws BookNotFoundException {
        return new BookDto(bookService.findById(id));
    }

}
