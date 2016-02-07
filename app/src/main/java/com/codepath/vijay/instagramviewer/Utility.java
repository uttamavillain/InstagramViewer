package com.codepath.vijay.instagramviewer;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by uttamavillain on 2/6/16.
 */
public class Utility {
    static String formatDate(long dateInMilliseconds) {
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }
}
