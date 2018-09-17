package com.svn.util;

import java.io.Console;
import java.util.Scanner;

/**
 * 命令行工具。<br/>
 * 主要目的有两个<br/>
 * 1.为了将调试模式和运行模式区分开<br/>
 * 2.统一处理程序中的Console和Scanner<br/>
 * 使用console<br/>
 * 好处是:避免控制台显示密码<br/>
 * 缺点是:无法再eclipse控制台调试<br/>
 * @author Administrator
 *
 */
public class ConsoleUtil {
	// 是否调试模式
	private static boolean isDebug = StringUtils.isEmpty(PropertyUtil.getDebug()) ? true
			: PropertyUtil.getDebug().equals("1");

	private static Console console;
	private static Scanner scanner;

	static {
		if (isDebug) {
			scanner = new Scanner(System.in);
		} else {
			console = System.console();
			if (console == null) {
				System.out.println("应用程序无法获取当前操作系统的控制台，程序退出！");
				System.exit(0);
			}
		}
	}

	/**
	 * 读取密码。
	 * @param desc
	 * @return
	 */
	public static String readPassword(String desc) {
		if (isDebug) {
			System.out.println(desc);
			return scanner.next();
		} else {
			return String.valueOf(console.readPassword(desc));
		}
	}
	public static String readLine(String desc) {
		return getLine(desc);
	}

	public static String readLine() {
		return getLine("");
	}

	private static String getLine(String desc) {
		if (isDebug) {
			System.out.println(desc);
			return scanner.next();
		} else {
			return console.readLine(desc);
		}
	}

	public static boolean hasNext() {
		return isDebug ? scanner.hasNext() : true;
	}

}
