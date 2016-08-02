/*
 * Copyright (c) Jim Coles (jameskcoles@gmail.com) 2016. through present.
 *
 * Licensed under the following license agreement:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Also see the LICENSE file in the repository root directory.
 */

package org.jkcsoft.web.struts.http;

//import com.metrowerks.mdp2.beans.Users;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import javax.security.auth.Subject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import org.jkcsoft.java.util.CharReplacer;
import org.jkcsoft.java.util.Dates;
import org.jkcsoft.java.util.LogHelper;
import org.jkcsoft.java.util.Strings;

public class HttpHelper {
    // session keys...
    private static final String KEY_TSESS_PERMS = "tsess.perms";
    private static final String KEY_NOW_TIME = "tsess.now";
    private static final String KEY_USER = "tsess.user";
    private static final String KEY_USER_DIR_ENTRY = "tsess.user.dir-entry";
    
    public static final Log log = LogHelper.getLogger(HttpHelper.class);
    public static final String PARM_BACK_URL = "startPage";
    public static final String PREFIX_ACTION = "action.";

    /**
     * Returns attribute value if found. If not or is not parsable returns default.
     *
     * @param request
     * @param param
     * @param defaultValue
     */
    public static int getAttributeValueAsInt(HttpServletRequest request,
                                             String param,
                                             int defaultValue) {
        String value = (String) request.getAttribute(param);
        if (value == null)
            return defaultValue;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    /**
     * Combine the 3 parms from a Display.genSelDate() generated select into a Timestamp
     * @param request
     * @param base
     * @return null if year is 0 otherwise vaild Timestamp object
     *
     */
    public static Timestamp getParameterValueAsDate(HttpServletRequest request,
                                                    String base) {

        return getParameterValueAsDate(request,base,true);

    }

    public static Timestamp getParameterValueAsDate(HttpServletRequest request,
                                                    String base, boolean parseDay) {
        int yr = HttpHelper.getParameterValueAsInt(request, base + "Yr", 0);
        if (yr > 0) {
            int mth = HttpHelper.getParameterValueAsInt(request, base + "Mth", 0);
            int day = 0;
            if (parseDay) {
                day = HttpHelper.getParameterValueAsInt(request, base + "Day", 0);
            }
            GregorianCalendar cal = new GregorianCalendar(yr, mth - 1, day);// mth 0 based
            return new Timestamp(cal.getTime().getTime());
        }

        return null;
    }

    /**
     *
     * @param request
     * @param base
     */
    public static Timestamp getParameterValueAsTimestamp(HttpServletRequest request,
                                                         String base) {
        int yr = HttpHelper.getParameterValueAsInt(request, base + "Yr", 0);
        if (yr > 0) {
            int mth = HttpHelper.getParameterValueAsInt(request, base + "Mth", 0);
            int day = HttpHelper.getParameterValueAsInt(request, base + "Day", 0);
            int hr = HttpHelper.getParameterValueAsInt(request, base + "Hr", 0);
            int min = HttpHelper.getParameterValueAsInt(request, base + "Min", 0);
            GregorianCalendar cal = new GregorianCalendar(yr, mth - 1, day, hr, min);// mth 0 based
            return new Timestamp(cal.getTime().getTime());
        }

        return null;
    }

    /**
     */
    public static int getParameterValueAsInt(HttpServletRequest request,
                                             String param,
                                             int defaultValue) {
        String value = request.getParameter(param);
        if (value == null)
            return defaultValue;
        int rtn = defaultValue;
        try {
            rtn = Integer.parseInt(value);
        } catch (Exception ex) {
            // just return default
        }
        return rtn;
    }

    public static double getParameterValueAsDouble(HttpServletRequest request,
                                                   String param,
                                                   double defaultValue) {
        String value = request.getParameter(param);
        if (value == null)
            return defaultValue;
        if (value.startsWith("$"))
            value = value.substring(1);
        int p = value.indexOf(',');
        // remove commas from Euro input if needed.
        if (p > -1) {
            StringBuilder sb = new StringBuilder();
            StringTokenizer st = new StringTokenizer(value, ",", false);
            while (st.hasMoreTokens())
                sb.append(st.nextToken());
            value = sb.toString();
        }
        double rtn = defaultValue;
        try {
            rtn = Double.parseDouble(value);
        } catch (Exception ex) {
            // just return default
        }
        return rtn;
    }

    /**
     * returns defaultValue if param is missing or empty string
     *
     * @param request
     * @param param
     * @param defaultValue
     */
    public static String getParameterValueAsString(HttpServletRequest request,
                                                   String param,
                                                   String defaultValue) {
        String value = request.getParameter(param);
        if (value == null || value.trim().length() == 0)
            return defaultValue;

        return value;
    }

    public static boolean getParameterValueAsBoolean(HttpServletRequest request,
                                                     String param) {
        return getParameterValueAsBoolean(request, param, false);
    }

    public static boolean getParameterValueAsBoolean(HttpServletRequest request,
                                                     String param,
                                                     boolean defaultValue) {
        String value = request.getParameter(param);
        if (value == null)
            return defaultValue;

        return Boolean.valueOf(value).booleanValue();
    }

    public static String getRequestParameter(String key, HttpServletRequest request) {
        String returnValue = null;
        if (request != null && key != null) {
            returnValue = request.getParameter(key);
        }
        return returnValue;
    }

    public static Object getRequestAttribute(String key, HttpServletRequest request) {
        Object returnValue = null;
        if (request != null && key != null) {
            returnValue = request.getAttribute(key);
        }
        if (returnValue == null) {
            returnValue = getSessionAttribute(key, request);
        }
        return returnValue;
    }


    public static Object getSessionAttribute(String key, HttpServletRequest request) {
        Object returnValue = null;
        if (request != null && key != null) {
            returnValue = getSessionAttribute(key, request.getSession());
        }
        return returnValue;
    }

    public static Object getSessionAttribute(String key, HttpSession session) {
        Object returnValue = null;
        if (session != null && key != null) {
            returnValue = session.getAttribute(key);
        }
        return returnValue;
    }

    public static void putRequestAttribute(String key, Object parameterValue, HttpServletRequest request) {
        if (key != null && request != null) {
            request.setAttribute(key, parameterValue);
        }
    }

    public static void putSessionAttribute(String key, Object parameterValue, HttpServletRequest request) {
        if (key != null && request != null) {
            putSessionAttribute(key, parameterValue, request.getSession());
        }
    }

    public static void putSessionAttribute(String key, Object parameterValue, HttpSession session) {
        if (key != null && session != null) {
            session.setAttribute(key, parameterValue);
        }
    }

    /**
     * allows non-struts web widgets to save struts errors
     *
     * @param request
     * @param errors
     * @throws Exception
     */
    public static void saveStrutsErrors(HttpServletRequest request, ActionMessages errors) throws Exception {

        // Remove any error messages attribute if none are required
        if ((errors == null) || errors.isEmpty()) {
            request.removeAttribute(Globals.ERROR_KEY);
            return;
        }

        // Save the error messages we need
        request.setAttribute(Globals.ERROR_KEY, errors);


    }

    public static String getSiteProperty(String key) {
        String retVal = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("site");
            retVal = bundle.getString(key);
        } catch (MissingResourceException e) {
            log.warn("Missing key [" + key + "]", e);
        }
        return retVal;
    }

