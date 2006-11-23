package org.seasar.kvasir.plust.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.plust.IKvasirProject;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.editors.XMLEditor;
import org.seasar.kvasir.plust.form.command.EditorCommandStack;
import org.seasar.kvasir.plust.form.command.IEditorCommandStack;
import org.seasar.kvasir.plust.form.command.IEditorCommandStackListener;
import org.seasar.kvasir.plust.model.PluginModel;
import org.seasar.kvasir.plust.model.PlustMapper;

import net.skirnir.xom.IllegalSyntaxException;
import net.skirnir.xom.ValidationException;
import net.skirnir.xom.XMLDocument;
import net.skirnir.xom.XMLParser;
import net.skirnir.xom.XMLParserFactory;
import net.skirnir.xom.XOMapper;
import net.skirnir.xom.XOMapperFactory;
import net.skirnir.xom.annotation.impl.AnnotationBeanAccessorFactory;


public class PluginsFormEditor extends FormEditor
    implements IEditorCommandStackListener
{

    public static final String ID = "org.seasar.kvasir.plust.editors.PluginsFormEditor"; //$NON-NLS-1$

    private PluginModel descriptor;

    private IEditorCommandStack commandStack = new EditorCommandStack();

    private FileEditorInput pomFileInput;

    private FileEditorInput buildPropInput;


    protected void setInput(IEditorInput input)
    {
        super.setInput(input);

        commandStack.addCommandStackListener(this);

        // TODO this cast is not safe. Editor's inputs are not only File.
        FileEditorInput editorInput = (FileEditorInput)input;
        IFile file = editorInput.getFile();

        //load target file.
        try {
            //pom
            IFile pomFile = file.getProject().getFile(
                IKvasirProject.POM_FILE_NAME);
            pomFileInput = new FileEditorInput(pomFile);

            //            if (!pomFile.exists()) {
            //                throw new Exception("POMファイル見つからない");
            //            }
            //            MavenProject project = KvasirPlugin.getDefault().getMavenProject(pomFile, new NullProgressMonitor());
            MavenProject project = null;

            IFile buildProperties = file.getProject().getFile(
                "build.properties");
            buildPropInput = new FileEditorInput(buildProperties);

            //build properties
            Properties properties = KvasirPlugin.getDefault()
                .loadBuildProperties(file.getProject());

            descriptor = loadXML(file.getLocation().toFile(), project,
                properties);
        } catch (Exception e) {
            KvasirPlugin.getDefault().log(
                Messages.getString("PluginsFormEditor.warn"), e); //$NON-NLS-1$
            descriptor = new PluginModel();
        }

        setPartName(file.getProject().getName() + " : +PLUSTプラグインエディタ");
    }


    private PluginModel loadXML(File file, MavenProject project,
        Properties properties)
        throws IllegalSyntaxException, FileNotFoundException, IOException,
        ValidationException
    {
        XMLParser parser = XMLParserFactory.newInstance();
        XMLDocument document = parser.parse(new FileReader(file));
        XOMapper mapper = XOMapperFactory.newInstance();
        mapper.setBeanAccessorFactory(new AnnotationBeanAccessorFactory());
        PluginDescriptor descriptor = (PluginDescriptor)mapper.toBean(document
            .getRootElement(), PluginDescriptor.class);
        return PlustMapper.toPlustModel(descriptor, project, properties);
    }


    protected void addPages()
    {
        try {
            addPage(new GeneralPage(this, descriptor));
            addPage(new DependencyPage(this, descriptor));
            addPage(new ExtensionPage(this, descriptor));
            addPage(new ExtensionPointPage(this, descriptor));
            int index = addPage(new XMLEditor(), getEditorInput());
            setPageText(index, "plugin.xml");

            index = addPage(new TextEditor(), buildPropInput);
            setPageText(index, "build.properties");
            index = addPage(new XMLEditor(), pomFileInput);
            setPageText(index, "pom.xml");

        } catch (PartInitException e) {
            KvasirPlugin.getDefault().log(
                Messages.getString("PluginsFormEditor.error"), e); //$NON-NLS-1$
        }

    }


    public void doSave(IProgressMonitor monitor)
    {
        //store to plugin.xml

//        FileEditorInput editorInput = (FileEditorInput)getEditorInput();
//        IFile file = editorInput.getFile();
//        String string = PlustMapper.toPluginXML(descriptor);
//        try {
//            file.setContents(new ByteArrayInputStream(string.getBytes()),IFile.KEEP_HISTORY, monitor);
//        } catch (CoreException e) {
//            e.printStackTrace();
//        }
        
        this.commandStack.clear();
        
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


    public Object getAdapter(Class adapter)
    {
        if (IEditorCommandStack.class.equals(adapter)) {
            return this.commandStack;
        }
        return super.getAdapter(adapter);
    }


    public void fireCommandStachChanged()
    {
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }


    public boolean isDirty()
    {
        return this.commandStack.isDirty();
    }
}
