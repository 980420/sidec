package gehos.comun.util.url;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.util.stream.Streams;

public class ArrayURL {
    
    public static URL create(String name, byte b[]) {
        try {
            ArrayURLStreamHandler sh = new ArrayURLStreamHandler(b);
            return new URL("array", null, 0, name, sh);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
	}
    
    public static URL create(String name, InputStream is) throws IOException {
        if (is == null)
            throw new NullPointerException("is");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Streams.copy(is, os);
        is.close();
        return create(name, os.toByteArray());
    }

}
