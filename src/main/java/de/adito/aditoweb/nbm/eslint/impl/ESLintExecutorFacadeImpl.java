package de.adito.aditoweb.nbm.eslint.impl;

import com.google.gson.Gson;
import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import de.adito.aditoweb.nbm.nbide.nbaditointerface.javascript.node.*;
import de.adito.notification.INotificationFacade;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.*;
import org.openide.filesystems.*;
import org.openide.util.BaseUtilities;

import java.io.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IESLintExecutorFacade}
 *
 * @author s.seemann, 16.08.2021
 */
public class ESLintExecutorFacadeImpl implements IESLintExecutorFacade
{
  private static final Logger LOGGER = Logger.getLogger(ESLintExecutorFacadeImpl.class.getName());
  private INodeJSEnvironment nodeJsEnv;
  private INodeJSExecutor executor;
  private ProgressHandle handle;

  @Override
  public void esLintAnalyze(@NonNull FileObject pFo)
  {
    esLintAnalyze(List.of(FileUtil.toFile(pFo)))
        .whenComplete((pESLintResults, pThrowable) -> {
          if (pThrowable != null)
          {
            INotificationFacade.INSTANCE.error(pThrowable);
            cleanup();
            return;
          }

          try
          {
            // publish errors to document
            if (pESLintResults != null)
            {
              ESLintErrorDescriptionProvider.getInstance().publishErrors(pESLintResults[0], pFo);
            }
          }
          catch (Exception e)
          {
            INotificationFacade.INSTANCE.error(e);
          }
          finally
          {
            cleanup();
          }
        });
  }

  @Override
  @NonNull
  public CompletableFuture<ESLintResult[]> esLintAnalyze(@NonNull List<File> pFiles)
  {
    LOGGER.log(Level.INFO, () -> "ESLint Analyzing " + pFiles.stream()
        .map(File::getAbsolutePath)
        .collect(Collectors.joining(", ")));

    try
    {
      setup(pFiles);

      ByteArrayOutputStream output = new ByteArrayOutputStream();
      List<String> params = pFiles.stream()
          .map(File::getAbsolutePath)
          .collect(Collectors.toList());
      params.add(0, "--format=json");
      return executor.executeAsync(nodeJsEnv, getExecBase(), output, null, null, params.toArray(new String[0]))
          .thenApplyAsync((pInteger -> {
            try
            {
              // the first line is the command, this line should be removed
              String result = output.toString().split("\n")[1];
              Gson gson = new Gson();

              return gson.fromJson(result, ESLintResult[].class);
            }
            finally
            {
              cleanup();
            }
          }));
    }
    catch (Exception pE)
    {
      INotificationFacade.INSTANCE.error(pE);
      cleanup();
    }
    return CompletableFuture.completedFuture(new ESLintResult[0]);
  }

  @Override
  public void esLintFix(@NonNull FileObject pFo)
  {
    esLintFix(pFo, null);
  }

  @Override
  public void esLintFix(@NonNull FileObject pFo, @Nullable Runnable pExecuteAfterFix)
  {
    esLintFix(List.of(FileUtil.toFile(pFo)))
        .whenComplete((pInteger, pThrowable) -> {
          if (pThrowable != null)
          {
            INotificationFacade.INSTANCE.error(pThrowable);
            cleanup();
            return;
          }
          esLintAnalyze(pFo);
          if(pExecuteAfterFix != null)
            pExecuteAfterFix.run();
        });
  }

  @Override
  @NonNull
  public CompletableFuture<Integer> esLintFix(@NonNull List<File> pFiles)
  {
    LOGGER.log(Level.INFO, () -> "ESLint Fixing " + pFiles.stream()
        .map(File::getAbsolutePath)
        .collect(Collectors.joining(", ")));

    try
    {
      setup(pFiles);

      List<String> params = pFiles.stream()
          .map(File::getAbsolutePath)
          .collect(Collectors.toList());
      params.add(0, "--fix");
      return executor.executeAsync(nodeJsEnv, getExecBase(), new ByteArrayOutputStream(), null,
                                   null, params.toArray(new String[0]))
          .thenApplyAsync(pInteger -> {
            cleanup();
            return pInteger;
          });
    }
    catch (Exception pE)
    {
      INotificationFacade.INSTANCE.error(pE);
      cleanup();
    }
    return CompletableFuture.completedFuture(-1);
  }

  private void setup(@NonNull List<File> pFiles)
  {
    if (handle == null)
    {
      handle = ProgressHandle.createHandle("Executing ESLint");
      handle.start();
      handle.switchToIndeterminate();
    }
    SaveUtil.saveUnsavedStates();

    // find project
    Project project = null;
    for (File file : pFiles)
    {
      project = FileOwnerQuery.getOwner(FileUtil.toFileObject(file));
      if (project != null)
        break;
    }
    if (project == null)
      throw new IllegalStateException("No project found");

    executor = INodeJSExecutor.findInstance(project).orElse(null);
    INodeJSProvider provider = INodeJSProvider.findInstance(project).orElse(null);
    if (executor == null
        || provider == null
        || !provider.current().blockingFirst().isPresent())
      throw new IllegalStateException("No NodeJS found");

    nodeJsEnv = provider.current().blockingFirst().get(); // NOSONAR the check is in the if above
  }

  private void cleanup()
  {
    if (handle != null)
    {
      handle.finish();
      handle.close();
      handle = null;
    }
  }

  @NonNull
  private INodeJSExecBase getExecBase()
  {
    if (BaseUtilities.isWindows())
      return INodeJSExecBase.binary("eslint.cmd");
    return INodeJSExecBase.binary("eslint");
  }
}
