package com.laurapall.sectorselect;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Test
    void testGetSectorTreeReturnsCorrectJson() throws Exception {
        SectorTreeDto child = new SectorTreeDto(5L, "Printing", 1);
        SectorTreeDto child2 = new SectorTreeDto(6L, "Food and Beverage", 1);
        child2.getChildren().add(new SectorTreeDto(342L, "Bakery & confectionery products", 2));

        SectorTreeDto root1 = new SectorTreeDto(1L, "Manufacturing", 0);
        root1.getChildren().add(child);
        root1.getChildren().add(child2);

        SectorTreeDto root2 = new SectorTreeDto(2L, "Service", 0);

        List<SectorTreeDto> expected = List.of(root1, root2);

        when(service.getSectorTree()).thenReturn(expected);

        List<SectorTreeDto> result = controller.getSectorTree();

        ObjectMapper mapper = new ObjectMapper();
        String resultJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);

        String expectedJson = """
                [
                  {
                    "id": 1,
                    "name": "Manufacturing",
                    "level": 0,
                    "children": [
                      {
                        "id": 5,
                        "name": "Printing",
                        "level": 1,
                        "children": []
                      },
                      {
                        "id": 6,
                        "name": "Food and Beverage",
                        "level": 1,
                        "children": [
                          {
                            "id": 342,
                            "name": "Bakery & confectionery products",
                            "level": 2,
                            "children": []
                          }
                        ]
                      }
                    ]
                  },
                  {
                    "id": 2,
                    "name": "Service",
                    "level": 0,
                    "children": []
                  }
                ]
                """;

        Assertions.assertEquals(mapper.readTree(expectedJson), mapper.readTree(resultJson));
    }

}
