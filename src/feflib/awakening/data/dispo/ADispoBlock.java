package feflib.awakening.data.dispo;

import java.io.IOException;
import java.util.Arrays;

import static feflib.utils.ByteUtils.getStringFromPointer;
import static feflib.utils.ByteUtils.toInt;

public class ADispoBlock {
    private String pid = "Placeholder";
    private String ac = "";
    private String acParam = "";
    private String mi = "";
    private String miParam = "";
    private String at = "";
    private String atParam = "";
    private String mv = "";
    private String mvParam = "";
    private String[] items = new String[5];

    private byte[] unknown = new byte[4];
    private byte[] unknownOne = new byte[0xC];
    private byte[] unknownTwo = new byte[2];
    private byte[] coordOne = new byte[2];
    private byte[] coordTwo = new byte[2];
    private byte[] unknownThree = new byte[2];
    private byte[][] itemBitflags = new byte[5][4];
    private byte[] unknownFour = new byte[0x10];

    public ADispoBlock(byte[] raw, int start) {
        for(int x = 0; x < items.length; x++)
            items[x] = "";
        try {
            read(raw, start);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ADispoBlock(String pid) {
        this.pid = pid;
    }

    public ADispoBlock(ADispoBlock src) {
        this.pid = src.getPid();
        this.ac = src.getAc();
        this.acParam = src.getAcParam();
        this.mi = src.getMi();
        this.miParam = src.getMiParam();
        this.at = src.getAt();
        this.atParam = src.getAtParam();
        this.mv = src.getMv();
        this.mvParam = src.getMvParam();
        this.unknown = Arrays.copyOf(src.getUnknown(), 4);
        this.unknownOne = Arrays.copyOf(src.getUnknownOne(), 0xC);
        this.unknownTwo = Arrays.copyOf(src.getUnknown(), 2);
        this.coordOne = Arrays.copyOf(src.getCoordOne(), 2);
        this.coordTwo = Arrays.copyOf(src.getCoordTwo(), 2);
        this.unknownThree = Arrays.copyOf(src.getUnknownThree(), 2);
        System.arraycopy(src.getItems(), 0, items, 0, 5);
        for(int x = 0; x < 5; x++) {
            System.arraycopy(src.getItemBitflags()[x], 0, itemBitflags[x], 0, 4);
        }
        this.unknownFour = Arrays.copyOf(unknownFour, 0x10);
    }

    private void read(byte[] raw, int start) throws IOException {
        pid = getStringFromPointer(raw, start);
        unknownOne = Arrays.copyOfRange(raw, start + 4, start + 16);
        unknown = Arrays.copyOfRange(raw, start + 16, start + 20);
        unknownTwo = Arrays.copyOfRange(raw, start + 20, start + 22);
        coordTwo = Arrays.copyOfRange(raw, start + 22, start + 24);
        coordOne = Arrays.copyOfRange(raw, start + 24, start + 26);
        unknownThree = Arrays.copyOfRange(raw, start + 26, start + 28);
        for(int x = 0; x < 5; x++) {
            if(toInt(raw, start + 28 + x * 8) == 0)
                break;
            items[x] = getStringFromPointer(raw, start + 28 + x * 8);
            itemBitflags[x] = Arrays.copyOfRange(raw, start + 32 + x * 8, start + 36 + x * 8);
        }
        if (toInt(raw, start + 68) != 0)
            ac = getStringFromPointer(raw, start + 68);
        if (toInt(raw, start + 72) != 0)
            acParam = getStringFromPointer(raw, start + 72);
        if (toInt(raw, start + 76) != 0)
            mi = getStringFromPointer(raw, start + 76);
        if (toInt(raw, start + 80) != 0)
            miParam = getStringFromPointer(raw, start + 80);
        if (toInt(raw, start + 84) != 0)
            at = getStringFromPointer(raw, start + 84);
        if (toInt(raw, start + 88) != 0)
            atParam = getStringFromPointer(raw, start + 88);
        if (toInt(raw, start + 92) != 0)
            mv = getStringFromPointer(raw, start + 92);
        if (toInt(raw, start + 96) != 0)
            mvParam = getStringFromPointer(raw, start + 96);
        unknownFour = Arrays.copyOfRange(raw, start + 100, start + 116);
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getAcParam() {
        return acParam;
    }

    public void setAcParam(String acParam) {
        this.acParam = acParam;
    }

    public String getMi() {
        return mi;
    }

    public void setMi(String mi) {
        this.mi = mi;
    }

    public String getMiParam() {
        return miParam;
    }

    public void setMiParam(String miParam) {
        this.miParam = miParam;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getAtParam() {
        return atParam;
    }

    public void setAtParam(String atParam) {
        this.atParam = atParam;
    }

    public String getMv() {
        return mv;
    }

    public void setMv(String mv) {
        this.mv = mv;
    }

    public String getMvParam() {
        return mvParam;
    }

    public void setMvParam(String mvParam) {
        this.mvParam = mvParam;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }

    public void setItem(int index, String item) {
        items[index] = item;
    }

    public byte[] getUnknown() {
        return unknown;
    }

    public void setUnknown(byte[] unknown) {
        this.unknown = unknown;
    }

    public byte[] getUnknownOne() {
        return unknownOne;
    }

    public void setUnknownOne(byte[] unknownOne) {
        this.unknownOne = unknownOne;
    }

    public byte[] getUnknownTwo() {
        return unknownTwo;
    }

    public void setUnknownTwo(byte[] unknownTwo) {
        this.unknownTwo = unknownTwo;
    }

    public byte[] getCoordOne() {
        return coordOne;
    }

    public void setCoordOne(byte[] coordOne) {
        this.coordOne = coordOne;
    }

    public byte[] getCoordTwo() {
        return coordTwo;
    }

    public void setCoordTwo(byte[] coordTwo) {
        this.coordTwo = coordTwo;
    }

    public byte[] getUnknownThree() {
        return unknownThree;
    }

    public void setUnknownThree(byte[] unknownThree) {
        this.unknownThree = unknownThree;
    }

    public byte[][] getItemBitflags() {
        return itemBitflags;
    }

    public void setItemBitflags(byte[][] itemBitflags) {
        this.itemBitflags = itemBitflags;
    }

    public byte[] getUnknownFour() {
        return unknownFour;
    }

    public void setUnknownFour(byte[] unknownFour) {
        this.unknownFour = unknownFour;
    }
}