package com.example.poubelleconnetable.api;

import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TrashStatisticsTest {
    @Test
    public void getTrash() throws Exception {
        final Trash trash = new Trash("user1", 5D);
        ReflectionAssert.assertReflectionEquals(
                trash,
                new TrashStatistics(
                        trash,
                        Arrays.asList(new Date(), new Date(345837583))
                ).getTrash()
        );
    }

    @Test
    public void getEmptyDates() throws Exception {
        final List<Date> dates = Arrays.asList(new Date(), new Date(345837583));
        ReflectionAssert.assertReflectionEquals(dates, new TrashStatistics(null, dates).getEmptyDates());
    }

}