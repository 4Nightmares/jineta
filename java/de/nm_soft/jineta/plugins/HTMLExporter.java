package de.nm_soft.jineta.plugins;

import java.io.*;
import javax.swing.*;

import de.nm_soft.jineta.*;

public class HTMLExporter implements Plugin {

  public EPlugin getPluginType() {
    return EPlugin.EXPORT;
  }

  public boolean getWindowsOnly() {
    return ANYSYSTEM;
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
      super(VUtil.getLocale("vMenuFileExportHTML"), VRes.loadIcon("Export"));
      mw = owner;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {

      if (mw.active == null)
        return;

      JFileChooser fc = VUtil.getFileChooser("html", "ffHTML");

      File f = new File(mw.active.getName().replaceAll("(\\?|\\\\|\\/|\\||\\*|\\s)", "_"));
      fc.setSelectedFile(f);

      if (fc.showSaveDialog(mw) != JFileChooser.APPROVE_OPTION)
        return;

      String aFile = fc.getSelectedFile().getPath();
      if (!aFile.endsWith(".html"))
        aFile += ".html";
      try {
        PrintStream pw = new PrintStream(new FileOutputStream(aFile), false, "ISO-8859-1");
        pw.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        pw.print(mw.display.getText());
        pw.flush(); pw.close();
      } catch (Exception ex) {
        VError.writeErrorLog(ex);
      }
    }
  }
}
