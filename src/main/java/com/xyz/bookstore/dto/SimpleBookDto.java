package com.xyz.bookstore.dto;

import com.xyz.bookstore.model.SimpleBookView;

/**
 * Created by hadi on 2/8/20.
 */
public class SimpleBookDto {
    private String name;
    private String author;

    public SimpleBookDto() {
    }

    public SimpleBookDto(SimpleBookView simpleBookView) {
        this(simpleBookView.getName(), simpleBookView.getAuthor());
    }

    public SimpleBookDto(String name, String author) {
        this.name = name;
        this.author = author;
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
}
