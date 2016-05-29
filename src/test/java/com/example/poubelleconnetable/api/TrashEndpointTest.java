package com.example.poubelleconnetable.api;

import java.util.Arrays;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import com.example.poubelleconnetable.utilities.OfyHelper;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

public class TrashEndpointTest {

	private static final User DEFAULT_USER = new User("test@exemple.com", "", null);
	private static final User NULL_USER = null;

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig().setNoStorage(true)).setEnvAuthDomain("localhost");

	private final TrashEndpoint trashEndpoint = new TrashEndpoint();
	Closeable session;

	@Before
	public void setUp() throws Exception {
		session = ObjectifyService.begin();
		new OfyHelper().contextInitialized(null);
		this.helper.setUp();
	}

	@After
	public void tearDown() throws Exception {
		session.close();
		this.helper.tearDown();
	}

	@Test
	public void createTrash() throws Exception {
		try {
			trashEndpoint.createTrash(NULL_USER, "trash1", 4D);
			Assert.fail("Method should check user is not null");
		} catch (final UnauthorizedException e) {
		}
		ReflectionAssert.assertReflectionEquals(
				new Trash("trash1", 4D),
				trashEndpoint.createTrash(DEFAULT_USER, "trash1", 4D));
	}

	@Test
	public void emptyTrash() throws Exception {
		createTrash();
		try {
			trashEndpoint.emptyTrash(NULL_USER, "trash1");
			Assert.fail("Method should check user is not null");
		} catch (final UnauthorizedException e) {
		}
		trashEndpoint.emptyTrash(DEFAULT_USER, "trash1");
	}

	@Test
	public void listTrash() throws Exception {
		createTrash();
		try {
			trashEndpoint.listTrash(NULL_USER);
			Assert.fail("Method should check user is not null");
		} catch (final UnauthorizedException e) {
		}
		ReflectionAssert.assertReflectionEquals(
				CollectionResponse.<Trash> builder().setItems(Arrays.asList(new Trash("trash1", 4D))).build(),
				trashEndpoint.listTrash(DEFAULT_USER));
	}

	@Test
	public void listTrashStatistics() throws Exception {
		emptyTrash(); // calls create trash test and then empty trash test
		try {
			trashEndpoint.listTrashStatistics(NULL_USER);
			Assert.fail("Method should check user is not null");
		} catch (final UnauthorizedException e) {
		}
		ReflectionAssert.assertReflectionEquals(
				CollectionResponse.<TrashStatistics> builder().setItems(Arrays.asList(
						new TrashStatistics(new Trash("trash1", 4D), Arrays.asList(new Date())))).build(),
				trashEndpoint.listTrashStatistics(DEFAULT_USER),
				ReflectionComparatorMode.LENIENT_DATES);
	}

	@Test
	public void deleteTrash() throws Exception {
		emptyTrash();
		try {
			trashEndpoint.deleteTrash(NULL_USER, "trash1");
			Assert.fail("Method should check user is not null");
		} catch (final UnauthorizedException e) {
		}
		trashEndpoint.deleteTrash(DEFAULT_USER, "trash1");
	}

}