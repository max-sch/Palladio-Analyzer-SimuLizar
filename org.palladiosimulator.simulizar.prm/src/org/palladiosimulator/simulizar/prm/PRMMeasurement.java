/**
 */
package org.palladiosimulator.simulizar.prm;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.simulizar.monitorrepository.MeasurementSpecification;

import de.uka.ipd.sdq.identifier.Identifier;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>PRM Measurement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc --> Measurement for a pcm model element. <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.palladiosimulator.simulizar.prm.PRMMeasurement#getMeasuringPoint <em>Measuring
 * Point</em>}</li>
 * <li>{@link org.palladiosimulator.simulizar.prm.PRMMeasurement#getMeasurementSpecification <em>
 * Measurement Specification</em>}</li>
 * <li>{@link org.palladiosimulator.simulizar.prm.PRMMeasurement#getMeasuringValue <em>Measuring
 * Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.palladiosimulator.simulizar.prm.PrmPackage#getPRMMeasurement()
 * @model
 * @generated
 */
public interface PRMMeasurement extends EObject, Identifier {
    /**
     * Returns the value of the '<em><b>Measuring Point</b></em>' reference. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Measuring Point</em>' reference isn't clear, there really should
     * be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Measuring Point</em>' reference.
     * @see #setMeasuringPoint(MeasuringPoint)
     * @see org.palladiosimulator.simulizar.prm.PrmPackage#getPRMMeasurement_MeasuringPoint()
     * @model
     * @generated
     */
    MeasuringPoint getMeasuringPoint();

    /**
     * Sets the value of the '
     * {@link org.palladiosimulator.simulizar.prm.PRMMeasurement#getMeasuringPoint
     * <em>Measuring Point</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value
     *            the new value of the '<em>Measuring Point</em>' reference.
     * @see #getMeasuringPoint()
     * @generated
     */
    void setMeasuringPoint(MeasuringPoint value);

    /**
     * Returns the value of the '<em><b>Measurement Specification</b></em>' reference. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Measurement Specification</em>' reference isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Measurement Specification</em>' reference.
     * @see #setMeasurementSpecification(MeasurementSpecification)
     * @see org.palladiosimulator.simulizar.prm.PrmPackage#getPRMMeasurement_MeasurementSpecification()
     * @model
     * @generated
     */
    MeasurementSpecification getMeasurementSpecification();

    /**
     * Sets the value of the '
     * {@link org.palladiosimulator.simulizar.prm.PRMMeasurement#getMeasurementSpecification
     * <em>Measurement Specification</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value
     *            the new value of the '<em>Measurement Specification</em>' reference.
     * @see #getMeasurementSpecification()
     * @generated
     */
    void setMeasurementSpecification(MeasurementSpecification value);

    /**
     * Returns the value of the '<em><b>Measuring Value</b></em>' attribute. The default value is
     * <code>"0.0"</code>. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Measuring Value</em>' attribute isn't clear, there really should
     * be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Measuring Value</em>' attribute.
     * @see #setMeasuringValue(double)
     * @see org.palladiosimulator.simulizar.prm.PrmPackage#getPRMMeasurement_MeasuringValue()
     * @model default="0.0"
     * @generated
     */
    double getMeasuringValue();

    /**
     * Sets the value of the '
     * {@link org.palladiosimulator.simulizar.prm.PRMMeasurement#getMeasuringValue
     * <em>Measuring Value</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value
     *            the new value of the '<em>Measuring Value</em>' attribute.
     * @see #getMeasuringValue()
     * @generated
     */
    void setMeasuringValue(double value);

} // PRMMeasurement
