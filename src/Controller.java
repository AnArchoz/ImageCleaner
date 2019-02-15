import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.TreeMap;

class Controller {

	private LinkedList<File> duplicateList = new LinkedList<>();
	private LinkedList<File> corruptList = new LinkedList<>();
	private TreeMap<String, File> fileMap = new TreeMap<>();
	private int fileCounter = 0;
	private int deleteCounter = 0;

	void findDuplicatesAndCorruptions(File directory) throws NoSuchAlgorithmException {
		File[] files = directory.listFiles();

		if (files != null) {
			for (File file : files) {
				fileCounter++;

				// Check if file is hidden (mainly for linux), return to next file in the loop
				if (file.isHidden()) {
					return;
				}

				// If the file checked is itself a folder enter the folder to recursively continue
				if (file.isDirectory()) {
					findDuplicatesAndCorruptions(file);
				} else {

					//Tries to generate an IOException for corrupted files, marking them as corrupted by returning true.
					if (fileIsCorrupted(file)) {
						corruptList.add(file);
					} else {
						try {
							//Create MD5-hash for the file
							MessageDigest md = MessageDigest.getInstance("MD5");
							md.update(Files.readAllBytes(Paths.get(file.toURI())));
							byte[] digest = md.digest();

							//Converts the digested byte array to a hexadecimal String
							String hashResult = getHash(digest);

							//Check if hash already exists in the map
							//If a hash for this filedata exists in the map, the file we are using for comparison is a duplicate
							// and will be added to the duplicate list.
							if (fileMap.get(hashResult) == null) {
								fileMap.put(hashResult, file);
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

		//Deletes all duplicate and corrupted images.
		duplicateList.forEach(this::delete);
		corruptList.forEach(this::delete);

		//Print the results
		System.out.println("Deleted " + deleteCounter + " files.");
		System.out.println(reportFindings());
	}


	private void delete(File file) {
		if (file != null) {
			if (file.delete()) {
				deleteCounter++;
			} else {
				System.out.println("Deletion failed" + file.getName());
			}
		}
	}

	private boolean fileIsCorrupted(File file) {
		boolean corrupt = false;
		try {
			ImageIO.read(file);
		} catch (IOException e) {
			corrupt = true;
		}
		return corrupt;
	}

	private String getHash(byte[] bytes) {
		StringBuilder hashResult = new StringBuilder();

		for (byte b : bytes) {
			char[] hexDigits = new char[2];
			hexDigits[0] = Character.forDigit((b >> 4) & 0xF, 16);
			hexDigits[1] = Character.forDigit((b & 0xF), 16);
			hashResult.append(new String(hexDigits));
		}
		return hashResult.toString();
	}

	private String reportFindings() {
		return "Your folder contained " + corruptList.size() + " corrupted files. \n" +
				"Your folder contained " + duplicateList.size() + " duplicate files. " +
				fileCounter + " files  were looked at"
				;
	}
}