    public static String getBackUrl(HttpServletRequest request) {
        String startPage = request.getParameter(PARM_BACK_URL);
        if (startPage == null) {
            startPage = (String) request.getSession().getAttribute(PARM_BACK_URL);
        }
        if (startPage != null) {
            request.getSession().setAttribute(PARM_BACK_URL, startPage);
        }
        return startPage;
    }

    public static String getButtonAction(HttpServletRequest request) {
        String retAction = null;
        Map mParams = request.getParameterMap();
        if (mParams != null) {
            Set sKeys = mParams.keySet();
            if (sKeys != null) {
                Iterator iKeys = sKeys.iterator();
                while (iKeys != null && iKeys.hasNext()) {
                    String key = (String) iKeys.next();
                    // "action.[action_label].x" or "action.[action_label]
                    if (key.startsWith(PREFIX_ACTION)) {
                        int lastIndex = key.lastIndexOf(".");
                        lastIndex = (lastIndex == -1 || lastIndex == (PREFIX_ACTION.length() - 1)) ? key.length() - 1 : lastIndex;
                        retAction = key.substring(PREFIX_ACTION.length(), lastIndex);
                        break;
                    }
                }
            }
        }

        return retAction;
    }

    /**
     * used in conjunction with tokentag
     *
     * @param httpServletRequest
     */
    public static boolean isTokenValid(HttpServletRequest httpServletRequest) {
        //If not using tokens, this is ok but notify
        if (!"true".equals(httpServletRequest.getParameter("isUsingTokens"))) {
            LogHelper.getLogger(HttpHelper.class).info("Checking for a token that does not exist.");
            return true;
        }

        boolean isGood = false;
        Integer sToken = (Integer) httpServletRequest.getSession().getAttribute("token");
        Integer pToken = Integer.valueOf(httpServletRequest.getParameter("token"));

        if (pToken.equals(sToken)) {
            isGood = true;
            httpServletRequest.getSession().removeAttribute("token");
        }

        return isGood;
    }

