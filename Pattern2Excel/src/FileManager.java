import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides file management.
 * 
 * @author SeanLin
 *
 */
public class FileManager {

	public static ArrayList<File> getTextFiles(List<File> targetFiles) {
		ArrayList<File> toReturn = new ArrayList<>();
		long s = System.currentTimeMillis();
		for (int i = 0; i < targetFiles.size(); i++) {
			File target = targetFiles.get(i);
				if (getFileExtension(target).equals("atf") ||
					getFileExtension(target).equals("txt")) {
					toReturn.add(target);
				}
		}
		System.out.println(System.currentTimeMillis() - s);

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

	/**
	 * This method removes the extension of the file and return the file name.
	 * 
	 * @param file
	 */
	public static String getName(File file) {

		String fullName = file.getName();
		return fullName.substring(0, fullName.lastIndexOf("."));
	}

	public static BufferedReader getReader(File file) throws IOException {
		FileInputStream readFile = new FileInputStream(file);
		InputStreamReader readIn = new InputStreamReader(readFile, "UTF8");
		return new BufferedReader(readIn);
	}

}
