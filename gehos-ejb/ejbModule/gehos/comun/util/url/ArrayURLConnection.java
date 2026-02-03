package gehos.comun.util.url;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicInteger;

public class ArrayURLConnection extends URLConnection {
    
    static AtomicInteger i = new AtomicInteger();
    private byte[] b;

    protected ArrayURLConnection(byte b[]) throws MalformedURLException, IOException {
        super(new URL("file:" + i.incrementAndGet()));
        this.b = b;
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public int getContentLength() {
        return b.length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(b);
    }

}
