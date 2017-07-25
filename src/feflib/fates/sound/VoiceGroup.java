package feflib.fates.sound;

import feflib.utils.ByteUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class VoiceGroup {
    private List<SoundEntry> entries = new ArrayList<>();
    private int size;
    private String mainLabel;

    VoiceGroup() {}

    VoiceGroup(byte[] bytes, int start) {
        try {
            mainLabel = ByteUtils.getString(bytes, ByteUtils.toInt(bytes, start + 4) + 0x20);
            int count = ByteUtils.toInt(bytes, start + 8);

            for(int x = 0; x < count; x++) {
                SoundEntry entry = new SoundEntry(bytes, start + 0x10 + 0x8 * x);
                entries.add(entry);
            }
            size = 0x10 + count * 8;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public List<SoundEntry> getEntries() {
        return entries;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int value) { size = value; }

    public void setMainLabel(String label) { this.mainLabel = label; }

    public String getMainLabel() {
        return mainLabel;
    }
}
