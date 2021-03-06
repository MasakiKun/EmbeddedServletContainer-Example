package kr.ayukawa.embeddservletcontainer.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.net.URL;
import java.util.Optional;

public class EntryPoint {
	public static final String WEBAPP_DIR = "webapp/";

	public static void main(String[] args) throws Exception {
		Optional<String> port = Optional.ofNullable(System.getProperty("tomcat.port"));

		File tomcatWorkingDir = File.createTempFile("tomcat.", port.orElse(".8080"));
		if(tomcatWorkingDir.exists()) {
			tomcatWorkingDir.delete();
			tomcatWorkingDir.mkdir();
			tomcatWorkingDir.deleteOnExit();
		}
		System.out.println(tomcatWorkingDir.getAbsoluteFile());

		Tomcat tomcat = new Tomcat();
		tomcat.setPort(Integer.valueOf(port.orElse("8080")));
		tomcat.setBaseDir(tomcatWorkingDir.getAbsolutePath());

		URL webappUrl = EntryPoint.class.getResource("/webapp");
		File webapp = new File(webappUrl.toURI());

		// ContextPath 등록
		Context ctx = tomcat.addWebapp("/", new File(WEBAPP_DIR).getAbsolutePath());

/*
		File additionWebInfClasses = new File("target/classes");
		WebResourceRoot resources = new StandardRoot(ctx);
		resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
		ctx.setResources(resources);
*/
/*
		StandardJarScanner scanner = (StandardJarScanner)ctx.getJarScanner();
		scanner.setScanClassPath(true);
		scanner.setScanBootstrapClassPath(true);
		scanner.setScanAllDirectories(true);
		scanner.setScanAllFiles(true);
*/

		// Servlet 등록
		tomcat.addServlet(ctx, "helloServlet", new kr.ayukawa.embeddservletcontainer.servlet.HelloServlet());

		// Servlet에 URL Mapping
		ctx.addServletMapping("/hello", "helloServlet");

		tomcat.start();
		tomcat.getServer().await();
	}
}
