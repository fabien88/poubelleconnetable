package com.example.poubelleconnetable.utilities;

import com.example.poubelleconnetable.model.EmptiedTrashLogEntity;
import com.example.poubelleconnetable.model.TrashEntity;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * OfyHelper, a ServletContextListener, is setup in web.xml to run before a JSP is run.  This is
 * required to let JSP's access Ofy.
 **/
public class OfyHelper implements ServletContextListener {
    public void contextInitialized(final ServletContextEvent event) {
        // This will be invoked as part of a warmup request, or the first user request if no warmup
        // request.
        ObjectifyService.register(TrashEntity.class);
        ObjectifyService.register(EmptiedTrashLogEntity.class);
    }

    public void contextDestroyed(final ServletContextEvent event) {
        // App Engine does not currently invoke this method.
    }
}