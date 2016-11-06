package com.smargav.api.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.Settings;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.smargav.api.logger.AppLogger;
import com.smargav.api.net.WebSession;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Amit S on 15/10/15.
 */
public class GPSUtils {
    public static LatLng getLocationFromString(Context ctx, String address)
            throws Exception {
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(ctx, Locale.ENGLISH);
            List addresses = null;
            try {
                addresses = geocoder.getFromLocationName(address, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if ((addresses != null) && (addresses.size() > 0)) {
                Address addr = (Address) addresses.get(0);
                LatLng l = new LatLng(addr.getLatitude(), addr.getLongitude());
                return l;
            }
        }

        WebSession session = new WebSession();
        String url =
                "http://maps.google.com/maps/api/geocode/json?address="
                        + URLEncoder.encode(address, "UTF-8")
                        + "&ka&sensor=false";
        try {
            String response = session.get(url);
            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(response);
            double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

            double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");

            return new LatLng(lat, lng);
        } catch (Exception e) {
            AppLogger.e(GPSUtils.class, new StringBuilder().append("Error Geocoding - ").append(e.getLocalizedMessage()).toString());
        }

        return null;
    }

    public static Address getStringFromLocation(Context ctx, double lat, double lng)
            throws Exception {
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(ctx, Locale.ENGLISH);
            List addresses = geocoder.getFromLocation(lat, lng, 1);
            if ((addresses != null) && (addresses.size() > 0)) {
                Address addr = (Address) addresses.get(0);

                String city = addr.getLocality();
                String pin = addr.getPostalCode();
                String state = addr.getAdminArea();
                String country = addr.getCountryName();
                for (int i = 0; i < addr.getMaxAddressLineIndex(); i++) {
                    String field = addr.getAddressLine(i);
                    boolean isModified = false;
                    if (StringUtils.containsIgnoreCase(field, city)) {
                        field = StringUtils.replace(field, city, "");
                        isModified = true;
                    }

                    if (StringUtils.containsIgnoreCase(field, pin)) {
                        field = StringUtils.replace(field, pin, "");
                        isModified = true;
                    }

                    if (StringUtils.containsIgnoreCase(field, state)) {
                        field = StringUtils.replace(field, state, "");
                        field = StringUtils.replace(field, ",", "");
                        isModified = true;
                    }

                    if (StringUtils.containsIgnoreCase(field, country)) {
                        field = StringUtils.replace(field, country, "");
                        isModified = true;
                    }

                    if (isModified) {
                        addr.setAddressLine(i, field);
                    }
                }

                return addr;
            }
        }
        try {
            String address = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=true&language=en", new Object[]{Double.valueOf(lat), Double.valueOf(lng)});

            WebSession session = new WebSession();
            String response = session.get(address);
            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(response);
            List retList = new ArrayList();

            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    String indiStr = result.getString("formatted_address");
                    Address addr = new Address(Locale.getDefault());
                    processAddressComponents(addr, result);
                    retList.add(addr);
                }
            }

            return (Address) retList.get(0);
        } catch (Exception e) {
            AppLogger.e(GPSUtils.class, new StringBuilder().append("Error Getting Address - ").append(e.getLocalizedMessage()).toString());

            e.printStackTrace();
        }

        return null;
    }

    private static void processAddressComponents(Address addr, JSONObject result)
            throws JSONException {
        Map lines = new HashMap();
        if (result.has("address_components")) {
            JSONArray array = result.getJSONArray("address_components");
            for (int j = 0; j < array.length(); j++) {
                JSONObject obj = array.getJSONObject(j);
                if (obj.has("types")) {
                    JSONArray types = obj.getJSONArray("types");
                    for (int k = 0; k < types.length(); k++) {
                        lines.put(types.getString(k), obj.getString("long_name"));
                    }
                }

            }

            int addrLines = 0;
            if (lines.containsKey("street_number")) {
                addr.setAddressLine(addrLines++, (String) lines.get("street_number"));
            }

            if (lines.containsKey("route")) {
                addr.setAddressLine(addrLines++, (String) lines.get("route"));
            }

            if (lines.containsKey("neighborhood")) {
                addr.setAddressLine(addrLines++, (String) lines.get("neighborhood"));
            }

            if (lines.containsKey("sublocality_level_1")) {
                addr.setAddressLine(addrLines++, (String) lines.get("sublocality_level_1"));
            } else if (lines.containsKey("sublocality")) {
                addr.setAddressLine(addrLines++, (String) lines.get("sublocality"));
            }

            if (lines.containsKey("locality")) {
                addr.setLocality((String) lines.get("locality"));
            }

            if (lines.containsKey("administrative_area_level_1")) {
                addr.setAdminArea((String) lines.get("administrative_area_level_1"));
            }

            if (lines.containsKey("country")) {
                addr.setCountryName((String) lines.get("country"));
            }

            if (lines.containsKey("postal_code"))
                addr.setPostalCode((String) lines.get("postal_code"));
        }
    }

    public static LatLng getLocationFromAddress(Context ctx, String strAddress) {
        Geocoder coder = new Geocoder(ctx);

        LatLng p1 = null;
        try {
            List address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = (Address) address.get(0);
            location.getLatitude();
            location.getLongitude();

            return new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
        }
        return null;
    }

    public static void turnGPSOn(Context ctx) {

        if (!canToggleGPS(ctx)) {
            return;
        }

        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            ctx.sendBroadcast(poke);
        }
    }


    private static boolean canToggleGPS(Context ctx) {
        PackageManager pacman = ctx.getPackageManager();
        PackageInfo pacInfo = null;

        try {
            pacInfo = pacman.getPackageInfo("com.android.settings", PackageManager.GET_RECEIVERS);
        } catch (PackageManager.NameNotFoundException e) {
            return false; //package not found
        }

        if (pacInfo != null) {
            for (ActivityInfo actInfo : pacInfo.receivers) {
                //test if recevier is exported. if so, we can toggle GPS.
                if (actInfo.name.equals("com.android.settings.widget.SettingsAppWidgetProvider") && actInfo.exported) {
                    return true;
                }
            }
        }

        return false; //default
    }

    public static void enableData(Context context, boolean enabled) {
        try {
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
            connectivityManagerField.setAccessible(true);
            final Object connectivityManager = connectivityManagerField.get(conman);
            final Class connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ***** END_TRIP CODE: SREEDEVI ******************/

    public static void showGpsEnablePrompt(final Activity context, GoogleApiClient.ConnectionCallbacks listener, GoogleApiClient.OnConnectionFailedListener failed) {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(listener)
                .addOnConnectionFailedListener(failed).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    context, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    public static boolean isGpsEnabled(Context ctx) {
        LocationManager mgr = (LocationManager) ctx
                .getSystemService(Context.LOCATION_SERVICE);
        return (mgr.isProviderEnabled(LocationManager.GPS_PROVIDER) || mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public static boolean isGpsProviderAvailable(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        if (!hasGps) {
            return false;
        }
        LocationManager mgr = (LocationManager) ctx
                .getSystemService(Context.LOCATION_SERVICE);
        return mgr.getProvider(LocationManager.GPS_PROVIDER) != null;
    }

}
