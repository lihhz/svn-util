package com.svn.util.mail;
 
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
 
class SendEmailSupport {
 
    public interface Callback {
        void success(String s);
 
        void error(String s, Exception e);
    }
 
    private Callback callback;  //信息回调接口
    private Properties properties;//系统属性对象
    private String mailAccount;   //发送邮箱地址
    private String mailPassword;  //验证密码
    private Session session;      //邮件会话对象
    private String myNickName;    //昵称，发送时自己的昵称
    private boolean debug = false;//debug模式
    private boolean isSaveEmail = false;
    private String pathName = "exc.eml";//邮件保存时的
 
    public SendEmailSupport(String mailAccount, String mailPassword) {
        this.mailAccount = mailAccount;
        this.mailPassword = mailPassword;
    }
 
    public SendEmailSupport setSaveEmail(String pathName) {
        isSaveEmail = true;
        this.pathName = pathName;
        return this;
    }
 
    private List<String> recipientT0List = new ArrayList<>();//收件地址
    private List<String> recipientCCList = new ArrayList<>();//密送地址
    private List<String> recipientBCCList = new ArrayList<>();//抄送地址
    private List<String> filePath = new ArrayList<>();//附件
 
    public SendEmailSupport setDebug(boolean sessionDebug) {
        debug = sessionDebug;
        return this;
    }
 
    /*** 设置多人收件人地址 */
    public SendEmailSupport addRecipientT0(String address) {
        recipientT0List.add(address);
        return this;
    }
 
    public SendEmailSupport addRecipientCC(String address) {
        recipientCCList.add(address);
        return this;
    }
 
    public SendEmailSupport addRecipientBCC(String address) {
        recipientBCCList.add(address);
        return this;
    }
 
    public SendEmailSupport addRecipientT0(List<String> address) {
        recipientT0List.addAll(address);
        return this;
    }
 
    public SendEmailSupport addRecipientCC(List<String> address) {
        recipientCCList.addAll(address);
        return this;
    }
 
    public SendEmailSupport addRecipientBCC(List<String> address) {
        recipientBCCList.addAll(address);
        return this;
    }
 
    /***添加文件***/
    public SendEmailSupport addFile(String filePath) {
        this.filePath.add(filePath);
        return this;
    }
 
    public SendEmailSupport addFile(List<String> list) {
        this.filePath.addAll(list);
        return this;
    }
 
    /****昵称设置**/
    public SendEmailSupport setMyNickName(String name) {
        myNickName = name;
        return this;
    }
 
    private MimeMessage message;
 
    /**
     * @param title 主题
     * @param datas 内容
     * @param type  内容格式类型 text/html;charset=utf-8
     * @return s
     */
    public SendEmailSupport createMail(String title, String datas, String type) {
        if (mailAccount.length() == 0 || mailAccount.equals(null)) {
            System.err.println("发件地址不存在！");
            return this;
        }
        if (myNickName == null) {
            myNickName = mailAccount;
        }
        getProperties();
        if (!sync) return this;
        try {
            message = new MimeMessage(session);
            // 设置发送邮件地址,param1 代表发送地址 param2 代表发送的名称(任意的) param3 代表名称编码方式
            message.setFrom(new InternetAddress(mailAccount, myNickName, "utf-8"));
 
            setRecipientT0();   //添加接收人地址
            setRecipientCC();   //添加抄送接收人地址
            setRecipientBCC();  //添加密送接收人地址
            BodyPart messageBodyPart = new MimeBodyPart();  // 创建消息部分
            Multipart multipart = new MimeMultipart();      // 创建多重消息
 
            messageBodyPart.setContent(datas, type);        // 消息内容
            multipart.addBodyPart(messageBodyPart);         // 设置文本消息部分
 
            addFile(multipart);                             //附件部分
            // 发送完整消息
            message.setContent(multipart);
            message.setSubject(title);              // 设置邮件主题
            message.setSentDate(new Date());        // 设置发送时间
            message.saveChanges();                  // 保存上面的编辑内容
            // 将上面创建的对象写入本地
            saveEmail(title);
        } catch (Exception e) {
            if (callback != null)
                callback.error("message error ", e);
            sync = false;
            System.out.println("生成邮件失败！请自行编辑邮件");
        }
        return this;
    }
 
    public void sendEmail(Callback callback) {
        this.callback = callback;
        if (!sync)
            return;
        try {
            Transport trans = session.getTransport();
            // 链接邮件服务器
            trans.connect(mailAccount, mailPassword);
            // 发送信息
            trans.sendMessage(message, message.getAllRecipients());
            // 关闭链接
            trans.close();
            if (callback != null)
                callback.success("发送完成");
        } catch (Exception e) {
            if (callback != null)
                callback.error("发送异常", e);
        }
    }
 
    private void saveEmail(String title) throws IOException, MessagingException {
        OutputStream out = null;
        if (isSaveEmail) {
            if (pathName.length() == 0 || pathName.equals(null)) {
                out = new FileOutputStream(title + ".eml");
            } else {
                String path[] = pathName.split("\\.");
                out = new FileOutputStream(path[0] + ".eml"); //+ title 
            }
        }
        message.writeTo(out);
        out.flush();
        out.close();
    }
 
