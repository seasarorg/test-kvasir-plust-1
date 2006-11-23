/**
 * 
 */
package org.seasar.kvasir.plust.model;

import java.util.ArrayList;
import java.util.List;


/**
 * @author shidat
 *
 */
public class PluginModel extends PlustModel
{

    //retrieved from build.properties

    //plugin.xml/plugin@id, pom.xml/project/artifactId
    private String pluginId;

    //pom.xml/project/parent/groupId
    private String testEnvironmentGroupId;

    //pom.xml/project/parent/artifactId?
    private String testEnvironmentArtifactId;

    //pom.xml/project/parent/version
    private String testEnviromentVersion;

    private String archetypeId;

    private String pluginPackagePath;

    private String pluginClassName;

    //same in plugin.xml, pom.xml
    private String pluginProviderName;

    //pom.xml/project/groupId
    private String pluginShortId;

    //same in plugin.xml, pom.xml
    private String pluginName;

    //same in plugin.xml, pom.xml
    private String pluginVersion;

    private String pluginClassNameXML;

    private List runtime = new ArrayList();

    private List requires = new ArrayList();

    private List extensions = new ArrayList();

    private List extensionPoints = new ArrayList();

    public String getArchetypeId()
    {
        return archetypeId != null ? archetypeId : "";
    }


    public void setArchetypeId(String archetypeId)
    {
        this.archetypeId = archetypeId;
        firePropertyChange("archetypeId", archetypeId);
    }


    public ExtensionPointModel[] getExtensionPoints()
    {
        return (ExtensionPointModel[])extensionPoints
            .toArray(new ExtensionPointModel[extensionPoints.size()]);
    }


    public void setExtensionPoints(List extensionPoints)
    {
        this.extensionPoints = extensionPoints;
    }


    public void addExtensionPoint(ExtensionPointModel extensionPoint)
    {
        this.extensionPoints.add(extensionPoint);
        firePropertyChange("extensionPoint", extensionPoint);
    }


    public void removeExtensionPoint(ExtensionPointModel extensionPoint)
    {
        this.extensionPoints.add(extensionPoint);
        firePropertyChange("extensionPoint", extensionPoint);
    }


    public ExtensionModel[] getExtensions()
    {
        return (ExtensionModel[])extensions
            .toArray(new ExtensionModel[extensions.size()]);
    }


    public void setExtensions(List extensions)
    {
        this.extensions = extensions;
    }


    public void addExtension(ExtensionModel extension)
    {
        this.extensions.add(extension);
        firePropertyChange("extension", extension);
    }


    public void removeExtension(ExtensionModel extension)
    {
        this.extensions.remove(extension);
        firePropertyChange("extension", extension);
    }


    public String getPluginClassName()
    {
        return pluginClassName != null ? pluginClassName : "";
    }


    public void setPluginClassName(String pluginClassName)
    {
        this.pluginClassName = pluginClassName;
        firePropertyChange("pluginClassName", pluginClassName);
    }


    public String getPluginClassNameXML()
    {
        return pluginClassNameXML != null ? pluginClassNameXML : "";
    }


    public void setPluginClassNameXML(String pluginClassNameXML)
    {
        this.pluginClassNameXML = pluginClassNameXML;
        firePropertyChange("pluginClassNameXML", pluginClassNameXML);
    }


    public String getPluginId()
    {
        return pluginId != null ? pluginId : "";
    }


    public void setPluginId(String pluginId)
    {
        this.pluginId = pluginId;
        firePropertyChange("pluginId", pluginId);
    }


    public String getPluginName()
    {
        return pluginName != null ? pluginName : "";
    }


    public void setPluginName(String pluginName)
    {
        this.pluginName = pluginName;
        firePropertyChange("pluginName", pluginName);
    }


    public String getPluginPackagePath()
    {
        return pluginPackagePath != null ? pluginPackagePath : "";
    }


    public void setPluginPackagePath(String pluginPackagePath)
    {
        this.pluginPackagePath = pluginPackagePath;
        firePropertyChange("pluginPackagePath", pluginPackagePath);
    }


    public String getPluginProviderName()
    {
        return pluginProviderName != null ? pluginProviderName : "";
    }


    public void setPluginProviderName(String pluginProviderName)
    {
        this.pluginProviderName = pluginProviderName;
        firePropertyChange("pluginProviderName", pluginProviderName);
    }


    public String getPluginShortId()
    {
        return pluginShortId != null ? pluginShortId : "";
    }


    public void setPluginShortId(String pluginShortId)
    {
        this.pluginShortId = pluginShortId;
        firePropertyChange("pluginShotId", pluginShortId);
    }


    public String getPluginVersion()
    {
        return pluginVersion != null ? pluginVersion : "";
    }


    public void setPluginVersion(String pluginVersion)
    {
        this.pluginVersion = pluginVersion;
        firePropertyChange("pluginVersion", pluginVersion);
    }


    public ImportModel[] getRequires()
    {
        return (ImportModel[])requires
            .toArray(new ImportModel[requires.size()]);
    }


    public void setRequires(List requires)
    {
        this.requires = requires;
    }


    public void addRequire(ImportModel importModel)
    {
        this.requires.add(importModel);
        firePropertyChange("requires", importModel);
    }


    public void removeRequire(ImportModel importModel)
    {
        this.requires.remove(importModel);
    }


    public LibraryModel[] getRuntime()
    {
        return (LibraryModel[])runtime
            .toArray(new LibraryModel[runtime.size()]);
    }


    public void setRuntime(List runtime)
    {
        this.runtime = runtime;
    }


    public void addRuntime(LibraryModel library)
    {
        this.runtime.add(library);
        firePropertyChange("runtime", library);
    }

    public void removeRuntime(LibraryModel library)
    {
        this.runtime.remove(library);
        firePropertyChange("runtime", library);
    }

    public String getTestEnviromentVersion()
    {
        return testEnviromentVersion != null ? testEnviromentVersion : "";
    }


    public void setTestEnviromentVersion(String testEnviromentVersion)
    {
        this.testEnviromentVersion = testEnviromentVersion;
        firePropertyChange("textEnviromentVersion", testEnviromentVersion);
    }


    public String getTestEnvironmentArtifactId()
    {
        return testEnvironmentArtifactId != null ? testEnvironmentArtifactId : "";
    }


    public void setTestEnvironmentArtifactId(String testEnvironmentArtifactId)
    {
        this.testEnvironmentArtifactId = testEnvironmentArtifactId;
        firePropertyChange("textEnviromentArtifactId", testEnvironmentArtifactId);
    }


    public String getTestEnvironmentGroupId()
    {
        return testEnvironmentGroupId != null ? testEnvironmentGroupId : "";
    }


    public void setTestEnvironmentGroupId(String testEnvironmentGroupId)
    {
        this.testEnvironmentGroupId = testEnvironmentGroupId;
        firePropertyChange("textEnvironmentGroupId", testEnvironmentGroupId);
    }

}
