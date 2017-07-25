package feflib.fates.inject;

import feflib.utils.ByteUtils;
import feflib.utils.Pair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InjectableFile {
    private List<Byte> data = new ArrayList<>();
    private List<Byte> pointerOne = new ArrayList<>();
    private List<Byte> pointerTwo = new ArrayList<>();
    private List<Byte> labels = new ArrayList<>();

    public InjectableFile(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            int dataSize = ByteUtils.toInt(bytes, 4);
            int pointerOneCount = ByteUtils.toInt(bytes, 8);
            int pointerTwoCount = ByteUtils.toInt(bytes, 12);
            int pointerTwoStart = dataSize + pointerOneCount * 4 + 0x20;
            int labelStart = pointerTwoStart + pointerTwoCount * 8;

            for(byte b : Arrays.copyOfRange(bytes, 0x20, dataSize + 0x20))
                data.add(b);
            for(byte b : Arrays.copyOfRange(bytes, dataSize + 0x20, pointerTwoStart))
                pointerOne.add(b);
            for(byte b : Arrays.copyOfRange(bytes, pointerTwoStart, labelStart))
                pointerTwo.add(b);
            for(byte b : Arrays.copyOfRange(bytes, labelStart, bytes.length))
                labels.add(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InjectableFile(byte[] bytes) {
        int dataSize = ByteUtils.toInt(bytes, 4);
        int pointerOneCount = ByteUtils.toInt(bytes, 8);
        int pointerTwoCount = ByteUtils.toInt(bytes, 12);
        int pointerTwoStart = dataSize + pointerOneCount * 4 + 0x20;
        int labelStart = pointerTwoStart + pointerTwoCount * 8;

        for(byte b : Arrays.copyOfRange(bytes, 0x20, dataSize + 0x20))
            data.add(b);
        for(byte b : Arrays.copyOfRange(bytes, dataSize + 0x20, pointerTwoStart))
            pointerOne.add(b);
        for(byte b : Arrays.copyOfRange(bytes, pointerTwoStart, labelStart))
            pointerTwo.add(b);
        for(byte b : Arrays.copyOfRange(bytes, labelStart, bytes.length))
            labels.add(b);
    }

    public byte[] toBin() {
        List<Byte> out = new ArrayList<>();
        out.addAll(ByteUtils.toByteList(fileLength()));
        out.addAll(ByteUtils.toByteList(data.size()));
        out.addAll(ByteUtils.toByteList(pointerOne.size() / 4));
        out.addAll(ByteUtils.toByteList(pointerTwo.size() / 8));
        for(int x = 0; x < 16; x++)
            out.add((byte) 0);
        out.addAll(data);
        out.addAll(pointerOne);
        out.addAll(pointerTwo);
        out.addAll(labels);

        byte[] output = new byte[out.size()];
        for(int x = 0; x < output.length; x++)
            output[x] = out.get(x);
        return output;
    }

    /**
     * Injects new data into the opened file at the given position.
     *
     * @param injectionData The raw data, labels, and pointer offsets to be injected.
     * @param injectStart The address in the file's data region to inject at.
     */
    public void inject(InjectionData injectionData, int injectStart) {
        int newData = injectionData.getRaw().length;
        int ptrTwoDiff = newData + injectionData.getPointers().size() * 4;
        int labelStart = data.size() + pointerOne.size() + pointerTwo.size();
        int labelDiff = ptrTwoDiff + (injectionData.isPointerTwo() ? 8 : 0);
        List<Byte> labelBytes = new ArrayList<>();
        List<Integer> labelOffsets = new ArrayList<>();
        List<Pair<Integer, Integer>> ptrOne = ptrOneList();
        List<Pair<Integer, Integer>> ptrTwo = ptrTwoList();

        // Write pointers to raw data; prepare for injection.
        for(int x = 0; x < injectionData.getPointers().size(); x++) {
            int ptr = labelStart + labelDiff + labels.size() + labelBytes.size();
            String str = injectionData.getLabels().get(x);
            if(!str.equals("NULL")) {
                labelOffsets.add(ptr);
                try {
                    for(byte b : str.getBytes("shift-jis"))
                        labelBytes.add(b);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                labelBytes.add((byte) 0);
            }
            else
                labelOffsets.add(0);
        }
        for(int x = 0; x < injectionData.getPointers().size(); x++) {
            byte[] ptrRaw = ByteUtils.toByteArray(labelOffsets.get(x));
            System.arraycopy(ptrRaw, 0, injectionData.getRaw(), injectionData.getPointers().get(x), 4);
        }

        // Fix pointer one.
        for (Pair<Integer, Integer> aPtrOne : ptrOne) {
            int ptr = aPtrOne.getFirst();
            int dataPtr = aPtrOne.getSecond();
            if (ptr > injectStart)
                ptr += newData;
            if (dataPtr > injectStart && dataPtr < labelStart)
                dataPtr += newData;
            else if (dataPtr >= labelStart)
                dataPtr += labelDiff;
            aPtrOne.setFirst(ptr);
            aPtrOne.setSecond(dataPtr);
        }

        // Append data.
        List<Byte> byteList = new ArrayList<>();
        for(byte b : injectionData.getRaw())
            byteList.add(b);
        data.addAll(injectStart, byteList);

        // Fix pointer two.
        for(Pair<Integer, Integer> aPtrTwo: ptrTwo) {
            int ptr = aPtrTwo.getFirst();
            if(ptr > injectStart)
                ptr += newData;
            aPtrTwo.setFirst(ptr);
        }

        // Append pointer one.
        for(int i : injectionData.getPointers())
            pointerOne.addAll(ByteUtils.toByteList(injectStart + i));

        // Append pointer two.
        if(injectionData.isPointerTwo()) {
            pointerTwo.addAll(ByteUtils.toByteList(injectStart));
            pointerTwo.addAll(ByteUtils.toByteList(labels.size()));
        }

        // Append label bytes.
        labels.addAll(labelBytes);

        // Repair data and pointer one.
        for(int x = 0; x < ptrOne.size(); x++) {
            byte[] ptrBytes = ByteUtils.toByteArray(ptrOne.get(x).getFirst());
            byte[] dataPtrBytes = ByteUtils.toByteArray(ptrOne.get(x).getSecond());
            for(int y = 0; y < 4; y++) {
                pointerOne.set(x * 4 + y, ptrBytes[y]);
                data.set(ptrOne.get(x).getFirst() + y, dataPtrBytes[y]);
            }
        }

        // Repair pointer two.
        for(int x = 0; x < ptrTwo.size(); x++) {
            byte[] ptrBytes = ByteUtils.toByteArray(ptrTwo.get(x).getFirst());
            for(int y = 0; y < 4; y++)
                pointerTwo.set(x * 8 + y, ptrBytes[y]);
        }
    }

    private void addPointers(List<Integer> pointers, List<String> uncompiled, int injectStart) {
        int diff = pointers.size() * 4;
        int labelStart = data.size() + pointerOne.size() + pointerTwo.size();
        List<Byte> labelBytes = new ArrayList<>();
        List<Integer> labelOffsets = new ArrayList<>();
        List<Pair<Integer, Integer>> ptrOne = ptrOneList();

        // Write pointers to raw data; prepare for injection.
        for(int x = 0; x < pointers.size(); x++) {
            int ptr = labelStart + diff + labels.size() + labelBytes.size();
            String str = uncompiled.get(x);
            if(!str.equals("NULL")) {
                labelOffsets.add(ptr);
                try {
                    for(byte b : str.getBytes("shift-jis"))
                        labelBytes.add(b);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                labelBytes.add((byte) 0);
            }
            else
                labelOffsets.add(0);
        }
        for(int x = 0; x < pointers.size(); x++) {
            byte[] ptrRaw = ByteUtils.toByteArray(labelOffsets.get(x));
            for(int y = 0; y < 4; y++) {
                data.set(injectStart + pointers.get(x) + y, ptrRaw[y]);
            }
        }

        // Fix pointer one.
        for (Pair<Integer, Integer> aPtrOne : ptrOne) {
            int ptr = aPtrOne.getFirst();
            int dataPtr = aPtrOne.getSecond();
            if (dataPtr >= labelStart)
                dataPtr += diff;
            aPtrOne.setFirst(ptr);
            aPtrOne.setSecond(dataPtr);
        }

        // Append pointer one.
        for(int i : pointers)
            pointerOne.addAll(ByteUtils.toByteList(injectStart + i));

        // Append label bytes.
        labels.addAll(labelBytes);

        // Repair data and pointer one.
        for(int x = 0; x < ptrOne.size(); x++) {
            byte[] ptrBytes = ByteUtils.toByteArray(ptrOne.get(x).getFirst());
            byte[] dataPtrBytes = ByteUtils.toByteArray(ptrOne.get(x).getSecond());
            for(int y = 0; y < 4; y++) {
                pointerOne.set(x * 4 + y, ptrBytes[y]);
                data.set(ptrOne.get(x).getFirst() + y, dataPtrBytes[y]);
            }
        }
    }

    private int fileLength() {
        return 0x20 + data.size() + pointerOne.size()
                + pointerTwo.size() + labels.size();
    }

    private List<Pair<Integer, Integer>> ptrOneList() {
        List<Pair<Integer, Integer>> ptrOne = new ArrayList<>();
        for(int x = 0; x < pointerOne.size() / 4; x++) {
            Pair<Integer, Integer> entry = new Pair<>(0, 0);
            entry.setFirst(ByteUtils.toInt(pointerOne, x * 4));
            entry.setSecond(ByteUtils.toInt(data, entry.getFirst()));
            ptrOne.add(entry);
        }
        return ptrOne;
    }

    private List<Pair<Integer, Integer>> ptrTwoList() {
        List<Pair<Integer, Integer>> ptrTwo = new ArrayList<>();
        for(int x = 0; x < pointerTwo.size() / 8; x++) {
            Pair<Integer, Integer> entry = new Pair<>(0, 0);
            entry.setFirst(ByteUtils.toInt(pointerTwo, x * 8));
            entry.setSecond(ByteUtils.toInt(pointerTwo, x * 8 + 4));
            ptrTwo.add(entry);
        }
        return ptrTwo;
    }

    public void putInt(int address, int value) {
        byte[] values = ByteUtils.toByteArray(value);
        for(int x = 0; x < 4; x++) {
            data.set(address + x, values[x]);
        }
    }

    public void putByte(int address, byte value) {
        data.set(address, value);
    }
}
