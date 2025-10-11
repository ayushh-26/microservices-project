package com.project.library.Books.repository;

import com.project.library.Books.irepository.IBookRepository;
import com.project.library.Books.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookRepositoryImpl implements IBookRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String generateBookId() {
        String sql = "SELECT book_id FROM books ORDER BY book_id DESC LIMIT 1";
        List<String> lastIdList = jdbcTemplate.queryForList(sql, String.class);
        String lastId = lastIdList.isEmpty() ? null : lastIdList.get(0);
        int nextNum = 1;
        if (lastId != null) {
            nextNum = Integer.parseInt(lastId.substring(1)) + 1;
        }
        return String.format("B%03d", nextNum);
    }

    @Override
    public Book addBook(Book book) {
        String bookId = generateBookId();
        book.setBookId(bookId);

        String sql = "INSERT INTO books (book_id, title, author, publisher, published_year, total_copies, available_copies) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, book.getBookId(), book.getTitle(), book.getAuthor(),
                book.getPublisher(), book.getPublishedYear(), book.getTotalCopies(), book.getAvailableCopies());
        return book;
    }

    @Override
    public List<Book> getAllBooks() {
        String sql = "SELECT * FROM books";
        return jdbcTemplate.query(sql, new BookRowMapper());
    }

    @Override
    public Book getBookById(String bookId) {
        String sql = "SELECT * FROM books WHERE book_id=?";
        List<Book> books = jdbcTemplate.query(sql, new BookRowMapper(), bookId);
        return books.isEmpty() ? null : books.get(0);
    }

    @Override
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title=?, author=?, publisher=?, published_year=?, total_copies=?, available_copies=? WHERE book_id=?";
        int updated = jdbcTemplate.update(sql, book.getTitle(), book.getAuthor(), book.getPublisher(),
                book.getPublishedYear(), book.getTotalCopies(), book.getAvailableCopies(), book.getBookId());
        return updated > 0;
    }

    @Override
    public boolean deleteBookById(String bookId) {
        String sql = "DELETE FROM books WHERE book_id=?";
        int deleted = jdbcTemplate.update(sql, bookId);
        return deleted > 0;
    }

    @Override
    public List<Book> getBooksByTitle(String title) {
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, new BookRowMapper(), "%" + title + "%");
    }

    @Override
    public List<Book> getBooksByExactTitle(String title) {
        String sql = "SELECT * FROM books WHERE LOWER(title) = LOWER(?)";
        return jdbcTemplate.query(sql, new BookRowMapper(), title);
    }

    @Override
    public List<Book> getBooksByAuthor(String author) {
        String sql = "SELECT * FROM books WHERE LOWER(author) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, new BookRowMapper(), "%" + author + "%");
    }

    @Override
    public void decrementAvailable(String bookId) {
        String sql = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ? AND available_copies > 0";
        jdbcTemplate.update(sql, bookId);
    }

    @Override
    public void incrementAvailable(String bookId) {
        String sql = "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?";
        jdbcTemplate.update(sql, bookId);
    }

    @Override
public List<Book> getBooksByPage(int page, int size) {
    int offset = page * size;
    String sql = "SELECT * FROM books LIMIT ? OFFSET ?";
    return jdbcTemplate.query(sql, new BookRowMapper(), size, offset);
}

// Optional: start & end rows
@Override
public List<Book> getBooksByStartEnd(int start, int end) {
    int size = end - start + 1;
    String sql = "SELECT * FROM books LIMIT ? OFFSET ?";
    return jdbcTemplate.query(sql, new BookRowMapper(), size, start);
}

// Total number of books
@Override
public int getTotalBooks() {
    String sql = "SELECT COUNT(*) FROM books";
    return jdbcTemplate.queryForObject(sql, Integer.class);
}

}
