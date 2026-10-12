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

        if (c == NEWLINE) {
            _columnNumber = 1;
            _lineNumber++;
        } else if (c != -1) {
            _columnNumber++;
        }

        return c;
    }

    @Override
    public void unread(int c) throws IOException {
        if (c == NEWLINE) {
            _lineNumber--;
        }
        pr.unread(c);
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
