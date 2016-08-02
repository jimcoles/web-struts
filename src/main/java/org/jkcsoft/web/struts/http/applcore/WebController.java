/*
 * Copyright (c) Jim Coles (jameskcoles@gmail.com) 2016. through present.
 *
 * Licensed under the following license agreement:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Also see the LICENSE file in the repository root directory.
 */

package org.jkcsoft.web.struts.http.applcore;

import org.apache.struts.upload.MultipartRequestWrapper;
import org.apache.struts.action.*; // ActionServlet, ActionMapping, Action, ActionForward

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;

import java.util.*; // StringTokenizer, List, Vector
import java.io.IOException;

/**
 * The TRI extension of the basic Struts controller servlet.
 *
 * Overrides the init() method for one-time initialization of TRI app
 * properties.
 * <br><br>
 * Adds a new method <code>handle()</code> that can be called by JSPs on an as needed
 * basis.  NOTE: Should probably break out this class (WebController) into two
 * classes: CentralController and DetachedController.  Can also look at using
 * a Servlet 2.3 Filter as the central controller instead of current design in which
 * the central controller is really a servlet that gets called for all requests.
 * The filter would have a chance to touch the incoming request and response
 * prior to handing of to the JSP.
 *
 * @author  Jim Coles
 * @version 1.0
 */
public abstract class WebController extends ActionServlet
{
  //---------------------------------------------------------------------------
  // Static vars and methods
  //---------------------------------------------------------------------------
  private static WebController _sharedInstance = null;

  /** JSPs should use this method to get a 'detached' controller. */
  public static WebController getInstance()
    throws Exception
  {
    if (_sharedInstance == null ) {
      throw new Exception("TRI Configuration Error: WebController was not instantiated at web server statup.  Shared controller instance is null.");
    }
    if (!_sharedInstance.isInit()) {
      throw new Exception("TRI Configuration Error: WebController was not initialized properly at web server statup.  Ensure use of 'load-on-startup'.");
    }
    return _sharedInstance;
  }

  /**
   * Combines info from current uri with relative target path to build
   * an absolute path.
   */
  public static String computeUri(List thisPageUriStack, String targetPagePath)
  {
    String retUri = null;
    StringBuffer sb = new StringBuffer(50);
    List outUriStack = new Vector(thisPageUriStack);
    StringTokenizer st = new StringTokenizer(targetPagePath, "/", false);
    int numRelPathTokens = st.countTokens();
    int outUriPos = outUriStack.size();  // position at 'top' item; 1-based
    // move thru target path directories from left to right, adjusting
    // the output uri stack appropriately.
    for(int relUriPos = 1; relUriPos <= numRelPathTokens; relUriPos++) {
      String dir = st.nextToken();
      if ("..".equals(dir)) {
        outUriPos--;
        if (outUriPos < 0) throw new java.lang.ArrayIndexOutOfBoundsException("Relative path '"+targetPagePath+"' went beyond root level with respect to current path.");
        if (outUriPos >= 0 )
          outUriStack.remove(outUriPos);
      }
      else if (".".equals(dir)) {
        // do nada.
      }
      else {
        outUriPos++;
        outUriStack.add(dir);
      }
    }

    Iterator iOutUri = outUriStack.iterator();
    while(iOutUri.hasNext())
    {
      String dir = (String) iOutUri.next();
      sb.append("/");
      sb.append(dir);
    }
    retUri = sb.toString();
    return retUri;
  }

  public static List buildUriPathStack(String uri)
  {
    List retList = new Vector();
    StringTokenizer st =  new StringTokenizer(uri, "/", false);
    while (st.hasMoreTokens()) {
      String pathNode = st.nextToken();
      // Add to stack if its a 'directory' level and not a file ref.
      if (st.hasMoreTokens() || pathNode.indexOf(".") == 0) {
        retList.add(pathNode);
      }
    }
    return retList;
  }

  public static boolean isAbsoluteUrl(String path) {
    return (path.startsWith("http://") || path.startsWith("https://"));
  }

  /**
   * Returns a String with the URL up to and including the domain name not
   * including trailing '/'.
   */
  public static String getUrlThruDomain(HttpServletRequest request)
  {
    String retUrl = request.getScheme()
      + "://" + request.getServerName()
      + ((request.getServerPort() == 80)?(""):(":"+request.getServerPort()));
    return retUrl;
  }

  //---------------------------------------------------------------------------
  // Instance vars
  //---------------------------------------------------------------------------
  private String _controllerPackageName = null;
  private boolean _bIsRootWebApp = true;
  private boolean _bInit = false;
  private Boolean _isRootWebApp = null;
  private String _ourContextPath = null;  // usually same as _containerContextPath.
  private String _containerContextPath = null;

  //---------------------------------------------------------------------------
  // Constructor(s)
  //---------------------------------------------------------------------------
  public WebController()
  {
    super();
    _sharedInstance = this;
  }

  //---------------------------------------------------------------------------
  // Public methods
  //---------------------------------------------------------------------------

  /**
   * Overrides javax.servlet.Servlet.init().  Gets the WebApp from the
   * sub-class and initializes it with this Controller's servlet context.
   */
  public void init() throws javax.servlet.ServletException
  {
    super.init();
    log("WebController.init(): called");
    _bInit = true;
    WebApp webApp = getWebApp();
    _controllerPackageName = webApp.getControllerPackageName();
    try {
      webApp.init(this.getServletContext());
    }
    catch (Exception ex) {
      throw new ServletException(ex);
    }
  }

