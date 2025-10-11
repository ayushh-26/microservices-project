package com.project.library.Books.service;

import com.project.library.Books.dto.BookDTO;
import com.project.library.Books.model.Book;
import com.project.library.Books.irepository.IBookRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Map;
import java.util.HashMap;

@Service
public class BookService {

    private final IBookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BookService(IBookRepository bookRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }


public Map<String, Object> getBooksPaginated(Integer page, Integer size, Integer start, Integer end) {
    int totalElements = bookRepository.getTotalBooks();
    List<Book> books;

    if (start != null && end != null) {
        books = bookRepository.getBooksByStartEnd(start, end);
        size = books.size();
        page = start / size; // optional, approximate page
    } else {
        books = bookRepository.getBooksByPage(page, size);
    }

    int totalPages = (int) Math.ceil((double) totalElements / size);

    List<BookDTO> bookDTOs = books.stream()
            .map(b -> modelMapper.map(b, BookDTO.class))
            .collect(Collectors.toList());

    Map<String, Object> response = new HashMap<>();
    response.put("page", page);
    response.put("size", size);
    response.put("totalPages", totalPages);
    response.put("totalElements", totalElements);
    response.put("books", bookDTOs);

    return response;
}

    // CREATE
    public BookDTO addBook(BookDTO dto) {
        Book book = modelMapper.map(dto, Book.class);
        Book created = bookRepository.addBook(book);
        return modelMapper.map(created, BookDTO.class);
    }

    // READ all
    public List<BookDTO> getAllBooks() {
        return bookRepository.getAllBooks().stream()
                .map(b -> modelMapper.map(b, BookDTO.class))
                .collect(Collectors.toList());
    }

    // READ by ID
    public BookDTO getBookById(String bookId) {
        Book b = bookRepository.getBookById(bookId);
        return b != null ? modelMapper.map(b, BookDTO.class) : null;
    }

    // READ by title (partial match)
    public List<BookDTO> getBooksByTitle(String title) {
        return bookRepository.getBooksByTitle(title).stream()
                .map(b -> modelMapper.map(b, BookDTO.class))
                .collect(Collectors.toList());
    }

    // READ by exact title
    public List<BookDTO> getBooksByExactTitle(String title) {
        return bookRepository.getBooksByExactTitle(title).stream()
                .map(b -> modelMapper.map(b, BookDTO.class))
                .collect(Collectors.toList());
    }

    // READ by author
    public List<BookDTO> getBooksByAuthor(String author) {
        return bookRepository.getBooksByAuthor(author).stream()
                .map(b -> modelMapper.map(b, BookDTO.class))
                .collect(Collectors.toList());
    }

    // Check availability for IssueService
    public Integer getAvailableCopies(String bookId) {
        Book b = bookRepository.getBookById(bookId);
        return b != null ? b.getAvailableCopies() : 0;
    }

    // Decrement copies when book issued
    public void decrementAvailable(String bookId) {
        bookRepository.decrementAvailable(bookId);
    }

    // Increment copies when book returned
    public void incrementAvailable(String bookId) {
        bookRepository.incrementAvailable(bookId);
    }

    // UPDATE
    public boolean updateBook(String bookId, BookDTO dto) {
        Book existing = bookRepository.getBookById(bookId);
        if (existing == null)
            return false;

        existing.setTitle(dto.getTitle());
        existing.setAuthor(dto.getAuthor());
        existing.setPublisher(dto.getPublisher());
        existing.setPublishedYear(dto.getPublishedYear());
        existing.setTotalCopies(dto.getTotalCopies());
        existing.setAvailableCopies(dto.getAvailableCopies());

        return bookRepository.updateBook(existing);
    }

    // DELETE
    public boolean deleteBook(String bookId) {
        return bookRepository.deleteBookById(bookId);
    }

}
