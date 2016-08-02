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

import org.apache.struts.action.*;
import org.jkcsoft.java.util.LogHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class provide a consistent mechanism for 'last ditch' exception handling.
 * Removes that responsibility from sub-classes thereby reducing complexity of sub-class code while
 * providing better consitency of error handling.
 *
 * @author J. Coles
 */
public abstract class FlexAction extends Action {

    public FlexAction() {
    }

    /**
     * Print out info about this request
     */
    public void printRequest(HttpServletRequest request) {
        HttpHelper.logRequest(request, this);
    }

    /**
     * Impl of struts method.  Delegates to MDP-specific abstract method doAction( ).
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        LogHelper.debug(this, "enter execute()");
        printRequest(request);
        ActionForward rtn = null;
        ActionMessages errors = new ActionMessages();
        try {
            rtn = doAction(mapping, form, request, response, errors);
        } catch (Throwable e) {
            // last-ditch exception handler:
            ///   route to global 'error' page with ActionMessages
            LogHelper.error(this, "Last-chance error handler caught exception ==>", e);
            errors.add("ActionMessage", new ActionMessage("system.error", e.getMessage()));
            rtn = mapping.findForward("error");
        }
        if (rtn == null) {
            LogHelper.warn(this, "The ActionForward returned from doAction() is null.");
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        LogHelper.debug(this, "exit execute()");
        return rtn;
    }

    /**
     * Abstract method impl'd by sub-classes.
     *
     * @param errors Error list to which sub-class can append ActionMessages.
     * @return
     * @throws Exception
     */
    public abstract ActionForward doAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response, ActionMessages errors) throws Exception;

    /**
     * Minor convenience method
     */
    public final void addError(ActionMessages aes, ActionMessage ae) {
        if (aes != null) aes.add("ActionMessage", ae);
    }

}
