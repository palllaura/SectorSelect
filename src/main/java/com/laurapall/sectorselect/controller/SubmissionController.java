package com.laurapall.sectorselect.controller;

import com.laurapall.sectorselect.dto.SubmissionRequest;
import com.laurapall.sectorselect.dto.SubmissionResponse;
import com.laurapall.sectorselect.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
     * Create new submission or edit previous submission.
     * @param request submission request with data.
     * @return response entity.
     */
    @PostMapping("/submit")
    public ResponseEntity<SubmissionResponse> submit(@Valid @RequestBody SubmissionRequest request) {
        if (request.getEditSubmissionId() == null) {
            SubmissionResponse response = service.createSubmission(request);

            if (!response.isValid()) return ResponseEntity.badRequest().body(response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            SubmissionResponse response = service.updateCurrentSubmission(request);

            if (!response.isValid()) return ResponseEntity.badRequest().body(response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }
}
