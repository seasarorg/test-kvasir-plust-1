/**
 * 
 */
package org.seasar.kvasir.plust.form;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.seasar.kvasir.plust.model.ExtensionElementModel;
import org.seasar.kvasir.plust.model.ExtensionModel;

import net.skirnir.xom.Element;


/**
 * @author shida
 *
 */
public class ExtensionDetailsPageProvider
    implements IDetailsPageProvider
{

    private Map pageCache = new HashMap();


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IDetailsPageProvider#getPage(java.lang.Object)
     */
    public IDetailsPage getPage(Object key)
    {
        if (pageCache.containsKey(key)) {
            return (IDetailsPage)pageCache.get(key);
        }
        if (key instanceof ExtensionModel) {
            ExtensionModel extension = (ExtensionModel)key;
            ExtensionDetailsPage page = new ExtensionDetailsPage(extension, extension.getPoint());
            pageCache.put(key, page);
            return page;
        }
        if (key instanceof ExtensionElementModel) {
            ExtensionElementModel element = (ExtensionElementModel)key;
            ExtensionElementDetailsPage page = new ExtensionElementDetailsPage(element);
            pageCache.put(key, page);
            return page;
        }
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IDetailsPageProvider#getPageKey(java.lang.Object)
     */
    public Object getPageKey(Object object)
    {
        return object;
    }

}
