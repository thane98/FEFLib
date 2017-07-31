package feflib.awakening.data.person;

import feflib.fates.gamedata.ChapterBlock;
import feflib.fates.inject.InjectableFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static feflib.utils.ByteUtils.toInt;
import static feflib.utils.ByteUtils.toShort;

public class AwakeningPerson {
    private static final int CHARACTER_ADDRESS = 0x30;
    private static final int CHARACTER_COUNT_ADDRESS = 0x24;

    private InjectableFile injectableFile;

    private List<ACharacterBlock> characters = new ArrayList<>();
    private List<ChapterBlock> chapters = new ArrayList<>();


    public AwakeningPerson(File file) {
        try {
            byte[] raw = Files.readAllBytes(file.toPath());
            injectableFile = new InjectableFile(raw);
            parseCharacters(raw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseCharacters(byte[] raw) {
        int count = toInt(raw, CHARACTER_COUNT_ADDRESS);
        for(int x = 0; x < count; x++) {
            characters.add(new ACharacterBlock(raw, CHARACTER_ADDRESS + x * 568));
        }
    }

    public List<ACharacterBlock> getCharacters() {
        return characters;
    }

    public byte[] getRaw() {
        return injectableFile.toBin();
    }

    public List<ChapterBlock> getChapters() {
        return chapters;
    }
}
