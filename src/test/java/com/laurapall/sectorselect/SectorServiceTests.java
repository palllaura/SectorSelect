package com.laurapall.sectorselect;

import com.laurapall.sectorselect.dto.SectorTreeDto;
import com.laurapall.sectorselect.entity.Sector;
import com.laurapall.sectorselect.repository.SectorRepository;
import com.laurapall.sectorselect.service.SectorService;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorServiceTests {

	@Mock
	private SectorRepository repository;

	@InjectMocks
	private SectorService service;

	private LogCaptor logCaptor;


	@BeforeEach
	void setUp() {
		logCaptor = LogCaptor.forClass(SectorService.class);
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
	void testGetSectorTreeCorrectAmountOfRootsAndSectors() {
		when(repository.findAll()).thenReturn(getListOfSectors());
		service.getSectorTree();
		Assertions.assertTrue(
				logCaptor.getLogs().stream()
						.anyMatch(msg -> msg.contains("Built sector tree with 1 root(s) and 3 total sectors")),
				"Expected log was not found");
	}

	@Test
	void testGetSectorListCorrectRootSector() {
		when(repository.findAll()).thenReturn(getListOfSectors());
		List<SectorTreeDto> result = service.getSectorTree();
		Assertions.assertEquals(1L, result.get(0).getId());
	}

	@Test
	void testGetSectorListCorrectChildrenForSectors() {
		when(repository.findAll()).thenReturn(getListOfSectors());
		List<SectorTreeDto> result = service.getSectorTree();

		SectorTreeDto dto1 = result.get(0);
		List<SectorTreeDto> childrenOfFirst = dto1.getChildren();
		Assertions.assertEquals(1, childrenOfFirst.size());
		SectorTreeDto dto2 = childrenOfFirst.get(0);
		Assertions.assertEquals(2L, dto2.getId());

		List<SectorTreeDto> childrenOfSecond = dto2.getChildren();
		Assertions.assertEquals(1, childrenOfSecond.size());
		SectorTreeDto dto3 = childrenOfSecond.get(0);
		Assertions.assertEquals(3L, dto3.getId());

		Assertions.assertEquals(List.of(), dto3.getChildren());
	}

	@Test
	void testGetSectorListCorrectLevelsForSectors() {
		when(repository.findAll()).thenReturn(getListOfSectors());
		List<SectorTreeDto> result = service.getSectorTree();
		SectorTreeDto dto1 = result.get(0);
		SectorTreeDto dto2 = dto1.getChildren().get(0);
		SectorTreeDto dto3 = dto2.getChildren().get(0);

		Assertions.assertEquals(0, dto1.getLevel());
		Assertions.assertEquals(1, dto2.getLevel());
		Assertions.assertEquals(2, dto3.getLevel());
	}

	@Test
	void testGetSectorTreeNoSectorsReturnsEmptyList() {
		when(repository.findAll()).thenReturn(List.of());
		List<SectorTreeDto> result = service.getSectorTree();
		Assertions.assertTrue(result.isEmpty());
	}

}
