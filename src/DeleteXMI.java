import java.io.File;

public class DeleteXMI {
	public static final String DIR = "henshin";

	public static void main(String[] args) {
		File dir = new File(DIR);
		String[] children = dir.list();
		for (String s : children) {
			if (s.contains("Result_") && s.contains("xmi")) {
				System.out.println("File: " + s + " has be deleted.");
				new File(dir, s).delete();
			}
		}
	}

}
