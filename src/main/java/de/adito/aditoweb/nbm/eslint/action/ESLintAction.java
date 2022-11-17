package de.adito.aditoweb.nbm.eslint.action;

import org.jetbrains.annotations.NotNull;
import org.netbeans.editor.BaseAction;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;

import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;

/**
 * Abstract action for all ESLint-Actions which require a FileObject
 *
 * @author s.seemann, 16.08.2021
 */
public abstract class ESLintAction extends BaseAction
{

  @Override
  public void actionPerformed(ActionEvent evt, JTextComponent target)
  {
    if(target != null)
    {
      FileObject fileObject = NbEditorUtilities.getFileObject(target.getDocument());
      if (fileObject != null)
        actionPerformed(target, fileObject);
    }
  }

  public abstract String iconResource();

  /**
   * Performs the action
   *
   * @param pFo the selected FileObject
   */
  public abstract void actionPerformed(@NotNull JTextComponent pTextComponent, @NotNull FileObject pFo);
}
