package com.handy.portal.core;

import android.webkit.WebSettings;

public class ServerParams {

    //public static final String BaseUrl = "https://hbay015.localtunnel.me/"; // FOR DEVELOPMENT
//    public static final String BaseUrl = "http://192.168.8.21:3000/"; // FOR Non-tunneled local dev
    //public static final String BaseUrl = "https://www.clothestwin.com/"; // FOR STAGING
    //public static final String BaseUrl = "https://s-handybook.hbinternal.com/"; // FOR STAGING
    public static final String BaseUrl = "https://localhost:3000/"; // FOR LOCALHOST
    //public static final String BaseUrl = "https://handybook.com/"; // FOR PRODUCTION

    public static final String SENDER_ID = "210568814034"; // FOR DEVELOPMENT AND STAGING

    public static final int cacheMode = WebSettings.LOAD_NO_CACHE; // FOR DEVELOPMENT

}
