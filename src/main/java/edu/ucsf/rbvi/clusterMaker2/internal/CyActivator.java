package edu.ucsf.rbvi.clusterMaker2.internal;


import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.ENABLE_FOR;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

// Java imports
import java.util.Properties;

// Cytoscape imports
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NetworkTaskFactory;
import org.osgi.framework.BundleContext;

// clusterMaker imports
import edu.ucsf.rbvi.clusterMaker2.internal.api.ClusterManager;
import edu.ucsf.rbvi.clusterMaker2.internal.api.ClusterTaskFactory;
import edu.ucsf.rbvi.clusterMaker2.internal.api.ClusterViz;
import edu.ucsf.rbvi.clusterMaker2.internal.ClusterManagerImpl;

// Algorithms
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.attributeClusterers.AttributeClusterTaskFactory;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.networkClusterers.NetworkClusterTaskFactory;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.networkClusterers.AP.APClusterTaskFactory;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.networkClusterers.FCM.FCMClusterTaskFactory;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.networkClusterers.GLay.GLayClusterTaskFactory;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.networkClusterers.MCL.MCLClusterTaskFactory;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.networkClusterers.MCODE.MCODEClusterTaskFactory;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.networkClusterers.SCPS.SCPSClusterTaskFactory;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.networkClusterers.TransClust.TransClustClusterTaskFactory;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.clusterFilters.FilterTaskFactory;

public class CyActivator extends AbstractCyActivator {

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {

		// See if we have a graphics console or not
		boolean haveGUI = true;
		CySwingApplication swingAppRef = getService(bc, CySwingApplication.class);
		if (swingAppRef == null) {
			// if haveGUI is false, we don't want to provide any hooks to the treeview
			haveGUI = false;
		}
		CyApplicationManager appRef = getService(bc, CyApplicationManager.class);
		CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);
		CyGroupManager groupManager = getService(bc, CyGroupManager.class);
		CyTableFactory tableFactory = getService(bc, CyTableFactory.class);
		CyTableManager tableManager = getService(bc, CyTableManager.class);


		// Create our context object.  This will probably keep track of all of the
		// registered clustering algorithms, settings, etc.
		ClusterManagerImpl clusterManager = new ClusterManagerImpl(appRef, serviceRegistrar, groupManager, tableFactory, tableManager );

		registerServiceListener(bc, clusterManager, "addClusterAlgorithm", "removeClusterAlgorithm", ClusterTaskFactory.class);
		registerServiceListener(bc, clusterManager, "addClusterVisualizer", "removeClusterVisualizer", ClusterViz.class);

		// Register each of our algorithms
		registerService(bc, new AttributeClusterTaskFactory(clusterManager), 
		                ClusterTaskFactory.class, new Properties());
		registerService(bc, new NetworkClusterTaskFactory(clusterManager), 
		                ClusterTaskFactory.class, new Properties());
		registerService(bc, new APClusterTaskFactory(clusterManager), ClusterTaskFactory.class, new Properties());
		registerService(bc, new FCMClusterTaskFactory(clusterManager), ClusterTaskFactory.class, new Properties());
		registerService(bc, new GLayClusterTaskFactory(clusterManager), ClusterTaskFactory.class, new Properties());
		registerService(bc, new MCLClusterTaskFactory(clusterManager), ClusterTaskFactory.class, new Properties());
		registerService(bc, new MCODEClusterTaskFactory(clusterManager), ClusterTaskFactory.class, new Properties());
		registerService(bc, new SCPSClusterTaskFactory(clusterManager), ClusterTaskFactory.class, new Properties());
		registerService(bc, new TransClustClusterTaskFactory(clusterManager), 
		                ClusterTaskFactory.class, new Properties());
		registerService(bc, new FilterTaskFactory(clusterManager), ClusterTaskFactory.class, new Properties());
	}

}
