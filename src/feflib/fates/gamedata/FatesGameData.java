package feflib.fates.gamedata;

import static feflib.utils.ByteUtils.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FatesGameData {
    private static final int CHAPTER_POINTER_ADDRESS = 0x20;
    private static final int CHAPTER_COUNT_ADDRESS = 0x24;
    private static final int CHARACTER_POINTER_ADDRESS = 0x28;

    private byte[] raw;

    private List<CharacterBlock> characters = new ArrayList<>();
    private List<ChapterBlock> chapters = new ArrayList<>();

    public FatesGameData(File file) {
        try {
            raw = Files.readAllBytes(file.toPath());
            parseCharacters();
            parseChapters();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseCharacters() {
        int tableStart = toInt(raw, CHARACTER_POINTER_ADDRESS) + 0x20;
        int count = toShort(raw, tableStart + 0x4);
        for(int x = 0; x < count; x++) {
            characters.add(new CharacterBlock(raw, tableStart + 0x10 + x * 0x98));
        }
    }

    private void parseChapters() {
        int tableStart = toInt(raw, CHAPTER_POINTER_ADDRESS) + 0x20;
        int count = toInt(raw, CHAPTER_COUNT_ADDRESS);
        for(int x = 0; x < count; x++) {
            chapters.add(new ChapterBlock(raw, tableStart + x * 0x1C));
        }
    }

    public List<CharacterBlock> getCharacters() {
        return characters;
    }

    public byte[] getRaw() {
        return raw;
    }

    public List<ChapterBlock> getChapters() {
        return chapters;
    }
}
