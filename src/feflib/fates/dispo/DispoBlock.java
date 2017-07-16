package feflib.fates.dispo;

import java.io.IOException;
import java.util.Arrays;

import static feflib.utils.ByteUtils.*;

public class DispoBlock {
    private String pid = "Placeholder";
    private String ac = "";
    private String aiPositionOne = "";
    private String mi = "";
    private String aiPositionTwo = "";
    private String at = "";
    private String aiPositionThree = "";
    private String mv = "";
    private String aiPositionFour = "";

    private byte team = 0;
    private byte level = 0;

    private int skillFlag = 0;

    private byte[] unknownOne = new byte[4];
    private byte[] secondCoord = new byte[2];
    private byte[] firstCoord = new byte[2];
    private byte[] unknownTwo = new byte[2];
    private byte[] spawnBitflags = new byte[4];
    private byte[][] itemBitflags = new byte[5][4];
    private byte[] unknownThree = new byte[0x18];

    private String[] items = new String[5];
    private String[] skills = new String[5];

    public DispoBlock(byte[] raw, int start) {
        for (int x = 0; x < 5; x++) {
            items[x] = "";
            skills[x] = "";
        }
        try {
            read(raw, start);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DispoBlock(String pid) {
        for (int x = 0; x < 5; x++) {
            items[x] = "";
            skills[x] = "";
        }
        this.pid = pid;
    }

    public DispoBlock(DispoBlock src) {
        this.pid = src.getPid();
        this.ac = src.getAc();
        this.aiPositionOne = src.getAiPositionOne();
        this.mi = src.getMi();
        this.aiPositionTwo = src.getAiPositionTwo();
        this.at = src.getAt();
        this.aiPositionThree = src.getAiPositionThree();
        this.mv = src.getMv();
        this.aiPositionFour = src.getAiPositionFour();
        this.team = src.getTeam();
        this.level = src.getLevel();
        this.skillFlag = src.getSkillFlag();
        this.unknownOne = src.getUnknownOne();
        this.secondCoord = src.getSecondCoord();
        this.firstCoord = src.getFirstCoord();
        this.unknownTwo = src.getUnknownTwo();
        this.spawnBitflags = src.getSpawnBitflags();
        this.itemBitflags = src.getItemBitflags();
        this.unknownThree = src.getUnknownThree();
        this.items = new String[src.getItems().length];
        System.arraycopy(src.getItems(), 0, items, 0, items.length);
        this.skills = new String[src.getSkills().length];
        System.arraycopy(src.getSkills(), 0, skills, 0, skills.length);
    }

    private void read(byte[] raw, int start) throws IOException {
        pid = getStringFromPointer(raw, start);
        unknownOne = Arrays.copyOfRange(raw, start + 4, start + 8);
        team = raw[start + 8];
        level = raw[start + 9];
        secondCoord = Arrays.copyOfRange(raw, start + 10, start + 12);
        firstCoord = Arrays.copyOfRange(raw, start + 12, start + 14);
        unknownTwo = Arrays.copyOfRange(raw, start + 14, start + 16);
        spawnBitflags = Arrays.copyOfRange(raw, start + 16, start + 20);
        for (int x = 0; x < 5; x++) {
            if (toInt(raw, start + 20 + x * 8) != 0) {
                items[x] = getStringFromPointer(raw, start + 20 + x * 8);
                itemBitflags[x] = Arrays.copyOfRange(raw, start + 24 + x * 8, start + 28 + x * 8);
            }
        }
        for (int x = 0; x < 5; x++) {
            if (toInt(raw, start + 60 + x * 4) != 0) {
                skills[x] = getStringFromPointer(raw, start + 60 + x * 4);
            }
        }
        skillFlag = toInt(raw, start + 80);
        if (toInt(raw, start + 84) != 0)
            ac = getStringFromPointer(raw, start + 84);
        if (toInt(raw, start + 88) != 0)
            aiPositionOne = getStringFromPointer(raw, start + 88);
        if (toInt(raw, start + 92) != 0)
            mi = getStringFromPointer(raw, start + 92);
        if (toInt(raw, start + 96) != 0)
            aiPositionTwo = getStringFromPointer(raw, start + 96);
        if (toInt(raw, start + 100) != 0)
            at = getStringFromPointer(raw, start + 100);
        if (toInt(raw, start + 104) != 0)
            aiPositionThree = getStringFromPointer(raw, start + 104);
        if (toInt(raw, start + 108) != 0)
            mv = getStringFromPointer(raw, start + 108);
        if (toInt(raw, start + 112) != 0)
            aiPositionFour = getStringFromPointer(raw, start + 112);
        unknownThree = Arrays.copyOfRange(raw, start + 116, start + 116 + 0x18);
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

    public String getMi() {
        return mi;
    }

    public void setMi(String mi) {
        this.mi = mi;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getMv() {
        return mv;
    }

    public void setMv(String mv) {
        this.mv = mv;
    }

    public byte getTeam() {
        return team;
    }

    public void setTeam(byte team) {
        this.team = team;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public int getSkillFlag() {
        return skillFlag;
    }

    public void setSkillFlag(int skillFlag) {
        this.skillFlag = skillFlag;
    }

    public byte[] getUnknownOne() {
        return unknownOne;
    }

    public void setUnknownOne(byte[] unknownOne) {
        this.unknownOne = unknownOne;
    }

    public byte[] getSecondCoord() {
        return secondCoord;
    }

    public void setSecondCoord(byte[] secondCoord) {
        this.secondCoord = secondCoord;
    }

    public byte[] getFirstCoord() {
        return firstCoord;
    }

    public void setFirstCoord(byte[] firstCoord) {
        this.firstCoord = firstCoord;
    }

    public byte[] getUnknownTwo() {
        return unknownTwo;
    }

    public void setUnknownTwo(byte[] unknownTwo) {
        this.unknownTwo = unknownTwo;
    }

    public byte[] getSpawnBitflags() {
        return spawnBitflags;
    }

    public void setSpawnBitflags(byte[] spawnBitflags) {
        this.spawnBitflags = spawnBitflags;
    }

    public byte[][] getItemBitflags() {
        return itemBitflags;
    }

    public void setItemBitflags(byte[][] itemBitflags) {
        this.itemBitflags = itemBitflags;
    }

    public String[] getItems() {
        return items;
    }

    public void setItem(String item, int index) {
        this.items[index] = item;
    }

    public String[] getSkills() {
        return skills;
    }

    public void setSkill(String skill, int index) {
        this.skills[index] = skill;
    }

    public String getAiPositionOne() {
        return aiPositionOne;
    }

    public void setAiPositionOne(String aiPositionOne) {
        this.aiPositionOne = aiPositionOne;
    }

    public String getAiPositionTwo() {
        return aiPositionTwo;
    }

    public void setAiPositionTwo(String aiPositionTwo) {
        this.aiPositionTwo = aiPositionTwo;
    }

    public String getAiPositionThree() {
        return aiPositionThree;
    }

    public void setAiPositionThree(String aiPositionThree) {
        this.aiPositionThree = aiPositionThree;
    }

    public String getAiPositionFour() {
        return aiPositionFour;
    }

    public void setAiPositionFour(String aiPositionFour) {
        this.aiPositionFour = aiPositionFour;
    }

    public byte[] getUnknownThree() {
        return unknownThree;
    }
}