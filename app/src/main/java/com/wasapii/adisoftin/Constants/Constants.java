package com.wasapii.adisoftin.Constants;

/**
 * Created by adisfot5 on 7/3/17.
 */
public class Constants {

    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    private static final int UPDATE_INTERVAL_IN_SECONDS = 60;
    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 60;
    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    // Stores the lat / long pairs in a text file
    public static final String LOCATION_FILE = "sdcard/location.txt";
    // Stores the connect / disconnect data in a text file
    public static final String LOG_FILE = "sdcard/log.txt";

    public static final String RUNNING = "runningInBackground"; // Recording data in background

    public static final String APP_PACKAGE_NAME = "com.blackcj.locationtracker";

    public static final String REGULAR_FONT="Mark Simonson - Proxima Nova Regular.ttf";

    public static final String LIGHT_FONT="Mark Simonson - Proxima Nova Light.ttf";

    public static final String BOLD_FONT="Mark Simonson - Proxima Nova Semibold.ttf";

    public static final String MDY = "MM/dd/yyyy";

    /**
     * Suppress default constructor for noninstantiability
     */
    private Constants() {
        throw new AssertionError();
    }
}
