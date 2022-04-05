package de.adito.aditoweb.nbm.eslint.impl;

import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import de.adito.aditoweb.nbm.nbide.nbaditointerface.javascript.node.*;
import de.adito.notification.INotificationFacade;
import org.apache.commons.io.output.WriterOutputStream;
import org.jetbrains.annotations.NotNull;
import org.netbeans.api.project.*;
import org.openide.filesystems.*;
import org.openide.util.*;
import org.openide.windows.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of {@link IESLintExecutorFacade}
 *
 * @author s.seemann, 16.08.2021
 */
public class ESLintExecutorFacadeImpl implements IESLintExecutorFacade
{
  private final InputOutput output = IOProvider.getDefault().getIO("ESLint", false);
  private final OutputStream outputWriter;

  private INodeJSEnvironment nodeJsEnv;
  private INodeJSExecutor executor;

  public ESLintExecutorFacadeImpl()
  {
    outputWriter = new WriterOutputStream(output.getOut(), StandardCharsets.UTF_8, 128, true);
  }


  @Override
  public void esLintAnalyze(@NotNull FileObject pFo)
  {
    _setup(pFo);
    try
    {
      executor.executeAsync(nodeJsEnv, _getExecBase(), outputWriter, null,
                            null, FileUtil.toFile(pFo).getAbsolutePath())
          .whenComplete((pInt, pThrowable) -> {
            output.getOut().println(NbBundle.getMessage(ESLintExecutorFacadeImpl.class, "LBL_OUTPUT_FINISHED"));
            try
            {
              outputWriter.close();
            }
            catch (IOException pE)
            {
              // TODO
              pE.printStackTrace();
            }
          });
    }
    catch (IOException pE)
    {
      INotificationFacade.INSTANCE.error(pE);
    }
  }

  @Override
  public void esLintFix(@NotNull FileObject pFo)
  {
    _setup(pFo);
    try
    {
      executor.executeAsync(nodeJsEnv, _getExecBase(), outputWriter, null,
                            null, "--fix", FileUtil.toFile(pFo).getAbsolutePath())
          .whenComplete((pInt, pThrowable) -> output.getOut().println(NbBundle.getMessage(ESLintExecutorFacadeImpl.class, "LBL_OUTPUT_FINISHED")));
    }
    catch (IOException pE)
    {
      INotificationFacade.INSTANCE.error(pE);
    }
  }

  private void _setup(@NotNull FileObject pFo)
  {
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

    try
    {
      output.getOut().reset();
    }
    catch (IOException pE)
    {
      INotificationFacade.INSTANCE.error(pE);
    }
    output.select();
    output.getOut().println(NbBundle.getMessage(ESLintExecutorFacadeImpl.class, "LBL_OUTPUT_STARTING"));
  }

  @NotNull
  private INodeJSExecBase _getExecBase()
  {
    if (BaseUtilities.isWindows())
      return INodeJSExecBase.binary("eslint.cmd");
    return INodeJSExecBase.binary("eslint");
  }
}
