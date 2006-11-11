package org.seasar.kvasir.plust.form;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.plust.KvasirPlugin;

public class ExtensionPage extends KvasirFormPage
{

    private ExtensionPropertyBlock block;

    public ExtensionPage(FormEditor editor, PluginDescriptor descriptor) {
        super(editor, descriptor, "extension", Messages.getString("ExtensionPage.name"));  //$NON-NLS-2$
        block = new ExtensionPropertyBlock(this);
    }
    
    protected void createFormContent(final IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        form.setText(Messages.getString("ExtensionPage.title")); //$NON-NLS-1$
        // TODO Eclipse3.1では存在しないAPI呼び出し。
//        form.setImage(KvasirPlugin.getImageDescriptor(KvasirPlugin.IMG_LOGO).createImage());
        block.createContent(managedForm);
    }
}
