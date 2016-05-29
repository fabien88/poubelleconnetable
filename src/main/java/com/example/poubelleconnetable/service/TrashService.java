package com.example.poubelleconnetable.service;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.example.poubelleconnetable.api.Trash;
import com.example.poubelleconnetable.api.TrashStatistics;
import com.example.poubelleconnetable.model.EmptiedTrashLogEntity;
import com.example.poubelleconnetable.model.TrashEntity;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;

/**
 * Service for managing trash.
 */
public class TrashService {

	private static final Logger LOG = Logger.getLogger(TrashService.class.getName());

	/**
	 * Helper to create user key
	 *
	 * @param user
	 *
	 * @return
	 */
	private Key<User> createUserKey(final User user) {
		return Key.create(User.class, user.getEmail());
	}

	/**
	 * Helper to create trash key with parent user key
	 *
	 * @param user
	 * @param trashName
	 *
	 * @return
	 */
	private Key<TrashEntity> createTrashKey(final User user, final String trashName) {
		return Key.create(createUserKey(user), TrashEntity.class, trashName);
	}

	/**
	 * create a trash and persist it in Datastore
	 *
	 * @param user
	 *            user
	 * @param trashName
	 *            trashName
	 * @param volume
	 *            trash volume
	 *
	 * @return created trash
	 *
	 * @throws ConflictException
	 *             if a same nammed trash already exists for this user
	 */
	public Trash createTrash(final User user, final String trashName, final Double volume) throws ConflictException {

		if (trashExists(user, trashName)) {
			throw new ConflictException("User has already a trash with that name");
		}

		if (volume == null) {
			throw new IllegalArgumentException("Volume should not be null");
		}
		if (trashName == null) {
			throw new IllegalArgumentException("Thrash name should not be null");
		}

		final TrashEntity trash = new TrashEntity(
				createUserKey(user),
				trashName,
				volume);

		ofy().save().entity(trash).now();

		return new Trash(trash.getName(), trash.getVolume());
	}

	/**
	 * Check if a trash with that name and user exists in datastore
	 *
	 * @param user
	 *            user
	 * @param trashName
	 *            trash name
	 *
	 * @return true if it does
	 */
	private boolean trashExists(final User user, final String trashName) {
		return ofy().load().filterKey(createTrashKey(user, trashName)).count() != 0;
	}

	/**
	 * Log empty trash operation in a dedicated entity in datastores
	 *
	 * @param user
	 *            user
	 * @param trashName
	 *            trash name
	 *
	 * @throws NotFoundException
	 *             if the trash to empty doesn't exists
	 */
	public void emptyTrash(final User user, final String trashName)
			throws NotFoundException {

		if (!trashExists(user, trashName)) {
			throw new NotFoundException("This trash doesn't exist");
		}

		final EmptiedTrashLogEntity emptiedTrashLogEntity = new EmptiedTrashLogEntity(
				createTrashKey(user, trashName),
				new Date());

		ofy().save().entities(emptiedTrashLogEntity);

	}

	/**
	 * List all trashed belongin to the given user
	 *
	 * @param user
	 *            user
	 *
	 * @return trash list
	 */
	public List<Trash> getTrashes(final User user) {

		final List<TrashEntity> trashEntities = ofy()
				.load()
				.type(TrashEntity.class) // We want only trash
				.ancestor(createUserKey(user)) // We filter entities on this user
				.list();

		final List<Trash> trashes = new ArrayList<>();

		LOG.info(String.format("%s trash fetched for specific user", trashEntities.size()));

		for (final TrashEntity entity : trashEntities) {
			trashes.add(new Trash(entity.getName(), entity.getVolume()));
		}

		return trashes;
	}

	/**
	 * Get statistics for a given trash
	 *
	 * @param user
	 *            user
	 * @param trashName
	 *            trash
	 *
	 * @return statistics
	 */
	protected List<Date> getTrashLogDateFor(final User user, final String trashName, final boolean naturalOrder) {

		final String orderSign;
		if (naturalOrder) {
			orderSign = "";
		} else {
			orderSign = "-";
		}

		// Retrieve all stored log for a given trash
		final List<EmptiedTrashLogEntity> list = ofy()
				.load()
				.type(EmptiedTrashLogEntity.class)
				.ancestor(createTrashKey(user, trashName)) // We filter on this specific trash
				.order(orderSign + "date")
				.list();

		LOG.info(String.format("%s trash logs fetched", list.size()));

		// For each emptying log, we add date to the list
		final List<Date> emptyDates = new ArrayList<>();
		for (final EmptiedTrashLogEntity entities : list) {
			emptyDates.add(entities.getDate());
		}
		return emptyDates;
	}

	protected List<Date> getTrashLogDateFor(final User user, final String trashName) {
		return getTrashLogDateFor(user, trashName, true);
	}

	/**
	 * Retrieve for all trashes of a given user its statistics
	 *
	 * @param user
	 *
	 * @return
	 */
	public List<TrashStatistics> getTrashStatistic(final User user) {
		final List<TrashStatistics> result = new ArrayList<>();
		for (final Trash trash : getTrashes(user)) {

			result.add(new TrashStatistics(trash, getTrashLogDateFor(user, trash.getName())));
		}
		return result;
	}

	public void deleteTrash(final User user, final String trashName) {
		// Delete logs
		ofy().delete().keys(ofy().load()
				.type(EmptiedTrashLogEntity.class)
				.ancestor(createTrashKey(user, trashName))
				.keys()).now();

		// Delete trash
		ofy().delete().key(createTrashKey(user, trashName)).now();
	}
}
