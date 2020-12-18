package com.lambdaschool.bookstore.services;

import com.lambdaschool.bookstore.BookstoreApplication;
import com.lambdaschool.bookstore.exceptions.ResourceNotFoundException;
import com.lambdaschool.bookstore.models.Author;
import com.lambdaschool.bookstore.models.Book;
import com.lambdaschool.bookstore.models.Section;
import com.lambdaschool.bookstore.models.Wrote;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookstoreApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//**********
// Note security is handled at the controller, hence we do not need to worry about security here!
//**********
public class BookServiceImplTest
{

    @Autowired
    private BookService bookService;

    @Before
    public void setUp() throws
            Exception
    {

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws
            Exception
    {
    }

    @Test
    public void a_findAll()
    {
        assertEquals(5, bookService.findAll().size());
    }

    @Test
    public void b_findBookById()
    {
        assertEquals("Flatterland", bookService.findBookById(26).getTitle());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void c_notFindBookById()
    {
        assertEquals("Test Book", bookService.findBookById(9999).getTitle());
    }

    @Test
    public void f_delete()
    {
        bookService.delete(26);
        assertEquals(5, bookService.findAll().size());
        
    }

    @Test
    public void d_save()
    {
        String newBookName = "Test book";
        Section section = new Section("Fiction");
        section.setSectionid(22);
        Author author = new Author("John", "Mitchell");
        author.setAuthorid(18);
        Book newBook = new Book(newBookName, "0123456789999", 2020, section);
        newBook.getWrotes().add(new Wrote(author, new Book()));
        
        newBook = bookService.save(newBook);
        assertNotNull(newBook);
        assertEquals(newBookName, newBook.getTitle());
    }

    @Test
    public void e_update()
    {
        String updatedBookName = "Test updated book";
        Book updatedBook = new Book();
        updatedBook.setTitle(updatedBookName);
        
        updatedBook = bookService.update(updatedBook, 27);
        assertNotNull(updatedBook);
        assertEquals(updatedBookName, updatedBook.getTitle());
    }

    @Test
    public void g_deleteAll()
    {
        bookService.deleteAll();
        assertEquals(0, bookService.findAll().size());
    }
}