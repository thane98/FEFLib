package feflib.fates.gamedata.person;


import static feflib.utils.ByteUtils.*;

import java.io.IOException;
import java.util.Arrays;

public class PersonBlock {
    private String pid = "";
    private String fid = "";
    private String aid = "";
    private String mPid = "";
    private String mPidH = "";
    private String combatMusic = "";
    private String enemyVoice = "";

    private byte level = 0;
    private byte internalLevel = 0;
    private byte enemyFlag = 0;
    private byte birthday = 0;
    private byte birthMonth = 0;
    private byte army = 0;
    private byte levelCap = 0;
    private byte amiibo = 0;
    private byte supportRoute = 0;

    private short id = 0;
    private short replacementId = 0;
    private short parentId = 0;
    private short parent = 0;
    private short childId = 0;
    private short supportIndex = 0;
    private short supportId = 0;

    private byte[] unknownBytes;
    private byte[] unknownLine;
    private byte[] skillFlags;
    private byte[] bitflags;
    private byte[] stats;
    private byte[] growths;
    private byte[] modifiers;
    private byte[] weaponRanks;
    private byte[] padding;

    private short[] personalSkills = new short[3];
    private short[] skills = new short[5];
    private short[] classes = new short[2];
    private short[] reclasses = new short[2];
    private short[] amiiboWeapons = new short[2];

    private byte[] attackBonuses;
    private byte[] guardBonuses;

    public PersonBlock() {

    }

