package com.project.library.Issues.controller;

import com.project.library.Issues.dto.IssueDTO;
import com.project.library.Issues.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/issues") // Base URL
public class IssueController {

    private final IssueService issueService;

    @Autowired
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    // ------------------- CREATE -------------------
    /*
     * POST http://localhost:9003/issues/add
     * Body:
     * {
     * "userId": "U001",
     * "bookId": "B001",
     * "issueDate": "2025-08-31",
     * "dueDate": "2025-09-10"
     * }
     */
    @PostMapping("/add")
    public ResponseEntity<IssueDTO> addIssue(@RequestBody IssueDTO dto) {
        try {
            IssueDTO created = issueService.addIssue(dto);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ------------------- READ -------------------
    /*
     * GET http://localhost:9003/issues/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<IssueDTO>> getAllIssues() {
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    /*
     * GET http://localhost:9003/issues/id/I001
     */
    @GetMapping("/id/{issueId}")
    public ResponseEntity<IssueDTO> getIssueById(@PathVariable String issueId) {
        IssueDTO dto = issueService.getIssueById(issueId);
        if (dto == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    /*
     * GET http://localhost:9003/issues/user/U001
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IssueDTO>> getIssuesByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(issueService.getIssuesByUserId(userId));
    }

    /*
     * GET http://localhost:9003/issues/bookname/Java Basics
     */
    @GetMapping("/bookname/{title}")
    public ResponseEntity<List<IssueDTO>> getIssuesByBookName(@PathVariable String title) {
        return ResponseEntity.ok(issueService.getIssuesByBookName(title));
    }

    // ------------------- UPDATE -------------------
    /*
     * PUT http://localhost:9003/issues/update/I001
     * Body:
     * {
     * "userId": "U002",
     * "bookId": "B002",
     * "issueDate": "2025-08-30",
     * "dueDate": "2025-09-15",
     * "returnDate": "2025-09-05"
     * }
     */
    @PutMapping("/update/{issueId}")
    public ResponseEntity<String> updateIssue(@PathVariable String issueId, @RequestBody IssueDTO dto) {
        boolean updated = issueService.updateIssue(issueId, dto);
        if (!updated)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Issue updated successfully.");
    }

    // ------------------- RETURN BOOK -------------------
    /*
     * PUT http://localhost:9003/issues/return/I001?returnDate=2025-09-05
     */
    @PutMapping("/return/{issueId}")
    public ResponseEntity<String> returnBook(@PathVariable String issueId,
            @RequestParam String returnDate) {
        boolean success = issueService.returnBook(issueId, returnDate);
        if (!success)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Book returned successfully.");
    }

    // ------------------- DELETE -------------------
    /*
     * DELETE http://localhost:9003/issues/delete/I001
     */
    @DeleteMapping("/delete/{issueId}")
    public ResponseEntity<String> deleteIssue(@PathVariable String issueId) {
        boolean deleted = issueService.deleteIssue(issueId);
        if (!deleted)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Issue deleted successfully.");
    }
}
