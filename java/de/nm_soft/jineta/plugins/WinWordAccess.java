package de.nm_soft.jineta.plugins;

import java.io.*;
import java.util.*;

import de.nm_soft.jineta.*;

// Simple Access to Winword
public class WinWordAccess implements Plugin {

  public EPlugin getPluginType() {
    return EPlugin.EXPORT;
  }

  public boolean getWindowsOnly() {
    return WINDOWSONLY;
  }

  public javax.swing.AbstractAction getAction(VMainWindow owner) {
    return new PluginAction(owner);
  }

  public javax.swing.KeyStroke getAccelerator() {
    return null;
  }

  class PluginAction extends javax.swing.AbstractAction {

    static final long serialVersionUID = 2005200088;

    VMainWindow mw;

    PluginAction(VMainWindow owner) {
      super(VUtil.getLocale("vMenuFileExportWord"), VRes.loadIcon("Export"));
      mw = owner;
    }

    private void putBookmark(PrintStream pw, String aBm, String aTxt) {
      if (aTxt != null)
        pw.println("setBookmarkText \"" + aBm + "\", \"" +
          aTxt.replaceAll("\"", "\"+Chr(34)+\"")
            .replaceAll("<A>", "")
            .replaceAll("</A>", "\n")
            .replaceAll("\n", "\"+Chr(13)+Chr(10)+\"") +
          "\"");
      else
        pw.println("setBookmarkText \"" + aBm + "\", \"\"");
    }

    private void putBookmark(PrintStream pw, String aBm, Vector<String> aList) {
      String l = null;
      String s = "";

      if (aList != null)
        for (Enumeration<String> en = aList.elements(); en.hasMoreElements(); ) {
          l = (String)en.nextElement();
          if (l != null) {
            l = l.replaceAll("\"", "\"+Chr(34)+\"");
            if (s != null && s.length() > 0)
              s = s + "\n" + l;
            else
              s = l;
          }
        }
      pw.println("setBookmarkText \"" + aBm + "\", \"" +
        s.replaceAll("\n", "\"+Chr(13)+Chr(10)+\"") + "\"");
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      final String LIBDIR = "libwin/";
      final String WordExportVBS = "WordExport.vbs";
      final String WordTemplateDOT = "WordTemplate.dot";

      Recipe r = mw.active;
      if (r == null)
        return;

      ClassLoader cl = getClass().getClassLoader();

      File f = new File(WordTemplateDOT);
      if (!f.exists()) {
        VUtil.copyStream(cl.getResourceAsStream(LIBDIR + WordTemplateDOT), f);
        f.deleteOnExit();
      }

      File f2 = new File(WordExportVBS);
      PrintStream pw = new PrintStream(VUtil.copyStreamGet(cl.getResourceAsStream(LIBDIR + WordExportVBS), f2));
      if (pw != null) {
        pw.println("Set doc = wordApp.Documents.add(\"" + f.getAbsolutePath() + "\")");
        putBookmark(pw, "name", r.getName());
        putBookmark(pw, "section", r.getCategory());
        putBookmark(pw, "taste", r.getTaste());
        putBookmark(pw, "ingredients", r.getIngredients());
        putBookmark(pw, "tools", r.getTools());
        putBookmark(pw, "decoration", r.getDecoration());
        putBookmark(pw, "procedure", r.getProcedure());
        putBookmark(pw, "hints", r.getComment());
        pw.close();
        try {
          Process p = Runtime.getRuntime().exec("WScript.exe \"" + f2.getAbsolutePath() + "\"");
          p.waitFor();
        } catch (Exception ex) {
          VError.writeErrorLog(ex);
        }
        f2.delete();
      }
    }
  }

}
