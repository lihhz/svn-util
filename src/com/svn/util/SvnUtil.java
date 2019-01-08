package com.svn.util;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.ISvnObjectReceiver;
import org.tmatesoft.svn.core.wc2.SvnLog;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnRevisionRange;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import com.svn.util.encrypt.DeEnCode;
import com.svn.util.file.PropertyUtil;
import com.svn.util.system.ClipboardSupport;
import com.svn.util.system.ConsoleUtil;
/**
 * 使用svnkit将对比内容导出为string
 * @author lihhz
 *
 */
public class SvnUtil {

	/**
	 * 使用svnkit将对比内容导出为string
	 * @return
	 * @throws SVNException 
	 * @throws UnsupportedEncodingException 
	 * @throws Exception
	 */
	public static String getChangeStr() throws SVNException, UnsupportedEncodingException{

		final StringBuffer sb = new StringBuffer();
		boolean auth = true;
		//用户信息
		String account = PropertyUtil.getUserAccount(),pass=PropertyUtil.getPassword();
		try{
			if(StringUtils.isEmpty(account)){
				System.out.println("没有找到用户信息!");
	            account = ConsoleUtil.readLine(StringUtils.COUNTER++ + ".请输入用户名:");  
	            pass = ConsoleUtil.readPassword(StringUtils.COUNTER++ + ".请输入密码:");
			}else{
				pass = DeEnCode.decode(pass);
			}
			
			Long bVersion ,eVersion;
			while(true){
				String v;
				while(true){
					v = ConsoleUtil.readLine(StringUtils.COUNTER++ + ".请输入起始版本号(导出时,包括该版本代码):");
					try{
						bVersion = Long.parseLong(v);
						break;
					}catch(NumberFormatException e){
						System.out.println("起始版本号无效,请重新输入!");
					}
				}
				while(true){
					v = ConsoleUtil.readLine(StringUtils.COUNTER++ + ".请输入截止版本号(导出时,包括该版本代码):");
					try{
						eVersion = Long.parseLong(v);
						break;
					}catch(NumberFormatException e){
						System.out.println("截止版本号无效,请重新输入!");
					}
				}
				if(bVersion <= eVersion){
					break;
				}else{
					System.out.println("错误:结束版本号["+eVersion+"]大于起始版本号["+bVersion+"]");
				}
			}
            
			// 实例化客户端管理类
			final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
			// svn用户名密码
			svnOperationFactory.setAuthenticationManager(BasicAuthenticationManager.newInstance(account, pass.toCharArray()));
			
			final SvnLog log = svnOperationFactory.createLog();
			log.addRange(SvnRevisionRange.create(SVNRevision.create(bVersion), SVNRevision.create(eVersion)));
			log.setDiscoverChangedPaths(true);
			//注意在配置文件中svnUri不能是Unicode
			log.setSingleTarget(SvnTarget.fromURL(SVNURL.parseURIEncoded(PropertyUtil.getSvnUri())));
	
			System.out.println(StringUtils.COUNTER++ + ".修改内容如下");
			final StringBuffer msg = new StringBuffer();
			log.setReceiver(new ISvnObjectReceiver<SVNLogEntry>() {
				public void receive(SvnTarget svnTarget, SVNLogEntry svnLogEntry) throws SVNException {
					// 每个版本执行一次
					// System.out.println("版本:" + arg1.getRevision() + "===========作者：" + arg1.getAuthor() + "======时间：" + sdf2.format(arg1.getDate()));
					String str = svnLogEntry.getMessage();
					if(!str.trim().equals("")){
						Pattern p = Pattern.compile("\\s*|\t|\r|\n");   
			            Matcher m = p.matcher(str);   
						msg.append("<li>").append(m.replaceAll("")).append("</li>");
					}
					System.out.println("   " + str);
					Map<String, SVNLogEntryPath> map = svnLogEntry.getChangedPaths();
					if (map.size() > 0) {
						Set<String> set = map.keySet();
						for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
							String key = (String) iterator.next();
							sb.append(key.replace(PropertyUtil.getUnnecessaryStr(), "")).append("\r\n");
							// SVNLogEntryPath path = map.get(key);
							// System.out.println(typeDic.get(path.getType() + "") + ":" + key);
							// handleFile(key);
						}
					}
				}
			});
			log.run();
			// 将升级信息添加到粘贴板
			ClipboardSupport.setSysClipboardText(msg.toString());
			System.out.println();
			System.out.println("升级内容已复制到剪贴板");
			System.out.println();
		}catch( SVNAuthenticationException e){
			e.printStackTrace();
			auth = false;
			System.out.println("鉴权失败!"+e.getMessage());
		}finally{
			PropertyUtil.writeUserProperties("userAccount", auth?account:"");
			PropertyUtil.writeUserProperties("password", auth?DeEnCode.encode(pass):"");
			//同步用户信息
			PropertyUtil.syncUserInfo();
		}
		return sb.toString();
	}
//	public static void main(String[] args) {
//		try{
//			// 实例化客户端管理类
//			final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
//			svnOperationFactory.setAuthenticationManager(BasicAuthenticationManager.newInstance("", "".toCharArray()));// svn用户名密码
//			
//			final SvnLog log = svnOperationFactory.createLog();
//			log.addRange(SvnRevisionRange.create(SVNRevision.create(37707), SVNRevision.create(37714)));
//			log.setDiscoverChangedPaths(true);
//			log.setSingleTarget(SvnTarget.fromURL(SVNURL.parseURIEncoded("http://svn.ufgov.com.cn/A7/source/project/trunk/BJ/\u6ce8\u518c\u4f1a\u8ba1\u5e08\u884c\u4e1a\u76d1\u7ba1\u7cfb\u7edf/cpams/cpams")));
//	
//			System.out.println(StringUtils.COUNTER++ + ".修改内容如下");
//			log.setReceiver(new ISvnObjectReceiver<SVNLogEntry>() {
//				public void receive(SvnTarget svnTarget, SVNLogEntry svnLogEntry) throws SVNException {
//					// 每个版本执行一次
////					 System.out.println("版本:" + arg1.getRevision() + "===========作者：" + arg1.getAuthor() + "======时间：" + sdf2.format(arg1.getDate()));
//					System.out.println("   "+svnLogEntry.getMessage());
//					Map<String, SVNLogEntryPath> map = svnLogEntry.getChangedPaths();
//					if (map.size() > 0) {
//						Set<String> set = map.keySet();
//						for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
//							String key = (String) iterator.next();
////							sb.append(key.replace("/source/project/trunk/BJ/注册会计师行业监管系统/cpams/cpams/", "")).append("\r\n");
//							 SVNLogEntryPath path = map.get(key);
////							 System.out.println(typeDic.get(path.getType() + "") + ":" + key);
//							System.out.println(key+","+path);
//						}
//					}
//				}
//			});
//		}catch( SVNAuthenticationException e){
//			System.out.println("鉴权失败!"+e.getMessage());
//		} catch (SVNException e) {
//			e.printStackTrace();
//		}finally{
//		}
//	
//	}
}
