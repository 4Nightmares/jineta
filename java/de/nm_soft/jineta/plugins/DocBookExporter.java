package de.nm_soft.jineta.plugins;

import java.io.*;
import java.util.*;
import javax.swing.*;

import de.nm_soft.jineta.*;

public class DocBookExporter implements Plugin {

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
      super(VUtil.getLocale("vMenuFileExportDocBook"), VRes.loadIcon("Export"));
      mw = owner;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {

      final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
      final String head = "<!DOCTYPE article PUBLIC " +
        "\"-//OASIS//DTD DocBook XML V4.1.2//EN\" " +
        "\"http://www.oasis-open.org/docbook/xml/4.1.2/docbookx.dtd\">";

      if (mw.active == null)
        return;

      JFileChooser fc = VUtil.getFileChooser("xml", "ffDocBook");

      File f = new File(mw.active.getName().replaceAll("(\\?|\\\\|\\/|\\||\\*)", "_"));
      fc.setSelectedFile(f);

      if (fc.showSaveDialog(mw) != JFileChooser.APPROVE_OPTION)
        return;

      String aFile = fc.getSelectedFile().getPath();
      if (!aFile.endsWith(".xml"))
        aFile += ".xml";
      PrintWriter pw = null;
      File org = new File(aFile);
      File temp = null;
      try {
        temp = File.createTempFile("docbook", null, new File(org.getParent()));
        pw = new PrintWriter(temp, "UTF-8");

        de.nm_soft.jineta.Recipe r = mw.active;

        pw.println(xml); pw.println(head);
        pw.println("<article>");
        pw.println("  <sect1>");
        pw.println("    <title>" + encode(r.getName()) + "</title>");
        if (r.getCategory() != null) {
          pw.println("    <sect2>");
          pw.println("      <title>" + encode(r.getCategory()) + "</title>");
          pw.println("    </sect2>");
        }
        if (r.getTaste() != null) {
          pw.println("    <para>" + encode(r.getTaste()) + "</para>");
        }
        if (r.getIngredients() != null && r.getIngredients().size() > 0) {
          pw.println("    <sect2>");
          pw.println("      <title>" + VUtil.getLocale("disp_Ingredients") + "</title>");
          doList(pw, r.getIngredients());
          pw.println("    </sect2>");
        }
        if (r.getTools() != null && r.getTools().size() > 0) {
          pw.println("    <sect2>");
          pw.println("      <title>" + VUtil.getLocale("disp_Tools") + "</title>");
          doList(pw, r.getTools());
          pw.println("    </sect2>");
        }
        if (r.getDecoration() != null && r.getTools().size() > 0) {
          pw.println("    <sect2>");
          pw.println("      <title>" + VUtil.getLocale("disp_Decorations") + "</title>");
          doList(pw, r.getDecoration());
          pw.println("    </sect2>");
        }
        if (r.getProcedure() != null) {
          pw.println("    <sect2>");
          pw.println("      <title>" + VUtil.getLocale("disp_Procedure") + "</title>");
          pw.println(encode(r.getProcedure()));
          pw.println("    </sect2>");
        }
        if (r.getComment() != null) {
          pw.println("    <sect2>");
          pw.println("      <title>" + VUtil.getLocale("disp_Comments") + "</title>");
          pw.println(encode(r.getComment()));
          pw.println("    </sect2>");
        }
        pw.println("  </sect1>");
        pw.println("</article>");

        pw.flush();
        pw.close();
        org.delete();
        temp.renameTo(org);
      } catch (Exception ex) {
        VError.writeErrorLog(ex);
      }
    }

    private void doList(PrintWriter pw, Vector<String> l) {
      String ts = null;
      pw.println("<itemizedlist>");
      for (Enumeration<String> e = l.elements(); e.hasMoreElements(); ) {
        ts = e.nextElement();
        pw.println("  <listitem><para>" + encode(ts) + "</para></listitem>");
      }
      pw.println("</itemizedlist>");
    }

    private String encode(String s) {
      return s.replaceAll("<A>", "<para>").replaceAll("</A>", "</para>\n")
        .replaceAll("<(.|/.)>", "");
    }
  }
}
