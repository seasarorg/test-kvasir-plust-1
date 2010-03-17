package org.seasar.kvasir.plust.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.maven.ide.eclipse.Maven2Plugin;
import org.maven.ide.eclipse.container.Maven2ClasspathContainer;
import org.seasar.kvasir.plust.Globals;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.form.PluginsFormEditor;

import net.skirnir.xom.XOMUtils;


public class NewPluginWizard extends Wizard
    implements INewWizard, IExecutableExtension
{
    private NewPluginWizardFirstPage firstPage_;

    private NewPluginWizardSecondPage secondPage_;

    private NewPluginWizardThirdPage thirdPage_;

    private IWorkbench workbench_;

    private IConfigurationElement config_;

    private IProject project_;

    private IJavaProject javaProject_;


    public NewPluginWizard()
    {
    }


    public void addPages()
    {
        firstPage_ = new NewPluginWizardFirstPage();
        addPage(firstPage_);

        secondPage_ = new NewPluginWizardSecondPage();
        addPage(secondPage_);

        thirdPage_ = new NewPluginWizardThirdPage();
        addPage(thirdPage_);
    }


    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        workbench_ = workbench;

        setNeedsProgressMonitor(true);
    }


    public boolean canFinish()
    {
        return thirdPage_.isPageComplete();
    }


    public boolean performFinish()
    {
        try {
            WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
                protected void execute(IProgressMonitor monitor)
                    throws CoreException, InvocationTargetException,
                    InterruptedException
                {
                    createProject(monitor != null ? monitor
                        : new NullProgressMonitor());
                }
            };

            getContainer().run(false, true, operation);
        } catch (InvocationTargetException ex) {
            reportError(ex);
            return false;
        } catch (InterruptedException ex) {
            return false;
        }

        BasicNewProjectResourceWizard.updatePerspective(config_);
        BasicNewResourceWizard.selectAndReveal(project_, workbench_
            .getActiveWorkbenchWindow());
        openPluginEditor();
        return true;
    }


    void openPluginEditor()
    {
        IResource resource = project_.findMember(new Path(
            KvasirPlugin.MANIFEST_PATH));
        if (resource.getType() == IResource.FILE) {
            FileEditorInput editorInput = new FileEditorInput((IFile)resource);
            IWorkbenchPage page = workbench_.getActiveWorkbenchWindow()
                .getActivePage();
            try {
                page.openEditor(editorInput, PluginsFormEditor.ID);
            } catch (PartInitException e) {
                e.printStackTrace();
            }
        }
    }


    void createProject(IProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask("Creating Kvasir/Sora plugin project", 9); //$NON-NLS-1$
        try {
            project_ = firstPage_.getProjectHandle();
            IPath locationPath = firstPage_.getLocationPath();

            if (!project_.exists()) {
                IProjectDescription desc = project_.getWorkspace()
                    .newProjectDescription(project_.getName());
                if (Platform.getLocation().equals(locationPath)) {
                    locationPath = null;
                }
                desc.setLocation(locationPath);
                project_.create(desc, new SubProgressMonitor(monitor, 1));
            } else {
                monitor.worked(1);
            }
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            project_.open(new SubProgressMonitor(monitor, 1));
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            createBuildProperties(new SubProgressMonitor(monitor, 1));
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            KvasirPlugin.getDefault().createInstanceFromArchetype(project_,
                false, new SubProgressMonitor(monitor, 1));
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            installLicenseFile(secondPage_.getSelectedLicense(),
                new SubProgressMonitor(monitor, 1));
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            IProjectDescription description = project_.getDescription();
            List<String> newNatureList = new ArrayList<String>();
            newNatureList.add(JavaCore.NATURE_ID);
            newNatureList.add(KvasirPlugin.NATURE_ID);
            if (Platform.getBundle(Globals.BUNDLENAME_TOMCATPLUGIN) != null) {
                newNatureList.add(Globals.NATURE_ID_TOMCAT);
            }
            String[] newNatures = (String[])newNatureList
                .toArray(new String[0]);
            description.setNatureIds(newNatures);
            project_.setDescription(description, new SubProgressMonitor(
                monitor, 1));
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            javaProject_ = JavaCore.create(project_);

            constructMavenDependencies(new SubProgressMonitor(monitor, 1));
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
        } finally {
            monitor.done();
        }
    }


    void createBuildProperties(SubProgressMonitor monitor)
        throws CoreException
    {
        monitor
            .beginTask("Creating build.properties", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
        try {
            KvasirPlugin plugin = KvasirPlugin.getDefault();
            Properties prop = new Properties();
            prop.setProperty("pluginId", secondPage_.getPluginId()); //$NON-NLS-1$
            prop.setProperty("pluginShortId", getShortId(secondPage_ //$NON-NLS-1$
                .getPluginId()));
            prop.setProperty("pluginName", secondPage_.getPluginName()); //$NON-NLS-1$
            prop.setProperty("pluginVersion", secondPage_.getPluginVersion()); //$NON-NLS-1$
            prop.setProperty("pluginProviderName", secondPage_ //$NON-NLS-1$
                .getPluginProviderName());
            prop.setProperty("pluginPackagePath", secondPage_.getPluginId() //$NON-NLS-1$
                .replace('.', '/'));
            prop.setProperty("pluginClassName", secondPage_ //$NON-NLS-1$
                .getPluginClassName());
            prop.setProperty(KvasirPlugin.PROP_TESTENVIRONMENTGROUPID,
                secondPage_ //$NON-NLS-1$
                    .getTestEnvironmentGroupId());
            prop.setProperty(KvasirPlugin.PROP_TESTENVIRONMENTARTIFACTID,
                secondPage_ //$NON-NLS-1$
                    .getTestEnvironmentArtifactId());
            prop.setProperty(KvasirPlugin.PROP_TESTENVIRONMENTVERSION,
                secondPage_ //$NON-NLS-1$
                    .getTestEnvironmentVersion());
            prop.setProperty("archetypeId", thirdPage_.getTemplateId()); //$NON-NLS-1$
            prop.setProperty(
                "pluginClassName_XML", XOMUtils.toXMLName(secondPage_ //$NON-NLS-1$
                    .getPluginClassName()));
            LicenseMetaData license = secondPage_.getSelectedLicense();
            if (license != null) {
                prop.setProperty("license", license.getName()); //$NON-NLS-1$
            }
            plugin.storeBuildProperties(project_, prop);
        } finally {
            monitor.done();
        }
    }


    void constructMavenDependencies(IProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask("Constructing Maven dependencies", 1); //$NON-NLS-1$
        try {
            // remove classpatch container from JavaProject
            IClasspathEntry[] entries = javaProject_.getRawClasspath();
            ArrayList<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>();
            for (int i = 0; i < entries.length; i++) {
                if (!Maven2ClasspathContainer
                    .isMaven2ClasspathContainer(entries[i].getPath())) {
                    newEntries.add(entries[i]);
                }
            }
            newEntries.add(JavaCore.newContainerEntry(new Path(
                Maven2Plugin.CONTAINER_ID)));
            javaProject_.setRawClasspath((IClasspathEntry[])newEntries
                .toArray(new IClasspathEntry[newEntries.size()]), null);

            //            KvasirPlugin.getDefault().updateClasspath(javaProject_,
            //                new SubProgressMonitor(monitor, 1));
        } finally {
            monitor.done();
        }
    }


    String getShortId(String pluginId)
    {
        if (pluginId == null) {
            return null;
        }
        int dot = pluginId.lastIndexOf('.');
        if (dot < 0) {
            return pluginId;
        } else {
            return pluginId.substring(dot + 1);
        }
    }


    void installLicenseFile(LicenseMetaData license, IProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask("Installing license file", 1); //$NON-NLS-1$
        try {
            if (license == null) {
                return;
            }

            URL licenseURL = KvasirPlugin.getDefault().getBundle().getEntry(
                KvasirPlugin.LICENSES_PATH + "/" + license.getFileName()); //$NON-NLS-1$
            IFile licenseFile = project_.getFile("LICENSE.txt"); //$NON-NLS-1$
            try {
                licenseFile.create(licenseURL.openStream(), false,
                    new SubProgressMonitor(monitor, 1));
            } catch (IOException ex) {
                throw new CoreException(KvasirPlugin.constructStatus(
                    "Can't open license file: " + licenseURL, ex)); //$NON-NLS-1$
            }
        } finally {
            monitor.done();
        }
    }


    void reportError(InvocationTargetException ex)
    {
        String title = KvasirPlugin
            .getString("NewPluginWizard.TITLE_ERROR_FAIL_CREATING_PROJECT"); //$NON-NLS-1$
        String message = KvasirPlugin
            .getString("NewPluginWizard.MESSAGE_ERROR_FAIL_CREATING_PROJECT"); //$NON-NLS-1$

        Throwable target = ex.getTargetException();
        IStatus status = null;
        if (target instanceof CoreException) {
            status = ((CoreException)target).getStatus();
        }

        KvasirPlugin.getDefault().log(ex);
        if (status != null) {
            ErrorDialog.openError(getShell(), title, message, status);
        } else {
            MessageDialog.openError(getShell(), title, message);
        }
    }


    public void setInitializationData(IConfigurationElement config,
        String propertyName, Object data)
        throws CoreException
    {
        config_ = config;
    }


    public NewPluginWizardFirstPage getFirstPage()
    {
        return firstPage_;
    }


    public NewPluginWizardSecondPage getSecondPage()
    {
        return secondPage_;
    }
}