import java.io.IOException;
import java.util.ArrayList;

public class Controller {
	String inputString = "";
	// FDs is the ArrayList of functional dependencies
	ArrayList<FDs> FDList = new ArrayList<FDs>();
	String allAttributesString = "";
	char[] allAttributes;
	int Fsize = 0;
	ArrayList<String> initialKeys = new ArrayList<String>();
	ArrayList<String> primaryKey = new ArrayList<String>();

	Controller() throws IOException {
		FileReader file = new FileReader();
		this.inputString = file.load_file_into_string();
		System.out.println("function dependencies is : [ " + inputString + " ]");
		setAllFDs();
		setAllFDsElements();
		initialKeys = getInitialKeys();
		setPrimaryKey();
	}

	boolean isSubset(String set, String subset) {
		int subsetSize = subset.length();
		int counter = 0;
		String currentElement = "";
		char[] subsetChar = subset.toCharArray();
		for (int i = 0; i < subsetSize; i++) {
			currentElement = "" + subsetChar[i];
			if (set.contains(currentElement))
				counter++;
		}
		if ( counter == subsetSize )
			return true;
		return false;
	}

	boolean isContainsAll(String distnctValues, String closure) {
		int distnctElementSize = distnctValues.length();
		int counter = 0;
		String currentElement = "";
		char[] distnctChars = distnctValues.toCharArray();
		for (int i = 0; i < distnctElementSize; i++) {
			currentElement = "" + distnctChars[i];
			if (closure.contains(currentElement)) {
				counter++;
			}
		}
		if (counter == distnctElementSize)
			return true;
		return false;
	}

	void setAllFDs() {
		// to isolate all function dependencies
		String[] fds = inputString.split(",");
		// to define determinant and dependent for each Functional Dependency

		for (int i = 0; i < fds.length; i++) {
			// for each A-B ( A determines B ) A is determinant And B is dependent
			FDs currentFDs = new FDs();
			String[] isolatorString = fds[i].split("-");
			// A
			currentFDs.setDeterminant(isolatorString[0]);
			// B
			currentFDs.setDependent(isolatorString[1]);
			// to add the object of functional dependency
			FDList.add(currentFDs);
		}
		Fsize = FDList.size();
		System.out.println("file has " + Fsize + " Functional Dependency ");
	}
	
	public void setAllFDsElements() {
		String[] commaLessArray = inputString.split(",");
		String commaLessString = "";
		for (String s : commaLessArray) {
			commaLessString += s;
		}
		String[] dashLessArray = commaLessString.split("-");
		String dashLessString = "";
		for (String s : dashLessArray) {
			dashLessString += s;
		}
		char[] elements = dashLessString.toCharArray();
		String castedChar = "";
		for (char c : elements) {
			castedChar = "" + c;
			if (!(allAttributesString.contains(castedChar)))
				allAttributesString += castedChar;
		}
		System.out.println("All Attributes is : " + allAttributesString + "\n");

		// set all attributes of the inputString
		allAttributes = allAttributesString.toCharArray();
	}

	public String getClosure(String attribute) {
		String oldXClosure = "";
		String xClosure = attribute;
		String determinant = "";
		String dependent = "";
		while (true) {
			for (int i = 0; i < Fsize; i++) {
				// current determinant (A) and dependent (B)
				determinant = FDList.get(i).getDeterminant();
				dependent = FDList.get(i).getDependent();
				if (isSubset(xClosure, determinant)) {
					// this if condition to prevent redundancy
					char[] dependentChar = dependent.toCharArray();
					String currentElement;
					for (int j = 0; j < dependent.length(); j++) {
						currentElement = "" + dependentChar[j];
						if (!(xClosure.contains(currentElement)))
							xClosure += currentElement;
					}
				}
			}
			// here is the end of the current round
			if (oldXClosure.equals(xClosure)) {
				break;
			}
			oldXClosure = xClosure;
		}
		return xClosure;
	}

	public void checkFD(String newFD) {
		// to check if the inputed fun. dependency is correct or not based on given FDs
		String[] temp = newFD.split("-");
		String determinant = temp[0];
		String dependent = temp[1];
		String c = getClosure(determinant);
		System.out.println("closure of '" + determinant + "' is : " + c);

		if (c.contains(dependent)) {
			System.out.println("this functional Dependency is correct");
		} else {
			System.out.println("this functional Dependency is not correct");
		}
	}

