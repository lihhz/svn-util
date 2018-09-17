package com.svn.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileSystemView;

import org.tmatesoft.svn.core.SVNException;

public class SvnMain {

	/**
	 * 核心方法
	 * 
	 * @param content
	 * @throws RuntimeException
	 * @throws FileNotFoundException
	 */
	private static void packFiles(String content) throws FileNotFoundException, RuntimeException {
		if (StringUtils.isEmpty(content)) {
			System.out.println("发生异常或者没有发生变化.");
			return;
		}
		// 获取svn配置和输出路径配置
		final String PROJECT_PATH = PropertyUtil.getProjectPath(), TARGET_PAHT = PropertyUtil.getTargetPath();
		if (StringUtils.isEmpty(PROJECT_PATH)) {
			System.out.println("应用程序无法获取conf.properties中的projectPath，请检查配置文件.");
			return;
		}
		if (StringUtils.isEmpty(TARGET_PAHT)) {
			System.out.println("应用程序无法获取conf.properties中的targetPath，请检查配置文件.");
			return;
		}

		System.out.println(StringUtils.COUNTER++ + ".开始组装文件");
		String[] contents = content.split("\r\n");
		for (int i = 0; i < contents.length; i++) {
			String line = contents[i];
			if (StringUtils.isEmpty(line)) {
				continue;
			}
			// 注意这里:为多操作系统做判断.windows是\\
			// 改为svnkit后不需要判断系统
			// FileUtil.isWinOs() ? "src\\\\([\\\\a-zA-Z0-9_\\-\\.]+)" :
			String regex = "src/([/a-zA-Z0-9_\\-\\.]+)";
			Matcher matcher = Pattern.compile(regex).matcher(line);
			while (matcher.find()) {
				String relativePath = matcher.group(0);
				if (relativePath.contains(".") && !relativePath.equals(".")) {// 只处理文件
					System.out.println(String.format("...正在处理文件：%s", relativePath));
					String oldPath = PROJECT_PATH + File.separator + relativePath;
					List<Map<String, String>> classpathList = ResolveClasspath.getInfo();
					boolean isInClasspath = false;
					for (Map<String, String> map : classpathList) {
						final String path = map.get("path"), output = map.get("output");
						// String temp = FileUtil.isWinOs() ? relativePath.replace("\\", "/") :
						// relativePath;
						if (relativePath.startsWith(path)) {
							isInClasspath = true;
							relativePath = relativePath.replace(path, "");
							final String from = PROJECT_PATH + File.separator + output + relativePath;
							// System.out.println(" 将从路径："+from+"拷贝文件！");
							if (oldPath.endsWith(".java")) {
								FileUtil.copyFile(from.split("\\.")[0] + ".class",
										TARGET_PAHT + "/WEB-INF/classes/" + relativePath.split("\\.")[0] + ".class");
								// 对内部类的支持
								// TODO:测试这个/在Linux和Windows下是否相同
								File parentDir = new File(from.split("\\.")[0] + ".class").getParentFile();
								File[] fileList = parentDir.listFiles();
								if (fileList != null) {
									for (File file : fileList) {
										String fileName = file.getName();
										String className = relativePath.substring(relativePath.lastIndexOf("/") + 1)
												.split("\\.")[0];
										if (fileName.startsWith(className)) {
											System.out.println();
										}
										if (fileName.startsWith(className + "$")) {
											FileUtil.copyFile(file.getAbsolutePath(), TARGET_PAHT + "/WEB-INF/classes/"
													+ relativePath.split("\\.")[0].replace(className, fileName));
										}
									}
								}
							} else {// 处理web文件
								FileUtil.copyFile(from, TARGET_PAHT + "/WEB-INF/classes" + relativePath);
							}
						} else {
							continue;
						}
					}
					if (!isInClasspath) {
						// 不是class，是webapp。照搬即可
						FileUtil.copyFile(oldPath,
								TARGET_PAHT + File.separator + relativePath.replace("src/main/webapp/", ""));
					}
				} else {
					// TODO:这里处理的是文件夹，先不管。因为空文件夹不必处理
					System.out.println("  文件夹暂不处理");
				}
			}
		}
		System.out.println("---------------组装文件结束---------------");
		confirm(TARGET_PAHT);

	}

