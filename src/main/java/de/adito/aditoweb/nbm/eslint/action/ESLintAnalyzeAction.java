package de.adito.aditoweb.nbm.eslint.action;

import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import org.jetbrains.annotations.NotNull;
import org.openide.filesystems.FileObject;

/**
 * Action for ESLint Analyze
 *
 * @author s.seemann, 16.08.2021
 */
//@ActionID(category = "adito/eslint", id = "de.adito.aditoweb.nbm.eslint.action.ESLintAnalyzeAction")
//@ActionRegistration(displayName = "ESLintAnalyzeAction")
public class ESLintAnalyzeAction extends ESLintAction
{
  @Override
  public String getName()
  {
    return "ESLint Analyze";
  }

  @Override
  public void actionPerformed(@NotNull FileObject pFo)
  {
    IESLintExecutorFacade.getInstance().esLintAnalyze(pFo);
  }
}