	public ArrayList<String> findCandidateKeys() {
		ArrayList<String> validKeys = new ArrayList<String>();
		String currentDeterminant = "";
		String currentClosure = "";

		for (FDs fd : FDList) {
			currentDeterminant = fd.getDeterminant();
			currentClosure = getClosure(currentDeterminant);
			if (isContainsAll(allAttributesString, currentClosure)) {
				if(!(validKeys.contains(currentDeterminant)))
					validKeys.add(currentDeterminant);
			}
		}
		return validKeys;
	}

	public ArrayList<String> findCompositeKey() {
		ArrayList<String> validKeys = new ArrayList<String>();
		String currentDeterminant = "";
		String currentClosure = "";
		char[] FDsChars = inputString.toCharArray();
		String currentChar = "";
		for (char chars : FDsChars) {
			currentChar = "" + chars;
			for (FDs fd : FDList) {
				currentDeterminant = currentChar + fd.getDeterminant();
				currentClosure = getClosure(currentDeterminant);
				if (isContainsAll(allAttributesString, currentClosure)) {
					validKeys.add(currentDeterminant);
					return validKeys;
				}
			}
		}
		return validKeys;
	}

	public ArrayList<String> getInitialKeys() {
		ArrayList<String> validKeys = findCandidateKeys();
		int numberOfValidKeys = 0;
		if (!(validKeys.isEmpty())) {
			numberOfValidKeys = validKeys.size();
			System.out.print("there is " + numberOfValidKeys + " available candidate Key = ");
			System.out.println(validKeys);
		} else if (validKeys.isEmpty()) {
			validKeys = findCompositeKey();
			numberOfValidKeys = validKeys.size();
			System.out.print("there is " + numberOfValidKeys + " available candidate Key = ");
			System.out.println(validKeys);
		}
		System.out.println("the primary key is : " + validKeys.get(0));
		System.out.println("\n ___________________________________________\n");
		return validKeys;
	}

	public void setPrimaryKey() {
		// this function is to assign one of candidate keys with the least size as
		// primary key
		ArrayList<String> candidateKeys= initialKeys;
		int minSize = candidateKeys.get(0).length();
		String PK = candidateKeys.get(0);
		for (String key : candidateKeys) {
			if (key.length() < minSize)
				PK = key;
		}
		primaryKey.add(PK);
	}

	public boolean isPrimeAttribute(String attribute) {
		String prime = "";
		for (String key : initialKeys) {
			prime += key;
		}
		if (prime.contains(attribute))
			return true;

		return false;
	}

	public boolean isPartOfAnyCandKey(String determinant) {
		int determinantLength = determinant.length();
		int keyLength = 0;

		for (String key : initialKeys) {
			keyLength = key.length();
			if (isSubset(key, determinant) && (determinantLength < keyLength))
				return true;
		}
		return false;
	}

	public ArrayList<ArrayList<FDs>> clearFDList(ArrayList<ArrayList<FDs>> FDList) {
		ArrayList<ArrayList<FDs>> cleared = new ArrayList<ArrayList<FDs>>();
		for (int i = 0; i < FDList.size(); i++) {
			if (!(FDList.get(i).isEmpty())) {
				cleared.add(FDList.get(i));
			}
		}
		return cleared;
	}

	public ArrayList<ArrayList<FDs>> get2NF() {
		/* iff it has No Partial Dependency, i.e., no non-prime
		 * attribute (attributes which are not part of any candidate key) is dependent
		 * on any proper subset of any candidate key of the table. *
		 */
		ArrayList<ArrayList<FDs>> toBeConverted = new ArrayList<ArrayList<FDs>>();
		toBeConverted.add(FDList);
		ArrayList<ArrayList<FDs>> converted2NF = new ArrayList<ArrayList<FDs>>();
		converted2NF.addAll(toBeConverted);
		String determinant = "";
		String dependent = "";
		int currentFdsSet = 0;
		for (ArrayList<FDs> set : toBeConverted) {
			ArrayList<FDs> FDsOfNewRelation = new ArrayList<FDs>();
			for (FDs fd : set) {
				determinant = fd.getDeterminant();
				dependent = fd.getDependent();
				if (isPartOfAnyCandKey(determinant) && !(isPrimeAttribute(dependent))) {
		//then there is partial dep. & this Fun.dep isn't 2NF so add it to a new relation
					FDsOfNewRelation.add(fd);
				}
			}
			converted2NF.get(currentFdsSet).removeAll(FDsOfNewRelation);
			converted2NF.add(FDsOfNewRelation);
			currentFdsSet++;
		}
		converted2NF = clearFDList(converted2NF);
		return converted2NF;
	}

