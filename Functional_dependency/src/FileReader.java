import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileReader{

	private String path = "functional_dependencies.txt";
	public RandomAccessFile fds;

	// constructor
	FileReader() throws FileNotFoundException {
		try {
			this.fds = new RandomAccessFile(path,"rw");
		} catch (Exception e) {
			System.out.println("File is not found !!!");
		}
	}
	
	public String load_file_into_string () throws IOException {
		String FDstring ="";
		FDstring= fds.readLine();
		return FDstring;
	}
	
	

}
