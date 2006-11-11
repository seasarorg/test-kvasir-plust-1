package org.seasar.kvasir.plust.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.plust.KvasirPlugin;

import net.skirnir.xom.IllegalSyntaxException;
import net.skirnir.xom.ValidationException;
import net.skirnir.xom.XMLDocument;
import net.skirnir.xom.XMLParser;
import net.skirnir.xom.XMLParserFactory;
import net.skirnir.xom.XOMapper;
import net.skirnir.xom.XOMapperFactory;
import net.skirnir.xom.annotation.impl.AnnotationBeanAccessorFactory;


public class PluginsFormEditor extends FormEditor
{

    public static final String ID = "org.seasar.kvasir.plust.editors.PluginsFormEditor"; //$NON-NLS-1$

    private PluginDescriptor descriptor;
    
    private IEditorCommandStack commandStack;
    
    protected void setInput(IEditorInput input)
    {
        super.setInput(input);
        // TODO this cast is not safe. Editor's inputs are not only File.
        FileEditorInput editorInput = (FileEditorInput)input;
        IFile file = editorInput.getFile();
        //load target file.
        try {
            descriptor = loadXML(file.getLocation().toFile());
        } catch (Exception e) {
            KvasirPlugin.getDefault().log(Messages.getString("PluginsFormEditor.warn"), e); //$NON-NLS-1$
            descriptor = new PluginDescriptor();
        }
    }
    
    private PluginDescriptor loadXML(File file) throws IllegalSyntaxException, FileNotFoundException, IOException, ValidationException {
        XMLParser parser = XMLParserFactory.newInstance();
        XMLDocument document = parser.parse(new FileReader(file));
        XOMapper mapper = XOMapperFactory.newInstance();
        mapper.setBeanAccessorFactory(new AnnotationBeanAccessorFactory());
        return (PluginDescriptor)mapper.toBean(document.getRootElement(), PluginDescriptor.class);
    }
    
    protected void addPages()
    {
        try {
            addPage(new GeneralPage(this, descriptor));
            addPage(new DependencyPage(this, descriptor));
            addPage(new ExtensionPage(this, descriptor));
            addPage(new ExtensionPointPage(this, descriptor));
            addPage(new SourcePage(this, descriptor));
        } catch (PartInitException e) {
            KvasirPlugin.getDefault().log(Messages.getString("PluginsFormEditor.error"), e); //$NON-NLS-1$
        }
        
    }


    public void doSave(IProgressMonitor monitor)
    {
//        FileEditorInput editorInput = (FileEditorInput)getEditorInput();
//        IFile file = editorInput.getFile();
//        XOMapper mapper = XOMapperFactory.newInstance();
//        StringWriter writer = new StringWriter();
//        mapper.toXML(descriptor, writer);
//        writer.flush();
//        String xml = writer.toString();
//        file.setContents(writer., true, true, monitor);
    }


    public void doSaveAs()
    {

    }


    public boolean isSaveAsAllowed()
    {
        return false;
    }

    @Override
    public Object getAdapter(Class adapter)
    {
        if (IEditorCommandStack.class.equals(adapter)) {
            return this.commandStack;
        }
        return super.getAdapter(adapter);
    }
}
