package kr.co._29cm.homework.shell.prompt;

import org.jline.reader.LineReader;

@FunctionalInterface
interface OrderAction {
    void execute(LineReader lineReader);
}