	/**
	 * 善后事宜.用来压缩和删除文件夹
	 * 
	 * @param targetPath
	 */
	private static void confirm(String targetPath) {
		try {
			// Scanner scanner = new Scanner(System.in);
			System.out.println(StringUtils.COUNTER++ + ".是否生成zip压缩文件?(y/n)");
			// 判断是否还有输入
			while (ConsoleUtil.hasNext()) {
				String str = ConsoleUtil.readLine();
				if (str.equalsIgnoreCase("y")) {
					ZipUtil.toZip(FileUtil.getRealFilePath(targetPath),
							new FileOutputStream(FileUtil.getRealFilePath(targetPath + ".zip")), true);
					System.out.println("==文件压缩成功.");
				}
				if (str.equalsIgnoreCase("y") || str.equalsIgnoreCase("n")) {
					break;
				} else {
					System.out.println("==输入字符非法,请重新输入!");
				}
			}
			System.out.println(StringUtils.COUNTER++ + ".是否删除之前生成的文件夹?(y/n)");
			while (ConsoleUtil.hasNext()) {
				String str = ConsoleUtil.readLine();
				if (str.equalsIgnoreCase("y")) {
					FileUtil.delFolder(FileUtil.getRealFilePath(targetPath));
					System.out.println("之前生成的文件夹删除成功");
				}
				if (str.equalsIgnoreCase("y") || str.equalsIgnoreCase("n")) {
					break;
				} else {
					System.out.println("输入字符非法,请重新输入!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 校验路径是否合法
	 */
	private static void checkPath() {
		// 校验配置文件，目前主要是校验linux和Windows的路径问题
		if (!FileUtil.checkPath(PropertyUtil.getProjectPath())) {
			System.out.println("项目路径与操作系统不匹配，请检查后重试！");
			System.exit(0);
		}
		if (!FileUtil.checkPath(PropertyUtil.getTargetPath())) {
			System.out.println("输出路径与操作系统不匹配，请检查后重试！");
			System.exit(0);
		}	
	}

	public static void main(String[] args) throws Exception, RuntimeException, SVNException {
		// 2018/4/24 抛弃之前使用svn.exe的做法
		// String[] ret = CmdUtil.runCmd();
		// // 0:执行完成;否则执行失败
		// if (ret[0].equals("0")) {
		// packFiles(ret[1]);
		// } else {
		// System.out.println("命令执行失败！失败原因：" + ret[1]);
		// System.exit(0);
		// }
		
		checkPath();
		String defaultDisktop = PropertyUtil.getDefaultDisktop();
		if (StringUtils.isEmpty(defaultDisktop) || defaultDisktop.equals("1")) {
			// 2018/5/30 获取当前用户桌面.有待在Linux下测试
			File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
			String desktopPath = desktopDir.getAbsolutePath();
			if(!FileUtil.IS_WIN_OS) {
				desktopPath += File.separator+"Desktop";
			}

			System.out.println("******************************************************************************");
			System.out.println("检测到你已配置默认输出到桌面.");
			System.out.println("如需取消,请修改conf.properties中defaultDisktop为0并设置targetPath!");
			System.out.println("读取桌面路径为:" + desktopPath);
			System.out.println("设置输出路径为:" + desktopPath + File.separator + "cpams");
			System.out.println("******************************************************************************");
			PropertyUtil.writeConfProperties("targetPath", desktopPath + File.separator + "cpams");
		}
		while (true) {
			packFiles(SvnUtil.getChangeStr());
			System.out.println("本次运行结束!");
			String str = ConsoleUtil.readLine("输入y继续运行,输入其它程序退出:");
			if (!str.equalsIgnoreCase("y")) {
				System.out.println("程序运行结束,程序退出!");
				break;
			}
			// scanner.close();

		}

		// String str = "abc{{dddd_23}}fjdfjidabc{{dddd_3}}fjdfjid";

		// Matcher matcher =
		// Pattern.compile("(\\{\\{)[a-zA-Z0-9_]+(\\}\\})").matcher(str);
		// while (matcher.find()) {
		// String relativePath = matcher.group(0);
		// System.out.println(relativePath);
		// }
	}
}
