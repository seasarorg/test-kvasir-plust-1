package org.seasar.kvasir.plust.form;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.seasar.kvasir.plust.model.PluginModel;
import org.seasar.kvasir.plust.model.PlustMapper;


public class SourcePage extends KvasirFormPage
{


    private SourceViewer viewer;

    private Document document;

    private VerticalRuler ruler;


    public SourcePage(FormEditor editor, PluginModel root)
    {
        super(editor, root, "source", Messages.getString("SourcePage.name")); //$NON-NLS-1$ //$NON-NLS-2$
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
        document.set(PlustMapper.toPluginXML(getDescriptor()));
        ruler.update();
    }
}
