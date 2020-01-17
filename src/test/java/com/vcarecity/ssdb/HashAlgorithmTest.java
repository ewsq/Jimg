package com.vcarecity.ssdb;

import com.vcarecity.utils.HashAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HashAlgorithmTest {

	static Random ran = new Random();
	
	/** key's count */
	private static final Integer EXE_TIMES = 100000;
	
	private static final Integer Cluster_COUNT = 5;
	
	private static final Integer VIRTUAL_Cluster_COUNT = 160;
	
	public static void main(String[] args) {
		HashAlgorithmTest test = new HashAlgorithmTest();
		
		/** Records the times of locating Cluster*/
		Map<Cluster, Integer> ClusterRecord = new HashMap<Cluster, Integer>();
		
		List<Cluster> allClusters = test.getClusters(Cluster_COUNT);
		KetamaClusterLocator locator = new KetamaClusterLocator(allClusters, HashAlgorithm.KETAMA_HASH, VIRTUAL_Cluster_COUNT);
		
		List<String> allKeys = test.getAllStrings();
		for (String key : allKeys) {
			Cluster Cluster = locator.getPrimary(key);
			
			Integer times = ClusterRecord.get(Cluster);
			if (times == null) {
				ClusterRecord.put(Cluster, 1);
			} else {
				ClusterRecord.put(Cluster, times + 1);
			}
		}
		
		System.out.println("Clusters count : " + Cluster_COUNT + ", Keys count : " + EXE_TIMES + ", Normal percent : " + (float) 100 / Cluster_COUNT + "%");
		System.out.println("-------------------- boundary  ----------------------");
		for (Map.Entry<Cluster, Integer> entry : ClusterRecord.entrySet()) {
			System.out.println("Cluster name :" + entry.getKey() + " - Times : " + entry.getValue() + " - Percent : " + (float)entry.getValue() / EXE_TIMES * 100 + "%");
		}
		
	}
	
	
	/**
	 * Gets the mock Cluster by the material parameter
	 * 
	 * @param ClusterCount 
	 * 		the count of Cluster wanted
	 * @return
	 * 		the Cluster list
	 */
	private List<Cluster> getClusters(int ClusterCount) {
		List<Cluster> Clusters = new ArrayList<Cluster>();
		
		for (int k = 1; k <= ClusterCount; k++) {
			Cluster Cluster = new Cluster("Cluster" + k);
			Clusters.add(Cluster);
		}
		
		return Clusters;
	}
	
	/**
	 *	All the keys	
	 */
	private List<String> getAllStrings() {
		List<String> allStrings = new ArrayList<String>(EXE_TIMES);
		
		for (int i = 0; i < EXE_TIMES; i++) {
			allStrings.add(generateRandomString(ran.nextInt(50)));
		}
		
		return allStrings;
	}
	
	/**
	 * To generate the random string by the random algorithm
	 * <br>
	 * The char between 32 and 127 is normal char
	 * 
	 * @param length
	 * @return
	 */
	private String generateRandomString(int length) {
		StringBuffer sb = new StringBuffer(length);
		
		for (int i = 0; i < length; i++) {
			sb.append((char) (ran.nextInt(95) + 32));
		}
		
		return sb.toString();
	}
}
