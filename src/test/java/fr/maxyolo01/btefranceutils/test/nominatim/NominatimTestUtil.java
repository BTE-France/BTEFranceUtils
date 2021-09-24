package fr.maxyolo01.btefranceutils.test.nominatim;

import com.google.gson.Gson;
import fr.dudie.nominatim.model.Address;

public final class NominatimTestUtil {

    private static final Gson GSON = new Gson();

    private NominatimTestUtil() {} // Utility class

    public static Address getDisplayNameAddress(
            String road,
            String suburb,
            String city,
            String administrative,
            String state,
            String postcode,
            String country,
            String countryCode,
            String displayName) {
        // Damn that's ugly
        String str = "{\n" +
                "\"display_name\": \"" + displayName + "\",\n" +
                "\"address\": [\n" +
                    "{\"key\": \"road\", \"value\": \"" + road + "\"},\n" +
                    "{\"key\": \"suburb\", \"value\": \"" + suburb + "\"},\n" +
                    "{\"key\": \"city\", \"value\": \"" + city + "\"},\n" +
                    "{\"key\": \"administrative\", \"value\": \"" + administrative + "\"},\n" +
                    "{\"key\": \"state\", \"value\": \"" + state + "\"},\n" +
                    "{\"key\": \"postcode\", \"value\": \"" + postcode + "\"},\n" +
                    "{\"key\": \"country\", \"value\": \"" + country + "\"},\n" +
                    "{\"key\": \"country_code\", \"value\": \"" + countryCode + "\"}\n" +
                "]\n"
            + "}";
        return GSON.fromJson(str, Address.class);
    }

}
