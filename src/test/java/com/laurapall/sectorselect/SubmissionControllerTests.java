package com.laurapall.sectorselect;

import com.laurapall.sectorselect.controller.SubmissionController;
import com.laurapall.sectorselect.dto.SubmissionRequest;
import com.laurapall.sectorselect.dto.SubmissionResponse;
import com.laurapall.sectorselect.service.SubmissionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerTests {

    @Mock
    private SubmissionService service;

    @InjectMocks
    private SubmissionController controller;

    @Test
    void testCreateCorrectReturnsValidResult() {
        SubmissionResponse response = new SubmissionResponse();
        response.setValid(true);

        when(service.createSubmission(any(SubmissionRequest.class)))
                .thenReturn(response);

        ResponseEntity<SubmissionResponse> result = controller.submit(new SubmissionRequest());

        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
        Assertions.assertSame(response, result.getBody());
    }

    @Test
    void testCreateReturnsBadRequestWhenInvalid() {
        SubmissionResponse response = new SubmissionResponse();
        response.setValid(false);

        when(service.createSubmission(any(SubmissionRequest.class)))
                .thenReturn(response);

        ResponseEntity<SubmissionResponse> result = controller.submit(new SubmissionRequest());

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Assertions.assertSame(response, result.getBody());
    }

    @Test
    void testUpdateReturnsOkWhenValid() {
        SubmissionResponse response = new SubmissionResponse();
        response.setValid(true);

        when(service.updateCurrentSubmission(any(SubmissionRequest.class)))
                .thenReturn(response);

        SubmissionRequest request = new SubmissionRequest();
        request.setEditSubmissionId(123L);

        ResponseEntity<SubmissionResponse> result = controller.submit(request);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertSame(response, result.getBody());
    }

    @Test
    void testUpdateReturnsBadRequestWhenInvalid() {
        SubmissionResponse response = new SubmissionResponse();
        response.setValid(false);

        when(service.updateCurrentSubmission(any(SubmissionRequest.class)))
                .thenReturn(response);

        SubmissionRequest request = new SubmissionRequest();
        request.setEditSubmissionId(123L);

        ResponseEntity<SubmissionResponse> result = controller.submit(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Assertions.assertSame(response, result.getBody());
    }
}
