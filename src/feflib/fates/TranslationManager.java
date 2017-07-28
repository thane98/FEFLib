package feflib.fates;

import java.util.HashMap;
import java.util.List;

public class TranslationManager {
    private static TranslationManager instance;

    private HashMap<String, String> characters = new HashMap<>();
    private HashMap<String, String> items = new HashMap<>();
    private HashMap<String, String> classes = new HashMap<>();
    private HashMap<String, String> skills = new HashMap<>();
    private HashMap<String, String> tiles = new HashMap<>();
    private HashMap<String, String> raw = new HashMap<>();

    public static TranslationManager getInstance() {
        if(instance == null)
            instance = new TranslationManager();
        return instance;
    }

    public void clearSuggestionLists() {
        characters.clear();
        items.clear();
        classes.clear();
        skills.clear();
        raw.clear();
    }

    public void loadSuggestionList(List<String> entries) {
        clearSuggestionLists();
        for(String s : entries) {
            String[] split = s.split(": ");
            if(split.length < 2)
                continue;
            raw.put(split[0], split[1]);
            String english = split[1].replaceAll("\\s","");
            if(split[0].startsWith("MPID_") && !split[0].contains("_H_")) {
                String japanese = split[0].replace("MPID_", "");
                characters.put("PID_" + english, "PID_" + japanese);
                characters.put("MPID_" + english, split[0]);
                characters.put("MPID_H_" + english, "MPID_H_" + japanese);
                characters.put("FID_" + english, "FID_" + japanese);
                characters.put("AID_" + english, "AID_" + japanese);
            }
            else if(split[0].startsWith("MIID_") && !split[0].contains("_H_")) {
                String japanese = split[0].replace("MIID_", "");
                items.put("IID_" + english, "IID_" + japanese);
                items.put("MIID_" + english, split[0]);
                items.put("MIID_H_" + english, "MIID_H_" + japanese);
            }
            else if(split[0].startsWith("MJID_") && !split[0].contains("_H_")) {
                String japanese = split[0].replace("MJID_", "");
                classes.put("JID_" + english, "JID_" + japanese);
                classes.put("MJID_" + english, split[0]);
                classes.put("MJID_H_" + english, "MJID_H_" + japanese);
            }
            else if(split[0].startsWith("MSEID_") && !split[0].contains("_H_")) {
                String japanese = split[0].replace("MSEID_", "");
                skills.put("SEID_" + english, "SEID_" + japanese);
                skills.put("MSEID_" + english, split[0]);
                skills.put("MSEID_H_" + english, "MSEID_H_" + japanese);
            }
            else if(split[0].startsWith("MTID") && !split[0].contains("_H_")) {
                String japanese = split[0].replace("MTID_", "");
                tiles.put("TID_" + english, "TID_" + japanese);
                tiles.put("MTID_" + english, split[0]);
            }
        }
    }

    public String getRealEntry(String english) {
        if(characters.containsKey(english))
            return characters.get(english);
        else if(items.containsKey(english))
            return items.get(english);
        else if(classes.containsKey(english))
            return classes.get(english);
        else if(skills.containsKey(english))
            return skills.get(english);
        else if(tiles.containsKey(english))
            return tiles.get(english);
        return null;
    }

    public HashMap<String, String> getCharacters() {
        return characters;
    }

    public HashMap<String, String> getItems() {
        return items;
    }

    public HashMap<String, String> getClasses() {
        return classes;
    }

    public HashMap<String, String> getSkills() {
        return skills;
    }

    public HashMap<String, String> getRaw() {
        return raw;
    }

    public HashMap<String, String> getTiles() {
        return tiles;
    }
}
