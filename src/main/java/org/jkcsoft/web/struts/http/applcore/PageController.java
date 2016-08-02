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

import com.jkc.util.Strings;
import com.jkc.cmds.*; // Response, etc.

import org.apache.struts.action.*; // Action, ActionForward, etc

import javax.servlet.*; // ServletException, RequestDispatcher
import javax.servlet.http.*; // HttpServletRequest, HttpSession, HttpServletResponse

import java.io.IOException;

/**
 * Base page controller
 * @author Jim Coles
 * @version 1.0
 */
public abstract class PageController extends org.apache.struts.action.Action
{
  //---------------------------------------------------------------------------
  // Instance vars
  //---------------------------------------------------------------------------
  private String _noCookiesPage = null;

  //---------------------------------------------------------------------------
  // Constructor
  //---------------------------------------------------------------------------
  public PageController()
  {
    super();
  }

  //---------------------------------------------------------------------------
  // Public instance methods
  //---------------------------------------------------------------------------
  public ActionForward getThisPage(ActionMapping mapping)
  {
    return mapping.findForward("parent");
  }

  private boolean cookiesEnabledOnClient(HttpServletRequest request)
  {
    // JKC 12/20/01 Not sure how to verify whether client browser is set
    //              to accept cookies.  For now, assume they can.

//  The following line does not work when current page is the entry page for a
//  visitor:
//
//      !request.isRequestedSessionIdFromCookie()
//
    return true;
  }

  /**
   * Overrides [struts].Action.perform() to enforce TRI standards of
   * one PageController per page and use of the 'action' request parameter.
   */
  public ActionForward perform(ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest request,
				 HttpServletResponse response)
    throws IOException, ServletException
  {
    ActionForward retForward = null;

    // Validation: ensure requester accepts cookies
    if (requiresSession() & !cookiesEnabledOnClient(request)) {
      retForward = new ActionForward(_getNoCookiesPage());
    }
    else {
      try {
        String action = Strings.toStringDef(request.getParameter("action"), "get").toLowerCase();
        if ("get".equals(action)) {
          String uri = request.getRequestURI();
          if (uri.endsWith(".jsp") || uri.endsWith(".JSP")) {
          // handle case where URI directly references a target jsp...
            retForward = null;
          }
          else {
          // handle the .do mode...
            retForward = getThisPage(mapping);
          }
          handleGet(request, response);
        }
        else {
          retForward = handle(mapping, request, response, action);
        }
      }
      catch (Throwable ex) {
        if (ex instanceof IOException) { throw (IOException) ex; }
        if (ex instanceof ServletException) { throw (ServletException) ex; }
        throw new ServletException(ex);
      }
    }
    return retForward;
  }

  /** Handle the 'opening' of the associated page. */
  public abstract void handleGet(HttpServletRequest request,
                                 HttpServletResponse response)
    throws Exception;

  /** Handle general request from the parent page. */
  public ActionForward handle(ActionMapping mapping,
				 HttpServletRequest request,
				 HttpServletResponse response,
         String action)
    throws Exception
  {
    throw new ServletException(unhandledActionMsg(action));
  }

  public boolean errors(Response resp, HttpSession session)
  {
    if (resp.hasErrors()) {
      session.setAttribute("errors", resp.getErrors());
    }
    return resp.hasErrors();
  }

  public String unhandledActionMsg(String action)
  {
    return "Don't know how to handle action '"+action+"'.";
  }

  public WebController getWebController()
  {
    // assertion here is that this PageController was instantiated by
    // a WebController
//    if (!(getServlet() instanceof WebController)) {
//      throw ??
//    }
    return (WebController) getServlet();
  }

  /** Sub-class should override if it does not required session. */
  public boolean requiresSession() { return true; }

  //---------------------------------------------------------------------------
  // Private instance methods
  //---------------------------------------------------------------------------

  private String _getNoCookiesPage()
  {
    if (_noCookiesPage == null) {
      _noCookiesPage = getWebController().getWebApp().getAppProps().getProperty("www-no-cookies-page");
      if (_noCookiesPage == null) {
        _noCookiesPage = "/common/no_cookies.jsp";
        getWebController().log("Warning: the config parameter 'www-no-cookies-page' was not set.  Using default value '"+_noCookiesPage+"'.");
      }
    }
    return _noCookiesPage;
  }
}