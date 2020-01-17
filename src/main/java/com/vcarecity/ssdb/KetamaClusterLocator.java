package com.vcarecity.ssdb;

import com.vcarecity.utils.HashAlgorithm;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 对Hash一致算法还没有搞明白，所以这个类先写这里，暂时还没有使用
 */

public final class KetamaClusterLocator {
	
	private TreeMap<Long,Cluster> ketamaNodes;
	private HashAlgorithm hashAlg;
	private int numReps = 160;
	
    public KetamaClusterLocator(List<Cluster> nodes, HashAlgorithm alg, int nodeCopies) {
		hashAlg = alg;
		ketamaNodes=new TreeMap<Long, Cluster>();
		
        numReps= nodeCopies;
        
		for (Cluster node : nodes) {
			for (int i = 0; i < numReps / 4; i++) {
				byte[] digest = hashAlg.computeMd5(node.getId() + i);
				for(int h = 0; h < 4; h++) {
					long m = hashAlg.hash(digest, h);
					ketamaNodes.put(m, node);
				}
			}
		}
    }

	public Cluster getPrimary(final String k) {
		byte[] digest = hashAlg.computeMd5(k);
		Cluster rv=getNodeForKey(hashAlg.hash(digest, 0));
		return rv;
	}

	Cluster getNodeForKey(long hash) {
		final Cluster rv;
		Long key = hash;
		if(!ketamaNodes.containsKey(key)) {
			SortedMap<Long, Cluster> tailMap=ketamaNodes.tailMap(key);
			if(tailMap.isEmpty()) {
				key=ketamaNodes.firstKey();
			} else {
				key=tailMap.firstKey();
			}
			//For JDK1.6 version
//			key = ketamaNodes.ceilingKey(key);
//			if (key == null) {
//				key = ketamaNodes.firstKey();
//			}
		}
		
		rv=ketamaNodes.get(key);
		return rv;
	}
}
