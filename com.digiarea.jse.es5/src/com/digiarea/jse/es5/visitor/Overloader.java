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
package com.digiarea.jse.es5.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.digiarea.common.Arrow;
import com.digiarea.jse.AnnotationExpr;
import com.digiarea.jse.BinaryExpr.BinaryOperator;
import com.digiarea.jse.BlockStmt;
import com.digiarea.jse.BodyDeclaration;
import com.digiarea.jse.ClassDeclaration;
import com.digiarea.jse.ClassOrInterfaceType;
import com.digiarea.jse.ConstructorDeclaration;
import com.digiarea.jse.EnumDeclaration;
import com.digiarea.jse.Expression;
import com.digiarea.jse.IfStmt;
import com.digiarea.jse.InterfaceDeclaration;
import com.digiarea.jse.JavadocComment;
import com.digiarea.jse.MethodDeclaration;
import com.digiarea.jse.Modifiers;
import com.digiarea.jse.NodeFacade;
import com.digiarea.jse.Parameter;
import com.digiarea.jse.PrimitiveType;
import com.digiarea.jse.PrimitiveType.Primitive;
import com.digiarea.jse.Project;
import com.digiarea.jse.ReferenceType;
import com.digiarea.jse.Statement;
import com.digiarea.jse.Type;
import com.digiarea.jse.TypeDeclaration;
import com.digiarea.jse.TypeParameter;
import com.digiarea.jse.VariableDeclarationExpr;
import com.digiarea.jse.VoidType;
import com.digiarea.jse.utils.LangUtils;
import com.digiarea.jse.visitor.VoidVisitorAdapter;

/**
 * The Class Overloader.
 */
public class Overloader implements Arrow<Project, Project> {

	/**
	 * The Class Data.
	 */
	private static class Data {

		/**
		 * The Constant JAVA_LANG_OBJECT.
		 */
		private static final String JAVA_LANG_OBJECT = "java.lang.Object";

		/**
		 * The type declaration.
		 */
		private TypeDeclaration typeDeclaration;
		
		/**
		 * The methods.
		 */
		private Map<String, Set<MethodDeclaration>> methods = new HashMap<String, Set<MethodDeclaration>>();
		
		/**
		 * The constructors.
		 */
		private Map<String, Set<ConstructorDeclaration>> constructors = new HashMap<String, Set<ConstructorDeclaration>>();

		/**
		 * Instantiates a new data.
		 *
		 * @param typeDeclaration the type declaration
		 */
		public Data(TypeDeclaration typeDeclaration) {
			super();
			this.typeDeclaration = typeDeclaration;
		}

		/**
		 * Adds the constructor.
		 *
		 * @param constructor the constructor
		 */
		public void addConstructor(ConstructorDeclaration constructor) {
			String name = typeDeclaration.getName();
			Set<ConstructorDeclaration> sorted = null;
			if (!constructors.containsKey(name)) {
				sorted = new HashSet<ConstructorDeclaration>();
				constructors.put(name, sorted);
			} else {
				sorted = constructors.get(name);
			}
			sorted.add(constructor);
		}

		/**
		 * Adds the method.
		 *
		 * @param method the method
		 */
		public void addMethod(MethodDeclaration method) {
			String name = method.getName();
			Set<MethodDeclaration> sorted = null;
			if (!methods.containsKey(name)) {
				sorted = new HashSet<MethodDeclaration>();
				methods.put(name, sorted);
			} else {
				sorted = methods.get(name);
			}
			sorted.add(method);
		}

