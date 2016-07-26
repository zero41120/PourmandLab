package nanopipettes;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class should handle all file input and output.
 */
public class FileManager {

	/**
	 * This method check if file exists.
	 * @param inputFile File to check.
	 * @throws FileNotFoundException If no such file.
	 */
	static public void checkExistance(File inputFile) throws FileNotFoundException{
		if (!inputFile.exists()) {
			throw new FileNotFoundException("TNError: Provided file name is not an existing file.");
		}
	}

	/**
	 * This method checks the file headers.
	 * @param inputText
	 */
	static public void checkHeader(File inputFile) {
		try (FileInputStream stream = new FileInputStream(inputFile);
				InputStreamReader reader = new InputStreamReader(stream, "UTF8");
				BufferedReader bufferedReader = new BufferedReader(reader)) {
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				if(FileFormatHelper.isDeclartion(line)){
					// If line is not a header nor declaration, a RunTimeException will be thrown.
					return; 
				}
			}
		} catch (IOException e) {
			// IOStream, usually should not be a problem.
			e.printStackTrace();
		} 
	}
	
	/**
	 * Credit to http://goo.gl/mYlBE
	 * This function calculates the total lines in a file.
	 */
	public static int countLines(File file) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(file));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
}
