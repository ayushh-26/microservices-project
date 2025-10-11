package com.project.library.Books.controller;

import com.project.library.Books.dto.BookDTO;
import com.project.library.Books.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books") // Base URL
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // ------------------- CREATE -------------------
    /*
     * POST http://localhost:9002/books/add
     * Body:
     * {
     * "title": "Java Basics",
     * "author": "John Doe",
     * "publisher": "TechPub",
     * "publishedYear": 2020,
     * "totalCopies": 10,
     * "availableCopies": 10
     * }
     */
    @PostMapping("/add")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        BookDTO created = bookService.addBook(bookDTO);
        return ResponseEntity.ok(created);
    }

    // ------------------- READ -------------------
    /*
     * GET http://localhost:9002/books/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /*
     * GET http://localhost:9002/books/id/B001
     */
    @GetMapping("/id/{bookId}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable String bookId) {
        BookDTO book = bookService.getBookById(bookId);
        if (book == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(book);
    }

    /*
     * GET http://localhost:9002/books/title/Java
     * → partial match
     */
    @GetMapping("/title/{title}")
    public ResponseEntity<List<BookDTO>> getBooksByTitle(@PathVariable String title) {
        return ResponseEntity.ok(bookService.getBooksByTitle(title));
    }

    /*
     * GET http://localhost:9002/books/search?title=Java Basics
     * → exact match (used by IssueService)
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String title) {
        return ResponseEntity.ok(bookService.getBooksByExactTitle(title));
    }

    /*
     * GET http://localhost:9002/books/author/John
     */
    @GetMapping("/author/{author}")
    public ResponseEntity<List<BookDTO>> getBooksByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(bookService.getBooksByAuthor(author));
    }

    // ------------------- UPDATE -------------------
    /*
     * PUT http://localhost:9002/books/update/B001
     * Body:
     * {
     * "title": "Updated Java",
     * "author": "Jane Doe",
     * "publisher": "NewPub",
     * "publishedYear": 2023,
     * "totalCopies": 12,
     * "availableCopies": 12
     * }
     */
    @PutMapping("/update/{bookId}")
    public ResponseEntity<String> updateBook(@PathVariable String bookId, @RequestBody BookDTO bookDTO) {
        boolean updated = bookService.updateBook(bookId, bookDTO);
        if (!updated)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Book updated successfully.");
    }

    // ------------------- DELETE -------------------
    /*
     * DELETE http://localhost:9002/books/delete/B001
     */
    @DeleteMapping("/delete/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable String bookId) {
        boolean deleted = bookService.deleteBook(bookId);
        if (!deleted)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Book deleted successfully.");
    }

    // ------------------- CHECK AVAILABILITY -------------------
    /*
     * GET http://localhost:9002/books/available/B001
     */
    @GetMapping("/available/{bookId}")
    public ResponseEntity<Integer> getAvailableCopies(@PathVariable String bookId) {
        Integer available = bookService.getAvailableCopies(bookId);
        return ResponseEntity.ok(available);
    }

    /*
     * PUT http://localhost:9002/books/decrement/B001
     */
    @PutMapping("/decrement/{bookId}")
    public ResponseEntity<Void> decrementCopies(@PathVariable String bookId) {
        bookService.decrementAvailable(bookId);
        return ResponseEntity.ok().build();
    }

    /*
     * PUT http://localhost:9002/books/increment/B001
     */
    @PutMapping("/increment/{bookId}")
    public ResponseEntity<Void> incrementCopies(@PathVariable String bookId) {
        bookService.incrementAvailable(bookId);
        return ResponseEntity.ok().build();
    }

    /*
 * GET /books/nextpage?page=0&size=100
 * or
 * GET /books/nextpage?page=0&start=10&end=70
 */
@GetMapping("/nextpage")
public ResponseEntity<Map<String, Object>> getBooksByPage(
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam(required = false) Integer start,
        @RequestParam(required = false) Integer end) {

    Map<String, Object> response = bookService.getBooksPaginated(page, size, start, end);
    return ResponseEntity.ok(response);
}

}
