/*
 * Copyright (c) Jim Coles (jameskcoles@gmail.com) 2016. through present.
 *
 * Licensed under the following license agreement:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Also see the LICENSE file in the repository root directory.
 */

package org.jkcsoft.web.jsp;

import org.jkcsoft.java.util.CharReplacer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;

public class JspUtil
{
  /**
   *
   */
  public static void setCookie(HttpServletResponse res, String cookieName,
                       String cookieValue, String path, int timeLength)
  {
    Cookie c = new Cookie(cookieName, cookieValue);
    c.setPath(path);
    c.setMaxAge(timeLength);
    res.addCookie(c);
  }

  /**
   *
   */
  public static void deleteCookie(HttpServletResponse res, String cookieName, String path)
  {
    setCookie(res, cookieName, null, path, 0);
  }

  /**
   *
   */
  public static String getValueFromCookie(HttpServletRequest req, String name)
  {
    String value = null;
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        // found cookieId
        if (cookies[i].getName().equals(name)) {
          value = (String) cookies[i].getValue();
          break;
        }
      }
    }
    return value;
  }

  /**
   *
   */
  public static Enumeration getRequestHeaders(HttpServletRequest req)
  {
    return req.getHeaderNames();
  }


  /**
   * doesn't work for IE 5.x as IE doesn't report the referer header
   */
  public static String getReferer(HttpServletRequest req)
  {
    return req.getHeader("REFERER");
  }

  /**
   *
   */
  public static boolean isBlank(HttpServletRequest req, String name)
  {
    return isBlank(req.getParameter(name));
  }

  /**
   *
   */
  public static boolean isBlank(String name)
  {
    boolean tf = false;
    if (name == null || name.trim().equals("") || name.equals("null"))
    {
      tf = true;
    }
    return tf;
  }


  //*************************

  public static final String CHAR_SCHEME_UTF8 = "UTF-8";
  
  public static String urlEncode(String str)
  {
    String retVal = null;
    try {
      retVal = URLEncoder.encode(str, CHAR_SCHEME_UTF8);
    }
    catch (java.io.UnsupportedEncodingException ex) {
      //
      System.out.println(ex.getMessage());
    }
    return retVal;
  }

  //*************************

  public static String urlDecode(String str)
  {
    String retVal = null;
    try {
      retVal = URLDecoder.decode(str, CHAR_SCHEME_UTF8);
    }
    catch (java.io.UnsupportedEncodingException ex) {
      System.out.println(ex.getMessage());
    }
    return retVal;
  }

  public static final char CHR_DQUOTE = '\"';
  public static final char CHR_AMP = '&';
  public static final char CHR_FORSLASH = '/';
  public static final char CHR_LT = '<';
  public static final char CHR_GT = '>';
  public static final String STR_DQUOTE = new String(new char[] {CHR_DQUOTE});
  public static final String STR_AMP = new String(new char[] {CHR_AMP});
  public static final String STR_FORSLASH = new String(new char[] {CHR_FORSLASH});
  public static final String STR_LT = new String(new char[] {CHR_LT});
  public static final String STR_GT = new String(new char[] {CHR_GT});
  public static final String CODE_DQUOTE   = "&#34;";
  public static final String CODE_AMP      = "&amp;";
  public static final String CODE_FORSLASH = "&#39;";
  public static final String CODE_LT       = "&lt;";
  public static final String CODE_GT       = "&gt;";

  private static final String[][] HTML_CODE_MAP = {{STR_DQUOTE, CODE_DQUOTE},
                                                   {STR_AMP, CODE_AMP},
                                                   {STR_FORSLASH, CODE_FORSLASH},
                                                   {STR_LT, CODE_LT},
                                                   {STR_GT, CODE_GT}
                                                   };
  private static CharReplacer _htmlCr = null;
  // Create a com.jkc.util.CharReplacer.
  static {
    _htmlCr = new CharReplacer(HTML_CODE_MAP);
  }
  /**
   * Returns the specified string converted to a format suitable for
   * HTML. All single-quote, double-quote, greater-than, less-than and
   * ampersand characters are replaces with their corresponding HTML
   * Character Entity code.
   *
   * @param in the String to convert
   * @return the converted String
   */
  public static String toHTMLString(String in) {
    return _htmlCr.replace(in);
  }
}

