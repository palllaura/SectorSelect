package com.laurapall.sectorselect;

import com.laurapall.sectorselect.dto.SubmissionRequest;
import com.laurapall.sectorselect.dto.SubmissionResponse;
import com.laurapall.sectorselect.entity.Sector;
import com.laurapall.sectorselect.entity.Submission;
import com.laurapall.sectorselect.repository.SectorRepository;
import com.laurapall.sectorselect.repository.SubmissionRepository;
import com.laurapall.sectorselect.service.SubmissionService;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTests {

	@Mock
	private SubmissionRepository submissionRepository;

	@Mock
	private SectorRepository sectorRepository;

	@InjectMocks
	private SubmissionService service;

	private LogCaptor logCaptor;

	private MockHttpSession session;

	@BeforeEach
	void setUp() {
		logCaptor = LogCaptor.forClass(SubmissionService.class);
		session = new MockHttpSession();
		session.setAttribute("submissionId", 123L);
	}

	/**
	 * Helper method to create a valid submission request.
	 * @return request.
	 */
	SubmissionRequest createValidSubmissionRequest() {
		SubmissionRequest request = new SubmissionRequest();
		request.setName("Client Name");
		request.setSectorIds(List.of(1L, 2L, 3L));
		request.setAgree(true);
		return request;
	}

	/**
	 * Helper method to create a list with sample sectors.
	 * @return sample sectors in a list.
	 */
	List<Sector> getListOfSectors() {
		Sector sector1 = new Sector();
		sector1.setId(1L);
		sector1.setName("Sector 1");
		sector1.setLevel(0);

		Sector sector2 = new Sector();
		sector2.setId(2L);
		sector2.setName("Sector 2");
		sector2.setLevel(1);
		sector2.setParent(sector1);

		Sector sector3 = new Sector();
		sector3.setId(3L);
		sector3.setName("Sector 3");
		sector3.setLevel(2);
		sector3.setParent(sector2);

		sector1.getChildren().add(sector2);
		sector2.getChildren().add(sector3);

		return List.of(sector1, sector2, sector3);
	}

	@Test
	void testCreateSubmissionCorrectReturnsValidResponse() {
		when(sectorRepository.findAllById(any())).thenReturn(getListOfSectors());
		SubmissionResponse result = service.createSubmission(createValidSubmissionRequest(), session);
		Assertions.assertTrue(result.isValid());
	}

	@Test
	void testCreateSubmissionCorrectSavedInRepository() {
		List<Sector> sectors = getListOfSectors();
		when(sectorRepository.findAllById(any())).thenReturn(sectors);

		service.createSubmission(createValidSubmissionRequest(), session);
		verify(submissionRepository, times(1)).save(any(Submission.class));
	}

	@Test
	void testCreateSubmissionCorrectCorrectDataSaved() {
		List<Sector> sectors = getListOfSectors();
		when(sectorRepository.findAllById(any())).thenReturn(sectors);
		service.createSubmission(createValidSubmissionRequest(), session);

		ArgumentCaptor<Submission> captor = ArgumentCaptor.forClass(Submission.class);
		verify(submissionRepository, times(1)).save(captor.capture());

		Submission saved = captor.getValue();

		Assertions.assertEquals("Client Name", saved.getName());
		Assertions.assertEquals(new HashSet<>(sectors), saved.getSectors());
	}

	@Test
	void testCreateSubmissionValidationFailsIfNameIsMissing() {
		SubmissionRequest request = createValidSubmissionRequest();
		request.setName(null);

		SubmissionResponse result = service.createSubmission(request, session);
		Assertions.assertFalse(result.isValid());

		Assertions.assertTrue(
				logCaptor.getLogs().stream()
						.anyMatch(msg -> msg.contains("Incorrect submission: name is required")),
				"Expected log was not found");
	}

	@Test
	void testCreateSubmissionValidationFailsIfNameIsBlank() {
		SubmissionRequest request = createValidSubmissionRequest();
		request.setName("      ");

		SubmissionResponse result = service.createSubmission(request, session);
		Assertions.assertFalse(result.isValid());

		Assertions.assertTrue(
				logCaptor.getLogs().stream()
						.anyMatch(msg -> msg.contains("Incorrect submission: name is required")),
				"Expected log was not found");
	}

	@Test
	void testCreateSubmissionValidationFailsIfSectorsAreMissing() {
		SubmissionRequest request = createValidSubmissionRequest();
		request.setSectorIds(null);

		SubmissionResponse result = service.createSubmission(request, session);
		Assertions.assertFalse(result.isValid());

		Assertions.assertTrue(
				logCaptor.getLogs().stream()
						.anyMatch(msg -> msg.contains("Incorrect submission: at least one sector must be selected")),
				"Expected log was not found");
	}

	@Test
	void testCreateSubmissionValidationFailsIfNoSectorsAreSelected() {
		SubmissionRequest request = createValidSubmissionRequest();
		request.setSectorIds(List.of());

		SubmissionResponse result = service.createSubmission(request, session);
		Assertions.assertFalse(result.isValid());

		Assertions.assertTrue(
				logCaptor.getLogs().stream()
						.anyMatch(msg -> msg.contains("Incorrect submission: at least one sector must be selected")),
				"Expected log was not found");
	}

	@Test
	void testCreateSubmissionValidationFailsIfAgreeToTermsIsFalse() {
		SubmissionRequest request = createValidSubmissionRequest();
		request.setAgree(false);

		SubmissionResponse result = service.createSubmission(request, session);
		Assertions.assertFalse(result.isValid());

		Assertions.assertTrue(
				logCaptor.getLogs().stream()
						.anyMatch(msg -> msg.contains("Incorrect submission: must agree to terms")),
				"Expected log was not found");
	}

	@Test
	void testUpdateCurrentSubmissionCorrectReturnsValidResponse() {
		Submission submission = new Submission();
		when(submissionRepository.findById(123L)).thenReturn(Optional.of(submission));
		when(sectorRepository.findAllById(any())).thenReturn(getListOfSectors());

		SubmissionResponse result = service.updateCurrentSubmission(createValidSubmissionRequest(), session);
		Assertions.assertTrue(result.isValid());
	}

	@Test
	void testUpdateCurrentSubmissionCorrectSavedInRepository() {
		Submission submission = new Submission();
		when(submissionRepository.findById(123L)).thenReturn(Optional.of(submission));
		when(sectorRepository.findAllById(any())).thenReturn(getListOfSectors());

		service.updateCurrentSubmission(createValidSubmissionRequest(), session);
		verify(submissionRepository, times(1)).save(any(Submission.class));
	}

	@Test
	void testUpdateCurrentSubmissionCorrectDataSaved() {
		List<Sector> sectors = getListOfSectors();
		Submission submission = new Submission();
		when(submissionRepository.findById(123L)).thenReturn(Optional.of(submission));
		when(sectorRepository.findAllById(any())).thenReturn(sectors);

		service.updateCurrentSubmission(createValidSubmissionRequest(), session);

		ArgumentCaptor<Submission> captor = ArgumentCaptor.forClass(Submission.class);
		verify(submissionRepository, times(1)).save(captor.capture());

		Submission saved = captor.getValue();

		Assertions.assertEquals("Client Name", saved.getName());
		Assertions.assertEquals(new HashSet<>(sectors), saved.getSectors());
	}

	@Test
	void testUpdateCurrentSubmissionInvalidResponseWhenIncorrectSession() {
		when(submissionRepository.findById(123L)).thenReturn(Optional.empty());

		SubmissionResponse result = service.updateCurrentSubmission(createValidSubmissionRequest(), session);
		Assertions.assertFalse(result.isValid());
	}

	@Test
	void testUpdateCurrentSubmissionNotSavedWhenValidationFails() {
		Submission submission = new Submission();
		when(submissionRepository.findById(123L)).thenReturn(Optional.of(submission));

		SubmissionRequest request = createValidSubmissionRequest();
		request.setAgree(false);

		service.updateCurrentSubmission(request, session);
		verifyNoMoreInteractions(submissionRepository);
	}

}
