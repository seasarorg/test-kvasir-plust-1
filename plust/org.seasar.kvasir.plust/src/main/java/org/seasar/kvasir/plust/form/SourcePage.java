
package org.seasar.kvasir.plust.form;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;

public class SourcePage extends FormPage
{

    public SourcePage(FormEditor editor, PluginDescriptor descriptor) {
        super(editor, "source", Messages.getString("SourcePage.name")); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
