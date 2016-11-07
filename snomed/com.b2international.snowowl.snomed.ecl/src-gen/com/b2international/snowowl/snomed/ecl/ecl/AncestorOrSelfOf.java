/**
 */
package com.b2international.snowowl.snomed.ecl.ecl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ancestor Or Self Of</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf#getConstraint <em>Constraint</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getAncestorOrSelfOf()
 * @model
 * @generated
 */
public interface AncestorOrSelfOf extends ExpressionConstraint
{
  /**
   * Returns the value of the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Constraint</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Constraint</em>' containment reference.
   * @see #setConstraint(NestableExpression)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getAncestorOrSelfOf_Constraint()
   * @model containment="true"
   * @generated
   */
  NestableExpression getConstraint();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf#getConstraint <em>Constraint</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Constraint</em>' containment reference.
   * @see #getConstraint()
   * @generated
   */
  void setConstraint(NestableExpression value);

} // AncestorOrSelfOf