    /**
     * used when you can't use the token tag for tokening
     * i.e multiple forms on a single page where the values would overrite each other
     *
     * @param httpServletRequest
     */
    public static String getToken(HttpServletRequest httpServletRequest) {
        Integer token = new Integer(new Random(new Date().getTime()).nextInt());
        httpServletRequest.getSession().setAttribute("token", token);
        return token.toString();
    }

    public static boolean isFileAttached(FormFile formFile) {
        return formFile != null
                && formFile.getFileName() != null
                && formFile.getFileName().trim().length() > 0;
    }

    public static void logRequest(HttpServletRequest request, Object logCategory) {
        // quick reject
        if (!LogHelper.getLogger(logCategory).isDebugEnabled()) return;

        StringBuilder sbMsg = new StringBuilder(100);
        appendLine(sbMsg, "");
        appendLine(sbMsg, "---------- Start Request Dump:");
        appendLine(sbMsg, "method [" + request.getMethod() + "]");
        appendLine(sbMsg, "servletPath [" + request.getServletPath() + "]");
        appendLine(sbMsg, "pathInfo [" + request.getPathInfo() + "]");
        appendLine(sbMsg, "queryString [" + request.getQueryString() + "]");

        Enumeration e = null;
        String name = null;

        appendLine(sbMsg, "-------------- Header Information");
        e = request.getHeaderNames();
        String header;
        while (e.hasMoreElements()) {
            header = (String) e.nextElement();
            appendLine(sbMsg, header + " [" + request.getHeader(header) + "]");
        }

        appendLine(sbMsg, "-------------- Parameter Information");

        e = request.getParameterNames();
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            String[] values = request.getParameterValues(name);
            String value = "";
            if (values.length > 1) {
                for (int inx = 0; inx < values.length; inx++) {
                    value = value + values[inx] + ",";
                }
            }
            else {
                value = values[0];
            }
            if (name.startsWith("password")) {
                value = "******";
            }
            appendLine(sbMsg, name + " [" + value + "]");
        }

