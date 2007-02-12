package org.seasar.kvasir.plust.builder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.maven.ide.eclipse.container.Maven2ClasspathContainer;
import org.seasar.kvasir.plust.IKvasirProject;
import org.seasar.kvasir.plust.KvasirPlugin;

import net.skirnir.freyja.TemplateEvaluator;


public class KvasirBuilder extends IncrementalProjectBuilder
{
    private TemplateEvaluator readPluginXmlEvaluator = new TemplateEvaluator(
        new ReadPluginXmlTagEvaluator(), null);

    private TemplateEvaluator writePluginXmlEvaluator = new TemplateEvaluator(
        new WritePluginXmlTagEvaluator(), null);


    /*
     * (non-Javadoc)
     * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
     *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask("Building Kvasir/Sora project",
            IProgressMonitor.UNKNOWN);
        try {
            IProject project = getProject();
            if (!project.hasNature(KvasirPlugin.NATURE_ID)) {
                return null;
            }

            IJavaProject javaProject = JavaCore.create(project);
            if (javaProject == null) {
                return null;
            }

            if (kind == INCREMENTAL_BUILD) {
                return null;
            }

            if (kind == AUTO_BUILD) {
                Verifier verifier = new Verifier();
                getDelta(project).accept(verifier, IContainer.EXCLUDE_DERIVED);

                KvasirPlugin plugin = KvasirPlugin.getDefault();

                boolean updatePomXml;
                boolean updateClassPath;
                boolean updateDependingPlugins;
                boolean updateLib;

                if (verifier.pluginXmlUpdated) {
                    updatePomXml = true;
                    updateClassPath = true;
                    updateDependingPlugins = true;
                    updateLib = false;
                } else if (verifier.pomXmlUpdated) {
                    updatePomXml = false;
                    updateClassPath = true;
                    updateDependingPlugins = false;
                    updateLib = true;
                } else {
                    updatePomXml = false;
                    updateClassPath = false;
                    updateDependingPlugins = false;
                    updateLib = false;
                }

                Artifact[] artifacts = null;
                if (updateDependingPlugins || updateLib) {
                    artifacts = KvasirPlugin.getDefault().gatherArtifacts(
                        project.getFile(IKvasirProject.POM_FILE_NAME),
                        new SubProgressMonitor(monitor, 1));
                }

                if (updatePomXml) {
                    updatePomXml(project, new SubProgressMonitor(monitor, 1));
                }
                if (updateClassPath) {
                    updateClasspath(javaProject, new SubProgressMonitor(
                        monitor, 1));
                }
                if (updateDependingPlugins) {
                    plugin.deployRequiredPluginsToTestEnvironment(project,
                        artifacts, new SubProgressMonitor(monitor, 1));
                }
                if (updateLib) {
                    plugin.updateOuterLibrariesProperties(project, artifacts,
                        new SubProgressMonitor(monitor, 1));
                }
            } else {
                KvasirPlugin plugin = KvasirPlugin.getDefault();

                plugin.buildTestEnvironment(project, monitor);

                updatePomXml(project, new SubProgressMonitor(monitor, 1));
                plugin.resetSourceCheckedSet();
                updateClasspath(javaProject, new SubProgressMonitor(monitor, 1));
            }

            return null;
        } finally {
            monitor.done();
        }
    }


    void updatePomXml(IProject project, IProgressMonitor monitor)
    {
        monitor.beginTask("Updating pom.xml", 1);
        try {
            IFile pluginFile = project.getFile(IKvasirProject.PLUGIN_FILE_PATH);
            if (!pluginFile.exists()) {
                return;
            }
            IFile pomFile = project.getFile(IKvasirProject.POM_FILE_NAME);
            if (!pomFile.exists()) {
                return;
            }

            KvasirTemplateContext context = (KvasirTemplateContext)readPluginXmlEvaluator
                .newContext();
            readPluginXmlEvaluator.evaluate(context, new BufferedReader(
                new InputStreamReader(new FileInputStream(pluginFile
                    .getLocation().toFile()), "UTF-8")));
            Import[] imports = context.getImports();

            Properties prop = KvasirPlugin.getDefault().loadBuildProperties(
                project);
            String testEnvironmentVersion = prop
                .getProperty(KvasirPlugin.PROP_TESTENVIRONMENTVERSION);

            KvasirPlugin.getDefault()
                .executeInEmbedder(
                    new UpdatePluginDependenciesTask(project, imports,
                        testEnvironmentVersion),
                    new SubProgressMonitor(monitor, 1));
        } catch (CoreException ex) {
            KvasirPlugin.getDefault().log(ex);
        } catch (IOException ex) {
            KvasirPlugin.getDefault().log(ex);
        } finally {
            monitor.done();
        }
    }


    private void updateClasspath(IJavaProject javaProject,
        IProgressMonitor monitor)
        throws JavaModelException
    {
        Set<IClasspathEntry> entries = new HashSet<IClasspathEntry>();
        Set<String> moduleArtifacts = new HashSet<String>();
        IFile pomFile = javaProject.getProject().getFile(
            IKvasirProject.POM_FILE_NAME);
        KvasirPlugin.getDefault().resolveClasspathEntries(entries,
            moduleArtifacts, pomFile, true, true, monitor);

        Maven2ClasspathContainer container = new Maven2ClasspathContainer(
            entries);
        JavaCore.setClasspathContainer(container.getPath(),
            new IJavaProject[] { javaProject },
            new IClasspathContainer[] { container }, monitor);
    }


    @Override
    protected void clean(IProgressMonitor monitor)
        throws CoreException
    {
        KvasirPlugin.getDefault().cleanTestEnvironment(getProject(), false,
            monitor);
    }


    private static final class Verifier
        implements IResourceDeltaVisitor
    {
        boolean pomXmlUpdated;

        boolean pluginXmlUpdated;


        public boolean visit(IResourceDelta delta)
        {
            IResource resource = delta.getResource();
            String path = resource.getProjectRelativePath().toPortableString();
            //            System.out.print("[NOTICE] " + path + " is ");
            //            if (kind == IResourceDelta.ADDED) {
            //                System.out.println("added");
            //            } else if (kind == IResourceDelta.CHANGED) {
            //                System.out.println("changed");
            //            } else if (kind == IResourceDelta.REMOVED) {
            //                System.out.println("removed");
            //            } else {
            //                System.out.println("processed (kind=" + kind + ")");
            //            }

            if (IKvasirProject.POM_FILE_NAME.equals(path)) {
                pomXmlUpdated = true;
                return false;
            } else if (IKvasirProject.PLUGIN_RESOURCES_PATH.startsWith(path)) {
                return true;
            } else if (IKvasirProject.PLUGIN_FILE_PATH.equals(path)) {
                pluginXmlUpdated = true;
                return false;
            } else {
                return false;
            }
        }
    }
}
