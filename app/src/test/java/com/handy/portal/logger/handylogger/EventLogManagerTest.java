package com.handy.portal.logger.handylogger;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.TestBaseApplication;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.manager.FileManager;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.logger.handylogger.model.EventLog;

import org.junit.Before;
import org.junit.Ignore;
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
    PrefsManager mPrefsManager;

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

    @Ignore // TODO: Fix Jenkins-specific test failure on this test
    @Test
    public void shouldSaveLogsToOneFile() {
        for(int i = 0; i < EventLogManager.MAX_EVENTS_PER_BUNDLE; i++) {
            addLogEvent("event" + i);
        }
        mEventLogManager.sendLogsFromPreference();
        assertEquals(1, mFileManager.getLogFileList().length);
    }

    @Test
    public void shouldSaveLogsToMultipleFiles() {
        for(int i = 0; i < (EventLogManager.MAX_EVENTS_PER_BUNDLE + 1) ; i++) {
            addLogEvent("event" + i);
        }
        mEventLogManager.sendLogsFromPreference();
        assertEquals(2, mFileManager.getLogFileList().length);
    }

    @Test
    public void preferenceShouldBeEmptyAfterSend() {
        addLogEvent("event");
        assertEquals(true, mPrefsManager.contains(PrefsKey.EVENT_LOG_BUNDLES));
        mEventLogManager.sendLogsFromPreference();
        assertEquals(false, mPrefsManager.contains(PrefsKey.EVENT_LOG_BUNDLES));
    }

    @Test
    public void savedFilesShouldHaveCorrectLogs() {
        addLogEvent("event1");
        addLogEvent("event2");
        String jsonString = mPrefsManager.getString(PrefsKey.EVENT_LOG_BUNDLES);
        mEventLogManager.sendLogsFromPreference();

        String fileJsonString = mFileManager.readFile(mFileManager.getLogFileList()[0]);

        //This should be within the jsonString from prefs
        assertTrue(jsonString.contains(fileJsonString));
    }

    private void addLogEvent(String eventName) {
        mEventLogManager.addLog(getLogEvent(eventName));
    }

    private LogEvent.AddLogEvent getLogEvent(String eventName) {
        return new LogEvent.AddLogEvent(new TestEventLog(eventName, "event_context"));
    }

    /**
     * This is needed for the gson to serialize correctly because anonymous class doesn't require constructor
     */
    private class TestEventLog extends EventLog {

        public TestEventLog(final String eventType, final String eventContext)
        {
            super(eventType, eventContext);
        }
    }
}