  /** Want sub-class to give us the specific WebApp to use. */
  public abstract WebApp getWebApp();

  public boolean isInit() { return _bInit; }

  /**
   * Similar to [struts].AcitonServlet.process( ), but allows JSP pages to delegate
   * control to the WebController at the JSP's option.  This method is called
   * directly by any .jsp page that wants to use a page controller.
   *
   * @return true if the calling page is the target of request and must therefore
   * set the response.  If false, calling JSP should simply return immediately
   * and let the servlet engine do the redirecting.
   */
  public boolean handle(String pageControllerClass, HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    boolean retGetThisPage = true;

    // validation
    if (_containerContextPath == null) {
      _containerContextPath = request.getContextPath();
      _setContextPath(request);
      log("INFO: Initializing WebController "+this.getClass().getName()+" for the context path '"+_containerContextPath+"'.");
    }
    else {
      if (_containerContextPath != request.getContextPath()) {
        log("WARNING: A single WebController (class="
           +this.getClass().getName()+") has been asked to handle a request for "
           +"more than one servlet context.  First context = '"
           +_containerContextPath+"'.  Second = '"+request.getContextPath()+"'.");
      }
    }

    ActionForward forward = null;
    ActionMapping mapping = new ActionMapping();

    String pageName = request.getRequestURI();
    String contentType = request.getContentType();
    String method = request.getMethod();

    //if this is a multipart request, wrap the HttpServletRequest object
    //with a MultipartRequestWrapper to keep the process sub-methods
    //from failing when checking for certain request parameters
    //for command tokens and cancel button detection
    if ((contentType != null) && (contentType.startsWith("multipart/form-data"))
        && (method.equals("POST")))
    {
      request = new MultipartRequestWrapper(request);
    }

    // Automatically select a locale for this user if requested
    processLocale(request);

    // Set the content type and no-caching headers if requested
    processContent(response);
    processNoCache(response);

    mapping.setType(_controllerPackageName + "." + pageControllerClass);
    // Acquire the Action instance to process this request
    Action actionInstance = processActionCreate(mapping, request);
    if (actionInstance == null) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                         internal.getMessage("actionCreate",
                                             mapping.getType()));
      return false;
    }

    // Call the Action instance itself
    forward = processActionPerform(actionInstance, mapping, null,
                             request, response);
    //set the request back to it's normal state if it's currently wrapped,
    //to avoid ClassCastExceptions from ServletContainers if forwarding
    if (request instanceof MultipartRequestWrapper) {
      request = ((MultipartRequestWrapper) request).getRequest();
    }

    if (forward != null) {
      // Process the returned ActionForward (if any)
      processActionForward(forward, mapping, null, request, response);
      retGetThisPage = false;
    }
    return retGetThisPage;
  }

  /**
   * Returns a String with the URL up to and including the web app path not
   * including the trailing '/'.
   */
  public String getUrlThruAppPath(HttpServletRequest request)
  {
    String retUrl = getUrlThruDomain(request) + _getOurContextPath();
    return retUrl;
  }

  //---------------------------------------------------------------------------
  // Protected methods
  //---------------------------------------------------------------------------

  /**
   * Overrides [struts].processActionForward( );
   * The only change is that it does not prepend the context path
   * to the target path of a redirect if this is a 'root' level
   * web application.
   */
  protected void processActionForward(ActionForward forward,
                                      ActionMapping mapping,
                                      ActionForm formInstance,
                                      HttpServletRequest request,
                                      HttpServletResponse response)
    throws IOException, ServletException
  {
    if (forward != null) {
      String path = forward.getPath();
      if (forward.getRedirect()) {

// Original lines from struts -->
//        if (path.startsWith("/"))
//            path = request.getContextPath() + path;
// End original lines from struts.

        // Massage url of targetted resource if dealing with 'root' web application.
        // This is because iPlanet adds an extra '/' after domain name if you have
        // a web app path identifier of '/', even if your path is page-relative.
        // Also, struts version of this method was pre-pending the context path
        // for all asbolute uri's (starting with '/').  That logic presumed the
        // resource was within the current web app context.  The logic below
        // does not do that, so PageController's must prepend
        // WebApp.getAppPath() to any absolute URI's within it's own web app.
        if (_bIsRootWebApp)
        {
          path = getUrlThruAppPath(request)
           + computeUri(buildUriPathStack(request.getRequestURI()), path);
        }
        response.sendRedirect(response.encodeRedirectURL(path));
      }
      else {
        RequestDispatcher rd =
        getServletContext().getRequestDispatcher(path);
        if (rd == null) {
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
             internal.getMessage("requestDispatcher", path));
          return;
        }
        rd.forward(request, response);
      }
    }
  }

  //---------------------------------------------------------------------------
  // Private methods
  //---------------------------------------------------------------------------

  private void _setContextPath(HttpServletRequest request)
  {
    _ourContextPath = _isRootWebApp(request)?"":request.getContextPath();
  }

  private String _getOurContextPath() { return _ourContextPath; }

  private boolean _isRootWebApp(HttpServletRequest request)
  {
    if (_isRootWebApp == null) {
      _isRootWebApp =
        new Boolean(request.getContextPath() == null || request.getContextPath().length() <= 1);
    }
    return _isRootWebApp.booleanValue();
  }


}