		/**
		 * Process.
		 */
		public void process() {
			// remove constructors and methods
			List<BodyDeclaration> declarations = new ArrayList<BodyDeclaration>();
			List<BodyDeclaration> members = typeDeclaration.getMembers();
			if (members != null) {
				for (BodyDeclaration declaration : members) {
					if (!(declaration instanceof ConstructorDeclaration)
							&& !(declaration instanceof MethodDeclaration)) {
						declarations.add(declaration);
					}
				}
			}
			// process constructors
			for (Map.Entry<String, Set<ConstructorDeclaration>> entry : constructors
					.entrySet()) {
				if (entry.getValue().size() > 1) {
					declarations.add(makeConstructor(entry.getKey(),
							entry.getValue()));
				} else {
					declarations.add(entry.getValue().iterator().next());
				}
			}
			// process methods
			for (Map.Entry<String, Set<MethodDeclaration>> entry : methods
					.entrySet()) {
				if (entry.getValue().size() > 1) {
					declarations.add(makeMethod(entry.getKey(),
							entry.getValue()));
				} else {
					declarations.add(entry.getValue().iterator().next());
				}
			}
			// replace members
			typeDeclaration.setMembers(NodeFacade.NodeList(declarations));
		}

		/**
		 * The magic arguments.
		 */
		private static String MAGIC_ARGUMENTS = "arguments";

		/**
		 * Make constructor.
		 *
		 * @param key the key
		 * @param value the value
		 * @return the constructor declaration
		 */
		private ConstructorDeclaration makeConstructor(String key,
				Set<ConstructorDeclaration> value) {
			// FIXME - fix super(...) and this(...) statements
			String paramName = makeParamName(value);
			List<AnnotationExpr> annotations = new ArrayList<AnnotationExpr>();
			JavadocComment javaDoc = NodeFacade.JavadocComment("");
			int modifiers = 0;
			List<TypeParameter> typeParameters = null;
			List<Parameter> parameters = makeParameters(paramName);
			List<ClassOrInterfaceType> throwsList = new ArrayList<>();
			BlockStmt body = null;
			IfStmt topIfStmt = null;
			IfStmt tmpIfStmt = null;
			for (ConstructorDeclaration item : value) {
				modifiers = makeModifiers(modifiers, item.getModifiers()
						.getModifiers());
				typeParameters = makeTypeParameters(typeParameters,
						item.getTypeParameters());
				BlockStmt stuff = item.getBlock();
				if (stuff != null) {
					tmpIfStmt = makeIf(paramName, item.getParameters(), stuff,
							tmpIfStmt);
					if (topIfStmt == null) {
						topIfStmt = tmpIfStmt;
					}
				}
				makeThrows(throwsList, item.getThrowsList());
				makeAnnotations(annotations, item.getAnnotations());
				makeJavaDoc(javaDoc, item.getJavaDoc());
			}
			if (topIfStmt != null) {
				if (Modifiers.isAbstract(modifiers)) {
					modifiers = Modifiers.removeModifier(modifiers,
							Modifiers.ABSTRACT);
				}
				body = NodeFacade.BlockStmt(NodeFacade
						.NodeList((Statement) topIfStmt));
			}
			ConstructorDeclaration result = NodeFacade.ConstructorDeclaration();
			result.setName(key);
			result.setAnnotations(NodeFacade.NodeList(annotations));
			result.setJavaDoc(javaDoc);
			result.setModifiers(NodeFacade.Modifiers(modifiers));
			result.setTypeParameters(NodeFacade.NodeList(typeParameters));
			// result.setParameters(parameters);
			result.setThrowsList(NodeFacade.NodeList(throwsList));
			result.setBlock(body);
			return result;
		}

