package com.project.library.Issues.controller;

import com.project.library.Issues.dto.IssueDTO;
import com.project.library.Issues.dto.IssueResponseDto;
import com.project.library.Issues.exception.*;
import com.project.library.Issues.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/issues")
public class IssueController {

    private final IssueService issueService;

    @Autowired
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    // ------------------- CREATE ISSUE -------------------
    @PostMapping("/add")
    public ResponseEntity<IssueResponseDto> addIssue(@RequestBody IssueDTO dto) {
        try {
            IssueResponseDto response = issueService.addIssue(dto);
            return ResponseEntity.ok(response);
        } catch (ValidationFailedException e) {
            return ResponseEntity.badRequest().body(new IssueResponseDto(null, dto.getBookId(), dto.getUserId(), "Validation Failed: " + e.getMessage()));
        } catch (BookUnavailableException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new IssueResponseDto(null, dto.getBookId(), dto.getUserId(), e.getMessage()));
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new IssueResponseDto(null, dto.getBookId(), dto.getUserId(), e.getMessage()));
        }
    }

    // ------------------- GET ALL ISSUES -------------------
    @GetMapping("/all")
    public ResponseEntity<List<IssueDTO>> getAllIssues() {
        List<IssueDTO> issues = issueService.getAllIssues();
        return ResponseEntity.ok(issues);
    }

    // ------------------- GET ISSUE BY ID -------------------
    @GetMapping("/id/{issueId}")
    public ResponseEntity<?> getIssueById(@PathVariable String issueId) {
        try {
            IssueDTO issue = issueService.getIssueById(issueId);
            return ResponseEntity.ok(issue);
        } catch (IssueNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new IssueResponseDto(issueId, null, null, e.getMessage()));
        }
    }

    // ------------------- GET ISSUES BY USER -------------------
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getIssuesByUserId(@PathVariable String userId) {
        try {
            List<IssueDTO> issues = issueService.getIssuesByUserId(userId);
            return ResponseEntity.ok(issues);
        } catch (IssueNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new IssueResponseDto(null, null, userId, e.getMessage()));
        }
    }

    // ------------------- GET ISSUES BY BOOK NAME -------------------
    @GetMapping("/bookname/{title}")
    public ResponseEntity<?> getIssuesByBookName(@PathVariable String title) {
        try {
            List<IssueDTO> issues = issueService.getIssuesByBookName(title);
            return ResponseEntity.ok(issues);
        } catch (ValidationFailedException e) {
            return ResponseEntity.badRequest().body(new IssueResponseDto(null, title, null, e.getMessage()));
        } catch (IssueNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new IssueResponseDto(null, title, null, e.getMessage()));
        }
    }

    // ------------------- UPDATE ISSUE -------------------
    @PutMapping("/update/{issueId}")
    public ResponseEntity<IssueResponseDto> updateIssue(@PathVariable String issueId, @RequestBody IssueDTO dto) {
        try {
            IssueResponseDto response = issueService.updateIssue(issueId, dto);
            return ResponseEntity.ok(response);
        } catch (IssueNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new IssueResponseDto(issueId, dto.getBookId(), dto.getUserId(), e.getMessage()));
        }
    }

    // ------------------- RETURN BOOK -------------------
    @PutMapping("/return/{issueId}")
    public ResponseEntity<IssueResponseDto> returnBook(@PathVariable String issueId, @RequestParam String returnDate) {
        try {
            issueService.returnBook(issueId, returnDate);
            return ResponseEntity.ok(new IssueResponseDto(issueId, null, null, "Book returned successfully"));
        } catch (ValidationFailedException e) {
            return ResponseEntity.badRequest().body(new IssueResponseDto(issueId, null, null, e.getMessage()));
        } catch (IssueNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new IssueResponseDto(issueId, null, null, e.getMessage()));
        }
    }

    // ------------------- DELETE ISSUE -------------------
    @DeleteMapping("/delete/{issueId}")
    public ResponseEntity<IssueResponseDto> deleteIssue(@PathVariable String issueId) {
        try {
            IssueResponseDto response = issueService.deleteIssue(issueId);
            return ResponseEntity.ok(response);
        } catch (IssueNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new IssueResponseDto(issueId, null, null, e.getMessage()));
        }
    }

    @GetMapping("/nextpage")
public ResponseEntity<?> getIssuesPaginated(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size) {
    try {
        Map<String, Object> response = issueService.getIssuesPaginated(page, size);
        return ResponseEntity.ok(response);
    } catch (ValidationFailedException e) {
        // Out-of-range pages or invalid page/size will return 400
        return ResponseEntity.badRequest().body(Map.of(
                "_status", "Failed",
                "message", e.getMessage()
        ));
    } catch (DatabaseException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("_status", "Failed", "message", e.getMessage()));
    }
}


}
