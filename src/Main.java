import feflib.fates.gamedata.ChapterBlock;
import feflib.fates.gamedata.FatesGameData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) {
        long begin = System.nanoTime();
        File file = new File("C:\\Users\\ethan\\Downloads\\GameData.bin");
        FatesGameData gameData = new FatesGameData(file);
        for(ChapterBlock c : gameData.getChapters()) {
            System.out.println(c.getCid());
            c.setBirthrightIndex((byte) 2);
        }
        File file2 = new File("C:\\Users\\ethan\\Downloads\\GameData2.bin");
        try {
            Files.write(file2.toPath(), gameData.getRaw());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Wrote file in " + ((System.nanoTime() - begin) / 1000000000.0) + " seconds.");
    }
}
