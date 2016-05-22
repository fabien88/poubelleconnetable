package com.example.poubelleconnetable.api;

import java.util.Date;
import java.util.List;

/**
 * Refer to all emptying statistic for a trash
 */
public class TrashStatistics {

    private Trash trash;

    /**
     * Dates when trash was emptied
     */
    private List<Date> emptyDates;

    public TrashStatistics() {
    }

    public TrashStatistics(final Trash trash, final List<Date> emptyDates) {
        this.trash = trash;
        this.emptyDates = emptyDates;
    }

    public Trash getTrash() {
        return this.trash;
    }

    public List<Date> getEmptyDates() {
        return this.emptyDates;
    }
}
