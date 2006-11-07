package org.seasar.kvasir.eclipse;

import java.util.Locale;

import org.seasar.kvasir.util.collection.I18NPropertyHandler;

import net.skirnir.xom.I18NString;


public class PluginPropertyI18NString
    implements I18NString
{
    private I18NPropertyHandler handler_;

    private String name_;

    private boolean resolve_;


    public PluginPropertyI18NString(I18NPropertyHandler prop, String name)
    {
        handler_ = prop;
        name_ = name;
        if (name != null) {
            if (name.startsWith("%")) {
                name_ = name.substring(1);
                resolve_ = true;
            }
        }
    }


    public String getString(Locale locale)
    {
        if (resolve_) {
            return handler_.getProperty(name_, locale);
        } else {
            return name_;
        }
    }
}
