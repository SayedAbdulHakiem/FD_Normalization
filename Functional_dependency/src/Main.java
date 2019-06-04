import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException {

		Controller controller = new Controller();

		System.out.println("1- to check incorrect functional depedency \n"
						 + "2- to find candidate keys and primary keys \n"
						 + "3- to state the normal form of the given functional dependencies \n"
						 + "4- to composite relation into BCNF \n" 
						 + "Enter the number of any previous options:");
		Scanner in = new Scanner(System.in);
		int option = in.nextInt();
		
		if (option == 1) {
			System.out.println("Enter functional dependency to validate it :");
			String newFD = in.next();
			controller.checkFD(newFD);
		} else if (option == 2) {
			controller.getInitialKeys();
		} else if (option == 3) {
			String NFofTheRelation ="The Relation is in "+ controller.stateTheNormalForm();
			System.out.println(NFofTheRelation);			
		} else if (option == 4) {
			ArrayList<ArrayList<FDs>> bcnf = controller.get3NF();
			controller.printFunctionalDependencies(bcnf);
		}
		in.close();
	}

}
