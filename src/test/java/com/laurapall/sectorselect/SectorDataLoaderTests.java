package com.laurapall.sectorselect;

import com.laurapall.sectorselect.entity.Sector;
import com.laurapall.sectorselect.repository.SectorRepository;
import com.laurapall.sectorselect.service.SectorDataLoader;
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
import java.util.Collection;
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
		verify(repository, never()).saveAll(any());
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
	void testDataLoaderTriggersSaveAllInRepositoryWhenEmpty() throws IOException {
		when(repository.count()).thenReturn(0L);
		dataLoader.loadIfEmpty();
		verify(repository, times(1)).saveAll(any());
	}

	@Test
	void testDataLoaderAddsCorrectNumberOfSectors() throws IOException {
		when(repository.count()).thenReturn(0L);
		dataLoader.loadIfEmpty();

		ArgumentCaptor<Collection<Sector>> captor = ArgumentCaptor.forClass(Collection.class);
		verify(repository, times(1)).saveAll(captor.capture());

		Collection<Sector> saved = captor.getValue();

		Assertions.assertNotNull(saved);
		Assertions.assertEquals(4, saved.size());
	}

	@Test
	void testDataLoaderAddedSectorsHaveCorrectLevels() throws IOException {
		when(repository.count()).thenReturn(0L);
		dataLoader.loadIfEmpty();

		ArgumentCaptor<Collection<Sector>> captor = ArgumentCaptor.forClass(Collection.class);
		verify(repository, times(1)).saveAll(captor.capture());

		Collection<Sector> saved = captor.getValue();
		Map<Long, Sector> map = saved.stream().collect(Collectors.toMap(Sector::getId, s -> s));

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
