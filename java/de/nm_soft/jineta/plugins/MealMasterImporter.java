package de.nm_soft.jineta.plugins;

import java.util.*;
import java.nio.charset.Charset;
import javax.swing.*;
import java.io.*;
import java.util.regex.*;

import de.nm_soft.jineta.*;

// Imports MealMaster-Data from a file
public class MealMasterImporter implements Plugin {

  public EPlugin getPluginType() {
    return EPlugin.IMPORT;
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

  static class PluginAction extends javax.swing.AbstractAction {

    static final long serialVersionUID = 2005200088;

    static final char hyphen = '-';
    static final char dot = '.';
    static final char comma = ',';
    static final char space = ' ';
    static final char slash = '/';
    static final char m = 'M';
    static final char eq = '=';

    static final String mMName = "MEAL-MASTER";
    static final String mMName2 = "REZKONV";
    static final String mMSpecial = "-----";
    static final String mMSpecialE = "=====";
    static final String mMSpecialM = "MMMMM";
    static final int mMSpecialLen = mMSpecial.length();
    static final String mMTitle = "TITLE: ";
    static final int mMTitleLen = mMTitle.length();
    static final String mMTitle2 = "TITEL: ";
    static final String mMCat = "CATEGORIES: ";
    static final String mMCat2 = "KATEGORIEN: ";
    static final int mMCatLen = mMCat.length();

    Pattern validAmount = Pattern.compile("^[ ]{1,6}[0-9]*[ \\.,/]?[0-9]*[\\.,/]?[0-9]*");
    Pattern validEnd = Pattern.compile("^[-]{4,38}-");
    Pattern validServings = Pattern.compile("^[0-9]+");

    VMainWindow mw;
    static JComboBox<String> cb = null;
    static SortedMap<String, Charset> chrs;
    Vector<Recipe> myRecipes = new Vector<Recipe>(100,10);

    PluginAction(VMainWindow owner) {
      super(VUtil.getLocale("vMenuFileImportMM"), VRes.loadIcon("Import"));
      mw = owner;
    }

    private boolean checkSpecialLine(String aStr) {
      return
        (aStr.startsWith(mMSpecial) || aStr.startsWith(mMSpecialM) ||
          aStr.startsWith(mMSpecialE));
    }

    private boolean checkHeaderLine(String aStr) {
      String s = aStr.toUpperCase();
      return
        (checkSpecialLine(s) &&
          (s.indexOf(mMName) >= 0 || s.indexOf(mMName2) >= 0));
    }

    private boolean checkTitleLine(String aStr) {
      String s = aStr.trim().toUpperCase();
      return s.startsWith(mMTitle) || s.startsWith(mMTitle2);
    }

    private boolean checkCategoriesLine(String aStr) {
      String s = aStr.trim().toUpperCase();
      return s.startsWith(mMCat.substring(0, mMCatLen-1)) ||
        s.startsWith(mMCat2.substring(0, mMCatLen-1));
    }

    private boolean checkServingsLine(String aStr) {
      return getServings(aStr) != null;
    }

    @SuppressWarnings("unused")
	private boolean checkEnd(String aStr) {
      return aStr.length() < 40 && validEnd.matcher(aStr).matches();
    }

    private String getTitle(String aStr) {
      return aStr.trim().substring(mMTitleLen);
    }

    private String getCategories(String aStr) {
      String s = aStr.trim();
      return s.length() > mMCatLen?s.substring(mMCatLen):null;
    }

    private String getServings(String aStr) {
      String sa[] = aStr.trim().split(" ", 3);
      return validServings.matcher(sa.length > 1?sa[1]:sa[0]).matches()?aStr.trim():null;
    }

    private boolean checkValidAmount(String aStr) {
      if (aStr == null || aStr.length() == 0)
        return false;
      return validAmount.matcher(aStr).matches();
    }

    private int checkIngredient(String aStr) {
      if (aStr.trim().length() > 0 && aStr.length() < 50) {
        if (aStr.length() > 20) {
          if (aStr.startsWith("                   "))
            return aStr.charAt(19) == hyphen?4:1;
          if (aStr.startsWith("           "))
            return aStr.charAt(11) == hyphen?aStr.charAt(12) == hyphen?4:2:1;
          if (checkValidAmount(aStr.substring(0, 7)))
            return 1;
        } else if (aStr.length() > 11) {
          if (aStr.startsWith("           "))
            return aStr.charAt(11) == hyphen?2:1;
          if (checkValidAmount(aStr.substring(0, 7)))
            return 1;
        } else if (checkSpecialLine(aStr)) {
          return 3;
        }
      }
      return 0;
    }

    private String getIngredient(String aStr, int n) {
      switch (n) {
      case 1:
      case 3:
        return strNormalize(aStr);
      case 2:
        return strNormalize(aStr.trim().substring(1));
      default: //case 4:
        return strNormalize(aStr.trim().substring(2));
      }
    }

    @SuppressWarnings("unused")
	private String getIngredient(String aStr) {
      return getIngredient(aStr, 1);
    }

    private String strNormalize(String aStr) {
      char l = space;
      String result = "";
      String s = aStr.trim();
      while (s.length() > 0) {
        if (l != space || s.charAt(0) != space) {
          l = s.charAt(0);
          result = result + l;
        }
        s = s.substring(1);
      }
      if (result.charAt(0) == ';')
        return result.substring(1);
      return result;
    }

    private String strCompress(String aStr) {
      String s = aStr;
      if (s == null || s.length() <= 5)
        return null;
      s = s.substring(mMSpecialLen);
      while (s.length() > 0 &&
        (s.charAt(0) == hyphen || s.charAt(0) == m || s.charAt(0) == eq))
        s = s.substring(1);
      while (s.length() > 0 &&
        (s.endsWith("-") || s.endsWith("M") || s.endsWith("=")))
        s = s.substring(0, s.length() - 1);
      return s;
    }

    static Box getPanel() {
      Box p = Box.createVerticalBox();
      p.add(Box.createGlue());
      p.add(Box.createGlue());
      p.add(Box.createGlue());
      p.add(Box.createGlue());
      p.add(Box.createGlue());
      p.add(Box.createGlue());
      Box b = Box.createHorizontalBox();
      b.add(Box.createGlue());
      b.add(new JLabel("Charset:"));
      b.add(Box.createGlue());
      p.add(b);
      chrs = Charset.availableCharsets();
      cb = new JComboBox<String>((String[])chrs.keySet().toArray());
      cb.setEditable(false);
      cb.setSelectedItem("ISO-8859-1");
      p.add(cb);
      p.add(Box.createGlue());
      p.add(Box.createGlue());
      p.add(Box.createGlue());
      p.add(Box.createGlue());
      p.add(Box.createGlue());
      p.add(Box.createGlue());
      return p;
    }

    public Charset getSelectedCharset() {
      return chrs != null?chrs.get(getSelected()):null;
    }

    public String getSelected() {
      return (String)cb.getSelectedItem();
    }

    LineNumberReader inFile = null;

    private int error(int aLine, String aLineS) {
      int res = JOptionPane.showConfirmDialog(mw,
        "MM File-Format-Error at Line: " + aLine + "\n\"" + aLineS + "\"\nTry Continue?",
        "MM Import-Error",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.ERROR_MESSAGE);
      if (res != JOptionPane.YES_OPTION && inFile != null)
        try { inFile.close(); } catch (Exception ex) {};
      return res;
    }

    private void finish(Recipe r) {
      String s = r.getProcedure();
      if (s != null) {
        if (!s.startsWith("<A>"))
          s = "<A>" + s;
        if (!s.endsWith("</A>"))
          s += "</A>";
        if (s.endsWith("<A>"))
          s = s.substring(0, s.length()-3);
        r.setProcedure(s.replace("<A></A>", ""));
      }
      s = r.getComment();
      if (s != null) {
        if (!s.startsWith("<A>"))
          s = "<A>" + s;
        if (!s.endsWith("</A>"))
          s += "</A>";
        if (s.endsWith("<A>"))
          s = s.substring(0, s.length()-3);
        r.setComment(s.replace("<A></A>", ""));
      }
      if (r.getCategory() == null)
        r.setCategory(Recipe.CAT_EMPTY);
      myRecipes.add(r);
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      JFileChooser fc = VUtil.getFileChooser("mm", "ffMM");
      fc.setAcceptAllFileFilterUsed(true);
      fc.setAccessory(getPanel());
      if (fc.showOpenDialog(mw) == JFileChooser.APPROVE_OPTION) {
        Charset cs = getSelectedCharset();
        if (cs == null)
          return;
        LineNumberReader inFile = null;
        try {
          inFile =
            new LineNumberReader(
            new InputStreamReader(
            new ProgressMonitorInputStream(mw, VUtil.getLocale("status_Loading"),
            new FileInputStream(fc.getSelectedFile())), cs));
          String line = null;
          Recipe r = null;
          int sect = 0;
          do {
            line = inFile.readLine();
            if (line == null) break;
            while (line.length() > 0 && line.endsWith(" "))
              line = line.substring(0, line.length()-1);
            if (line.length() > 0) {
              if (checkSpecialLine(line)) {
                switch (sect) {
                case 0:
                  if (checkHeaderLine(line)) {
                    r = new RecipeImpl(ERecipe.RECIPE);
                    sect++;
                  } else {
                    if (error(inFile.getLineNumber(), line) != JOptionPane.YES_OPTION)
                      return;
                  }
                  break;
                case 4:
                  if (line.length() < 40) {
                    finish(r);
                    r = null;
                    sect = 0;
                  } else {
                    line = strCompress(line);
                    if (line.equals("REF")) {
                      r.setComment(r.getComment() + "<A><F>" + line + "</F></A>");
                      sect++;
                    } else
                      r.addIngredient("<F>" + line + "</F>");
                  }
                  break;
                case 6:
                  if (line.length() < 40) {
                    finish(r);
                    r = null;
                    sect = 0;
                  } else {
                    line = strCompress(line);
                    r.setProcedure(r.getProcedure() + "<A><F>" + line + "</F></A>");
                  }
                  break;
                }
              } else {
                switch (sect) {
                case 1:
                  if (checkTitleLine(line)) {
                    r.setName(getTitle(line));
                    sect++;
                  } else {
                    if (error(inFile.getLineNumber(), line) != JOptionPane.YES_OPTION)
                      return;
                  }
                  break;
                case 2:
                  if (checkCategoriesLine(line)) {
                    r.setCategory(getCategories(line));
                    sect++;
                  } else {
                    if (error(inFile.getLineNumber(), line) != JOptionPane.YES_OPTION)
                      return;
                  }
                  break;
                case 3:
                  if (checkServingsLine(line)) {
                    r.setComment("<A>" + getServings(line) + "</A>");
                    sect++;
                  } else {
                    error(inFile.getLineNumber(), line); return;
                  }
                  break;
                case 4:
                  int err = checkIngredient(line);
                  switch (err) {
                  case 0:
                    if (r.getProcedure() == null)
                      r.setProcedure("<A>" + line);
                    else
                      r.setProcedure(r.getProcedure() + "<A>" + line);
                    sect += 2;
                    break;
                  case 1:
                    r.addIngredient(getIngredient(line, err));
                    break;
                  case 2:
                  case 4:
                    if (r.getIngredients().size() > 0)
                      r.getIngredients().setElementAt(
                        r.getIngredients().get(r.getIngredients().size()-1) + " " +
                        getIngredient(line, err),
                        r.getIngredients().size()-1);
                    else
                      r.addIngredient(getIngredient(line, err));
                    break;
                  case 3:
                    r.addIngredient("<F>" + strCompress(line) + "</F>");
                    break;
                  }
                  break;
                case 5:
                  if (r.getComment() == null)
                    r.setComment(line.trim());
                  else
                    r.setComment(r.getComment() + line.trim());
                  break;
                case 6:
                  if (line.charAt(0) == ':' || line.charAt(0) == '#') {
                    if (line.charAt(0) == '#')
                      line = line
                        .replace("#AT ", "#Autor: ")
                        .replace("#D ", "#Datum: ")
                        .replace("#NI ", "#")
                        .replace("#NO ", "#");
                    if (r.getComment() == null)
                      r.setComment("<A>" + line.trim().substring(1) + "</A>");
                    else
                      r.setComment(r.getComment() + "<A>" + line.trim().substring(1) + "</A>");
                  } else
                    if (r.getProcedure() == null)
                      r.setProcedure(line.trim());
                    else
                      r.setProcedure(r.getProcedure() + " " + line.trim());
                  break;
                }
              }
            } else if (sect == 5)
              sect++;
            else if (sect == 6)
              r.setProcedure(r.getProcedure() + "</A><A>");
          } while (true);
        } catch (Exception ex) {
          VError.writeErrorLog(ex);
          mw.getToolkit().beep();
        } finally {
        	if(inFile != null){
				try {
					inFile.close();
				} catch (IOException ex) {
					VError.writeErrorLog(ex);
				}
        	}
        }
        
        mw.recipes.addAll(myRecipes);
        mw.setModified(true);
        mw.relist(true);
      }
      return;
   }
  }
}

