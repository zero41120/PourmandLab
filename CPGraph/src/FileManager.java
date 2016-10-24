import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

	public static ArrayList<File> getATFFiles(List<File> targetFiles) {
		ArrayList<File> toReturn = new ArrayList<>();
		for (int i = 0; i < targetFiles.size(); i++) {
			if (getFileExtension(targetFiles.get(i)).equals("atf")) {
				toReturn.add(targetFiles.get(i));
			}
		}
		return toReturn;
	}
	
	public static String getFileExtension(File file) {
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}
	
}
