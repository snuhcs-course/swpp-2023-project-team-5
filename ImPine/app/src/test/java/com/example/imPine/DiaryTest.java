package com.example.imPine;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DiaryTest {

    private Diary diary;

    @Before
    public void setUp() throws Exception {
        diary = new Diary("1", "Test Title", "Test Description", false);
    }

    @After
    public void tearDown() throws Exception {
        diary = null;
    }

    @Test
    public void getId_returnsCorrectId() {
        assertEquals("1", diary.getId());
    }

    @Test
    public void setId_setsCorrectId() {
        diary.setId("2");
        assertEquals("2", diary.getId());
    }

    @Test
    public void getTitle_returnsCorrectTitle() {
        assertEquals("Test Title", diary.getTitle());
    }

    @Test
    public void setTitle_setsCorrectTitle() {
        diary.setTitle("New Title");
        assertEquals("New Title", diary.getTitle());
    }

    @Test
    public void getDescription_returnsCorrectDescription() {
        assertEquals("Test Description", diary.getDescription());
    }

    @Test
    public void setDescription_setsCorrectDescription() {
        diary.setDescription("New Description");
        assertEquals("New Description", diary.getDescription());
    }

    @Test
    public void isCompleted_returnsFalseWhenNotCompleted() {
        assertFalse(diary.isCompleted());
    }

    @Test
    public void setCompleted_setsCompletionToTrue() {
        diary.setCompleted(true);
        assertTrue(diary.isCompleted());
    }
}
