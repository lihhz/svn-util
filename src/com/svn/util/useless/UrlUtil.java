package com.svn.util.useless;

import java.io.CharArrayWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.BitSet;
@Deprecated
public class UrlUtil {

	// 以下代码判断是否encode过
	private static BitSet dontNeedEncoding;
	static final int caseDiff = ('a' - 'A');
	static {
		dontNeedEncoding = new BitSet(256);
		int i;
		for (i = 'a'; i <= 'z'; i++) {
			dontNeedEncoding.set(i);
		}
		for (i = 'A'; i <= 'Z'; i++) {
			dontNeedEncoding.set(i);
		}
		for (i = '0'; i <= '9'; i++) {
			dontNeedEncoding.set(i);
		}
		dontNeedEncoding.set('+');
		/**
		 * 这里会有误差,比如输入一个字符串 123+456,它到底是原文就是123+456还是123 456做了urlEncode后的内容呢？
		 * <br>
		 * 其实问题是一样的，比如遇到123%2B456,它到底是原文即使如此，还是123+456 urlEncode后的呢？ <br>
		 * 在这里，我认为只要符合urlEncode规范的，就当作已经urlEncode过了<br>
		 * 毕竟这个方法的初衷就是判断string是否urlEncode过<br>
		 */

		dontNeedEncoding.set('/');
		dontNeedEncoding.set(':');
		dontNeedEncoding.set('-');
		dontNeedEncoding.set('_');
		dontNeedEncoding.set('.');
		dontNeedEncoding.set('*');
	}

	/**
	 * 判断str是否urlEncoder.encode过<br>
	 * 经常遇到这样的情况，拿到一个URL,但是搞不清楚到底要不要encode.<Br>
	 * 不做encode吧，担心出错，做encode吧，又怕重复了<Br>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean hasUrlEncoded(String str) {

		/**
		 * 支持JAVA的URLEncoder.encode出来的string做判断。 即: 将' '转成'+' <br>
		 * 0-9a-zA-Z保留 <br>
		 * '-'，'_'，'.'，'*'保留 <br>
		 * 其他字符转成%XX的格式，X是16进制的大写字符，范围是[0-9A-F]
		 */
		boolean needEncode = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (dontNeedEncoding.get((int) c)) {
				continue;
			}
			if (c == '%' && (i + 2) < str.length()) {
				// 判断是否符合urlEncode规范
				char c1 = str.charAt(++i);
				char c2 = str.charAt(++i);
				if (isDigit16Char(c1) && isDigit16Char(c2)) {
					continue;
				}
			}
			// 其他字符，肯定需要urlEncode
			needEncode = true;
			break;
		}

		return !needEncode;
	}

	/**
	 * 判断c是否是16进制的字符
	 * 
	 * @param c
	 * @return
	 */
	private static boolean isDigit16Char(char c) {
		return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F');
	}

	public static String encode(String s, String enc) throws UnsupportedEncodingException {

		boolean needToChange = false;
		StringBuffer out = new StringBuffer(s.length());
		Charset charset;
		CharArrayWriter charArrayWriter = new CharArrayWriter();

		if (enc == null)
			throw new NullPointerException("charsetName");

		try {
			charset = Charset.forName(enc);
		} catch (IllegalCharsetNameException e) {
			throw new UnsupportedEncodingException(enc);
		} catch (UnsupportedCharsetException e) {
			throw new UnsupportedEncodingException(enc);
		}

		for (int i = 0; i < s.length();) {
			int c = (int) s.charAt(i);
			// System.out.println("Examining character: " + c);
			if (dontNeedEncoding.get(c)) {
				if (c == ' ') {
					c = '+';
					needToChange = true;
				}
				// System.out.println("Storing: " + c);
				out.append((char) c);
				i++;
			} else {
				// convert to external encoding before hex conversion
				do {
					charArrayWriter.write(c);
					/*
					 * If this character represents the start of a Unicode
					 * surrogate pair, then pass in two characters. It's not
					 * clear what should be done if a bytes reserved in the
					 * surrogate pairs range occurs outside of a legal surrogate
					 * pair. For now, just treat it as if it were any other
					 * character.
					 */
					if (c >= 0xD800 && c <= 0xDBFF) {
						/*
						 * System.out.println(Integer.toHexString(c) +
						 * " is high surrogate");
						 */
						if ((i + 1) < s.length()) {
							int d = (int) s.charAt(i + 1);
							/*
							 * System.out.println("\tExamining " +
							 * Integer.toHexString(d));
							 */
							if (d >= 0xDC00 && d <= 0xDFFF) {
								/*
								 * System.out.println("\t" +
								 * Integer.toHexString(d) + " is low surrogate"
								 * );
								 */
								charArrayWriter.write(d);
								i++;
							}
						}
					}
					i++;
				} while (i < s.length() && !dontNeedEncoding.get((c = (int) s.charAt(i))));

				charArrayWriter.flush();
				String str = new String(charArrayWriter.toCharArray());
				byte[] ba = str.getBytes(charset);
				for (int j = 0; j < ba.length; j++) {
					out.append('%');
					char ch = Character.forDigit((ba[j] >> 4) & 0xF, 16);
					// converting to use uppercase letter as part of
					// the hex value if ch is a letter.
					if (Character.isLetter(ch)) {
						ch -= caseDiff;
					}
					out.append(ch);
					ch = Character.forDigit(ba[j] & 0xF, 16);
					if (Character.isLetter(ch)) {
						ch -= caseDiff;
					}
					out.append(ch);
				}
				charArrayWriter.reset();
				needToChange = true;
			}
		}

		return (needToChange ? out.toString() : s);
	}
}
