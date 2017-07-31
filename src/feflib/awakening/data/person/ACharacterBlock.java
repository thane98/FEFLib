package feflib.awakening.data.person;

import feflib.fates.inject.InjectableFile;
import feflib.utils.ByteUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;

import static feflib.utils.ByteUtils.getStringFromPointer;
import static feflib.utils.ByteUtils.toByteArray;
import static feflib.utils.ByteUtils.toShort;

public class ACharacterBlock {
    private IntegerProperty level = new SimpleIntegerProperty();
    private byte id;

    private IntegerProperty[] unknown = new IntegerProperty[2];
    private IntegerProperty[] stats = new IntegerProperty[8];
    private IntegerProperty[] modifiers = new IntegerProperty[8];
    private IntegerProperty[] weaponRanks = new IntegerProperty[5];
    private IntegerProperty[] skills = new IntegerProperty[5];

    private StringProperty[] reclasses = new StringProperty[6];

    private StringProperty pid = new SimpleStringProperty();
    private StringProperty fid = new SimpleStringProperty();
    private StringProperty mPid = new SimpleStringProperty();
    private StringProperty mPidH = new SimpleStringProperty();
    private StringProperty parent = new SimpleStringProperty();
    private StringProperty job = new SimpleStringProperty();

    private int blockStart;
    private InjectableFile file;

