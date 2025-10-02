package com.laurapall.sectorselect;

import com.laurapall.sectorselect.entity.Sector;
import com.laurapall.sectorselect.repository.SectorRepository;
import com.laurapall.sectorselect.service.SectorDataLoader;
import jakarta.persistence.EntityManager;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorDataLoaderTests {

	@Mock
	private EntityManager entityManager;

	@Mock
	private SectorRepository repository;

	@InjectMocks
	private SectorDataLoader dataLoader;

	private LogCaptor logCaptor;

	@BeforeEach
	void setUp() {
		logCaptor = LogCaptor.forClass(SectorDataLoader.class);
	}

	@Test
	void testDataLoaderSkipsWhenRepositoryHasSectors() throws IOException {
		when(repository.count()).thenReturn(5L);

		dataLoader.loadIfEmpty();
		verify(entityManager, never()).persist(any());
	}

	@Test
	void testDataLoaderSkipsCorrectLog() throws IOException {
		when(repository.count()).thenReturn(5L);

		dataLoader.loadIfEmpty();
		Assertions.assertTrue(
				logCaptor.getLogs().stream()
						.anyMatch(msg -> msg.contains("Sectors already present; skipping load.")),
				"Expected warning log was not found"
		);
	}


	@Test
	void testDataLoaderPersistsEntitiesWhenEmpty() throws IOException {
		when(repository.count()).thenReturn(0L);
		dataLoader.loadIfEmpty();
		verify(entityManager, times(4)).persist(any(Sector.class));
	}

	@Test
	void testDataLoaderAddsCorrectNumberOfSectors() throws IOException {
		when(repository.count()).thenReturn(0L);

		dataLoader.loadIfEmpty();

		ArgumentCaptor<Sector> captor = ArgumentCaptor.forClass(Sector.class);
		verify(entityManager, times(4)).persist(captor.capture());

		List<Sector> persisted = captor.getAllValues();

		Assertions.assertNotNull(persisted);
		Assertions.assertEquals(4, persisted.size());
	}

	@Test
	void testDataLoaderAddedSectorsHaveCorrectLevels() throws IOException {
		when(repository.count()).thenReturn(0L);

		dataLoader.loadIfEmpty();

		ArgumentCaptor<Sector> captor = ArgumentCaptor.forClass(Sector.class);
		verify(entityManager, times(4)).persist(captor.capture());
		List<Sector> persisted = captor.getAllValues();

		Map<Long, Sector> map = persisted.stream().collect(Collectors.toMap(Sector::getId, s -> s));

		Assertions.assertEquals(0, map.get(1L).getLevel());
		Assertions.assertEquals(1, map.get(6L).getLevel());
		Assertions.assertEquals(2, map.get(342L).getLevel());
	}

	@Test
	void testDataLoaderLogsWarningForInvalidParent() throws IOException {
		when(repository.count()).thenReturn(0L);

		dataLoader.loadIfEmpty();

		Assertions.assertTrue(
				logCaptor.getWarnLogs().stream()
						.anyMatch(msg -> msg.contains("Parent id 2 not found for child 444")),
				"Expected warning for missing parent not found in logs"
		);
	}

}
