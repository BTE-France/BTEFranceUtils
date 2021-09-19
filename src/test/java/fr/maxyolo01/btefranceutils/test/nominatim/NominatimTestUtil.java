package fr.maxyolo01.btefranceutils.test.nominatim;

import com.google.gson.Gson;
import fr.dudie.nominatim.model.Address;

public final class NominatimTestUtil {

    private static final Gson GSON = new Gson();

    private NominatimTestUtil() {} // Utility class

    public static Address getDisplayNameAddress(String displayName) {
        String str = "{\"display_name\": \"" + displayName + "\"}";
        return GSON.fromJson(str, Address.class);
    }

}
