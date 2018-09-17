package com.svn.util.useless;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.svn.util.FileUtil;
import com.svn.util.PropertyUtil;

/**
 * 执行命令帮助类
 * 2018/4/24 抛弃.使用svnkit来代替
 * @author lihh
 *
 */
@Deprecated
public class CmdUtil {

	/**
	 * 组装命令字符串
	 * @return
	 */
	private static String getCmdStr() {
		
		return String.format("%s cd %s&&svn diff -r %s --summarize", 
				FileUtil.IS_WIN_OS ? "cmd /c " + PropertyUtil.getProjectPath().substring(0, 2) +"&&" : "",
						FileUtil.getRealFilePath(PropertyUtil.getProjectPath()),
						PropertyUtil.getVersion());
	}

	/**
	 * 执行命令
	 * 
	 * @param cmd
	 * @return
	 */
	public static String[] runCmd() {
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		BufferedReader reader = null;
		StringBuffer content = new StringBuffer();
		try {
			String cmd = getCmdStr();
			if (FileUtil.IS_WIN_OS) {
				System.out.println(" 开始执行Windows命令：" + cmd);
				process = runtime.exec(cmd);
			} else {
				System.out.println(" 开始执行Linux命令：" + cmd);
				String[] cmdA = { "/bin/sh", "-c", cmd };
				process = runtime.exec(cmdA);
			}
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();// 等待子进程完成再往下执行。
			String line = null;
			while ((line = reader.readLine()) != null) {
				content.append(line + "\r\n");
			}
			//如果命令执行错误，那么输出错误信息
			if(content.toString().equals("")) {
				reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				while ((line = reader.readLine()) != null) {
					content.append(line + "\r\n");
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			reader = null;
		}

		int i = process.exitValue();// 接收执行完毕的返回值
		process.destroy();// 销毁子进程
		process = null;
		return new String[]{String.valueOf(i),content.toString()};
	}

}
