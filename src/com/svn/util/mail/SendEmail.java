package com.svn.util.mail;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.svn.util.file.PropertyUtil;

public class SendEmail {

	// 发件人地址
//	private static String senderAddress = PropertyUtil.getProperty("senderAddress");
	// 收件人地址
	private static String recipientToAddress = PropertyUtil.getProperty("recipientToAddress");
	// 抄送地址
	private static String recipientCcAddress = PropertyUtil.getProperty("recipientCcAddress");
	// 发件人账户名
	private static String senderAccount = PropertyUtil.getProperty("senderAccount");
	// 发件人账户密码
	private static String senderPassword = PropertyUtil.getProperty("senderPassword");
	
	private static String emlFilePath = PropertyUtil.getTargetPath()+".eml";

	public static void sendEmail(String title,String content,String attachPath){

		List<String> toList = new ArrayList<String>();
		String[] arr = recipientToAddress.split(";");
		if (arr.length > 0) {
			for (int i = 0; i < arr.length; i++) {
				System.out.println("发送到:" + arr[i]);
				toList.add(arr[i]);
			}
		}
		List<String> bccList = new ArrayList<String>();
		String[] bccarr = recipientCcAddress.split(";");
		if (bccarr.length > 0) {
			for (int i = 0; i < bccarr.length; i++) {
				System.out.println("抄送到:" + bccarr[i]);
				bccList.add(bccarr[i]);
			}
		}

		new SendEmailSupport(senderAccount, senderPassword).setDebug(true)
//		.setMyNickName("这是我的昵称")
				.addFile(attachPath)// 添加附件
//				.addFile("C:/Users/25171/Desktop/QQ图片20180317192741.jpg")
				// .addFile(List<String> list)//添加附件集合
				.setSaveEmail(emlFilePath)// 保存邮件
				// .addRecipientT0("251716795@qq.com")//添加收件人地址
				.addRecipientT0(toList)// 添加收件人地址集合
				// .addRecipientCC(map)//添加密送收件人地址
				.addRecipientCC(bccList)// 添加抄送收件人地址
//				.createMail("标题", "发送的内容", "text/html;charset=utf-8").sendEmail(new SendEmail.Callback() {
//					@Override
//					public void success(String s) {
//						System.out.println(s);// 发送完成后回调接口
//					}
//
//					@Override
//					public void error(String s, Exception e) {
//						System.out.println(s);
//						e.printStackTrace();// 异常失败的回调接口
//					}
//				});
		.createMail(title, content, "text/html;charset=utf-8");
		try {
			Desktop.getDesktop().open(new File(emlFilePath));
		} catch (IOException e) {
			System.out.println("打开邮件客户端失败，请手动打开邮件！邮件路径为："+ emlFilePath);
		}
	}
	

//	public static void main(String[] args) {
//		sendEmail("title","contetn",PropertyUtil.getTargetPath()+"/cpams.zip");
//	}

//	public static void main2(String[] args) throws Exception {
//		// 1、连接邮件服务器的参数配置
//		Properties props = new Properties();
//		// 设置用户的认证方式
//		props.setProperty("mail.smtp.auth", "true");
//		// 设置传输协议
//		props.setProperty("mail.transport.protocol", "smtp");
//		// 设置发件人的SMTP服务器地址
//		props.setProperty("mail.smtp.host", "smtp.163.com");
//		// 2、创建定义整个应用程序所需的环境信息的 Session 对象
//		Session session = Session.getInstance(props);
//		// 设置调试信息在控制台打印出来
//		session.setDebug(true);
//		// 3、创建邮件的实例对象
//		Message msg = createMimeMessage(session, "Hello World", "C:\\Users\\Administrator\\Desktop\\cpams.zip");
//
//		msg.saveChanges();
//
//		saveEmail(msg, "123", "C:\\Users\\Administrator\\Desktop\\2.eml");
//
//		// 4、根据session对象获取邮件传输对象Transport
//		// Transport transport = session.getTransport();
//		// // 设置发件人的账户名和密码
//		// transport.connect(senderAccount, senderPassword);
//		// // 发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人,
//		// // 抄送人, 密送人
//		// transport.sendMessage(msg, msg.getAllRecipients());
//		//
//		// // 5、关闭邮件连接
//		// transport.close();
//	}

//	/**
//	 * 获得创建一封邮件的实例对象
//	 * 
//	 * @param session
//	 * @return
//	 * @throws MessagingException
//	 * @throws AddressException
//	 */
//	public static MimeMessage createMimeMessage(Session session, String textMsg, String filePath) throws Exception {
//		// 1.创建一封邮件的实例对象
//		MimeMessage msg = new MimeMessage(session);
//		// 2.设置发件人地址
//		msg.setFrom(new InternetAddress(senderAddress));
//		new InternetAddress();
//		/**
//		 * 3.设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
//		 * MimeMessage.RecipientType.TO:发送 MimeMessage.RecipientType.CC：抄送
//		 * MimeMessage.RecipientType.BCC：密送
//		 */
//		// msg.setRecipient(MimeMessage.RecipientType.TO,new
//		// InternetAddress(recipientToAddress));
//		// msg.setRecipients(MimeMessage.RecipientType.TO,
//		// InternetAddress.parse(recipientToAddress));
//		// msg.setRecipients(MimeMessage.RecipientType.CC,
//		// InternetAddress.parse(recipientCcAddress));
//		setRecipientCc(msg);
//		setRecipientTo(msg);
//		// 4.设置邮件主题
//		msg.setSubject("邮件主题", "UTF-8");
//
//		// 下面是设置邮件正文
//		// msg.setContent("简单的纯文本邮件！", "text/html;charset=UTF-8");
//
//		// // 5. 创建图片"节点"
//		// MimeBodyPart image = new MimeBodyPart();
//		// // 读取本地文件
//		// DataHandler dh = new DataHandler(new
//		// FileDataSource("src\\mailTestPic.png"));
//		// // 将图片数据添加到"节点"
//		// image.setDataHandler(dh);
//		// // 为"节点"设置一个唯一编号（在文本"节点"将引用该ID）
//		// image.setContentID("mailTestPic");
//
//		// 6. 创建文本"节点"
//		MimeBodyPart text = new MimeBodyPart();
//		// 这里添加图片的方式是将整个图片包含到邮件内容中, 实际上也可以以 http 链接的形式添加网络图片
//		// text.setContent("这是一张图片<br/><a
//		// href='http://www.cnblogs.com/ysocean/p/7666061.html'><img
//		// src='cid:mailTestPic'/></a>", "text/html;charset=UTF-8");
//		text.setContent(textMsg, "text/html;charset=UTF-8");
//
//		// // 7. （文本+图片）设置 文本 和 图片"节点"的关系（将 文本 和 图片"节点"合成一个混合"节点"）
//		// MimeMultipart mm_text_image = new MimeMultipart();
//		// mm_text_image.addBodyPart(text);
//		// mm_text_image.addBodyPart(image);
//		// mm_text_image.setSubType("related"); // 关联关系
//		//
//		// // 8. 将 文本+图片 的混合"节点"封装成一个普通"节点"
//		// // 最终添加到邮件的 Content 是由多个 BodyPart 组成的 Multipart, 所以我们需要的是 BodyPart,
//		// // 上面的 mailTestPic 并非 BodyPart, 所有要把 mm_text_image 封装成一个 BodyPart
//		// MimeBodyPart text_image = new MimeBodyPart();
//		// text_image.setContent(mm_text_image);
//
//		// 9. 创建附件"节点"
//		MimeBodyPart attachment = new MimeBodyPart();
//		// 读取本地文件
//		// DataHandler dh2 = new DataHandler(new
//		// FileDataSource("src\\mailTestDoc.docx"));
//		DataHandler dh2 = new DataHandler(new FileDataSource(filePath));
//
//		// 将附件数据添加到"节点"
//		attachment.setDataHandler(dh2);
//		// 设置附件的文件名（需要编码）
//		attachment.setFileName(MimeUtility.encodeText(dh2.getName()));
//
//		// 10. 设置（文本+图片）和 附件 的关系（合成一个大的混合"节点" / Multipart ）
//		MimeMultipart mm = new MimeMultipart();
//		// mm.addBodyPart(text_image);
//		mm.addBodyPart(text);
//		mm.addBodyPart(attachment); // 如果有多个附件，可以创建多个多次添加
//		mm.setSubType("mixed"); // 混合关系
//
//		// 11. 设置整个邮件的关系（将最终的混合"节点"作为邮件的内容添加到邮件对象）
//		msg.setContent(mm);
//		// 设置邮件的发送时间,默认立即发送
//		msg.setSentDate(new Date());
//
//		return msg;
//	}