		/**
		 * Make method.
		 *
		 * @param key the key
		 * @param value the value
		 * @return the method declaration
		 */
		private MethodDeclaration makeMethod(String key,
				Set<MethodDeclaration> value) {
			String paramName = makeParamName(key, value);
			List<AnnotationExpr> annotations = new ArrayList<AnnotationExpr>();
			JavadocComment javaDoc = NodeFacade.JavadocComment("");
			int modifiers = 0;
			List<TypeParameter> typeParameters = null;
			Type type = NodeFacade.VOID_TYPE;
			List<Parameter> parameters = makeParameters(paramName);
			List<ClassOrInterfaceType> throwsList = new ArrayList<>();
			BlockStmt body = null;
			IfStmt topIfStmt = null;
			IfStmt tmpIfStmt = null;
			for (MethodDeclaration item : value) {
				modifiers = makeModifiers(modifiers, item.getModifiers()
						.getModifiers());
				typeParameters = makeTypeParameters(typeParameters,
						item.getTypeParameters());
				type = makeType(type, item.getType());
				BlockStmt stuff = item.getBlock();
				if (stuff != null) {
					tmpIfStmt = makeIf(paramName, item.getParameters(), stuff,
							tmpIfStmt);
					if (topIfStmt == null) {
						topIfStmt = tmpIfStmt;
					}
				}
				makeThrows(throwsList, item.getThrowsList());
				makeAnnotations(annotations, item.getAnnotations());
				makeJavaDoc(javaDoc, item.getJavaDoc());
			}
			List<Statement> statements = new ArrayList<Statement>();
			if (topIfStmt != null) {
				if (Modifiers.isAbstract(modifiers)) {
					modifiers = Modifiers.removeModifier(modifiers,
							Modifiers.ABSTRACT);
				}
				statements.add(topIfStmt);
				if (type == null) {
					statements.add(NodeFacade.ReturnStmt(NodeFacade
							.NullLiteralExpr()));
				}
				body = NodeFacade.BlockStmt(statements);
			}
			if (type == null) {
				type = NodeFacade.ClassOrInterfaceType(JAVA_LANG_OBJECT);

			}
			MethodDeclaration result = NodeFacade.MethodDeclaration();
			result.setName(key);
			result.setAnnotations(NodeFacade.NodeList(annotations));
			result.setJavaDoc(javaDoc);
			result.setModifiers(NodeFacade.Modifiers(modifiers));
			result.setTypeParameters(NodeFacade.NodeList(typeParameters));
			result.setType(type);
			// result.setParameters(parameters);
			result.setThrowsList(NodeFacade.NodeList(throwsList));
			result.setBlock(body);
			return result;
		}

		/**
		 * Make java doc.
		 *
		 * @param javaDoc the java doc
		 * @param javaDok the java dok
		 */
		private void makeJavaDoc(JavadocComment javaDoc, JavadocComment javaDok) {
			if (javaDok != null) {
				javaDoc.setContent(javaDoc.getContent() + javaDok.getContent());
			}
		}

		/**
		 * Make throws.
		 *
		 * @param throwsList the throws list
		 * @param throwz the throwz
		 */
		private void makeThrows(List<ClassOrInterfaceType> throwsList,
				List<ClassOrInterfaceType> throwz) {
			if (throwz != null) {
				for (ClassOrInterfaceType type : throwz) {
					if (!throwsList.contains(type)) {
						throwsList.add(type);
					}
				}
			}
		}

		/**
		 * Make annotations.
		 *
		 * @param annotations the annotations
		 * @param annots the annots
		 */
		private void makeAnnotations(List<AnnotationExpr> annotations,
				List<AnnotationExpr> annots) {
			if (annots != null) {
				for (AnnotationExpr annotationExpr : annots) {
					if (!annotations.contains(annotationExpr)) {
						annotations.add(annotationExpr);
					}
				}
			}
		}

		/**
		 * Make if.
		 *
		 * @param paramName the param name
		 * @param parameters the parameters
		 * @param body the body
		 * @param ifStmt the if stmt
		 * @return the if stmt
		 */
		private IfStmt makeIf(String paramName, List<Parameter> parameters,
				BlockStmt body, IfStmt ifStmt) {
			IfStmt ifStatement = NodeFacade.IfStmt();
			ifStatement.setCondition(makeCondition(paramName, parameters));
			List<Statement> statements = makeLocalDeclarations(paramName,
					parameters);
			if (body.getStatements() != null) {
				statements.addAll(body.getStatements());
			}
			ifStatement.setThenStmt(NodeFacade.BlockStmt(statements));
			if (ifStmt != null) {
				ifStmt.setElseStmt(ifStatement);
			}
			return ifStatement;
		}

