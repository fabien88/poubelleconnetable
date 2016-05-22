package com.example.poubelleconnetable.model;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

/**
 * Trash datastore entity.
 */
@Entity
public class TrashEntity {

    @Parent
    private Key<User> owner;

    @Id
    public String name;

    public Double volume;

    public TrashEntity() {
    }

    public TrashEntity(final Key<User> userKey, final String name, final Double volume) {
        this.owner = userKey;
        this.name = name;
        this.volume = volume;
    }

    public String getName() {
        return this.name;
    }

    public Double getVolume() {
        return this.volume;
    }
}
