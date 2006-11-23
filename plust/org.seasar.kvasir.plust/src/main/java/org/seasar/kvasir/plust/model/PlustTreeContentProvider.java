/**
 * 
 */
package org.seasar.kvasir.plust.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import net.skirnir.xom.Element;
import net.skirnir.xom.Node;


/**
 * @author shida
 *
 */
public class PlustTreeContentProvider
    implements ITreeContentProvider
{

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parentElement)
    {
        if (parentElement.getClass().isArray()) {
            return (Object[])parentElement;
        }
        if (parentElement instanceof ExtensionModel) {
            ExtensionModel extension = (ExtensionModel)parentElement;
            return extension.getProperty();
        }
        if (parentElement instanceof Element) {
            Element element = (Element)parentElement;
            Node[] children = element.getChildren();
            List rv = new ArrayList();
            for (int i = 0; i < children.length; i++) {
                Node node = children[i];
                if (node.getType() == Node.ELEMENT) {
                    rv.add(node);
                }
            }
            return rv.toArray();
        }
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element)
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element)
    {
        if (element.getClass().isArray())
        {
            return true;
        }
        if (element instanceof ExtensionModel)
        {
            ExtensionModel model = (ExtensionModel)element;
            return model.getProperty().length != 0;
        }
        if (element instanceof Element)
        {
            Element e = (Element)element;
            return e.getChildren() != null;
        }
        return false;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement)
    {
        return getChildren(inputElement);
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {

    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {

    }

}
