package com.example.poubelleconnetable.api;

import org.junit.Assert;
import org.junit.Test;

public class TrashTest {
    @Test
    public void getVolume() throws Exception {
        Assert.assertEquals(new Double(5D), new Trash("foo", 5D).getVolume());
        Assert.assertEquals(null, new Trash("foo", null).getVolume());
    }

    @Test
    public void getName() throws Exception {
        Assert.assertEquals("foo", new Trash("foo", 5D).getName());
        Assert.assertEquals(null, new Trash(null, null).getVolume());
    }

}