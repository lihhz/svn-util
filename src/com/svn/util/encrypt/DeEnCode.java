package com.svn.util.encrypt;  
  
import java.nio.charset.Charset;  
  
/**
 * 使用异或简单加密解密
 * @author Administrator
 *
 */
public class DeEnCode {  
  
    private static final Charset charset = Charset.forName("UTF-8");
    //这个key可以作为配置做私钥
    private static byte[] keyBytes = "FECOI()*&<MNCXZPKL".getBytes(charset);  
      
    /**
     * 编码作为加密
     * @param enc
     * @return
     */
    public static String encode(String enc){  
        byte[] b = enc.getBytes(charset);  
        for(int i=0,size=b.length;i<size;i++){  
            for(byte kb:keyBytes){  
                b[i] = (byte) (b[i]^kb);  
            }  
        }  
        return new String(b);  
    }
      
    /**
     * 解码作为解密
     * @param dec
     * @return
     */
    public static String decode(String dec){  
        byte[] e = dec.getBytes(charset);  
        byte[] dee = e;  
        for(int i=0,size=e.length;i<size;i++){  
            for(byte kb:keyBytes){  
                e[i] = (byte) (dee[i]^kb);  
            }  
        }  
        return new String(e);  
    } 
}  