package org.seasar.kvasir.plust.form;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.seasar.kvasir.plust.model.PluginModel;


public class ExtensionPage extends KvasirFormPage
{

    private ExtensionBlock block;


    public ExtensionPage(FormEditor editor, PluginModel root)
    {
        super(editor, root,
            "extension", Messages.getString("ExtensionPage.name")); //$NON-NLS-2$
        block = new ExtensionBlock(this);
    }


    protected void createFormContent(final IManagedForm managedForm)
    {
        final ScrolledForm form = managedForm.getForm();
        managedForm.setInput(getEditorInput());
        form.setText(Messages.getString("ExtensionPage.title")); //$NON-NLS-1$
        // TODO Eclipse3.1では存在しないAPI呼び出し。
        //        form.setImage(KvasirPlugin.getImageDescriptor(KvasirPlugin.IMG_LOGO).createImage());
        block.createContent(managedForm);
    }
}
