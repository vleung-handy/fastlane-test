package com.handy.portal.logger.handylogger.model;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.manager.PrefsManager;

import java.io.Serializable;

import static com.stripe.net.APIResource.GSON;

/**
 * Created by sng on 10/11/16.
 */

public class Session implements Serializable
{
    private static int SESSION_TIMEOUT = 1 * 60 * 1000; //30 minutes
    private int id;
    private int eventCount;
    private long lastModified;

    private static Session mInstance;

    public static Session getInstance(PrefsManager prefsManager) {
        if (prefsManager.contains(PrefsKey.LOG_SESSION))
        {
            String sessionStr = prefsManager.getString(PrefsKey.LOG_SESSION);
            mInstance = GSON.fromJson(sessionStr, Session.class);
        } else {
            mInstance = new Session(prefsManager);
        }

        return mInstance;
    }

    private Session(PrefsManager prefsManager)
    {
        id = 1;
        eventCount = 0;
        saveSession(prefsManager);
    }

    public int getId()
    {
        return id;
    }

    public int getEventCount()
    {
        //Always start with count 1
        if (eventCount == 0)
        { eventCount = 1; }

        return eventCount;
    }

    public void incrementEventCount(PrefsManager prefsManager)
    {
        //If greater then threshold then init a new session
        if (System.currentTimeMillis() - lastModified > SESSION_TIMEOUT)
        {
            //New session must increment the session id
            id++;
            //Reset event count on new sessions
            eventCount = 1;
        }
        else
        {
            //increment event count for current session
            eventCount++;
        }

        saveSession(prefsManager);
    }

    private void saveSession(PrefsManager prefsManager) {
        //update last modified time
        lastModified = System.currentTimeMillis();
        //Save the session on ever change
        prefsManager.setString(PrefsKey.LOG_SESSION, GSON.toJson(mInstance));
    }
}
