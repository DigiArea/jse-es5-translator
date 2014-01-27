/*******************************************************************************
 * Copyright (c) 2011 - 2014 DigiArea, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     DigiArea, Inc. - initial API and implementation
 *******************************************************************************/
package com.digiarea.jse.es5;

import com.digiarea.jse.CompilationUnit;
import com.digiarea.jse.NameExpr;
import com.digiarea.jse.Node;
import com.digiarea.jse.TypeDeclaration;
import com.digiarea.jse.utils.Enclosure;



/**
 * The Class Context.
 */
public class Context {

	/**
	 * The type declaration.
	 */
	private TypeDeclaration typeDeclaration = null;

	/**
	 * The compilation unit.
	 */
	private CompilationUnit compilationUnit = null;

	/**
	 * The parent.
	 */
	private Node parent = null;

	/**
	 * The anonymous class.
	 */
	private boolean anonymousClass = false;

	/** The enclosure. */
	private Enclosure enclosure;

	/**
	 * Gets the compilation unit.
	 *
	 * @return the compilation unit
	 */
	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	/**
	 * Sets the compilation unit.
	 *
	 * @param compilationUnit the new compilation unit
	 */
	public void setCompilationUnit(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	/**
	 * Gets the type declaration.
	 *
	 * @return the type declaration
	 */
	public TypeDeclaration getTypeDeclaration() {
		return typeDeclaration;
	}

	/**
	 * Sets the type declaration.
	 *
	 * @param typeDeclaration the new type declaration
	 */
	public void setTypeDeclaration(TypeDeclaration typeDeclaration) {
		this.typeDeclaration = typeDeclaration;
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * Sets the parent.
	 *
	 * @param parent the new parent
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * Gets the enclosure.
	 * 
	 * @return the enclosure
	 */
	public NameExpr getEnclosure() {
		return enclosure.get();
	}

	/**
	 * Sets the enclosure.
	 * 
	 * @param enclosure
	 *            the new enclosure
	 */
	public void setEnclosure(NameExpr enclosure) {
		this.enclosure = new Enclosure(enclosure);
	}

	/**
	 * Adds the enclosure.
	 * 
	 * @param name
	 *            the name
	 */
	public void addEnclosure(String name) {
		this.enclosure.add(name);
	}

	/**
	 * Cut enclosure.
	 */
	public void cutEnclosure() {
		enclosure.cut();
	}

	/**
	 * Checks if is anonymous class.
	 *
	 * @return true, if is anonymous class
	 */
	public boolean isAnonymousClass() {
		return anonymousClass;
	}

	/**
	 * Sets the anonymous class.
	 *
	 * @param anonymousClass the new anonymous class
	 */
	public void setAnonymousClass(boolean anonymousClass) {
		this.anonymousClass = anonymousClass;
	}

}
