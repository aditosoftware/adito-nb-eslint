package de.adito.aditoweb.nbm.eslint.action;

import org.jetbrains.annotations.NotNull;
import org.openide.filesystems.FileObject;
import org.openide.util.*;
import org.openide.util.actions.SystemAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Abstract action for all ESLint-Actions which require a FileObject
 *
 * @author s.seemann, 16.08.2021
 */
public abstract class ESLintAction extends SystemAction implements ContextAwareAction
{
  private FileObject fo;

  @Override
  public Action createContextAwareInstance(Lookup pContext)
  {
    if (pContext != null)
    {
      FileObject foLookup = pContext.lookup(FileObject.class);
      if (foLookup != null)
      {
        fo = foLookup;
        return this;
      }
    }
    fo = null;
    return null;
  }

  @Override
  public boolean isEnabled()
  {
    return super.isEnabled() && fo != null && fo.getExt().endsWith("js");
  }

  @Override
  public HelpCtx getHelpCtx()
  {
    return HelpCtx.DEFAULT_HELP;
  }

  @Override
  public void actionPerformed(ActionEvent ev)
  {
    if (fo != null)
      actionPerformed(fo);
  }

  /**
   * Performs the action
   *
   * @param pFo the selected FileObject
   */
  public abstract void actionPerformed(@NotNull FileObject pFo);
}
