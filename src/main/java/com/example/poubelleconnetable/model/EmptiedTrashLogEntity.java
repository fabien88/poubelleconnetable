package com.example.poubelleconnetable.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;

/**
 * Trash datastore entity.
 */
@Entity
public class EmptiedTrashLogEntity {

    @Parent
    private Key<TrashEntity> trash;

    @Id
    private Long id;

    @Index
    private Date date;

    public EmptiedTrashLogEntity() {
    }

    public EmptiedTrashLogEntity(final Key<TrashEntity> trash, final Date date) {
        this.trash = trash;
        this.date = date;
    }

    public Date getDate() {
        return this.date;
    }

}
