package com.example.poubelleconnetable.service;

import com.example.poubelleconnetable.api.Trash;
import com.example.poubelleconnetable.utilities.CustomReflectionAssert;
import com.example.poubelleconnetable.utilities.OfyHelper;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.*;

public class TrashServiceTest {
    private static final User DEFAULT_USER = new User("test@exemple.com", "", null);
    private static final User ANOTHER_USER = new User("anothertest@exemple.com", "", null);
    private static final User YET_ANOTHER_USER = new User("yetanothertest@exemple.com", "", null);
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig()
                    .setNoStorage(true)
    ).setEnvAuthDomain("localhost");

    private final TrashService trashService = new TrashService();
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
        this.trashService.createTrash(DEFAULT_USER, "trash1", 1D);
        this.trashService.createTrash(DEFAULT_USER, "trash2", 2D);
        try {
            this.trashService.createTrash(DEFAULT_USER, "trash1", 3D);
            Assert.fail("Should not be possible to create same trash name");
        } catch (final ConflictException e) {
        }
        try {
            this.trashService.createTrash(DEFAULT_USER, "trash1", null);
            Assert.fail("Should not be possible to create trash with no volume");
        } catch (final ConflictException e) {
        }

        this.trashService.createTrash(ANOTHER_USER, "trash1", 4D);
        this.trashService.createTrash(ANOTHER_USER, "trash2", 5D);
    }

    @Test
    public void emptyTrash() throws Exception {
        createTrash();
        trashService.emptyTrash(DEFAULT_USER, "trash1");
        Thread.sleep(1000L);
        trashService.emptyTrash(DEFAULT_USER, "trash1");
        Thread.sleep(1000L);
        trashService.emptyTrash(DEFAULT_USER, "trash1");
        trashService.emptyTrash(DEFAULT_USER, "trash2");
        try {
            trashService.emptyTrash(DEFAULT_USER, "notexistingtrash");
            Assert.fail("Should not be possible to empty non existing trash");
        } catch (final NotFoundException e) {
        }

        trashService.emptyTrash(ANOTHER_USER, "trash1");

        try {
            trashService.emptyTrash(YET_ANOTHER_USER, "trash1");
            Assert.fail("Should not be possible to empty non existing trash");
        } catch (final NotFoundException e) {
        }

    }

    @Test
    public void getTrashes() throws Exception {
        createTrash();
        ReflectionAssert.assertLenientEquals(
                Arrays.asList(new Trash("trash1", 1D), new Trash("trash2", 2D)),
                trashService.getTrashes(DEFAULT_USER)
        );
        ReflectionAssert.assertLenientEquals(
                Arrays.asList(new Trash("trash1", 4D), new Trash("trash2", 5D)),
                trashService.getTrashes(ANOTHER_USER)
        );
        ReflectionAssert.assertLenientEquals(
                Collections.emptyList(),
                trashService.getTrashes(YET_ANOTHER_USER)
        );
    }

    @Test
    public void getTrashLogsFor() throws Exception {
        emptyTrash();
        // Check log empty trash has been made 1 second ago maximum
        Assert.assertEquals(
                new Date().getTime(),
                trashService.getTrashLogDateFor(DEFAULT_USER, "trash1").get(0).getTime(),
                1000
        );

        // DEFAULT_USER should have 3 empty log for trash1
        ReflectionAssert.assertReflectionEquals(
                Arrays.asList(new Date(), new Date(), new Date()),
                trashService.getTrashLogDateFor(DEFAULT_USER, "trash1"),
                ReflectionComparatorMode.LENIENT_DATES
        );

        // Check dates are ordered in natural order
        List<Date> actualDates = trashService.getTrashLogDateFor(DEFAULT_USER, "trash1", true);
        List<Date> expectedOrderedDates = new ArrayList<>(actualDates);
        Collections.sort(expectedOrderedDates);
        ReflectionAssert.assertReflectionEquals(expectedOrderedDates, actualDates);

        // Check dates are ordered in reverse order
        actualDates = trashService.getTrashLogDateFor(DEFAULT_USER, "trash1", false);
        expectedOrderedDates = new ArrayList<>(actualDates);
        Collections.sort(expectedOrderedDates, Collections.reverseOrder());
        ReflectionAssert.assertReflectionEquals(expectedOrderedDates, actualDates);

        // And one for trash2
        ReflectionAssert.assertReflectionEquals(
                Arrays.asList(new Date()),
                trashService.getTrashLogDateFor(DEFAULT_USER, "trash2"),
                ReflectionComparatorMode.LENIENT_DATES
        );

        // ANOTHER_USER should have 1 empty log for trash1
        ReflectionAssert.assertReflectionEquals(
                Arrays.asList(new Date()),
                trashService.getTrashLogDateFor(ANOTHER_USER, "trash1"),
                ReflectionComparatorMode.LENIENT_DATES
        );
        // ANOTHER_USER should have 0 empty log for trash2
        ReflectionAssert.assertReflectionEquals(
                Arrays.asList(),
                trashService.getTrashLogDateFor(ANOTHER_USER, "trash2")
        );

        // YET_ANOTHER_USER has no existing trash
        ReflectionAssert.assertReflectionEquals(
                Arrays.asList(),
                trashService.getTrashLogDateFor(YET_ANOTHER_USER, "notexistingtrash")
        );

    }

    @Test
    public void deleteTrash() throws Exception {
        // Init all trashes
        emptyTrash();

        // Check trash1 is present
        CustomReflectionAssert.reflectionContains(new Trash("trash1", 1D), trashService.getTrashes(DEFAULT_USER));
        // And that we have logs for it
        Assert.assertFalse(trashService.getTrashLogDateFor(DEFAULT_USER, "trash1").isEmpty());

        // Delete trash1
        trashService.deleteTrash(DEFAULT_USER, "trash1");

        // Check that it is no more present
        CustomReflectionAssert.reflectionNotContains(new Trash("trash1", 1D), trashService.getTrashes(DEFAULT_USER));
        // Check that we have no more logs for it
        Assert.assertTrue(trashService.getTrashLogDateFor(DEFAULT_USER, "trash1").isEmpty());

        CustomReflectionAssert.reflectionContains(new Trash("trash2", 2D), trashService.getTrashes(DEFAULT_USER));
        Assert.assertFalse(trashService.getTrashLogDateFor(DEFAULT_USER, "trash2").isEmpty());
        trashService.deleteTrash(DEFAULT_USER, "trash2");
        Assert.assertTrue(trashService.getTrashLogDateFor(DEFAULT_USER, "trash2").isEmpty());
        Assert.assertTrue(trashService.getTrashLogDateFor(DEFAULT_USER, "trash2").isEmpty());

        Assert.assertFalse(trashService.getTrashLogDateFor(ANOTHER_USER, "trash1").isEmpty());
        CustomReflectionAssert.reflectionContains(new Trash("trash1", 4D), trashService.getTrashes(ANOTHER_USER));
        trashService.deleteTrash(ANOTHER_USER, "trash1");
        CustomReflectionAssert.reflectionNotContains(new Trash("trash1", 4D), trashService.getTrashes(ANOTHER_USER));
        Assert.assertTrue(trashService.getTrashLogDateFor(ANOTHER_USER, "trash1").isEmpty());

        // Trash doesn't exist, and trying to delete it does nothing more
        Assert.assertTrue(trashService.getTrashLogDateFor(YET_ANOTHER_USER, "notexistingtrash").isEmpty());
        CustomReflectionAssert.reflectionNotContains(new Trash("notexistingtrash", 2D), trashService.getTrashes(YET_ANOTHER_USER));
        trashService.deleteTrash(YET_ANOTHER_USER, "notexistingtrash");
        CustomReflectionAssert.reflectionNotContains(new Trash("notexistingtrash", 2D), trashService.getTrashes(YET_ANOTHER_USER));
        Assert.assertTrue(trashService.getTrashLogDateFor(YET_ANOTHER_USER, "notexistingtrash").isEmpty());

    }

}