package com.digiarea.jse.es5;

import com.digiarea.jse.CompilationUnit;
import com.digiarea.jse.NameExpr;
import com.digiarea.jse.Node;
import com.digiarea.jse.TypeDeclaration;
import com.digiarea.jse.utils.Enclosure;



public class Context {

	private TypeDeclaration typeDeclaration = null;

	private CompilationUnit compilationUnit = null;

	private Node parent = null;

	private boolean anonymousClass = false;

	/** The enclosure. */
	private Enclosure enclosure;

	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public void setCompilationUnit(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public TypeDeclaration getTypeDeclaration() {
		return typeDeclaration;
	}

	public void setTypeDeclaration(TypeDeclaration typeDeclaration) {
		this.typeDeclaration = typeDeclaration;
	}

	public Node getParent() {
		return parent;
	}

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

	public boolean isAnonymousClass() {
		return anonymousClass;
	}

	public void setAnonymousClass(boolean anonymousClass) {
		this.anonymousClass = anonymousClass;
	}

}
