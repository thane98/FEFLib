package feflib.fates.gamedata.dispo;

import feflib.utils.ByteUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class DispoFaction {
    private String name;
    private ArrayList<DispoBlock> spawns = new ArrayList<>();

    public DispoFaction(byte[] raw, int start) {
        try {
            name = ByteUtils.getStringFromPointer(raw, start);
            int blockStart = ByteUtils.toInt(raw, start + 4) + 0x20;
            int count = ByteUtils.toInt(raw, start + 8);
            for(int x = 0; x < count; x++) {
                spawns.add(new DispoBlock(raw, blockStart + x * 0x8C));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public DispoFaction(String name) {
        this.name = name;
    }

    public boolean containsByPid(String input) {
        for (DispoBlock d : spawns) {
            if (d.getPid().equals(input))
                return true;
        }
        return false;
    }

    public DispoBlock getByPid(String input) {
        for (DispoBlock d : spawns) {
            if (d.getPid().equals(input))
                return d;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<DispoBlock> getSpawns() {
        return spawns;
    }

    public void addSpawn(DispoBlock block) {
        spawns.add(block);
    }
}
