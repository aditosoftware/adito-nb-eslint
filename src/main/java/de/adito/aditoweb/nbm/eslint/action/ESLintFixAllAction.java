package de.adito.aditoweb.nbm.eslint.action;

import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import org.jetbrains.annotations.NotNull;
import org.openide.awt.*;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * Action for ESLint Fix
 *
 * @author s.seemann, 16.08.2021
 */
@ActionReferences({
    @ActionReference(path = "Editors/text/typescript/Toolbars/Default", position = 50000),
    @ActionReference(path = "Editors/text/javascript/Toolbars/Default", position = 50000)
})
@ActionID(category = "adito/eslint", id = "de.adito.aditoweb.nbm.eslint.action.ESLintFixAction")
@ActionRegistration(displayName = "#LBL_ESLintFixAllAction")
@NbBundle.Messages("LBL_ESLintFixAllAction=ESLint Fix All")
public class ESLintFixAllAction extends ESLintAction
{
  @Override
  public String getName()
  {
    return NbBundle.getMessage(ESLintFixAllAction.class, "LBL_ESLintFixAllAction");
  }

  @Override
  public void actionPerformed(@NotNull FileObject pFo)
  {
    IESLintExecutorFacade.getInstance().esLintFix(pFo);
  }
}
