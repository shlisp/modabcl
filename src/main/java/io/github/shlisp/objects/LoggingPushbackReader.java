/*
 *   Masahito Henmi
 *   This file is licensed under the same terms as "Armed Bear Common Lisp (ABCL)".
 */
package io.github.shlisp.objects;

import java.io.Closeable;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class LoggingPushbackReader implements IPushbackReader, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingPushbackReader.class);
    
    final IPushbackReader dele;
    
    public LoggingPushbackReader(IPushbackReader pr) {
        this.dele = pr;
    }
    
    @Override
    public String getFileName() {
        return dele.getFileName();
    }
    
    
    @Override
    public int read() throws IOException {
        return dele.read();
    }

    @Override
    public void unread(int c) throws IOException {
        
        LOG.warn("========== unread  {}", Integer.toHexString(c));

        dele.unread(c);
    }

    @Override
    public boolean ready() throws IOException {
        return dele.ready();
    }

    @Override
    public int getColumnNumber() {
        return dele.getColumnNumber();
    }

    @Override
    public int getLineNumber() {
        return dele.getLineNumber();
    }

    @Override
    public void close() throws IOException {
        if (dele instanceof Closeable) {
            Closeable cloa = (Closeable) dele;
            cloa.close();
        }
    }
    
}
