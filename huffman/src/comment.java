//import java.io.File;
//
//import javax.swing.filechooser.FileFilter;
//
//public class comment extends FileFilter {
//
//	private String[] fileextensions;
//	private String description = "";
//
//	public comment(String description, String... fileextension) {
//		this.fileextensions = fileextension;
//		this.description = description;
//	}
//
//	@Override
//	public String getDescription() {
//		return description;
//	}
//
//	public String[] getExtensions() {
//		return fileextensions;
//	}
//
//	@Override
//	public boolean accept(File f) {
//		String[] extensions = this.getExtensions();
//		String extfile = getFileExtension(f);
//		for (int t = 0; t < extensions.length; t++) {
//			if (extfile.equals(extensions[t])) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	private String getFileExtension(File file) {
//		String name = file.getName();
//		int lastIndexOf = name.lastIndexOf(".");
//		if (lastIndexOf == -1) {
//			return ""; // empty extension
//		}
//		return name.substring(lastIndexOf + 1);
//	}
//
//}
