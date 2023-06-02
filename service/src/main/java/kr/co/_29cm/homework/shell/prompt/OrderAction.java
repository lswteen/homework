package kr.co._29cm.homework.shell.prompt;

import org.jline.reader.LineReader;

@FunctionalInterface
public interface OrderAction {
    void execute(LineReader lineReader);
}
