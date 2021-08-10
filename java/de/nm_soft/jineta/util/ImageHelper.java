package de.nm_soft.jineta.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Vector;

import javax.swing.ImageIcon;

import de.nm_soft.jineta.Recipe;

public class ImageHelper {
  final static String imgs[] = new String[] { "jpeg", "jpg", "gif", "tiff",
    "tif", "png" };


  //TODO: Write folder validator
  
  static public File getImage(Recipe r, String recipes){
    //No Image for recipe
    if(r.getImageId() <= 0) return null;
    //Get the image
    File dir = getFolder(recipes);
    for(final File f : dir.listFiles()){
      if(f.getName().startsWith(r.getImageId().toString() + ".")){
        if(isValidImg(f)) return f;
      }
    }
    System.err.println("Recipe " + r.getName() + " should have an Image, but I could not find it.");
    return null;
  }
  
  static File getFolder(String file){
    File dir =  new File(file.substring(0,file.lastIndexOf('.')));
    if(!dir.isDirectory()){
      if(dir.exists()){
        System.err.println(dir.getAbsolutePath() + "exists, but is not a folder");
        return null;
      }
      if(! dir.mkdirs()){
        System.err.println("Could nor create folder " + dir.getAbsolutePath() + "exists, but is not a folder.");
        return null;
      }
    }
    return dir;
  }
  
  static boolean isValidImg(File f){   
    if(!isImage(f)) return false;
    if(new ImageIcon(f.getPath()).getImage()  ==  null) {
      //TODO Show warning
      System.err.println("File " + f.getAbsolutePath() + "is not a valid Image");
      return false;
    }
    return true;
  }
  
  public static int getFreeId(final Vector<Recipe> recipes){
    int id = 1;
    for(final Recipe r : recipes){
      if(r.getImageId() >= id) id = r.getImageId() + 1;
    }
    return id;
  }
  
  
  public static boolean copyFile(File f, String recipeFile, Recipe recipe, final Vector<Recipe> recipes){
   if(!isValidImg(f)) return false;

    File dir = getFolder(recipeFile);
    if(dir == null) return false;
    
    //Assign Id if necessary
    if(recipe.getImageId() < 1) recipe.setImageId(getFreeId(recipes));
    
    File to = new File(dir.getAbsolutePath() + File.separatorChar + recipe.getImageId() + f.getName().substring(f.getName().lastIndexOf('.')));
    try {
      Files.copy(f.toPath(),  to.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e1) {
      //TODO Make sensible Error Message
      e1.printStackTrace();
      return false;
    }
    return true;
  }
  
  static public boolean isImage(File f) {
    if(!f.exists()) return false;
    for (String im : imgs) {
      if (f.getName().endsWith("." + im))
        return true;
    }

    return false;
  }
}