		/**
		 * Make local declarations.
		 *
		 * @param paramName the param name
		 * @param parameters the parameters
		 * @return the list
		 */
		private List<Statement> makeLocalDeclarations(String paramName,
				List<Parameter> parameters) {
			List<Statement> statements = new ArrayList<Statement>();
			if (parameters != null && parameters.size() > 0) {
				int index = 0;
				for (Parameter parameter : parameters) {
					Expression init = NodeFacade.CastExpr(
							parameter.getType(),
							NodeFacade.ArrayAccessExpr(
									NodeFacade.NameExpr(paramName),
									NodeFacade.IntegerLiteralExpr(index)));
					VariableDeclarationExpr expr = NodeFacade
							.VariableDeclarationExpr(parameter.getType(),
									parameter.getId().getName(), init);
					if (parameter.getModifiers().isFinal()) {
						expr.setModifiers(NodeFacade.Modifiers(Modifiers.FINAL));
					}
					statements.add(NodeFacade.ExpressionStmt(expr));
					index++;
				}
			}
			return statements;
		}

		/**
		 * Make condition.
		 *
		 * @param paramName the param name
		 * @param parameters the parameters
		 * @return the expression
		 */
		private Expression makeCondition(String paramName,
				List<Parameter> parameters) {
			int size = 0;
			if (parameters != null) {
				size = parameters.size();
			}
			Expression result = NodeFacade.BinaryExpr(
					NodeFacade.FieldAccessExpr(paramName, "length"),
					NodeFacade.IntegerLiteralExpr(size), BinaryOperator.equals);
			if (size > 0) {
				int index = 0;
				for (Parameter parameter : parameters) {
					Type type = fixType(parameter.getType());
					result = NodeFacade.BinaryExpr(result,
							NodeFacade.InstanceOfExpr(NodeFacade
									.ArrayAccessExpr(NodeFacade
											.NameExpr(paramName), NodeFacade
											.IntegerLiteralExpr(index)), type),
							BinaryOperator.and);
					index++;
				}
			}
			return result;
		}

		/**
		 * Fix type.
		 *
		 * @param type the type
		 * @return the type
		 */
		private Type fixType(Type type) {
			if (type instanceof ReferenceType) {
				ReferenceType rType = (ReferenceType) type;
				return NodeFacade.ReferenceType(fixType(rType.getType()),
						rType.getSlots());
			} else if (type instanceof PrimitiveType) {
				Primitive primitive = ((PrimitiveType) type).getType();
				switch (primitive) {
				case Boolean:
					return LangUtils.BOOLEAN_TYPE;
				case Char:
					return LangUtils.CHARACTER_TYPE;
				case Byte:
					return LangUtils.BYTE_TYPE;
				case Short:
					return LangUtils.SHORT_TYPE;
				case Int:
					return LangUtils.INTEGER_TYPE;
				case Long:
					return LangUtils.LONG_TYPE;
				case Float:
					return LangUtils.FLOAT_TYPE;
				case Double:
					return LangUtils.DOUBLE_TYPE;
				}
			}
			return type;
		}

		/**
		 * Make parameters.
		 *
		 * @param paramName the param name
		 * @return the list
		 */
		private List<Parameter> makeParameters(String paramName) {
			List<Parameter> parameters = new ArrayList<Parameter>();
			Parameter parameter = NodeFacade.Parameter();
			parameter.setId(NodeFacade.VariableDeclaratorId(paramName));
			parameter
					.setType(NodeFacade.ClassOrInterfaceType(JAVA_LANG_OBJECT));
			parameter.setEllipsis(NodeFacade.Ellipsis());
			parameters.add(parameter);
			return parameters;
		}

		/**
		 * Make type.
		 *
		 * @param type the type
		 * @param type2 the type2
		 * @return the type
		 */
		private Type makeType(Type type, Type type2) {
			// TODO type
			if (!(type2 instanceof VoidType)) {
				return null;
			}
			return type;
		}

