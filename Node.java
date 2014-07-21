package cloudP2;

import java.util.LinkedHashMap;

public class Node {
	// instance variable
	long id = 0;
	LinkedHashMap<Long, Long> fingerTable;
	final static long NUM = 4294967295L;
	// suppose each node contains max 50 songs
	// first is owner's IP,second is song's ID
	Long[][] songTable = new Long[50][2];

	LinkedHashMap<Long, String> fingerInterval;
	LinkedHashMap<Long, Integer> fingerJ;

	// constructor

	public Node(long id) {
		this.id = id;
	}

	public Node() {
	}

	// method
	// update finger table
	public void updateFT() {
		// update all nodes in NodeList
		for (long nodeID : P2.nodeList.keySet()) {

			long firstKey = P2.nodeList.firstKey();
			long lastKey = P2.nodeList.lastKey();

			Node curNode = new Node();
			// update the current node's fingerTable
			curNode = (Node) P2.nodeList.get(nodeID);

			curNode.fingerTable = new LinkedHashMap<Long, Long>();

			for (int j = 0; j < 32; j++) {
				// k = i + 2^j, k is FT's key
				long k = (long) (nodeID + Math.pow(2, j)) % NUM;

				// special case, only one node
				if (P2.nodeList.size() == 1) {
					curNode.fingerTable.put(k, firstKey);
				} else if (k > lastKey) {
					curNode.fingerTable.put(k, firstKey);
				} else {
					for (long nodeID2 : P2.nodeList.keySet()) {
						if (k < nodeID2) {
							curNode.fingerTable.put(k, nodeID2);
							break;
						} else if (k > nodeID2) {
							continue;
						}
					}
				}
			}

			// after build a fingerTable, then build fingerInterval
			curNode.fingerInterval = new LinkedHashMap<Long, String>();
			curNode.fingerJ = new LinkedHashMap<Long, Integer>();

			for (int jj = 0; jj < 32; jj++) {
				long k1 = (long) (nodeID + Math.pow(2, jj)) % NUM;
				String range = "";

				String numStr = "4294967295";
//				if (CloudP2.nodeList.size() == 1) {
//					range = 0 + ":" + numStr;
//				} else if (CloudP2.nodeList.size() > 1) {
					long k2 = (long) (nodeID + Math.pow(2, jj + 1)) % NUM;
					// if current key is the last key
					if (jj == 31) {
						long k0 = (long) (nodeID + Math.pow(2, jj - 1)) % NUM;
						long firstK = curNode.id + 1; // 49
						if (k1 > k0) {
							range = k1 + ":" + numStr + "&" + 0 + ":" + firstK;
						} else {
							range = k1 + ":" + firstK;
						}
					} else if (k2 > k1) {
						range = k1 + ":" + k2;
					} else if (k2 < k1) {
						range = k1 + ":" + numStr + "&" + 0 + ":" + k2;
					}
				//}
				curNode.fingerInterval.put(k1, range);
				curNode.fingerJ.put(k1, jj);
			}

			// System.out.println("FingerInterval: " + curNode.fingerInterval);
			// System.out.println("FingerJ: " + curNode.fingerJ);
			// System.out.println("Finger table: " + curNode.fingerTable);
		}
	}

	// update song table
	public static void updateST(long OID, long MID) {
		// System.out.println("AAAA: " + MID);
		long lastKey = P2.nodeList.lastKey();
		long firstKey = P2.nodeList.firstKey();

		for (Long key : P2.nodeList.keySet()) {
			Node curNode = new Node();
			curNode = (Node) P2.nodeList.get(key);

			if (key > MID || key == MID) {

				for (int i = 0; i < 50; i++) {
					if (curNode.songTable[i][0] == null) {
						curNode.songTable[i][0] = OID;
						curNode.songTable[i][1] = MID;
						break;
					}
				}
				P2.musicSucs.put(MID, key);
				break;
			}
			// CloudP2.nodeList.h
			if (MID > lastKey) {
				// change to the first node
				curNode = (Node) P2.nodeList.get(firstKey);
				for (int i = 0; i < 50; i++) {
					if (curNode.songTable[i][0] == null) {
						curNode.songTable[i][0] = OID;
						curNode.songTable[i][1] = MID;
						break;
					}
				}
				P2.musicSucs.put(MID, firstKey);
			}
		}
	}

	// looking for music from sharing node
	public static void findMusic(Node targetNode, long MID) {

		long MSID = P2.musicSucs.get(MID);
		
		// parse the range
		for (Long key : targetNode.fingerInterval.keySet()) {
			int flag = 0;
			String range = targetNode.fingerInterval.get(key);

			if (range.contains("&")) {
				String[] rangeArr1 = range.split("&");
				String range1 = rangeArr1[0];
				String range2 = rangeArr1[1];

				String[] range1Arr = range1.split(":");
				long down1 = Long.parseLong(range1Arr[0]);
				long up1 = Long.parseLong(range1Arr[1]);
				
				if (MID < up1 && MID >= down1) {
					flag++;
				}

				String[] range2Arr = range2.split(":");
				long down2 = Long.parseLong(range2Arr[0]);
				long up2 = Long.parseLong(range2Arr[1]);
				if (MID < up2 && MID >= down2) {
					flag++;
				}

				if (flag == 1) {
					int index = targetNode.fingerJ.get(key);
					System.out.println(Tools.ipList.get(MSID)
							+ " (finger table entry j = " + index + ")");
					break;
				}
			} else {
				String[] rangeArr = range.split(":");
				long down = Long.parseLong(rangeArr[0]);
				long up = Long.parseLong(rangeArr[1]);
				
			
				
				if (MID < up && MID >= down) {
					int index = targetNode.fingerJ.get(key);
					System.out.println(Tools.ipList.get(MSID)
							+ " (finger table entry j = " + index + ")");
					break;
				}
			}
		}
	}
}
