package feflib.fates.person;

import static feflib.utils.ByteUtils.*;

import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.*;

public class FatesPerson {
    private ArrayList<PersonBlock> characters = new ArrayList<>();
    private String fileName;

    public FatesPerson(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            parseTable(bytes);
            fileName = file.getName().substring(0, file.getName().indexOf('.'));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the file's table entries into character blocks.
     *
     * @param bytes The array of bytes from the file being opened.
     * @throws IOException An exception will occur if shift-jis encoding is not supported.
     */
    private void parseTable(byte[] bytes) throws IOException {
        fileName = getStringFromPointer(bytes, 0x20);

        int count = toShort(bytes, 0x24);
        for (int x = 0; x < count; x++) {
            PersonBlock block = new PersonBlock(bytes, 0x30 + x * 0x98);
            characters.add(block);
        }
    }

    /**
     * Serializes the current list of characters to a binary format compatible
     * with 3DS Fire Emblem games. Uses the name from the original file instead
     * of a new name.
     *
     * @return The serialized Person file.
     */
    public byte[] serialize() {
        return serialize(fileName);
    }

    /**
     * Serializes the current list of characters to a binary format compatible
     * with 3DS Fire Emblem games.
     *
     * @param fileName The name of the target file.
     * @return The serialized Person file.
     */
    public byte[] serialize(String fileName) {
        HashMap<String, Integer> labelMap = new HashMap<>();
        HashMap<List<Byte>, Integer> stanceMap = new HashMap<>();
        List<Byte> labelBytes = new ArrayList<>();
        List<Byte> stanceBytes = new ArrayList<>();
        List<Integer> pointerOne = new ArrayList<>();
        List<Pair<Integer, Integer>> pointerTwo = new ArrayList<>();


        // Add labels to the map. PIDs first, then everything else.
        for (PersonBlock c : characters) {
            labelMap.put(c.getPid(), labelBytes.size());
            this.addLabel(labelBytes, c.getPid());
        }
        labelMap.put(fileName, labelBytes.size());
        this.addLabel(labelBytes, fileName);
        pointerOne.add(0);
        for (int x = 0; x < characters.size(); x++) {
            PersonBlock c = characters.get(x);
            pointerTwo.add(new Pair<>(0x10 + 0x98 * x, labelMap.get(c.getPid())));
            pointerOne.add(0x10 + 0x98 * x + 8);
            pointerOne.add(0x10 + 0x98 * x + 28);
            pointerOne.add(0x10 + 0x98 * x + 32);

            // Labels
            if (!c.getFid().equals("")) {
                if (!labelMap.containsKey(c.getFid())) {
                    labelMap.put(c.getFid(), labelBytes.size());
                    this.addLabel(labelBytes, c.getFid());
                }
                pointerOne.add(0x10 + 0x98 * x + 12);
            }
            if (!c.getAid().equals("")) {
                if (!labelMap.containsKey(c.getAid())) {
                    labelMap.put(c.getAid(), labelBytes.size());
                    this.addLabel(labelBytes, c.getAid());
                }
                pointerOne.add(0x10 + 0x98 * x + 16);
            }
            if (!c.getMPid().equals("")) {
                if (!labelMap.containsKey(c.getMPid())) {
                    labelMap.put(c.getMPid(), labelBytes.size());
                    this.addLabel(labelBytes, c.getMPid());
                }
                pointerOne.add(0x10 + 0x98 * x + 20);
            }
            if (!c.getMPidH().equals("")) {
                if (!labelMap.containsKey(c.getMPidH())) {
                    labelMap.put(c.getMPidH(), labelBytes.size());
                    this.addLabel(labelBytes, c.getMPidH());
                }
                pointerOne.add(0x10 + 0x98 * x + 24);
            }
            if (!c.getCombatMusic().equals("")) {
                if (!labelMap.containsKey(c.getCombatMusic())) {
                    labelMap.put(c.getCombatMusic(), labelBytes.size());
                    this.addLabel(labelBytes, c.getCombatMusic());
                }
                pointerOne.add(0x10 + 0x98 * x + 136);
            }
            if (!c.getEnemyVoice().equals("")) {
                if (!labelMap.containsKey(c.getEnemyVoice())) {
                    labelMap.put(c.getEnemyVoice(), labelBytes.size());
                    this.addLabel(labelBytes, c.getEnemyVoice());
                }
                pointerOne.add(0x10 + 0x98 * x + 140);
            }

            // Stances.
            List<Byte> temp = byteArrayToImmutableList(c.getAttackBonuses());
            if (!stanceMap.containsKey(temp)) {
                stanceMap.put(temp, stanceBytes.size());
                stanceBytes.addAll(temp);
            }
            temp = byteArrayToImmutableList(c.getGuardBonuses());
            if (!stanceMap.containsKey(temp)) {
                stanceMap.put(temp, stanceBytes.size());
                stanceBytes.addAll(temp);
            }
        }

        int stanceStart = 0x30 + characters.size() * 0x98;
        int pointerOneStart = stanceStart + stanceBytes.size() + 4;
        int pointerTwoStart = pointerOneStart + pointerOne.size() * 4;
        int labelStart = pointerTwoStart + pointerTwo.size() * 8;
        int ptrLabelStart = labelStart - 0x20;
        byte[] raw = new byte[labelStart + labelBytes.size()];
        System.arraycopy(toByteArray(raw.length), 0, raw, 0, 4);
        System.arraycopy(toByteArray(pointerOneStart - 0x20), 0, raw, 4, 4);
        System.arraycopy(toByteArray(pointerOne.size()), 0, raw, 8, 4);
        System.arraycopy(toByteArray(pointerTwo.size()), 0, raw, 12, 4);
        System.arraycopy(toByteArray(ptrLabelStart + labelMap.get(fileName)), 0, raw, 0x20, 4);
        System.arraycopy(toByteArray(characters.size()), 0, raw, 0x24, 2);
        raw[0x26] = 0x7A;
        for (int x = 0; x < stanceBytes.size(); x++) {
            raw[stanceStart + x] = stanceBytes.get(x);
        }
        for (int x = 0; x < labelBytes.size(); x++) {
            raw[labelStart + x] = labelBytes.get(x);
        }

        // Serialize character blocks.
        for (int x = 0; x < characters.size(); x++) {
            PersonBlock c = characters.get(x);
            int start = 0x30 + x * 0x98;
            System.arraycopy(c.getBitflags(), 0, raw, start, 8);
            System.arraycopy(toByteArray(ptrLabelStart + labelMap.get(c.getPid())), 0,
                    raw, start + 8, 4);
            if (!c.getFid().equals("")) {
                System.arraycopy(toByteArray(ptrLabelStart + labelMap.get(c.getFid())), 0,
                        raw, start + 12, 4);
            }
            if (!c.getAid().equals("")) {
                System.arraycopy(toByteArray(ptrLabelStart + labelMap.get(c.getAid())), 0,
                        raw, start + 16, 4);
            }
            if (!c.getMPid().equals("")) {
                System.arraycopy(toByteArray(ptrLabelStart + labelMap.get(c.getMPid())), 0,
                        raw, start + 20, 4);
            }
            if (!c.getMPidH().equals("")) {
                System.arraycopy(toByteArray(ptrLabelStart + labelMap.get(c.getMPidH())), 0,
                        raw, start + 24, 4);
            }
            int ptr = stanceStart - 0x20 + stanceMap.get(byteArrayToImmutableList(c.getAttackBonuses()));
            System.arraycopy(toByteArray(ptr), 0, raw, start + 28, 4);
            ptr = stanceStart - 0x20 + stanceMap.get(byteArrayToImmutableList(c.getGuardBonuses()));
            System.arraycopy(toByteArray(ptr), 0, raw, start + 32, 4);
            System.arraycopy(toByteArray(c.getId()), 0, raw, start + 36, 2);
            raw[start + 38] = c.getSupportRoute();
            raw[start + 39] = c.getArmy();
            System.arraycopy(toByteArray(c.getReplacementId()), 0, raw, start + 40, 2);
            System.arraycopy(toByteArray(c.getParent()), 0, raw, start + 42, 2);
            System.arraycopy(toByteArray(c.getClasses()[0]), 0, raw, start + 44, 2);
            System.arraycopy(toByteArray(c.getClasses()[1]), 0, raw, start + 46, 2);
            System.arraycopy(toByteArray(c.getSupportId()), 0, raw, start + 48, 2);
            raw[start + 50] = c.getLevel();
            raw[start + 51] = c.getInternalLevel();
            raw[start + 52] = c.getEnemyFlag();
            System.arraycopy(c.getUnknownBytes(), 0, raw, start + 53, 3);
            System.arraycopy(c.getStats(), 0, raw, start + 56, 8);
            System.arraycopy(c.getGrowths(), 0, raw, start + 64, 3);
            System.arraycopy(c.getModifiers(), 0, raw, start + 72, 3);
            System.arraycopy(c.getUnknownLine(), 0, raw, start + 80, 16);
            System.arraycopy(c.getWeaponRanks(), 0, raw, start + 96, 8);
            for (int y = 0; y < 5; y++) {
                System.arraycopy(toByteArray(c.getSkills()[y]), 0, raw, start + 104 + y * 2, 2);
            }
            System.arraycopy(c.getSkillFlags(), 0, raw, start + 114, 2);
            for (int y = 0; y < 3; y++) {
                System.arraycopy(toByteArray(c.getPersonalSkills()[y]), 0, raw, start + 116 + y * 2, 2);
            }
            raw[start + 122] = c.getBirthday();
            raw[start + 123] = c.getBirthMonth();
            System.arraycopy(toByteArray(c.getReclasses()[0]), 0, raw, start + 124, 2);
            System.arraycopy(toByteArray(c.getReclasses()[1]), 0, raw, start + 126, 2);
            System.arraycopy(toByteArray(c.getParentId()), 0, raw, start + 128, 2);
            System.arraycopy(toByteArray(c.getChildId()), 0, raw, start + 130, 2);
            System.arraycopy(toByteArray(c.getSupportIndex()), 0, raw, start + 132, 2);
            raw[start + 134] = c.getLevelCap();
            raw[start + 135] = c.getAmiibo();
            if (!c.getCombatMusic().equals("")) {
                System.arraycopy(toByteArray(ptrLabelStart + labelMap.get(c.getCombatMusic())), 0,
                        raw, start + 136, 4);
            }
            if (!c.getEnemyVoice().equals("")) {
                System.arraycopy(toByteArray(ptrLabelStart + labelMap.get(c.getEnemyVoice())), 0,
                        raw, start + 140, 4);
            }
            System.arraycopy(toByteArray(c.getAmiiboWeapons()[0]), 0, raw, start + 144, 2);
            System.arraycopy(toByteArray(c.getAmiiboWeapons()[1]), 0, raw, start + 146, 2);
            System.arraycopy(c.getPadding(), 0, raw, start + 148, 4);
        }

        // Sort pointer one and write to pointer regions.
        List<Integer> sortedPointers = sortPointers(raw, pointerOne, labelStart);
        for (int x = 0; x < sortedPointers.size(); x++) {
            System.arraycopy(toByteArray(sortedPointers.get(x)), 0,
                    raw, pointerOneStart + x * 4, 4);
        }
        for (int x = 0; x < pointerTwo.size(); x++) {
            System.arraycopy(toByteArray(pointerTwo.get(x).getKey()), 0,
                    raw, pointerTwoStart + x * 8, 4);
            System.arraycopy(toByteArray(pointerTwo.get(x).getValue()), 0,
                    raw, pointerTwoStart + 4 + x * 8, 4);
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
            for (byte b : label.getBytes("shift-jis"))
                bytes.add(b);
            bytes.add((byte) 0);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sorts a list of pointers to recreate the ordering used in vanilla
     * 3DS Fire Emblem person files.
     *
     * @param raw        The raw bytes of the current file.
     * @param pointerOne A list containing every address to be included in pointer region one.
     * @param labelStart The address where labels begin in the current file.
     * @return A list of pointers sorted to match a typical 3DS Fire Emblem file.
     */
    private List<Integer> sortPointers(byte[] raw, List<Integer> pointerOne, int labelStart) {
        // Pair pointers with the value they point to.
        List<Integer> subPtrs = new ArrayList<>();
        List<Pair<Integer, Integer>> labelPtrs = new ArrayList<>();
        for (Integer aPointerOne : pointerOne) {
            int val = toInt(raw, aPointerOne + 0x20);
            if (val < labelStart - 0x20)
                subPtrs.add(aPointerOne);
            else
                labelPtrs.add(new Pair<>(aPointerOne, toInt(raw, aPointerOne + 0x20)));
        }

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

        // Sub pointers are sorted by key.
        Collections.sort(subPtrs);

        List<Integer> sorted = new ArrayList<>();
        sorted.addAll(subPtrs);
        for (Pair<Integer, Integer> p : sortedLabels)
            sorted.add(p.getKey());
        return sorted;
    }

    public PersonBlock getByPid(String pid) {
        for(PersonBlock c : characters) {
            if(c.getPid().equals(pid))
                return c;
        }
        return null;
    }

    public ArrayList<PersonBlock> getCharacters() {
        return characters;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}