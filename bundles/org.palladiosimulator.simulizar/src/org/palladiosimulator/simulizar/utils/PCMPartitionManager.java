package org.palladiosimulator.simulizar.utils;

import static org.palladiosimulator.simulizar.utils.ResourceUtil.createRuntimeMeasurementModelResource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.analyzer.workflow.jobs.LoadPCMModelsIntoBlackboardJob;
import org.palladiosimulator.commons.emfutils.EMFCopyHelper;
import org.palladiosimulator.pcm.PcmPackage;
import org.palladiosimulator.runtimemeasurement.RuntimeMeasurementFactory;
import org.palladiosimulator.runtimemeasurement.RuntimeMeasurementModel;
import org.palladiosimulator.runtimemeasurement.RuntimeMeasurementPackage;
import org.palladiosimulator.simulizar.runconfig.SimuLizarWorkflowConfiguration;

import de.uka.ipd.sdq.stoex.StoexPackage;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class PCMPartitionManager {

	private static final Logger LOGGER = Logger.getLogger(PCMPartitionManager.class.getName());
	
	private final PCMResourceSetPartition globalPartition;
	private final MDSDBlackboard blackboard;
	private boolean isObservingPcmChanges = false;
    private PCMResourceSetPartition currentPartition;
    private boolean modelsDirty = false;

    /**
     * A log listener which logs all changes in the global PCM model.
     */
    private final Adapter globalPCMChangeListener = new EContentAdapter() {

        @Override
        public void notifyChanged(final Notification notification) {
            super.notifyChanged(notification);
            // The models should not be marked as a new local copy if only individual values, and
            // not references (i.e. wiring) has changed.
            if (isPCMModel(notification.getFeature())
                    && notification.getEventType() != Notification.REMOVING_ADAPTER) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Detected change in global PCM model. Changed object: " + notification.getNotifier());
                    LOGGER.debug(notification.toString());
                }
                PCMPartitionManager.this.modelsDirty = true;
            }
        }

        private boolean isPCMModel(Object feature) {
            if(EcorePackage.eINSTANCE.getEObject().isInstance(feature)
                    && EcorePackage.eINSTANCE.getEStructuralFeature().isInstance(feature)){
                EStructuralFeature eStructFeature = (EStructuralFeature) feature;
                EClass containingClass = eStructFeature.getEContainingClass();
                EObject rootContainer = EcoreUtil.getRootContainer(containingClass);
                
                if(PcmPackage.eINSTANCE == rootContainer || StoexPackage.eINSTANCE == rootContainer) {
                    return true;
                }
            }
            return false;
        }

    };
    
    /**
     * Constructor
     *
     * @param blackboard
     *            the workflow engine's blackboard holding all models.
     */
    public PCMPartitionManager(final MDSDBlackboard blackboard, final SimuLizarWorkflowConfiguration config) {
        this.blackboard = blackboard;   
        this.globalPartition = (PCMResourceSetPartition) blackboard.getPartition(LoadPCMModelsIntoBlackboardJob.PCM_MODELS_PARTITION_ID);
        this.currentPartition = this.copyPCMPartition();
        
        initRuntimeMeasurementModel(config.getMonitorRepositoryFile());
    }
    
    private PCMPartitionManager(PCMPartitionManager managerToCopy) {
    	managerToCopy.checkAndHandleDeferredChanges();
    	this.currentPartition = managerToCopy.currentPartition;
    	this.blackboard = managerToCopy.blackboard;
    	this.globalPartition = managerToCopy.globalPartition;
    }

    private void initRuntimeMeasurementModel(String folder) {
    	if (folder.isEmpty() == false) {
    		createAndSaveRuntimeMeasurementModel(folder).ifPresent(r -> this.globalPartition.loadModel(r.getURI()));
    	}
	}

    private Optional<Resource> createAndSaveRuntimeMeasurementModel(String storingLocation) {
		Resource rmModelResource = createRuntimeMeasurementModelResource(storingLocation);
		rmModelResource.getContents().add(RuntimeMeasurementFactory.eINSTANCE.createRuntimeMeasurementModel());
    	try {
    		rmModelResource.save(Collections.EMPTY_MAP);
    	} catch (IOException ex) {
    		LOGGER.info(String.format("The runtime measurement resource could not be saved: %s", ex.getMessage()));
    		return Optional.empty();
    	}
    	return Optional.of(rmModelResource);
	}

	public PCMResourceSetPartition getGlobalPCMModel() {
        return this.globalPartition;
    }
    
	public PCMResourceSetPartition getLocalPCMModel() {
        checkAndHandleDeferredChanges();
        return this.currentPartition;
    }

    private void checkAndHandleDeferredChanges() {
        if (this.modelsDirty) {
            this.currentPartition = this.copyPCMPartition();
            this.modelsDirty = false;
        }
    }
    
    /**
     * @return a copy of the global PCM modeling partition
     */
    private PCMResourceSetPartition copyPCMPartition() {
        LOGGER.debug("Take a new copy of the global PCM for new simulation threads");
        final PCMResourceSetPartition newPartition = new PCMResourceSetPartition();
        final List<EObject> modelCopy = EMFCopyHelper.deepCopyToEObjectList(this.globalPartition.getResourceSet());
        for (int i = 0; i < modelCopy.size(); i++) {
            final Resource resource = newPartition.getResourceSet().createResource(URI.createFileURI("/temp" + i));
            resource.getContents().add(modelCopy.get(i));
        }
        return newPartition;
    }

    /**
     * Calling this method has the current instance observe the {@link PCMResourceSetPartition} in
     * order to keep track of model changes.
     *
     * @see #getGlobalPCMModel()
     * @see #stopObservingPcmChanges()
     */
    public void startObservingPcmChanges() {
    	if (!this.isObservingPcmChanges) {
        	filterResourcesToListen().forEach(resource -> addListenerTo(resource));
            this.isObservingPcmChanges = true;
        }
    }
    
    /**
     * Calling this method has the current instance stop observing the
     * {@link PCMResourceSetPartition}.
     *
     * @see #getGlobalPCMModel()
     * @see #startObservingPcmChanges()
     */
    public void stopObservingPcmChanges() {
        if (this.isObservingPcmChanges) {
            filterResourcesToListen().forEach(resource -> removeListenerFrom(resource));
            this.isObservingPcmChanges = false;
        }
    }
    
    private List<Resource> filterResourcesToListen() {
    	Stream<Resource> resources = this.globalPartition.getResourceSet().getResources().stream();
		return resources.filter(resourcesToListen())
						.collect(Collectors.toList());
	}
    
    private Predicate<Resource> resourcesToListen() {
    	return r -> {
    		if (r.getContents().isEmpty()) {
    			return false;
    		}
    		return r.getContents().get(0) instanceof RuntimeMeasurementModel ? false : true;
    	};
    }

	private void addListenerTo(Resource resource) {
    	final List<Adapter> adapters = resource.eAdapters();
    	if (adapters.contains(this.globalPCMChangeListener) == false) {
    		adapters.add(this.globalPCMChangeListener);
	    }
	}
	
	private void removeListenerFrom(Resource resource) {
		resource.eAdapters().remove(this.globalPCMChangeListener);
	}
	
	public <T extends EObject> T findModel(EClass targetType) {
		List<T> result = this.globalPartition.getElement(targetType);
		if (result.isEmpty()) {
			LOGGER.info(String.format("No model with target type %s was found.", targetType));
            return null;
		}
		return result.get(0);
	}
	
	public PCMPartitionManager makeSnapshot() {
		return new PCMPartitionManager(this);
	}
	
	public MDSDBlackboard getBlackboard() {
		return this.blackboard;
	}
	
	public void cleanUp() {
		EClass targetType = RuntimeMeasurementPackage.eINSTANCE.getRuntimeMeasurementModel();
		Optional.ofNullable(findModel(targetType)).ifPresent(eObj -> delete((RuntimeMeasurementModel) eObj));
	}

	private void delete(RuntimeMeasurementModel rmModel) {
		try {
			rmModel.eResource().delete(Collections.EMPTY_MAP);
		} catch (IOException e) {
			LOGGER.info(String.format("The runtime measurement resource could not be deleted: %s", e.getMessage()));
		}
	}
}
