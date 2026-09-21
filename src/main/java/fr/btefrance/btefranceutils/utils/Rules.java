package fr.btefrance.btefranceutils.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.btefrance.btefranceutils.BTEFranceUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Rules {
    private static Rules instance;
    private static final String filename = "rules.json";
    private final File file;
    private final Gson gson;
    private Set<String> acceptedUUIDs;

    private Rules() {
        this.file = new File(BTEFranceUtils.instance().getDataFolder(), filename);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadRules();
    }

    public static Rules instance() {
        if (instance == null) {
            instance = new Rules();
        }
        return instance;
    }

    private void loadRules() {
        if (!file.exists()) {
            acceptedUUIDs = new HashSet<>();
            saveRules();
            BTEFranceUtils.instance().getLogger().info("Creating " + filename + " as it does not exist.");
        } else {
            try (FileReader reader = new FileReader(file)) {
                Set<String> loaded = gson.fromJson(reader, Set.class);
                acceptedUUIDs = (loaded != null) ? loaded : new HashSet<>();
            } catch (IOException e) {
                e.printStackTrace();
                acceptedUUIDs = new HashSet<>();
            }
        }
    }

    private void saveRules() {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(acceptedUUIDs, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasAcceptedRules(UUID playerUUID) {
        return acceptedUUIDs.contains(playerUUID.toString());
    }

    public void acceptRules(UUID playerUUID) {
        if (acceptedUUIDs.add(playerUUID.toString())) {
            saveRules();
        }
    }

    public void denyRules(UUID playerUUID) {
        if (acceptedUUIDs.remove(playerUUID.toString())) {
            saveRules();
        }
    }
}
