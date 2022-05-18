package de.adito.aditoweb.nbm.eslint.impl;

import com.google.gson.Gson;
import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import de.adito.aditoweb.nbm.nbide.nbaditointerface.javascript.node.*;
import de.adito.notification.INotificationFacade;
import org.jetbrains.annotations.NotNull;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.*;
import org.openide.filesystems.*;
import org.openide.util.BaseUtilities;

import java.io.ByteArrayOutputStream;
import java.util.logging.*;

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
  public void esLintAnalyze(@NotNull FileObject pFo)
  {
    LOGGER.log(Level.INFO, () -> "ESLint Analyzing " + pFo.getPath());
    _setup(pFo);
    try
    {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      executor.executeAsync(nodeJsEnv, _getExecBase(), output, null, null, FileUtil.toFile(pFo).getAbsolutePath(), "--format=json")
          .whenComplete(((pInteger, pThrowable) -> {
            if (pThrowable != null)
            {
              INotificationFacade.INSTANCE.error(pThrowable);
              _cleanup();
              return;
            }

            // the first line is the command, this line should be removed
            String result = output.toString().split("\n")[1];
            Gson gson = new Gson();

            ESLintResult[] esLintResult = gson.fromJson(result, ESLintResult[].class);
            if (esLintResult != null)
            {
              ESLintErrorDescriptionProvider.getInstance().publishErrors(esLintResult[0], pFo);
            }
            _cleanup();
          }));
    }
    catch (Exception pE)
    {
      INotificationFacade.INSTANCE.error(pE);
      _cleanup();
    }
  }

  @Override
  public void esLintFix(@NotNull FileObject pFo)
  {
    LOGGER.log(Level.INFO, () -> "ESLint Fixing " + pFo.getPath());
    _setup(pFo);
    try
    {
      executor.executeAsync(nodeJsEnv, _getExecBase(), new ByteArrayOutputStream(), null,
                            null, "--fix", FileUtil.toFile(pFo).getAbsolutePath())
          .whenCompleteAsync((pInteger, pThrowable) -> {
            if (pThrowable != null)
            {
              INotificationFacade.INSTANCE.error(pThrowable);
              _cleanup();
              return;
            }
            esLintAnalyze(pFo);
          });
    }
    catch (Exception pE)
    {
      INotificationFacade.INSTANCE.error(pE);
      _cleanup();
    }
  }

  private void _setup(@NotNull FileObject pFo)
  {
    if (handle == null)
    {
      handle = ProgressHandle.createHandle("Executing ESLint");
      handle.start();
      handle.switchToIndeterminate();
    }
    SaveUtil.saveUnsavedStates();
    Project project = FileOwnerQuery.getOwner(pFo);
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

  private void _cleanup()
  {
    if (handle != null)
    {
      handle.finish();
      handle.close();
      handle = null;
    }
  }

  @NotNull
  private INodeJSExecBase _getExecBase()
  {
    if (BaseUtilities.isWindows())
      return INodeJSExecBase.binary("eslint.cmd");
    return INodeJSExecBase.binary("eslint");
  }
}
