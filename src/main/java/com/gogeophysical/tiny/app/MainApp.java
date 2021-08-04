package com.gogeophysical.tiny.app;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gogeophysical.appToolControl.AppToolController;
import com.gogeophysical.appToolControl.MyAppToolConfig;
import com.gogeophysical.javaPreferences.PrefManager;
import com.gogeophysical.licenseDesktop.LicenseUICore;
import com.gogeophysical.tiny.api.SysLogTool;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	public String appName = "Tiny App";
	private String appOfficial = "com.gogeophysical.app.tinyApp";
	private int appPort = 2014;
//	private String lic = "ZGQCypv2oGlFOfq3lHyN760pizslMy"; // Summer Lic
	public static final Double GOLDEN_RATIO = 1.618;
	private static Logger log;

	public AppToolController control;

	public ResourceBundle words;

	public PrefManager prefs = new PrefManager(MainApp.class);
	public Stage primaryStage;

	public enum Apps {LOG};
	
	public Image fishIcon = new Image(getClass().getResourceAsStream("/iconsTiny/icons8-roach-96.png"));
	public Image logIcon = new Image(getClass().getResourceAsStream("/iconsTiny/icons8-activity-history-32.png"));


	@Override
	public void init() {

		AppToolController.checkInstance(appPort); // this will cause systen exit if app already running.

		// wait to call logger so it doesn't overwrite the one if already running
		log = LogManager.getLogger();
		log.info("init");
	}
	
	private void reInit() {
		log.info("Initializing Main App");
		try {
			prefs.addDefaultString("language", "en");

			MyAppToolConfig config = new MyAppToolConfig(); // this file is the configuration.  COuld make xml?

			config.addToolConfig(Apps.LOG.toString(), Apps.LOG.toString(),  logIcon, SysLogTool.class.getName());
			
			config.includeApp(Apps.LOG.toString());


			control = new AppToolController(this, config);

			log.info("Done Initializing Main App");

		} catch (Exception e) {
			log.error(e.getStackTrace().toString());
			log.error("ERROR", e);
		}		
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		log.info("start");

		try {
			reInit();

			if (this.primaryStage==null) {
				this.primaryStage = stage;
			}

			Scene scene ;
			if (stage.getScene()!=null) {
				double width = stage.getScene().getWidth();
				double height = stage.getScene().getHeight();
				scene = new Scene(control.getBorderPane(), width, height);

			} else {
				scene = new Scene(control.getBorderPane(), 600*GOLDEN_RATIO, 600);
			}

			control.getBorderPane().setOnMouseEntered(e->{
				primaryStage.toFront();
			});

//			URL sqlScriptUrl = MainApp.class.getClassLoader().getResource("chart.css");
//			scene.getStylesheets().add(sqlScriptUrl.toString());
//
//			URL css = MainApp.class.getClassLoader().getResource("chart.css");
//			scene.getStylesheets().add(css.toString());
			
			primaryStage.setTitle(appName);		
			primaryStage.setScene(scene);
			primaryStage.show();

			//customEditsToBorderPane();

			control.hideToolBar();
			control.showToolBar(); 
			
			finishLoading();
			//control.showApp(Apps.STAT.toString());
		} catch (Exception e) {
			log.error("ERROR",e);
		}
	}
	
	private void finishLoading() {

		control.hideToolBar();
		control.hideLeft(true);
		Runnable success  = () -> {
			control.hideLeftHideButton();
			control.hideUnDockButton();	
			control.hidePDFButton();
			control.hidePNGButton();
			control.showToolBar(); 
			control.hideRightHideButton();
			control.hideLeft(false);
			control.showApp(Apps.LOG.toString());
		};
		Runnable failure  = () -> {};

		try {
			LicenseUICore.runLicRegCheck(primaryStage, prefs, appName, appOfficial, success, failure);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void stop() {
		log.info("System exit via stop Method");
		LogManager.shutdown();
		Platform.exit();
		System.exit(0);
	}
	
	public void runSafe(final Runnable runnable) {
		Objects.requireNonNull(runnable, "runnable");
		if (Platform.isFxApplicationThread()) {
			runnable.run();
		} else {
			Platform.runLater(runnable);
		}
	}
}
