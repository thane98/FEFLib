package feflib.fates.gamedata;

import static feflib.utils.ByteUtils.*;

import feflib.utils.ByteUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.UnsupportedEncodingException;

public class ChapterBlock {
    private String cid;
    private int id;

    private IntegerProperty type = new SimpleIntegerProperty();
    private IntegerProperty birthrightIndex = new SimpleIntegerProperty();
    private IntegerProperty conquestIndex = new SimpleIntegerProperty();
    private IntegerProperty revelationIndex = new SimpleIntegerProperty();
    private IntegerProperty birthrightCondition = new SimpleIntegerProperty();
    private IntegerProperty conquestCondition = new SimpleIntegerProperty();
    private IntegerProperty revelationCondition = new SimpleIntegerProperty();
    private IntegerProperty marriedCharacter = new SimpleIntegerProperty();
    private IntegerProperty offspringSealLevel = new SimpleIntegerProperty();
    private IntegerProperty offspringSealLevelTwo = new SimpleIntegerProperty();
    private IntegerProperty route = new SimpleIntegerProperty();

    public ChapterBlock(byte[] raw, int start) {
        try {
            read(raw, start);
            addListeners(raw, start);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void read(byte[] raw, int start) throws UnsupportedEncodingException {
        cid = getStringFromPointer(raw, start);
        id = raw[start + 8];
        type.setValue(raw[start + 9]);
        birthrightIndex.setValue(raw[start + 10]);
        conquestIndex.setValue(raw[start + 11]);
        revelationIndex.setValue(raw[start + 12]);
        birthrightCondition.setValue(raw[start + 13]);
        conquestCondition.setValue(raw[start + 14]);
        revelationCondition.setValue(raw[start + 15]);
        marriedCharacter.setValue(toShort(raw, start + 16));
        offspringSealLevel.setValue(raw[start + 18]);
        offspringSealLevelTwo.setValue(raw[start + 19]);
        route.setValue(raw[start + 20]);
    }

    private void addListeners(byte[] raw, int start) {
        type.addListener((observable, oldValue, newValue) -> raw[start + 9] = newValue.byteValue());
        birthrightIndex.addListener((observable, oldValue, newValue) -> raw[start + 10] = newValue.byteValue());
        conquestIndex.addListener((observable, oldValue, newValue) -> raw[start + 11] = newValue.byteValue());
        revelationIndex.addListener((observable, oldValue, newValue) -> raw[start + 12] = newValue.byteValue());
        birthrightCondition.addListener((observable, oldValue, newValue) -> raw[start + 13] = newValue.byteValue());
        conquestCondition.addListener((observable, oldValue, newValue) -> raw[start + 14] = newValue.byteValue());
        revelationCondition.addListener((observable, oldValue, newValue) -> raw[start + 15] = newValue.byteValue());
        marriedCharacter.addListener((observable, oldValue, newValue) ->
                System.arraycopy(ByteUtils.toByteArray(newValue.shortValue()), 0,
                        raw, start + 16, 2));
        offspringSealLevel.addListener((observable, oldValue, newValue) -> raw[start + 18] = newValue.byteValue());
        offspringSealLevelTwo.addListener((observable, oldValue, newValue) -> raw[start + 19] = newValue.byteValue());
        route.addListener((observable, oldValue, newValue) -> raw[start + 20] = newValue.byteValue());
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type.get();
    }

    public IntegerProperty typeProperty() {
        return type;
    }

    public void setType(byte type) {
        this.type.set(type);
    }

    public int getBirthrightIndex() {
        return birthrightIndex.get();
    }

    public IntegerProperty birthrightIndexProperty() {
        return birthrightIndex;
    }

    public void setBirthrightIndex(byte birthrightIndex) {
        this.birthrightIndex.set(birthrightIndex);
    }

    public int getConquestIndex() {
        return conquestIndex.get();
    }

    public IntegerProperty conquestIndexProperty() {
        return conquestIndex;
    }

    public void setConquestIndex(byte conquestIndex) {
        this.conquestIndex.set(conquestIndex);
    }

    public int getRevelationIndex() {
        return revelationIndex.get();
    }

    public IntegerProperty revelationIndexProperty() {
        return revelationIndex;
    }

    public void setRevelationIndex(byte revelationIndex) {
        this.revelationIndex.set(revelationIndex);
    }

    public int getBirthrightCondition() {
        return birthrightCondition.get();
    }

    public IntegerProperty birthrightConditionProperty() {
        return birthrightCondition;
    }

    public void setBirthrightCondition(byte birthrightCondition) {
        this.birthrightCondition.set(birthrightCondition);
    }

    public int getConquestCondition() {
        return conquestCondition.get();
    }

    public IntegerProperty conquestConditionProperty() {
        return conquestCondition;
    }

    public void setConquestCondition(byte conquestCondition) {
        this.conquestCondition.set(conquestCondition);
    }

    public int getRevelationCondition() {
        return revelationCondition.get();
    }

    public IntegerProperty revelationConditionProperty() {
        return revelationCondition;
    }

    public void setRevelationCondition(byte revelationCondition) {
        this.revelationCondition.set(revelationCondition);
    }

    public int getMarriedCharacter() {
        return marriedCharacter.get();
    }

    public IntegerProperty marriedCharacterProperty() {
        return marriedCharacter;
    }

    public void setMarriedCharacter(short marriedCharacter) {
        this.marriedCharacter.set(marriedCharacter);
    }

    public int getOffspringSealLevel() {
        return offspringSealLevel.get();
    }

    public IntegerProperty offspringSealLevelProperty() {
        return offspringSealLevel;
    }

    public void setOffspringSealLevel(byte offspringSealLevel) {
        this.offspringSealLevel.set(offspringSealLevel);
    }

    public int getOffspringSealLevelTwo() {
        return offspringSealLevelTwo.get();
    }

    public IntegerProperty offspringSealLevelTwoProperty() {
        return offspringSealLevelTwo;
    }

    public void setOffspringSealLevelTwo(byte offspringSealLevelTwo) {
        this.offspringSealLevelTwo.set(offspringSealLevelTwo);
    }

    public int getRoute() {
        return route.get();
    }

    public IntegerProperty routeProperty() {
        return route;
    }

    public void setRoute(byte route) {
        this.route.set(route);
    }
}
