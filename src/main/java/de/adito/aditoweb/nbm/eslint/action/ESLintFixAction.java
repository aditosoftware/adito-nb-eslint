package de.adito.aditoweb.nbm.eslint.action;

import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import org.jetbrains.annotations.NotNull;
import org.openide.awt.*;
import org.openide.filesystems.FileObject;

/**
 * Action for ESLint Fix
 *
 * @author s.seemann, 16.08.2021
 */
@ActionID(category = "adito/eslint", id = "de.adito.aditoweb.nbm.eslint.action.ESLintFixAction")
@ActionRegistration(displayName = "ESLintFixAction")
public class ESLintFixAction extends ESLintAction
{
  @Override
  public String getName()
  {
    return "ESLint Format";
  }

  @Override
  public void actionPerformed(@NotNull FileObject pFo)
  {
    IESLintExecutorFacade.getInstance().esLintFix(pFo);
  }
}
