package feflib.fates.inject;

import java.util.ArrayList;
import java.util.List;

public class InjectionData {
    private List<Integer> pointers = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private byte[] raw;
    private boolean pointerTwo;

    public InjectionData(int rawSize) {
        raw = new byte[rawSize];
    }

    public InjectionData(byte[] raw) {
        this.raw = raw;
    }

    public List<Integer> getPointers() {
        return pointers;
    }

    public void setPointers(List<Integer> pointers) { this.pointers = pointers; };

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) { this.labels = labels; }

    public byte[] getRaw() {
        return raw;
    }

    public boolean isPointerTwo() {
        return pointerTwo;
    }

    public void setPointerTwo(boolean pointerTwo) {
        this.pointerTwo = pointerTwo;
    }

    public void putBytes(int address, byte[] values) {
        for(int x = 0; x < values.length; x++)
            raw[address + x] = values[x];
    }

    public void putByte(int address, byte input) {
        raw[address] = input;
    }

    public void putShort(int address, short input) {
        raw[address + 1] = (byte) ((int) input >> 0x8);
        raw[address] = (byte) ((int) input & 0xFF);
    }

    public void putInt(int address, int input) {
        raw[address + 3] = (byte) (input >> 0x18);
        raw[address + 2] =
                (byte) ((input >> 0x10) & 0xFF);
        raw[address + 1] =
                (byte) ((input >> 0x8) & 0xFF);
        raw[address] = (byte) (input & 0xFF);
    }
}
