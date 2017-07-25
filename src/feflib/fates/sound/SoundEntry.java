package feflib.fates.sound;

import feflib.utils.ByteUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class SoundEntry {
    private String label;
    private byte[] tag;
    SoundEntry(byte[] source, int start) {
        try {
            label = ByteUtils.getString(source, ByteUtils.toInt(source, start) + 0x20);
            tag = Arrays.copyOfRange(source, start + 4, start + 8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    SoundEntry() {}

    public String getLabel() {
        return label;
    }

    public byte[] getTag() {
        return tag;
    }

    public void setLabel(String value) { label = value; }

    public void setTag(byte[] value) { tag = value; }
}
