package com.example.imPine;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

import com.example.imPine.Diary;
import com.example.imPine.DiaryAdapter;

public class DiaryAdapterTest {

    private DiaryAdapter adapterWithEmptyList;
    private DiaryAdapter adapterWithThreeItems;

    @Before
    public void setUp() throws Exception {
        adapterWithEmptyList = new DiaryAdapter(Collections.emptyList());
        adapterWithThreeItems = new DiaryAdapter(Arrays.asList(
                new Diary("Title1", "Desc1", false),
                new Diary("Title2", "Desc2", false),
                new Diary("Title3", "Desc3", false)
        ));
    }

    @Test
    public void getItemCount_withEmptyList_returnsZero() {
        assertEquals(0, adapterWithEmptyList.getItemCount());
    }

    @Test
    public void getItemCount_withThreeItems_returnsThree() {
        assertEquals(3, adapterWithThreeItems.getItemCount());
    }
}
