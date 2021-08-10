package de.nm_soft.jineta;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.SingleSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.undo.CannotUndoException;
//import javax.swing.tree.*;
import javax.swing.undo.UndoManager;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import de.nm_soft.jineta.plugins.Plugin;
import de.nm_soft.jineta.util.ImageFilter;
import de.nm_soft.jineta.util.ImageHelper;
import de.nm_soft.jineta.util.ImgPrev;
import de.nm_soft.jineta.util.ResourceList;

/**
 * Main Window of Jineta
 * @author NM-Soft (Frank Tusche)
 */
public class VMainWindow extends JFrame
  implements HyperlinkListener, ChangeListener, UndoableEditListener {

  static final long serialVersionUID = 2005200001;

  ClassLoader cl = null;

  /** The name of the (empty) template database. */
  static final String fileNew = "Empty.xml";

  /** True if application is running on Windows platform */
  boolean isWindows = false;

  /** The main JFileChooser component */
  JFileChooser fc = null;

  JPanel vMainContentPane;

  JButton vBtnHelp = new JButton(VRes.loadIcon("Help"));

  JPanel vMainStatusBar = new JPanel();
  JLabel vsbSize = new JLabel();
  JLabel vsbModified = new JLabel(VRes.loadIcon("BulbOff"));
  JLabel vsbMessage = new JLabel();

  BorderLayout vMainBorderLayout = new BorderLayout();
  TitledBorder vMainTitledBorder;

  Action vCut = new DefaultEditorKit.CutAction();
  Action vCopy = new DefaultEditorKit.CopyAction();
  Action vPaste = new DefaultEditorKit.PasteAction();
  AbstractAction vSearch;
  AbstractAction vSearchPlus;
  AbstractAction vOpen;
  AbstractAction vSave;

  JMenuBar vMainMenu = new JMenuBar();
  JMenu vMenuFile = new JMenu();
  JMenu vMenuHelp = new JMenu();
  JMenu vMenuEdit = new JMenu();
  JMenu vMenuExtras = new JMenu();
  JMenu vMenuFileExport = new JMenu();
  JMenu vMenuFileImport = new JMenu();

  JToolBar vMainToolBar = null;

  JPopupMenu cbCatMenu = new JPopupMenu();
  JPopupMenu cbRezMenu = new JPopupMenu();

  JTabbedPane tpType = new JTabbedPane();
  JPanel pRecipes = new JPanel();
  JComboBox<String> cbRCat = new JComboBox<String>();
  JList<Recipe> lRecipes = new JList<Recipe>();
  JComboBox<String> cbTCat = new JComboBox<String>();
  JList<Recipe> lTips = new JList<Recipe>();
  JComboBox<String> cbICat = new JComboBox<String>();
  JList<Recipe> lInformations = new JList<Recipe>();
  JList<Recipe> lSearchResults = new JList<Recipe>();
  JPanel pTips = new JPanel();
  JPanel pInformation = new JPanel();
  JPanel pSearchResults = new JPanel();

  /** The HTML display area */
  public JTextPane display = new JTextPane();

  /** Edit Page: name of recipe */
  public JTextField tfName = new JTextField();
  /** Edit Page: category of recipe */
  public JTextField tfCategory = new JTextField();
  /** Edit Page: taste of recipe */
  public JTextField tfTaste = new JTextField();
  /** Edit Page: "Contains Alcohol" */
  public JCheckBox tbAlcohol = new JCheckBox(VRes.loadIcon("Cancel"));
  /** Edit Page: */
  public JSpinner sNote = new JSpinner(new SpinnerNumberModel(0, 0, 6, 1));
  /** Edit Page: entry is a recipe */
  public JRadioButton rbRecipe = new JRadioButton(VRes.loadIcon("Book"));
  /** Edit Page: entry is a Tip */
  public JRadioButton rbTip = new JRadioButton(VRes.loadIcon("Book"));
  /** Edit Page: entry is an information */
  public JRadioButton rbInformation = new JRadioButton(VRes.loadIcon("Book"));
  /** Edit Page: ingredients */
  public JTextArea taIngredients = new JTextArea();
  /** Edit Page: tools */
  public JTextArea taTools = new JTextArea();
  /** Edit Page: decoration */
  public JTextArea taDecoration = new JTextArea();
  /** Edit Page: procedure */
  public JTextArea taProcedure = new JTextArea();
  /** Edit Page: comments */
  public JTextArea taComments = new JTextArea();
  /** Edit Page: image */
  JButton bImage = new JButton(VRes.loadIcon("Open"));
  JButton bAccept = new JButton(VRes.loadIcon("Ok"));
  JButton bCancel = new JButton(VRes.loadIcon("Cancel"));
  JButton bDelete = new JButton(VRes.loadIcon("Erase"));
  JButton bCalc = null;
  JPanel pEdit = new JPanel();

  JTabbedPane tpMain = new JTabbedPane();
  JToolBar toolbar = null;

  JSplitPane vDisplayArea;
/*
  JTree tree = new JTree();
  DefaultMutableTreeNode root = null;
  TreeMap<String, DefaultMutableTreeNode>
    tRoots = new TreeMap<String, DefaultMutableTreeNode>(),
    tTips = new TreeMap<String, DefaultMutableTreeNode>(),
    tInfos = new TreeMap<String, DefaultMutableTreeNode>(),
    tRecipes = new TreeMap<String, DefaultMutableTreeNode>();
*/
  /** True, if current recipe at Edit Page is a newly created */
  boolean isNew = false;
  /** Recipe database has been modified */
  boolean modified = false;

  Preferences properties;
  JinetaSettings settings;

  /** The recipe selected at the moment or null if none is currently selected. */
  public Recipe active = null;

  /** Filename of recipe database currently in memory */
  String loaded = fileNew;

  /** Contains references to all recipes in memory. */
  public Vector<Recipe> recipes = new Vector<Recipe>(2000,100);
  /** Currently existing recipe categories */
  TreeSet<String> rCategories = new TreeSet<String>();
  /** Currently existing tip categories */
  TreeSet<String> tCategories = new TreeSet<String>();
  /** Currently existing information categories */
  TreeSet<String> iCategories = new TreeSet<String>();

  String packageName = null;

  protected UndoManager undo = new UndoManager();
  protected VundoAction undoAction = new VundoAction();
  protected VredoAction redoAction = new VredoAction();

  /** create a new VMainWindow */
  public VMainWindow()
  {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      cl = getClass().getClassLoader();
      VMainWindowInit();
    }
    catch(Exception e) {
      VError.writeErrorLog(e);
    }
    System.setProperty("entityExpansionLimit", "999999999");
  }

  private void initPanel(JPanel aPanel, JComboBox<String> aCb, JList<Recipe> aList, ERecipe aType) {
    aPanel.setLayout(new BorderLayout());
    aCb.setEditable(false);
    aCb.addItemListener(new VcbRCat_ActionAdataper(aList, aType));
    aCb.addMouseListener(new cbRCat_MouseAdapter());
    aCb.setRenderer(new VcrCbList());
    aPanel.add(aCb, BorderLayout.NORTH);
    aList.setCellRenderer(new VcrRecipes());
    aList.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
    aList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    aList.addListSelectionListener(new VlslRecipes(aList));
    aList.addMouseListener(new lRecipes_MouseAdapter());
    aPanel.add(new JScrollPane(aList), BorderLayout.CENTER);
  }

  private void initPanel(JPanel aPanel, JList<Recipe> aList) {
    aPanel.setLayout(new BorderLayout());
    aList.setCellRenderer(new VcrRecipes());
    aList.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
    aList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    aList.addListSelectionListener(new VlslRecipes(aList));
    aPanel.add(new JScrollPane(aList), BorderLayout.CENTER);
  }

  private void insertLabeledTextField(String aLabel, JTextField aTa, JPanel aPanel, GridBagLayout gbl, GridBagConstraints gbc) {
    JLabel lbl = new JLabel(aLabel); lbl.setLabelFor(aTa);
    aPanel.add(lbl);
    gbc.weightx = 1.0; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
    gbl.setConstraints(lbl, gbc);
    aPanel.add(aTa);
    gbc.weightx = 5.0; gbc.gridwidth = GridBagConstraints.REMAINDER; gbc.anchor = GridBagConstraints.EAST;
    gbl.setConstraints(aTa, gbc);
    aTa.getDocument().addUndoableEditListener(this);
  }

  private void insertLabeledTextArea(String aLabel, JTextArea aTa, JPanel aPanel, GridBagLayout gbl, GridBagConstraints gbc) {
    aTa.setWrapStyleWord(true);
    aTa.setLineWrap(true);
    aTa.setFont(tfTaste.getFont());
    JLabel lbl = new JLabel(aLabel); lbl.setLabelFor(aTa);
    aPanel.add(lbl);
    gbc.weightx = 1.0; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
    gbl.setConstraints(lbl, gbc);
    JScrollPane sp = new JScrollPane(aTa);
    aPanel.add(sp);
    gbc.weightx = 9.0; gbc.gridwidth = GridBagConstraints.REMAINDER; gbc.anchor = GridBagConstraints.EAST;
    gbl.setConstraints(sp, gbc);
    aTa.getKeymap().removeKeyStrokeBinding(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
    aTa.getDocument().addUndoableEditListener(this);
  }

  private JToolBar newToolbar() {
    JToolBar toolbar = new JToolBar();
    toolbar.setEnabled(true);
    toolbar.setFloatable(false);
    return toolbar;
  }

  private void addToolbarButton(JButton aBtn, JToolBar aToolBar) {
    aBtn.setBorderPainted(false);
    aToolBar.add(aBtn);
  }

  private void addToolbarButton(JButton aBtn, JToolBar aToolBar, String aHint) {
    aBtn.setToolTipText(aHint);
    addToolbarButton(aBtn, aToolBar);
  }

  private JButton addToolbarButton(Action aAction, JToolBar aToolBar) {
    JButton aBtn = new JButton(aAction);
    aBtn.setToolTipText(aBtn.getText());
    aBtn.setText(null);
    addToolbarButton(aBtn, aToolBar);
    return aBtn;
  }

  private JButton addToolbarButton(Action aAction, JToolBar aToolBar, int keyBind) {
    JButton aBtn = addToolbarButton(aAction, aToolBar);
    aBtn.setMnemonic(keyBind);
    return aBtn;
  }


  @SuppressWarnings("unused")
private JButton addToolbarButton(Action aAction, JToolBar aToolBar, Icon aIcon) {
    JButton aBtn = new JButton(aAction);
    aBtn.setIcon(aIcon);
    aBtn.setText(null);
    addToolbarButton(aBtn, aToolBar);
    return aBtn;
  }

  @SuppressWarnings("unused")
private JButton addToolbarButton(Action aAction, JToolBar aToolBar, Icon aIcon, String aHint) {
    JButton aBtn = new JButton(aAction);
    aBtn.setIcon(aIcon);
    aBtn.setText(null);
    addToolbarButton(aBtn, aToolBar, aHint);
    return aBtn;
  }

  private void defineAction(Action aAction, Icon aIcon, String aName) {
    aAction.putValue(Action.SMALL_ICON, aIcon);
    aAction.putValue(Action.SHORT_DESCRIPTION, aName);
    aAction.putValue(Action.NAME, aName);
  }

  private File _DTD = null;

  private void extractFilesNeeded() throws Exception {
    final String myDTD = "Vineta.dtd";
    _DTD = new File(myDTD);
    InputStream is =  cl.getResourceAsStream(myDTD);
    if(is == null){
      throw new Exception("File " + myDTD + " not found");
    }
    VUtil.copyStream(cl.getResourceAsStream(myDTD), _DTD);
  }

  private void destroyFilesNeeded() {
    try {
      // Do not delete for now
      // TODO: Look at deletion of Vineta.dtd
      // The file is copied to WD as it is needed to verify the other files.
      // Deleting it at the and does not seem to be the best idea. Maybe there is a better way.
      //_DTD.delete();
    } catch (Exception e) {}
  }

  // Main Window initialization
  private void VMainWindowInit() throws Exception  {
    isWindows = System.getProperty("os.name").startsWith("Windows");
    packageName = getClass().getPackage().getName();
    // restore states
    settings = new JinetaSettings(this);
    properties = Preferences.userNodeForPackage(getClass());
    settings.load(properties);
    // some basics
    vMainContentPane = (JPanel)getContentPane();
    vMainTitledBorder = new TitledBorder(VinetaVersionInfo.getName());

    extractFilesNeeded();

    fc = VUtil.getFileChooser("xml", "ffVineta");

    // window size and title
    Dimension d = new Dimension(640, 480);
    setSize(d);
    setMinimumSize(d);
    setTitle(VinetaVersionInfo.getName());
    setIconImage(VinetaVersionInfo.getIcon().getImage());
    // actions
    defineAction(vCut, VRes.loadIcon("Cut"), VUtil.getLocale("vMenuEditCut"));
    defineAction(vCopy, VRes.loadIcon("Copy"), VUtil.getLocale("vMenuEditCopy"));
    defineAction(vPaste, VRes.loadIcon("Paste"), VUtil.getLocale("vMenuEditInsert"));
    // setup tree and display area
    // left side of window, pages
    initPanel(pRecipes, cbRCat, lRecipes, ERecipe.RECIPE);
    initPanel(pTips, cbTCat, lTips, ERecipe.TIP);
    initPanel(pInformation, cbICat, lInformations, ERecipe.INFORMATION);
    // left side of window, recipes-page
    tpType.addTab(VUtil.getLocale("root_recipes"), VRes.loadIcon("OpenBook"), pRecipes);
    // left side of window, tips-page
    tpType.addTab(VUtil.getLocale("root_tips"), VRes.loadIcon("OpenBook"), pTips);
    tpType.setSelectedIndex(0);
    // left side of window, informations-page
    tpType.addTab(VUtil.getLocale("root_information"), VRes.loadIcon("OpenBook"), pInformation);
    tpType.setSelectedIndex(0);

    initPanel(pSearchResults, lSearchResults);
    for (int i = 0; i < 3; i++)
      tpType.setDisabledIconAt(i, VRes.loadIcon("Book"));
    tpType.getModel().addChangeListener(this);
    // right part of window, view-page
    display.setEditable(false);
    display.setContentType("text/html; charset=UTF-8");
    display.setText(VUtil.getLocale("msg_Welcome"));
    display.addHyperlinkListener(this);
    vMainContentPane.setLayout(vMainBorderLayout);
    tpMain.addTab(VUtil.getLocale("tab_View"), VRes.loadIcon("View"), new JScrollPane(display));
    // right part of windows, edit-page
    // top part of edit-page
    JPanel top = new JPanel();
    top.setBorder(BorderFactory.createEtchedBorder());
    GridBagLayout gbl = new GridBagLayout();
    top.setLayout(gbl);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1.0;

    insertLabeledTextField(VUtil.getLocale("edit_Name"), tfName, top, gbl, gbc);

    tbAlcohol.setSelectedIcon(VRes.loadIcon("Ok"));
    tbAlcohol.setText(VUtil.getLocale("edit_Alcohol"));
    top.add(tbAlcohol);
    gbc.weightx = 1.0; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
    gbl.setConstraints(tbAlcohol, gbc);

    JLabel lbl = new JLabel(VUtil.getLocale("edit_Note")); lbl.setLabelFor(sNote);
    top.add(lbl);
    gbc.weightx = 0.5; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.CENTER;
    gbl.setConstraints(lbl, gbc);
    ((JSpinner.NumberEditor)sNote.getEditor()).getTextField().setEditable(false);
    top.add(sNote);
    gbc.weightx = 0.5; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.CENTER;
    gbl.setConstraints(sNote, gbc);
    JPanel p = new JPanel();
    p.setBorder(BorderFactory.createEtchedBorder());
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    ButtonGroup bg = new ButtonGroup();
    rbRecipe.setSelectedIcon(VRes.loadIcon("OpenBook")); rbRecipe.setText(VUtil.getLocale("root_recipes")); bg.add(rbRecipe);
    rbRecipe.addChangeListener(new VEditTypeChange());
    rbTip.setSelectedIcon(VRes.loadIcon("OpenBook")); rbTip.setText(VUtil.getLocale("root_tips")); bg.add(rbTip);
    rbInformation.setSelectedIcon(VRes.loadIcon("OpenBook"));
    rbInformation.setText(VUtil.getLocale("root_information")); bg.add(rbInformation); p.add(rbRecipe); p.add(rbTip); p.
    add(rbInformation); top.add(p); gbc.weightx = 4.0; gbc.gridwidth =
    GridBagConstraints.REMAINDER; gbc.anchor = GridBagConstraints.EAST; gbl.
    setConstraints(p, gbc);

    insertLabeledTextField(VUtil.getLocale("edit_Category"), tfCategory, top, gbl, gbc);
    insertLabeledTextField(VUtil.getLocale("edit_Taste"), tfTaste, top, gbl, gbc);
    // Image Selector
    
    JLabel lbl2 = new JLabel(VUtil.getLocale("edit_Image")); lbl2.setLabelFor(bImage);
    top.add(lbl2);
    gbc.weightx = 1.0; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
    gbl.setConstraints(lbl2, gbc);
    top.add(bImage);
    gbc.weightx = 5.0; gbc.gridwidth = GridBagConstraints.REMAINDER; gbc.anchor = GridBagConstraints.EAST;
    gbl.setConstraints(bImage, gbc);
    bImage.addActionListener(new bImage_Action());

    
    
    
    // middle part of edit-page
    JPanel bottom = new JPanel();
    bottom.setBorder(BorderFactory.createEtchedBorder());
    gbl = new GridBagLayout();
    bottom.setLayout(gbl);

    insertLabeledTextArea(VUtil.getLocale("edit_Ingredients"), taIngredients, bottom, gbl, gbc);
    insertLabeledTextArea(VUtil.getLocale("edit_Tools"), taTools, bottom, gbl, gbc);
    insertLabeledTextArea(VUtil.getLocale("edit_Decorations"), taDecoration, bottom, gbl, gbc);
    insertLabeledTextArea(VUtil.getLocale("edit_Procedure"), taProcedure, bottom, gbl, gbc);
    insertLabeledTextArea(VUtil.getLocale("edit_Comment"), taComments, bottom, gbl, gbc);
    // bottom part of edit-page
    toolbar = newToolbar();

    addToolbarButton(new VtbEditAccept_Action(), toolbar, KeyEvent.VK_ENTER);
    addToolbarButton(new VtbEditCancel_Action(), toolbar, KeyEvent.VK_BACK_SPACE);
    toolbar.addSeparator();
    addToolbarButton(new VtbEditNew_Action(), toolbar, KeyEvent.VK_F3);
    AbstractAction aRezDel = new VtbEditDelete_Action();
    addToolbarButton(aRezDel, toolbar, KeyEvent.VK_F12);
    toolbar.addSeparator();
    addToolbarButton(undoAction, toolbar);
    addToolbarButton(redoAction, toolbar);
    toolbar.addSeparator();
    addToolbarButton(new VtbEditBold_Action(), toolbar, 'F');
    addToolbarButton(new VtbEditItalic_Action(), toolbar, 'I');
    addToolbarButton(new VtbEditUnderline_Action(), toolbar, 'U');
    addToolbarButton(new VtbEditList_Action(), toolbar, 'L');
    addToolbarButton(new VtbEditNumbering_Action(), toolbar, 'N');
    toolbar.addSeparator();
    // Combine 3 edit sections
    pEdit.setLayout(new BorderLayout());
    pEdit.add(top, BorderLayout.NORTH);
    pEdit.add(bottom, BorderLayout.CENTER);
    pEdit.add(toolbar, BorderLayout.SOUTH);
    tpMain.addTab(VUtil.getLocale("tab_Edit"), VRes.loadIcon("Edit"), pEdit);
/*
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setShowsRootHandles(true);
//    tree.addTreeSelectionListener(this);
    tree.setRootVisible(false);
//    tree.setCellRenderer(new Renderer());
    JPanel pan = new JPanel();
    pan.setLayout(new BorderLayout());
    pan.add(new JScrollPane(tree), BorderLayout.CENTER);
    tpMain.addTab("Baum", VRes.loadIcon("OpenBook"), pan);
    buildTree();
*/
    // combine all three
    vDisplayArea = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tpType, tpMain);
    vMainContentPane.add(vDisplayArea, BorderLayout.CENTER);
    vDisplayArea.setDividerLocation(200);
    // Toolbar
    vMainToolBar = newToolbar();
    vSave = new VMenuFileSave_Action();
    vOpen = new VMenuFileOpen_Action();
    addToolbarButton(vOpen, vMainToolBar);
    addToolbarButton(vSave, vMainToolBar);
    vMainToolBar.addSeparator();
    vSearch = new VtbSearch_Action();
    vSearchPlus = new VtbSearchPlus_Action();
    addToolbarButton(vSearch, vMainToolBar);
    addToolbarButton(vSearchPlus, vMainToolBar);
    vMainToolBar.addSeparator();
    addToolbarButton(vCut, vMainToolBar);
    addToolbarButton(vCopy, vMainToolBar);
    addToolbarButton(vPaste, vMainToolBar);
    vMainToolBar.addSeparator();
    vMainContentPane.add(vMainToolBar, BorderLayout.NORTH);
    // Menu File
    vMenuFile.setText(VUtil.getLocale("vMenuFile")); vMenuFile.setMnemonic('D');
    vMenuFileExport.setText(VUtil.getLocale("vMenuFileExport"));
    vMenuFileImport.setText(VUtil.getLocale("vMenuFileImport"));
    // Menu Help
    vMenuHelp.setText(VUtil.getLocale("vMenuHelp")); vMenuHelp.setMnemonic('H');
    // Menu Edit
    vMenuEdit.setText(VUtil.getLocale("vMenuEdit")); vMenuEdit.setMnemonic('B');
    // Menu Extras
    vMenuExtras.setText(VUtil.getLocale("vMenuExtras")); vMenuExtras.setMnemonic('X');
    // setup menu
    JMenuItem mi = new JMenuItem(new VMenuFileNew_Action());
    mi.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK));
    vMenuFile.add(mi);
    mi = new JMenuItem(vOpen);
    mi.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_MASK));
    vMenuFile.add(mi);
    mi = new JMenuItem(vSave);
    mi.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK));
    vMenuFile.add(mi);
    vMenuFile.add(new JMenuItem(new VMenuFileSaveAs_Action()));
    vMenuFile.addSeparator();
    vMenuFile.add(vMenuFileImport);
    vMenuFile.add(vMenuFileExport);
    vMenuFile.addSeparator();
    mi = new JMenuItem(new VMenuFilePrint_Action());
    mi.setAccelerator(KeyStroke.getKeyStroke('P', InputEvent.CTRL_MASK));
    vMenuFile.add(mi);
    vMenuFile.addSeparator();
    mi = new JMenuItem(new VMenuFileExit_Action());
    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
    vMenuFile.add(mi);
    vMenuFileImport.add(new JMenuItem(new VMenuFileImportAdd_Action()));
    vMenuFileExport.add(new JMenuItem(new VMenuFileExportOld_Action()));
    vMenuFileExport.add(new JMenuItem(new VMenuFileExportSingle_Action()));
    mi = new JMenuItem(undoAction);
    mi.setAccelerator(KeyStroke.getKeyStroke('Z', InputEvent.CTRL_MASK));
    vMenuEdit.add(mi);
    mi = new JMenuItem(redoAction);
    vMenuEdit.add(mi);
    vMenuEdit.addSeparator();
    mi = new JMenuItem(vCut);
    mi.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
    vMenuEdit.add(mi);
    mi = new JMenuItem(vCopy);
    mi.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
    vMenuEdit.add(mi);
    mi = new JMenuItem(vPaste);
    mi.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK));
    vMenuEdit.add(mi);
    vMenuEdit.addSeparator();
    mi = new JMenuItem(new VtbEditSelectAll_Action());
    mi.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));
    vMenuEdit.add(mi);
    vMenuEdit.addSeparator();
    mi = new JMenuItem(vSearch);
    mi.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
    vMenuEdit.add(mi);
    mi = new JMenuItem(vSearchPlus);
    mi.setAccelerator(KeyStroke.getKeyStroke('H', InputEvent.CTRL_MASK));
    vMenuEdit.add(mi);
    vMenuEdit.addSeparator();

    loadPlugins();
    vMainToolBar.addSeparator();
    vBtnHelp = addToolbarButton(new VtbHelp_Action(), vMainToolBar);

    vMenuHelp.add(vBtnHelp.getAction());
    vMenuHelp.add(new JMenuItem(new VMenuHelpAbout_Action()));
    vMenuExtras.addSeparator();
    vMenuExtras.add(new JMenuItem(new Backup_Action()));
    vMenuExtras.add(new JMenuItem(new Restore_Action()));
    vMenuExtras.add(new JMenuItem(new VRestore_Action()));
    vMenuExtras.addSeparator();
    vMenuExtras.add(new JMenuItem(new VMenuExtrasSetup_Action()));
    vMenuExtras.add(new JMenuItem(new VMenuExtrasLaf_Action(this)));
    vMenuExtras.addSeparator();
    vMenuExtras.add(new JMenuItem(new Update_Action()));
    vMainMenu.add(vMenuFile);
    vMainMenu.add(vMenuEdit);
    vMainMenu.add(vMenuExtras);
    vMainMenu.add(vMenuHelp);
    this.setJMenuBar(vMainMenu);
    // setup PopupMenu ChangeCategory
    cbCatMenu.add(new JMenuItem(new VcbPMCC()));
    cbCatMenu.addSeparator();
    cbCatMenu.add(new JMenuItem(new VcbPMDC()));
    // setup PopupMenu Recipes
    cbRezMenu.add(new JMenuItem(aRezDel));
    // init statusbar
    vsbSize.setHorizontalAlignment(JLabel.RIGHT);
    vsbSize.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    d = new Dimension(80,20);
    vsbSize.setPreferredSize(d);
    vsbSize.setMinimumSize(d);
    vsbModified.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    d = new Dimension(20,20);
    vsbModified.setPreferredSize(d);
    vsbModified.setMinimumSize(d);
    vsbMessage.setHorizontalAlignment(JLabel.LEFT);
    vsbMessage.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    vMainStatusBar.setEnabled(true);
    vMainStatusBar.setBorder(BorderFactory.createEmptyBorder());
    BorderLayout bl = new BorderLayout();
    bl.setHgap(0);
    bl.setVgap(0);
    vMainStatusBar.setLayout(bl);
    JPanel sblp = new JPanel();
    sblp.setLayout(new BorderLayout());
    sblp.add(vsbSize, BorderLayout.WEST);
    sblp.add(vsbModified, BorderLayout.EAST);
    vMainStatusBar.add(sblp, BorderLayout.WEST);
    vMainStatusBar.add(vsbMessage, BorderLayout.CENTER);
    vMainContentPane.add(vMainStatusBar, BorderLayout.SOUTH);
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    // try restoring
    restore(properties);
    if (settings.saveTimer > 0) {
      java.util.Timer timer = new java.util.Timer("jineta sheduler", true);
      timer.schedule(new AutoSaver(), settings.saveTimer*60*1000, settings.saveTimer*60*1000);
    }
  }


  private boolean isPlugin(Class<?> c) {
    if (c == null || c.isInterface() || !Modifier.isPublic(c.getModifiers()))
      return false;
    Class<?> cls[] = c.getInterfaces();
    for (int i = 0; i < cls.length; i++)
      if (cls[i].getName().equals(Plugin.INTERFACE))
        return true;
    return false;
  }

  // load plugins
  private void loadPlugins() {
    try {
      Pattern p = Pattern.compile("^.*/" + Plugin.PACKAGE.replace(".", "/") + "/([A-Za-z0-9_]*)\\.class$");
      for (String file : ResourceList.getResources(p)) {
        Matcher m = p.matcher(file);
        m.find();
        String name = m.group(1);
        try {
          Class<?> c = null;
          try {
            c = Class.forName(Plugin.PACKAGE + "." + name);
          } catch (NoClassDefFoundError ex) {
          }
          if (c != null && isPlugin(c)) {
            Plugin pi = ((Plugin) c.newInstance());
            if (!pi.getWindowsOnly() || (pi.getWindowsOnly() && isWindows))
              switch (pi.getPluginType()) {
              case EXPORT:
                addPlugin(vMenuFileExport, pi);
                break;
              case IMPORT:
                addPlugin(vMenuFileImport, pi);
                break;
              case EDITTOOL:
                addPlugin(vMenuEdit, pi, toolbar);
                break;
              case TOOL:
                addPlugin(vMenuExtras, pi, vMainToolBar);
                break;
              default:
                VError.writeErrorLog("Unknown Plugin Type "
                    + pi.getPluginType() + " for " + name);
              }
          }
        } catch (Exception e) {
          VError.writeErrorLog(e);
        }
      }
    } catch (Exception e) {
      VError.writeErrorLog(e);
    }

  }

  // add a Plugin (for loadPlugins) to a JMenu
  private void addPlugin(JMenu aMenu, Plugin aPlugin) {
    addPlugin(aMenu, aPlugin, null);
  }

  // add a Plugin (for loadPlugins) to a JMenu and JToolBar
  private void addPlugin(JMenu aMenu, Plugin aPlugin, JToolBar aToolbar) {
    try {
      AbstractAction a = aPlugin.getAction(this);
      if (a != null) {
        JMenuItem mi = new JMenuItem(a);
        mi.setAccelerator(aPlugin.getAccelerator());
        aMenu.add(mi);
        if (aToolbar != null)
          addToolbarButton(mi.getAction(), aToolbar);
      }
    } catch (Exception e) {
      VError.writeErrorLog(e);
    }
  }
