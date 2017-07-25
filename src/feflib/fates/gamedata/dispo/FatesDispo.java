package feflib.fates.gamedata.dispo;

import static feflib.utils.ByteUtils.*;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.*;

public class FatesDispo {
    private ArrayList<DispoFaction> factions = new ArrayList<>();

    public FatesDispo(File file) {
        try {
            byte[] raw = Files.readAllBytes(file.toPath());
            parseFactions(raw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseFactions(byte[] raw) {
        int count = 0;
        while(toInt(raw, 0x20 + count * 0xC) != 0) {
            factions.add(new DispoFaction(raw, 0x20 + count * 0xC));
            count++;
        }
    }

    /**
     * Serializes the current list of factions to a binary format compatible
     * with 3DS Fire Emblem games.
     *
     * @return The serialized Dispo file.
     */
    public byte[] serialize() {
        HashMap<String, Integer> labelMap = new HashMap<>();
        HashMap<String, Integer> factionMap = new HashMap<>();
        List<Byte> labelBytes = new ArrayList<>();
        List<Integer> pointerOne = new ArrayList<>();

        // Map labels using the correct order.
        int factionStart = 0x20 + (factions.size() + 1) * 0xC;
        for(int x = 0; x < factions.size(); x++) {
            DispoFaction f = factions.get(x);
            labelMap.put(f.getName(), labelBytes.size());
            addLabel(labelBytes, f.getName());
            factionMap.put(f.getName(), factionStart);
            factionStart += f.getSpawns().size() * 0x8C;
            pointerOne.add(x * 0xC);
            pointerOne.add(x * 0xC + 4);
        }
        for(DispoFaction f : factions) {
            int ptrStart = factionMap.get(f.getName()) - 0x20;
            for(int i = 0; i < f.getSpawns().size(); i++) {
                int blockStart = ptrStart + i * 0x8C;
                DispoBlock b = f.getSpawns().get(i);
                if(!labelMap.containsKey(b.getPid())) {
                    labelMap.put(b.getPid(), labelBytes.size());
                    addLabel(labelBytes, b.getPid());
                }
                pointerOne.add(blockStart);
                for(int x = 0; x < 5; x++) {
                    if(!b.getItems()[x].equals("")) {
                        if(!labelMap.containsKey(b.getItems()[x])) {
                            labelMap.put(b.getItems()[x], labelBytes.size());
                            addLabel(labelBytes, b.getItems()[x]);   
                        }
                        pointerOne.add(blockStart + 20 + x * 8);
                    }
                }
                for(int x = 0; x < 5; x++) {
                    if(!b.getSkills()[x].equals("")) {
                        if(!labelMap.containsKey(b.getSkills()[x])) {
                            labelMap.put(b.getSkills()[x], labelBytes.size());
                            addLabel(labelBytes, b.getSkills()[x]);
                        }
                        pointerOne.add(blockStart + 60 + x * 4);
                    }
                }
                if(!b.getAc().equals("")) {
                    if(!labelMap.containsKey(b.getAc())) {
                        labelMap.put(b.getAc(), labelBytes.size());
                        addLabel(labelBytes, b.getAc());
                    }
                    pointerOne.add(blockStart + 84);   
                }
                if(!b.getAiPositionOne().equals("")) {
                    if(!labelMap.containsKey(b.getAiPositionOne())) {
                        labelMap.put(b.getAiPositionOne(), labelBytes.size());
                        addLabel(labelBytes, b.getAiPositionOne());
                    }
                    pointerOne.add(blockStart + 88);
                }
                if(!b.getMi().equals("")) {
                    if(!labelMap.containsKey(b.getMi())) {
                        labelMap.put(b.getMi(), labelBytes.size());
                        addLabel(labelBytes, b.getMi());
                    }
                    pointerOne.add(blockStart + 92);
                }
                if(!b.getAiPositionTwo().equals("")) {
                    if(!labelMap.containsKey(b.getAiPositionTwo())) {
                        labelMap.put(b.getAiPositionTwo(), labelBytes.size());
                        addLabel(labelBytes, b.getAiPositionTwo());
                    }
                    pointerOne.add(blockStart + 96);
                }
                if(!b.getAt().equals("")) {
                    if(!labelMap.containsKey(b.getAt())) {
                        labelMap.put(b.getAt(), labelBytes.size());
                        addLabel(labelBytes, b.getAt());
                    }
                    pointerOne.add(blockStart + 100);
                }
                if(!b.getAiPositionThree().equals("")) {
                    if(!labelMap.containsKey(b.getAiPositionThree())) {
                        labelMap.put(b.getAiPositionThree(), labelBytes.size());
                        addLabel(labelBytes, b.getAiPositionThree());
                    }
                    pointerOne.add(blockStart + 104);
                }
                if(!b.getMv().equals("")) {
                    if(!labelMap.containsKey(b.getMv())) {
                        labelMap.put(b.getMv(), labelBytes.size());
                        addLabel(labelBytes, b.getMv());
                    }
                    pointerOne.add(blockStart + 108);
                }
                if(!b.getAiPositionFour().equals("")) {
                    if(!labelMap.containsKey(b.getAiPositionFour())) {
                        labelMap.put(b.getAiPositionFour(), labelBytes.size());
                        addLabel(labelBytes, b.getAiPositionFour());
                    }
                    pointerOne.add(blockStart + 112);
                }
            }
        }

        // Begin serializing.
        int labelStart = factionStart + pointerOne.size() * 4;
        byte[] raw = new byte[labelStart + labelBytes.size()];

        System.arraycopy(toByteArray(raw.length), 0, raw, 0, 4);
        System.arraycopy(toByteArray(factionStart - 0x20), 0, raw, 4, 4);
        System.arraycopy(toByteArray(pointerOne.size()), 0, raw, 8, 4);
        for(int x = 0; x < labelBytes.size(); x++) {
            raw[labelStart + x] = labelBytes.get(x);
        }
        for(int x = 0; x < factions.size(); x++) {
            DispoFaction f = factions.get(x);
            System.arraycopy(toByteArray(labelMap.get(f.getName()) + labelStart - 0x20), 0,
                    raw, 0x20 + x * 0xC, 4);
            System.arraycopy(toByteArray(factionMap.get(f.getName()) - 0x20), 0,
                    raw, 0x24 + x * 0xC, 4);
            System.arraycopy(toByteArray(f.getSpawns().size()), 0, raw, 0x28 + x * 0xC, 4);
            for(int y = 0; y < f.getSpawns().size(); y++) {
                DispoBlock b = f.getSpawns().get(y);
                int start = factionMap.get(f.getName()) + y * 0x8C;
                System.arraycopy(toByteArray(labelMap.get(b.getPid()) + labelStart - 0x20), 0,
                        raw, start, 4);
                System.arraycopy(b.getUnknownOne(), 0, raw, start + 4, 4);
                raw[start + 8] = b.getTeam();
                raw[start + 9] = b.getLevel();
                System.arraycopy(b.getSecondCoord(), 0, raw, start + 10, 2);
                System.arraycopy(b.getFirstCoord(), 0, raw, start + 12, 2);
                System.arraycopy(b.getUnknownTwo(), 0, raw, start + 14, 2);
                System.arraycopy(b.getSpawnBitflags(), 0, raw, start + 16, 4);
                for(int z = 0; z < 5; z++) {
                    if(!b.getItems()[z].equals("")) {
                        System.arraycopy(toByteArray(labelMap.get(b.getItems()[z]) + labelStart - 0x20), 0,
                                raw, start + 20 + z * 8, 4);
                        System.arraycopy(b.getItemBitflags()[z], 0, raw, start + 24 + z * 8, 4);
                    }
                }
                for(int z = 0; z < 5; z++) {
                    if(!b.getSkills()[z].equals("")) {
                        System.arraycopy(toByteArray(labelMap.get(b.getSkills()[z]) + labelStart - 0x20), 0,
                                raw, start + 60 + z * 4, 4);
                    }
                }
                System.arraycopy(b.getSkillFlag(), 0, raw, start + 80, 4);
                if(!b.getAc().equals("")) {
                    System.arraycopy(toByteArray(labelMap.get(b.getAc()) + labelStart - 0x20), 0, 
                            raw, start + 84, 4);
                }
                if(!b.getAiPositionOne().equals("")) {
                    System.arraycopy(toByteArray(labelMap.get(b.getAiPositionOne()) + labelStart - 0x20), 0,
                            raw, start + 88, 4);
                }
                if(!b.getMi().equals("")) {
                    System.arraycopy(toByteArray(labelMap.get(b.getMi()) + labelStart - 0x20), 0,
                            raw, start + 92, 4);
                }
                if(!b.getAiPositionTwo().equals("")) {
                    System.arraycopy(toByteArray(labelMap.get(b.getAiPositionTwo()) + labelStart - 0x20), 0,
                            raw, start + 96, 4);
                }
                if(!b.getAt().equals("")) {
                    System.arraycopy(toByteArray(labelMap.get(b.getAt()) + labelStart - 0x20), 0,
                            raw, start + 100, 4);
                }
                if(!b.getAiPositionThree().equals("")) {
                    System.arraycopy(toByteArray(labelMap.get(b.getAiPositionThree()) + labelStart - 0x20), 0,
                            raw, start + 104, 4);
                }
                if(!b.getMv().equals("")) {
                    System.arraycopy(toByteArray(labelMap.get(b.getMv()) + labelStart - 0x20), 0,
                            raw, start + 108, 4);
                }
                if(!b.getAiPositionFour().equals("")) {
                    System.arraycopy(toByteArray(labelMap.get(b.getAiPositionFour()) + labelStart - 0x20), 0,
                            raw, start + 112, 4);
                }
                System.arraycopy(b.getUnknownThree(), 0, raw, start + 116, 0x18);
            }
        }
        List<Integer> sorted = sortPointers(raw, pointerOne, labelStart);
        for(int x = 0; x < sorted.size(); x++) {
            System.arraycopy(toByteArray(sorted.get(x)), 0, raw, factionStart + x * 4, 4);
        }
        return raw;
    }

    /**
     * Converts a string to an array of bytes and adds it to the label region
     * of the current file.
     *
     * @param bytes The bytes that make up the label region of the file.
     * @param label The string to be added to the label region.
     */
    private void addLabel(List<Byte> bytes, String label) {
        try {
            for(byte b : label.getBytes("shift-jis"))
                bytes.add(b);
            bytes.add((byte) 0);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sorts a list of pointers to recreate the ordering used in vanilla
     * 3DS Fire Emblem dispo files.
     *
     * @param raw The raw bytes of the current file.
     * @param pointers A list containing every address to be included in pointer region one.
     * @param labelStart The address where labels begin in the current file.
     * @return A list of pointers sorted to match a typical 3DS Fire Emblem file.
     */
    private List<Integer> sortPointers(byte[] raw, List<Integer> pointers, int labelStart) {
        List<Pair<Integer, Integer>> labelPtrs = new ArrayList<>();
        List<Integer> sorted = new ArrayList<>();
        for(int x = 0; x < factions.size(); x++)
            sorted.add(pointers.get((x * 2) + 1));
        for(int x = 0; x < factions.size(); x++)
            sorted.add(pointers.get(x * 2));
        for (int x = factions.size() * 2; x < pointers.size(); x++) {
            int pointer = pointers.get(x);
            Pair<Integer, Integer> pair = new Pair<>(pointer, toInt(raw, pointer + 0x20));
            if (pair.getValue() >= labelStart - 0x20) {
                labelPtrs.add(pair);
            }
        }


        // Sort labels.
        // Label pointers are sorted by key. Groups are sorted by value.
        int min;
        for (int i = 0; i < labelPtrs.size(); i++) {
            min = i;
            for (int j = i + 1; j < labelPtrs.size(); j++) {
                if (labelPtrs.get(j).getKey() < labelPtrs.get(min).getKey()) {
                    min = j;
                }
            }
            Pair<Integer, Integer> temp = labelPtrs.get(i);
            labelPtrs.set(i, labelPtrs.get(min));
            labelPtrs.set(min, temp);
        }
        List<Pair<Integer, Integer>> sortedLabels = new ArrayList<>();
        for (int x = 0; x < labelPtrs.size(); x++) {
            if (sortedLabels.contains(labelPtrs.get(x)))
                continue;
            List<Pair<Integer, Integer>> grouped = new ArrayList<>();
            grouped.add(labelPtrs.get(x));
            for (int y = x + 1; y < labelPtrs.size(); y++) {
                if (Objects.equals(labelPtrs.get(x).getValue(), labelPtrs.get(y).getValue()))
                    grouped.add(labelPtrs.get(y));
            }
            for (int i = 0; i < grouped.size(); i++) {
                min = i;
                for (int j = i + 1; j < grouped.size(); j++) {
                    if (grouped.get(j).getValue() < grouped.get(min).getValue()) {
                        min = j;
                    }
                }
                Pair<Integer, Integer> temp = grouped.get(i);
                grouped.set(i, grouped.get(min));
                grouped.set(min, temp);
            }
            sortedLabels.addAll(grouped);
        }

        for(Pair<Integer, Integer> p : sortedLabels) {
            sorted.add(p.getKey());
        }
        return sorted;
    }

    public DispoFaction getByName(String input) {
        for (DispoFaction f : factions) {
            if (f.getName().equals(input))
                return f;
        }
        return null;
    }

    public ArrayList<DispoFaction> getFactions() {
        return factions;
    }
}
