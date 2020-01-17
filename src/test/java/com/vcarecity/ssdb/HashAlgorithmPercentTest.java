package com.vcarecity.ssdb;

import com.vcarecity.utils.HashAlgorithm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HashAlgorithmPercentTest {

	static Random ran = new Random();
	
	/** key's count */
	private static final Integer EXE_TIMES = 100000;
	
	private static final Integer Cluster_COUNT = 50;
	
	private static final Integer VIRTUAL_Cluster_COUNT = 160;
	
	static List<String> allKeys = null;
	
	static {
		allKeys = getAllStrings();
	}
	
	public static void main(String[] args) {
		
		Map<String, List<Cluster>> map = generateRecord();
		
		List<Cluster> allClusters = getClusters(Cluster_COUNT);
		System.out.println("Normal case : Clusters count : " + allClusters.size());
		call(allClusters, map);
		
		allClusters = getClusters(Cluster_COUNT + 8);
		System.out.println("Added case : Clusters count : " + allClusters.size());
		call(allClusters, map);
		
		allClusters = getClusters(Cluster_COUNT - 10);
		System.out.println("Reduced case : Clusters count : " + allClusters.size());
		call(allClusters, map);
		
		int addCount = 0;
		int reduceCount = 0;
		for (Map.Entry<String, List<Cluster>> entry : map.entrySet()) {
			List<Cluster> list = entry.getValue();
			
			if (list.size() == 3) {
				if (list.get(0).equals(list.get(1))) {
					addCount++;
				}
				
				if (list.get(0).equals(list.get(2))) {
					reduceCount++;
				}
			} else {
				System.out.println("It's wrong size of list, key is " + entry.getKey() + ", size is " + list.size());
			}
		}
		
		System.out.println(addCount + "   ---   " + reduceCount);
		
		System.out.println("Same percent in added case : " + (float) addCount * 100/ EXE_TIMES + "%");
		System.out.println("Same percent in reduced case : " + (float) reduceCount * 100/ EXE_TIMES + "%");
		
	}
	
	private static void call(List<Cluster> Clusters, Map<String, List<Cluster>> map) {
		KetamaClusterLocator locator = new KetamaClusterLocator(Clusters, HashAlgorithm.KETAMA_HASH, VIRTUAL_Cluster_COUNT);
		
		for (Map.Entry<String, List<Cluster>> entry : map.entrySet()) {
			Cluster Cluster = locator.getPrimary(entry.getKey());
			
			if (Cluster != null) {
				List<Cluster> list = entry.getValue();
				list.add(Cluster);
			}
		}
	}
	
	private static Map<String, List<Cluster>> generateRecord() {
		Map<String, List<Cluster>> record = new HashMap<String, List<Cluster>>(EXE_TIMES);
		
		for (String key : allKeys) {
			List<Cluster> list = record.get(key);
			if (list == null) {
				list = new ArrayList<Cluster>();
				record.put(key, list);
			}
		}
		
		return record;
	}
	
	
	/**
	 * Gets the mock Cluster by the material parameter
	 * 
	 * @param ClusterCount 
	 * 		the count of Cluster wanted
	 * @return
	 * 		the Cluster list
	 */
	private static List<Cluster> getClusters(int ClusterCount) {
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
	private static List<String> getAllStrings() {
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
	private static String generateRandomString(int length) {
		StringBuffer sb = new StringBuffer(length);
		
		for (int i = 0; i < length; i++) {
			sb.append((char) (ran.nextInt(95) + 32));
		}
		
		return sb.toString();
	}
}
