package de.adito.aditoweb.nbm.eslint.action;

import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import org.jetbrains.annotations.NotNull;
import org.openide.awt.*;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * Action for ESLint Analyze
 *
 * @author s.seemann, 16.08.2021
 */
@ActionReferences({
    @ActionReference(path = "Editors/text/typescript/Toolbars/Default", position = 49000),
    @ActionReference(path = "Editors/text/javascript/Toolbars/Default", position = 49000)
})
@ActionID(category = "adito/eslint", id = "de.adito.aditoweb.nbm.eslint.action.ESLintAnalyzeAction")
@ActionRegistration(displayName = "#ESLintAnalyzeAction")
@NbBundle.Messages("ESLintAnalyzeAction=ESLint Analyze")
public class ESLintAnalyzeAction extends ESLintAction
{
  @Override
  public String getName()
  {
    return NbBundle.getMessage(ESLintAnalyzeAction.class, "ESLintAnalyzeAction");
  }

  @Override
  public void actionPerformed(@NotNull FileObject pFo)
  {
    IESLintExecutorFacade.getInstance().esLintAnalyze(pFo);
  }
}
