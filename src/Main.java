import java.io.File;
import java.security.NoSuchAlgorithmException;

public class Main {

	public static void main(String[] args){
		Controller controller = new Controller();
		String folderPath = "Your Folder Filled With Images Here";

		try {
			controller.findDuplicatesAndCorruptions(new File(folderPath));
		} catch (NoSuchAlgorithmException e){
			e.printStackTrace();
		}
	}
}
