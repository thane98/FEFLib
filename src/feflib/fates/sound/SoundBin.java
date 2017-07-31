package feflib.fates.sound;

import feflib.fates.inject.InjectableFile;
import feflib.fates.inject.InjectionData;
import feflib.utils.ByteUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class SoundBin {
    private List<VoiceGroup> entries = new ArrayList<>();
    private File target;

    private int countStart;
    private int injectStart;
    private InjectableFile injectableFile;

    public SoundBin(File file) {
        target = file;
        analyze();
        injectableFile = new InjectableFile(target.toPath());
    }
    
    private void analyze() {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(target.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int startPoint = 0x24 + ByteUtils.toInt(bytes, 0x20) * 0x20;
        countStart = startPoint;
        int count = ByteUtils.toInt(bytes, startPoint);
        int increment = 0;
        for(int x = 0; x < count; x++) {
            VoiceGroup group = new VoiceGroup(bytes, startPoint + 4 + increment);
            increment += group.getSize();
            entries.add(group);
        }
        injectStart = startPoint + 4 + increment;
    }

    public void addEntries(String mainName, File dir) {
        if(!dir.isDirectory() || dir.listFiles() == null)
            return;
        File[] wavs = dir.listFiles();

        HashMap<String, List<String>> groups = new HashMap<>();
        groups.put("ATTACK_L", new ArrayList<>());
        groups.put("ATTACK_H", new ArrayList<>());
        groups.put("DAMAGE_L", new ArrayList<>());
        groups.put("DAMAGE_H", new ArrayList<>());
        groups.put("DEAD", new ArrayList<>());
        groups.put("WIN", new ArrayList<>());
        groups.put("B_ATTACK", new ArrayList<>());
        groups.put("B_GUARD", new ArrayList<>());
        groups.put("B_SUPPORT", new ArrayList<>());
        groups.put("B_WIN", new ArrayList<>());
        groups.put("CURED", new ArrayList<>());
        groups.put("CUT_IN", new ArrayList<>());

        for(File f : wavs) {
            String name = f.getName().substring(0, f.getName().lastIndexOf("."));
            if(name.contains("_ATTACK_L_"))
                groups.get("ATTACK_L").add(name);
            else if(name.contains("_ATTACK_H_"))
                groups.get("ATTACK_H").add(name);
            else if(name.contains("_DAMAGE_L_"))
                groups.get("DAMAGE_L").add(name);
            else if(name.contains("_DAMAGE_H_"))
                groups.get("DAMAGE_H").add(name);
            else if(name.contains("_DEAD_"))
                groups.get("DEAD").add(name);
            else if(name.contains("_B_WIN_"))
                groups.get("B_WIN").add(name);
            else if(name.contains("_WIN_"))
                groups.get("WIN").add(name);
            else if(name.contains("_B_ATTACK_"))
                groups.get("B_ATTACK").add(name);
            else if(name.contains("_B_GUARD_"))
                groups.get("B_GUARD").add(name);
            else if(name.contains("_B_SUPPORT_"))
                groups.get("B_SUPPORT").add(name);
            else if(name.contains("_CURED_"))
                groups.get("CURED").add(name);
            else if(name.contains("_CUT_IN_"))
                groups.get("CUT_IN").add(name);
            else if(name.contains("_DAMAGE_")) {
                if (groups.get("DAMAGE_L").size() < 3)
                    groups.get("DAMAGE_L").add(name);
                else
                    groups.get("DAMAGE_H").add(name);
            }
        }
        for(String s : groups.keySet()) {
            createVoiceGroup("VOICE_" + mainName + "_" + s, groups.get(s));
        }
        injectableFile.putInt(countStart - 0x20, entries.size());
    }

    private void createVoiceGroup(String main, List<String> names) {
        // Set up raw bytes.
        byte[] raw = new byte[0x10 + names.size() * 8];
        List<Integer> pointers = new ArrayList<>();
        pointers.add(4);
        raw[0] = 2;
        raw[2] = -1;
        raw[3] = -1;
        System.arraycopy(ByteUtils.toByteArray(names.size()), 0, raw, 0x8, 4);
        System.arraycopy(ByteUtils.toByteArray(names.size()), 0, raw, 0xC, 4);
        for(int x = 0; x < names.size(); x++) {
            raw[0x14 + x * 8] = 1;
            pointers.add(0x10 + x * 8);
        }

        // Create injection data.
        InjectionData data = new InjectionData(raw);
        names.add(0, main);
        data.setPointerTwo(true);
        data.setPointers(pointers);
        data.setLabels(names);

        injectableFile.inject(data, injectStart - 0x20);
        injectStart += raw.length;

        // Create new objects.
        VoiceGroup group = new VoiceGroup();
        group.setMainLabel(main);
        group.setSize(raw.length);
        names.remove(main);
        for(String s : names) {
            SoundEntry entry = new SoundEntry();
            entry.setLabel(s);
            entry.setTag(Arrays.copyOfRange(raw, 0x14, 0x18));
            group.getEntries().add(entry);
        }
        entries.add(group);
    }

    public void appendItem(int index, String name) {
        VoiceGroup group = entries.get(index);
        int address = getStartAddress(index);
        byte[] raw = { 0, 0, 0, 0, 1, 0, 0, 0 };
        InjectionData data = new InjectionData(raw);
        data.setLabels(Collections.singletonList(name));
        data.setPointers(Collections.singletonList(0));
        data.setPointerTwo(false);

        injectableFile.inject(data, address - 0x20 + group.getSize());
        injectStart += 8;
        group.setSize(group.getSize() + 8);

        SoundEntry entry = new SoundEntry();
        entry.setLabel(name);
        entry.setTag(Arrays.copyOfRange(raw, 4, 8));
        group.getEntries().add(entry);

        injectableFile.putInt(address - 0x14, group.getEntries().size());
        injectableFile.putInt(address - 0x18, group.getEntries().size());
    }

    private int getStartAddress(int index) {
        int address = countStart + 4;
        for(int x = 0; x < index; x++) {
            address += entries.get(x).getSize();
        }
        return address;
    }

    public void changeTag(int index, SoundEntry entry, byte[] tag) {
        int address = getStartAddress(index) - 0xC + entries.get(index).getEntries().indexOf(entry) * 8;
        entry.setTag(tag);
        for(int x = 0; x < 4; x++) {
            injectableFile.putByte(address + x, tag[x]);
        }
    }

    public List<VoiceGroup> getEntries() {
        return entries;
    }

    public InjectableFile getInjectableFile() {
        return injectableFile;
    }
}
