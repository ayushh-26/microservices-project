package com.project.library.Issues.service;

import com.project.library.Issues.dto.BookDTO;
import com.project.library.Issues.dto.IssueDTO;
import com.project.library.Issues.dto.IssueResponseDto;
import com.project.library.Issues.exception.*;
import com.project.library.Issues.irepository.IIssueRepository;
import com.project.library.Issues.model.Issue;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
                .baseUrl("http://localhost:9005/books")
                .build();
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-INTERNAL-SERVICE", "issueservice");
        return headers;
    }

    // ------------------- CREATE ISSUE -------------------
    public IssueResponseDto addIssue(IssueDTO dto) {
        if (dto == null) throw new ValidationFailedException("IssueDTO cannot be null");
        if (dto.getUserId() == null || dto.getBookId() == null || dto.getIssueDate() == null || dto.getDueDate() == null)
            throw new ValidationFailedException("UserId, BookId, IssueDate, DueDate are mandatory");

        try {
            Integer available = restClient.get()
                    .uri("/available/{bookId}", dto.getBookId())
                    .headers(headers -> headers.addAll(defaultHeaders()))
                    .retrieve()
                    .body(Integer.class);

            if (available == null) throw new DatabaseException("Book Service did not respond for available copies");
            if (available <= 0) throw new BookUnavailableException("No copies available for book: " + dto.getBookId());

            restClient.put()
                    .uri("/decrement/{bookId}", dto.getBookId())
                    .headers(headers -> headers.addAll(defaultHeaders()))
                    .retrieve()
                    .toBodilessEntity();

            Issue issue = modelMapper.map(dto, Issue.class);
            Issue created = issueRepository.addIssue(issue);

            return new IssueResponseDto(created.getIssueId(), created.getBookId(), created.getUserId(), "Success");

        } catch (BookUnavailableException | ValidationFailedException e) {
            throw e;
        } catch (RestClientException e) {
            throw new DatabaseException("Book Service unavailable: " + e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException("Failed to create issue: " + e.getMessage());
        }
    }

    // ------------------- GET ALL ISSUES -------------------
    public List<IssueDTO> getAllIssues() {
        try {
            return issueRepository.getAllIssues().stream()
                    .map(i -> modelMapper.map(i, IssueDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch issues: " + e.getMessage());
        }
    }

    // ------------------- GET ISSUE BY ID -------------------
    public IssueDTO getIssueById(String issueId) {
        if (issueId == null || issueId.isEmpty())
            throw new ValidationFailedException("IssueId cannot be null or empty");

        try {
            Issue issue = issueRepository.getIssueById(issueId);
            if (issue == null) throw new IssueNotFoundException("Issue not found: " + issueId);
            return modelMapper.map(issue, IssueDTO.class);
        } catch (IssueNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch issue: " + e.getMessage());
        }
    }

    // ------------------- GET ISSUES BY USER -------------------
    public List<IssueDTO> getIssuesByUserId(String userId) {
        if (userId == null || userId.isEmpty())
            throw new ValidationFailedException("UserId cannot be null or empty");

        try {
            List<Issue> issues = issueRepository.getIssuesByUserId(userId);
            if (issues.isEmpty()) throw new IssueNotFoundException("No issues found for userId: " + userId);
            return issues.stream().map(i -> modelMapper.map(i, IssueDTO.class)).collect(Collectors.toList());
        } catch (IssueNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch issues for user: " + e.getMessage());
        }
    }

    // ------------------- GET ISSUES BY BOOK TITLE -------------------
    public List<IssueDTO> getIssuesByBookName(String title) {
        if (title == null || title.isEmpty()) throw new ValidationFailedException("Book title cannot be empty");

        try {
            List<?> booksRaw = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/search").queryParam("title", title).build())
                    .headers(headers -> headers.addAll(defaultHeaders()))
                    .retrieve()
                    .body(List.class);

            if (booksRaw == null || booksRaw.isEmpty()) throw new IssueNotFoundException("No books found for title: " + title);

            List<BookDTO> books = booksRaw.stream()
                    .map(obj -> modelMapper.map(obj, BookDTO.class))
                    .collect(Collectors.toList());

            List<IssueDTO> issues = books.stream()
                    .flatMap(book -> issueRepository.getIssuesByBookId(book.getBookId()).stream())
                    .map(issue -> modelMapper.map(issue, IssueDTO.class))
                    .collect(Collectors.toList());

            if (issues.isEmpty()) throw new IssueNotFoundException("No issues found for books with title: " + title);

            return issues;

        } catch (IssueNotFoundException | ValidationFailedException e) {
            throw e;
        } catch (RestClientException e) {
            throw new DatabaseException("Book Service unavailable while fetching issues by title: " + e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch issues by book title: " + e.getMessage());
        }
    }

    // ------------------- UPDATE ISSUE -------------------
    public IssueResponseDto updateIssue(String issueId, IssueDTO dto) {
        if (dto == null) throw new ValidationFailedException("IssueDTO cannot be null");
        if (issueId == null || issueId.isEmpty())
            throw new ValidationFailedException("IssueId cannot be null or empty");

        try {
            Issue existing = issueRepository.getIssueById(issueId);
            if (existing == null) throw new IssueNotFoundException("Issue not found: " + issueId);

            existing.setBookId(dto.getBookId());
            existing.setUserId(dto.getUserId());
            existing.setIssueDate(dto.getIssueDate());
            existing.setDueDate(dto.getDueDate());
            existing.setReturnDate(dto.getReturnDate());

            boolean updated = issueRepository.updateIssue(existing);
            if (!updated) throw new DatabaseException("Failed to update issue: " + issueId);

            return new IssueResponseDto(existing.getIssueId(), existing.getBookId(), existing.getUserId(), "Success");

        } catch (IssueNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to update issue: " + e.getMessage());
        }
    }

    // ------------------- RETURN BOOK -------------------
    public boolean returnBook(String issueId, String returnDateStr) {
        if (issueId == null || issueId.isEmpty())
            throw new ValidationFailedException("IssueId cannot be null or empty");

        Issue existing = issueRepository.getIssueById(issueId);
        if (existing == null) throw new IssueNotFoundException("Issue not found: " + issueId);

        try {
            Date returnDate = Date.valueOf(returnDateStr);
            existing.setReturnDate(returnDate);

            boolean updated = issueRepository.updateIssue(existing);
            if (!updated) throw new DatabaseException("Failed to update return date for issue: " + issueId);

            restClient.put()
                    .uri("/increment/{bookId}", existing.getBookId())
                    .headers(headers -> headers.addAll(defaultHeaders()))
                    .retrieve()
                    .toBodilessEntity();

            return true;

        } catch (IllegalArgumentException e) {
            throw new ValidationFailedException("Invalid returnDate format. Required format: yyyy-[m]m-[d]d");
        } catch (RestClientException e) {
            throw new DatabaseException("Failed to increment book copies during return: " + existing.getBookId());
        } catch (Exception e) {
            throw new DatabaseException("Failed to return book: " + e.getMessage());
        }
    }

    // ------------------- DELETE ISSUE -------------------
    public IssueResponseDto deleteIssue(String issueId) {
        if (issueId == null || issueId.isEmpty())
            throw new ValidationFailedException("IssueId cannot be null or empty");

        try {
            Issue existing = issueRepository.getIssueById(issueId);
            if (existing == null) throw new IssueNotFoundException("Issue not found: " + issueId);

            if (existing.getReturnDate() == null) {
                restClient.put()
                        .uri("/increment/{bookId}", existing.getBookId())
                        .headers(headers -> headers.addAll(defaultHeaders()))
                        .retrieve()
                        .toBodilessEntity();
            }

            boolean deleted = issueRepository.deleteIssueById(issueId);
            if (!deleted) throw new DatabaseException("Failed to delete issue: " + issueId);

            return new IssueResponseDto(issueId, existing.getBookId(), existing.getUserId(), "Success");

        } catch (IssueNotFoundException e) {
            throw e;
        } catch (RestClientException e) {
            throw new DatabaseException("Failed to increment book copies during delete: " + e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException("Failed to delete issue: " + e.getMessage());
        }
    }

    // ------------------- PAGINATION -------------------
public Map<String, Object> getIssuesPaginated(int page, int size) {
    if (page < 0) throw new ValidationFailedException("Page index cannot be negative");
    if (size <= 0) throw new ValidationFailedException("Page size must be greater than zero");

    try {
        int totalElements = issueRepository.getTotalIssuesCount();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Handle page out-of-range
        if (totalPages == 0) {
            page = 0; // no data, default to page 0
        } else if (page >= totalPages) {
            throw new ValidationFailedException("Page index out of range. Total pages: " + totalPages);
        }

        int offset = page * size;
        List<IssueDTO> issues = issueRepository.getIssuesPaginated(offset, size).stream()
                .map(i -> modelMapper.map(i, IssueDTO.class))
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", totalPages);
        response.put("totalElements", totalElements);
        response.put("issues", issues);
        response.put("_status", "Success");

        return response;

    } catch (ValidationFailedException e) {
        throw e;
    } catch (Exception e) {
        throw new DatabaseException("Failed to fetch paginated issues: " + e.getMessage());
    }
}

}