    public ACharacterBlock(byte[] bytes, int start) {
        blockStart = start;

        // Initialize observable value arrays.
        for(int x = 0; x < 2; x++)
            unknown[x] = new SimpleIntegerProperty();
        for(int x = 0; x < 8; x++)
            stats[x] = new SimpleIntegerProperty();
        for(int x = 0; x < 8; x++)
            modifiers[x] = new SimpleIntegerProperty();
        for(int x = 0; x < 5; x++)
            weaponRanks[x] = new SimpleIntegerProperty();
        for(int x = 0; x < 5; x++)
            skills[x] = new SimpleIntegerProperty();
        for(int x = 0; x < 6; x++)
            reclasses[x] = new SimpleStringProperty();

        try {
            read(bytes, start);
            addListeners(start);
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
        pid.setValue(getStringFromPointer(raw, start));
        fid.setValue(getStringFromPointer(raw, start + 4));
        job.setValue(getStringFromPointer(raw, start + 8));
        mPid.setValue(getStringFromPointer(raw, start + 12));
        mPidH.setValue(getStringFromPointer(raw, start + 16));
        for(int x = 0; x < 2; x++)
            unknown[x].setValue(raw[start + 52 + x]);
        for(int x = 0; x < 8; x++) {
            stats[x].setValue(raw[start + 20 + x]);
        }
        for(int x = 0; x < 8; x++) {
            modifiers[x].setValue(raw[start + 44 + x]);
        }
        for(int x = 0; x < 5; x++) {
            weaponRanks[x].setValue(raw[start + 54 + x]);
        }
        for(int x = 0; x < 5; x++) {
            skills[x].setValue(toShort(raw, start + 64 + x * 2));
        }
        for(int x = 0; x < 6; x++) {
            reclasses[x].setValue(getStringFromPointer(raw, start + 100 + x * 4));
        }
        parent.setValue(getStringFromPointer(raw, start + 124));
        level.setValue(raw[start + 61]);
        id = raw[start + 63];
    }

    private void addListeners(int start) {
        pid.addListener((observable, oldValue, newValue) -> file.repoint(newValue, start - 0x20));
        fid.addListener((observable, oldValue, newValue) -> file.repoint(newValue, start - 0x1C));
        job.addListener((observable, oldValue, newValue) -> file.repoint(newValue, start - 0x18));
        mPid.addListener((observable, oldValue, newValue) -> file.repoint(newValue, start - 0x14));
        mPidH.addListener((observable, oldValue, newValue) -> file.repoint(newValue, start - 0x10));
        for(int x = 0; x < 2; x++) {
            int i = x;
            unknown[x].addListener((observable, oldValue, newValue) ->
                    file.putByte(start + 52 + i - 0x20, newValue.byteValue()));
        }
        for(int x = 0; x < 8; x++) {
            int i = x;
            stats[x].addListener((observable, oldValue, newValue) ->
                    file.putByte(start + 20 + i - 0x20, newValue.byteValue()));
        }
        for(int x = 0; x < 8; x++) {
            int i = x;
            modifiers[x].addListener((observable, oldValue, newValue) ->
                    file.putByte(start + 44 + i - 0x20, newValue.byteValue()));
        }
        for(int x = 0; x < 5; x++) {
            int i = x;
            weaponRanks[x].addListener((observable, oldValue, newValue) ->
                    file.putByte(start + 54 + i - 0x20, newValue.byteValue()));
        }
        for(int x = 0; x < 5; x++) {
            int i = x;
            skills[x].addListener((observable, oldValue, newValue) -> {
                byte[] bytes = toByteArray(newValue.shortValue());
                for(int y = 0; y < 2; y++) {
                    file.putByte(start + 64 + y + i * 2 - 0x20, bytes[y]);
                }
            });
        }
        for(int x = 0; x < 6; x++) {
            int i = x;
            reclasses[x].addListener((observable, oldValue, newValue) -> file.repoint(newValue, start + 100 * i - 0x20));
        }
        parent.addListener((observable, oldValue, newValue) -> file.repoint(newValue, start + 124 - 0x20));
        level.addListener((observable, oldValue, newValue) -> file.putByte(start + 61, newValue.byteValue()));
    }

    public int getLevel() {
        return level.get();
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public void setLevel(int level) {
        this.level.set(level);
    }

    public byte[] getStats() {
        byte[] bytes = new byte[8];
        for(int x = 0; x < 8; x++)
            bytes[x] = stats[x].getValue().byteValue();
        return bytes;
    }

    public void setStats(byte[] stats) {
        for(int x = 0; x < 8; x++)
            this.stats[x].setValue(stats[x]);
    }

    public byte[] getModifiers() {
        byte[] bytes = new byte[8];
        for(int x = 0; x < 8; x++)
            bytes[x] = modifiers[x].getValue().byteValue();
        return bytes;
    }

    public void setModifiers(byte[] modifiers) {
        for(int x = 0; x < 8; x++)
            this.modifiers[x].setValue(modifiers[x]);
    }

    public byte[] getWeaponRanks() {
        byte[] bytes = new byte[8];
        for(int x = 0; x < 5; x++)
            bytes[x] = weaponRanks[x].getValue().byteValue();
        return bytes;
    }

    public void setWeaponRanks(byte[] weaponRanks) {
        for(int x = 0; x < 8; x++)
            this.weaponRanks[x].setValue(weaponRanks[x]);
    }

    public short[] getSkills() {
        short[] shorts = new short[5];
        for(int x = 0; x < 5; x++)
            shorts[x] = skills[x].getValue().shortValue();
        return shorts;
    }

    public void setSkills(short[] skills) {
        for(int x = 0; x < 8; x++)
            this.skills[x].setValue(skills[x]);
    }

    public String[] getReclasses() {
        String[] strings = new String[6];
        for(int x = 0; x < 5; x++)
            strings[x] = reclasses[x].getValue();
        return strings;
    }

    public void setReclasses(String[] reclasses) {
        for(int x = 0; x < 6; x++)
            this.reclasses[x].setValue(reclasses[x]);
    }

    public String getPid() {
        return pid.get();
    }

    public StringProperty pidProperty() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid.set(pid);
    }

    public String getFid() {
        return fid.get();
    }

    public StringProperty fidProperty() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid.set(fid);
    }

    public String getmPid() {
        return mPid.get();
    }

    public StringProperty mPidProperty() {
        return mPid;
    }

    public void setmPid(String mPid) {
        this.mPid.set(mPid);
    }

    public String getmPidH() {
        return mPidH.get();
    }

    public StringProperty mPidHProperty() {
        return mPidH;
    }

    public void setmPidH(String mPidH) {
        this.mPidH.set(mPidH);
    }

    public String getParent() {
        return parent.get();
    }

    public StringProperty parentProperty() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent.set(parent);
    }

    public String getJob() {
        return job.get();
    }

    public StringProperty jobProperty() {
        return job;
    }

    public void setJob(String job) {
        this.job.set(job);
    }
}
