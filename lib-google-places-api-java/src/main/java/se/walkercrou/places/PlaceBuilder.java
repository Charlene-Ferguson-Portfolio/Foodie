package se.walkercrou.places;

import org.json.JSONObject;

import java.util.Locale;

import se.walkercrou.places.exception.GooglePlacesException;

import static se.walkercrou.places.GooglePlaces.INTEGER_ACCURACY;
import static se.walkercrou.places.GooglePlaces.OBJECT_LOCATION;
import static se.walkercrou.places.GooglePlaces.STRING_ADDRESS;
import static se.walkercrou.places.GooglePlaces.STRING_LANGUAGE;
import static se.walkercrou.places.GooglePlaces.STRING_NAME;
import static se.walkercrou.places.GooglePlaces.STRING_PHONE_NUMBER;
import static se.walkercrou.places.GooglePlaces.STRING_TYPE;
import static se.walkercrou.places.GooglePlaces.STRING_WEBSITE;

/**
 * Class for building Place information to be submitted to Google.
 */
public class PlaceBuilder {
    private final double lat, lng;
    private final String name;
    private final String type;
    private int accuracy = -1;
    private String phoneNumber;
    private String address;
    private String website;
    private Locale locale;

    /**
     * Creates a new PlaceBuilder with the initial name, latitude, longitude, and types
     * . The specified name must not be
     * null, and there must be at least one type defined.
     *
     * @param name to use
     * @param lat  latitude
     * @param lng  longitude
     * @param type type
     */
    public PlaceBuilder(String name, double lat, double lng, String type) {
        if (name == null || name.isEmpty() || type == null)
            throw new GooglePlacesException("Must specify a name, location and type");
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
    }

    /**
     * Sets the accuracy of the location signal on which this request is based,
     * expressed in meters.
     *
     * @param accuracy accuracy
     * @return this
     */
    public PlaceBuilder accuracy(int accuracy) {
        this.accuracy = accuracy;
        return this;
    }

    /**
     * Sets the phone number of the place. Well formatted phone numbers are more likely
     * to pass the moderation queue.
     *
     * @param phoneNumber to set
     * @return this
     */
    public PlaceBuilder phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    /**
     * The address of the place you wish to add. If a place has a well-formatted,
     * human-readable address, it is more
     * likely to pass the moderation process for inclusion in the Google Maps database.
     *
     * @param address address to set
     * @return this
     */
    public PlaceBuilder address(String address) {
        this.address = address;
        return this;
    }

    /**
     * A URL pointing to the authoritative website for this Place, such as a business
     * home page. If a Place has a
     * well-formatted website address, it is more likely to pass the moderation process
     * for inclusion in the Google Maps
     * database.
     *
     * @param website website to set
     * @return this
     */
    public PlaceBuilder website(String website) {
        this.website = website;
        return this;
    }

    /**
     * Locale in which the location is being reported.
     *
     * @param locale of location
     * @return this
     */
    public PlaceBuilder locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    /**
     * Returns a Google formatted JSON object to be sent to Google's servers.
     *
     * @return Google formatted JSON
     */
    public JSONObject toJson() {
        JSONObject obj = new JSONObject().put(OBJECT_LOCATION,
                new JSONObject().put("lat", lat)
                        .put("lng", lng))
                .put(STRING_NAME, name)
                .put(STRING_TYPE, type);
        if (accuracy != -1) obj.put(INTEGER_ACCURACY, accuracy);
        if (phoneNumber != null) obj.put(STRING_PHONE_NUMBER, phoneNumber);
        if (address != null) obj.put(STRING_ADDRESS, address);
        if (website != null) obj.put(STRING_WEBSITE, website);
        if (locale != null) obj.put(STRING_LANGUAGE, locale.getLanguage());
        return obj;
    }
}
