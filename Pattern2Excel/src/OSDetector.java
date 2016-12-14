import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

/**
 * This is a utility class the helps the program detecting the operating system.
 * This class also provides a method that opens a file in the operating system's
 * default file explorer.
 */
public class OSDetector {
	private static boolean isWindows = false;
	private static boolean isLinux = false;
	private static boolean isMac = false;

	static {
		String os = System.getProperty("os.name").toLowerCase();
		isWindows = os.contains("win");
		isLinux = os.contains("nux") || os.contains("nix");
		isMac = os.contains("mac");
	}

	public static boolean isWindows() {
		return isWindows;
	}

	public static boolean isLinux() {
		return isLinux;
	}

	public static boolean isMac() {
		return isMac;
	}

	/**
	 * This method opens the directory of provided file. Credit:
	 * https://goo.gl/Vtfvd3
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void openDirectory(File file) throws IOException {
		if (OSDetector.isWindows()) {
			Runtime.getRuntime()
					.exec(new String[] { "rundll32", "url.dll,FileProtocolHandler", file.getAbsolutePath() });
		} else if (OSDetector.isLinux() || OSDetector.isMac()) {
			Runtime.getRuntime().exec(new String[] { "/usr/bin/open", file.getAbsolutePath() });
		} else {
			// Unknown OS, try with desktop
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(file);
			} else {
			}
		}
	}

}