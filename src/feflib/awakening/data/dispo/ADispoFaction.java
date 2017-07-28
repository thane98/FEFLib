package feflib.awakening.data.dispo;

import feflib.utils.ByteUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ADispoFaction {
    private String name;
    private ArrayList<ADispoBlock> spawns = new ArrayList<>();

    public ADispoFaction(byte[] raw, int start) {
        try {
            name = ByteUtils.getStringFromPointer(raw, start);
            int blockStart = ByteUtils.toInt(raw, start + 4) + 0x20;
            int count = ByteUtils.toInt(raw, start + 8);
            for(int x = 0; x < count; x++) {
                spawns.add(new ADispoBlock(raw, blockStart + x * 0x74));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public ADispoFaction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ADispoBlock> getSpawns() {
        return spawns;
    }

    public void addSpawn(ADispoBlock block) {
        spawns.add(block);
    }
}
