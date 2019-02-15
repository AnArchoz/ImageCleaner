import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.TreeMap;

public class Controller {

	private LinkedList<File> duplicateList = new LinkedList<>();
	private LinkedList<File> corruptList = new LinkedList<>();
	private TreeMap<String, File> fileMap = new TreeMap<>();
	private int count = 0;

	public void findDuplicatesAndCorruptions(File directory) throws NoSuchAlgorithmException {
		File[] files = directory.listFiles();

		if (files != null) {
			for (File file : files) {
				count++;
				// Check if file is hidden (mainly for linux), return to next file in the loop
				if (file.isHidden()) {
					return;
				}
				// If the file checked is itself a folder enter the folder to recursively continue
				if (file.isDirectory()) {
					findDuplicatesAndCorruptions(file);
				} else {

					if (fileIsCorrupted(file)) {
						corruptList.add(file);
					} else {

						try {

							//Create MD5-hash for the file
							MessageDigest md = MessageDigest.getInstance("MD5");
							md.update(Files.readAllBytes(Paths.get(file.toURI())));
							byte[] digest = md.digest();

							//Converts the digested byte array to a hexadecimal String
							StringBuilder hashResult = new StringBuilder();
							for (byte b : digest) {
								char[] hexDigits = new char[2];
								hexDigits[0] = Character.forDigit((b >> 4) & 0xF, 16);
								hexDigits[1] = Character.forDigit((b & 0xF), 16);
								hashResult.append(new String(hexDigits));
							}

							System.out.println(hashResult.toString());

							//Check if hash already exists in the map
							//If a hash for this filedata exists in the map, the file we are comparing is a duplicate
							// and will be added to the duplicate list.
							if (fileMap.get(hashResult.toString()) == null) {
								fileMap.put(hashResult.toString(), file);
							} else {
								duplicateList.add(file);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				System.out.println("counting: " + corruptList.size() + " corrupted files...\n" +
						"and " + duplicateList.size() + " duplicate files...");
			}
		}
		System.out.println(reportFindings());

		delete();
		System.out.println();
	}

	//Deletes all duplicate and corrupted images.

	private void delete() {
		int counter = 0;

		for (File file : duplicateList) {
			if (file.delete()) {
				counter++;
			} else {
				System.out.println("Deletion failed");
			}
		}

		for (File file : corruptList) {
			if (file.delete()) {
				counter++;
			} else {
				System.out.println("Deletion failed");
			}
		}

		System.out.println("Deleted " + counter + " files.");
	}

	//Tries to generate an IOException for corrupted files, marking them as corrupted by returning true.

	private boolean fileIsCorrupted(File file) {
		boolean corrupt = false;
		try {
			ImageIO.read(file);
		} catch (IOException e) {
			corrupt = true;
		}
		return corrupt;
	}

	private String reportFindings() {
		return "Your folder contained " + corruptList.size() + " corrupted files. \n" +
				"Your folder contained " + duplicateList.size() + " duplicate files. " +
				count + " files  were looked at"
				;
	}
}
