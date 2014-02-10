/* vim: set ts=2: */
/**
 * Copyright (c) 201 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package edu.ucsf.rbvi.clusterMaker2.internal.algorithms.clusterFilters.Density;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.NodeCluster;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.clusterFilters.AbstractNetworkFilter;
import edu.ucsf.rbvi.clusterMaker2.internal.api.ClusterManager;
import edu.ucsf.rbvi.clusterMaker2.internal.utils.ModelUtils;

public class DensityFilter extends AbstractNetworkFilter {
	public static String SHORTNAME = "density";
	public static String NAME = "Density Filter";
	public final static String GROUP_ATTRIBUTE = "__DensityGroups.SUID";

	@Tunable(description="Network to filter", context="nogui")
	public CyNetwork network = null;
	
	@ContainsTunables
	public DensityContext context = null;

	public DensityFilter(DensityContext context, ClusterManager clusterManager) {
		super(clusterManager, GROUP_ATTRIBUTE);
		this.context = context;
		if (network == null)
			network = clusterManager.getNetwork();
		context.setNetwork(network);
		super.network = network;
	}

	public String getShortName() { return SHORTNAME; }

	public String getName() { return NAME; }

	public String getClusterAttribute() {
		return context.getClusterAttribute();
	}

	public String getClusterAttributeName() {
		return context.getClusterAttributeName();
	}

	public boolean restoreEdges() {return context.vizProperties.restoreEdges;}
	public boolean showUI() {return context.vizProperties.showUI;}

	public NodeCluster doFilter(List<CyNode>nodeList, Map<NodeCluster, List<CyNode>> addedNodeMap) {
		// Get the number of edges within the cluster
		List<CyEdge> edgeList = ModelUtils.getConnectingEdges(network,nodeList);
		if (edgeList == null) return null;
		int edgeCount = edgeList.size();
		int nodeCount = nodeList.size();

		// Calculate the density
		double density = (double)(edgeCount*2)/(double)(nodeCount * (nodeCount-1));
		if (density >= context.minimumDensity)
			return new NodeCluster(nodeList);
		return null;
	}

}