/*
  private void buildTree() {
    DefaultMutableTreeNode tn = null;
    root = new DefaultMutableTreeNode();

    tn = new DefaultMutableTreeNode(VUtil.getLocale("root_recipes"));
    root.add(tn);
    tRoots.put("root_recipes", tn);

    tn = new DefaultMutableTreeNode(VUtil.getLocale("root_tips"));
    root.add(tn);
    tRoots.put("root_tips", tn);

    tn = new DefaultMutableTreeNode(VUtil.getLocale("root_information"));
    root.add(tn);
    tRoots.put("root_information", tn);

    Recipe r = null;
    for (Enumeration<Recipe> e = (Enumeration<Recipe>)recipes.elements(); e.hasMoreElements(); ) {
      r = e.nextElement();
      TreeMap<String, DefaultMutableTreeNode> tm = null;
      DefaultMutableTreeNode dmtn = null;
      switch (r.getType()) {
        case Recipe.RECIPE:
          tm = tRecipes;
          dmtn = tRoots.get("root_recipes");
          break;
        case Recipe.TIP:
          tm = tTips;
          dmtn = tRoots.get("root_tips");
          break;
        case Recipe.INFORMATION:
          tm = tInfos;
          dmtn = tRoots.get("root_information");
          break;
      }
      if (tm.containsKey(r.getCategory())) {
        tn = tm.get(r.getCategory());
        tn.add(new DefaultMutableTreeNode(r));
      } else {
        tn = new DefaultMutableTreeNode(r.getCategory());
        tm.put(r.getCategory(), tn);
        dmtn.add(tn);
        tn.add(new DefaultMutableTreeNode(r));
      }
    }

    tree.setModel(new DefaultTreeModel(root));
  }
*/
  private void askSave() {
    if (modified) {
      if (JOptionPane.showConfirmDialog(this,
        VUtil.getLocale("dlg_AskSave"), VUtil.getLocale("msg_Alert"),
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
        save();
    }
    setModified(false);
  }

  // File | Exit action performed
  private void vMenuFileExit_actionPerformed(ActionEvent e) {
    store();
    askSave();
    destroyFilesNeeded();
    VError.closeErrorFile();
    System.exit(0);
  }

  // Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      vMenuFileExit_actionPerformed(null);
    }
  }

  private static final String propDivider = "Divider";
  private static final String propX = "X";
  private static final String propY = "Y";
  private static final String propWidth = "Width";
  private static final String propHeight = "Height";
  private static final String propFile = "File";
  private static final String propMode = "Mode";
  private static final String propCat = "Category";
  private static final String propSelectedR = "SelectedR";
  private static final String propSelectedT = "SelectedT";
  private static final String propSelectedI = "SelectedI";
  private static final String propCategoryR = "CategoryR";
  private static final String propCategoryT = "CategoryT";
  private static final String propCategoryI = "CategoryI";
  /** used internally */
  protected static final String propLaf = "LookAndFeel";
  private static final String propDir = "Directory";

  // restore saved settings
  private void restore(Preferences p) {
    if (p != null) {
      // Divider
      vDisplayArea.setDividerLocation(p.getInt(propDivider, 200));
      // X + Y + Width + Height
      setBounds(p.getInt(propX, 100),
        p.getInt(propY, 100),
        p.getInt(propWidth, 640),
        p.getInt(propHeight, 480));
      // restore last file used
      loaded = p.get(propFile, loaded);
//      validate();
    }
    loadXML(loaded);
    if (p != null) {
      selectRecipe(p.get(propSelectedR, null), p.get(propCategoryR, null),
        ERecipe.RECIPE);
      selectRecipe(p.get(propSelectedT, null), p.get(propCategoryT, null),
        ERecipe.TIP);
      selectRecipe(p.get(propSelectedI, null), p.get(propCategoryI, null),
        ERecipe.INFORMATION);
      int x = p.getInt(propMode, 0);
      if (x >= 2 || x < 0)
        x = 0;
      tpMain.setSelectedIndex(x);
      x = p.getInt(propCat, 0);
      if (x >= 3 || x < 0)
        x = 0;
      tpType.setSelectedIndex(x);
      String dir = properties.get(propDir, null);
      if (dir != null)
        fc.setCurrentDirectory(new File(dir));
    }
    setVisible(true);
    validate();
  }

  private void selectRecipe(Recipe r) {
    if (r == null) return;
    selectRecipe(r.getName(), r.getCategory(), r.getType());
  }

  private boolean isInCategory(String aCat, String aCatList) {
    if (aCatList != null) {
      String c[] = aCatList.split("[ ]*,[ ]*");
      for (int i = 0; i < c.length; i++)
        if (c[i].equalsIgnoreCase(aCat))
          return true;
    }
    return false;
  }

  private void selectRecipe(String aName, String aCat, ERecipe aType) {
    tpType.setSelectedIndex(aType.ordinal() - ERecipe.RECIPE.ordinal());
    Recipe r = null;
    if (aCat == null || aCat.length() == 0)
       aCat = Recipe.CAT_EMPTY;
    if (aName != null)
      for (Recipe e : recipes) {
        r = e;
        if (r != null && isInCategory(aCat, r.getCategory()) && aName.equals(r.getName()) && aType.equals(r.getType()))
          break;
      }
    if (r != null) {
      switch (aType) {
        case RECIPE:
          cbRCat.setSelectedItem(aCat);
          lRecipes.setSelectedValue(r, true);
          break;
        case TIP:
          cbTCat.setSelectedItem(aCat);
          lTips.setSelectedValue(r, true);
          break;
        case INFORMATION:
          cbICat.setSelectedItem(aCat);
          lInformations.setSelectedValue(r, true);
          break;
      }
    }
  }

  // save settings
  private void store() {
    // Divider
    properties.putInt(propDivider, vDisplayArea.getDividerLocation());
    // X
    properties.putInt(propX, this.getX());
    // Y
    properties.putInt(propY, this.getY());
    // Width
    properties.putInt(propWidth, this.getWidth());
    // Height
    properties.putInt(propHeight, this.getHeight());
    // last file used
    if (loaded != null)
      properties.put(propFile, loaded);
    // selected item
    Recipe r = lRecipes.getSelectedValue();
    if (r != null && r.getName() != null)
      properties.put(propSelectedR, r.getName());
    if (cbRCat.getSelectedItem() != null)
      properties.put(propCategoryR, (String)cbRCat.getSelectedItem());
    r = lTips.getSelectedValue();
    if (r != null && r.getName() != null)
      properties.put(propSelectedT, r.getName());
    if (cbTCat.getSelectedItem() != null)
      properties.put(propCategoryT, (String)cbTCat.getSelectedItem());
    r = lInformations.getSelectedValue();
    if (r != null && r.getName() != null)
      properties.put(propSelectedI, r.getName());
    if (cbICat.getSelectedItem() != null)
      properties.put(propCategoryI, (String)cbICat.getSelectedItem());
    // mode
    properties.putInt(propMode, tpMain.getSelectedIndex());
    // category
    properties.putInt(propCat, tpType.getSelectedIndex());
    // Look and Feel
    properties.put(propLaf, UIManager.getLookAndFeel().getClass().getName());
    // last directory of FileChooser
    properties.put(propDir, fc.getCurrentDirectory().getPath());

    settings.save(properties);
  }

  // shows a message in the statusbar
  private void displayMessage(String aMessage) {
    vsbMessage.setText(aMessage);
  }

  /** Recreate all lists and comboboxes. Update everything if rebuild is true. */
  public void relist(boolean rebuild) {
    if (rebuild) {
//      buildTree();
      rCategories.clear();
      tCategories.clear();
      iCategories.clear();
      Recipe r = null;
      for (Object e : recipes) {
        r = (Recipe)e;
        String cat = r.getCategory();
        if (r != null && r.getCategory() != null) {
          switch (r.getType()) {
            case RECIPE:
              addCategories(rCategories, cat);
              break;
            case TIP:
              addCategories(tCategories, cat);
              break;
            case INFORMATION:
              addCategories(iCategories, cat);
              break;
          }
        }
      }
    }
    Vector<Recipe> v = new Vector<Recipe>();
    lRecipes.setListData(v);
    lTips.setListData(v);
    lInformations.setListData(v);
    updateCb(cbRCat, rCategories);
    updateCb(cbTCat, tCategories);
    updateCb(cbICat, iCategories);
    if (tpType.getTabCount() > 3)
      tpType.removeTabAt(3);
    setDisplay();
  }

  private void addCategories(TreeSet<String> aList, String aItem) {
    int p;
    String s[] = aItem.split(",");
    for (int i = 0; i < s.length; i++) {
      s[i] = s[i].trim();
      if (!aList.contains(s[i])) aList.add(s[i]);
      p = s[i].indexOf(Recipe.SUB_CAT);
      if (p >= 0) {
        String c = s[i].substring(0, p);
        if (!aList.contains(c)) aList.add(c);
      }
    }
  }

  // loads an XML-file
  private void loadXML(String aFilename) {
    loadXML(new File(aFilename));
  }

  private void loadXML(File aFile) {
    int n;
    boolean ok = true;
    SAXParser sp = null;
    displayMessage(VUtil.getLocale("status_Loading"));
    n = recipes.size();
    XmlToRecipe sh = null;
    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setValidating(true);
      sp = spf.newSAXParser();
      sh = new XmlToRecipe();
      sh.mainWindow = this;
      ProgressMonitorInputStream pmis =
        new ProgressMonitorInputStream(this, VUtil.getLocale("status_Loading"),
          new FileInputStream(aFile));
      pmis.getProgressMonitor().setMillisToDecideToPopup(20);
      pmis.getProgressMonitor().setMillisToPopup(50);
      sp.parse(new BufferedInputStream(pmis), sh);
    } catch (Exception e) {
      //VError.writeErrorLog(e);
      displayMessage(VUtil.getLocale("status_DocError") + ": " + e.toString());
      ok = false;
      try {
        sp.parse(new BufferedInputStream(
          new ProgressMonitorInputStream(this, VUtil.getLocale("status_Loading"),
            cl.getResource(fileNew).openStream())),
            sh);
        ok = false;
        loaded = fileNew;
        setTitle(VinetaVersionInfo.getName() + " - " + loaded);
        displayMessage(VUtil.getLocale("status_Ok"));
      } catch (Exception ex) {
        VError.writeErrorLog(ex);
        displayMessage(VUtil.getLocale("status_DocError") + ": " + ex.toString());
      }
    }
    if (ok && sp != null) {
      loaded = aFile.getAbsolutePath();
      if (n <= 0)
        setTitle(VinetaVersionInfo.getName() + " - " + aFile.getName());
    }
    relist(sp != null);
    Runtime.getRuntime().gc();
    if (ok)
      displayMessage(VUtil.getLocale("status_Ok"));
  }

  // refills a ComboBox with new categories
  private void updateCb(JComboBox<String> aCb, TreeSet<String> aCat) {
    aCb.removeAllItems();
    for (String i : aCat ) {
      aCb.addItem(i);
    }
    try {
      aCb.setSelectedIndex(0);
    } catch (Exception e) {}
  }

  // clear all fields on edit-page
  private void clearEditPage() {
    tfName.setText(null);
    tfCategory.setText(null);
    tfTaste.setText(null);
    tbAlcohol.setSelected(false);
    sNote.setValue(new Integer(0));
    taIngredients.setText(null);
    taTools.setText(null);
    taDecoration.setText(null);
    taProcedure.setText(null);
    taComments.setText(null);
  }

  // shows the recipe if any selected
  private void setDisplay() {
    final String
      bFont = "font-family:'SansSerif',sans-serif; font-size:",
      bFont12 = bFont + settings.fontSize + "pt;",
      bFont14 = bFont + (settings.fontSize + 2) + "pt;",
      bFont18 = bFont + (settings.fontSize + 6) + "pt;",
      style =
        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" +
        "<HTML><HEAD>" +
        "<style type='text/css'>" +
        "H1 { " + bFont18 + " margin-left:0cm; margin-top:0cm; margin-bottom:0cm; }" +
        "H2 { " + bFont14 + " margin-left:0cm; margin-top:0.25cm; margin-bottom:0cm; }" +
        "P, B, I, U, BR { " + bFont12 + " margin-left:0.5cm; margin-top:0cm; }" +
        "DIV { " + bFont12 + " margin-left:0cm; margin-top:0cm; }" +
        "UL, OL, LI { " + bFont12 + " margin-left:0.5cm; margin-top:0cm; margin-bottom:0cm; padding-left:0.5cm; padding-right:0cm; }" +
        "</style>" +
        "</HEAD><BODY>";

    clearEditPage();

    String s = new String(style);
    if (active != null) {
      s += RecipeRenderer.render(active, loaded);
      tfName.setText(active.getName());
      tfCategory.setText(active.getCategory());
      tfTaste.setText(active.getTaste());
      tbAlcohol.setSelected(active.getAlcohol());
      sNote.setValue(new Integer(active.getNote()));
      switch (active.getType()) {
      case RECIPE:
        rbRecipe.setSelected(true);
        break;
      case TIP:
        rbTip.setSelected(true);
        break;
      case INFORMATION:
        rbInformation.setSelected(true);
        break;
      }
      if(active.getImageId() > 0) {
    	  bImage.setText("Id: " + active.getImageId().toString());
      } else {
    	  bImage.setText("");
      }

      appendVector(active.getIngredients(), taIngredients);
      appendVector(active.getTools(), taTools);
      appendVector(active.getDecoration(), taDecoration);
      if (active.getProcedure() != null)
        taProcedure.setText(active.getProcedure().replace("<A>", "")
            .replace("</A>", "\n"));
      taProcedure.setCaretPosition(0);
      if (active.getComment() != null)
        taComments.setText(active.getComment().replace("<A>", "")
            .replace("</A>", "\n"));
      taComments.setCaretPosition(0);

    } else {
      s += VUtil.getLocale("msg_Empty");
    }
    s += "</BODY></HTML>";
    display.setText(convertTags(s));
    display.setCaretPosition(0);
    if (tpType.getTabCount() == 3) {
      setTabAt(0, cbRCat);
      setTabAt(1, cbTCat);
      setTabAt(2, cbICat);
    }
    isNew = false;
    validate();
    lRecipes.repaint();
    lTips.repaint();
    lInformations.repaint();
    undo.discardAllEdits();
    undoAction.updateUndoState();
    redoAction.updateRedoState();
  }
  
  // append a Vector to a JTextArea
  private void appendVector(Vector<String> v, JTextArea ta) {
    if (v == null)
      return;
    String ts = null;
    for (Object e : v) {
      ts = (String)e;
      if (ts != null)
        ta.append(ts.concat("\n"));
    }
    ta.setCaretPosition(0);
  }

  private void setTabAt(int n, JComboBox<String> cb) {
    if (cb.getItemCount() == 0)
      tpType.setEnabledAt(n, false);
    else
      tpType.setEnabledAt(n, true);
  }

  // convert vineta-tags to html-tags
  private String convertTags(String aStr) {
    String s = aStr;
    s = s.replace("<A>", "<P>").replace("</A>", "</P>");
    s = s.replace("<F>", "<B>").replace("</F>", "</B>");
    s = s.replace("<K>", "<I>").replace("</K>", "</I>");
    s = s.replace("<NZ>", "<BR>");
    s = convertList(s, "L", "UL");
    s = convertList(s, "N", "OL");
    s = s.replaceAll("<(?=[0-9\\s<\\.,])", "&lt;");
    return s;
  }

  private String convertList(String aStr, String aTag, String aHTMLTag) {
    String s = aStr;
    int a, b;
    do {
      a = s.indexOf("<".concat(aTag).concat(">"));
      if (a >= 0) {
        s = s.substring(0, a)
          .concat("<").concat(aHTMLTag).concat(">")
          .concat(s.substring(a+3));
        b = s.indexOf("</".concat(aTag).concat(">"), a);
        if (b >= 0)
          s = s.substring(0, b)
            .concat("</").concat(aHTMLTag).concat(">")
            .concat(s.substring(b+4));
        else
          s += "</".concat(aHTMLTag).concat(">");
        String t;
        if (b >= 0)
          t = s.substring(a, b);
        else
          t = s.substring(a);
        t = t.replace("<P>", "<LI>").replace("</P>", "</LI>").replace("<BR>", "<LI>");
        if (b >= 0)
          s = s.substring(0, a) + t + s.substring(b);
        else
          s = s.substring(0, a) + t;
      }
      s = s.replaceAll("<P>\\s*<UL>", "<UL><LI>").replaceAll("<P>\\s*<OL>", "<OL><LI>");
      s = s.replaceAll("<LI>\\s*</UL>", "</UL><P>").replaceAll("<LI>\\s*</OL>", "</OL><P>");
    } while (a >= 0);
    return s;
  }

  // save current database
  private void save() {
    saveAs(loaded, true);
    modified = false;
  }

  // save recipes to a file
  // if no filename given, or "Empty.xml" then ask for a name
  private void saveAs(String aFile, boolean newFormat) {
    saveAs(aFile, newFormat, true);
  }

  // save active recipe to a file
  // if no filename given, or "Empty.xml" then ask for a name
  private void saveAs(boolean newFormat) {
    saveAs(null, newFormat, false);
  }

  // save all or just one recipe(s) to a file
  // if no filename given, or "Empty.xml" then ask for a name
  private void saveAs(String aFile, boolean newFormat, boolean all) {
    if (!all && active == null) return;
    RicipeToXML rr = new RicipeToXML();
    rr.nf = newFormat;
    if (aFile == null || aFile.endsWith(fileNew)) {
      if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
        return;
      aFile = fc.getSelectedFile().getPath();
      if (!aFile.endsWith(".xml"))
        aFile += ".xml";
      if (all) loaded = aFile;
    }
    displayMessage(VUtil.getLocale("status_Saving"));
    PrintWriter pw = null;
    File org = new File(aFile);
    File temp = null;
    try {
      temp = File.createTempFile("jineta", null, new File(org.getParent()));
      if (newFormat)
        pw = new PrintWriter(temp, "UTF-8");
      else {
        pw = new PrintWriter(temp, "ISO-8859-1");
      }
    } catch (Exception e) {
      VError.writeErrorLog(e);
    }
    if (pw != null) {
      pw.println("<?xml version=\"1.0\" encoding=\"" + (newFormat?"UTF-8":"ISO-8859-1") + "\"?>");
      pw.println("<!--");
      pw.println("#filetype:	Vineta Recipe Database");
      pw.println("#version:	" + (newFormat?"3.0":"2.0"));
      pw.println("-->");
      pw.println("<!DOCTYPE " + (newFormat?"Rezepte":"Cocktails") + " SYSTEM \"Vineta.dtd\">");
      pw.println(newFormat?"<Rezepte>":"<Cocktails>");
      ProgressMonitor pm = new ProgressMonitor(this, VUtil.getLocale("status_Saving"), aFile, 0, recipes.size());
      if (all) {
        int i = 0;
        for (Recipe e : recipes) {
          rr.writeRecipe(e, pw, newFormat, all);
          pm.setProgress(++i);
        }
      } else
        rr.writeRecipe(active, pw, newFormat, all);
      pw.println(newFormat?"</Rezepte>":"</Cocktails>");
      pw.flush();
      pw.close();
      org.delete();
      temp.renameTo(org);
    }
    if (all) setTitle(VinetaVersionInfo.getName() + " - " + org.getName());
    displayMessage(VUtil.getLocale("status_Ok"));
    setModified(false);
  }

  // get indenting string (spaces)
  
  private void setFormat(JTextComponent activeEdit, String aFormatId) {
    if (activeEdit != null) {
      String s = activeEdit.getSelectedText();
      if (s != null)
        activeEdit.replaceSelection("<" + aFormatId + ">" +
          activeEdit.getSelectedText() +
          "</" + aFormatId + ">");
      activeEdit.requestFocus();
    }
  }

  /** set status of modified and "modified" icon */
  public void setModified(boolean aValue) {
    modified = aValue;
    vsbModified.setIcon(modified?VRes.loadIcon("BulbOn"):VRes.loadIcon("BulbOff"));
  }

  /** get status of modified */
  public boolean getModified() {
    return modified;
  }

  /** Performs actions for hyperlink-clicks.
    */
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      JEditorPane pane = (JEditorPane) e.getSource();
      if (e instanceof HTMLFrameHyperlinkEvent) {
        HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
        HTMLDocument doc = (HTMLDocument)pane.getDocument();
        doc.processHTMLFrameHyperlinkEvent(evt);
      } else {
        try {
          pane.setPage(e.getURL());
        } catch (Throwable t) {
          VError.writeErrorLog(t);
        }
      }
    }
  }

  /** different tab selected (recipes, tips, informations, search results) */
  public void stateChanged(ChangeEvent e) {
    SingleSelectionModel model = (SingleSelectionModel) e.getSource();
    if (model == null) return;
    switch (model.getSelectedIndex()) {
      case 0:
        active = lRecipes.getSelectedValue();
        break;
      case 1:
        active = lTips.getSelectedValue();
        break;
      case 2:
        active = lInformations.getSelectedValue();
        break;
      case 3:
        active = lSearchResults.getSelectedValue();
    }
    setDisplay();
  }

  public void undoableEditHappened(UndoableEditEvent e) {
    undo.addEdit(e.getEdit());
    undoAction.updateUndoState();
    redoAction.updateRedoState();
  }

  /////////////////
  // Sub-Classes //
  /////////////////
  //
  // Actions, Handlers and Renderers
  //
  // recreate recipe-list if new category selected
  class VcbRCat_ActionAdataper implements ItemListener {
    JList<Recipe> l;
    ERecipe type;

    VcbRCat_ActionAdataper(JList<Recipe> aList, ERecipe aType) {
      l = aList;
      type = aType;
    }

    public void itemStateChanged(ItemEvent e) {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        String i = (String)e.getItem();
        if (i != null) {
        Vector<Recipe> v = new Vector<Recipe>();
          for (Recipe r : recipes) {
            if (r != null)
              if (r.getType() == type)
                if (r.getCategory() != null) {
                  if (isInCategory(i, r.getCategory()))
                    v.add(r);
                }
          }
          vsbSize.setText(String.valueOf(recipes.size()));
          Collections.sort(v);
          l.setListData(v);
        }
      }
    }
  }

  class cbRCat_MouseAdapter extends MouseAdapter {

    private void doIt(MouseEvent e) {
      if (e.isPopupTrigger()) {
        cbCatMenu.show(e.getComponent(), e.getX(), e.getY());
      }
    }

    public void mousePressed(MouseEvent e) {
      doIt(e);
    }

    public void mouseReleased(MouseEvent e) {
      doIt(e);
    }
  }

  class lRecipes_MouseAdapter extends MouseAdapter {

    
	private void doIt(MouseEvent e) {
      if (e.isPopupTrigger()) {
		JList<?> l = (JList<?>)e.getSource();
        if (l.getMinSelectionIndex() == l.getMaxSelectionIndex())
          l.setSelectedIndex(l.locationToIndex(e.getPoint()));
        cbRezMenu.show(e.getComponent(), e.getX(), e.getY());
      }
    }

    public void mousePressed(MouseEvent e) {
      doIt(e);
    }

    public void mouseReleased(MouseEvent e) {
      doIt(e);
    }
  }
  
  class bImage_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    bImage_Action() {
      super(VUtil.getLocale("vMenuFileOpen"), VRes.loadIcon("Open"));
    }

    public void actionPerformed(ActionEvent e) {
      
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setFileHidingEnabled(true);
        fc.addChoosableFileFilter(new ImageFilter());
        fc.setAccessory(new ImgPrev(fc));
         
      if (fc.showOpenDialog(VMainWindow.this) == JFileChooser.APPROVE_OPTION) {
        ImageHelper.copyFile(fc.getSelectedFile(), loaded, active, recipes);
        setDisplay();
      }
    }
  }

  class VundoAction extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VundoAction() {
      super(VUtil.getLocale("vUndo"), VRes.loadIcon("Undo"));
    }

    public void updateUndoState() {
      setEnabled(undo.canUndo());
    }

    public void actionPerformed(ActionEvent e) {
        try {
            undo.undo();
        } catch (CannotUndoException ex) { }
        updateUndoState();
        redoAction.updateRedoState();
    }
  }

  class VredoAction extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VredoAction() {
      super(VUtil.getLocale("vRedo"), VRes.loadIcon("Redo"));
    }

    public void updateRedoState() {
      setEnabled(undo.canRedo());
    }

    public void actionPerformed(ActionEvent e) {
        try {
            undo.redo();
        } catch (CannotUndoException ex) { }
        updateRedoState();
        undoAction.updateUndoState();
    }
  }

  String getSelectedCategory() {
    switch (tpType.getSelectedIndex()) {
      case 0:
        return (String)cbRCat.getSelectedItem();
      case 1:
        return (String)cbTCat.getSelectedItem();
      case 2:
        return (String)cbICat.getSelectedItem();
      default:
        return null;
    }
  }

  // Action for cbPopupMenu | DeleteCategory
  class VcbPMDC extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VcbPMDC() {
      super(VUtil.getLocale("vPMDC"));
    }

    public void actionPerformed(ActionEvent e) {
      Recipe r = null;
      String srcCat = getSelectedCategory();
      for (Iterator<Recipe> en = recipes.iterator(); en.hasNext(); ) {
        r = en.next();
        if (isInCategory(srcCat, r.getCategory()))
         en.remove();
      }
      setModified(true);
      relist(true);
    }
  }

  // Action for cbPopupMenu | ChangeCategory
  class VcbPMCC extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VcbPMCC() {
      super(VUtil.getLocale("vPMCC"));
    }

    public void actionPerformed(ActionEvent e) {
      String srcCat = getSelectedCategory();
      if (srcCat == null)
        return;
      String dstCat = JOptionPane.showInputDialog(VUtil.getLocale("dlg_newCat"), srcCat);
      if (dstCat == null || dstCat.length() == 0 || dstCat.equals(srcCat))
        return;
      Recipe old = active;
      for (Recipe r : recipes) {
        try {
          r.setCategory(r.getCategory().replaceFirst(
            "(^|\\b)\\Q" +
            srcCat +
            "\\E(\\b|$)",
            dstCat));
        } catch (Exception ex) {
          VError.writeErrorLog(ex);
        }
      }
      setModified(true);
      relist(true);
      selectRecipe(old);
    }
  }

  // SAX-loader for recipe-databases

  // draw . or ! in front of list-items
  class VcrRecipes extends DefaultListCellRenderer {
	private static final long serialVersionUID = 5815820524622884468L;

	public Component getListCellRendererComponent(JList<?> list, Object value, int index,
      boolean isSelected,  boolean cellHasFocus) {
      try {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (((Recipe)value).getType().equals(ERecipe.RECIPE))
          setIcon(((Recipe)value).getAlcohol() ? VRes.loadIcon("RedDot") : VRes.loadIcon("BlackDot"));
        else
          setIcon(VRes.loadIcon("BlackDot"));
        setToolTipText(((Recipe)value).getName());
      } catch (Exception e) {};
      return this;
    }
  }

  // special drawing of sub-categories in comboboxes
  class VcrCbList extends DefaultListCellRenderer {
	private static final long serialVersionUID = 3106056833102437155L;

	public Component getListCellRendererComponent(JList<?> list, Object value, int index,
      boolean isSelected,  boolean cellHasFocus) {
      try {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String s = (String)value;
        setToolTipText(s);
        int p = s.indexOf(Recipe.SUB_CAT);
        if (p >= 0) {
          setText(s.substring(p + Recipe.SUB_CAT.length()));
          setIcon(VRes.loadIcon("SubCat"));
        }
      } catch (Exception ex) {}
      return this;
    }
  }

  // recreate Display if selected recipe changes
  class VlslRecipes implements ListSelectionListener {
    JList<Recipe> l;

    VlslRecipes(JList<Recipe> aList) {
      this.l = aList;
    }

    public void valueChanged(ListSelectionEvent e) {
      active = l.getSelectedValue();
      setDisplay();
    }
  }

  // just forward the action for File | New
  class VMenuFileNew_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VMenuFileNew_Action() {
      super(VUtil.getLocale("vMenuFileNew"), VRes.loadIcon("New"));
    }

    public void actionPerformed(ActionEvent e) {
      askSave();
      recipes.clear();
      loadXML(fileNew);
    }
  }

  // just forward the action for File | Open
  class VMenuFileOpen_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VMenuFileOpen_Action() {
      super(VUtil.getLocale("vMenuFileOpen"), VRes.loadIcon("Open"));
    }

    public void actionPerformed(ActionEvent e) {
      if (fc.showOpenDialog(VMainWindow.this) == JFileChooser.APPROVE_OPTION) {
        askSave();
        recipes.clear();
        loadXML(fc.getSelectedFile());
      }
    }
  }

  // just forward the action for File | Import | Add
  class VMenuFileImportAdd_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VMenuFileImportAdd_Action() {
      super(VUtil.getLocale("vMenuFileImportAdd"), VRes.loadIcon("Import"));
    }

    public void actionPerformed(ActionEvent e) {
      if (fc.showOpenDialog(VMainWindow.this) == JFileChooser.APPROVE_OPTION) {
        askSave();
        loadXML(fc.getSelectedFile());
      }
    }
  }

  // just forward the action for File | Exit
  class VMenuFileExit_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VMenuFileExit_Action() {
      super(VUtil.getLocale("vMenuFileExit"), VRes.loadIcon("Exit"));
    }

    public void actionPerformed(ActionEvent e) {
      vMenuFileExit_actionPerformed(e);
    }
  }

  // show About-box Help | About
  class VMenuHelpAbout_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VMenuHelpAbout_Action() {
      super(VUtil.getLocale("vMenuHelpAbout"), VRes.loadIcon("Help"));
    }

    public void actionPerformed(ActionEvent e) {
      new AboutBox(VMainWindow.this);
    }
  }

  // save current file File | Save
  class VMenuFileSave_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VMenuFileSave_Action() {
      super(VUtil.getLocale("vMenuFileSave"), VRes.loadIcon("Save"));
    }

    public void actionPerformed(ActionEvent e) {
      saveAs(loaded, true);
    }
  }

  // save current file File | SaveAs
  class VMenuFileSaveAs_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VMenuFileSaveAs_Action() {
      super(VUtil.getLocale("vMenuFileSaveAs"), VRes.loadIcon("SaveAs"));
    }

    public void actionPerformed(ActionEvent e) {
      saveAs(fileNew, true);
    }
  }

  // save current file File | Print
  class VMenuFilePrint_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VMenuFilePrint_Action() {
      super(VUtil.getLocale("vMenuFilePrint"), VRes.loadIcon("Print"));
    }

    public void actionPerformed(ActionEvent e) {
      if (active != null) {
        new DocumentRenderer(active.getName()).print(display);
      }
    }
  }

  // save current file File | Export | Save for Vineta 1.2
  class VMenuFileExportOld_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VMenuFileExportOld_Action() {
      super(VUtil.getLocale("vMenuFileExportOld"), VRes.loadIcon("ExportMany"));
    }

    public void actionPerformed(ActionEvent e) {
      saveAs(fileNew, false);
    }
  }

  // save current file File | Export | Give to friend
  class VMenuFileExportSingle_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VMenuFileExportSingle_Action() {
      super(VUtil.getLocale("vMenuFileExportForward"), VRes.loadIcon("Export"));
    }

    public void actionPerformed(ActionEvent e) {
      saveAs(false);
    }
  }

  // edit-cancel
  class VtbEditCancel_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VtbEditCancel_Action() {
      super(VUtil.getLocale("tt_vBtnCancel"), VRes.loadIcon("Cancel"));
    }

    public void actionPerformed(ActionEvent e) {
      setDisplay();
    }
  }

  // edit-accept
  class VtbEditAccept_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VtbEditAccept_Action() {
      super(VUtil.getLocale("tt_vBtnOk"), VRes.loadIcon("Ok"));
    }

    public void actionPerformed(ActionEvent e) {
      Recipe r = null;
      boolean relist = false;
      ERecipe oldType;
      if (!(isNew || active == null)) {
        r = active;
      } else {
        r = new RecipeImpl();
        recipes.add(r);
      }
      r.setName(tfName.getText());
      String c = tfCategory.getText();
      if ((isNew || active == null) ||
        (r.getCategory() == null && c != null) ||
        (r.getCategory() != null && c == null) ||
        (!r.getCategory().equals(c)))
        relist = true;
      if (c == null || c.length() == 0)
        c = Recipe.CAT_EMPTY;
      r.setCategory(c);

      oldType = r.getType();
      if (rbRecipe.isSelected()) {
        r.setType(ERecipe.RECIPE);
        r.setNote((Integer)sNote.getValue());
        r.setTaste(tfTaste.getText());
        r.setAlcohol(tbAlcohol.isSelected());
        r.setIngredients(getMultiline(taIngredients));
        r.setTools(getMultiline(taTools));
        r.setDecoration(getMultiline(taDecoration));
        r.setProcedure(getTextA(taProcedure));
      }
      if (rbTip.isSelected())
        r.setType(ERecipe.TIP);
      if (rbInformation.isSelected())
        r.setType(ERecipe.INFORMATION);
      if (r.getType() != oldType)
        relist = true;

      r.setImageId(new Integer(bImage.getText()));
      r.setComment(getTextA(taComments));

      if (settings.saveOnAccept)
        save();
      else
        setModified(true);

      if (relist) {
        relist(true);
        selectRecipe(r);
      } else
        setDisplay();
      isNew = false;
    }

    private Vector<String> getMultiline(JTextArea ta) {
      String s = ta.getText();
      if (s == null || s.length() == 0)
        return null;
      Vector<String> v = new Vector<String>(10, 10);
      Pattern pat = Pattern.compile("(?m)(.+)$");
      Matcher m = pat.matcher(s);
      String z = null;
      while (m.find()) {
        z = m.group(1);
        if (z.length() != 0)
          v.add(z);
      }
      return v;
    }

    private String getTextA(JTextArea ta) {
      String s = ta.getText();
      if (s == null || s.length() == 0)
        return null;
      s = s.replace("\r\n", "</A><A>")
        .replaceAll("(\r|\n|\u0085|\u2028|\u2029)", "</A><A>");
      s = "<A>".concat(s);
      if (s.endsWith("<A>"))
        s = s.substring(0, s.length() - 3);
      else
        s = s.concat("</A>");
      while (s.endsWith("<A></A>"))
        s = s.substring(0, s.length() - 7);
      return s;
    }
  }

  // edit-new
  class VtbEditNew_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VtbEditNew_Action() {
      super(VUtil.getLocale("tt_New"), VRes.loadIcon("New"));
    }

    public void actionPerformed(ActionEvent e) {
      clearEditPage();
      if (active != null)
        tfCategory.setText(active.getCategory());
      isNew = true;
    }
  }

  // edit-delete
  class VtbEditDelete_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VtbEditDelete_Action() {
      super(VUtil.getLocale("tt_Remove"), VRes.loadIcon("Erase"));
    }

    public void actionPerformed(ActionEvent e) {
      if (active != null) {
        int idx = 0;
        switch (tpType.getSelectedIndex()) {
          case 0:
            idx = cbRCat.getSelectedIndex();
            break;
          case 1:
            idx = cbTCat.getSelectedIndex();
            break;
          case 2:
            idx = cbICat.getSelectedIndex();
        }
        recipes.remove(active);
        active = null;
        relist(true);
        try {
          switch (tpType.getSelectedIndex()) {
            case 0:
              cbRCat.setSelectedIndex(idx);
              break;
            case 1:
              cbTCat.setSelectedIndex(idx);
              break;
            case 2:
              cbICat.setSelectedIndex(idx);
          }
        } catch (Exception ex) {
          VError.writeErrorLog(ex);
        }
        setModified(true);
        isNew = false;
      }
    }
  }

  // edit-bold
  class VtbEditBold_Action extends TextAction {

    static final long serialVersionUID = 2005200001;

    VtbEditBold_Action() {
      super(VUtil.getLocale("tt_Bold"));
      putValue(Action.SMALL_ICON, VRes.loadIcon("Bold"));
    }

    public void actionPerformed(ActionEvent e) {
      setFormat(getTextComponent(e), "F");
    }
  }

  // edit-italic
  class VtbEditItalic_Action extends TextAction {

    static final long serialVersionUID = 2005200001;

    VtbEditItalic_Action() {
      super(VUtil.getLocale("tt_Italic"));
      putValue(Action.SMALL_ICON, VRes.loadIcon("Italic"));
    }

    public void actionPerformed(ActionEvent e) {
      setFormat(getTextComponent(e), "K");
    }
  }

  // edit-underline
  class VtbEditUnderline_Action extends TextAction {

    static final long serialVersionUID = 2005200001;

    VtbEditUnderline_Action() {
      super(VUtil.getLocale("tt_Underline"));
      putValue(Action.SMALL_ICON, VRes.loadIcon("Underline"));
    }

    public void actionPerformed(ActionEvent e) {
      setFormat(getTextComponent(e), "U");
    }
  }

  // edit-list
  class VtbEditList_Action extends TextAction {

    static final long serialVersionUID = 2005200001;

    VtbEditList_Action() {
      super(VUtil.getLocale("tt_List"));
      putValue(Action.SMALL_ICON, VRes.loadIcon("List"));
    }

    public void actionPerformed(ActionEvent e) {
      setFormat(getTextComponent(e), "L");
    }
  }

  // edit-numbering
  class VtbEditNumbering_Action extends TextAction {

    static final long serialVersionUID = 2005200001;

    VtbEditNumbering_Action() {
      super(VUtil.getLocale("tt_Numbering"));
      putValue(Action.SMALL_ICON, VRes.loadIcon("Numbering"));
     }

    public void actionPerformed(ActionEvent e) {
      setFormat(getTextComponent(e), "N");
    }
  }

  // edit-select-all
  class VtbEditSelectAll_Action extends TextAction {

    static final long serialVersionUID = 2005200001;

    VtbEditSelectAll_Action() {
      super(VUtil.getLocale("tt_SelectAll"));
    }

    public void actionPerformed(ActionEvent e) {
      getTextComponent(e).selectAll();
    }
  }

  // Edit | Search full-text
  class VtbSearch_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VtbSearch_Action() {
      super(VUtil.getLocale("tt_Search"), VRes.loadIcon("Search"));
    }

    public void actionPerformed(ActionEvent e) {
      String ex = JOptionPane.showInputDialog(VUtil.getLocale("dlg_srcText"));
      if (ex == null || ex.length() == 0) {
        return;
      }
      String src[] = ex.toUpperCase().split(" ");
      Vector<Recipe> recs = new Vector<Recipe>(recipes.size(),1);
      for (Recipe r : recipes) {
        String s = r.toText().toUpperCase();
        boolean found = true;
        for (int i = 0; i < src.length; i++) {
          if (src[i].length() > 0) {
            if (src[i].charAt(0) == '-') {
              if (s.contains(src[i].substring(1))) {
                found = false;
                break;
              }
            } else
              if (!s.contains(src[i])) {
                found = false;
                break;
              }
          }
        }
        if (found)
          recs.add(r);
      }
      lSearchResults.setListData(recs);
      tpType.addTab(VUtil.getLocale("root_search"), VRes.loadIcon("Search"), pSearchResults);
      tpType.setSelectedIndex(3);
    }
  }

  // Edit | Search Special
  class VtbSearchPlus_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VtbSearchPlus_Action() {
      super(VUtil.getLocale("tt_SearchPlus"), VRes.loadIcon("AdvSearch"));
    }

    public void actionPerformed(ActionEvent e) {
      String regEx = JOptionPane.showInputDialog(VUtil.getLocale("dlg_RegEx"));
      if (regEx == null || regEx.length() == 0) {
        return;
      }
      regEx = ".*" + regEx + ".*";
      Vector<Recipe> recs = new Vector<Recipe>(recipes.size(),1);
      for (Recipe r : recipes) {
        if (r.toText().matches(regEx))
          recs.add(r);
      }
      lSearchResults.setListData(recs);
      tpType.addTab(VUtil.getLocale("root_search"), VRes.loadIcon("Search"), pSearchResults);
      tpType.setSelectedIndex(3);
    }
  }

  // Help
  class VtbHelp_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VtbHelp_Action() {
      super(VUtil.getLocale("tt_vBtnHelp"), VRes.loadIcon("Help"));
      putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F1);
    }

    public void actionPerformed(ActionEvent e) {
      new HelpViewer(cl);
    }
  }

  // Extras | Options
  class VMenuExtrasSetup_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VMenuExtrasSetup_Action() {
      super(VUtil.getLocale("vMenuExtrasOptions"));
    }

    public void actionPerformed(ActionEvent e) {
      settings.execSetupDialog();
      setDisplay();
    }
  }

  // Extras | SetLookAndFeel
  class VMenuExtrasLaf_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VMainWindow mw;

    VMenuExtrasLaf_Action(VMainWindow owner) {
      super(VUtil.getLocale("vMenuExtrasLaf"));
      mw = owner;
    }

    public void actionPerformed(ActionEvent e) {
      LookAndFeelChooser.execLafDialog(mw);
    }
  }

  // (de)activate textfields dependent on recipe.type
  class VEditTypeChange implements ChangeListener {

    public void stateChanged(ChangeEvent e) {
      if (rbRecipe.isSelected()) {
        taIngredients.setEnabled(true);
        taTools.setEnabled(true);
        taDecoration.setEnabled(true);
        taProcedure.setEnabled(true);
        tfTaste.setEnabled(true);
        tbAlcohol.setEnabled(true);
        sNote.setEnabled(true);
      } else {
        taIngredients.setEnabled(false);
        taTools.setEnabled(false);
        taDecoration.setEnabled(false);
        taProcedure.setEnabled(false);
        tfTaste.setEnabled(false);
        tbAlcohol.setEnabled(false);
        sNote.setEnabled(false);
      }
    }
  }

  // Help | About
  class AboutBox extends JDialog implements ActionListener {

    static final long serialVersionUID = 2005200001;

    JPanel panel1 = new JPanel();
    JPanel panel2 = new JPanel();
    JPanel insetsPanel1 = new JPanel();
    JPanel insetsPanel2 = new JPanel();
    JPanel insetsPanel3 = new JPanel();
    JButton button1 = new JButton();
    JLabel imageLabel = new JLabel();
    JLabel label1 = new JLabel();
    JLabel label3 = new JLabel();
    JLabel label4 = new JLabel();
    JLabel label5 = new JLabel();
    BorderLayout borderLayout1 = new BorderLayout();
    BorderLayout borderLayout2 = new BorderLayout();
    FlowLayout flowLayout1 = new FlowLayout();
    GridLayout gridLayout1 = new GridLayout();

    public AboutBox(JFrame parent) {
      super(parent, true);
      enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      try {
        aboutInit();
      }
      catch(Exception e) {
        VError.writeErrorLog(e);
      }
    }

    //Component initialization
    private void aboutInit() throws Exception {
      this.setTitle(VUtil.getLocale("vMenuHelpAbout") + " " + VinetaVersionInfo.getName() + "...");
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      panel1.setLayout(borderLayout1);
      panel2.setLayout(borderLayout2);
      insetsPanel1.setLayout(flowLayout1);
      insetsPanel2.setLayout(flowLayout1);
      insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      gridLayout1.setRows(4);
      gridLayout1.setColumns(1);
      label1.setText(VinetaVersionInfo.getName() + ", " + VinetaVersionInfo.getVersion());
      label3.setText(VUtil.getLocale("about_Comment"));
      label4.setText(VinetaVersionInfo.getCopyright());
      label5.setText("cp=\"".concat(System.getProperty("java.class.path")).concat("\""));
      insetsPanel3.setLayout(gridLayout1);
      insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      button1.setText(VUtil.getLocale("tt_vBtnOk"));
      button1.addActionListener(this);
      imageLabel.setIcon(VinetaVersionInfo.getIcon());
      imageLabel.setIconTextGap(4);
      insetsPanel2.add(imageLabel, null);
      panel2.add(insetsPanel2, BorderLayout.WEST);
      add(panel1);
      insetsPanel3.add(label1, null);
      insetsPanel3.add(label3, null);
      insetsPanel3.add(label4, null);
      insetsPanel3.add(label5, null);
      panel2.add(insetsPanel3, BorderLayout.CENTER);
      insetsPanel1.add(button1, null);
      panel1.add(insetsPanel1, BorderLayout.SOUTH);
      panel1.add(panel2, BorderLayout.NORTH);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension dlgSize = getPreferredSize();
      Point loc = getLocation();
      setLocation((screenSize.width - dlgSize.width) / 2 + loc.x, (screenSize.height - dlgSize.height) / 2 + loc.y);
      setResizable(false);
      pack();
      setVisible(true);
    }

    //Close the dialog on a button event
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() == button1)
        dispose();
    }
  }

  // Extras | Restore
  class Restore_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    Restore_Action() {
      super(VUtil.getLocale("vMenuExtrasRestore"));
    }

    public void actionPerformed(ActionEvent e) {
      askSave();
      JFileChooser fc = VUtil.getFileChooser("J2B", "ffV2B");

      if (fc.showOpenDialog(VMainWindow.this) != JFileChooser.APPROVE_OPTION)
        return;

      File aFile = fc.getSelectedFile();
      try {
        File dst = new File(aFile.getName().replace(".J2B", ".xml"));
        VUtil.copyStream(
          new java.util.zip.GZIPInputStream(aFile.toURI().toURL().openStream()),
          dst);
        loadXML(dst);
      } catch (Exception ex) {
        VError.writeErrorLog(ex);
      }
    }

  }

  // Extras | RestoreVineta
  class VRestore_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    VRestore_Action() {
      super(VUtil.getLocale("vMenuExtrasVRestore"));
    }

    public void actionPerformed(ActionEvent e) {
      askSave();
      JFileChooser fc = VUtil.getFileChooser("V2B", "ffV2BV");

      if (fc.showOpenDialog(VMainWindow.this) != JFileChooser.APPROVE_OPTION)
        return;

      File aFile = fc.getSelectedFile();
      try {
        File dst = new File(aFile.getName().replace(".V2B", ".xml"));
        VUtil.copyStream(
          new java.util.zip.InflaterInputStream(aFile.toURI().toURL().openStream()),
          dst);
        loadXML(dst);
      } catch (Exception ex) {
        VError.writeErrorLog(ex);
      }
    }

  }

  // Extras | Backup
  class Backup_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    Backup_Action() {
      super(VUtil.getLocale("vMenuExtrasBackup"));
    }

    public void actionPerformed(ActionEvent e) {
      askSave();
      JFileChooser fc = VUtil.getFileChooser("J2B", "ffV2B");

      if (fc.showSaveDialog(VMainWindow.this) != JFileChooser.APPROVE_OPTION)
        return;

      String aFile = fc.getSelectedFile().getPath();
      if (!aFile.endsWith(".J2B"))
        aFile += ".J2B";
      try {
        File f = new File(aFile);
        f.createNewFile();
        VUtil.copyStream(
          new File(loaded).toURI().toURL().openStream(),
          new java.util.zip.GZIPOutputStream(new FileOutputStream(f)));
      } catch (Exception ex) {
        VError.writeErrorLog(ex);
      }
    }

  }

  // Extras | Update
  class Update_Action extends AbstractAction {

    static final long serialVersionUID = 2005200001;

    Update_Action() {
      super(VUtil.getLocale("vMenuExtrasUpdate"));
    }

    public void actionPerformed(ActionEvent e) {
      try {
        URL url = new URL("http://www.tusche-welt.de/upd/jineta.ver");
        InputStream is = url.openStream();
        String version = null;
        if (is != null) {
          byte b[] = new byte[256];
          int len = is.read(b);
          is.close();
          if (len > 0) {
            version = new String(b, 0, len);
          }
        }
        if (version != null) {
          int n[] = new int[4], o[] = new int[4];
          String sPre[] = version.split("\\s");
          String s[] = sPre[1].split("\\.");
          for (int i = 0; i < 4; i++) n[i] = Integer.parseInt(s[i]);
          sPre = VinetaVersionInfo.getVersion().split("\\s");
          s = sPre[1].split("\\.");
          for (int i = 0; i < 4; i++) o[i] = Integer.parseInt(s[i]);
          if (n[0] > o[0] ||
            (n[0] == o[0] && (n[1] > o[1] ||
            (n[1] == o[1] && (n[2] > o[2] ||
            (n[2] == o[2] && n[3] > o[3])))))) {
            if (JOptionPane.showConfirmDialog(
              VMainWindow.this,
              VUtil.getLocale("dlg_Ask_Update"),
              VUtil.getLocale("dlg_Update"),
              JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
              File tmp = File.createTempFile("jineta", null);
              url = new URL(url, "jineta.pack.gz");
              VUtil.copyStream(
                new ProgressMonitorInputStream(
                  VMainWindow.this,
                  VUtil.getLocale("status_Loading"),
                  url.openStream()),
                tmp);
              File org = VRes.getPath();
              try {
                org.delete();
              } catch (Exception ex) {};
              VUtil.unpackFile(tmp, org);
              JOptionPane.showMessageDialog(VMainWindow.this,
                VUtil.getLocale("dlg_Done_Update"));
            }
          } else
            JOptionPane.showMessageDialog(VMainWindow.this,
              VUtil.getLocale("dlg_No_Update"));
        }
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(VMainWindow.this,
          VUtil.getLocale("status_DocError"));
        VError.writeErrorLog(ex);
      }
    }
  }

  class AutoSaver extends TimerTask {
    AutoSaver() {
      super();
    }

    public void run() {
      saveAs(loaded, true);
    }
  }

}

