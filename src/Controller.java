import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;


public class Controller {
	private LinkedList<File> duplicateList = new LinkedList<File>();
	private LinkedList<File> corruptList = new LinkedList<>();
	private Map<String, File> fileMap = new TreeMap<>();

	public void findDuplicatesAndCorruptions(File directory) throws NoSuchAlgorithmException {
		byte[] fileData;

		for (File file : directory.listFiles()) {
			// Check if file is corrupted, return to next file in the loop otherwise
			if (fileIsCorrupted(file)) {
				corruptList.add(file);
			} else {

				//Buffer for the filedata
				fileData = new byte[(int) file.length()];

				try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
					//Write filedata to the buffer
					stream.read(fileData);

					//Create MD5-hash for the file
					MessageDigest md = MessageDigest.getInstance("MD5");
					md.update(fileData);
					byte[] digest = md.digest();

					StringBuilder hashResult = new StringBuilder();
					for (byte b : digest) {
						hashResult.append(String.format("%02x", b));
					}

					//Check if hash already exists in the map
					File mapCheck = fileMap.get(hashResult.toString());

					//If hash for this filedata exists it means This file is a duplicate
					// and will be added to the duplicate list.

					if (mapCheck == null) {
						fileMap.put(hashResult.toString(), file);
					} else {
						duplicateList.add(file);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			System.out.println("counting: " + corruptList.size() + " corrupted files...\n" +
					"and " + duplicateList.size() + "duplicate files...");

		}

		System.out.println(reportFindings());


	}

	//Tries to generate an IOException for corrupted files, marking them as corrupted by returning true.
	private boolean fileIsCorrupted(File file) {
		boolean corrupt = false;
		try {
			Image image = ImageIO.read(file);
		} catch (IOException e) {
			corrupt = true;
		}
		return corrupt;
	}

	private String reportFindings() {

		return "Your folder contained " + corruptList.size() + " corrupted files. \n" +
				"Your folder contained " + duplicateList.size() + " duplicate files.";
	}
}
