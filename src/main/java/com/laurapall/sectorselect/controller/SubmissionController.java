package com.laurapall.sectorselect.controller;

import com.laurapall.sectorselect.dto.SubmissionRequest;
import com.laurapall.sectorselect.dto.SubmissionResponse;
import com.laurapall.sectorselect.service.SubmissionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/submission")
public class SubmissionController {

    private final SubmissionService service;

    /**
     * Controller constructor.
     * @param service submission service.
     */
    public SubmissionController(SubmissionService service) { this.service = service; }

    /**
     * Create new submission.
     * @param request submission request with data.
     * @param session Http session.
     * @return response entity.
     */
    @PostMapping
    public ResponseEntity<SubmissionResponse> create(
            @Valid @RequestBody SubmissionRequest request, HttpSession session) {
        SubmissionResponse response = service.createSubmission(request, session);

        if (!response.isValid()) return ResponseEntity.badRequest().body(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update current submission.
     * @param request submission request with data.
     * @param session Http session.
     * @return response entity.
     */
    @PutMapping("/current")
    public ResponseEntity<SubmissionResponse> update(
            @Valid @RequestBody SubmissionRequest request, HttpSession session) {
        SubmissionResponse response = service.updateCurrentSubmission(request, session);

        if (!response.isValid()) return ResponseEntity.badRequest().body(response);
        return ResponseEntity.ok(response);
    }
}
