package org.beanmaker.v2.runtime;

import org.jcodegen.html.DivTag;

import java.util.ResourceBundle;

public interface HtmlDefaultButtonsHelper {

    DivTag getDefaultDialogButtons(
            HtmlFormHelper htmlFormHelper,
            long id,
            String name,
            ResourceBundle resourceBundle);

    DivTag getDefaultDialogButtons(
            HtmlFormHelper htmlFormHelper,
            long id,
            String name,
            ResourceBundle resourceBundle,
            boolean submitDisabled);

    DivTag getLonelyCloseButton(ResourceBundle resourceBundle);

}
