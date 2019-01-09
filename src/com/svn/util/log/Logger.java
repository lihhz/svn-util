package com.svn.util.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.svn.util.StringUtils;
import com.svn.util.log.LoggerSupport.Level;

public class Logger {
	
	static{
		try {
			LoggerSupport.setLogOutFile(new File("run.log"));
			LoggerSupport.setLogOutTarget(true, true);
			LoggerSupport.setLogOutLevel(Level.INFO);
			info("");
			info("======="+new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS").format(new Date()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void closeConsoleLogger(String str){

		LoggerSupport.setLogOutTarget(false, true);
		Logger.info(str);
		LoggerSupport.setLogOutTarget(true, true);
	}
	
	public static void step(String message) {
		info(StringUtils.COUNTER++ +". "+ message);
	}
	public static void normal(String message) {
		info(message);
	}

	public static void info(String message) {
		LoggerSupport.info(Level.INFO.tag, message);
	}

	public static void warn(String message) {
		LoggerSupport.warn(Level.WARN.tag, message);
	}

	public static void error(String message) {
		LoggerSupport.error(Level.ERROR.tag, message);
	}
	
	public static void main(String[] args) {
		info("134");
	}

}