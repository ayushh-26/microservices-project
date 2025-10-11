package com.project.library.Issues.service;

import com.project.library.Issues.dto.IssueDTO;
import com.project.library.Issues.dto.BookDTO;
import com.project.library.Issues.irepository.IIssueRepository;
import com.project.library.Issues.model.Issue;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IssueService {

    private final IIssueRepository issueRepository;
    private final ModelMapper modelMapper;
    private final RestClient restClient;

    @Autowired
    public IssueService(IIssueRepository issueRepository, ModelMapper modelMapper) {
        this.issueRepository = issueRepository;
        this.modelMapper = modelMapper;
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:9002/books")
                .build();
    }

    // ------------------- CREATE ISSUE -------------------
    public IssueDTO addIssue(IssueDTO dto) {
        // 1. Check available copies
        Integer available = restClient.get()
                .uri("/available/{bookId}", dto.getBookId())
                .retrieve()
                .body(Integer.class);

        if (available == null || available <= 0) {
            throw new RuntimeException("No copies available for book: " + dto.getBookId());
        }

        // 2. Decrement available copies
        try {
            restClient.put()
                    .uri("/decrement/{bookId}", dto.getBookId())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to decrement book copies: " + dto.getBookId(), e);
        }

        // 3. Save issue record
        Issue issue = modelMapper.map(dto, Issue.class);
        Issue created = issueRepository.addIssue(issue);

        return modelMapper.map(created, IssueDTO.class);
    }

    // ------------------- PAGINATION -------------------
public Map<String, Object> getIssuesPaginated(int page, int size) {
    int offset = page * size;
    int totalElements = issueRepository.getTotalIssuesCount();
    int totalPages = (int) Math.ceil((double) totalElements / size);

    List<IssueDTO> issues = issueRepository.getIssuesPaginated(offset, size).stream()
            .map(i -> modelMapper.map(i, IssueDTO.class))
            .collect(Collectors.toList());

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("page", page);
    response.put("size", size);
    response.put("totalPages", totalPages);
    response.put("totalElements", totalElements);
    response.put("issues", issues);
    return response;
}


    // ------------------- RETURN BOOK -------------------
    public boolean returnBook(String issueId, String returnDateStr) {
        Issue existing = issueRepository.getIssueById(issueId);
        if (existing == null)
            return false;

        // Convert String to java.sql.Date
        Date returnDate = Date.valueOf(returnDateStr);

        // Update return date
        existing.setReturnDate(returnDate);
        boolean updated = issueRepository.updateIssue(existing);

        if (!updated)
            return false;

        // Increment available copies in BookService
        try {
            restClient.put()
                    .uri("/increment/{bookId}", existing.getBookId())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to increment book copies: " + existing.getBookId(), e);
        }

        return true;
    }

    // ------------------- READ -------------------
    public List<IssueDTO> getAllIssues() {
        return issueRepository.getAllIssues().stream()
                .map(i -> modelMapper.map(i, IssueDTO.class))
                .collect(Collectors.toList());
    }

    public IssueDTO getIssueById(String issueId) {
        Issue issue = issueRepository.getIssueById(issueId);
        return issue != null ? modelMapper.map(issue, IssueDTO.class) : null;
    }

    public List<IssueDTO> getIssuesByUserId(String userId) {
        return issueRepository.getIssuesByUserId(userId).stream()
                .map(i -> modelMapper.map(i, IssueDTO.class))
                .collect(Collectors.toList());
    }

    // ------------------- GET ISSUES BY BOOK TITLE -------------------
    public List<IssueDTO> getIssuesByBookName(String title) {
        List<?> booksRaw = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search").queryParam("title", title).build())
                .retrieve()
                .body(List.class);

        if (booksRaw == null || booksRaw.isEmpty())
            return List.of();

        List<BookDTO> books = booksRaw.stream()
                .map(obj -> modelMapper.map(obj, BookDTO.class))
                .collect(Collectors.toList());

        return books.stream()
                .flatMap(book -> issueRepository.getIssuesByBookId(book.getBookId()).stream())
                .map(issue -> modelMapper.map(issue, IssueDTO.class))
                .collect(Collectors.toList());
    }

    // ------------------- UPDATE ISSUE -------------------
    public boolean updateIssue(String issueId, IssueDTO dto) {
        Issue existing = issueRepository.getIssueById(issueId);
        if (existing == null)
            return false;

        existing.setUserId(dto.getUserId());
        existing.setBookId(dto.getBookId());
        existing.setIssueDate(dto.getIssueDate());
        existing.setDueDate(dto.getDueDate());
        existing.setReturnDate(dto.getReturnDate());

        return issueRepository.updateIssue(existing);
    }

    // ------------------- DELETE ISSUE -------------------
    public boolean deleteIssue(String issueId) {
        Issue existing = issueRepository.getIssueById(issueId);
        if (existing == null)
            return false;

        // If the book was not returned yet, increment available copies
        if (existing.getReturnDate() == null) {
            try {
                restClient.put()
                        .uri("/increment/{bookId}", existing.getBookId())
                        .retrieve()
                        .toBodilessEntity();
            } catch (RestClientException e) {
                throw new RuntimeException("Failed to increment book copies during delete: " + existing.getBookId(), e);
            }
        }

        return issueRepository.deleteIssueById(issueId);
    }
}
