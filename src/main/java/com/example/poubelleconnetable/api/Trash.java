package com.example.poubelleconnetable.api;

/**
 * Trash resource. This class is used in the Backend Cloud Endpoints API and is projected to the
 * clients in the generated client libraries.
 */
public class Trash {

    private String name;

    private Double volume;

    public Trash() {
    }

    /**
     * @param name   The trash name
     * @param volume The trash volume
     */
    public Trash(final String name, final Double volume) {
        this.name = name;
        this.volume = volume;
    }

    /**
     * @return the trash volume
     */
    public Double getVolume() {
        return this.volume;
    }

    /**
     * @return the trash name
     */
    public String getName() {
        return this.name;
    }
}
