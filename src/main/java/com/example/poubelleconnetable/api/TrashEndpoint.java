
package com.example.poubelleconnetable.api;

import com.example.poubelleconnetable.service.TrashService;
import com.example.poubelleconnetable.utilities.Authentication;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;

import javax.inject.Named;

/**
 * Defines v1 of a trashManager API, which provides trash management methods.
 */
@Api(name = "trashManager",
        version = "v1",
        title = "Trash API",
        description = "Your personnal trash manager",
        scopes = {Configuration.EMAIL_SCOPE},
        clientIds = {Configuration.API_EXPLORER_CLIENT_ID,
                Configuration.WEB_CLIENT_ID,
        }
)
public class TrashEndpoint {

    TrashService trashService = new TrashService();

    /**
     * Create a new trash
     *
     * @param user      injected user if authenticated
     * @param trashName trash name to create
     * @param volume    volume of the trash to create
     *
     * @return created trash
     *
     * @throws UnauthorizedException if user is not authenticated
     * @throws ConflictException     if a trash with that name already exists
     */
    @ApiMethod(
            httpMethod = "POST",
            path = "me/trashes"
    )
    public Trash createTrash(
            final User user,
            @Named("trashName") final String trashName,
            @Named("volume") final Double volume)
            throws UnauthorizedException, ConflictException {

        Authentication.validateUser(user);

        return this.trashService.createTrash(user, trashName, volume);
    }

    /**
     * Delete a trash
     *
     * @param user      injected user if authenticated
     * @param trashName trash name
     *
     * @throws UnauthorizedException if user is not authenticated
     */
    @ApiMethod(
            path = "me/trashes/{trashName}"
    )
    public void deleteTrash(
            final User user,
            @Named("trashName") final String trashName
    )
            throws UnauthorizedException, ConflictException {

        Authentication.validateUser(user);

        this.trashService.deleteTrash(user, trashName);
    }

    /**
     * Empty a given trash
     *
     * @param user      injected user if authenticated
     * @param trashName trash to empty
     *
     * @throws UnauthorizedException if user is not authenticated
     * @throws NotFoundException     if the given trash doesn't exists
     */
    @ApiMethod(
            httpMethod = "POST",
            path = "me/trashes/{trashName}/empty"
    )
    public void emptyTrash(final User user, @Named("trashName") final String trashName)
            throws UnauthorizedException, NotFoundException {

        Authentication.validateUser(user);

        this.trashService.emptyTrash(user, trashName);
    }

    /**
     * List all trash a user own
     *
     * @param user injected user if authenticated
     *
     * @return List of user trash or empty list if it has none
     *
     * @throws UnauthorizedException if user is not authenticated
     */
    @ApiMethod(
            httpMethod = "GET",
            path = "me/trashes"
    )
    public CollectionResponse<Trash> listTrash(final User user)
            throws UnauthorizedException {

        Authentication.validateUser(user);

        return CollectionResponse.<Trash>builder().setItems(this.trashService.getTrashes(user)).build();
    }

    /**
     * List trash statistics of for a user trashes
     *
     * @param user
     *
     * @return list of trashes statistics
     *
     * @throws UnauthorizedException if user is not authenticated
     */
    @ApiMethod(
            httpMethod = "GET",
            path = "me/trashes/statistics"
    )
    public CollectionResponse<TrashStatistics> listTrashStatistics(final User user)
            throws UnauthorizedException {

        Authentication.validateUser(user);

        return CollectionResponse.<TrashStatistics>builder().setItems(this.trashService.getTrashStatistic(user)).build();
    }

}
