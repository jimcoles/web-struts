/*
 * Copyright (c) Jim Coles (jameskcoles@gmail.com) 2016. through present.
 *
 * Licensed under the following license agreement:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Also see the LICENSE file in the repository root directory.
 */

package org.jkcsoft.web.struts.http.controllers;

//import com.metrowerks.mdp2.beans.Users;

import org.apache.commons.logging.Log;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.jkcsoft.java.util.CharReplacer;
import org.jkcsoft.java.util.LogHelper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

public class HttpHelper {
    public static final Log log = LogHelper.getLogger(HttpHelper.class);

    private HttpServletRequest request;
    private HashMap localRequest;
    private String parameterPrefix = "";
    private boolean debug = log.isDebugEnabled();
    public static final String PARM_BACK_URL = "startPage";
    public static final String PREFIX_ACTION = "action.";

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpHelper(HttpServletRequest request) {
        this.request = request;
        localRequest = new HashMap();
    }

    public void setParameter(String name, String value) {
        String key = this.parameterPrefix + name;
        localRequest.put(key, value);
    }

    public HttpHelper(HttpServletRequest request, String parameterPrefix) {
        this(request);
        setParameterPrefix(parameterPrefix);
    }

    public void setParameterPrefix(String parameterPrefix) {
        this.parameterPrefix = parameterPrefix;
    }

    public String getParameterPrefix() {
        return this.parameterPrefix;
    }

    public Object getAttribute(String attributeName, Object defaultValue) {
        Object value = request.getAttribute(attributeName);
        if (value == null)
            return defaultValue;
        else
            return value;
    }

    public Object[] getArrayAttribute(String attributeName, Object[] defaultValue) {
        Object value = request.getAttribute(attributeName);
        Object[] elements;

        if (value == null)
            elements = defaultValue;
        else {
            if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                elements = new Object[length];
                for (int i = 0; i < length; i++) {
                    elements[i] = Array.get(value, i);
                }
                return elements;
            } else
                elements = defaultValue;
        }

