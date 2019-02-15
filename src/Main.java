import java.io.File;
import java.security.NoSuchAlgorithmException;

public class Main {

	public static void main(String[] args) {
		Controller controller = new Controller();
		String folderPath = "Your Folder To Be Searched Here";

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
