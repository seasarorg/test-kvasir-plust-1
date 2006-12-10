package org.seasar.kvasir.plust;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidArtifactRTException;
import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.maven.ide.eclipse.MavenEmbedderCallback;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.seasar.kvasir.plust.builder.GatherArtifactsTask;
import org.seasar.kvasir.plust.launch.console.KvasirConsole;
import org.seasar.kvasir.plust.maven.ConsoleMavenEmbeddedLogger;

import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;


/**
 * The main plugin class to be used in the desktop.
 */
public class KvasirPlugin extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "org.seasar.kvasir.plust";

    public static final String NATURE_ID = PLUGIN_ID + ".kvasirNature";

    public static final String BUILDER_ID = PLUGIN_ID + ".kvasirBuilder";

    public static final String POM_MARKER_ID = PLUGIN_ID + ".pomMarker";

    public static final String LICENSES_PATH = "licenses";

    /** reise embedder instance or create new one on every operation */
    private static final boolean REUSE_EMBEDDER = false;

    public static final String PROP_TESTENVIRONMENTGROUPID = "testEnvironmentGroupId";

    public static final String PROP_TESTENVIRONMENTARTIFACTID = "testEnvironmentArtifactId";

    public static final String PROP_TESTENVIRONMENTVERSION = "testEnvironmentVersion";

    public static final String IMG_VERTICAL = "icons/th_vertical.gif";

    public static final String IMG_HORIZONTAL = "icons/th_horizontal.gif";

    public static final String IMG_EXTENSION = "icons/extension_obj.gif";

    public static final String IMG_LOGO = "icons/logo.jpg";

    public static final String MANIFEST_PATH = "/src/main/plugin/plugin.xml";

    public static final String IMG_DECORATION = "icons/kvasir-decoration.gif";

    public static final String IMG_REQUIRED = "icons/required-plugin.gif";

    public static final String IMG_LIBRARY = "icons/runtime-path.gif";

    public static final String IMG_EXTENSION_POINT = "icons/extension-point.gif";

    public static final String IMG_ELEMENT = "icons/xom-element.gif";

    //The shared instance.
    private static KvasirPlugin plugin;

    private MavenEmbedder mavenEmbedder_;

    private KvasirConsole console_;

    private Set sourceCheckedArtifactIdSet_ = new HashSet();

    private ImageRegistry imageRegistry;

    private Map projectCache = new HashMap();


    /**
     * The constructor.
     */
    public KvasirPlugin()
    {
        plugin = this;

        imageRegistry = new ImageRegistry();
        imageRegistry.put(IMG_REQUIRED, getImageDescriptor(IMG_REQUIRED));
        imageRegistry.put(IMG_LIBRARY, getImageDescriptor(IMG_LIBRARY));
        imageRegistry.put(IMG_EXTENSION_POINT,
            getImageDescriptor(IMG_EXTENSION_POINT));
        imageRegistry.put(IMG_EXTENSION, getImageDescriptor(IMG_EXTENSION));
        imageRegistry.put(IMG_ELEMENT, getImageDescriptor(IMG_ELEMENT));
    }


    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context)
        throws Exception
    {
        super.start(context);

        try {
            console_ = new KvasirConsole();
        } catch (RuntimeException ex) {
            log(new Status(IStatus.ERROR, PLUGIN_ID, -1,
                "Unable to start console: " + ex.toString(), ex));
        }
    }


    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context)
        throws Exception
    {
        super.stop(context);

        stopEmbedder();
        if (console_ != null) {
            console_.shutdown();
        }

        plugin = null;
    }


    /**
     * Returns the shared instance.
     */
    public static KvasirPlugin getDefault()
    {
        return plugin;
    }


    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
    }


    protected void initializeImageRegistry(ImageRegistry reg)
    {

        super.initializeImageRegistry(reg);
    }


    public static IStatus constructStatus(Throwable t)
    {
        return constructStatus(t.getMessage(), t);
    }


    public static IStatus constructStatus(String message)
    {
        return constructStatus(message, null);
    }


    public static IStatus constructStatus(String message, Throwable t)
    {
        return new Status(IStatus.ERROR, KvasirPlugin.PLUGIN_ID, 0, message, t);
    }


    public static void mkdirs(IResource container, IProgressMonitor monitor)
        throws CoreException
    {
        if (container.getType() != IResource.FOLDER) {
            return;
        }
        IFolder folder = (IFolder)container;
        if (!folder.exists()) {
            mkdirs(folder.getParent(), monitor);
            folder.create(false, true, new SubProgressMonitor(monitor, 1));
        }
    }


    public static boolean shouldResourceBeIgnored(String path)
    {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        String name;
        int slash = path.lastIndexOf('/');
        if (slash >= 0) {
            name = path.substring(slash + 1);
        } else {
            name = path;
        }
        return name.equalsIgnoreCase(".svn") || name.equalsIgnoreCase("_svn")
            || name.equalsIgnoreCase("CVS");
    }


    public static void copy(IResource source, IPath destination, boolean force,
        IProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask("Copying resources", IProgressMonitor.UNKNOWN);
        try {
            copy0(source, destination, force, monitor);
        } finally {
            monitor.done();
        }
    }


    static void copy0(IResource source, IPath destination, boolean force,
        IProgressMonitor monitor)
        throws CoreException
    {
        if (shouldResourceBeIgnored(source.getName())) {
            return;
        }
        IResource destinationResource = ResourcesPlugin.getWorkspace()
            .getRoot().findMember(destination);
        if (source.getType() == IResource.FILE) {
            if (destinationResource != null
                && destinationResource.getType() == IResource.FILE) {
                IFile destinationFile = ResourcesPlugin.getWorkspace()
                    .getRoot().getFile(destination);
                destinationFile.setContents(((IFile)source).getContents(),
                    false, false, new SubProgressMonitor(monitor, 1));
            } else {
                if (destinationResource != null) {
                    destinationResource.delete(false, new SubProgressMonitor(
                        monitor, 1));
                }
                IFile destinationFile = ResourcesPlugin.getWorkspace()
                    .getRoot().getFile(destination);
                mkdirs(destinationFile.getParent(), new SubProgressMonitor(
                    monitor, 1));
                source.copy(destination, false, new SubProgressMonitor(monitor,
                    1));
            }
        } else if (source.getType() == IResource.FOLDER) {
            IFolder destinationFolder = null;
            if (destinationResource != null) {
                if (destinationResource.getType() == IResource.FOLDER) {
                    destinationFolder = (IFolder)destinationResource;
                } else {
                    destinationResource.delete(false, new SubProgressMonitor(
                        monitor, 1));
                }
            }
            if (destinationFolder == null) {
                destinationFolder = ResourcesPlugin.getWorkspace().getRoot()
                    .getFolder(destination);
                mkdirs(destinationFolder, new SubProgressMonitor(monitor, 1));
            }
            IResource[] members = ((IFolder)source).members();
            for (int i = 0; i < members.length; i++) {
                copy0(members[i], destination.append(members[i].getName()),
                    false, monitor);
            }
        } else {
            throw new CoreException(constructStatus("Can't copy resource "
                + source + " to " + destination));
        }
    }


    public void log(IStatus status)
    {
        getLog().log(status);
    }


    public void log(String message, Throwable t)
    {
        getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 0, message, t));
    }


    public void log(Throwable t)
    {
        String message = t.getMessage();
        if (message == null) {
            message = "";
        }
        getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 0, message, t));
    }


    public KvasirConsole getConsole()
    {
        return console_;
    }


    private MavenEmbedder createEmbedder()
    {
        MavenEmbedder embedder = new MavenEmbedder();
        MavenEmbedderLogger logger = new ConsoleMavenEmbeddedLogger(
            getConsole());
        final boolean debugEnabled = false;
        logger.setThreshold(debugEnabled ? MavenEmbedderLogger.LEVEL_DEBUG
            : MavenEmbedderLogger.LEVEL_INFO);
        embedder.setLogger(logger);

        // TODO find a better ClassLoader
        // ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        embedder.setClassLoader(getClass().getClassLoader());
        embedder.setInteractiveMode(false);

        try {
            embedder.start();
        } catch (MavenEmbedderException ex) {
            log("Unable to start MavenEmbedder", ex);
        }

        return embedder;
    }


    private void stopEmbedder()
    {
        if (mavenEmbedder_ != null) {
            try {
                mavenEmbedder_.stop();
                mavenEmbedder_ = null;
            } catch (MavenEmbedderException ex) {
                log("Unable to stop MavenEmbedder", ex);
            }
        }
    }


    public MavenProject getMavenProject(IFile pomFile, IProgressMonitor monitor)
    {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        return (MavenProject)executeInEmbedder(new ReadProjectTask(pomFile),
            monitor);
    }


    public Artifact[] gatherArtifacts(IFile pomFile, IProgressMonitor monitor)
    {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        return (Artifact[])executeInEmbedder(new GatherArtifactsTask(pomFile),
            monitor);
    }


    public void resolveClasspathEntries(Set libraryEntries,
        Set moduleArtifacts, IFile pomFile, boolean recursive,
        boolean downloadSources, IProgressMonitor monitor)
    {
        monitor.beginTask("Reading " + pomFile.getLocation(),
            IProgressMonitor.UNKNOWN);
        try {
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            final MavenProject mavenProject = getMavenProject(pomFile,
                new SubProgressMonitor(monitor, 1));
            if (mavenProject == null) {
                return;
            }

            deleteMarkers(pomFile);
            // TODO use version?
            moduleArtifacts.add(mavenProject.getGroupId() + ":"
                + mavenProject.getArtifactId());

            Set artifacts = mavenProject.getArtifacts();
            for (Iterator it = artifacts.iterator(); it.hasNext();) {
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                final Artifact a = (Artifact)it.next();

                monitor.subTask("Processing " + a.getId());

                if (!"jar".equals(a.getType())) {
                    continue;
                }
                // TODO use version?
                if (!moduleArtifacts.contains(a.getGroupId() + ":"
                    + a.getArtifactId())
                    &&
                    // TODO verify if there is an Eclipse API to check that archive is acceptable
                    ("jar".equals(a.getType()) || "zip".equals(a.getType()))) {
                    String artifactLocation = a.getFile().getAbsolutePath();

                    // TODO add a lookup through workspace projects

                    Path srcPath = null;
                    File srcFile = new File(artifactLocation.substring(0,
                        artifactLocation.length() - 4)
                        + "-sources.jar");
                    if (srcFile.exists()) {
                        // XXX ugly hack to do not download any sources
                        srcPath = new Path(srcFile.getAbsolutePath());
                    } else if (downloadSources && !isSourceChecked(a)) {
                        srcPath = (Path)executeInEmbedder(
                            new MavenEmbedderCallback() {
                                public Object run(MavenEmbedder mavenEmbedder,
                                    IProgressMonitor monitor)
                                {
                                    monitor.beginTask("Resolve sources "
                                        + a.getId(), IProgressMonitor.UNKNOWN);
                                    try {
                                        Artifact src = mavenEmbedder
                                            .createArtifactWithClassifier(a
                                                .getGroupId(), a
                                                .getArtifactId(), a
                                                .getVersion(), "java-source",
                                                "sources");
                                        if (src != null) {
                                            mavenEmbedder
                                                .resolve(
                                                    src,
                                                    mavenProject
                                                        .getRemoteArtifactRepositories(),
                                                    mavenEmbedder
                                                        .getLocalRepository());
                                            return new Path(src.getFile()
                                                .getAbsolutePath());
                                        }
                                    } catch (AbstractArtifactResolutionException ex) {
                                        String name = ex.getGroupId() + ":"
                                            + ex.getArtifactId() + "-"
                                            + ex.getVersion() + "."
                                            + ex.getType();
                                        getConsole().logMessage(
                                            ex.getOriginalMessage() + " "
                                                + name);
                                    } finally {
                                        monitor.done();
                                    }
                                    return null;
                                }
                            }, new SubProgressMonitor(monitor, 1));
                        setSourceChecked(a);
                    }

                    libraryEntries.add(JavaCore.newLibraryEntry(new Path(
                        artifactLocation), srcPath, null));
                }
            }

            if (recursive) {
                IContainer parent = pomFile.getParent();

                List modules = mavenProject.getModules();
                for (Iterator it = modules.iterator(); it.hasNext()
                    && !monitor.isCanceled();) {
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }

                    String module = (String)it.next();
                    IResource memberPom = parent.findMember(module + "/"
                        + IKvasirProject.POM_FILE_NAME);
                    if (memberPom != null
                        && memberPom.getType() == IResource.FILE) {
                        resolveClasspathEntries(libraryEntries,
                            moduleArtifacts, (IFile)memberPom, true,
                            downloadSources, new SubProgressMonitor(monitor, 1));
                    }
                }
            }
        } catch (OperationCanceledException ex) {
            throw ex;
        } catch (InvalidArtifactRTException ex) {
            addMarker(pomFile, ex.getBaseMessage(), 1, IMarker.SEVERITY_ERROR);
        } catch (Throwable ex) {
            addMarker(pomFile, ex.toString(), 1, IMarker.SEVERITY_ERROR);
        } finally {
            monitor.done();
        }
    }


    public boolean isSourceChecked(Artifact artifact)
    {
        return sourceCheckedArtifactIdSet_.contains(artifact.getId());
    }


    public void setSourceChecked(Artifact artifact)
    {
        sourceCheckedArtifactIdSet_.add(artifact.getId());
    }


    public void resetSourceCheckedSet()
    {
        sourceCheckedArtifactIdSet_.clear();
    }


    public void addMarker(IResource file, String message, int lineNumber,
        int severity)
    {
        try {
            deleteMarkers(file);

            // TODO Workaround
            if (message.indexOf("Duplicate project ID found") >= 0) {
                return;
            }

            IMarker marker = file.createMarker(POM_MARKER_ID);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.SEVERITY, severity);
            if (lineNumber == -1) {
                lineNumber = 1;
            }
            marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
        } catch (CoreException ex) {
            log(ex);
        }
    }


    public void deleteMarkers(IResource file)
    {
        try {
            file.deleteMarkers(POM_MARKER_ID, false, IResource.DEPTH_ZERO);
        } catch (CoreException ex) {
            log(ex);
        }
    }


    public Object executeInEmbedder(MavenEmbedderCallback template,
        IProgressMonitor monitor)
    {
        try {
            return template.run(getMavenEmbedder(), monitor);
        } finally {
            if (!REUSE_EMBEDDER)
                stopEmbedder();
        }
    }


    public Object executeInEmbedder(String name, MavenEmbedderCallback template)
    {
        try {
            EmbedderJob job = new EmbedderJob(name, template,
                getMavenEmbedder());
            job.schedule();
            try {
                job.join();
                // TODO check job.getResult()
                return job.getCallbackResult();
            } catch (InterruptedException ex) {
                getConsole().logError("Interrupted " + ex.toString());
                return null;
            }
        } finally {
            if (!REUSE_EMBEDDER)
                stopEmbedder();
        }
    }


    private synchronized MavenEmbedder getMavenEmbedder()
    {
        if (REUSE_EMBEDDER) {
            if (mavenEmbedder_ == null) {
                mavenEmbedder_ = createEmbedder();
            }
            return mavenEmbedder_;
        } else {
            return createEmbedder();
        }
    }


    public void unzip(File zipArchive, IFolder destination,
        boolean forceOverwite, IProgressMonitor monitor)
        throws CoreException
    {
        if (!forceOverwite && destination.exists()) {
            return;
        }

        monitor.beginTask("Unzipping", IProgressMonitor.UNKNOWN);
        try {
            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile(zipArchive);
                for (Enumeration enm = zipFile.entries(); enm.hasMoreElements();) {
                    monitor.worked(1);
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }

                    ZipEntry entry = (ZipEntry)enm.nextElement();
                    if (entry.isDirectory()) {
                        mkdirs(destination.getFolder(entry.getName()),
                            new SubProgressMonitor(monitor, 1));
                    } else {
                        IFile file = destination.getFile(entry.getName());
                        if (file.exists()) {
                            file.setContents(zipFile.getInputStream(entry),
                                false, false,
                                new SubProgressMonitor(monitor, 1));
                        } else {
                            mkdirs(file.getParent(), new SubProgressMonitor(
                                monitor, 1));
                            file.create(zipFile.getInputStream(entry), false,
                                new SubProgressMonitor(monitor, 1));
                        }
                    }
                }
            } catch (ZipException ex) {
                throw new CoreException(constructStatus(ex));
            } catch (IOException ex) {
                throw new CoreException(constructStatus(ex));
            } finally {
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException ex) {
                        log(ex);
                    }
                }
            }
        } finally {
            monitor.done();
        }
    }


    public Properties loadBuildProperties(IProject project)
        throws CoreException
    {
        Properties prop = new Properties();

        IFile buildProperties = project.getFile("build.properties");
        if (buildProperties.exists()) {
            InputStream is = null;
            try {
                is = buildProperties.getContents();
                prop.load(is);
            } catch (IOException ex) {
                throw new CoreException(constructStatus(
                    "Can't load build.properties", ex));
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        log("Can't close input stream: " + buildProperties, ex);
                    }
                }
            }
        }

        prop.setProperty("projectRoot", project.getLocation().toOSString()); //$NON-NLS-1$

        return prop;
    }


    public void storeBuildProperties(IProject project, Properties prop)
        throws CoreException
    {
        IFile buildProperties = project.getFile("build.properties");
        File outputFile = buildProperties.getLocation().toFile();
        OutputStream os = null;
        try {
            os = new FileOutputStream(outputFile);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            prop.store(bos, null);
            bos.flush();
            bos.close();
            os = null;

            buildProperties.refreshLocal(IResource.DEPTH_ZERO, null);
        } catch (IOException ex) {
            throw new CoreException(KvasirPlugin.constructStatus(
                "Can't output " + buildProperties, ex));
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                    plugin.getLog().log(
                        KvasirPlugin.constructStatus(
                            "Can't close output stream", ex));
                }
            }
        }
    }


    public void createInstanceFromArchetype(IProject project,
        boolean forBuilding, IProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask("Creating instance from archetype",
            IProgressMonitor.UNKNOWN);
        try {
            Properties root = loadBuildProperties(project);
            String archetypeId = root.getProperty("archetypeId");
            if (archetypeId == null) {
                return;
            }
            final Bundle bundle = getBundle();
            String archetypePath = "archetypes/" + archetypeId + "/";

            Configuration cfg = new Configuration();
            cfg.setEncoding(Locale.getDefault(), "UTF-8");
            cfg.setTemplateLoader(new URLTemplateLoader() {
                protected URL getURL(String path)
                {
                    return bundle.getEntry(path);
                }
            });
            cfg.setObjectWrapper(new DefaultObjectWrapper());

            evaluateTemplate(archetypePath, "", forBuilding, project
                .getProjectRelativePath(), project, cfg, bundle, root,
                archetypeId, new SubProgressMonitor(monitor, 1));
        } finally {
            monitor.done();
        }
    }


    void evaluateTemplate(String templateRootPath, String templateRelativePath,
        boolean forBuilding, IPath location, IProject project,
        Configuration cfg, final Bundle bundle, Properties prop,
        String archetypeId, IProgressMonitor monitor)
        throws CoreException
    {
        String targetPath = templateRootPath + templateRelativePath;
        for (Enumeration enm = bundle.getEntryPaths(targetPath); enm != null
            && enm.hasMoreElements();) {
            String path = (String)enm.nextElement();
            String relativePath = path.substring(templateRootPath.length());
            if (("build/webapp/".startsWith(relativePath) || relativePath
                .startsWith("build/webapp/"))
                ^ forBuilding) {
                continue;
            }

            if (shouldResourceBeIgnored(path.substring(targetPath.length()))) {
                continue;
            }

            StringWriter sw = new StringWriter();
            try {
                new Template("pathName", new StringReader(URLDecoder.decode(
                    relativePath, "UTF-8")), cfg).process(prop, sw);
            } catch (IOException ex) {
                throw new CoreException(KvasirPlugin.constructStatus(
                    "Can't process template '" + path + "' in archetype '"
                        + archetypeId + "'", ex));
            } catch (TemplateException ex) {
                throw new CoreException(KvasirPlugin.constructStatus(
                    "Can't process template '" + path + "' in archetype '"
                        + archetypeId + "'", ex));
            }
            IPath theLocation = location.append(sw.toString());
            if (path.endsWith("/")) {
                KvasirPlugin.mkdirs(project.getFolder(theLocation),
                    new SubProgressMonitor(monitor, 1));
                evaluateTemplate(templateRootPath, relativePath, forBuilding,
                    location, project, cfg, bundle, prop, archetypeId, monitor);
            } else {
                byte[] evaluated;
                try {
                    sw = new StringWriter();
                    cfg.getTemplate(path).process(prop, sw);
                    evaluated = sw.toString().getBytes("UTF-8");
                } catch (TemplateException ex) {
                    throw new CoreException(KvasirPlugin.constructStatus(
                        "Can't process template '" + path + "' in archetype '"
                            + archetypeId + "'", ex));
                } catch (IOException ex) {
                    throw new CoreException(KvasirPlugin.constructStatus(
                        "Can't process template '" + path + "' in archetype '"
                            + archetypeId + "'", ex));
                }

                IFile outputFile = project.getFile(theLocation);
                if (outputFile.exists()) {
                    outputFile.setContents(new ByteArrayInputStream(evaluated),
                        false, false, new SubProgressMonitor(monitor, 1));
                } else {
                    KvasirPlugin.mkdirs(outputFile.getParent(),
                        new SubProgressMonitor(monitor, 1));
                    outputFile.create(new ByteArrayInputStream(evaluated),
                        false, new SubProgressMonitor(monitor, 1));
                }
            }
        }
    }


    public static String getString(String key)
    {
        if (getDefault() == null) {
            // FIXME nullにならないようにしたいのだが…。
            return key;
        }
        return Platform.getResourceString(getDefault().getBundle(), "%" + key);
    }


    public KvasirProject getKvasirProject(IEditorInput input)
    {
        if (!projectCache.containsKey(input)) {
            IProject project = getCurrentProject(input);
            IJavaProject javaProject = JavaCore.create(project);

            KvasirProject kvasirProject = new KvasirProject(javaProject);
            projectCache.put(input, kvasirProject);
        }
        return (KvasirProject)projectCache.get(input);
    }


    public void flushKvasirProject(IEditorInput input)
    {
        projectCache.remove(input);
    }


    public IProject getCurrentProject(IEditorInput input)
    {
        IFileEditorInput editorInput = (IFileEditorInput)input;
        return editorInput.getFile().getProject();
    }


    public Image getImage(String key)
    {
        return imageRegistry.get(key);
    }


    public String getIndexDir()
    {
        return new File(getStateLocation().toFile(), "index").getAbsolutePath();
    }
}
