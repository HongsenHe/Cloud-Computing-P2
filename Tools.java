package cloudP2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Tools {
	// key is ip's long, value is IP 
	static HashMap<Long, String> ipList = new HashMap<Long, String>();
	// key is music's IP long, value is its hex
	static HashMap<Long, String> musicHex = new HashMap<Long, String>();
	
	// convert IP address to long type, source from web
	public static long ipToLong(String ipAddress) {
		long result = 0;
		String[] atoms = ipAddress.split("\\.");

		for (int i = 3; i >= 0; i--) {
			result |= (Long.parseLong(atoms[3 - i]) << (i * 8));
		}
		return result & 0xFFFFFFFF;
	}

	// convert the music name into a long
	// e.g. Listen to the Music -> List/en t/o th/e Mu/sic
	public static long stringToLong(String str) {
		// convert this string to hex, then reverse odd number chunks
		// also, this string should be divided into 4-byte chunks
		long result = 0;
		int addi = 4 - str.length() % 4;

		char[] strCharArr = new char[str.length() + addi];
		char[] tempStrCharArr = str.toCharArray();
		for(int b = 0; b < tempStrCharArr.length; b++){
			strCharArr[b] = tempStrCharArr[b];
		}
		
		String each = "";
		ArrayList<String> chunkList = new ArrayList<String>();
		for (int j = 0; j < strCharArr.length; j++) {
			each += String.valueOf(strCharArr[j]);
			
			if ((j + 1) % 4 == 0) {
				// convert each chunk to hex	
				String hex = stringToHex(each);
				chunkList.add(hex);
				each = "";
			}
		}

		// convert odd index chunk
		int flag = 0;
		String[] chunkList2 = new String[chunkList.size()];
		int index = 0;
		for (String eachChunk : chunkList) {
			String binChunk = hexToBin(eachChunk);				
			flag ++;
			if(flag%2 == 1){
				// reverse bin, then convert it to hex, then save
				String revString = rvsStr(binChunk);
				String revHex = binToHex(revString);
				chunkList2[index] = revHex;
			}
			else{				
				String revHex2 = binToHex(binChunk);
				chunkList2[index] = revHex2;
			}
			index++;
		}
		
		// after reverse, convert hex to long
		String XORResult = "";
		for(int m = 0; m < chunkList2.length; m++){
			// special case
			if(chunkList2.length == 1){
				XORResult = chunkList2[0];
				break;
			}
			if (m == 0){
				XORResult = chunkList2[0];
			}else{
				XORResult = XOR(XORResult, chunkList2[m]);
			}
			
		}
		result = hexToLong(XORResult);
		String songHex = "0x" + XORResult;
		musicHex.put(result, songHex);
		return result;
	}
	
	// convert from string to hex
	public static String stringToHex(String str) {
		String hex = "";
		hex = String.format("%x", new BigInteger(1, str.getBytes()));
		return hex;
	}

	// convert from hex to binary
	public static String hexToBin(String hex) {
		String bin = "";
		int i = Integer.parseInt(hex, 16);
		bin = Integer.toBinaryString(i);
		return bin;
	}

	// reverse a string
	public static String rvsStr(String str){
		// a trick method
		String result = "";
		String zero = "0";
		int addi = 0;
		if(str.length()< 32){
			addi = 32 - str.length();
		}
		for(int i = 0; i < addi; i++){
			str = zero + str;
		}
		
		char[] strCA = str.toCharArray();
		for(int i = strCA.length-1; i > -1; i--){
			result += String.valueOf(strCA[i]);
		}
		return result;
	}
	
	// convert from binary to hex
	public static String binToHex(String bin){
		String hex = "";
		hex = new BigInteger(bin,2).toString(16);
		return hex;
	}
	
	// do XOR 
	public static String XOR(String str1, String str2){
		String result = "";
		BigInteger i1 = new BigInteger(str1, 16);
		BigInteger i2 = new BigInteger(str2, 16);
		BigInteger res = i1.xor(i2);
		result = res.toString(16);
		return result;
	}
	
	// convert hex to long
	public static long hexToLong(String hex){
		long result = 0;
		result = Long.parseLong(hex, 16);
		return result;
	}
}