    public PersonBlock(byte[] bytes, int start) {
        try {
            read(bytes, start);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses data from a character block at the given address.
     *
     * @param bytes The bytes that make up the file currently being opened.
     * @param start The index in the array to begin reading the block from.
     * @throws IOException An exception will occur if shift-jis encoding is not supported.
     */
    public void read(byte[] bytes, int start) throws IOException {
        bitflags = Arrays.copyOfRange(bytes, start, start + 8);
        pid = getStringFromPointer(bytes, start + 8);
        if(toInt(bytes, start + 12) != 0)
            fid = getStringFromPointer(bytes, start + 12);
        if(toInt(bytes, start + 16) != 0)
            aid = getStringFromPointer(bytes, start + 16);
        if(toInt(bytes, start + 20) != 0)
            mPid = getStringFromPointer(bytes, start + 20);
        if(toInt(bytes, start + 24) != 0)
            mPidH = getStringFromPointer(bytes, start + 24);
        attackBonuses = Arrays.copyOfRange(bytes, toInt(bytes, start + 28) + 0x20,
                toInt(bytes, start + 28) + 0x34);
        guardBonuses = Arrays.copyOfRange(bytes, toInt(bytes, start + 32) + 0x20,
                toInt(bytes, start + 32) + 0x44);
        id = toShort(bytes, start + 36);
        supportRoute = bytes[start + 38];
        army = bytes[start + 39];
        replacementId = toShort(bytes, start + 40);
        parent = toShort(bytes, start + 42);
        classes[0] = toShort(bytes, start + 44);
        classes[1] = toShort(bytes, start + 46);
        supportId = toShort(bytes, start + 48);
        level = bytes[start + 50];
        internalLevel = bytes[start + 51];
        enemyFlag = bytes[start + 52];
        unknownBytes = Arrays.copyOfRange(bytes, start + 53, start + 56);
        stats = Arrays.copyOfRange(bytes, start + 56, start + 64);
        growths = Arrays.copyOfRange(bytes, start + 64, start + 72);
        modifiers = Arrays.copyOfRange(bytes, start + 72, start + 80);
        unknownLine = Arrays.copyOfRange(bytes, start + 80, start + 96);
        weaponRanks = Arrays.copyOfRange(bytes, start + 96, start + 104);
        for(int x = 0; x < 5; x++)
            skills[x] = toShort(bytes, start + 104 + x * 2);
        skillFlags = Arrays.copyOfRange(bytes, start + 114, start + 116);
        for(int x = 0; x < 3; x++)
            personalSkills[x] = toShort(bytes, start + 116 + x * 2);
        birthday = bytes[start + 122];
        birthMonth = bytes[start + 123];
        reclasses[0] = toShort(bytes, start + 124);
        reclasses[1] = toShort(bytes, start + 126);
        parentId = toShort(bytes, start + 128);
        childId = toShort(bytes, start + 130);
        supportIndex = toShort(bytes, start + 132);
        levelCap = bytes[start + 134];
        amiibo = bytes[start + 135];
        if(toInt(bytes, start + 136) != 0)
            combatMusic = getStringFromPointer(bytes, start + 136);
        if(toInt(bytes, start + 140) != 0)
            enemyVoice = getStringFromPointer(bytes, start + 140);
        amiiboWeapons[0] = toShort(bytes, start + 144);
        amiiboWeapons[1] = toShort(bytes, start + 146);
        padding = Arrays.copyOfRange(bytes, start + 148, start + 152);
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getMPid() {
        return mPid;
    }

    public void setMPid(String mPid) {
        this.mPid = mPid;
    }

    public String getMPidH() {
        return mPidH;
    }

    public void setMPidH(String mPidH) {
        this.mPidH = mPidH;
    }

    public String getCombatMusic() {
        return combatMusic;
    }

    public void setCombatMusic(String combatMusic) {
        this.combatMusic = combatMusic;
    }

    public String getEnemyVoice() {
        return enemyVoice;
    }

    public void setEnemyVoice(String enemyVoice) {
        this.enemyVoice = enemyVoice;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getInternalLevel() {
        return internalLevel;
    }

    public void setInternalLevel(byte internalLevel) {
        this.internalLevel = internalLevel;
    }

    public byte getEnemyFlag() {
        return enemyFlag;
    }

    public void setEnemyFlag(byte enemyFlag) {
        this.enemyFlag = enemyFlag;
    }

    public byte getBirthday() {
        return birthday;
    }

    public void setBirthday(byte birthday) {
        this.birthday = birthday;
    }

    public byte getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(byte birthMonth) {
        this.birthMonth = birthMonth;
    }

    public byte getArmy() {
        return army;
    }

    public void setArmy(byte army) {
        this.army = army;
    }

    public byte getLevelCap() {
        return levelCap;
    }

    public void setLevelCap(byte levelCap) {
        this.levelCap = levelCap;
    }

    public byte getAmiibo() {
        return amiibo;
    }

    public void setAmiibo(byte amiibo) {
        this.amiibo = amiibo;
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

    public short getParentId() {
        return parentId;
    }

    public void setParentId(short parentId) {
        this.parentId = parentId;
    }

    public short getParent() {
        return parent;
    }

    public void setParent(short parent) {
        this.parent = parent;
    }

    public short getChildId() {
        return childId;
    }

    public void setChildId(short childId) {
        this.childId = childId;
    }

    public short getSupportIndex() {
        return supportIndex;
    }

    public void setSupportIndex(short supportIndex) {
        this.supportIndex = supportIndex;
    }

    public short getSupportId() {
        return supportId;
    }

    public void setSupportId(short supportId) {
        this.supportId = supportId;
    }

    public byte[] getUnknownBytes() {
        return unknownBytes;
    }

    public void setUnknownBytes(byte[] unknownBytes) {
        this.unknownBytes = unknownBytes;
    }

    public byte[] getUnknownLine() {
        return unknownLine;
    }

    public void setUnknownLine(byte[] unknownLine) {
        this.unknownLine = unknownLine;
    }

    public byte[] getSkillFlags() {
        return skillFlags;
    }

    public void setSkillFlags(byte[] skillFlags) {
        this.skillFlags = skillFlags;
    }

    public byte[] getBitflags() {
        return bitflags;
    }

    public void setBitflags(byte[] bitflags) {
        this.bitflags = bitflags;
    }

    public byte[] getStats() {
        return stats;
    }

    public void setStats(byte[] stats) {
        this.stats = stats;
    }

    public byte[] getGrowths() {
        return growths;
    }

    public void setGrowths(byte[] growths) {
        this.growths = growths;
    }

    public byte[] getModifiers() {
        return modifiers;
    }

    public void setModifiers(byte[] modifiers) {
        this.modifiers = modifiers;
    }

    public byte[] getWeaponRanks() {
        return weaponRanks;
    }

    public void setWeaponRanks(byte[] weaponRanks) {
        this.weaponRanks = weaponRanks;
    }

    public byte[] getPadding() {
        return padding;
    }

    public void setPadding(byte[] padding) {
        this.padding = padding;
    }

    public short[] getPersonalSkills() {
        return personalSkills;
    }

    public void setPersonalSkills(short[] personalSkills) {
        this.personalSkills = personalSkills;
    }

    public short[] getSkills() {
        return skills;
    }

    public void setSkills(short[] skills) {
        this.skills = skills;
    }

    public short[] getClasses() {
        return classes;
    }

    public void setClasses(short[] classes) {
        this.classes = classes;
    }

    public short[] getReclasses() {
        return reclasses;
    }

    public void setReclasses(short[] reclasses) {
        this.reclasses = reclasses;
    }

    public short[] getAmiiboWeapons() {
        return amiiboWeapons;
    }

    public void setAmiiboWeapons(short[] amiiboWeapons) {
        this.amiiboWeapons = amiiboWeapons;
    }

    public byte[] getAttackBonuses() {
        return attackBonuses;
    }

    public void setAttackBonuses(byte[] attackBonuses) {
        this.attackBonuses = attackBonuses;
    }

    public byte[] getGuardBonuses() {
        return guardBonuses;
    }

    public void setGuardBonuses(byte[] guardBonuses) {
        this.guardBonuses = guardBonuses;
    }

    public byte getSupportRoute() {
        return supportRoute;
    }

    public void setSupportRoute(byte supportRoute) {
        this.supportRoute = supportRoute;
    }
}