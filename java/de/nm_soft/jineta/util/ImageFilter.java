package de.nm_soft.jineta.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ImageFilter extends FileFilter {

	// Accept all directories and all gif, jpg, tiff, or png files.
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		return ImageHelper.isImage(f);
	}

	// The description of this filter
	public String getDescription() {
		return "Images";
	}

}
