package feflib.controls;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexField extends TextField {
    private static final String MASK_HEXADECIMAL = "H";
    private static final Pattern PATTERN_HEXADECIMAL = Pattern.compile("[0-9A-Fa-f]");
    private static final char EMPTY_CHAR = "0".charAt(0);
    private final String mask;
    private String text;
    private boolean spaced;
    private Set<Character> specialSymbols = new HashSet<>();

    public HexField(int length, boolean spaced) {
        super();

        String mask;
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < length; x++) {
            if (x > 0) {
                if (spaced)
                    sb.append(" HH");
                else
                    sb.append("HH");
            } else
                sb.append("HH");
        }
        mask = sb.toString();

        this.mask = mask;
        this.spaced = spaced;
        this.text = textFromMask(specialSymbols);
        this.setText(text);

        this.caretPositionProperty().addListener((observable1, oldValue, newValue) -> {
            int caretPosition = (int) newValue;
            if (caretPosition >= text.length()) {
                Platform.runLater(() -> positionCaret(text.length()));
                return;
            }
            if (caretPosition < 0) {
                Platform.runLater(() -> positionCaret(0));
            }
        });

        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && (getText() == null || getText().equals(""))) {
                this.setText(text);
                Platform.runLater(() -> positionCaret(0));
            }
            if (!newValue && getText().equals(textFromMask(null))) {
                this.setText(text);
            }
        });

        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null && !newValue.equals("") &&
                    (text == null || !text.equals(newValue))) {
                final int caretPosition = getCaretPosition();
                if (caretPosition >= oldValue.length() || isSpecial(oldValue.charAt(caretPosition))) {
                    text = oldValue;
                    setText(text);
                    return;
                }
                char challenger = Character.toUpperCase(newValue.charAt(caretPosition));
                String currentMask = String.valueOf(mask.charAt(caretPosition));
                Pattern currentPattern;

                switch (currentMask) {
                    case MASK_HEXADECIMAL:
                        currentPattern = PATTERN_HEXADECIMAL;
                        break;
                    default:
                        throw new IllegalArgumentException();
                }

                Matcher matcher = currentPattern.matcher(String.valueOf(challenger));

                if (matcher.matches()) {
                    text = (replaceInsteadInsertion(newValue, challenger, caretPosition));
                    if (caretPosition + 1 < text.length() && isSpecial(text.charAt(caretPosition + 1))) {
                        Platform.runLater(() -> positionCaret(caretPosition + 2));
                    } else Platform.runLater(() -> positionCaret(caretPosition + 1));
                } else {
                    text = oldValue;
                }
                this.setText(text);

            } else if (newValue != null && newValue.length() == mask.length()) {
                text = newValue;
                textFromMask(specialSymbols);
                this.setText(text);
            }
        });

        this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getEventType() == KeyEvent.KEY_PRESSED) {
                final int caretPosition = getCaretPosition();
                switch (event.getCode()) {
                    case BACK_SPACE:
                        if (caretPosition != 0) {
                            for (int i = 1; caretPosition - i >= 0; i++) {
                                if (!isSpecial(text.charAt(caretPosition - i))) {
                                    text = replaceCharAtPosition(text, EMPTY_CHAR, caretPosition - i);
                                    this.setText(text);
                                    final int j = i;
                                    Platform.runLater(() -> positionCaret(caretPosition - j));
                                    break;
                                }
                            }
                        }
                        event.consume();
                        break;
                    case DELETE:
                        for (int i = 0; caretPosition + i < text.length(); i++) {
                            if (!isSpecial(text.charAt(caretPosition + i))) {
                                text = replaceCharAtPosition(text, EMPTY_CHAR, caretPosition + i);
                                this.setText(text);
                                final int j = i + 1;
                                Platform.runLater(() -> positionCaret(caretPosition + j));
                                break;
                            }
                        }
                        event.consume();
                        break;
                }
            }
        });
    }

    private static String replaceInsteadInsertion(String str, char ch, int pos) {
        char[] buffer = new char[str.toCharArray().length];
        for (int i = 0, j = 0; i < buffer.length; i++, j++) {
            if (i != pos) {
                buffer[j] = str.toCharArray()[i];
            } else {
                buffer[j] = ch;
                ++i;
            }
        }
        String result = new String(buffer);
        result = result.substring(0, result.length() - 1);
        return result;
    }

    private static String replaceCharAtPosition(String str, char ch, int pos) {
        char[] buffer = new char[str.toCharArray().length];
        for (int i = 0; i < buffer.length; i++) {
            if (i != pos) buffer[i] = str.toCharArray()[i];
            else buffer[i] = ch;
        }
        str = new String(buffer);
        return str;
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (start == end && !text.equals("")) super.replaceText(start, end, text);
    }

    /**
     * Method used for check an empty characters.
     *
     * @return true - no empty characters
     */
    public boolean isFilled() {
        for (char ch : text.toCharArray()) {
            if (ch == EMPTY_CHAR) return false;
        }
        return true;
    }

    private String textFromMask(Set<Character> specialSymbols) {
        String tempText = mask.replace(MASK_HEXADECIMAL, String.valueOf(EMPTY_CHAR));
        if (specialSymbols != null) {
            for (int i = 0; i < tempText.length(); i++) {
                char ch = tempText.charAt(i);
                if (ch != EMPTY_CHAR) specialSymbols.add(ch);
            }
        }
        return tempText;
    }

    private boolean isSpecial(char character) {
        for (char ch : specialSymbols) {
            if (character == ch) return true;
        }
        return false;
    }

    public byte[] getValue() {
        String[] split;
        if (spaced)
            split = this.getText().split(" ");
        else
            split = this.getText().split("(?<=\\G..)");
        byte[] bytes = new byte[split.length];
        for (int x = 0; x < bytes.length; x++)
            bytes[x] = (byte) Integer.parseInt(split[x], 16);
        return bytes;
    }

    public void setValue(byte[] values) {
        StringBuilder out = new StringBuilder();
        for (byte value : values) {
            String formatted = String.format("%02x", value & 0xFF).toUpperCase();
            out.append(formatted).append(" ");
        }
        text = out.toString();
        this.setText(out.toString());
    }
}
