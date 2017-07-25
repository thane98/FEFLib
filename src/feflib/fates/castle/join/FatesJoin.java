package feflib.fates.castle.join;

import javafx.util.Pair;

import static feflib.utils.ByteUtils.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FatesJoin {
    private ArrayList<JoinBlock> blocks = new ArrayList<>();

    public FatesJoin(File file) {
        try {
            byte[] raw = Files.readAllBytes(file.toPath());
            readBlocks(raw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the file's table entries into join blocks.
     *
     * @param raw The array of bytes from the file being opened.
     * @throws IOException An exception will occur if shift-jis encoding is not supported.
     */
    private void readBlocks(byte[] raw) throws IOException {
        int length = toInt(raw, 0x20);
        for (int x = 0; x < length; x++) {
            blocks.add(new JoinBlock(raw, 0x28 + 0x20 * x));
        }
    }

    /**
     * Serializes the current list of join blocks to a binary format compatible
     * with Fire Emblem Fates
     *
     * @return The serialized Join file.
     */
    public byte[] serialize() {
        HashMap<String, Integer> labelMap = new HashMap<>();
        List<Byte> labelBytes = new ArrayList<>();
        List<Integer> pointerOne = new ArrayList<>();

        // Map labels.
        for(int x = 0; x < blocks.size(); x++) {
            JoinBlock j = blocks.get(x);
            labelMap.put(j.getCharacter(), labelBytes.size());
            addLabel(labelBytes, j.getCharacter());
            pointerOne.add(0x8 + x * 0x20);
            if(!j.getBirthrightJoin().equals("")) {
                if(!labelMap.containsKey(j.getBirthrightJoin())) {
                    labelMap.put(j.getBirthrightJoin(), labelBytes.size());
                    addLabel(labelBytes, j.getBirthrightJoin());
                }
                pointerOne.add(0xC + x * 0x20);
            }
            if(!j.getConquestJoin().equals("")) {
                if(!labelMap.containsKey(j.getConquestJoin())) {
                    labelMap.put(j.getConquestJoin(), labelBytes.size());
                    addLabel(labelBytes, j.getConquestJoin());
                }
                pointerOne.add(0x10 + x * 0x20);
            }
            if(!j.getRevelationJoin().equals("")) {
                if(!labelMap.containsKey(j.getRevelationJoin())) {
                    labelMap.put(j.getRevelationJoin(), labelBytes.size());
                    addLabel(labelBytes, j.getRevelationJoin());
                }
                pointerOne.add(0x14 + x * 0x20);
            }
        }

        // Begin serializing.
        int pointerStart = blocks.size() * 0x20 + 0x24;
        int labelStart = pointerStart + pointerOne.size() * 4;
        byte[] raw = new byte[labelStart + labelBytes.size()];

        System.arraycopy(toByteArray(raw.length), 0, raw, 0, 4);
        System.arraycopy(toByteArray(pointerStart - 0x20), 0, raw, 4, 4);
        System.arraycopy(toByteArray(pointerOne.size()), 0, raw, 8, 4);
        System.arraycopy(toByteArray(blocks.size()), 0, raw, 0x20, 4);
        for(int x = 0; x < blocks.size(); x++) {
            JoinBlock j = blocks.get(x);
            int start = 0x28 + x * 0x20;
            System.arraycopy(toByteArray(labelMap.get(j.getCharacter()) + labelStart - 0x20), 0,
                    raw, start, 4);
            if(!j.getBirthrightJoin().equals("")) {
                System.arraycopy(toByteArray(labelMap.get(j.getBirthrightJoin()) + labelStart - 0x20), 0,
                        raw, start + 4, 4);
            }
            if(!j.getConquestJoin().equals("")) {
                System.arraycopy(toByteArray(labelMap.get(j.getConquestJoin()) + labelStart - 0x20), 0,
                        raw, start + 8, 4);
            }
            if(!j.getRevelationJoin().equals("")) {
                System.arraycopy(toByteArray(labelMap.get(j.getRevelationJoin()) + labelStart - 0x20), 0,
                        raw, start + 12, 4);
            }
            System.arraycopy(j.getUnknownOne(), 0, raw, start + 16, 0xC);
            System.arraycopy(toByteArray(j.getUnknownTwo()), 0, raw, start + 28, 4);
        }

        List<Integer> sorted = sortPointers(raw, pointerOne);
        for(int x = 0; x < sorted.size(); x++) {
            System.arraycopy(toByteArray(sorted.get(x)), 0, raw, pointerStart + x * 4, 4);
        }
        for(int x = 0; x < labelBytes.size(); x++) {
            raw[labelStart + x] = labelBytes.get(x);
        }
        return raw;
    }

    /**
     * Sorts a list of pointers to recreate the ordering used in the vanilla
     * castle_join file.
     *
     * @param raw The raw bytes of the current file.
     * @param pointers A list containing every address to be included in pointer region one.
     * @return A list of pointers sorted to match a typical 3DS Fire Emblem file.
     */
    private List<Integer> sortPointers(byte[] raw, List<Integer> pointers) {
        List<Pair<Integer, Integer>> labelPtrs = new ArrayList<>();
        List<Integer> sorted = new ArrayList<>();
        for (Integer pointer : pointers) {
            labelPtrs.add(new Pair<>(pointer, toInt(raw, pointer + 0x20)));
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

    public JoinBlock getByPid(String pid) {
        for (JoinBlock j : blocks) {
            if (j.getCharacter().equals(pid))
                return j;
        }
        return blocks.get(0);
    }

    public boolean containsByPid(String pid) {
        for (JoinBlock j : blocks) {
            if (j.getCharacter().equals(pid))
                return true;
        }
        return false;
    }

    public void addBlock(String pid, String birthright, String conquest, String revelation) {
        JoinBlock block = new JoinBlock();
        block.setCharacter(pid);
        block.setBirthrightJoin(birthright);
        block.setConquestJoin(conquest);
        block.setRevelationJoin(revelation);
        byte[] temp = new byte[0xC];
        for (int x = 0; x < temp.length; x++)
            temp[x] = -1;
        block.setUnknownOne(temp);
        block.setUnknownTwo(1);
        blocks.add(block);
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

    public ArrayList<JoinBlock> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<JoinBlock> blocks) {
        this.blocks = blocks;
    }
}