    /*** 设置收件人地址信息*/
    private void setRecipientT0() throws MessagingException, UnsupportedEncodingException {
        if (recipientT0List.size() > 0) {
            InternetAddress[] sendTo = new InternetAddress[recipientT0List.size()];
            for (int i = 0; i < recipientT0List.size(); i++) {
                System.out.println("发送到:" + recipientT0List.get(i));
                sendTo[i] = new InternetAddress(recipientT0List.get(i), "", "UTF-8");
            }
            message.addRecipients(MimeMessage.RecipientType.TO, sendTo);
        }
    }
 
    /***设置密送地址**/
    private void setRecipientCC() throws MessagingException, UnsupportedEncodingException {
        if (recipientCCList.size() > 0) {
            InternetAddress[] sendTo = new InternetAddress[recipientCCList.size()];
            for (int i = 0; i < recipientCCList.size(); i++) {
                System.out.println("发送到:" + recipientCCList.get(i));
                sendTo[i] = new InternetAddress(recipientCCList.get(i), "", "UTF-8");
            }
            message.addRecipients(MimeMessage.RecipientType.CC, sendTo);
        }
    }
 
    /***设置抄送邮件地址**/
    private void setRecipientBCC() throws MessagingException, UnsupportedEncodingException {
        if (recipientBCCList.size() > 0) {
            InternetAddress[] sendTo = new InternetAddress[recipientBCCList.size()];
            for (int i = 0; i < recipientBCCList.size(); i++) {
                System.out.println("发送到:" + recipientBCCList.get(i));
                sendTo[i] = new InternetAddress(recipientBCCList.get(i), "", "UTF-8");
            }
            message.addRecipients(MimeMessage.RecipientType.BCC, sendTo);
        }
    }
 
    /***添加附件****/
    private void addFile(Multipart multipart) throws MessagingException, UnsupportedEncodingException {
        if (filePath.size() == 0)
            return;
        for (int i = 0; i < filePath.size(); i++) {
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            // 选择出每一个附件名
            String pathName = filePath.get(i);
            System.out.println("添加附件 ====>" + pathName);
            // 得到数据源
            FileDataSource fds = new FileDataSource(pathName);
            // 得到附件本身并至入BodyPart
            messageBodyPart.setDataHandler(new DataHandler(fds));
            //采用这去除中文乱码
            messageBodyPart.setFileName(MimeUtility.encodeText(fds.getName()));
            multipart.addBodyPart(messageBodyPart);
        }
    }
 
    private boolean sync = true;
 
    /**
     * 规定设置 传输协议为smtp  根据输入的邮箱地址自动匹配smtp服务器地址与smtp服务器地址端口
     */
    private void getProperties() {
        String account[] = mailAccount.split("@");
        String mailTpye = "";
        try {
            mailTpye = account[1];
        } catch (Exception e) {
            System.err.println("不正确的邮箱地址！");
            sync = false;
            return;
        }
        String SMTPHost = "";//smtp服务器地址
        String SMTPPort = "";//smtp服务器地址端口
        switch (mailTpye) {
            case "qq.com":
            case "foxmail.com":
                SMTPHost = "smtp.qq.com";
                SMTPPort = "465";
                break;
            case "sina.com":
                SMTPHost = "smtp.sina.com";
                SMTPPort = "25";
                break;
            case "sina.cn":
                SMTPHost = "smtp.sina.cn";
                SMTPPort = "25";
                break;
            case "139.com":
                SMTPHost = "smtp.139.com";
                SMTPPort = "465";
                break;
            case "163.com":
                SMTPHost = "smtp.163.com";
                SMTPPort = "25";
                break;
            case "188.com":
                SMTPHost = "smtp.188.com";
                SMTPPort = "25";
                break;
            case "126.com":
                SMTPHost = "smtp.126.com";
                SMTPPort = "25";
                break;
            case "gmail.com":
                SMTPHost = "smtp.gmail.com";
                SMTPPort = "465";
                break;
            case "outlook.com":
                SMTPHost = "smtp.outlook.com";
                SMTPPort = "465";
                break;
            default:
                System.err.println("暂时不支持此账号作为服务账号发送邮件！");
                return;
        }
        Properties prop = new Properties();
        prop.setProperty("mail.transport.protocol", "smtp"); // 设置邮件传输采用的协议smtp
        prop.setProperty("mail.smtp.host", SMTPHost);// 设置发送人邮件服务器的smtp地址
        prop.setProperty("mail.smtp.auth", "true");     // 设置验证机制
        prop.setProperty("mail.smtp.port", SMTPPort);// SMTP 服务器的端口 (非 SSL 连接的端口一般默认为 25, 可以不添加, 如果开启了 SSL 连接,
        prop.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.setProperty("mail.smtp.socketFactory.fallback", "false");
        prop.setProperty("mail.smtp.socketFactory.port", SMTPPort);
        properties = prop;
        session = Session.getInstance(properties);
        session.setDebug(debug);
    }
 
}