	public ArrayList<ArrayList<FDs>> get3NF() {
		/* iff at least one of the following condition holds
		 *  1-  in every  X–>Y, X is super key
		 * 	2- Y is a prime attribute (each element of Y is part of anyCandKey
		 */
		ArrayList<ArrayList<FDs>> toBeConverted = new ArrayList<ArrayList<FDs>>();
		toBeConverted = get2NF();
		int lengthOf2NF = toBeConverted.size();
		ArrayList<ArrayList<FDs>> converted3NF = new ArrayList<ArrayList<FDs>>();
		converted3NF = toBeConverted;
		String determinant = "";
		String dependent = "";
		for (int i = 0; i < lengthOf2NF; i++) {
			ArrayList<FDs> FDsOfNewRelation = new ArrayList<FDs>();
			for (int j = 0; j < toBeConverted.get(i).size(); j++) {
				determinant = toBeConverted.get(i).get(j).getDeterminant();
				dependent = toBeConverted.get(i).get(j).getDependent();
				if (!(initialKeys.contains(determinant)) && !(isPrimeAttribute(dependent))) {
					// then the relation is not in 3NF so we will add it to a new relation
					FDsOfNewRelation.add(toBeConverted.get(i).get(j));
					converted3NF.get(i).remove(toBeConverted.get(i).get(j));
				}
			}
			if (!(FDsOfNewRelation.isEmpty()))
				converted3NF.add(FDsOfNewRelation);
		}
		converted3NF = clearFDList(converted3NF);
		return converted3NF;
	}

	public ArrayList<ArrayList<FDs>> getBCNF() {
	//in BCNF iff in every non-trivial functional dependency X–>Y ,all X is a super key.
		ArrayList<ArrayList<FDs>> toBeConverted = new ArrayList<ArrayList<FDs>>();
		toBeConverted = get3NF();
		int lengthOf3NF = toBeConverted.size();
		ArrayList<ArrayList<FDs>> convertedBCNF = new ArrayList<ArrayList<FDs>>();
		convertedBCNF = toBeConverted;
		String determinant = "";
		
		for (int i = 0; i < lengthOf3NF; i++) {
			ArrayList<FDs> FDsOfNewRelation = new ArrayList<FDs>();
			for (int j = 0; j < toBeConverted.get(i).size(); j++) {
				determinant = toBeConverted.get(i).get(j).getDeterminant();
				if (!(initialKeys.contains(determinant))) {
				// then the relation is not in BCNF so we will add it to a new relation
					FDsOfNewRelation.add(toBeConverted.get(i).get(j));
					convertedBCNF.get(i).remove(toBeConverted.get(i).get(j));
				}
			}
			if (!(FDsOfNewRelation.isEmpty())) {
				convertedBCNF.add(FDsOfNewRelation);
			}
		}
		convertedBCNF = clearFDList(convertedBCNF);
		return convertedBCNF;
	}

	public void printFunctionalDependencies(ArrayList<ArrayList<FDs>> allNF) {
		int numOfDividedRelations = allNF.size();
		int sizeOfRelation = 0;
		String determinant = "";
		String dependent = "";
		for (int i = 0; i < numOfDividedRelations; i++) {
			System.out.println("Functional Dependencies Of the Relation #" + (i + 1));
			sizeOfRelation = allNF.get(i).size();
			for (int j = 0; j < sizeOfRelation; j++) {
				determinant = allNF.get(i).get(j).getDeterminant();
				dependent = allNF.get(i).get(j).getDependent();
				System.out.print(determinant + "-" + dependent);
				if (j != sizeOfRelation - 1) 
					System.out.print(",");				
			}
			System.out.println();
		}
	}

	public String stateTheNormalForm() {
		ArrayList<ArrayList<FDs>> _2NF = get2NF();
		ArrayList<ArrayList<FDs>> _3NF = get3NF();
		ArrayList<ArrayList<FDs>> _BCNF = getBCNF();

		int size1NF, size2NF, size3NF, sizeBCNF;
		size2NF = _2NF.size();
		size3NF = _3NF.size();
		sizeBCNF = _BCNF.size();
		size1NF = 1;
		// =1 because the first normal form has 1 set of relations
		if (size1NF != size2NF)
			return "1NF";
		else if (size2NF != size3NF)
			return "2NF";
		else if (size3NF != sizeBCNF)
			return "3NF";
		else
			return "BCNF";
	}
}
