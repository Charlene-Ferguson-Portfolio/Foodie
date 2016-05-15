package com.gaborbiro.foodie.util;

import android.location.Location;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationUtils {

    private static final String TAG = LocationUtils.class.getSimpleName();

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final double ONE_MILE_METERS = 0.000621371192237;

    public static final int SEARCH_RADIUS_METERS = (int) (1 / ONE_MILE_METERS);

    public static final int LOCATION_UPDATE_THRESHOLD_TIME_MSEC = TWO_MINUTES;
    public static final int LOCATION_UPDATE_THRESHOLD_METERS =
            (int) (SEARCH_RADIUS_METERS * 0.1);

    public static final SimpleDateFormat DATE_FORMAT_ISO8601 =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to
     *                            compare the new one
     */
    public static boolean isBetterLocation(Location location,
            Location currentBestLocation) {
        if (location == null) {
            return false;
        }
        if (currentBestLocation == null) {
            Logger.d(TAG, "Location found: " + formatLocation(location));
            // A new location is always better than no location
            return true;
        }

        if (location.getLatitude() == currentBestLocation.getLatitude() &&
                location.getLongitude() == currentBestLocation.getLongitude()) {
            Logger.d(TAG, "Same location, ignoring: " + formatLocation(location));
            return false;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > LOCATION_UPDATE_THRESHOLD_TIME_MSEC;
        boolean isSignificantlyOlder = timeDelta < -LOCATION_UPDATE_THRESHOLD_TIME_MSEC;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new
        // location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            Logger.d(TAG, "Sign. newer location found: " + formatLocation(location));
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            Logger.d(TAG, "Sign. older location, ignoring: " + formatLocation(location));
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta =
                (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate =
                accuracyDelta > LOCATION_UPDATE_THRESHOLD_METERS;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider =
                isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            Logger.d(TAG, "More accurate location found: " + formatLocation(location));
            return true;
        } else if (isNewer && !isLessAccurate) {
            Logger.d(TAG, "Newer location found: " + formatLocation(location));
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            Logger.d(TAG, "Newer2 location found: " + formatLocation(location));
            return true;
        }
        Logger.d(TAG, "No better location, ignoring: " + formatLocation(location));
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * Round down the coordinates as much as possible by eliminating digits in such a way
     * that the resulting coordinates are no further away from the origin than {@value
     * LOCATION_UPDATE_THRESHOLD_METERS} meters
     */
    public static Location roundUp(Location location) {
        double currentBestLat = location.getLatitude();
        double currentBestLng = location.getLongitude();
        String latStr = Double.toString(currentBestLat);
        String lngStr = Double.toString(currentBestLng);
        boolean stop = false;

        do {
            int latDigitCount = latStr.length() - latStr.indexOf('.') - 1;
            int lngDigitCount = lngStr.length() - lngStr.indexOf('.') - 1;

            if (latDigitCount > lngDigitCount) {
                latStr = getRoundingFormat(--latDigitCount).format(currentBestLat);
            } else if (lngDigitCount >= latDigitCount) {
                lngStr = getRoundingFormat(--lngDigitCount).format(currentBestLng);
            }
            double candidateLat = Double.valueOf(latStr);
            double candidateLng = Double.valueOf(lngStr);
            int distance = (int) distance(location.getLatitude(), location.getLongitude(),
                    candidateLat, candidateLng);

            if (distance < LOCATION_UPDATE_THRESHOLD_METERS) {
                currentBestLat = candidateLat;
                currentBestLng = candidateLng;
            } else {
                stop = true;
            }
        } while (!stop);
        Location result = new Location(location);
        result.setLatitude(currentBestLat);
        result.setLongitude(currentBestLng);
        return result;
    }

    private static DecimalFormat getRoundingFormat(int decimals) {
        DecimalFormat df = new DecimalFormat("#." + fill('#', decimals));
        df.setRoundingMode(RoundingMode.FLOOR);
        return df;
    }

    private static String fill(char c, int length) {
        StringBuffer outputBuffer = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            outputBuffer.append(c);
        }
        return outputBuffer.toString();
    }

    public static double distance(Location location1, Location location2) {
        return distance(location1.getLatitude(), location1.getLongitude(),
                location2.getLatitude(), location2.getLongitude());
    }

    /**
     * Calculate the distance between two coordinates in meters
     */
    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    public static String formatLocation(Location location) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(location.getLatitude());
        buffer.append(",");
        buffer.append(location.getLongitude());
        buffer.append(" acc=");
        buffer.append((int) location.getAccuracy());
        buffer.append(" time=");
        buffer.append(DATE_FORMAT_ISO8601.format(new Date(location.getTime())));
        buffer.append(" provider=");
        buffer.append(location.getProvider());
        return buffer.toString();
    }
}
