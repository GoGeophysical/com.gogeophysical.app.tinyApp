module com.gogeophysical.app.tinyApp {
	exports com.gogeophysical.tiny.app;
	exports com.gogeophysical.tiny.api;

	requires com.gogeophysical.appToolControl;
	requires com.gogeophysical.javaPreferences;
	requires com.gogeophysical.javafx;
	requires com.gogeophysical.log4j;
	requires com.gogeophysical.licenseDesktop;
	
	requires java.management;
	requires org.apache.commons.io;
}