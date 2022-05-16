package de.adito.aditoweb.nbm.eslint.api;

import de.adito.aditoweb.nbm.eslint.impl.ESLintExecutorFacadeImpl;
import org.jetbrains.annotations.NotNull;
import org.openide.filesystems.FileObject;

/**
 * Facade ESLintExecutor
 *
 * @author s.seemann, 16.08.2021
 */
public interface IESLintExecutorFacade
{
  IESLintExecutorFacade INSTANCE = new ESLintExecutorFacadeImpl();

  static IESLintExecutorFacade getInstance()
  {
    return INSTANCE;
  }

  /**
   * Executes the ESLint command
   *
   * @param pFo the FileObject, which should be analyzed
   */
  void esLintAnalyze(@NotNull FileObject pFo);

  /**
   * Executes the ESLint --fix command
   *
   * @param pFo the FileObject, which should be fixed
   */
  void esLintFix(@NotNull FileObject pFo);
}
