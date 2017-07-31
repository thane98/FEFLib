package feflib.fates.castle.join;

import java.io.IOException;
import java.util.Arrays;

import static feflib.utils.ByteUtils.getStringFromPointer;
import static feflib.utils.ByteUtils.toInt;

public class JoinBlock {
    private String character = "";
    private String birthrightJoin = "";
    private String conquestJoin = "";
    private String revelationJoin = "";
    private byte[] unknownOne;

    public JoinBlock(byte[] raw, int start) {
        try {
            read(raw, start);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JoinBlock() {
        character = "PlaceholderName";
    }

    public JoinBlock(JoinBlock j) {
        this.character = "PlaceholderName";
        this.birthrightJoin = j.getBirthrightJoin();
        this.conquestJoin = j.getConquestJoin();
        this.revelationJoin = j.getRevelationJoin();
        this.unknownOne = j.getUnknownOne();
    }


    public void read(byte[] raw, int start) throws IOException {
        character = getStringFromPointer(raw, start);
        if (toInt(raw, start + 4) != 0)
            birthrightJoin = getStringFromPointer(raw, start + 4);
        if (toInt(raw, start + 8) != 0)
            conquestJoin = getStringFromPointer(raw, start + 8);
        if (toInt(raw, start + 12) != 0)
            revelationJoin = getStringFromPointer(raw, start + 12);
        unknownOne = Arrays.copyOfRange(raw, start + 16, start + 28);
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getBirthrightJoin() {
        return birthrightJoin;
    }

    public void setBirthrightJoin(String birthrightJoin) {
        this.birthrightJoin = birthrightJoin;
    }

    public String getConquestJoin() {
        return conquestJoin;
    }

    public void setConquestJoin(String conquestJoin) {
        this.conquestJoin = conquestJoin;
    }

    public String getRevelationJoin() {
        return revelationJoin;
    }

    public void setRevelationJoin(String revelationJoin) {
        this.revelationJoin = revelationJoin;
    }

    public byte[] getUnknownOne() {
        return unknownOne;
    }

    public void setUnknownOne(byte[] unknownOne) {
        this.unknownOne = unknownOne;
    }
}
