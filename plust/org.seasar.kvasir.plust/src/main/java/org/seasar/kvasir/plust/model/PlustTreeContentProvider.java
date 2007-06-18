/**
 * 
 */
package org.seasar.kvasir.plust.model;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.Messages;

import net.skirnir.xom.ValidationException;


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
            try {
                return extension.getRootElements();
            } catch (ValidationException e) {
                MessageDialog
                    .openWarning(
                        null,
                        Messages.getString("PlustTreeContentProvider.0"), Messages.getString("PlustTreeContentProvider.1") + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
                KvasirPlugin.getDefault().log(
                    Messages.getString("PlustTreeContentProvider.2"), e); //$NON-NLS-1$
            }
        }
        if (parentElement instanceof ExtensionElementModel) {
            ExtensionElementModel model = (ExtensionElementModel)parentElement;
            return model.getChildren();
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
        if (element.getClass().isArray()) {
            return true;
        }
        if (element instanceof ExtensionModel
            || element instanceof ExtensionElementModel) {
            return true;
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
