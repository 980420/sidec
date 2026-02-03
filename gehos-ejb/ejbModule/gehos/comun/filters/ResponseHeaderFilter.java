package gehos.comun.filters;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ResponseHeaderFilter implements Filter {

	private FilterConfig filterConfig;

	public void destroy() {
	}

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpResp = (HttpServletResponse) response;
		Enumeration e = filterConfig.getInitParameterNames();
		while (e.hasMoreElements()) {
			String headerName = (String) e.nextElement();
			httpResp.addHeader(headerName, filterConfig
					.getInitParameter(headerName));
			//System.out.println("FILTRO LLAMADO");
		}
		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

}
