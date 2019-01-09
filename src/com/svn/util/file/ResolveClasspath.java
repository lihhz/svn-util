package com.svn.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.svn.util.log.Logger;

/**
 * 解析.classpath文件
 * 
 * @author lihh
 *
 */
public class ResolveClasspath {

	/**
	 * 解析出classpath中与项目有关的路径和输出路径
	 * @return
	 */
	public static List<Map<String, String>> getInfo() {
		
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			File classpath = getClasspath();
			if(classpath == null || !classpath.exists()){
				Logger.error("没有找到.classpath文件！程序退出");
				System.exit(0);
			}
			
			NodeList nodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(classpath).getDocumentElement().getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node classpathentryNode = nodeList.item(i);
				if (classpathentryNode.getNodeName().equals("classpathentry")) {
					// 避免#text
					NamedNodeMap map = classpathentryNode.getAttributes();
					if (map != null) {
						Map<String, String> m = new HashMap<String, String>();
						Node item = map.getNamedItem("path");
						if (item.getNodeValue().startsWith("src")) {
							m.put(item.getNodeName(), item.getNodeValue());
							Node output = map.getNamedItem("output");
							m.put(output.getNodeName(), output.getNodeValue());
							list.add(m);
						}
					}
				}
			}
		}catch (Exception e) {
			Logger.error("发生错误！程序退出");
			e.printStackTrace();
			System.exit(0);
		}
		return list;
	}
	/**
	 * 找.classpath文件
	 * @return
	 */
	private static File getClasspath(){
		File proDir = new File(FileUtil.getRealFilePath(PropertyUtil.getProjectPath()));
		if (!proDir.isDirectory()) {
			// 不是文件夹，来个报错
			return null;
		}
		return new File(FileUtil.getRealFilePath(proDir.getAbsolutePath() + "/.classpath"));
	}
}
