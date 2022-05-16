package de.adito.aditoweb.nbm.eslint.impl;

/**
 * @author s.seemann, 12.05.2022
 */
public class ESLintResult
{
  private String filePath;
  private Message[] messages;
  private int errorCount;
  private int fatalErrorCount;
  private int warningCount;
  private int fixableErrorCount;
  private int fixableWarningCount;

  public String getFilePath()
  {
    return filePath;
  }

  public Message[] getMessages()
  {
    return messages;
  }

  public static class Message
  {
    private String ruleId;
    private int severity;
    private String message;
    private int line;
    private int column;
    private String nodeType;
    private String messageId;
    private int endLine;
    private int endColumns;
    private Fix fix;

    public String getRuleId()
    {
      return ruleId;
    }

    public String getMessage()
    {
      return message;
    }

    public int getSeverity()
    {
      return severity;
    }

    public int getLine()
    {
      return line;
    }

    public int getColumn()
    {
      return column;
    }

    public int getEndLine()
    {
      return endLine;
    }

    public int getEndColumns()
    {
      return endColumns;
    }

    public Fix getFix()
    {
      return fix;
    }
  }

  public static class Fix
  {
    private int[] range;
    private String text;

    public int getRangeStart()
    {
      return range[0];
    }

    public int getRangeEnd()
    {
      return range[1];
    }

    public String getText()
    {
      return text;
    }
  }
}
