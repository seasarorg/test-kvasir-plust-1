package org.seasar.kvasir.plust.model;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.seasar.kvasir.base.plugin.descriptor.Extension;
import org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint;
import org.seasar.kvasir.base.plugin.descriptor.Import;
import org.seasar.kvasir.base.plugin.descriptor.Library;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.descriptor.Requires;
import org.seasar.kvasir.base.plugin.descriptor.Runtime;
import org.seasar.kvasir.base.plugin.descriptor.impl.PluginDescriptorImpl;
import org.seasar.kvasir.plust.KvasirProject;

import net.skirnir.xom.Element;
import net.skirnir.xom.XOMapper;
import net.skirnir.xom.XOMapperFactory;
import net.skirnir.xom.annotation.impl.AnnotationBeanAccessorFactory;


/**
 * @author shidat
 *
 */
public class PlustMapper
{

    public static PluginModel toPlustModel(PluginDescriptor descriptor,
        MavenProject project, Properties properties, KvasirProject kvasirProject)
    {
        //TODO べたっと書いておく.
        PluginModel root = new PluginModel();
        root.setArchetypeId(properties.getProperty("archetypeId"));
        root.setPluginClassName(properties.getProperty("pluginClassName"));
        root.setPluginClassNameXML(properties
            .getProperty("pluginClassName_XML"));
        root.setPluginId(properties.getProperty("pluginId"));
        root.setPluginName(properties.getProperty("pluginName"));
        root.setPluginPackagePath(properties.getProperty("pluginPackagePath"));
        root
            .setPluginProviderName(properties.getProperty("pluginProviderName"));
        root.setPluginShortId(properties.getProperty("pluginShortId"));
        root.setPluginVersion(properties.getProperty("pluginVersion"));
        root.setTestEnviromentVersion(properties
            .getProperty("testEnvironmentVersion"));
        root.setTestEnvironmentArtifactId(properties
            .getProperty("testEnvironmentArtifactId"));
        root.setTestEnvironmentGroupId(properties
            .getProperty("testEnvironmentGroupId"));

        Import[] imports = descriptor.getRequires().getImports();
        for (int i = 0; i < imports.length; i++) {
            Import imp = imports[i];
            ImportModel model = new ImportModel();
            model.setPluginId(imp.getPlugin());
            model.setVersion(imp.getVersionString());
            root.addRequire(model);
        }

        Library[] libraries = descriptor.getRuntime().getLibraries();
        for (int i = 0; i < libraries.length; i++) {
            Library library = libraries[i];
            //TODO ライブラリをどうすっか..
            LibraryModel model = new LibraryModel();
            model.setLibrary(library);
            root.addRuntime(model);
        }

        Extension[] extensions = descriptor.getExtensions();
        for (int i = 0; i < extensions.length; i++) {
            Extension extension = extensions[i];
            ExtensionModel model = new ExtensionModel();
            model.setPoint(extension.getPoint());
            model.setProperty(extension.getElements());
            model.setKvasirProject(kvasirProject);
            root.addExtension(model);
        }

        ExtensionPoint[] extensionPoints = descriptor.getExtensionPoints();
        for (int i = 0; i < extensionPoints.length; i++) {
            ExtensionPoint point = extensionPoints[i];
            ExtensionPointModel model = new ExtensionPointModel();
            model.setId(point.getId());
            model.setClassName(point.getElementClassName());
            model.setDescription(point.getDescription());
            root.addExtensionPoint(model);
        }

        return root;
    }


    public static String toPluginXML(PluginModel model)
    {
        PluginDescriptorImpl rootImpl = new PluginDescriptorImpl();
        rootImpl.setId(model.getPluginId());
        rootImpl.setVersionString(model.getPluginVersion());
        rootImpl.setName(model.getPluginName());
        rootImpl.setProviderName(model.getPluginProviderName());
        rootImpl.setXmlns("http://kvasir.sandbox.seasar.org/plugin/3.0.0");
        rootImpl.setXmlns_xsi("http://www.w3.org/2001/XMLSchema-instance");
        rootImpl
            .setXsi_schemaLocation("http://kvasir.sandbox.seasar.org/plugin/3.0.0 http://kvasir.sandbox.seasar.org/support/plugin-3_0_0.xsd");

        Runtime runtime = new Runtime();
        LibraryModel[] libraryModels = model.getRuntime();
        for (int i = 0; i < libraryModels.length; i++) {
            LibraryModel lib = libraryModels[i];
            runtime.addLibrary(lib.getLibrary());
        }
        rootImpl.setRuntime(runtime);

        Requires requires = new Requires();
        ImportModel[] importModels = model.getRequires();
        for (int i = 0; i < importModels.length; i++) {
            ImportModel impModel = importModels[i];
            Import imp = new Import();
            imp.setPlugin(impModel.getPluginId());
            imp.setVersionString(impModel.getVersion());
            requires.addImport(imp);
        }
        rootImpl.setRequires(requires);

        ExtensionModel[] extensions = model.getExtensions();
        for (int i = 0; i < extensions.length; i++) {
            ExtensionModel ext = extensions[i];
            Extension extension = new Extension();
            extension.setPoint(ext.getPoint());
            Element[] property = ext.getProperty();
            for (int j = 0; j < property.length; j++) {
                Element element = property[j];
                extension.addElement(element);
            }
            rootImpl.addExtension(extension);
        }

        ExtensionPointModel[] extensionPoints = model.getExtensionPoints();
        for (int i = 0; i < extensionPoints.length; i++) {
            ExtensionPointModel pointModel = extensionPoints[i];
            ExtensionPoint point = new ExtensionPoint();
            point.setId(pointModel.getId());
            point.setElementClassName(pointModel.getClassName());
            point.setDescription(pointModel.getDescription());

            rootImpl.addExtensionPoint(point);
        }

        XOMapper mapper = XOMapperFactory.newInstance();
        mapper.setBeanAccessorFactory(new AnnotationBeanAccessorFactory());
        StringWriter writer = new StringWriter();

        try {
            mapper.toXML(rootImpl, writer);
            writer.flush();
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "";
    }


    public static Properties toBuildProperty(PluginModel model)
    {
        Properties properties = new Properties();
        properties.setProperty("archetypeId", model.getArchetypeId());
        properties.setProperty("pluginClassName", model.getPluginClassName());
        properties.setProperty("pluginClassName_XML", model
            .getPluginClassNameXML());
        properties.setProperty("pluginId", model.getPluginId());
        properties.setProperty("pluginName", model.getPluginName());
        properties.setProperty("pluginPackagePath", model
            .getPluginPackagePath());
        properties.setProperty("pluginProviderName", model
            .getPluginProviderName());
        properties.setProperty("pluginShortId", model.getPluginShortId());
        properties.setProperty("pluginVersion", model.getPluginVersion());
        properties.setProperty("testEnvironmentVersion", model
            .getTestEnviromentVersion());
        properties.setProperty("testEnvironmentArtifactId", model
            .getTestEnvironmentArtifactId());
        properties.setProperty("testEnvironmentGroupId", model
            .getTestEnvironmentGroupId());

        return properties;
    }


    public static String toPomXML(PluginModel model, MavenProject project)
    {

        return "";
    }

}
