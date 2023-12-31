package com.team25.neety;

/**
 * This class contains all the constants used in the app
 */

import java.util.Locale;

public final class Constants {
    public static final int NO_SERIAL = -1;

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

    public static final String INTENT_ITEM_KEY = "PASSED_ITEM";

    public static final String INTENT_ITEM_ID_KEY = "PASSED_ITEM_ID";
    public static final String ITEM_MAIN_TO_EDIT = "MAIN_TO_EDIT";

    public static final int EDIT_ITEM_ACTIVITY_CODE = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 10;

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 1010;

    public static final Locale locale = Locale.US;

    // Private constructor because you should never instantiate this class
    private Constants() {}
}
