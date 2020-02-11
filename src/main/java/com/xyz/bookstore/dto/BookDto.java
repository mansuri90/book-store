package com.xyz.bookstore.dto;

import com.xyz.bookstore.model.Book;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * Created by hadi on 2/8/20.
 */
public class BookDto {
    private Long id;
    @NotBlank(message = "isbn field is required")
    private String isbn;
    @NotBlank(message = "name field is required")
    private String name;
    @NotBlank(message = "author field is required")
    private String author;
    @NotEmpty(message = "categories field is required")
    private Set<Book.Category> categories;
    private String createdBy;
    private Long createdDate;
    private String lastModifiedBy;
    private Long lastModifiedDate;

    public BookDto() {
    }

    public BookDto(Long id, String isbn, String name, String author, Set<Book.Category> categories,
                   String createdBy, Long createdDate, String lastModifiedBy, Long lastModifiedDate) {
        this.id = id;
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.categories = categories;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
    }

    public BookDto(String isbn, String name, String author, Set<Book.Category> categories) {
        this(null, isbn, name, author, categories, null, null, null, null);
    }

    public BookDto(Book book) {
        this(book.getId(), book.getISBN(), book.getName(), book.getAuthor(), book.getCategories()
                , book.getCreatedBy(), book.getCreatedDate(), book.getLastModifiedBy(), book.getLastModifiedDate());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Set<Book.Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Book.Category> categories) {
        this.categories = categories;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
