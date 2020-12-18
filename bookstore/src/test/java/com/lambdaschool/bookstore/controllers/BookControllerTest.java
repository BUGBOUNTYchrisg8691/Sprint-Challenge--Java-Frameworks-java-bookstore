package com.lambdaschool.bookstore.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaschool.bookstore.BookstoreApplication;
import com.lambdaschool.bookstore.exceptions.ResourceNotFoundException;
import com.lambdaschool.bookstore.models.Author;
import com.lambdaschool.bookstore.models.Book;
import com.lambdaschool.bookstore.models.Section;
import com.lambdaschool.bookstore.models.Wrote;
import com.lambdaschool.bookstore.services.BookService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)

/*****
 * Due to security being in place, we have to switch out WebMvcTest for SpringBootTest
 * @WebMvcTest(value = BookController.class)
 */
@SpringBootTest(classes = BookstoreApplication.class)

/****
 * This is the user and roles we will use to test!
 */
@WithMockUser(username = "admin", roles = {"ADMIN", "DATA"})
public class BookControllerTest
{
    /******
     * WebApplicationContext is needed due to security being in place.
     */
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    List<Book> bookList = new ArrayList<>();

    @Before
    public void setUp() throws
            Exception
    {
        /*****
         * The following is needed due to security being in place!
         */
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        /*****
         * Note that since we are only testing bookstore data, you only need to mock up bookstore data.
         * You do NOT need to mock up user data. You can. It is not wrong, just extra work.
         */
    
        Author a1 = new Author("John", "Mitchell");
        Author a2 = new Author("Dan", "Brown");
        Author a3 = new Author("Jerry", "Poe");
        Author a4 = new Author("Wells", "Teague");
        Author a5 = new Author("George", "Gallinger");
        Author a6 = new Author("Ian", "Stewart");
    
        Section s1 = new Section("Fiction");
        Section s2 = new Section("Technology");
        Section s3 = new Section("Travel");
        Section s4 = new Section("Business");
        Section s5 = new Section("Religion");
    
        Book b1 = new Book("Flatterland", "9780738206752", 2001, s1);
        b1.getWrotes()
                .add(new Wrote(a6, new Book()));
        b1.setBookid(0);
        bookList.add(b1);
    
        Book b2 = new Book("Digital Fortess", "9788489367012", 2007, s1);
        b2.getWrotes()
                .add(new Wrote(a2, new Book()));
        b1.setBookid(1);
        bookList.add(b2);
    
        Book b3 = new Book("The Da Vinci Code", "9780307474278", 2009, s1);
        b3.getWrotes()
                .add(new Wrote(a2, new Book()));
        b1.setBookid(2);
        bookList.add(b3);
    
        Book b4 = new Book("Essentials of Finance", "1314241651234", 0, s4);
        b4.getWrotes()
                .add(new Wrote(a3, new Book()));
        b4.getWrotes()
                .add(new Wrote(a5, new Book()));
        b1.setBookid(3);
        bookList.add(b4);
    
        Book b5 = new Book("Calling Texas Home", "1885171382134", 2000, s3);
        b5.getWrotes()
                .add(new Wrote(a4, new Book()));
        b1.setBookid(4);
        bookList.add(b5);
    
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws
            Exception
    {
    }

    @Test
    public void listAllBooks() throws
            Exception
    {
        String url = "/books/books";
        
        String booksAsJson = objectMapper.writeValueAsString(bookList);
    
        Mockito.when(bookService.findAll()).thenReturn(bookList);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(booksAsJson);
        
        mockMvc.perform(mockRequest).andExpect(status().isOk());
    }

    @Test
    public void getBookById() throws
            Exception
    {
        String url = "/books/book/{bookid}";
        
        String bookAsJson = objectMapper.writeValueAsString(bookList.get(1));
        
        Mockito.when(bookService.findBookById(1L)).thenReturn(bookList.get(1));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(url, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookAsJson);
        
        mockMvc.perform(mockRequest).andExpect(status().isOk());
    }

    @Test
    public void getNoBookById() throws
            Exception
    {
        String url = "/books/book/100";
        Mockito.when(bookService.findBookById(100)).thenReturn(null);
        RequestBuilder request = MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request).andReturn();
        String content = result.getResponse().getContentAsString();
        String empty = "";
        assertEquals(content, empty);
    }
    
    @Test
    public void addNewBook() throws
            Exception
    {
        String url = "/books/book";
        
        String bookName = "Test Book";
        Section section = new Section("Fiction");
        Author author = new Author("Dan", "Brown");
        Book book = new Book(bookName, "0123456789999", 2001, section);
        book.getWrotes().add(new Wrote(author, new Book()));
        book.setBookid(99);
        
        Mockito.when(bookService.save(any(Book.class))).thenReturn(book);
        
        String bookAsJsonString = objectMapper.writeValueAsString(book);
    
        RequestBuilder request = MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookAsJsonString);
        
        mockMvc.perform(request).andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateFullBook() throws Exception
    {
        String url = "/books/book/{bookid}";
        String bookName = "Test Book";
        Section section = new Section("Fiction");
        Author author = new Author("Dan", "Brown");
        Book book = new Book(bookName, "0000000000000", 2020, section);
        book.getWrotes().add(new Wrote(author, new Book()));
        
        Mockito.when(bookService.update(book, 1L)).thenReturn(book);
        
        String bookAsJsonString = objectMapper.writeValueAsString(book);
        
        RequestBuilder request = MockMvcRequestBuilders.put(url, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(bookAsJsonString);
        
        mockMvc.perform(request).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteBookById() throws
            Exception
    {
        String url = "/books/book/{id}";
        
        RequestBuilder request = MockMvcRequestBuilders.delete(url, "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        
        mockMvc.perform(request).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
    }
}