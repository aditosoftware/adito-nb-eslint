package de.adito.aditoweb.nbm.eslint.api;

import de.adito.aditoweb.nbm.eslint.impl.*;
import lombok.NonNull;
import org.jetbrains.annotations.*;
import org.openide.filesystems.FileObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
  void esLintAnalyze(@NonNull FileObject pFo);

  /**
   * Executes the ESLint command
   *
   * @param pFiles list of files, which should be analyzed
   * @return a future which returns the ESLintResult
   */
  @NonNull
  CompletableFuture<ESLintResult[]> esLintAnalyze(@NonNull List<File> pFiles);

  /**
   * Executes the ESLint --fix command
   *
   * @param pFo the FileObject, which should be fixed
   */
  void esLintFix(@NonNull FileObject pFo);

  /**
   * Executes the ESLint --fix command
   *
   * @param pFo the FileObject, which should be fixed
   * @param pExecuteAfterFix Runnable that will be executed after the fixes are applied
   */
  void esLintFix(@NonNull FileObject pFo, @Nullable Runnable pExecuteAfterFix);

  /**
   * Executes the ESLint --fix command
   *
   * @param pFiles list of files, which should be fixed
   * @return a future which returns the exit code of the execution
   */
  @NonNull
  CompletableFuture<Integer> esLintFix(@NonNull List<File> pFiles);
}
