package feflib.fates.gamedata;

import feflib.utils.ByteUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.IOException;

import static feflib.utils.ByteUtils.getStringFromPointer;
import static feflib.utils.ByteUtils.toShort;

public class CharacterBlock {
    private IntegerProperty level = new SimpleIntegerProperty();
    private IntegerProperty internalLevel = new SimpleIntegerProperty();
    private IntegerProperty levelCap = new SimpleIntegerProperty();
    private IntegerProperty supportRoute = new SimpleIntegerProperty();
    private IntegerProperty parent = new SimpleIntegerProperty();
    private IntegerProperty supportId = new SimpleIntegerProperty();

    private IntegerProperty[] bitflags = new IntegerProperty[8];
    private IntegerProperty[] stats = new IntegerProperty[8];
    private IntegerProperty[] growths = new IntegerProperty[8];
    private IntegerProperty[] modifiers = new IntegerProperty[8];
    private IntegerProperty[] weaponRanks = new IntegerProperty[8];
    private IntegerProperty[] personalSkills = new IntegerProperty[3];
    private IntegerProperty[] skills = new IntegerProperty[5];
    private IntegerProperty[] classes = new IntegerProperty[2];
    private IntegerProperty[] reclasses = new IntegerProperty[2];

    // These should be considered read-only once assigned.
    private String pid;
    private String mPid;
    private short id;
    private short replacementId;
    private int blockStart;

