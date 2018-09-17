package com.svn.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 操作文件<br/>
 * 路径判断<br/>
 * 
 * @author Administrator
 *
 */
public class FileUtil {
	


	/**
	 * 判断系统类型
	 * 
	 * @return
	 */
	public static boolean IS_WIN_OS = System.getProperty("os.name").toLowerCase().startsWith("win");
	/**
	 * 删除文件夹
	 * 
	 * @param folderPath
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			(new File(folderPath)).delete(); // 删除空文件夹
		} catch (Exception e) {
			System.out.println("文件夹删除失败!");
			e.printStackTrace();
		}
	}

	/**
	 * 删除路径下的所有文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		for (int i = 0; i < tempList.length; i++) {
			File temp = new File(path + (path.endsWith(File.separator) ? "" : File.separator) + tempList[i]);
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	/***
	 * 复制单个文件*
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt*
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt*@return boolean
	 */
	private static void mkDir(File file) {
		if (file.getParentFile().exists()) {
			file.mkdir();
		} else {
			mkDir(file.getParentFile());
			file.mkdir();
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param oldPath
	 * @param newPath
	 */
	public static void copyFile(String oldPath, String newPath) {
		InputStream is = null;
		OutputStream os = null;

		// 使用BufferedInputStream比直接使用InputStream更高效
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			oldPath = getRealFilePath(oldPath);
			newPath = getRealFilePath(newPath);
			String[] arr1 = newPath.split("\\.");
			// 注意这里:为多操作系统做判断.windows是\\
			String[] arr2 = arr1[0].split(IS_WIN_OS ? File.separator + File.separator : File.separator);
			String filePath = "";
			for (int i = 0; i <= arr2.length - 2; i++) {
				filePath += arr2[i] + File.separator;
			}

			File file = new File(filePath);
			mkDir(file);
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				is = new FileInputStream(oldPath); // 读入原文件
				bis = new BufferedInputStream(is);

				os = new FileOutputStream(newPath);
				bos = new BufferedOutputStream(os);

				byte[] buffer = new byte[1024];
				 while ((bis.read(buffer)) != -1) {
					// bos.write(buffer, 0, length);
					// bos.write内部实际上调用如下write(b, 0, b.length);，因此可以不需要做的这么复杂
					bos.write(buffer);

				}
				// 缓冲区的内容写入到文件
				bos.flush();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
				// 只需要关闭装饰类即可，因为会在内部调用被装饰类的close。查看源码以获得证据
				/*
				 * if (is != null) { is.close(); } if (os != null) { os.flush();
				 * os.close(); }
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getRealFilePath(String path) {
		return path.replace("/", File.separator).replace("\\", File.separator);
	}

	public static String getHttpURLPath(String path) {
		return path.replace("\\", "/");
	}

	/**
	 * 检验路径是否合法
	 * 
	 * @param path
	 * @return
	 */
	public static boolean checkPath(String path) {
		if (StringUtils.isEmpty(path)) {
			return false;
		}
		return IS_WIN_OS ? path.contains(":") : !path.contains(":");
	}

}
