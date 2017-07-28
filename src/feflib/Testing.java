package feflib;

import feflib.awakening.data.dispo.AwakeningDispo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Testing {
    public static void main(String[] args) {
        AwakeningDispo dispo = new AwakeningDispo(new File("C:\\Users\\ethan\\Downloads\\000.bin"));
        File file = new File("C:\\Users\\ethan\\Downloads\\002.bin");
        try {
            Files.write(file.toPath(), dispo.serialize());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
