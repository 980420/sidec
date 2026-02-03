package gehos.comun.util.url;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class ArrayURLStreamHandler extends URLStreamHandler {

    private byte[] b;

    public ArrayURLStreamHandler(byte[] b) {
        this.b = b;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new ArrayURLConnection(b);
    }

}
