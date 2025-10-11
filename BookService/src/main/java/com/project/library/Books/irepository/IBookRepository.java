package com.project.library.Books.irepository;

import com.project.library.Books.model.Book;
import java.util.List;

public interface IBookRepository {
    Book addBook(Book book);

    List<Book> getAllBooks();

    Book getBookById(String bookId);

    boolean updateBook(Book book);

    boolean deleteBookById(String bookId);

    // Additional search methods
    List<Book> getBooksByTitle(String title); // partial

    List<Book> getBooksByExactTitle(String title); // exact

    List<Book> getBooksByAuthor(String author);

    List<Book> getBooksByPage(int page, int size);

    List<Book> getBooksByStartEnd(int start, int end);

    int getTotalBooks();

    // Methods to support IssueService
    void decrementAvailable(String bookId);

    void incrementAvailable(String bookId);
}