	/*** 设置密送地址 **/
	// private static void setRecipientCC(Message message) throws
	// MessagingException, UnsupportedEncodingException {
	// if (recipientCCList.size() > 0) {
	// InternetAddress[] sendTo = new InternetAddress[recipientCCList.size()];
	// for (int i = 0; i < recipientCCList.size(); i++) {
	// System.out.println("发送到:" + recipientCCList.get(i));
	// sendTo[i] = new InternetAddress(recipientCCList.get(i), "", "UTF-8");
	// }
	// message.addRecipients(MimeMessage.RecipientType.CC, sendTo);
	// }
	// }

//	/*** 设置发送邮件地址 **/
//	private static void setRecipientTo(Message message) throws MessagingException, UnsupportedEncodingException {
//		String[] arr = recipientToAddress.split(";");
//		if (arr.length > 0) {
//			InternetAddress[] sendTo = new InternetAddress[arr.length];
//			for (int i = 0; i < arr.length; i++) {
//				System.out.println("发送到:" + arr[i]);
//				sendTo[i] = new InternetAddress(arr[i], "", "UTF-8");
//			}
//			message.addRecipients(MimeMessage.RecipientType.TO, sendTo);
//		}
//	}
//
//	/*** 设置抄送邮件地址 **/
//	private static void setRecipientCc(Message message) throws MessagingException, UnsupportedEncodingException {
//		String[] arr = recipientCcAddress.split(";");
//		if (arr.length > 0) {
//			InternetAddress[] sendTo = new InternetAddress[arr.length];
//			for (int i = 0; i < arr.length; i++) {
//				System.out.println("发送到:" + arr[i]);
//				sendTo[i] = new InternetAddress(arr[i], "", "UTF-8");
//			}
//			message.addRecipients(MimeMessage.RecipientType.CC, sendTo);
//		}
//	}
//
//	private static void saveEmail(Message message, String title, String pathName)
//			throws IOException, MessagingException {
//		OutputStream out = null;
//
//		if (pathName.length() == 0 || pathName.equals(null)) {
//			out = new FileOutputStream(title + ".eml");
//		} else {
//			String path[] = pathName.split("\\.");
//			out = new FileOutputStream(path[0] + title + ".eml");
//		}
//
//		message.writeTo(out);
//		out.flush();
//		out.close();
//	}
}
