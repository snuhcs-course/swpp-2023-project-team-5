package com.imPine.imPineThankYou.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PlantResponseTest {

    private PlantResponse plantResponse;
    private List<Plant> testPlants;

    @Before
    public void setUp() {
        plantResponse = new PlantResponse();
        testPlants = new ArrayList<>();
        testPlants.add(new Plant("Plant1", 100, "image1.jpg"));
        testPlants.add(new Plant("Plant2", 200, "image2.jpg"));
    }

    @Test
    public void getPlants_returnsCorrectList() {
        plantResponse.setPlants(testPlants);
        List<Plant> plants = plantResponse.getPlants();

        assertNotNull("Plants list should not be null", plants);
        assertEquals("Plants list size should match", testPlants.size(), plants.size());
        assertEquals("Plants list contents should match", testPlants, plants);
    }

    @Test
    public void setPlants_setsListCorrectly() {
        plantResponse.setPlants(testPlants);
        assertEquals("Plants list should be set correctly", testPlants, plantResponse.getPlants());
    }
}
