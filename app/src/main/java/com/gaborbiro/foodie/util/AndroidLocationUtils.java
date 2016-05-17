package com.gaborbiro.foodie.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class AndroidLocationUtils {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_AND_COARSE_LOCATION = 1;

    public static Location getLastKnownLocation(Context context) {
        Location bestKnownLocation = null;

        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            Location lastKnownGpsLocation = getDiscreteLastKnownLocation(locationManager,
                    LocationManager.GPS_PROVIDER);
            Location lastKnownNetworkLocation =
                    getDiscreteLastKnownLocation(locationManager,
                            LocationManager.NETWORK_PROVIDER);

            if (LocationUtils.isBetterLocation(lastKnownGpsLocation,
                    lastKnownNetworkLocation)) {
                bestKnownLocation = lastKnownGpsLocation;
            } else {
                bestKnownLocation = lastKnownNetworkLocation;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return bestKnownLocation;
    }

    private static Location getDiscreteLastKnownLocation(LocationManager locationManager,
            String provider) throws SecurityException {
        Location lastKnownLocation = locationManager.getLastKnownLocation(provider);

        if (lastKnownLocation == null) {
            return null;
        }

        return LocationUtils.roundDown(lastKnownLocation);
    }

    /**
     * @return array of missing permissions
     */
    public static String[] verifyLocationPermissions(Context context) {
        List<String> permissionsToAsk = new ArrayList<>();

        if (!hasFineLocationPermission(context)) {
            permissionsToAsk.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!hasCoarseLocationPermission(context)) {
            permissionsToAsk.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        return permissionsToAsk.toArray(new String[permissionsToAsk.size()]);
    }

    /**
     * Results will come in the
     * {@link android.support.v4.app.FragmentActivity#onRequestPermissionsResult(int, String[], int[])} method
     */
    public static void askForLocationPermissions(Activity activity,
            String[] missingLocationPermissions) {
        ActivityCompat.requestPermissions(activity, missingLocationPermissions,
                PERMISSIONS_REQUEST_ACCESS_FINE_AND_COARSE_LOCATION);
    }

    private static boolean hasFineLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasCoarseLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static boolean verifyLocationPermissionsResult(int requestCode,
            @NonNull String permissions[], @NonNull int[] grantResults) {
        // TODO verify the permissions parameter to see if everything is there
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_AND_COARSE_LOCATION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }
}
