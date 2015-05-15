package com.handy.portal.core;

import android.webkit.WebSettings;

public class ServerParams
{

    //public static final String BASE_URL = "https://hbay015.localtunnel.me/"; // FOR DEVELOPMENT
//    public static final String BASE_URL = "http://192.168.8.21:3000/"; // FOR Non-tunneled local dev
    //public static final String BASE_URL = "https://www.clothestwin.com/"; // FOR STAGING
    //public static final String BASE_URL = "https://s-handybook.hbinternal.com/"; // FOR STAGING
    // public static final String BASE_URL = "https://localhost:3000/"; // FOR LOCALHOST
    public static final String BASE_URL = "http://10.0.2.2:3000/portal/"; // FOR LOCALHOST
    public static final String PORTAL_WEB_URL = "http://10.0.2.2:3000/portal/home"; // FOR LOCALHOST
    //public static final String BASE_URL = "https://handybook.com/"; // FOR PRODUCTION

    public static final String SENDER_ID = "210568814034"; // FOR DEVELOPMENT AND STAGING

    public static final int cacheMode = WebSettings.LOAD_NO_CACHE; // FOR DEVELOPMENT

    public static class Targets
    {
        public static final String AVAILABLE = PORTAL_WEB_URL + "?goto=available&hide_nav=1";
        public static final String FUTURE = PORTAL_WEB_URL + "?goto=future&hide_nav=1";
        public static final String PROFILE = PORTAL_WEB_URL + "?goto=profile&hide_nav=1";
        public static final String HELP = PORTAL_WEB_URL + "?goto=help&hide_nav=1";
    }

}
