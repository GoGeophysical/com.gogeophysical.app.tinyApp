package com.gogeophysical.tiny.api;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.gogeophysical.appToolControl.AppTool;
import com.gogeophysical.appToolControl.AppToolController;
import com.gogeophysical.appToolControl.AppToolInterface;
import com.gogeophysical.javafx.UI;
import com.gogeophysical.tiny.app.MainApp;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * 
 * This is a tool to display the processing log text to monitor program and to view if there is trouble
 * @author Summer
 *
 */
public class SysLogTool extends AppTool implements AppToolInterface {


	private static final Logger log = LogManager.getLogger();

	private BorderPane mainPane;
	private TextArea area;
	public static String defaultLogName = System.getProperty("user.home") + File.separator + "tinyApp.log";

	private Text statusText;
	MainApp app;

	public SysLogTool(AppToolController control, String appname) {
		super(control, appname);
		this.app = (MainApp) control.application;

	}

	@Override
	public void make() {


		mainPane = new BorderPane();

		Text text2 = new Text("System Runtime Info and Error Logging");

		ToolBar bar = new ToolBar(text2, UI.getLeftSpacer());

		mainPane.setTop(bar);

		Label updateLabel = new Label("last updated:  ");
		statusText = new Text("hello");

		ToolBar status = new ToolBar(updateLabel, statusText);

		mainPane.setBottom(status);
		area = new TextArea();
		HBox hbox = new HBox();
//		scroll = new ScrollPane();
		mainPane.setCenter(area);

		hbox.getChildren().add(area);
		area.prefHeightProperty().bind(control.getBorderPane().heightProperty().multiply(1));
		area.prefWidthProperty().bind(control.getBorderPane().widthProperty().multiply(1));
		area.setWrapText(true);

		mainPane.setCenter(hbox);


		super.centerNode = mainPane;
		super.rightNode = null;
		super.made = true;
	}

	public static void reverseArray(String[] arr) {
		List<String> list = Arrays.asList(arr);
		Collections.reverse(list);
		System.out.print("Reverse Array :");
	}

	@Override
	public void appRefresh() {
		Path fileName = Path.of(defaultLogName);
		if (Files.notExists(fileName)) {
			try {
	            File file = new File(fileName.toString());
	            file.createNewFile();
	            System.out.println("Empty File Created:- " + file.length());
	        } catch (IOException e) {
	        	Alert alert = new Alert(AlertType.ERROR);
	        	alert.setContentText("Could not make default log file: " +fileName.toString() +". Try making this empty file and with read and write permissions.");
	            e.printStackTrace();
	            return;
	        }
		}
		try {

			String actual = "";
			actual = Files.readString(fileName);
			area.setText(actual);
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			String strDate = formatter.format(date);
			statusText.setText(strDate);
			area.end();

			getGeneralJVMInfo();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("ERROR loading: " + fileName, e);
		}

	}

	public static void getGeneralJVMInfo() {

		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		log.info("*******************");
		log.info("uptime: " + Duration.millis(runtimeBean.getUptime()).toString());
		log.info("name: " + runtimeBean.getName());
		log.info("pid: " + runtimeBean.getName().split("@")[0]);
		log.info("heap size: " + runtimeBean.getInputArguments().get(0));

		OperatingSystemMXBean systemBean = ManagementFactory.getOperatingSystemMXBean();
		log.info("os name: " + systemBean.getName());
		log.info("os version: " + systemBean.getVersion());
		log.info("system load average: " + systemBean.getSystemLoadAverage());
		log.info("available processors: " + systemBean.getAvailableProcessors());

		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		log.info("thread count: " + threadBean.getThreadCount());
		log.info("peak thread count: " + threadBean.getPeakThreadCount());

		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heap = memoryBean.getHeapMemoryUsage();
		MemoryUsage nonHeap = memoryBean.getNonHeapMemoryUsage();
		log.info("heap memory max: " + FileUtils.byteCountToDisplaySize(heap.getMax()));
		log.info("heap memory used: " + FileUtils.byteCountToDisplaySize(heap.getUsed()));
		log.info("heap memory init: " + FileUtils.byteCountToDisplaySize(heap.getInit()));
		log.info("heap memory committed: " + FileUtils.byteCountToDisplaySize(heap.getCommitted()));

		log.info("non-heap memory max: " + FileUtils.byteCountToDisplaySize(nonHeap.getMax()));
		log.info("non-non-heap memory used: " + FileUtils.byteCountToDisplaySize(nonHeap.getUsed()));
		log.info("non heap memory init: " + FileUtils.byteCountToDisplaySize(nonHeap.getInit()));
		log.info("non-heap memory committed: " + FileUtils.byteCountToDisplaySize(nonHeap.getCommitted()));

		Runtime runt = Runtime.getRuntime();
		log.info("JVM Initial Memory (-Xms): " + FileUtils.byteCountToDisplaySize(runt.freeMemory()));
		log.info("JVM Maximum Memory (-Xmx): " + FileUtils.byteCountToDisplaySize(runt.maxMemory()));
		log.info("JVM Total Used Memory: " + FileUtils.byteCountToDisplaySize(runt.totalMemory()));

	}

	@Override
	public void appClear() {

	}

}
