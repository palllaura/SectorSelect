package com.laurapall.sectorselect;

import com.laurapall.sectorselect.controller.SectorController;

import com.laurapall.sectorselect.dto.SectorTreeDto;
import com.laurapall.sectorselect.service.SectorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorControllerTests {

    @Mock
    private SectorService service;

    @InjectMocks
    private SectorController controller;

    @Test
    void testGetSectorTreeTriggersCorrectMethodInService() {
        controller.getSectorTree();
        verify(service, times(1)).getSectorTree();
    }

    @Test
    void testGetSectorTreeReturnsServiceResult() {
        List<SectorTreeDto> expected = List.of(new SectorTreeDto(1L, "Sector 1", 0));
        when(service.getSectorTree()).thenReturn(expected);

        List<SectorTreeDto> result = controller.getSectorTree();
        Assertions.assertEquals(expected, result);
    }


}