        return elements;
    }

    public String getStringAttribute(String attributeName,
                                     String defaultValue) {
        Object value = request.getAttribute(attributeName);
        if (value == null)
            return defaultValue;
        else
            return value.toString();
    }

    public int getIntAttribute(String attributeName,
                               int defaultValue) {
        try {
            Object value = request.getAttribute(attributeName);
            if (value == null)
                return defaultValue;
            else if (value instanceof Integer)
                return ((Integer) value).intValue();
            else
                return Integer.parseInt(value.toString());
        } catch (NumberFormatException nfe) {
            log.error(nfe);
            return defaultValue;
        }
    }

    public long getLongAttribute(String attributeName,
                                 long defaultValue) {
        try {
            Object value = request.getAttribute(attributeName);
            if (value == null)
                return defaultValue;
            else if (value instanceof Long)
                return ((Long) value).longValue();
            else
                return Long.parseLong(value.toString());
        } catch (NumberFormatException nfe) {
            log.error(nfe);
            return defaultValue;
        }
    }

    public boolean getBooleanAttribute(String key, boolean defaultValue) {
        if (request.getAttribute(key) == null)
            return defaultValue;
        else
            return new Boolean(request.getAttribute(key).toString()).booleanValue();
    }

    public String getParameter(String parameterName) {
        return getStringParameter(parameterName, "");
    }


    public String getStringParameter(String parameterName,
                                     String defaultValue) {
        Object value = implGetParameter(parameterName);
        if (value == null)
            return defaultValue;
        else
            return value.toString().trim();
    }

    public void setAttribute(String name, Object value) {
        request.setAttribute(name, value);
    }

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
     *
     * @param request
     * @param base
     * @return null if year is 0 otherwise vaild Timestamp object
     */
    public static Timestamp getParameterValueAsDate(HttpServletRequest request,
                                                    String base) {

        return getParameterValueAsDate(request, base, true);

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

    public int getIntParameter(String parameterName,
                               int defaultValue) {
        try {
            Object value = implGetParameter(parameterName);
            if (value == null)
                return defaultValue;
            else if (value.toString().length() == 0)
                return defaultValue;
            else if (value instanceof Integer)
                return ((Integer) value).intValue();
            else
                return Integer.parseInt(value.toString());
        } catch (NumberFormatException nfe) {
            log.error(nfe);
            return defaultValue;
        }
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

    public boolean getBooleanParameter(String key, boolean defaultValue) {
        if (implGetParameter(key) == null)
            return defaultValue;
        else
            return (implGetParameter(key).equalsIgnoreCase("true") ||
                    implGetParameter(key).equals("1"));
    }


    public long getLongParameter(String parameterName,
                                 long defaultValue) {
        try {
            Object value = implGetParameter(parameterName);
            if (value == null)
                return defaultValue;
            else if (value.toString().length() == 0)
                return defaultValue;
            else if (value instanceof Long)
                return ((Long) value).longValue();
            else
                return Long.parseLong(value.toString());
        } catch (NumberFormatException nfe) {
            log.error(nfe);
            return defaultValue;
        }
    }


    public long getParameter(String parameterName,
                             long defaultValue) {
        return getLongParameter(parameterName, defaultValue);
    }

    public int getParameter(String parameterName,
                            int defaultValue) {
        return getIntParameter(parameterName, defaultValue);
    }

    public boolean getParameter(String parameterName,
                                boolean defaultValue) {
        return getBooleanParameter(parameterName, defaultValue);
    }

    public String getParameter(String parameterName, String defaultValue) {
        return getStringParameter(parameterName, defaultValue);
    }

    public Enumeration getParameterNames() {
        return request.getParameterNames();
    }

    public String[] getParameterValues(String parameterName) {
        return implGetParameterValues(parameterName);
    }

    public int[] getParameter(String parameterName, int[] def) {
        return getParameterValues(parameterName, def);
    }

    public String[] getParameter(String parameterName, String[] def) {
        return getParameterValues(parameterName, def);
    }

    public long[] getParameter(String parameterName, long[] def) {
        return getParameterValues(parameterName, def);
    }

    /**
     * Returns the default if null
     */
    public String[] getParameterValues(String parameterName, String[] def) {
        String[] p = implGetParameterValues(parameterName);
        if (p == null)
            p = def;

        return p;
    }

    /**
     * Returns the default if null
     */
    public int[] getParameterValues(String parameterName, int[] def) {
        String[] pstr = implGetParameterValues(parameterName);
        int[] ret;
        if (pstr == null)
            ret = def;
        else {
            ret = new int[pstr.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = Integer.parseInt(pstr[i]);
            }
        }

        return ret;
    }


    /**
     * Returns the default if null
     */
    public long[] getParameterValues(String parameterName, long[] def) {
        String[] pstr = implGetParameterValues(parameterName);
        long[] ret;
        if (pstr == null)
            ret = def;
        else {
            ret = new long[pstr.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = Long.parseLong(pstr[i]);
            }
        }
        return ret;
    }

    private String[] implGetParameterValues(String parameterName) {
        String[] value = request.getParameterValues(this.parameterPrefix + parameterName);
        if (debug)
            log.debug("Request.getParameter('" + parameterName + "'): " + request.getParameter(this.parameterPrefix + parameterName));

        return value;
    }

    private String implGetParameter(String parameterName) {
        String key = this.parameterPrefix + parameterName;
        String value;
        if (localRequest.get(key) != null)
            value = localRequest.get(key).toString();
        else
            value = request.getParameter(key);

        if (debug)
            log.debug("Request.getParameter('" + parameterName + "'): " + value);

        return value;
    }

    public void addErrorAlert(String message) {
        getErrorAlerts().add(message);
    }

    public Vector getErrorAlerts() {
        Vector errorAlerts = (Vector) this.getAttribute("errorAlerts", new Vector());
        this.setAttribute("errorAlerts", errorAlerts);
        return errorAlerts;
    }

    public void setFormError(String inputName, String message) {
        getFormErrors().put(inputName, message);
    }

    public String getFormError(String inputName) {
        Hashtable formErrors = getFormErrors();
        if (formErrors.get(inputName) == null)
            return "";

        return (String) formErrors.get(inputName);
    }

    public Hashtable getFormErrors() {
        Hashtable formErrors = (Hashtable) this.getAttribute("formErrors", new Hashtable());
        this.setAttribute("formErrors", formErrors);
        return formErrors;
    }

    /**
     * If the field is empty then the setFormError will be set
     */
    public boolean assertRequired(String inputName) {
        if (request.getParameter(inputName).trim().length() == 0) {
            setFormError(inputName, "Required");
            return false;
        } else
            return true;
    }

    /**
     * If the field is not a valid integer then the setFormError will be set
     */
    public boolean assertValidInt(String inputName) {
        try {
            Integer.parseInt(this.getParameter(inputName));
            return true;
        } catch (NumberFormatException nfe) {
            setFormError(inputName, "Invalid Number: " + this.getParameter(inputName));
            return false;
        }
    }

    /**
     * If the field is not a valid integer then the setFormError will be set
     */
    public boolean assertValidLong(String inputName) {
        try {
            Long.parseLong(this.getParameter(inputName));
            return true;
        } catch (NumberFormatException nfe) {
            setFormError(inputName, "Invalid Number: " + this.getParameter(inputName));
            return false;
        }
    }

    /**
     * If the values for this field are not a valid integer
     *
     * @return boolean true in all values are integers
     */
    public boolean assertValidIntArray(String inputName) {
        try {
            String[] values = request.getParameterValues(inputName);
            for (int i = values.length; --i >= 0; ) {
                Integer.parseInt(values[i]);
            }
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * If errors where found in the validation process
     * this method will return a boolean result.
     */
    public boolean containsError() {
        if (this.getFormErrors().size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns the "action" of the request
     * The action is the extra path info associated with the URL not including
     * the query string and minus the leading "/".
     *
     * @return the action from the request's URL
     */

    public String getAction() {
        String pathInfo = request.getServletPath();
        log.info("pathInfo = " + pathInfo);

        return "/" + pathInfo.substring(pathInfo.lastIndexOf('/') + 1);
    }

    /**
     * Print out info about this request
     *
     * @deprecated Use logRequest(request, logCat)
     */
    public void printRequest() {
        String action = request.getParameter("action");
        log.info("Query String = " + request.getQueryString());
        log.info("Path Info " + request.getPathInfo());
        log.info("getServletPath " + request.getServletPath());
        log.info(".action = " + action);

        log.info("-------------- Parameter Information------------------");

        Enumeration e = request.getParameterNames();
        String name;
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            String[] values = request.getParameterValues(name);
            String value = "";
            for (int inx = 0; inx < values.length; inx++) {
                value = value + values[inx] + ",";
            }
            log.info("service() - name = " + name + " value(s) = " + value);
        }

        log.info("-------------- Attribute Information------------------");

        e = request.getAttributeNames();

        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            log.info("service() - name = " + name + " value = " + request.getAttribute(name).toString());
        }

        log.info("-------------- Header Information------------------");
        e = request.getHeaderNames();
        String header;
        while (e.hasMoreElements()) {
            header = (String) e.nextElement();
            log.info("service() - header = " + header + " value = " + request.getHeader(header));
        }
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


    /**
     * helper to see if someone is logged in
     *
     * @return
     */

    // TODO: Add 'User' methods back in when we define a 'user' object for authenticaiton

//    public static boolean isLoggedIn(HttpServletRequest request) throws Exception {
//
//        if (getUserFromSession(request) != null) {
//            return true;
//        } else {
//            return false;
//        }
//
//    }

//    public static Users getUserFromSession(HttpServletRequest request) {
//        return (Users) request.getSession().getAttribute(RequestAttributeNames.USER_ATTRIBUTE);
//    }

//    public static void setLoginInfoInSession(HttpServletRequest request, Users user) throws Exception {
//        int opcoId = UserManager.getOpCoForUserId(user.getUser_id(), RequestHelper.getSubProgramId(request));
//        request.getSession(true).setAttribute(RequestAttributeNames.USER_ATTRIBUTE, user);
//        setOpCoInSession(request, opcoId);
//    }
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
        appendLine(sbMsg, "Query String = " + request.getQueryString());
        appendLine(sbMsg, "Path Info " + request.getPathInfo());
        appendLine(sbMsg, "getServletPath " + request.getServletPath());

        Enumeration e;
        String name;

        appendLine(sbMsg, "-------------- Header Information");
        e = request.getAttributeNames();

        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            appendLine(sbMsg, name + "=" + request.getAttribute(name).toString());
        }
        e = request.getHeaderNames();
        String header;
        while (e.hasMoreElements()) {
            header = (String) e.nextElement();
            appendLine(sbMsg, header + "=" + request.getHeader(header));
        }

        appendLine(sbMsg, "-------------- Parameter Information");

        e = request.getParameterNames();
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            String[] values = request.getParameterValues(name);
            String value = "";
            for (int inx = 0; inx < values.length; inx++) {
                value = value + values[inx] + ",";
            }
            appendLine(sbMsg, name + "=" + value);
        }

        appendLine(sbMsg, "-------------- Attribute Information");


        appendLine(sbMsg, "---------- End Request Dump:");
        LogHelper.debug(logCategory, sbMsg);
    }

    public static void appendLine(StringBuilder sbMsg, String msg) {
        sbMsg.append("  ").append(msg).append("\n");
    }

    public static Map getNameValuePairFromParameterName(String key, HttpServletRequest request) {
        Map nameValuePair = new HashMap();

        Enumeration parameterNames = request.getParameterNames();
        for (; parameterNames.hasMoreElements(); ) {
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
    private static CharReplacer _htmlCrForTextArea = null;

    // Create a com.jkc.util.CharReplacer.
    static {
        _htmlCr = new CharReplacer(HTML_CODE_MAP);
        _htmlCrForTextArea = new CharReplacer(new String[][]
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

    public static String toHTMLStringForTextArea(String in) {
        return _htmlCrForTextArea.replace(in);
    }


}

