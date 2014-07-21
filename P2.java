package cloudP2;

import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

public class P2 {

	static Scanner input;
	static HashMap<Integer, String> eachLine;
	static TreeMap<Long, Object> nodeList = new TreeMap<Long, Object>();
	// music id is the key, its successor is the value
	static TreeMap<Long, Long> musicSucs = new TreeMap<Long, Long>();
	// music id is the key, flag is value. flag = 1,can read, flag = 0,cannot
	static TreeMap<Long, Integer> musicFlag = new TreeMap<Long, Integer>();

	public static void main(String[] args) {

		input = new Scanner(System.in);
		String file = "";
		while (input.hasNextLine()) {
			String inputLine = input.nextLine();
			if (inputLine.startsWith("#")) {
				continue;
			} else {
				file += inputLine;
			}
		}
		file = file.trim();

		// run each operator
		String[] fileArr = file.split(";");
		for (int i = 0; i < fileArr.length; i++) {
			int algoResult = 0;
			algoResult = parseInput(fileArr[i]);

			// different algorithm call different method
			switch (algoResult) {
			// result = addNote
			case 1:
				addNode(eachLine.get(algoResult));
				break;
			// result = insertItem
			case 2:
				insertItem(eachLine.get(algoResult));
				break;
			// result = find
			case 3:
				find(eachLine.get(algoResult));
				break;
			// result = deleteItem
			case 4:
				deleteItem(eachLine.get(algoResult));
				break;
			// result = removeNode
			case 5:
				removeNode(eachLine.get(algoResult));
				break;
			case 0:
				System.out.println("Operation Not Found!");
				System.out.println("System exit");
				System.exit(0);
				break;
			}
		}

		// done
	}

	public static int parseInput(String str) {

		int algoNum = 0;

		char[] line = str.toCharArray();
		for (int i = 0; i < line.length; i++) {

			String methodName = "";
			String message = "";

			if (line[i] == '(') {
				methodName = String.valueOf(line).substring(0, i).trim();
				message = String.valueOf(line)
						.substring(i + 1, line.length - 1);
			}

			if (methodName.toUpperCase().equals("ADDNODE")) {
				algoNum = 1;
			} else if (methodName.toUpperCase().equals("INSERTITEM")) {
				algoNum = 2;
			} else if (methodName.toUpperCase().equals("FIND")) {
				algoNum = 3;
			} else if (methodName.toUpperCase().equals("DELETEITEM")) {
				algoNum = 4;
			} else if (methodName.toUpperCase().equals("REMOVENODE")) {
				algoNum = 5;
			}

			if (algoNum != 0) {
				eachLine = new HashMap<Integer, String>();
				eachLine.put(algoNum, message);
				// System.out.println("Key: " + algoNum + "Value: " + message);
				break;
			}

		}

		// System.out.println(eachLine);
		return algoNum;
	}

	// implement each function
	public static void addNode(String str) {

		str = str.replace("\"", "");
		long ip = Tools.ipToLong(str);
		Node node = new Node(ip);
		nodeList.put(ip, node);
		Tools.ipList.put(ip, str);
		
		node.updateFT();
		System.out.println("Success added the node: " + str + "\n");
	}

	public static void insertItem(String str) {
		String result = "";
		String[] strArr = str.split(",");

		// strOID is owner ID, strMID is music ID
		String strOID = strArr[0].replace("\"", "").trim();
		String strMID = strArr[1].replace("\"", "").trim();
		long OID = Tools.ipToLong(strOID);
		long MID = Tools.stringToLong(strMID);

		musicFlag.put(MID, 1);
		Node.updateST(OID, MID);

		result = "Success added the item: ";
		System.out.println(result + strMID + "\n");
	}

	public static void find(String str) {
		String[] strArr = str.split(",");
		String musicName = strArr[1];
		String strNID = strArr[0].replace("\"", "").trim();
		String strMID = strArr[1].replace("\"", "").trim();
		long NID = Tools.ipToLong(strNID);
		long MID = Tools.stringToLong(strMID);

		// looking for NID's successor
		Node targetNode = new Node();
		long printKey = 0L;
		for (long key : nodeList.keySet()) {
			if (key > NID) {
				targetNode = (Node) nodeList.get(key);
				printKey = key;
				break;
			}
			if(NID > nodeList.lastKey()){
				targetNode = (Node) nodeList.get(nodeList.firstKey());
				printKey = nodeList.firstKey();
				break;
			}
		}
		
		
		// print messages:
		/*
		 * I guess: 1. Song is not in the system, return No such item 2. Song is
		 * in the system, but delete, return failed found 3. Song is in the
		 * system, not delete, return success found
		 */
		if (!musicFlag.containsKey(MID)) {
			System.out.println("No such item: " + strMID);
		} else {
			if (musicFlag.get(MID) == 1) {
				System.out.println("Success found: " + strMID);
			} else {
				System.out.println("Failed found: " + strMID);
			}
		}

		System.out.println(musicName + " hashed to " + Tools.musicHex.get(MID));
		System.out.print(strNID + " -> " + Tools.ipList.get(printKey)
				+ " (successor) " + " -> ");

		// find music see MID in which entry of targetNode's FT
		Node.findMusic(targetNode, MID);
		System.out.println();
	}

	public static void deleteItem(String str) {
		String[] strArr = str.split(",");
		// String strNID = strArr[0].replace("\"", "").trim();
		String strMID = strArr[1].replace("\"", "").trim();
		// long NID = Tools.ipToLong(strNID);
		long MID = Tools.stringToLong(strMID);

		if (musicFlag.containsKey(MID)) {
			System.out.println("Success deleted: " + strMID);
			// update the musicFlag treeMap, set flag to 0
			musicFlag.put(MID, 0);
		} else {
			System.out.println("Error: File (" + strMID
					+ ") Not Found! Cannot be deleted!!");
			System.out.println("System exit");
			System.exit(0);
		}
		System.out.println();
	}

	public static void removeNode(String str) {
		str = str.replace("\"", "");
		long rmKey = Tools.ipToLong(str);
		
		for (long key : musicSucs.keySet()) {
			long songSucs = musicSucs.get(key);
			if (songSucs == rmKey) {
				// same key, value is the curKey's successor
				musicSucs.put(key, nodeList.higherKey(rmKey));
			}
		}
		
		if (nodeList.containsKey(rmKey)) {
			nodeList.remove(rmKey);
			Tools.ipList.remove(rmKey);
			System.out.println("Success removed the node: " + str);
		} else {
			System.out.println("Error: Cannot be deleted, No such node: " + str);
			System.out.println("System exit");
			System.exit(0);
		}
		
		Node temp = new Node();
		// update all fingerTable, should be static method, anyway
		temp.updateFT();

		System.out.println();
	}

}
