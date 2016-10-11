package com.handy.portal.logger.handylogger;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.core.TestBaseApplication;
import com.handy.portal.logger.handylogger.model.EventLog;
import com.handy.portal.manager.FileManager;
import com.handy.portal.manager.PrefsManager;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import javax.inject.Inject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by sng on 10/6/16.
 */
public class EventLogManagerTest extends RobolectricGradleTestWrapper
{
    @Inject
    EventLogManager mEventLogManager;

    @Inject
    FileManager mFileManager;

    @Inject
    PrefsManager mDefaultPreferencesManager;

    @Before
    public void setUp()
    {
        ((TestBaseApplication) RuntimeEnvironment.application.getApplicationContext()).inject(this);
    }

    @Test
    public void shouldSaveLogsToFile()
    {
        addLogEvent("event1");

        //No logs shoudl be stored to file system until a send is requested
        assertEquals(0, mFileManager.getLogFileList().length);
        mEventLogManager.sendLogsFromPreference();
        assertEquals(1, mFileManager.getLogFileList().length);
    }

    @Test
    public void shouldSaveLogsToOneFile() {
        for(int i=0; i < EventLogManager.MAX_NUM_PER_BUNDLE ; i++) {
            addLogEvent("event" + i);
        }
        mEventLogManager.sendLogsFromPreference();
        assertEquals(1, mFileManager.getLogFileList().length);
    }

    @Test
    public void shouldSaveLogsToMultipleFiles() {
        for(int i=0; i < (EventLogManager.MAX_NUM_PER_BUNDLE + 1) ; i++) {
            addLogEvent("event" + i);
        }
        mEventLogManager.sendLogsFromPreference();
        assertEquals(2, mFileManager.getLogFileList().length);
    }

    @Test
    public void preferenceShouldBeEmptyAfterSend() {
        addLogEvent("event");
        assertEquals(true, mDefaultPreferencesManager.contains(PrefsKey.EVENT_LOG_BUNDLES));
        mEventLogManager.sendLogsFromPreference();
        assertEquals(false, mDefaultPreferencesManager.contains(PrefsKey.EVENT_LOG_BUNDLES));
    }

    @Test
    public void savedFilesShouldHaveCorrectLogs() {
        addLogEvent("event1");
        addLogEvent("event2");
        String jsonString = mDefaultPreferencesManager.getString(PrefsKey.EVENT_LOG_BUNDLES);
        mEventLogManager.sendLogsFromPreference();

        String fileJsonString = mFileManager.readFile(mFileManager.getLogFileList()[0]);

        //This should be within the jsonString from prefs
        assertTrue(jsonString.contains(fileJsonString));
    }

    private void addLogEvent(String eventName) {
        mEventLogManager.addLog(getLogEvent(eventName));
    }

    private LogEvent.AddLogEvent getLogEvent(String eventName) {
        return new LogEvent.AddLogEvent(new EventLog(eventName, "context") {});
    }
}
