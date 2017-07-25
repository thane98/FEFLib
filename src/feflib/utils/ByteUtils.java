package feflib.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Contains utility methods utilized by most bin files.
 */
public class ByteUtils {
    public static short toShort(byte[] encodedValues, int index) {
        return (short) (((encodedValues[index + 1] & 0xff) << 8) + (encodedValues[index] & 0xff));
    }

    public static int toBigEndianShort(byte[] encodedValues, int index) {
        return (((encodedValues[index] << 8) & 0xFF00) | (encodedValues[index + 1] & 0xFF));
    }

    public static int toInt(byte[] encodedValue, int index) {
        int value = (encodedValue[index + 3] << (Byte.SIZE * 3));
        value |= (encodedValue[index + 2] & 0xFF) << (Byte.SIZE * 2);
        value |= (encodedValue[index + 1] & 0xFF) << (Byte.SIZE);
        value |= (encodedValue[index] & 0xFF);
        return value;
    }

    public static int toInt(List<Byte> bytes, int start) {
        byte[] byteArray = new byte[4];
        for(int x = 0; x < 4; x++)
            byteArray[x] = bytes.get(start + x);
        return toInt(byteArray, 0);
    }

    public static byte[] toByteArray(short value) {
        byte[] ret = new byte[2];
        ret[0] = (byte) (value & 0xff);
        ret[1] = (byte) ((value >> 8) & 0xff);
        return ret;
    }

    public static List<Byte> toByteList(byte[] arr) {
        List<Byte> bytes = new ArrayList<>();
        for (byte b : arr)
            bytes.add(b);
        return bytes;
    }

    public static byte[] toByteArray(int value) {
        byte[] encodedValue = new byte[Integer.SIZE / Byte.SIZE];
        encodedValue[3] = (byte) (value >> (Byte.SIZE * 3));
        encodedValue[2] = (byte) (value >> (Byte.SIZE * 2));
        encodedValue[1] = (byte) (value >> Byte.SIZE);
        encodedValue[0] = (byte) value;
        return encodedValue;
    }

    public static List<Byte> toByteList(int i) {
        final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(i);
        byte[] bytes = bb.array();
        List<Byte> out = new ArrayList<>();
        for(byte b : bytes)
            out.add(b);
        return out;
    }

    public static String getString(byte[] source, int index) throws UnsupportedEncodingException {
        int end = index;
        while (source[end] != 0) {
            end++;
        }
        return new String(Arrays.copyOfRange(source, index, end), "shift-jis");
    }

    /**
     * Reads a pointer from the given index in the source array and then parses a
     * String at the address indicated by the pointer's value + 0x20.
     *
     * @param source The source array containing the pointer and the expected String.
     * @param index An index in source where a pointer to a String occurs.
     * @return The string that the Pointer at index points to.
     * @throws UnsupportedEncodingException An exception will occur if shift-jis encoding is not supported.
     */
    public static String getStringFromPointer(byte[] source, int index) throws UnsupportedEncodingException {
        return getString(source, ByteUtils.toInt(source, index) + 0x20);
    }

    /**
     * Converts an array of bytes to an immutable list of bytes.
     *
     * @param arr The array of bytes to be converted.
     * @return An immutable list of bytes containing every byte from the given array.
     */
    public static List<Byte> byteArrayToImmutableList(byte[] arr) {
        List<Byte> bytes = new ArrayList<>();
        for (byte b : arr)
            bytes.add(b);
        return Collections.unmodifiableList(bytes);
    }
}
