package de.adito.aditoweb.nbm.eslint.options;

import lombok.NonNull;
import org.openide.util.NbPreferences;

import java.util.Objects;
import java.util.prefs.Preferences;

/**
 * Options for ESLint
 *
 * @author s.seemann, 16.05.2022
 */
public class ESLintOptions
{

  private static final Preferences PREFS = NbPreferences.forModule(ESLintOptions.class);

  private boolean onSave;

  public void setOnSave(boolean pOnSave)
  {
    onSave = pOnSave;
  }

  public boolean isOnSave()
  {
    return onSave;
  }

  @NonNull
  public static ESLintOptions getInstance()
  {
    ESLintOptions options = new ESLintOptions();
    options.onSave = Boolean.parseBoolean(PREFS.get("onSave", "false"));
    return options;
  }

  public static void update(@NonNull ESLintOptions pOptions)
  {
    PREFS.put("onSave", Boolean.toString(pOptions.isOnSave()));
  }

  @Override
  public boolean equals(Object pO)
  {
    if (this == pO) return true;
    if (!(pO instanceof ESLintOptions)) return false;
    ESLintOptions that = (ESLintOptions) pO;
    return onSave == that.onSave;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(onSave);
  }
}
