/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.palladiosimulator.simulizar.prm;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import de.uka.ipd.sdq.identifier.Identifier;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>PRM Model</b></em>'. <!--
 * end-user-doc -->
 *
 * <!-- begin-model-doc --> Base class representing the prm model. <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.palladiosimulator.simulizar.prm.PRMModel#getMeasurements <em>Measurements</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.palladiosimulator.simulizar.prm.PrmPackage#getPRMModel()
 * @model
 * @generated
 */
public interface PRMModel extends EObject, Identifier {
    /**
     * Returns the value of the '<em><b>Measurements</b></em>' containment reference list. The list
     * contents are of type {@link org.palladiosimulator.simulizar.prm.PRMMeasurement}. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Measurements</em>' containment reference list isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Measurements</em>' containment reference list.
     * @see org.palladiosimulator.simulizar.prm.PrmPackage#getPRMModel_Measurements()
     * @model containment="true"
     * @generated
     */
    EList<PRMMeasurement> getMeasurements();

} // PRMModel