		/**
		 * Make type parameters.
		 *
		 * @param typeParameters the type parameters
		 * @param typeParameters2 the type parameters2
		 * @return the list
		 */
		private List<TypeParameter> makeTypeParameters(
				List<TypeParameter> typeParameters,
				List<TypeParameter> typeParameters2) {
			// TODO typeParameters
			return null;
		}

		/**
		 * Make modifiers.
		 *
		 * @param modifiers the modifiers
		 * @param itemModifiers the item modifiers
		 * @return the int
		 */
		private int makeModifiers(int modifiers, int itemModifiers) {
			// TODO modifiers
			return Modifiers.addModifier(modifiers, itemModifiers);
		}

		/**
		 * Make param name.
		 *
		 * @param value the value
		 * @return the string
		 */
		private String makeParamName(Set<ConstructorDeclaration> value) {
			return MAGIC_ARGUMENTS;
		}

		/**
		 * Make param name.
		 *
		 * @param key the key
		 * @param value the value
		 * @return the string
		 */
		private String makeParamName(String key, Set<MethodDeclaration> value) {
			return MAGIC_ARGUMENTS;
		}
	}

	/**
	 * The Class Context.
	 */
	private static class Context {
		
		/**
		 * The data.
		 */
		private Data data;

		/**
		 * Gets the data.
		 *
		 * @return the data
		 */
		public Data getData() {
			return data;
		}

		/**
		 * Sets the data.
		 *
		 * @param data the new data
		 */
		public void setData(Data data) {
			this.data = data;
		}

		/**
		 * Process.
		 */
		public void process() {
			data.process();
		}

	}

	/**
	 * The Class Visitor.
	 */
	private static class Visitor extends VoidVisitorAdapter<Context> {

		/* (non-Javadoc)
		 * @see com.digiarea.jse.visitor.VoidVisitorAdapter#visit(com.digiarea.jse.ClassDeclaration, java.lang.Object)
		 */
		@Override
		public void visit(ClassDeclaration n, Context ctx) throws Exception {
			Data oldData = ctx.getData();
			ctx.setData(new Data(n));
			super.visit(n, ctx);
			ctx.process();
			ctx.setData(oldData);
		}

		/* (non-Javadoc)
		 * @see com.digiarea.jse.visitor.VoidVisitorAdapter#visit(com.digiarea.jse.ConstructorDeclaration, java.lang.Object)
		 */
		@Override
		public void visit(ConstructorDeclaration n, Context ctx)
				throws Exception {
			ctx.getData().addConstructor(n);
			super.visit(n, ctx);
		}

		/* (non-Javadoc)
		 * @see com.digiarea.jse.visitor.VoidVisitorAdapter#visit(com.digiarea.jse.EnumDeclaration, java.lang.Object)
		 */
		@Override
		public void visit(EnumDeclaration n, Context ctx) throws Exception {
			Data oldData = ctx.getData();
			ctx.setData(new Data(n));
			super.visit(n, ctx);
			ctx.process();
			ctx.setData(oldData);
		}

		/* (non-Javadoc)
		 * @see com.digiarea.jse.visitor.VoidVisitorAdapter#visit(com.digiarea.jse.InterfaceDeclaration, java.lang.Object)
		 */
		@Override
		public void visit(InterfaceDeclaration n, Context ctx) throws Exception {
			Data oldData = ctx.getData();
			ctx.setData(new Data(n));
			super.visit(n, ctx);
			ctx.process();
			ctx.setData(oldData);
		}

		/* (non-Javadoc)
		 * @see com.digiarea.jse.visitor.VoidVisitorAdapter#visit(com.digiarea.jse.MethodDeclaration, java.lang.Object)
		 */
		@Override
		public void visit(MethodDeclaration n, Context ctx) throws Exception {
			ctx.getData().addMethod(n);
			super.visit(n, ctx);
		}

	}

	/* (non-Javadoc)
	 * @see com.digiarea.common.Arrow#arrow(java.lang.Object)
	 */
	@Override
	public Project arrow(Project input) throws Exception {
		new Visitor().visit(input, new Context());
		return input;
	}

}
