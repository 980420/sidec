package gehos.configuracion.filtroFirefox;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author diuber
 *
 */
public class RichFacesFirefoxFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) request) {
            @Override
            public String getRequestURI() {
                try {
                    return URLDecoder.decode(super.getRequestURI(), "UTF-8");
                } catch (UnsupportedEncodingException e) {

                    throw new IllegalStateException("Cannot decode request URI.", e);
                }
            }
        }, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    public void destroy() {
        // do nothing
    }

}
