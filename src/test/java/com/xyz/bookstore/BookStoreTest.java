package com.xyz.bookstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.bookstore.dto.BookDto;
import com.xyz.bookstore.model.Book;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by hadi on 2/8/20.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookStoreTest {
    private static BookDto firstRequestDto;
    private final String bookAdminPath = "/api/v1/book-admin";
    private final String booksPath = "/api/v1/books";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void init() {
        HashSet<Book.Category> categories = new HashSet<>();
        categories.add(Book.Category.Classic);
        categories.add(Book.Category.Commic);
        firstRequestDto = new BookDto("ISBN_1", "name_1", "author_1", categories);
    }

    @Test
    @Order(0)
    void getBook_NotFound() throws Exception {
        Long bookId = 1L;
        mockMvc.perform(
                get(booksPath + "/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(1)
    @WithMockUser(username = "user", roles = "USER")
    void createBook_Forbidden() throws Exception {
        mockMvc.perform(
                post(bookAdminPath)
                        .content(asJsonString(firstRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    void createBook_Unauthorized() throws Exception {
        mockMvc.perform(
                post(bookAdminPath)
                        .content(asJsonString(firstRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBook_BadRequest() throws Exception {
        mockMvc.perform(
                post(bookAdminPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("INVALID_REQUEST_BODY"));

        mockMvc.perform(
                post(bookAdminPath)
                        .content(asJsonString(new HashMap<>()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.isbn").value("isbn field is required"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.name").value("name field is required"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.author").value("author field is required"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.categories").value("categories field is required"));
    }

    @Test
    @Order(3)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBook_OK() throws Exception {
        Long statTimeMillis = System.currentTimeMillis();
        ResultActions resultActions = mockMvc.perform(
                post(bookAdminPath)
                        .content(asJsonString(firstRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn").value(firstRequestDto.getIsbn()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(firstRequestDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author").value(firstRequestDto.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdBy").value("admin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdDate").value(Matchers.greaterThan(statTimeMillis)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdDate").value(Matchers.lessThan(System.currentTimeMillis())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastModifiedBy").value("admin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastModifiedDate").value(Matchers.greaterThan(statTimeMillis)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastModifiedDate").value(Matchers.lessThan(System.currentTimeMillis())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categories", Matchers.hasSize(firstRequestDto.getCategories().size())));
        for (Book.Category category : firstRequestDto.getCategories()) {
            resultActions = resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.categories", Matchers.hasItem(category.toString())));
        }
    }

    @Test
    @Order(4)
    void getBook_OK() throws Exception {
        Long bookId = 1L;

        ResultActions resultActions = mockMvc.perform(
                get(booksPath + "/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bookId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn").value(firstRequestDto.getIsbn()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(firstRequestDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author").value(firstRequestDto.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdBy").value("admin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdDate").value(Matchers.lessThan(System.currentTimeMillis())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastModifiedBy").value("admin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastModifiedDate").value(Matchers.lessThan(System.currentTimeMillis())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categories", Matchers.hasSize(firstRequestDto.getCategories().size())));
        for (Book.Category category : firstRequestDto.getCategories()) {
            resultActions = resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.categories", Matchers.hasItem(category.toString())));
        }
    }

    @Test
    @Order(5)
    @WithMockUser(username = "admin_update", roles = "ADMIN")
    void updateBook_ThenGetBook_OK() throws Exception {
        HashSet<Book.Category> categories = new HashSet<>();
        categories.add(Book.Category.Commic);
        Long bookId = 1L;
        BookDto updateRequestDto = new BookDto("ISBN_2", "name_2", "author_2", categories);

        final Long statTimeMillis = System.currentTimeMillis();
        mockMvc.perform(
                put(bookAdminPath + "/" + bookId)
                        .content(asJsonString(updateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bookId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn").value(updateRequestDto.getIsbn()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updateRequestDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author").value(updateRequestDto.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categories", Matchers.hasItem(Book.Category.Commic.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categories", Matchers.hasSize(updateRequestDto.getCategories().size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdBy").value("admin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdDate").value(Matchers.lessThan(System.currentTimeMillis())))

                .andExpect(MockMvcResultMatchers.jsonPath("$.lastModifiedBy").value("admin_update"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastModifiedDate").value(Matchers.greaterThan(statTimeMillis)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastModifiedDate").value(Matchers.lessThan(System.currentTimeMillis())));

        mockMvc.perform(
                get(booksPath + "/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bookId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn").value(updateRequestDto.getIsbn()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updateRequestDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author").value(updateRequestDto.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categories", Matchers.hasItem(Book.Category.Commic.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categories", Matchers.hasSize(updateRequestDto.getCategories().size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdBy").value("admin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdDate").value(Matchers.lessThan(System.currentTimeMillis())))

                .andExpect(MockMvcResultMatchers.jsonPath("$.lastModifiedBy").value("admin_update"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastModifiedDate").value(Matchers.greaterThan(statTimeMillis)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastModifiedDate").value(Matchers.lessThan(System.currentTimeMillis())));
    }

    @Test
    @Order(6)
    @WithMockUser(username = "user", roles = "USER")
    void deleteBook_Forbidden() throws Exception {
        Long bookId = 1L;
        mockMvc.perform(
                delete(bookAdminPath + "/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(
                get(booksPath + "/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBook_OK() throws Exception {
        Long bookId = 1L;
        mockMvc.perform(
                delete(bookAdminPath + "/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(
                get(booksPath + "/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBook_NotFound() throws Exception {
        Long bookId = 1L;
        mockMvc.perform(
                delete(bookAdminPath + "/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    void createBook_EndToEnd_Forbidden() throws Exception {
        HashSet<Book.Category> categories = new HashSet<>();
        categories.add(Book.Category.Classic);
        ResponseEntity<BookDto> result = restTemplate.withBasicAuth("user", "password")
                .postForEntity(bookAdminPath, new BookDto("ISBN", "name", "author", categories), BookDto.class);
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    @Order(10)
    void createBook_EndToEnd_OK() throws Exception {
        Long startTime = System.currentTimeMillis();

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "password")
                .postForEntity(bookAdminPath, firstRequestDto, String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        BookDto responseDto = objectMapper.readValue(result.getBody(), BookDto.class);

        assertNotNull(responseDto.getId(), "book.id");
        assertEquals(firstRequestDto.getName(), responseDto.getName());
        assertEquals(firstRequestDto.getAuthor(), responseDto.getAuthor());
        assertEquals(firstRequestDto.getIsbn(), responseDto.getIsbn());
        assertEquals(firstRequestDto.getCategories(), responseDto.getCategories());

        assertEquals("admin", responseDto.getCreatedBy());
        assertTrue(startTime.compareTo(responseDto.getCreatedDate()) < 0, "createDate");
        assertTrue(System.currentTimeMillis() > startTime.compareTo(responseDto.getCreatedDate()), "createDate");

        assertEquals("admin", responseDto.getLastModifiedBy());
        assertTrue(startTime.compareTo(responseDto.getLastModifiedDate()) < 0, "lastModifiedDate");
        assertTrue(System.currentTimeMillis() > startTime.compareTo(responseDto.getLastModifiedDate()), "lastModifiedDate");
    }

    @Test
    @Order(11)
    void deleteBook_EndToEnd_OK() throws Exception {
        Long secondCreateBookId = 2L;
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "password")
                .exchange(bookAdminPath + "/" + secondCreateBookId, HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @Order(12)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getSimpleViews() throws Exception {
        List<BookDto> createdBookDtos = new ArrayList<>();
        int count = 14;
        for (int i = 0; i < count; i++) {
            BookDto requestDto = new BookDto("ISBN_" + i, "name_" + i, "author_" + i, firstRequestDto.getCategories());
            MvcResult mvcResult = mockMvc.perform(
                    post(bookAdminPath)
                            .content(asJsonString(requestDto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                    .andReturn();

            BookDto responseDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), BookDto.class);
            createdBookDtos.add(responseDto);
        }

        //default page & size
        ResultActions resultActions = this.mockMvc.perform(
                get(booksPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(count))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(count)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$.first").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfElements").value(count))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.empty").value(false));
        for (int i = 0; i < createdBookDtos.size(); i++) {
            resultActions = resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.content[" + i + "].name").value(createdBookDtos.get(i).getName()));
            resultActions = resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.content[" + i + "].author").value(createdBookDtos.get(i).getAuthor()));
        }

        //sort by name descending, then get first page with size 5
        final int pageSize = 5;
        resultActions = this.mockMvc.perform(
                get(booksPath + "?size=" + pageSize + "&page=0&sort=name,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(count))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(pageSize)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(pageSize))
                .andExpect(MockMvcResultMatchers.jsonPath("$.first").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfElements").value(pageSize))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.empty").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sort.sorted").value(true));

        //sort by name descending
        Collections.sort(createdBookDtos, (book1, book2) -> book2.getName().compareTo(book1.getName()));

        for (int i = 0; i < pageSize; i++) {
            resultActions = resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.content[" + i + "].name").value(createdBookDtos.get(i).getName()));
            resultActions = resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.content[" + i + "].author").value(createdBookDtos.get(i).getAuthor()));
        }

        //sort by name descending, then get second page with size 5 as a list
        resultActions = this.mockMvc.perform(
                get(booksPath + "/list?size=" + pageSize + "&page=1&sort=name,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.*").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", Matchers.hasSize(pageSize)));

        for (int i = 0; i < pageSize; i++) {
            resultActions = resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.[" + i + "].name").value(createdBookDtos.get(pageSize + i).getName()));
            resultActions = resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.[" + i + "].author").value(createdBookDtos.get(pageSize + i).getAuthor()));
        }
    }

    String asJsonString(final Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

}