    public CharacterBlock(byte[] bytes, int start) {
        blockStart = start;

        // Initialize observable value arrays.
        for(int x = 0; x < 8; x++)
            bitflags[x] = new SimpleIntegerProperty();
        for(int x = 0; x < 8; x++)
            stats[x] = new SimpleIntegerProperty();
        for(int x = 0; x < 8; x++)
            growths[x] = new SimpleIntegerProperty();
        for(int x = 0; x < 8; x++)
            modifiers[x] = new SimpleIntegerProperty();
        for(int x = 0; x < 8; x++)
            weaponRanks[x] = new SimpleIntegerProperty();
        for(int x = 0; x < 3; x++)
            personalSkills[x] = new SimpleIntegerProperty();
        for(int x = 0; x < 5; x++)
            skills[x] = new SimpleIntegerProperty();
        for(int x = 0; x < 2; x++)
            classes[x] = new SimpleIntegerProperty();
        for(int x = 0; x < 2; x++)
            reclasses[x] = new SimpleIntegerProperty();

        try {
            read(bytes, start);
            addListeners(bytes, start);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses data from a character block at the given address.
     *
     * @param raw The bytes that make up the file currently being opened.
     * @param start The index in the array to begin reading the block from.
     * @throws IOException An exception will occur if shift-jis encoding is not supported.
     */
    private void read(byte[] raw, int start) throws IOException {
        for(int x = 0; x < 8; x++) {
            bitflags[x].setValue(raw[start + x]);
        }
        pid = getStringFromPointer(raw, start + 8);
        mPid = getStringFromPointer(raw, start + 20);
        id = toShort(raw, start + 36);
        supportRoute.setValue(raw[start + 38]);
        replacementId = toShort(raw, start + 40);
        parent.setValue(toShort(raw, start + 42));
        classes[0].setValue(toShort(raw, start + 44));
        classes[1].setValue(toShort(raw, start + 46));
        supportId.setValue(toShort(raw, start + 48));
        level.setValue(raw[start + 50]);
        internalLevel.setValue(raw[start + 51]);
        for(int x = 0; x < 8; x++) {
            stats[x].setValue(raw[start + 56 + x]);
        }
        for(int x = 0; x < 8; x++) {
            growths[x].setValue(raw[start + 64 + x]);
        }
        for(int x = 0; x < 8; x++) {
            modifiers[x].setValue(raw[start + 72 + x]);
        }
        for(int x = 0; x < 8; x++) {
            weaponRanks[x].setValue(raw[start + 96 + x]);
        }
        for(int x = 0; x < 5; x++) {
            skills[x].setValue(toShort(raw, start + 104 + x * 2));
        }
        for(int x = 0; x < 3; x++) {
            personalSkills[x].setValue(toShort(raw, start + 116 + x * 2));
        }
        reclasses[0].setValue(toShort(raw, start + 124));
        reclasses[1].setValue(toShort(raw, start + 126));
        levelCap.setValue(raw[start + 134]);
    }

    private void addListeners(byte[] raw, int start) {
        level.addListener((observable, oldValue, newValue) -> raw[start + 50] = newValue.byteValue());
        internalLevel.addListener((observable, oldValue, newValue) -> raw[start + 51] = newValue.byteValue());
        levelCap.addListener((observable, oldValue, newValue) -> raw[start + 134] = newValue.byteValue());
        supportRoute.addListener((observable, oldValue, newValue) -> raw[start + 38] = newValue.byteValue());
        parent.addListener((observable, oldValue, newValue) ->
                System.arraycopy(ByteUtils.toByteArray(newValue.shortValue()), 0,
                        raw, start + 42, 2));
        supportId.addListener((observable, oldValue, newValue) ->
                System.arraycopy(ByteUtils.toByteArray(newValue.shortValue()), 0,
                        raw, start + 48, 2));
        for(int x = 0; x < 8; x++) {
            int i = x;
            bitflags[x].addListener((observable, oldValue, newValue) -> raw[start + i] = newValue.byteValue());
        }
        for(int x = 0; x < 8; x++) {
            int i = x;
            stats[x].addListener((observable, oldValue, newValue) -> raw[start + 56 + i] = newValue.byteValue());
        }
        for(int x = 0; x < 8; x++) {
            int i = x;
            growths[x].addListener((observable, oldValue, newValue) -> raw[start + 64 + i] = newValue.byteValue());
        }
        for(int x = 0; x < 8; x++) {
            int i = x;
            modifiers[x].addListener((observable, oldValue, newValue) -> raw[start + 72 + i] = newValue.byteValue());
        }
        for(int x = 0; x < 8; x++) {
            int i = x;
            weaponRanks[x].addListener((observable, oldValue, newValue) -> raw[start + 96 + i] = newValue.byteValue());
        }
        for(int x = 0; x < 5; x++) {
            int i = x;
            skills[x].addListener((observable, oldValue, newValue) ->
                    System.arraycopy(ByteUtils.toByteArray(newValue.shortValue()), 0,
                            raw, start + 104 + i * 2, 2));
        }
        for(int x = 0; x < 3; x++) {
            int i = x;
            personalSkills[x].addListener((observable, oldValue, newValue) ->
                    System.arraycopy(ByteUtils.toByteArray(newValue.shortValue()), 0,
                            raw, start + 116 + i * 2, 2));
        }
        for(int x = 0; x < 2; x++) {
            int i = x;
            classes[x].addListener((observable, oldValue, newValue) ->
                    System.arraycopy(ByteUtils.toByteArray(newValue.shortValue()), 0,
                    raw, start + 44 + i * 2, 2));
        }
        for(int x = 0; x < 2; x++) {
            int i = x;
            reclasses[x].addListener((observable, oldValue, newValue) ->
                    System.arraycopy(ByteUtils.toByteArray(newValue.shortValue()), 0,
                            raw, start + 124 + i * 2, 2));
        }
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getReplacementId() {
        return replacementId;
    }

    public void setReplacementId(short replacementId) {
        this.replacementId = replacementId;
    }

    public int getLevel() {
        return level.get();
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public void setLevel(byte level) {
        this.level.set(level);
    }

    public int getLevelCap() {
        return levelCap.get();
    }

    public IntegerProperty levelCapProperty() {
        return levelCap;
    }

    public void setLevelCap(byte levelCap) {
        this.levelCap.set(levelCap);
    }

    public int getSupportRoute() {
        return supportRoute.get();
    }

    public IntegerProperty supportRouteProperty() {
        return supportRoute;
    }

    public void setSupportRoute(byte supportRoute) {
        this.supportRoute.set(supportRoute);
    }

    public int getParent() {
        return parent.get();
    }

    public IntegerProperty parentProperty() {
        return parent;
    }

    public void setParent(short parent) {
        this.parent.set(parent);
    }
    
    public byte[] getBitflags() {
        byte[] bitflags = new byte[8];
        for(int x = 0; x < 8; x++)
            bitflags[x] = (byte) this.bitflags[x].get();
        return bitflags;
    }

    public IntegerProperty[] bitflagsProperty() {
        return bitflags;
    }

    public void setBitflags(byte[] bitflags) {
        for(int x = 0; x < 8; x++)
            this.bitflags[x].setValue(bitflags[x]);
    }

    public short[] getClasses() {
        short[] classes = new short[2];
        for(int x = 0; x < 2; x++)
            classes[x] = (short) this.classes[x].get();
        return classes;
    }

    public IntegerProperty[] classesProperty() {
        return classes;
    }

    public void setClasses(short[] classes) {
        for(int x = 0; x < 2; x++)
            this.classes[x].setValue(classes[x]);
    }

    public byte[] getStats() {
        byte[] stats = new byte[8];
        for(int x = 0; x < 8; x++)
            stats[x] = (byte) this.stats[x].get();
        return stats;
    }

    public IntegerProperty[] statsProperty() {
        return stats;
    }

    public void setStats(byte[] stats) {
        for(int x = 0; x < 8; x++)
            this.stats[x].setValue(stats[x]);
    }

    public byte[] getGrowths() {
        byte[] growths = new byte[8];
        for(int x = 0; x < 8; x++)
            growths[x] = (byte) this.growths[x].get();
        return growths;
    }

    public IntegerProperty[] growthsProperty() {
        return growths;
    }

    public void setGrowths(byte[] growths) {
        for(int x = 0; x < 8; x++)
            this.growths[x].setValue(growths[x]);
    }

    public byte[] getModifiers() {
        byte[] modifiers = new byte[8];
        for(int x = 0; x < 8; x++)
            modifiers[x] = (byte) this.modifiers[x].get();
        return modifiers;
    }

    public IntegerProperty[] modifiersProperty() {
        return modifiers;
    }

    public void setModifiers(byte[] modifiers) {
        for(int x = 0; x < 8; x++)
            this.modifiers[x].setValue(modifiers[x]);
    }

    public byte[] getWeaponRanks() {
        byte[] weaponRanks = new byte[8];
        for(int x = 0; x < 8; x++)
            weaponRanks[x] = (byte) this.weaponRanks[x].get();
        return weaponRanks;
    }

    public IntegerProperty[] weaponRanksProperty() {
        return weaponRanks;
    }

    public void setWeaponRanks(byte[] weaponRanks) {
        for(int x = 0; x < 8; x++)
            this.weaponRanks[x].setValue(weaponRanks[x]);
    }

    public int getInternalLevel() {
        return internalLevel.get();
    }

    public IntegerProperty internalLevelProperty() {
        return internalLevel;
    }

    public void setInternalLevel(int internalLevel) {
        this.internalLevel.set(internalLevel);
    }
    
    public short[] getReclasses() {
        short[] reclasses = new short[2];
        for(int x = 0; x < 2; x++)
            reclasses[x] = (short) this.reclasses[x].get();
        return reclasses;
    }

    public IntegerProperty[] reclassesProperty() {
        return reclasses;
    }

    public void setReclasses(short[] reclasses) {
        for(int x = 0; x < 2; x++)
            this.reclasses[x].setValue(reclasses[x]);
    }

    public short[] getSkills() {
        short[] skills = new short[5];
        for(int x = 0; x < 5; x++)
            skills[x] = (short) this.skills[x].get();
        return skills;
    }

    public IntegerProperty[] skillsProperty() {
        return skills;
    }

    public void setSkills(short[] skills) {
        for(int x = 0; x < 5; x++)
            this.skills[x].setValue(skills[x]);
    }

    public short[] getPersonalSkills() {
        short[] personalSkills = new short[3];
        for(int x = 0; x < 3; x++)
            personalSkills[x] = (short) this.personalSkills[x].get();
        return personalSkills;
    }

    public IntegerProperty[] personalSkillsProperty() {
        return personalSkills;
    }

    public void setPersonalSkills(short[] personalSkills) {
        for(int x = 0; x < 3; x++)
            this.personalSkills[x].setValue(personalSkills[x]);
    }

    public int getSupportId() {
        return supportId.get();
    }

    public IntegerProperty supportIdProperty() {
        return supportId;
    }

    public void setSupportId(int supportId) {
        this.supportId.set(supportId);
    }

    public String getMPid() {
        return mPid;
    }

    public void setMPid(String mPid) {
        this.mPid = mPid;
    }

    public int getBlockStart() {
        return blockStart;
    }

    public void setBlockStart(int blockStart) {
        this.blockStart = blockStart;
    }
}
