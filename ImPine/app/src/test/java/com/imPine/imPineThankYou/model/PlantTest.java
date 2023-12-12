package com.imPine.imPineThankYou.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PlantTest {

    private Plant plant;

    @Before
    public void setUp() {
        plant = new Plant("Pine", 15, "image.jpg");
        plant.setPlant_id(1);
        plant.setAvatar(10);
        plant.setLast_watered("2023-03-20");
        plant.setStatus("Healthy");
    }

    @Test
    public void testGetName() {
        assertEquals("Pine", plant.getName());
    }

    @Test
    public void testGetHeight() {
        assertEquals(15, plant.getHeight());
    }

    @Test
    public void testGetImage() {
        assertEquals("image.jpg", plant.getImage());
    }

    @Test
    public void testGetPlantId() {
        assertEquals(1, plant.getPlant_id());
    }

    @Test
    public void testGetAvatar() {
        assertEquals(10, plant.getAvatar());
    }

    @Test
    public void testGetLastWatered() {
        assertEquals("2023-03-20", plant.getLast_watered());
    }

    @Test
    public void testGetStatus() {
        assertEquals("Healthy", plant.getStatus());
    }

    @Test
    public void testSetHeight() {
        plant.setHeight(20);
        assertEquals(20, plant.getHeight());
    }

    @Test
    public void testSetLastWatered() {
        plant.setLast_watered("2023-04-20");
        assertEquals("2023-04-20", plant.getLast_watered());
    }

    @Test
    public void testSetName() {
        plant.setName("New Pine");
        assertEquals("New Pine", plant.getName());
    }

    @Test
    public void testSetStatus() {
        plant.setStatus("Unhealthy");
        assertEquals("Unhealthy", plant.getStatus());
    }
}
