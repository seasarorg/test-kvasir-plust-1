
package org.seasar.kvasir.plust.form;

import java.io.IOException;
import java.io.StringWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;

import net.skirnir.xom.XOMapper;
import net.skirnir.xom.XOMapperFactory;
import net.skirnir.xom.annotation.impl.AnnotationBeanAccessorFactory;

public class SourcePage extends FormPage
{

    private PluginDescriptor descriptor;
    private StyledText text;
    
    public SourcePage(FormEditor editor, PluginDescriptor descriptor) {
        super(editor, "source", Messages.getString("SourcePage.name")); //$NON-NLS-1$ //$NON-NLS-2$
        this.descriptor = descriptor;
    }
    
    @Override
    public void createPartControl(Composite parent)
    {
        super.createPartControl(parent);
        parent.setLayout(new FillLayout());
        text = new StyledText(parent, SWT.NONE);
        refresh();
    }


    public void refresh() {
        XOMapper mapper = XOMapperFactory.newInstance();
        mapper.setBeanAccessorFactory(new AnnotationBeanAccessorFactory());
        StringWriter writer = new StringWriter();
        try {
            mapper.toXML(descriptor, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        text.setText(writer.toString());
    }
}
