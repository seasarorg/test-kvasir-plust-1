package org.seasar.kvasir.plust.form;

import java.io.IOException;
import java.io.StringWriter;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;

import net.skirnir.xom.XOMapper;
import net.skirnir.xom.XOMapperFactory;
import net.skirnir.xom.annotation.impl.AnnotationBeanAccessorFactory;


public class SourcePage extends FormPage
{

    private PluginDescriptor descriptor;

    private SourceViewer viewer;

    private Document document;

    private VerticalRuler ruler;


    public SourcePage(FormEditor editor, PluginDescriptor descriptor)
    {
        super(editor, "source", Messages.getString("SourcePage.name")); //$NON-NLS-1$ //$NON-NLS-2$
        this.descriptor = descriptor;
    }


    protected void createFormContent(IManagedForm managedForm)
    {
        super.createFormContent(managedForm);
        managedForm.getForm().getBody().setLayout(new FillLayout());
        ruler = new VerticalRuler(10);
        viewer = new SourceViewer(managedForm.getForm().getBody(), ruler,
            SWT.V_SCROLL | SWT.H_SCROLL);
        document = new Document();
        viewer.setDocument(document);
        refresh();
    }


    public void refresh()
    {
        XOMapper mapper = XOMapperFactory.newInstance();
        mapper.setBeanAccessorFactory(new AnnotationBeanAccessorFactory());
        StringWriter writer = new StringWriter();

        try {
            mapper.toXML(descriptor, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.set(writer.toString());
        ruler.update();
    }
}
