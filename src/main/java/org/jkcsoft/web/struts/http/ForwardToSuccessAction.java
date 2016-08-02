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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * Just forwards to the <forward> entry named 'success'.
 * Use to achieve .do alias to avoid .jsp in browser url.
 * 
 * @version 1.0
 * @author J. Coles
 */
public class ForwardToSuccessAction extends AbstractHttpAction {

    public ActionForward doAction(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response,
            ActionMessages errors) throws Exception {

        ActionForward forward = mapping.findForward("success");

        return (forward);
    }
}
