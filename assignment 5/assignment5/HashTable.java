package assignment5;

import java.io.IOException;

public class HashTable {
	private DataItem[] hashArray;

	private int arraySize;

	public HashTable(int size) {
		arraySize = size;
		hashArray = new DataItem[arraySize];
	}

	public int hashFunction(int key) {
		return key % arraySize;
	}
	
	public int doubleFun(int key) {
		int r = arraySize - 5;
		return r + key % r;
	}

	public void insert(DataItem item) {
		int key = item.getKey();
		int hashVal = hashFunction(key); // hash the key
		int i=0;

		while (hashArray[hashVal] != null && hashArray[hashVal].getKey() != -1) {
		//choose one from three methods as follow:
			// 1.linear probing
			   // ++hashVal; 
			// 2.quadratic probing
		       // hashVal = hashVal+i*i; 
			// 3.double hashing	
			hashVal = (hashVal + i * doubleFun(key)); 
			 i++;
			hashVal %= arraySize;
		}
		hashArray[hashVal] = item; 
	}

	public static void main(String[] args) throws IOException {
		DataItem aDataItem;
		int aKey, hashSize, keySize, keysPerCell;
		double loadFac;

		//keysize should be changed following hashsize
		hashSize = 1000;
		keySize = 450;
		keysPerCell = 10;
		HashTable theHashTable = new HashTable(hashSize);

		long startTime = System.currentTimeMillis();	
		//calculate the average of 10 times
		//for(int i=0;i<10;i++){
		for (int j = 0; j < keySize; j++) {
			aKey = (int) (Math.random() * keysPerCell * hashSize);
			aDataItem = new DataItem(aKey);
			theHashTable.insert(aDataItem);
		 }
		//}
		loadFac = (double) keySize / (double) hashSize;
		System.out.println("Load factor: " + loadFac);
		long endTime = System.currentTimeMillis();
		System.out.println("Cost time: " + (endTime - startTime) + "ms");
	}
}