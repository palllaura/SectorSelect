package com.laurapall.sectorselect.service;

import com.laurapall.sectorselect.dto.SubmissionRequest;
import com.laurapall.sectorselect.dto.SubmissionResponse;
import com.laurapall.sectorselect.entity.Sector;
import com.laurapall.sectorselect.entity.Submission;
import com.laurapall.sectorselect.repository.SectorRepository;
import com.laurapall.sectorselect.repository.SubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

/**
 * Service class to handle all submission-related actions.
 */
@Service
public class SubmissionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionService.class);

    private final SubmissionRepository submissionRepository;
    private final SectorRepository sectorRepository;

    /**
     * Service constructor.
     * @param submissionRepository submission repository.
     * @param sectorRepository sector repository.
     */
    public SubmissionService(SubmissionRepository submissionRepository, SectorRepository sectorRepository) {
        this.submissionRepository = submissionRepository;
        this.sectorRepository = sectorRepository;
    }

    /**
     * Create and save new submission if request is valid.
     * @param request submission request.
     * @return submission response.
     */
    @Transactional
    public SubmissionResponse createSubmission(SubmissionRequest request) {
        if (!validateRequest(request)) return invalidResponse();

        List<Long> ids = request.getSectorIds();
        List<Sector> sectors = sectorRepository.findAllById(ids);

        Submission submission = new Submission();
        submission.setName(request.getName().trim());
        submission.setSectors(new LinkedHashSet<>(sectors));
        submissionRepository.save(submission);

        LOGGER.info("Successfully created submission with id: {}", submission.getId());
        return toResponse(submission);
    }

    /**
     * Update current submission if request is valid.
     * @param request submission request.
     * @return submission response.
     */
    @Transactional
    public SubmissionResponse updateCurrentSubmission(SubmissionRequest request) {
        Optional<Submission> submissionOptional = Optional.empty();
        if (request.getEditSubmissionId() != null) {
            submissionOptional = submissionRepository.findById(request.getEditSubmissionId());
        }
        if (submissionOptional.isEmpty() || !validateRequest(request)) return invalidResponse();

        List<Long> ids = request.getSectorIds();
        List<Sector> sectors = sectorRepository.findAllById(ids);

        Submission submission = submissionOptional.get();
        submission.setName(request.getName().trim());
        submission.setSectors(new LinkedHashSet<>(sectors));
        submissionRepository.save(submission);
        LOGGER.info("Successfully updated submission with id: {}", submission.getId());
        return toResponse(submission);
    }

    /**
     * Validate request fields.
     * @param req submission request.
     * @return true if request is valid, else false.
     */
    private boolean validateRequest(SubmissionRequest req) {
        boolean isValid = true;

        if (req.getName() == null || req.getName().trim().isEmpty()) {
            isValid = false;
            LOGGER.warn("Incorrect submission: name is required");
        }
        if (req.getSectorIds() == null || req.getSectorIds().isEmpty()) {
            isValid = false;
            LOGGER.warn("Incorrect submission: at least one sector must be selected");
        }
        if (!Boolean.TRUE.equals(req.getAgree())) {
            isValid = false;
            LOGGER.warn("Incorrect submission: must agree to terms");
        }
        return isValid;
    }

    /**
     * Create new submission response from submission.
     * @param submission submission.
     * @return submission response.
     */
    private SubmissionResponse toResponse(Submission submission) {
        SubmissionResponse response = new SubmissionResponse();
        response.setValid(true);
        response.setId(submission.getId());
        response.setName(submission.getName());
        response.setSectorIds(submission.getSectors().stream().map(Sector::getId).toList());
        return response;
    }

    /**
     * Create a response for invalid submission.
     * @return response.
     */
    private SubmissionResponse invalidResponse() {
        SubmissionResponse response = new SubmissionResponse();
        response.setValid(false);
        return response;
    }
}

