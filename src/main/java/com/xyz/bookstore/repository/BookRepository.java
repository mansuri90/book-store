package com.xyz.bookstore.repository;

import com.xyz.bookstore.model.SimpleBookView;
import com.xyz.bookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hadi on 2/8/20.
 */
@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {

    List<SimpleBookView> getSimpleBookViewListBy(Pageable pageable);

    Page<SimpleBookView> getSimpleBookViewPageBy(Pageable pageable);
}
