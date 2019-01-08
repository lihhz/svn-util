package com.svn.util.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * @Desc:properties文件获取工具类 Created by lihhz on 2017/7/4.
 */
public class PropertyUtil {
	private static InputStream in;
	private static Properties props,userInfoProps;
	/*
	 * 2018/5/30  写入用户Properties信息
	 */
	public static void writeUserProperties (String pKey, String pValue){
		writeProperties("user_info.ini",pKey,pValue);
	}
	/*
	 * 2018/5/30 写入用户Properties信息
	 */
	public static void writeConfProperties (String pKey, String pValue){
		writeProperties("conf.ini",pKey,pValue);
	}
	/**
	 * 写入Properties信息
	 * @param pKey
	 * @param pValue
	 * @throws IOException
	 */
    private static void writeProperties (String file,String pKey, String pValue){
    	//使用InputStream、OutputStream时，可能造成中文乱码
    	//所以这里使用InputStreamReader、OutputStreamWriter
    	InputStreamReader in = null;
    	OutputStreamWriter out =null;
    	try{
	        Properties pps = new Properties();
	        String conf = PropertyUtil.class.getClassLoader().getResource(file).getFile();
	        //对路径进行转码，避免中文或者特殊情况下，被转码情况
	        conf = URLDecoder.decode(conf,"utf-8");
	        InputStreamReader reader = new InputStreamReader(new FileInputStream(conf),"utf-8");
	        //从输入流中读取属性列表（键和元素对） 
	        pps.load(reader);
	        //调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。  
	        //强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
	        out = new OutputStreamWriter(new FileOutputStream(conf), "utf-8");
	        pps.setProperty(pKey, pValue);
	        //以适合使用 load 方法加载到 Properties 表中的格式，  
	        //将此 Properties 表中的属性列表（键和元素对）写入输出流  
	        pps.store(out, "Update " + pKey + " name");
    	}catch(IOException e){
    		e.printStackTrace();
    	}finally{
			try {
	    		if(in != null){
	    			in.close();
	    		}
	    		if(out != null ){
	    			out.close();
	    		}
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
    	}
    }
	/**
	 * 加载conf.ini
	 */
	synchronized private static void loadUserInfoProps() {
		userInfoProps = new Properties();
		try {
			in = PropertyUtil.class.getClassLoader().getResourceAsStream("user_info.ini");
			userInfoProps.load(new InputStreamReader(in, "utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 加载conf.ini
	 */
	synchronized private static void loadProps() {
		props = new Properties();
		try {
			in = PropertyUtil.class.getClassLoader().getResourceAsStream("conf.ini");
			props.load(new InputStreamReader(in, "utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取conf属性值
	 * 
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		if (null == props) {
			loadProps();
		}
		return props.getProperty(key);
	}
	public static String getUserProperty(String key) {
		if (null == userInfoProps) {
			loadUserInfoProps();
		}
		return userInfoProps.getProperty(key);
	}
	

	/**
	 * 再同步一次用户信息.避免第一次访问时,多次询问用户名密码
	 */
	public static void syncUserInfo(){
		loadUserInfoProps();
	}

	public static String getUserAccount(){
		return getUserProperty("userAccount");
	}
	public static String getPassword(){
		return getUserProperty("password");
	}

	public static String getSvnUri(){
		return getProperty("svnUri");
	}

	public static String getProjectPath() {
		return getProperty("projectPath");
	}
	public static String getDebug() {
		return getProperty("debug");
	}
	//2018/5/30 
	public static String getDefaultDisktop() {
		return getProperty("defaultDisktop");
	}
	
	public static String getTargetPath() {
		return getProperty("targetPath");
	}
	public static String getVersion() {
		return getProperty("version");
	}
	public static String getUnnecessaryStr() {
		return getProperty("unnecessaryStr");
	}
	
//	public static void main(String[] args) throws UnsupportedEncodingException {
//		//从当前package下查找文件,注意java文件被编译后是不存在的，只能查找class文件而不是java文件
//		URL path = PropertyUtil.class.getResource("FileUtil.class");
//		System.out.println(path);
//		//file:/media/lihh/%e6%96%b0%e5%8a%a0%e5%8d%b7/workspace/svn-util-v4.4/bin/com/svn/util/FileUtil.class
//		
//		//因为是从当前package下寻找，没有找到所以返回null
//		path = PropertyUtil.class.getResource("user_info.ini");
//		System.out.println(path);
//		//null
//
//		//从classpath下寻找,但是不会自动的递归下边的package，所以如果要寻找package或者文件夹，则需要写从classpath开始的路径
//		path = PropertyUtil.class.getResource("/user_info.ini");
//		System.out.println(path);
//		//file:/media/lihh/%e6%96%b0%e5%8a%a0%e5%8d%b7/workspace/svn-util-v4.4/bin/user_info.ini
//
//		//相当于没有带/的class.getResource()
//		path = PropertyUtil.class.getClassLoader().getResource("/user_info.ini");
//		System.out.println(path);
//		//null
//
//		//相当于带/的class.getResource()
//		path = PropertyUtil.class.getClassLoader().getResource("user_info.ini");
//		System.out.println(path);
//		//file:/media/lihh/%e6%96%b0%e5%8a%a0%e5%8d%b7/workspace/svn-util-v4.4/bin/user_info.ini
//	}
}