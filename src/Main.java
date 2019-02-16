import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.security.NoSuchAlgorithmException;

public class Main {

	public static void main(String[] args) {
		Controller controller = new Controller();
		String folderPath = "";

		// Make ugly swing look like sexy
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e){
			e.printStackTrace();
		}
		
		JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int y = fc.showOpenDialog(null);

		if (y == JFileChooser.APPROVE_OPTION) {
			folderPath = fc.getSelectedFile().getAbsolutePath();
		} else {
			System.exit(1);
		}

		try {
			long start = System.currentTimeMillis();
			controller.findDuplicatesAndCorruptions(new File(folderPath));
			long end = System.currentTimeMillis();
			System.out.println((end - start) / 1000.0 + " seconds.");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
