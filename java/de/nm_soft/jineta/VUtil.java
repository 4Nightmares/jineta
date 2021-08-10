package de.nm_soft.jineta;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.util.jar.*;
import java.util.zip.*;

public class VUtil {

  /** Refernce to localization strings */
  private static final PropertyResourceBundle lang =
    (PropertyResourceBundle)PropertyResourceBundle.getBundle("lang/jineta");

  /** get a locale String */
  public static final String getLocale(String aId) { return lang.getString(aId); }

  /** get a JFileChooser for Extension aExt and Description aDescription from locale */
  public static final JFileChooser getFileChooser(String aExt, String aDescription) {
    JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setMultiSelectionEnabled(false);
    fc.setFileHidingEnabled(true);
    fc.addChoosableFileFilter(new SimpleFileFilter(aExt, lang.getString(aDescription)));
    fc.setAcceptAllFileFilterUsed(false);
    fc.setFileView(
      new FileView() {
        public Icon getIcon(File f) {
          if (!f.isDirectory() && f.getName().endsWith(".xml"))
            return VRes.loadIcon("BookShelf");
          else
            return null;
        }
      }
    );
    return fc;
  }

  private static void doCopy(InputStream fi, OutputStream fo) {
    try {
      byte buf[] = new byte[4096];
      int len;
      while ((len = fi.read(buf)) >= 0) {
        fo.write(buf, 0, len);
      }
    } catch (Exception ex) {
      VError.writeErrorLog(ex);
    }
  }

  /** Used to move one file to another
    * on local file system.
    */
  public static void moveFile(File fi, File fo) {
    FileOutputStream fos = null;
    FileInputStream fis = null;
    try {
      fos = new FileOutputStream(fo);
      fis = new FileInputStream(fi);
    } catch (Exception ex) {
      VError.writeErrorLog(ex);
    }
    doCopy(fis, fos);
    try {
      fis.close();
      fos.close();
      fi.delete();
    } catch (Exception ex) {
      VError.writeErrorLog(ex);
    }
  }

  /** Used to move one file to another
    * on local file system.
    */
  public static void unpackFile(File fi, File fo) {
    JarOutputStream fos = null;
    GZIPInputStream fis = null;
    try {
      fis = new GZIPInputStream(new FileInputStream(fi));
      fos = new JarOutputStream(new FileOutputStream(fo));
      Pack200.newUnpacker().unpack(fis, fos);
      fis.close();
      fos.flush();
      fos.close();
      fi.delete();
    } catch (Exception ex) {
      VError.writeErrorLog(ex);
    }
  }

  /** Used to extract (temporary) files needed from jineta.jar
    * to local file system.
    */
  public static void copyStream(InputStream fi, OutputStream fo) {
    doCopy(fi, fo);
    try {
      fo.flush();
      fo.close();
    } catch (Exception ex) {
      VError.writeErrorLog(ex);
    }
  }

  /** Used to extract (temporary) files needed from jineta.jar
    * to local file system.
    */
  public static void copyStream(InputStream fi, File f) {
   FileOutputStream fo;
   try {
      f.createNewFile();
      fo = new FileOutputStream(f);
      copyStream(fi, fo);
    } catch (Exception ex) {
      VError.writeErrorLog(ex);
    }
  }

  /** Used to extract (temporary) files needed from jineta.jar
    * to local file system. The stream is returned so it may be appended.
    */
  public static FileOutputStream copyStreamGet(InputStream fi, File f) {
   FileOutputStream fo;
   try {
      f.createNewFile();
      fo = new FileOutputStream(f);
      doCopy(fi, fo);
      return fo;
    } catch (Exception ex) {
      VError.writeErrorLog(ex);
    }
    return null;
  }

  static private class SimpleFileFilter extends javax.swing.filechooser.FileFilter {

    String myExt = null;
    String myDescription = null;

    public SimpleFileFilter(String aExt, String aDescription) {
      myExt = "." + aExt.toLowerCase();
      myDescription = aDescription;
    }

    public boolean accept(File f) {
      return f.isDirectory() || f.getName().toLowerCase().endsWith(myExt);
    }

    public String getDescription() {
      return myDescription + " (*" + myExt + ")";
    }
  }

}
