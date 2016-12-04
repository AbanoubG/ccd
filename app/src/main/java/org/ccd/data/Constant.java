package org.ccd.data;

public class Constant {

    /**
     * -------------------- EDIT THIS WITH YOURS ---------------------------------------------------
     */

    // Edit WEB_URL with your url. Make sure you have backslash('/') in the end url
    public static String WEB_URL = "http://app.copticchurch-directory.org/";

    // for map zoom
    public static final double city_lat = -6.9174639;
    public static final double city_lng = 107.6191228;

    // for gcm
    public static final String PROJECT_API_NUMBER = "107798XXXXXXX";


    /**
     * ------------------- DON'T EDIT THIS ---------------------------------------------------------
     */

    // image file url
    public static String getURLimgPlace(String file_name) {
        return WEB_URL + "uploads/place/" + file_name;
    }

    // this limit value used for give pagination (request and display) to decrease payload
    public static final int LIMIT_PLACE_REQUEST = 50;
    public static final int LIMIT_LOADMORE = 50;

    // for search logs Tag
    public static final String LOG_TAG = "CITY_LOG";

    // Google analytics event category
    public enum Event {
        FAVORITES,
        THEME,
        NOTIFICATION,
        REFRESH
    }

}