        appendLine(sbMsg, "-------------- Attribute Information");
        e = request.getAttributeNames();
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            appendLine(sbMsg, name + " [" + request.getAttribute(name).toString() + "]");
        }

        appendLine(sbMsg, "---------- End Request Dump:");
        LogHelper.debug(logCategory, sbMsg);
    }

    public static void appendLine(StringBuilder sbMsg, String msg) {
        Strings.appendLine(sbMsg, "  " + msg);
    }

    public static Map getNameValuePairFromParameterName(String key, HttpServletRequest request) {
        Map nameValuePair = new HashMap();

        Enumeration parameterNames = request.getParameterNames();
        for (; parameterNames.hasMoreElements();) {
            String paraName = (String) parameterNames.nextElement();
            if (!paraName.startsWith(key)) {
                continue;
            }

            String name = paraName.substring((key.length()), paraName.length());
            String value = request.getParameter(paraName);

            nameValuePair.put(name, value);

        }
        return nameValuePair;
    }
    
	/**
	 *
	 */
	public static void setCookie(HttpServletResponse res, String cookieName,
								 String cookieValue, String path, int timeLength) {
		Cookie c = new Cookie(cookieName, cookieValue);
		c.setPath(path);
		c.setMaxAge(timeLength);
		res.addCookie(c);
	}

	/**
	 *
	 */
	public static void deleteCookie(HttpServletResponse res, String cookieName, String path) {
		setCookie(res, cookieName, null, path, 0);
	}

	/**
	 *
	 */
	public static String getValueFromCookie(HttpServletRequest req, String name) {
		String value = null;
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				// found cookieId
				if (cookies[i].getName().equals(name)) {
					value = cookies[i].getValue();
					break;
				}
			}
		}
		return value;
	}

	/**
	 *
	 */
	public static Enumeration getRequestHeaders(HttpServletRequest req) {
		return req.getHeaderNames();
	}


	/**
	 * doesn't work for IE 5.x as IE doesn't report the referer header
	 */
	public static String getReferer(HttpServletRequest req) {
		return req.getHeader("REFERER");
	}

	/**
	 *
	 */
	public static boolean isBlank(HttpServletRequest req, String name) {
		return isBlank(req.getParameter(name));
	}

	/**
	 *
	 */
	public static boolean isBlank(String value) {
		boolean tf = false;
		if (value == null || value.trim().equals("") || value.equals("null")) {
			tf = true;
		}
		return tf;
	}


	//*************************

	public static final String CHAR_SCHEME_UTF8 = "UTF-8";

	public static String urlEncode(HttpServletRequest request, String str) throws UnsupportedEncodingException {
		String retVal = null;
        retVal = URLEncoder.encode(str, request.getCharacterEncoding());
		return retVal;
	}

	//*************************

	public static String urlDecode(HttpServletRequest request, String str) throws UnsupportedEncodingException {
		String retVal = null;
		retVal = URLDecoder.decode(str, request.getCharacterEncoding());
		return retVal;
	}

	public static final char CHR_DQUOTE = '\"';
	public static final char CHR_AMP = '&';
	public static final char CHR_FORSLASH = '/';
	public static final char CHR_LT = '<';
	public static final char CHR_GT = '>';
	public static final String STR_NL = "\n";
	public static final String STR_DQUOTE = new String(new char[]{CHR_DQUOTE});
	public static final String STR_AMP = new String(new char[]{CHR_AMP});
	public static final String STR_FORSLASH = new String(new char[]{CHR_FORSLASH});
	public static final String STR_LT = new String(new char[]{CHR_LT});
	public static final String STR_GT = new String(new char[]{CHR_GT});
	public static final String CODE_DQUOTE = "&#034;";
	public static final String CODE_AMP = "&#038;";
	public static final String CODE_FORSLASH = "&#047;";
	public static final String CODE_LT = "&#060;";
	public static final String CODE_GT = "&#062;";
	public static final String CODE_NL = "<br>";

	private static final String[][] HTML_CODE_MAP = {{STR_DQUOTE, CODE_DQUOTE},
													 {STR_AMP, CODE_AMP},
													 {STR_FORSLASH, CODE_FORSLASH},
													 {STR_LT, CODE_LT},
													 {STR_GT, CODE_GT},
													 {STR_NL, CODE_NL}
	};
	private static CharReplacer _htmlCr = null;

	// leave of the newline --> <br> replacement since <textarea> knows how to interpret that
	private static CharReplacer _htmlCrNoBrTag = null;

	// Create a com.jkc.util.CharReplacer.
	static {
		_htmlCr = new CharReplacer(HTML_CODE_MAP);
		_htmlCrNoBrTag = new CharReplacer(new String[] []
								   {{STR_DQUOTE, CODE_DQUOTE},
									{STR_AMP, CODE_AMP},
									{STR_FORSLASH, CODE_FORSLASH},
									{STR_LT, CODE_LT},
									{STR_GT, CODE_GT}});
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

	public static String toHTMLStringNoBrTag(String in) {
		return _htmlCrNoBrTag.replace(in);
	}

    public static Date getRequestTime(HttpServletRequest request) {
        java.util.Date dtNow = (java.util.Date) request.getAttribute(KEY_NOW_TIME);
        if (dtNow == null) {
            dtNow = Dates.nowDate();
            request.setAttribute(KEY_NOW_TIME, dtNow);
        }
        return dtNow;
    }

    /**
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        return request.getSession().getAttribute(KEY_USER) != null;
    }

    public static Subject getLoggedInSubject(HttpServletRequest request) {
        return (Subject) request.getSession().getAttribute(KEY_USER);
    }

    public static Principal getLoggedInPrincipal(HttpServletRequest request) {
        Principal principal = null;
        Subject subject = getLoggedInSubject(request);
        if (subject != null) {
            try {
                principal = (Principal) subject.getPrincipals().toArray()[0];
            } catch (RuntimeException e) {
                LogHelper.getLogger(HttpHelper.class).error("Error getting login Principal", e);
            }
        }
        return principal;
    }

    public static String getLoggedInUserName(HttpServletRequest request) {
        String un = null;
        
        Principal p = getLoggedInPrincipal(request);
        if (p!=null) {
            un = p.getName();
        }
        
        return un;
    }

/* TODO: redo with JAAS api

    public static UserDO getLoggedInUser(HttpServletRequest request) throws Exception {
        return TsessUpsGateway.getInstance().getDb()
            .selectUserByUsername(getLoggedInUserName(request));
    }

    public static UserDirectoryEntry getLoggedInUserDirEntry(HttpServletRequest request){
        return (UserDirectoryEntry) request.getSession().getAttribute(KEY_USER_DIR_ENTRY);
    }
*/

    /**
     */
    public static void setLogin(HttpServletRequest request, Subject subject) {
        HttpSession session = request.getSession();
        session.setAttribute(KEY_USER, subject);

/* TODO: Use generic interface to get DAO directory object
        try {
            session.setAttribute(KEY_USER_DIR_ENTRY,
                TsessUpsGateway.getInstance().getDb()
                    .selectUserDirEntryByUsername(
                            getLoggedInUserName(request)));
        } catch (Exception e) {
            log.error("Accessing User Directory", e);
        }
*/
    }

    public static void clearLogin(HttpServletRequest request) {
        request.getSession().removeAttribute(KEY_USER);
    }

    /**
     * Does lazy init of permission map on request-scoped object to
     * avoid redundant db calls.
     * 
     * @param perm
     */


/*  TODO: move somewhere else... security object in session context...
    public static boolean hasPermission(HttpServletRequest request, Permission perm) throws ServletException {
        Boolean has = Boolean.FALSE;
        if (isLoggedIn(request)) {
            Map mapPerms = (Map) request.getAttribute(KEY_TSESS_PERMS);
            if (mapPerms == null) {
                mapPerms = new HashMap();
                request.setAttribute(KEY_TSESS_PERMS, mapPerms);
            }
            //
            has = (Boolean) mapPerms.get(perm);
            if (has == null) {
                String username = getLoggedInUserName(request);
                try {
                    has = new Boolean(Security.hasPermission(username, perm));
                } catch (Exception e) {
                    log.error(e);
                    throw new ServletException(e);
                }
                mapPerms.put(perm, has);
            }
        }
        return has.booleanValue();
    }
*/

}

