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

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;


/**
 * Holds common helper methods for form validation and such. 
 */
public abstract class BaseActionForm extends ActionForm
{
    private String submitAction;

    public String getSubmitAction()
    {
        return submitAction;
    }

    public void setSubmitAction(String submitaction)
    {
        this.submitAction = submitaction;
    }

    protected boolean requiredText(String val, ActionMessages aes, String msgKey)
    {
        if (val == null || val.trim().length() == 0) {
            aes.add("formError",new ActionMessage(msgKey));
            return false;
        }
        return true;
    }

    protected boolean requiredText(String val, ActionMessages aes, String msgKey, String arg)
    {
        if (val == null || val.trim().length() == 0) {
            aes.add("formError",new ActionMessage(msgKey, arg));
            return false;
        }
        return true;
    }

    protected boolean requiredSelection(int val, ActionMessages aes, String msgKey)
    {
        if (val == 0 ) {
            aes.add("formError",new ActionMessage(msgKey));
            return false;
        }
        return true;
    }

    protected boolean requiredSelection(int val, ActionMessages aes, String msgKey, String arg)
    {
        if (val == 0 ) {
            aes.add("formError",new ActionMessage(msgKey, arg));
            return false;
        }
        return true;
    }

    protected boolean requiredSelection(Long val, ActionMessages aes, String msgKey, String arg)
    {
        if (val == null || val.longValue() == 0 ) {
            aes.add("formError",new ActionMessage(msgKey, arg));
            return false;
        }
        return true;
    }

    protected boolean requiredSelection(Integer val, ActionMessages aes, String msgKey, String arg)
    {
        if (val == null || val.intValue() == 0 ) {
            aes.add("formError",new ActionMessage(msgKey, arg));
            return false;
        }
        return true;
    }
    
    protected boolean requiredFile(FormFile file, ActionMessages aes, String msgKey, String arg) {
        if (!HttpHelper.isFileAttached(file)) {
            aes.add("formError", new ActionMessage(msgKey, arg));
            return false;
        }
        return true;
    }

    protected boolean requiredSelection(int[] vals, ActionMessages aes, String msgKey)
    {
        if (vals == null || vals.length == 0 ) {
            aes.add("formError",new ActionMessage(msgKey));
            return false;
        }
        return true;
    }

    protected boolean requiredSelection(int[] vals, ActionMessages aes, String msgKey, Object arg)
    {
        if (vals == null || vals.length == 0 ) {
            aes.add("formError",new ActionMessage(msgKey, new Object[] {arg}));
            return false;
        }
        return true;
    }

    public void chkLen(String val, int len, ActionMessages errors, String fieldName) {
        if ((val != null) && (val.length() > len))
            errors.add("formError", new ActionMessage("field.max", fieldName, "" + len));
    }
    /** Minor convenience method */
    public final void addError(ActionMessages aes, ActionMessage ae) {
        if (aes != null) aes.add("formError", ae);
    }

}
