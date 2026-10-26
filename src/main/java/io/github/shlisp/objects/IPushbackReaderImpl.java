/*
 *   Masahito Henmi
 *   This file is licensed under the same terms as "Armed Bear Common Lisp (ABCL)".
 */
package io.github.shlisp.objects;

import java.io.Closeable;
import java.io.IOException;
import java.io.PushbackReader;

public class IPushbackReaderImpl implements IPushbackReader, Closeable {

    private final PushbackReader pr;

    private int _lineNumber = 1;
    private int _columnNumber = 1;

    // read() する前の位置の履歴。unread() で1つずつ戻す（リングバッファ）
    // PushbackReader のバッファ（5 以上）より大きければよい
    private static final int HISTORY_SIZE = 16;
    private final int[] histLine = new int[HISTORY_SIZE];
    private final int[] histColumn = new int[HISTORY_SIZE];
    private int histTop = 0;     // 次に積む場所
    private int histCount = 0;   // 積んである数

    private final String fileName;


    private static final int NEWLINE = (int) '\n';

    public IPushbackReaderImpl(PushbackReader pushback, String fileName) {
        this.pr = pushback;
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public void close() throws IOException {

        pr.close();
    }

    @Override
    public int read() throws IOException {
        int c = pr.read();
        if (c == -1) {
            // EOF は位置を進めないし、履歴にも積まない
            return c;
        }

        // 読む前の位置を積む（いっぱいなら一番古いものを上書き）
        histLine[histTop] = _lineNumber;
        histColumn[histTop] = _columnNumber;
        histTop = (histTop + 1) % HISTORY_SIZE;
        if (histCount < HISTORY_SIZE) {
            histCount++;
        }

        if (c == NEWLINE) {
            _columnNumber = 1;
            _lineNumber++;
        } else {
            _columnNumber++;
        }

        return c;
    }

    @Override
    public void unread(int c) throws IOException {
        if (c == -1) {
            // EOF を戻すと ￿ が入ってしまうので無視
            return;
        }
        pr.unread(c);

        // 読む前の位置に戻す（改行を戻したときは、前の行の列に戻る）
        if (histCount > 0) {
            histTop = (histTop - 1 + HISTORY_SIZE) % HISTORY_SIZE;
            histCount--;
            _lineNumber = histLine[histTop];
            _columnNumber = histColumn[histTop];
        }
    }

    // 位置を変えずに次の文字を見る（CL の peek-char）
    public int peek() throws IOException {
        int c = read();
        unread(c);
        return c;
    }

    @Override
    public boolean ready() throws IOException {
        return pr.ready();
    }

    @Override
    public int getColumnNumber() {
        return _columnNumber;
    }

    @Override
    public int getLineNumber() {
        return _lineNumber;
    }

}
