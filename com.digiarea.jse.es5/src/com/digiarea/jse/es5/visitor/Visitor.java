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
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import com.digiarea.common.utils.SourcePrinter;
import com.digiarea.es5.ArrayAccessExpression;
import com.digiarea.es5.ArrayLiteral;
import com.digiarea.es5.AssignmentExpression;
import com.digiarea.es5.AssignmentExpression.AssignOperator;
import com.digiarea.es5.BinaryExpression;
import com.digiarea.es5.BinaryExpression.BinaryOperator;
import com.digiarea.es5.Block;
import com.digiarea.es5.BlockComment;
import com.digiarea.es5.BreakStatement;
import com.digiarea.es5.CallExpression;
import com.digiarea.es5.CaseClause;
import com.digiarea.es5.CatchClause;
import com.digiarea.es5.CompilationUnit;
import com.digiarea.es5.ConditionalExpression;
import com.digiarea.es5.ContinueStatement;
import com.digiarea.es5.DoWhileStatement;
import com.digiarea.es5.EmptyStatement;
import com.digiarea.es5.EnclosedExpression;
import com.digiarea.es5.Expression;
import com.digiarea.es5.ExpressionStatement;
import com.digiarea.es5.FieldAccessExpression;
import com.digiarea.es5.ForStatement;
import com.digiarea.es5.ForeachStatement;
import com.digiarea.es5.FunctionExpression;
import com.digiarea.es5.IdentifierName;
import com.digiarea.es5.IfStatement;
import com.digiarea.es5.JSDocComment;
import com.digiarea.es5.LabelledStatement;
import com.digiarea.es5.LineComment;
import com.digiarea.es5.Node;
import com.digiarea.es5.NodeFacade;
import com.digiarea.es5.ObjectLiteral;
import com.digiarea.es5.Parameter;
import com.digiarea.es5.Project;
import com.digiarea.es5.PropertyAssignment;
import com.digiarea.es5.PutAssignment;
import com.digiarea.es5.ReturnStatement;
import com.digiarea.es5.Statement;
import com.digiarea.es5.SwitchStatement;
import com.digiarea.es5.ThisExpression;
import com.digiarea.es5.ThrowStatement;
import com.digiarea.es5.TryStatement;
import com.digiarea.es5.UnaryExpression;
import com.digiarea.es5.UnaryExpression.UnaryOperator;
import com.digiarea.es5.VariableDeclaration;
import com.digiarea.es5.VariableExpression;
import com.digiarea.es5.WhileStatement;
import com.digiarea.es5.WithStatement;
import com.digiarea.jse.AnnotationDeclaration;
import com.digiarea.jse.AnnotationMemberDeclaration;
import com.digiarea.jse.ArraySlot;
import com.digiarea.jse.BodyDeclaration;
import com.digiarea.jse.ClassDeclaration;
import com.digiarea.jse.ClassOrInterfaceType;
import com.digiarea.jse.ConstructorDeclaration;
import com.digiarea.jse.CreationReference;
import com.digiarea.jse.Ellipsis;
import com.digiarea.jse.EnumDeclaration;
import com.digiarea.jse.ExpressionMethodReference;
import com.digiarea.jse.FieldDeclaration;
import com.digiarea.jse.InitializerDeclaration;
import com.digiarea.jse.InstanceOfExpr;
import com.digiarea.jse.InterfaceDeclaration;
import com.digiarea.jse.LambdaBlock;
import com.digiarea.jse.LambdaExpr;
import com.digiarea.jse.MethodDeclaration;
import com.digiarea.jse.Modifiers;
import com.digiarea.jse.NameExpr;
import com.digiarea.jse.NodeList;
import com.digiarea.jse.PrimitiveType;
import com.digiarea.jse.PrimitiveType.Primitive;
import com.digiarea.jse.QualifiedNameExpr;
import com.digiarea.jse.ReferenceType;
import com.digiarea.jse.SuperExpr;
import com.digiarea.jse.SuperMethodReference;
import com.digiarea.jse.SwitchEntryStmt;
import com.digiarea.jse.ThrowStmt;
import com.digiarea.jse.Type;
import com.digiarea.jse.TypeDeclaration;
import com.digiarea.jse.TypeMethodReference;
import com.digiarea.jse.VariableDeclarator;
import com.digiarea.jse.VariableDeclaratorId;
import com.digiarea.jse.VoidType;
import com.digiarea.jse.WildcardType;
import com.digiarea.jse.builder.ModelBuilder.BuilderType;
import com.digiarea.jse.builder.ModelHierarchy;
import com.digiarea.jse.es5.Context;
import com.digiarea.jse.utils.NodeUtils;
import com.digiarea.jse.visitor.GenericVisitor;

/**
 * The Class Visitor.
 */
public class Visitor implements GenericVisitor<Node, Context> {

	/**
	 * The Constant EMPTY.
	 */
	private static final String EMPTY = "";

	/**
	 * The Constant OBJECT.
	 */
	private static final String OBJECT = "object";

	/**
	 * The Constant UNDEFINED.
	 */
	private static final String UNDEFINED = "undefined";

	/**
	 * The Constant NUMBER.
	 */
	private static final String NUMBER = "number";

	/**
	 * The Constant STRING.
	 */
	private static final String STRING = "string";

	/**
	 * The Constant ERROR.
	 */
	private static final String ERROR = "Error";

	/**
	 * The Constant BOOLEAN.
	 */
	private static final String BOOLEAN = "boolean";
	// closure
	/**
	 * The Constant GOOG_BASE.
	 */
	private static final String GOOG_BASE = "goog.base";

	/**
	 * The Constant GOOG_REQUIRE.
	 */
	private static final String GOOG_REQUIRE = "goog.require";

	/**
	 * The Constant GOOG_INHERITS.
	 */
	private static final String GOOG_INHERITS = "goog.inherits";

	/**
	 * The Constant GOOG_PROVIDE.
	 */
	private static final String GOOG_PROVIDE = "goog.provide";

	/**
	 * The Constant GOOG_ISSTRING.
	 */
	private static final String GOOG_ISSTRING = "goog.isString";

	/**
	 * The Constant GOOG_ISNUMBER.
	 */
	private static final String GOOG_ISNUMBER = "goog.isNumber";

	// java
	/**
	 * The Constant JAVA_LANG_INTEGER.
	 */
	private static final String JAVA_LANG_INTEGER = "java.lang.Integer";

	/**
	 * The Constant JAVA_LANG_STRING.
	 */
	private static final String JAVA_LANG_STRING = "java.lang.String";
	// private static final String JAVA_LANG_EXCEPTION = "java.lang.Exception";
	/**
	 * The Constant JAVA_LANG_OBJECT.
	 */
	private static final String JAVA_LANG_OBJECT = "java.lang.Object";

	/**
	 * The types.
	 */
	private static List<String> types = Arrays.asList(JAVA_LANG_STRING,
			JAVA_LANG_INTEGER);

	// javascript
	/**
	 * The Constant PROTOTYPE.
	 */
	private static final String PROTOTYPE = "prototype";

	/**
	 * The model hierarchy.
	 */
	private ModelHierarchy modelHierarchy;

	/**
	 * The proces bodies.
	 */
	private boolean procesBodies = true;

	/**
	 * Instantiates a new visitor.
	 * 
	 * @param modelHierarchy
	 *            the model hierarchy
	 * @param procesBodies
	 *            the proces bodies
	 */
	public Visitor(ModelHierarchy modelHierarchy, boolean procesBodies) {
		super();
		this.modelHierarchy = modelHierarchy;
		try {
			this.modelHierarchy.process();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.procesBodies = procesBodies;
	}

	/**
	 * Instantiates a new visitor.
	 * 
	 * @param modelHierarchy
	 *            the model hierarchy
	 */
	public Visitor(ModelHierarchy modelHierarchy) {
		this(modelHierarchy, true);
	}

	/**
	 * Make type declaration.
	 * 
	 * @param n
	 *            the n
	 * @param ctx
	 *            the ctx
	 * @return the statement
	 * @throws Exception
	 *             the exception
	 */
	private Statement makeTypeDeclaration(TypeDeclaration n, Context ctx)
			throws Exception {
		TypeDeclaration oldDecl = ctx.getTypeDeclaration();
		ctx.setTypeDeclaration(n);
		ctx.addEnclosure(n.getName());
		Block img = NodeFacade.Block();
		if (n.getMembers() != null) {
			List<Statement> fields = new ArrayList<Statement>();
			List<Statement> statik = new ArrayList<Statement>();
			List<Statement> constructors = new ArrayList<Statement>();
			List<Statement> methods = new ArrayList<Statement>();
			List<Statement> inner = new ArrayList<Statement>();
			for (BodyDeclaration item : n.getMembers()) {
				if (item != null) {
					if (item instanceof FieldDeclaration) {
						FieldDeclaration field = (FieldDeclaration) item;
						if (field.getModifiers().isStatic()) {
							statik.add((Statement) item.accept(this, ctx));
						} else {
							fields.add((Statement) item.accept(this, ctx));
						}
					} else if (item instanceof ConstructorDeclaration) {
						constructors.add((Statement) item.accept(this, ctx));
					} else if (item instanceof MethodDeclaration) {
						methods.add((Statement) item.accept(this, ctx));
					} else {
						inner.add((Statement) item.accept(this, ctx));
					}
				}
			}
			if (constructors.isEmpty()) {
				// add default constructor if needed
				ConstructorDeclaration constructor = com.digiarea.jse.NodeFacade
						.ConstructorDeclaration(Modifiers.PUBLIC, n.getName());
				com.digiarea.jse.Statement explicitConstructorInvocationStmt = com.digiarea.jse.NodeFacade
						.ExplicitConstructorInvocationStmt();
				constructor.setBlock(com.digiarea.jse.NodeFacade
						.BlockStmt(Arrays
								.asList(explicitConstructorInvocationStmt)));
				constructors.add((Statement) constructor.accept(this, ctx));
			} else {
				for (Statement statement : constructors) {
					ExpressionStatement constructor = (ExpressionStatement) statement;
					FunctionExpression functionExpression = (FunctionExpression) ((AssignmentExpression) constructor
							.getExpression()).getValue();
					List<Statement> body = new ArrayList<Statement>();
					if (functionExpression.getBody().getStatements() != null) {
						body.addAll(functionExpression.getBody()
								.getStatements());
					}
					functionExpression.setBody(NodeFacade.Block(body));
				}
			}
			ExpressionStatement inheritStmt = null;
			if (n instanceof ClassDeclaration) {
				ClassDeclaration clazz = (ClassDeclaration) n;
				inheritStmt = createClosureInherit(clazz, ctx);
			}
			List<Statement> members = new ArrayList<Statement>();
			members.addAll(constructors);
			if (inheritStmt != null) {
				members.add(inheritStmt);
			}
			members.addAll(methods);
			members.addAll(inner);
			members.addAll(fields);
			members.addAll(statik);
			img.setStatements(NodeFacade.NodeList(members));
		}
		// we add comments from Class and Interfaces to goog.provide declaration
		// if (n.getJavaDoc() != null) {
		// img.setComment((Comment) n.getJavaDoc().accept(this, ctx));
		// }
		ctx.setTypeDeclaration(oldDecl);
		ctx.cutEnclosure();
		return img;
	}

	/**
	 * Make prototype.
	 * 
	 * @param decl
	 *            the decl
	 * @param name
	 *            the name
	 * @param expression
	 *            the expression
	 * @return the statement
	 */
	private Statement makePrototype(String decl, String name,
			Expression expression) {
		return NodeFacade.ExpressionStatement(NodeFacade.AssignmentExpression(
				NodeFacade.FieldAccessExpression(
						NodeFacade.FieldAccessExpression(
								NodeFacade.IdentifierName(decl),
								NodeFacade.IdentifierName(Visitor.PROTOTYPE)),
						NodeFacade.IdentifierName(name)), expression,
				AssignOperator.assign));
	}

	/**
	 * Make constructor.
	 * 
	 * @param decl
	 *            the decl
	 * @param expression
	 *            the expression
	 * @return the statement
	 */
	private Statement makeConstructor(String decl, Expression expression) {
		return NodeFacade.ExpressionStatement(NodeFacade.AssignmentExpression(
				NodeFacade.FieldAccessExpression(
						NodeFacade.IdentifierName(decl), null), expression,
				AssignOperator.assign));
	}

	/**
	 * Make static.
	 * 
	 * @param decl
	 *            the decl
	 * @param name
	 *            the name
	 * @param expression
	 *            the expression
	 * @return the statement
	 */
	private Statement makeStatic(String decl, String name, Expression expression) {
		return NodeFacade.ExpressionStatement(NodeFacade.AssignmentExpression(
				NodeFacade.FieldAccessExpression(
						NodeFacade.IdentifierName(decl),
						NodeFacade.IdentifierName(name)), expression,
				AssignOperator.assign));
	}

	/**
	 * Make simple.
	 * 
	 * @param name
	 *            the name
	 * @param expression
	 *            the expression
	 * @return the statement
	 */
	private Statement makeSimple(String name, FunctionExpression expression) {
		return NodeFacade.ExpressionStatement(NodeFacade.AssignmentExpression(
				NodeFacade.IdentifierName(name), expression,
				AssignOperator.assign));
	}

	/**
	 * Make variable.
	 * 
	 * @param ctx
	 *            the ctx
	 * @param id
	 *            the id
	 * @param init
	 *            the init
	 * @return the node
	 * @throws Exception
	 *             the exception
	 */
	private Node makeVariable(Context ctx, String id,
			com.digiarea.jse.Expression init) throws Exception {
		com.digiarea.jse.Node parent = ctx.getParent();
		if (parent != null && (parent instanceof FieldDeclaration)) {
			FieldDeclaration field = (FieldDeclaration) parent;
			Expression head = null;
			if (ctx.isAnonymousClass()) {
				head = null;
			} else if (field.getModifiers().isStatic()) {
				head = NodeFacade.FieldAccessExpression(
						NodeFacade.IdentifierName(getFullName(ctx)), null);
			} else {
				// head = NodeFacade.ThisExpression();
				head = NodeFacade.FieldAccessExpression(
						NodeFacade.IdentifierName(getFullName(ctx) + "."
								+ PROTOTYPE), null);
			}
			Expression img = head == null ? NodeFacade.IdentifierName(id)
					: NodeFacade.FieldAccessExpression(head,
							NodeFacade.IdentifierName(id));
			if (init != null) {
				img = NodeFacade.AssignmentExpression(img,
						(Expression) init.accept(this, ctx),
						AssignOperator.assign);
			} else {
				img = NodeFacade.AssignmentExpression(img,
						getDefault(field.getType()), AssignOperator.assign);
			}
			return NodeFacade.ExpressionStatement(img);
		} else {
			VariableDeclaration img = NodeFacade.VariableDeclaration();
			if (id != null) {
				img.setName(id);
			}
			if (init != null) {
				img.setExpression((Expression) init.accept(this, ctx));
			}
			return img;
		}
	}

	/**
	 * Gets the default.
	 * 
	 * @param type
	 *            the type
	 * @return the default
	 */
	private Expression getDefault(Type type) {
		if (type instanceof PrimitiveType) {
			if (((PrimitiveType) type).getType() == Primitive.Boolean) {
				return NodeFacade.BooleanLiteral(false);
			} else {
				return NodeFacade.DecimalLiteral("0");
			}
		} else {
			return NodeFacade.NullLiteral();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * AnnotationDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.AnnotationDeclaration n, Context ctx)
			throws Exception {
		return makeTypeDeclaration(n, ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * AnnotationMemberDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.AnnotationMemberDeclaration n,
			Context ctx) throws Exception {
		FunctionExpression img = NodeFacade.FunctionExpression();
		img.setBody(NodeFacade.Block());
		Statement statement = null;
		if (ctx.isAnonymousClass()) {
			statement = makeSimple(n.getName(), img);
		} else if (n.getModifiers().isStatic()) {
			statement = makeStatic(getFullName(ctx), n.getName(), img);
		} else {
			statement = makePrototype(getFullName(ctx), n.getName(), img);
		}
		com.digiarea.jse.Node oldParent = ctx.getParent();
		ctx.setParent(n);
		if (n.getJavaDoc() != null) {
			statement.setJsDocComment((JSDocComment) n.getJavaDoc().accept(
					this, ctx));
		} else {
			statement.setJsDocComment((JSDocComment) visit(
					com.digiarea.jse.NodeFacade.JavadocComment(), ctx));
		}
		ctx.setParent(oldParent);
		return statement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * ArrayAccessExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ArrayAccessExpr n, Context ctx)
			throws Exception {
		ArrayAccessExpression img = NodeFacade.ArrayAccessExpression();
		if (n.getName() != null) {
			img.setName((Expression) n.getName().accept(this, ctx));
		}
		if (n.getIndex() != null) {
			img.setIndex((Expression) n.getIndex().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * ArrayCreationExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ArrayCreationExpr n, Context ctx)
			throws Exception {
		if (n.getInitializer() != null) {
			return (ArrayLiteral) n.getInitializer().accept(this, ctx);
		} else {
			Expression scope = NodeFacade.IdentifierName("Array");
			CallExpression img = NodeFacade.CallExpression(scope, null);
			List<Expression> values = new ArrayList<Expression>();
			for (ArraySlot slot : n.getSlots()) {
				if (slot != null) {
					values.add((Expression) slot.getExpression().accept(this,
							ctx));
				}
			}
			img.setArgs(NodeFacade.NodeList(values));
			return NodeFacade.AllocationExpression(img);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * ArrayInitializerExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ArrayInitializerExpr n, Context ctx)
			throws Exception {
		ArrayLiteral img = NodeFacade.ArrayLiteral();
		if (n.getValues() != null) {
			List<Expression> values = new ArrayList<Expression>();
			for (com.digiarea.jse.Expression item : n.getValues()) {
				if (item != null) {
					values.add((Expression) item.accept(this, ctx));
				}
			}
			img.setExpressions(NodeFacade.NodeList(values));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.AssertStmt
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.AssertStmt n, Context ctx)
			throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.AssignExpr
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.AssignExpr n, Context ctx)
			throws Exception {
		AssignmentExpression img = (AssignmentExpression) n.getOperator()
				.accept(this, ctx);
		if (n.getTarget() != null) {
			img.setTarget((Expression) n.getTarget().accept(this, ctx));
		}
		if (n.getValue() != null) {
			img.setValue((Expression) n.getValue().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.AssignExpr
	 * .AssignOperator, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.AssignExpr.AssignOperator n, Context ctx)
			throws Exception {
		AssignmentExpression img = NodeFacade.AssignmentExpression();
		AssignOperator operator = null;
		switch (n) {
		case and:
			operator = AssignOperator.and;
			break;
		case lShift:
			operator = AssignOperator.lShift;
			break;
		case assign:
			operator = AssignOperator.assign;
			break;
		case minus:
			operator = AssignOperator.minus;
			break;
		case or:
			operator = AssignOperator.or;
			break;
		case plus:
			operator = AssignOperator.plus;
			break;
		case rem:
			operator = AssignOperator.rem;
			break;
		case rSignedShift:
			operator = AssignOperator.rSignedShift;
			break;
		case rUnsignedShift:
			operator = AssignOperator.rUnsignedShift;
			break;
		case slash:
			operator = AssignOperator.slash;
			break;
		case star:
			operator = AssignOperator.star;
			break;
		case xor:
			operator = AssignOperator.xor;
			break;
		}
		img.setAssignOperator(operator);
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.BinaryExpr
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.BinaryExpr n, Context ctx)
			throws Exception {
		BinaryExpression img = (BinaryExpression) n.getOperator().accept(this,
				ctx);
		if (n.getLeft() != null) {
			img.setLeft((Expression) n.getLeft().accept(this, ctx));
		}
		if (n.getRight() != null) {
			img.setRight((Expression) n.getRight().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.BinaryExpr
	 * .BinaryOperator, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.BinaryExpr.BinaryOperator n, Context ctx)
			throws Exception {
		BinaryExpression img = NodeFacade.BinaryExpression();
		BinaryOperator operator = null;
		switch (n) {
		case and:
			operator = BinaryOperator.and;
			break;
		case binAnd:
			operator = BinaryOperator.binAnd;
			break;
		case binOr:
			operator = BinaryOperator.binOr;
			break;
		case divide:
			operator = BinaryOperator.divide;
			break;
		case equals:
			// type cast is not allowed
			operator = BinaryOperator.identity;
			break;
		case greater:
			operator = BinaryOperator.greater;
			break;
		case greaterEquals:
			operator = BinaryOperator.greaterEquals;
			break;
		case lShift:
			operator = BinaryOperator.lShift;
			break;
		case less:
			operator = BinaryOperator.less;
			break;
		case lessEquals:
			operator = BinaryOperator.lessEquals;
			break;
		case minus:
			operator = BinaryOperator.minus;
			break;
		case notEquals:
			// type cast is not allowed
			operator = BinaryOperator.notIdentity;
			break;
		case or:
			operator = BinaryOperator.or;
			break;
		case plus:
			operator = BinaryOperator.plus;
			break;
		case rSignedShift:
			operator = BinaryOperator.rSignedShift;
			break;
		case rUnsignedShift:
			operator = BinaryOperator.rUnsignedShift;
			break;
		case remainder:
			operator = BinaryOperator.remainder;
			break;
		case times:
			operator = BinaryOperator.times;
			break;
		case xor:
			operator = BinaryOperator.xor;
			break;
		}
		img.setBinaryOperator(operator);
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.BlockComment
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.BlockComment n, Context ctx)
			throws Exception {
		BlockComment img = NodeFacade.BlockComment();
		img.setContent("/*" + n.getContent() + "*/");
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.BlockStmt,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.BlockStmt n, Context ctx)
			throws Exception {
		Block img = NodeFacade.Block();
		if (n.getStatements() != null) {
			List<Statement> stmts = new ArrayList<Statement>();
			for (com.digiarea.jse.Statement item : n.getStatements()) {
				if (item != null) {
					stmts.add((Statement) item.accept(this, ctx));
				}
			}
			img.setStatements(NodeFacade.NodeList(stmts));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * BooleanLiteralExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.BooleanLiteralExpr n, Context ctx)
			throws Exception {
		return NodeFacade.BooleanLiteral(n.isValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.BreakStmt,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.BreakStmt n, Context ctx)
			throws Exception {
		BreakStatement img = NodeFacade.BreakStatement();
		img.setIdentifier(n.getId());
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.CastExpr,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.CastExpr n, Context ctx)
			throws Exception {
		return (Expression) n.getExpression().accept(this, ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.CatchClause
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.CatchClause n, Context ctx)
			throws Exception {
		CatchClause img = NodeFacade.CatchClause();
		// FIXME
		img.setString(n.getName());
		if (n.getCatchBlock() != null) {
			img.setBlock((Block) n.getCatchBlock().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * CharLiteralExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.CharLiteralExpr n, Context ctx)
			throws Exception {
		String value = n.getValue();
		return NodeFacade.StringLiteralSingle(value.substring(1,
				value.length() - 1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * ClassDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ClassDeclaration n, Context ctx)
			throws Exception {
		return makeTypeDeclaration(n, ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.ClassExpr,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ClassExpr n, Context ctx)
			throws Exception {
		com.digiarea.jse.Node parent = ctx.getParent();
		ctx.setParent(n);
		FieldAccessExpression img = NodeFacade.FieldAccessExpression();
		if (n.getType() != null) {
			img.setScope((Expression) n.getType().accept(this, ctx));
		}
		img.setField(NodeFacade.IdentifierName("class"));
		ctx.setParent(parent);
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * ClassOrInterfaceType, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ClassOrInterfaceType n, Context ctx)
			throws Exception {
		FieldAccessExpression img = NodeFacade.FieldAccessExpression();
		if (n.getScope() != null) {
			img.setScope((Expression) n.getScope().accept(this, ctx));
		}
		if (n.getName() != null) {
			if (canAddRequire(n, ctx)) {
				addRequire(n.getName().toString());
			}
			// TODO FieldAccessExpression or IdentifierName?
			Node expression = n.getName().accept(this, ctx);
			if (expression instanceof IdentifierName) {
				if (img.getScope() != null) {
					img.setField((IdentifierName) expression);
				} else {
					return NodeFacade.IdentifierName(expression.toString());
				}
			} else {
				return (FieldAccessExpression) expression;
			}
		}
		return img;
	}

	/**
	 * The requires.
	 */
	private LinkedHashSet<String> requires;

	/**
	 * The provide.
	 */
	private String provide;

	/**
	 * Adds the require.
	 * 
	 * @param require
	 *            the require
	 */
	private void addRequire(String require) {
		if (!require.equals(provide)) {
			requires.add(require);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * CompilationUnit, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.CompilationUnit n, Context ctx)
			throws Exception {
		// comments
		// if (n.getComments() != null) {
		// List<Comment> comments = new ArrayList<>();
		// for (com.digiarea.jse.Comment comment : n.getComments()) {
		// if (comment != null) {
		// comments.add((Comment) comment.accept(this, ctx));
		// }
		// }
		// }
		// as package might be set we can set the NodeFacade.enclosure
		if (n.getPackageDeclaration() != null) {
			ctx.setEnclosure(n.getPackageDeclaration().getName());
		} else {
			ctx.setEnclosure(com.digiarea.jse.NodeFacade.NameExpr());
		}
		ctx.setCompilationUnit(n);
		CompilationUnit img = NodeFacade.CompilationUnit();
		if (n.getTypes() != null) {
			List<Statement> types = new ArrayList<Statement>();
			requires = new LinkedHashSet<String>();
			for (TypeDeclaration item : n.getTypes()) {
				if (item != null) {
					ExpressionStatement provide = createClosureProvide(n, ctx);
					if (item.getJavaDoc() != null) {
						provide.setJsDocComment((JSDocComment) item
								.getJavaDoc().accept(this, ctx));
					}
					types.add(provide);
					Statement statement = (Statement) item.accept(this, ctx);
					for (String string : requires) {
						types.add(createClosureRequire(string, ctx));
					}
					types.add(statement);
				}
			}
			img.setElements(NodeFacade.NodeList(types));
		}
		img.setName(n.getName());
		return img;
	}

	/**
	 * Creates the closure require.
	 * 
	 * @param n
	 *            the n
	 * @param ctx
	 *            the ctx
	 * @return the expression statement
	 * @throws Exception
	 *             the exception
	 */
	private ExpressionStatement createClosureRequire(String n, Context ctx)
			throws Exception {
		CallExpression provideExpression = NodeFacade.CallExpression();
		IdentifierName name = NodeFacade.IdentifierName(GOOG_REQUIRE);
		provideExpression.setScope(name);
		List<Expression> args = new ArrayList<Expression>();
		args.add(NodeFacade.StringLiteralDouble(com.digiarea.jse.NodeFacade
				.ClassOrInterfaceType(n).accept(this, ctx).toString()));
		provideExpression.setArgs(NodeFacade.NodeList(args));
		return NodeFacade.ExpressionStatement(provideExpression);
	}

	/**
	 * Creates the closure inherit.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param ctx
	 *            the ctx
	 * @return the expression statement
	 * @throws Exception
	 *             the exception
	 */
	private ExpressionStatement createClosureInherit(ClassDeclaration clazz,
			Context ctx) throws Exception {
		if (clazz.getExtendsType() != null) {
			String extendsString = clazz.getExtendsType().accept(this, ctx)
					.toString();
			addRequire(extendsString);
			CallExpression provideExpression = NodeFacade.CallExpression();
			IdentifierName name = NodeFacade.IdentifierName(GOOG_INHERITS);
			provideExpression.setScope(name);
			List<Expression> args = new ArrayList<Expression>();
			args.add((Expression) ctx.getEnclosure().accept(this, ctx));
			args.add((Expression) com.digiarea.jse.NodeFacade.NameExpr(
					extendsString).accept(this, ctx));
			provideExpression.setArgs(NodeFacade.NodeList(args));
			return NodeFacade.ExpressionStatement(provideExpression);
		}
		return null;
	}

	/**
	 * Creates the closure provide.
	 * 
	 * @param n
	 *            the n
	 * @param ctx
	 *            the ctx
	 * @return the expression statement
	 * @throws Exception
	 *             the exception
	 */
	private ExpressionStatement createClosureProvide(
			com.digiarea.jse.CompilationUnit n, Context ctx) throws Exception {
		CallExpression provideExpression = NodeFacade.CallExpression();
		IdentifierName name = NodeFacade.IdentifierName(GOOG_PROVIDE);
		provideExpression.setScope(name);
		List<Expression> args = new ArrayList<Expression>();
		provide = n.getName();
		args.add(NodeFacade.StringLiteralDouble(com.digiarea.jse.NodeFacade
				.ClassOrInterfaceType(provide).accept(this, ctx).toString()));
		provideExpression.setArgs(NodeFacade.NodeList(args));
		return NodeFacade.ExpressionStatement(provideExpression);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * ConditionalExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ConditionalExpr n, Context ctx)
			throws Exception {
		ConditionalExpression img = NodeFacade.ConditionalExpression();
		if (n.getCondition() != null) {
			img.setCondition((Expression) n.getCondition().accept(this, ctx));
		}
		if (n.getThenExpression() != null) {
			img.setThenExpr((Expression) n.getThenExpression()
					.accept(this, ctx));
		}
		if (n.getElseExpression() != null) {
			img.setElseExpr((Expression) n.getElseExpression()
					.accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * ConstructorDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ConstructorDeclaration n, Context ctx)
			throws Exception {
		// FIXME method reference!
		com.digiarea.jse.Node oldParent = ctx.getParent();
		ctx.setParent(n);
		FunctionExpression img = NodeFacade.FunctionExpression();
		// img.setName(getFullName(ctx));
		if (n.getParameters() != null) {
			List<Parameter> parameters = new ArrayList<Parameter>();
			for (com.digiarea.jse.Parameter item : n.getParameters()) {
				if (item != null) {
					parameters.add((Parameter) item.accept(this, ctx));
				}
			}
			img.setParameters(NodeFacade.NodeList(parameters));
		}
		if (procesBodies && n.getBlock() != null) {
			img.setBody((Block) n.getBlock().accept(this, ctx));
		} else {
			img.setBody(NodeFacade.Block());
		}
		Statement statement = null;
		statement = makeConstructor(getFullName(ctx), img);
		if (n.getJavaDoc() != null) {
			statement.setJsDocComment((JSDocComment) n.getJavaDoc().accept(
					this, ctx));
		} else {
			statement.setJsDocComment((JSDocComment) visit(
					com.digiarea.jse.NodeFacade.JavadocComment(), ctx));
		}
		ctx.setParent(oldParent);
		return statement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.ContinueStmt
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ContinueStmt n, Context ctx)
			throws Exception {
		ContinueStatement img = NodeFacade.ContinueStatement();
		img.setIdentifier(n.getId());
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.DoStmt,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.DoStmt n, Context ctx) throws Exception {
		DoWhileStatement img = NodeFacade.DoWhileStatement();
		if (n.getBody() != null) {
			img.setBody((Statement) n.getBody().accept(this, ctx));
		}
		if (n.getCondition() != null) {
			img.setCondition((Expression) n.getCondition().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * DoubleLiteralExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.DoubleLiteralExpr n, Context ctx)
			throws Exception {
		String expr = n.getValue();
		char ch = expr.charAt(expr.length() - 1);
		if (ch == 'f' || ch == 'F' || ch == 'd' || ch == 'D') {
			expr = expr.substring(0, expr.length() - 1);
		}
		return NodeFacade.DecimalLiteral(expr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * EmptyMemberDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.EmptyMemberDeclaration n, Context ctx)
			throws Exception {
		EmptyStatement img = NodeFacade.EmptyStatement();
		if (n.getJavaDoc() != null) {
			img.setJsDocComment((JSDocComment) n.getJavaDoc().accept(this, ctx));
		} else {
			img.setJsDocComment((JSDocComment) visit(
					com.digiarea.jse.NodeFacade.JavadocComment(), ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.EmptyStmt,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.EmptyStmt n, Context ctx)
			throws Exception {
		return NodeFacade.EmptyStatement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * EmptyTypeDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.EmptyTypeDeclaration n, Context ctx)
			throws Exception {
		return makeTypeDeclaration(n, ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.EnclosedExpr
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.EnclosedExpr n, Context ctx)
			throws Exception {
		EnclosedExpression img = NodeFacade.EnclosedExpression();
		if (n.getInner() != null) {
			img.setInner((Expression) n.getInner().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * EnumConstantDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.EnumConstantDeclaration n, Context ctx)
			throws Exception {
		FunctionExpression img = NodeFacade.FunctionExpression();
		img.setName(n.getName());
		List<Expression> args = new ArrayList<Expression>();
		if (n.getArgs() != null) {
			for (com.digiarea.jse.Expression item : n.getArgs()) {
				if (item != null) {
					args.add((Expression) item.accept(this, ctx));
				}
			}
		}
		if (n.getClassBody() != null) {
			List<Statement> classBody = new ArrayList<Statement>();
			for (BodyDeclaration item : n.getClassBody()) {
				if (item != null) {
					classBody.add((Statement) item.accept(this, ctx));
				}
			}
			img.setBody(NodeFacade.Block(classBody));
		}
		Statement statement = makeStatic(getFullName(ctx), n.getName(),
				NodeFacade.CallExpression(NodeFacade
						.EnclosedExpression(NodeFacade
								.AllocationExpression(img)), args));
		if (n.getJavaDoc() != null) {
			statement.setJsDocComment((JSDocComment) n.getJavaDoc().accept(
					this, ctx));
		} else {
			statement.setJsDocComment((JSDocComment) visit(
					com.digiarea.jse.NodeFacade.JavadocComment(), ctx));
		}
		return statement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * EnumDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.EnumDeclaration n, Context ctx)
			throws Exception {
		List<BodyDeclaration> members = new ArrayList<BodyDeclaration>();
		members.addAll(n.getEntries());
		if (n.getMembers() != null) {
			members.addAll(n.getMembers());
		}
		n.setMembers(com.digiarea.jse.NodeFacade.NodeList(members));
		return makeTypeDeclaration(n, ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * ExplicitConstructorInvocationStmt, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ExplicitConstructorInvocationStmt n,
			Context ctx) throws Exception {
		return createClosureSuperCall(n, ctx);
	}

	/**
	 * Creates the closure super call.
	 * 
	 * @param n
	 *            the n
	 * @param ctx
	 *            the ctx
	 * @return the expression statement
	 * @throws Exception
	 *             the exception
	 */
	private ExpressionStatement createClosureSuperCall(
			com.digiarea.jse.ExplicitConstructorInvocationStmt n, Context ctx)
			throws Exception {
		if (n != null) {
			if (ctx.getTypeDeclaration() instanceof ClassDeclaration) {
				ClassDeclaration clazz = (ClassDeclaration) ctx
						.getTypeDeclaration();
				if (clazz.getExtendsType() != null) {
					CallExpression provideExpression = NodeFacade
							.CallExpression();
					IdentifierName name = NodeFacade
							.IdentifierName(removeGenericTypes(
									clazz.getExtendsType()).toString()
									+ ".call");
					provideExpression.setScope(name);
					List<Expression> args = new ArrayList<Expression>();
					args.add((Expression) com.digiarea.jse.NodeFacade
							.ThisExpr().accept(this, ctx));
					if (n.getArgs() != null) {
						for (com.digiarea.jse.Expression expression : n
								.getArgs()) {
							args.add((Expression) expression.accept(this, ctx));
						}
					}
					provideExpression.setArgs(NodeFacade.NodeList(args));
					return NodeFacade.ExpressionStatement(provideExpression);
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.ExpressionStmt
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ExpressionStmt n, Context ctx)
			throws Exception {
		ExpressionStatement img = NodeFacade.ExpressionStatement();
		if (n.getExpression() != null) {
			img.setExpression((Expression) n.getExpression().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * FieldAccessExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.FieldAccessExpr n, Context ctx)
			throws Exception {
		com.digiarea.jse.Node parent = ctx.getParent();
		ctx.setParent(n);
		FieldAccessExpression img = NodeFacade.FieldAccessExpression();
		if (n.getScope() != null) {
			img.setScope((Expression) n.getScope().accept(this, ctx));
		}
		img.setField(NodeFacade.IdentifierName(n.getField()));
		ctx.setParent(parent);
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * FieldDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.FieldDeclaration n, Context ctx)
			throws Exception {
		com.digiarea.jse.Node oldParent = ctx.getParent();
		ctx.setParent(n);
		List<Statement> variables = new ArrayList<Statement>();
		if (n.getVariables() != null) {
			for (VariableDeclarator item : n.getVariables()) {
				if (item != null) {
					variables.add((Statement) item.accept(this, ctx));
				}
			}
		}
		Statement statement = null;
		if (variables.size() == 1) {
			statement = variables.get(0);
		} else {
			statement = NodeFacade.Block(variables);
		}
		if (n.getJavaDoc() != null) {
			statement.setJsDocComment((JSDocComment) n.getJavaDoc().accept(
					this, ctx));
		} else {
			statement.setJsDocComment((JSDocComment) visit(
					com.digiarea.jse.NodeFacade.JavadocComment(), ctx));
		}
		ctx.setParent(oldParent);
		return statement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.ForeachStmt
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ForeachStmt n, Context ctx)
			throws Exception {
		ForeachStatement img = NodeFacade.ForeachStatement();
		if (n.getVariable() != null) {
			img.setVariable((Expression) n.getVariable().accept(this, ctx));
		}
		if (n.getIterable() != null) {
			img.setExpression((Expression) n.getIterable().accept(this, ctx));
		}
		if (n.getBody() != null) {
			img.setBody((Statement) n.getBody().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.ForStmt,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ForStmt n, Context ctx) throws Exception {
		ForStatement img = NodeFacade.ForStatement();
		if (n.getInit() != null) {
			List<Expression> init = new ArrayList<Expression>();
			for (com.digiarea.jse.Expression item : n.getInit()) {
				if (item != null) {
					init.add((Expression) item.accept(this, ctx));
				}
			}
			img.setVariable(NodeFacade.SequenceExpression(init));
		}
		if (n.getCompare() != null) {
			img.setCondition((Expression) n.getCompare().accept(this, ctx));
		}
		if (n.getUpdate() != null) {
			List<Expression> update = new ArrayList<Expression>();
			for (com.digiarea.jse.Expression item : n.getUpdate()) {
				if (item != null) {
					update.add((Expression) item.accept(this, ctx));
				}
			}
			img.setExpr(NodeFacade.SequenceExpression(update));
		}
		if (n.getBody() != null) {
			img.setBody((Statement) n.getBody().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.IfStmt,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.IfStmt n, Context ctx) throws Exception {
		IfStatement img = NodeFacade.IfStatement();
		if (n.getCondition() != null) {
			img.setCondition((Expression) n.getCondition().accept(this, ctx));
		}
		if (n.getThenStmt() != null) {
			img.setThenStatement((Statement) n.getThenStmt().accept(this, ctx));
		}
		if (n.getElseStmt() != null) {
			img.setElseStatement((Statement) n.getElseStmt().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * ImportDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ImportDeclaration n, Context ctx)
			throws Exception {
		// nothing to do, resolving must be run first
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * InitializerDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.InitializerDeclaration n, Context ctx)
			throws Exception {
		if (n.getBlock() != null) {
			return n.getBlock().accept(this, ctx);
		} else {
			return NodeFacade.Block();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.InstanceOfExpr
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.InstanceOfExpr n, Context ctx)
			throws Exception {
		Expression img = null;

		if (types.contains(n.getType().toString())) {
			CallExpression func = NodeFacade.CallExpression();
			if (n.getType().toString().equals(JAVA_LANG_STRING)) {
				func.setScope((Expression) com.digiarea.jse.NodeFacade
						.NameExpr(GOOG_ISSTRING).accept(this, ctx));
			} else if (n.getType().toString().equals(JAVA_LANG_INTEGER)) {
				func.setScope((Expression) com.digiarea.jse.NodeFacade
						.NameExpr(GOOG_ISNUMBER).accept(this, ctx));
			}
			List<Expression> parameters = new ArrayList<Expression>();
			parameters.add((Expression) n.getExpression().accept(this, ctx));
			func.setArgs(NodeFacade.NodeList(parameters));
			img = func;
		} else {
			BinaryExpression bin = NodeFacade.BinaryExpression();
			bin.setBinaryOperator(BinaryOperator.opInstanceof);
			if (n.getExpression() != null) {
				bin.setLeft((Expression) n.getExpression().accept(this, ctx));
			}
			if (n.getType() != null) {
				com.digiarea.jse.Node oldParent = ctx.getParent();
				ctx.setParent(n);
				bin.setRight((Expression) n.getType().accept(this, ctx));
				ctx.setParent(oldParent);
			}
			img = bin;
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * IntegerLiteralExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.IntegerLiteralExpr n, Context ctx)
			throws Exception {
		return NodeFacade.DecimalLiteral(n.getValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * InterfaceDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.InterfaceDeclaration n, Context ctx)
			throws Exception {
		return makeTypeDeclaration(n, ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.JavadocComment
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.JavadocComment n, Context ctx)
			throws Exception {
		JSDocComment img = NodeFacade.JSDocComment();
		String content = EMPTY;
		if (n != null && n.getContent() != null && !n.getContent().isEmpty()) {
			content = n.getContent().replaceAll("\t", SourcePrinter.INDENT)
					+ "\n";
			// params & throws from JavaDoc
			// content = content.replaceAll("@param", "@parameter");
			// content = content.replaceAll("@throws", "@drops");
		}
		com.digiarea.jse.Node parent = ctx.getParent();
		if (parent != null) {
			if (parent instanceof ConstructorDeclaration) {
				ConstructorDeclaration constructor = (ConstructorDeclaration) parent;
				List<com.digiarea.jse.Parameter> parameters = constructor
						.getParameters();
				if (parameters != null) {
					content = jsDocParameters(parameters, content, ctx);
				}
				TypeDeclaration typeDeclaration = ctx.getTypeDeclaration();
				boolean isClass = typeDeclaration instanceof ClassDeclaration;
				boolean isInterface = typeDeclaration instanceof InterfaceDeclaration;
				// FIXME enums
				boolean isEnum = typeDeclaration instanceof EnumDeclaration;
				boolean isAnnotation = typeDeclaration instanceof AnnotationDeclaration;
				if (typeDeclaration != null) {
					if (isAnnotation) {
						content = content + "\n * @annotation";
					} else if (isInterface) {
						content = content + "\n * @interface";
						InterfaceDeclaration clazz = (InterfaceDeclaration) typeDeclaration;
						if (clazz.getExtendsList() != null) {
							for (ClassOrInterfaceType type : clazz
									.getExtendsList()) {
								if (type != null) {
									content = content + "\n * @extends {"
											+ type.accept(this, ctx).toString()
											+ "}";
								}
							}
						}
					} else if (isEnum) {
						// FIXME dead code; may be translation for enums must be
						// different?
						content = content + "\n * @constructor";
						EnumDeclaration clazz = (EnumDeclaration) typeDeclaration;
						if (clazz.getImplementsList() != null) {
							for (ClassOrInterfaceType type : clazz
									.getImplementsList()) {
								if (type != null) {
									content = content + "\n * @implements {"
											+ type.accept(this, ctx).toString()
											+ "}";
								}
							}
						}
					} else if (isClass) {
						content = content + "\n * @constructor";
						ClassDeclaration clazz = (ClassDeclaration) typeDeclaration;
						if (clazz.getExtendsType() != null) {
							content = content
									+ "\n * @extends {"
									+ clazz.getExtendsType().accept(this, ctx)
											.toString() + "}";
						}
						if (clazz.getImplementsList() != null) {
							for (ClassOrInterfaceType type : clazz
									.getImplementsList()) {
								if (type != null) {
									content = content + "\n * @implements {"
											+ type.accept(this, ctx).toString()
											+ "}";
								}
							}
						}
					}
					content = content
							+ getJSDocModifiers(typeDeclaration.getModifiers()
									.getModifiers());
				}
			} else if (parent instanceof MethodDeclaration) {
				MethodDeclaration method = (MethodDeclaration) parent;
				content = content
						+ getJSDocModifiers(method.getModifiers()
								.getModifiers());
				List<com.digiarea.jse.Parameter> parameters = method
						.getParameters();
				if (parameters != null) {
					content = jsDocParameters(parameters, content, ctx);
				}
				Type type = method.getType();
				content = jsDocReturn(type, content, ctx);
			} else if (parent instanceof FieldDeclaration) {
				FieldDeclaration field = (FieldDeclaration) parent;
				content = content + "\n * @type {"
						+ getJSDocType(field.getType(), ctx) + "} ";
			} else if (parent instanceof AnnotationMemberDeclaration) {
				AnnotationMemberDeclaration method = (AnnotationMemberDeclaration) parent;
				content = content
						+ getJSDocModifiers(method.getModifiers()
								.getModifiers());
				Type type = method.getType();
				content = jsDocReturn(type, content, ctx);
				com.digiarea.jse.Expression defaultValue = method
						.getDefaultValue();
				if (defaultValue != null) {
					content = content + "\n * @defaultValue "
							+ defaultValue.toString();
				}
			} else {
				System.err.println(parent.getClass());
			}
		}
		img.setContent(content + "\n");
		return img;
	}

	/**
	 * Js doc return.
	 * 
	 * @param type
	 *            the type
	 * @param content
	 *            the content
	 * @param ctx
	 *            the ctx
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	private String jsDocReturn(Type type, String content, Context ctx)
			throws Exception {
		String result = content;
		if (type != null && !(type instanceof VoidType)) {
			String string = "@return";
			if (result.contains(string)) {
				result = result.replaceAll(string,
						"@return {" + getJSDocType(type, ctx) + "} ");
			} else {
				result = result + "\n * @return {" + getJSDocType(type, ctx)
						+ "}";
			}
		}
		return result;
	}

	/**
	 * Js doc parameters.
	 * 
	 * @param parameters
	 *            the parameters
	 * @param content
	 *            the content
	 * @param ctx
	 *            the ctx
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	private String jsDocParameters(List<com.digiarea.jse.Parameter> parameters,
			String content, Context ctx) throws Exception {
		String result = content;
		if (parameters != null) {
			for (com.digiarea.jse.Parameter parameter : parameters) {
				Type type = parameter.getType();
				if (type != null) {
					String name = parameter.getId().getName();
					String string = "@param " + name;
					if (result.contains(string)) {
						result = result.replaceAll(string, "@param {"
								+ getJSDocType(type, ctx) + "} " + name);
					} else {
						result = result + "\n * @param {"
								+ getJSDocType(type, ctx) + "} " + name;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Gets the JS doc type.
	 * 
	 * @param type
	 *            the type
	 * @param ctx
	 *            the ctx
	 * @return the JS doc type
	 * @throws Exception
	 *             the exception
	 */
	private String getJSDocType(Type type, Context ctx) throws Exception {
		if (type != null) {
			if (type instanceof VoidType) {
				return UNDEFINED;
			} else if (type instanceof PrimitiveType) {
				Primitive primitive = ((PrimitiveType) type).getType();
				switch (primitive) {
				case Boolean:
					return BOOLEAN;
				case Byte:
				case Char:
				case Double:
				case Float:
				case Int:
				case Long:
				case Short:
					return NUMBER;
				}
			} else if (type instanceof ReferenceType) {
				ReferenceType rType = (ReferenceType) type;
				NodeList<ArraySlot> slots = rType.getSlots();
				int arrayCount = slots != null ? slots.size() : 0;
				if (arrayCount == 0) {
					return getJSDocType(rType.getType(), ctx);
				} else {
					ReferenceType newType = com.digiarea.jse.NodeFacade
							.ReferenceType(rType.getType(), arrayCount - 1);
					return "Array.<" + getJSDocType(newType, ctx) + ">";
				}
			} else if (type.toString().equals(JAVA_LANG_OBJECT)) {
				return OBJECT;
			} else if (NodeUtils.isString(type)) {
				return STRING;
			} else if (NodeUtils.isWrapped(type)) {
				if (NodeUtils.isBoolean(type, false)) {
					return BOOLEAN;
				} else {
					return NUMBER;
				}
			} else if (type instanceof ClassOrInterfaceType) {
				// need to provide and require
				type.accept(this, ctx);
				ClassOrInterfaceType clazz = (ClassOrInterfaceType) type;
				StringBuilder rowType = new StringBuilder();
				ClassOrInterfaceType scope = clazz.getScope();
				List<Type> args = clazz.getTypeArgs();
				if (scope != null || args != null) {
					if (scope != null) {
						rowType.append(getJSDocType(scope, ctx));
						rowType.append(".");
					}
					rowType.append(getJSDocType(com.digiarea.jse.NodeFacade
							.ClassOrInterfaceType(clazz.getName()), ctx));
					if (args != null && args.size() > 0) {
						rowType.append(".<");
						for (Type arg : args) {
							rowType.append(getJSDocType(arg, ctx));
						}
						rowType.append(">");
					}
				} else {
					rowType.append(type.toString());
				}
				return rowType.toString();
			} else if (type instanceof WildcardType) {
				WildcardType wild = (WildcardType) type;
				if (wild.getExtendsType() != null) {
					return getJSDocType(wild.getExtendsType(), ctx);
				} else {
					return EMPTY;
				}
			} else {
				System.err.println("WRONG TYPE: " + type);
				return type.accept(this, ctx).toString().replace("<", ".<");
			}
		}
		return null;
	}

	/**
	 * Gets the JS doc modifiers.
	 * 
	 * @param modifier
	 *            the modifier
	 * @return the JS doc modifiers
	 */
	private String getJSDocModifiers(int modifier) {
		String content = EMPTY;
		if (Modifiers.isPrivate(modifier)) {
			content = content + "\n * @private";
		} else if (Modifiers.isProtected(modifier)) {
			content = content + "\n * @protected";
		}
		return content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.LabeledStmt
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.LabeledStmt n, Context ctx)
			throws Exception {
		LabelledStatement img = NodeFacade.LabelledStatement();
		img.setLabel(n.getLabel());
		if (n.getStmt() != null) {
			img.setStatement((Statement) n.getStmt().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.LineComment
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.LineComment n, Context ctx)
			throws Exception {
		LineComment img = NodeFacade.LineComment();
		img.setContent("//" + n.getContent());
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * LongLiteralExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.LongLiteralExpr n, Context ctx)
			throws Exception {
		String expr = n.getValue().substring(0, n.getValue().length() - 1);
		return NodeFacade.HexIntegerLiteral(expr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * MarkerAnnotationExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.MarkerAnnotationExpr n, Context ctx)
			throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * MemberValuePair, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.MemberValuePair n, Context ctx)
			throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.MethodCallExpr
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.MethodCallExpr n, Context ctx)
			throws Exception {
		CallExpression img = NodeFacade.CallExpression();
		Expression scope = null;
		IdentifierName name = NodeFacade.IdentifierName(n.getName());
		if (n.getScope() != null) {
			if (n.getScope() instanceof SuperExpr) {
				scope = NodeFacade.IdentifierName(GOOG_BASE);
			} else {
				scope = (Expression) n.getScope().accept(this, ctx);
				scope = NodeFacade.FieldAccessExpression(scope, name);
			}
		} else {
			scope = name;
		}
		img.setScope(scope);
		if (n.getArgs() != null) {
			List<Expression> args = new ArrayList<Expression>();
			if (n.getScope() instanceof SuperExpr) {
				args.add(NodeFacade.ThisExpression());
				args.add((Expression) com.digiarea.jse.NodeFacade
						.StringLiteralExpr(n.getName()).accept(this, ctx));
			}
			for (com.digiarea.jse.Expression item : n.getArgs()) {
				if (item != null) {
					args.add((Expression) item.accept(this, ctx));
				}
			}
			img.setArgs(NodeFacade.NodeList(args));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * MethodDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.MethodDeclaration n, Context ctx)
			throws Exception {
		// FIXME method reference!
		FunctionExpression img = NodeFacade.FunctionExpression();
		if (n.getParameters() != null) {
			List<Parameter> parameters = new ArrayList<Parameter>();
			for (com.digiarea.jse.Parameter item : n.getParameters()) {
				if (item != null) {
					parameters.add((Parameter) item.accept(this, ctx));
				}
			}
			img.setParameters(NodeFacade.NodeList(parameters));
		}
		if (procesBodies && n.getBlock() != null) {
			img.setBody((Block) n.getBlock().accept(this, ctx));
		} else {
			img.setBody(NodeFacade.Block());
		}
		Statement statement = null;
		if (ctx.isAnonymousClass()) {
			statement = makeSimple(n.getName(), img);
		} else if (n.getModifiers().isStatic()) {
			statement = makeStatic(getFullName(ctx), n.getName(), img);
		} else {
			statement = makePrototype(getFullName(ctx), n.getName(), img);
		}
		com.digiarea.jse.Node oldParent = ctx.getParent();
		ctx.setParent(n);
		if (n.getJavaDoc() != null) {
			statement.setJsDocComment((JSDocComment) n.getJavaDoc().accept(
					this, ctx));
		} else {
			statement.setJsDocComment((JSDocComment) visit(
					com.digiarea.jse.NodeFacade.JavadocComment(), ctx));
		}
		ctx.setParent(oldParent);
		return statement;
	}

	/**
	 * Gets the full name.
	 * 
	 * @param ctx
	 *            the ctx
	 * @return the full name
	 */
	private String getFullName(Context ctx) {
		// return ctx.getCompilationUnit().getPackageDeclaration().getName()
		// .toString()
		// + "." + ctx.getTypeDeclaration().getName();
		return ctx.getEnclosure().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.Modifiers,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.Modifiers n, Context ctx)
			throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.NameExpr,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.NameExpr n, Context ctx)
			throws Exception {
		IdentifierName img = NodeFacade.IdentifierName();
		String name = n.getName();
		com.digiarea.jse.Node parent = ctx.getParent();
		if (parent instanceof QualifiedNameExpr) {
			name = fixNamespace(name);
		}
		img.setValue(name);
		return img;
	}

	/**
	 * Fix namespace.
	 * 
	 * @param name
	 *            the name
	 * @return the string
	 */
	private String fixNamespace(String name) {
		// nothing to do
		// if (name.equals("java")) {
		// return "js";
		// } else if (name.equals("javax")) {
		// return "jsx";
		// } else if (name.equals("javafx")) {
		// return "jsfx";
		// } else {
		// return name;
		// }
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * NormalAnnotationExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.NormalAnnotationExpr n, Context ctx)
			throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * NullLiteralExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.NullLiteralExpr n, Context ctx)
			throws Exception {
		return NodeFacade.NullLiteral();
	}

	/**
	 * Can add require.
	 * 
	 * @param type
	 *            the type
	 * @param ctx
	 *            the ctx
	 * @return true, if successful
	 */
	private boolean canAddRequire(ClassOrInterfaceType type, Context ctx) {
		if ((modelHierarchy.getBuilder(type.getName().toString(),
				BuilderType.CLASS) != null && modelHierarchy.getUpdater(type
				.getName().toString()) != null)
				|| type.getName().toString().startsWith("goog.")
				|| type.getName().toString().startsWith("digiarea.")) {
			if (!(ctx.getParent() instanceof InstanceOfExpr)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * ObjectCreationExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ObjectCreationExpr n, Context ctx)
			throws Exception {
		com.digiarea.jse.Node parent = ctx.getParent();
		if (canAddRequire(n.getType(), ctx)) {
			addRequire(n.getType().getName().toString());
		}
		Expression scope = null;
		if (parent != null && (parent instanceof ThrowStmt)) {
			scope = NodeFacade.IdentifierName(ERROR);
		} else {
			IdentifierName name = NodeFacade.IdentifierName(removeGenericTypes(
					n.getType()).accept(this, ctx).toString());
			if (n.getScope() != null) {
				scope = NodeFacade.FieldAccessExpression((Expression) n
						.getScope().accept(this, ctx), name);
			} else {
				scope = name;
			}
		}
		List<Expression> args = new ArrayList<Expression>();
		if (n.getArgs() != null) {
			for (com.digiarea.jse.Expression item : n.getArgs()) {
				if (item != null) {
					args.add((Expression) item.accept(this, ctx));
				}
			}
		}
		CallExpression img = NodeFacade.CallExpression(scope, args);
		ObjectLiteral initializer = null;
		if (n.getAnonymousClassBody() != null) {
			boolean anonymousClass = ctx.isAnonymousClass();
			ctx.setAnonymousClass(true);
			List<PropertyAssignment> properties = new ArrayList<>();
			initializer = NodeFacade.ObjectLiteral(properties);
			for (BodyDeclaration item : n.getAnonymousClassBody()) {
				if (item != null) {
					Statement result = (Statement) item.accept(this, ctx);
					if (item instanceof InitializerDeclaration) {
						// FIXME
						/**
						 * var Baz = (function() {
						 * 
						 * { // InitializerDeclaration }
						 * 
						 * return { foo: fooVal, bar: barVal }; })();
						 */

					} else if (result instanceof Block) {
						List<Statement> stmts = ((Block) result)
								.getStatements();
						for (Statement statement : stmts) {
							PutAssignment stmt = makePutAssignment(statement,
									ctx);
							if (stmt != null) {
								properties.add(stmt);
							}
						}
					} else {
						PutAssignment put = makePutAssignment(result, ctx);
						if (put != null) {
							properties.add(put);
						}
					}
				}
			}
			ctx.setAnonymousClass(anonymousClass);
		}
		return NodeFacade.NewExpression(img, initializer);
	}

	/**
	 * Make put assignment.
	 * 
	 * @param statement
	 *            the statement
	 * @param ctx
	 *            the ctx
	 * @return the put assignment
	 * @throws Exception
	 *             the exception
	 */
	private PutAssignment makePutAssignment(Statement statement, Context ctx)
			throws Exception {
		if (statement instanceof ExpressionStatement) {
			Expression expression = ((ExpressionStatement) statement)
					.getExpression();
			if (expression instanceof AssignmentExpression) {
				AssignmentExpression expr = (AssignmentExpression) expression;
				return NodeFacade.PutAssignment(
						(IdentifierName) expr.getTarget(), expr.getValue());
			}
		} else {
			System.err.println("ERR: " + ctx.getCompilationUnit().getName());
			System.err.println("STM: " + statement.getClass());
			System.err.println("Wrong Statement for PutAssignment: "
					+ statement);
		}
		return null;
	}

	/**
	 * Removes the generic types.
	 * 
	 * @param type
	 *            the type
	 * @return the class or interface type
	 * @throws Exception
	 *             the exception
	 */
	private ClassOrInterfaceType removeGenericTypes(ClassOrInterfaceType type)
			throws Exception {
		ClassOrInterfaceType newType = (ClassOrInterfaceType) type.clone();
		newType.setTypeArgs(null);
		return newType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * PackageDeclaration, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.PackageDeclaration n, Context ctx)
			throws Exception {
		// nothing to do
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.Parameter,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.Parameter n, Context ctx)
			throws Exception {
		Parameter img = NodeFacade.Parameter();
		if (n.getId() != null) {
			img.setName(n.getId().getName());
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.PrimitiveType
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.PrimitiveType n, Context ctx)
			throws Exception {
		if (n.getType() == com.digiarea.jse.PrimitiveType.Primitive.Boolean) {
			return NodeFacade.IdentifierName("boolean");
		} else {
			return NodeFacade.IdentifierName(NUMBER);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.PrimitiveType
	 * .Primitive, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.PrimitiveType.Primitive n, Context ctx)
			throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.Project,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.Project n, Context ctx) throws Exception {
		Project img = NodeFacade.Project();
		if (n.getCompilationUnits() != null) {
			List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();
			for (com.digiarea.jse.CompilationUnit item : n
					.getCompilationUnits()) {
				if (item != null) {
					compilationUnits.add((CompilationUnit) item.accept(this,
							ctx));
				}
			}
			img.setCompilationUnits(NodeFacade.NodeList(compilationUnits));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * QualifiedNameExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.QualifiedNameExpr n, Context ctx)
			throws Exception {
		com.digiarea.jse.Node parent = ctx.getParent();
		ctx.setParent(n);
		NameExpr qualifier = n.getQualifier();
		if (Character.isUpperCase(n.getName().charAt(0))) {
			if (ctx.getParent() == null
					|| !(ctx.getParent() instanceof InstanceOfExpr)) {
				addRequire(qualifier.toString() + "." + n.getName());
			}
		}
		FieldAccessExpression img = NodeFacade.FieldAccessExpression();
		if (qualifier != null) {
			img.setScope((Expression) qualifier.accept(this, ctx));
		}
		img.setField(NodeFacade.IdentifierName(n.getName()));
		ctx.setParent(parent);
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.ReferenceType
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ReferenceType n, Context ctx)
			throws Exception {
		if (n.getType() != null) {
			return n.getType().accept(this, ctx);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.ReturnStmt
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ReturnStmt n, Context ctx)
			throws Exception {
		ReturnStatement img = NodeFacade.ReturnStatement();
		if (n.getExpression() != null) {
			img.setExpression((Expression) n.getExpression().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * SingleMemberAnnotationExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.SingleMemberAnnotationExpr n, Context ctx)
			throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * StringLiteralExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.StringLiteralExpr n, Context ctx)
			throws Exception {
		return NodeFacade.StringLiteralDouble(n.getValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.SuperExpr,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.SuperExpr n, Context ctx)
			throws Exception {
		return NodeFacade.ThisExpression();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * SwitchEntryStmt, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.SwitchEntryStmt n, Context ctx)
			throws Exception {
		CaseClause img = NodeFacade.CaseClause();
		if (n.getLabel() != null) {
			img.setExpression((Expression) n.getLabel().accept(this, ctx));
		}
		if (n.getStmts() != null) {
			List<Statement> stmts = new ArrayList<Statement>();
			for (com.digiarea.jse.Statement item : n.getStmts()) {
				if (item != null) {
					stmts.add((Statement) item.accept(this, ctx));
				}
			}
			img.setStatements(NodeFacade.NodeList(stmts));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.SwitchStmt
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.SwitchStmt n, Context ctx)
			throws Exception {
		SwitchStatement img = NodeFacade.SwitchStatement();
		if (n.getSelector() != null) {
			img.setExpression((Expression) n.getSelector().accept(this, ctx));
		}
		if (n.getEntries() != null) {
			List<CaseClause> entries = new ArrayList<CaseClause>();
			List<com.digiarea.jse.Statement> jseStatements = null;
			for (SwitchEntryStmt item : n.getEntries()) {
				if (item != null) {
					if (item.getLabel() != null) {
						entries.add((CaseClause) item.accept(this, ctx));
					} else {
						jseStatements = item.getStmts();
					}
				}
			}
			List<Statement> es5Statements = new ArrayList<Statement>();
			if (jseStatements != null) {
				for (com.digiarea.jse.Statement statement : jseStatements) {
					es5Statements.add((Statement) statement.accept(this, ctx));
				}
			}
			img.setBlock(NodeFacade.CaseBlock(
					NodeFacade.DefaultClause(es5Statements),
					NodeFacade.NodeList(entries), null, 0, 0));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * SynchronizedStmt, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.SynchronizedStmt n, Context ctx)
			throws Exception {
		WithStatement img = NodeFacade.WithStatement();
		if (n.getExpression() != null) {
			img.setExpression((Expression) n.getExpression().accept(this, ctx));
		}
		if (n.getBlock() != null) {
			img.setStatement((Statement) n.getBlock().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.ThisExpr,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ThisExpr n, Context ctx)
			throws Exception {
		ThisExpression img = NodeFacade.ThisExpression();
		// TODO check this out (Expression) n.getClassExpr().accept(this, ctx);
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.ThrowStmt,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.ThrowStmt n, Context ctx)
			throws Exception {
		com.digiarea.jse.Node oldParent = ctx.getParent();
		ctx.setParent(n);
		ThrowStatement img = NodeFacade.ThrowStatement();
		if (n.getExpression() != null) {
			Expression expr = (Expression) n.getExpression().accept(this, ctx);
			img.setExpression(expr);
		}
		ctx.setParent(oldParent);
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.TryStmt,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.TryStmt n, Context ctx) throws Exception {
		TryStatement img = NodeFacade.TryStatement();
		if (n.getTryBlock() != null) {
			img.setTryBlock((Block) n.getTryBlock().accept(this, ctx));
		}
		if (n.getCatchClauses() != null) {
			List<CatchClause> catchs = new ArrayList<CatchClause>();
			for (com.digiarea.jse.CatchClause item : n.getCatchClauses()) {
				if (item != null) {
					catchs.add((CatchClause) item.accept(this, ctx));
				}
			}
			img.setCatchClause(catchs.get(0));
		}
		if (n.getFinallyBlock() != null) {
			img.setFinallyBlock((Block) n.getFinallyBlock().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * TypeDeclarationStmt, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.TypeDeclarationStmt n, Context ctx)
			throws Exception {
		return n.getTypeDeclaration().accept(this, ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.TypeParameter
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.TypeParameter n, Context ctx)
			throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.UnaryExpr,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.UnaryExpr n, Context ctx)
			throws Exception {
		UnaryExpression img = (UnaryExpression) n.getOperator().accept(this,
				ctx);
		if (n.getExpression() != null) {
			img.setExpression((Expression) n.getExpression().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.UnaryExpr
	 * .UnaryOperator, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.UnaryExpr.UnaryOperator n, Context ctx)
			throws Exception {
		UnaryExpression img = NodeFacade.UnaryExpression();
		UnaryOperator operator = null;
		switch (n) {
		case inverse:
			operator = UnaryOperator.inverse;
			break;
		case negative:
			operator = UnaryOperator.negative;
			break;
		case not:
			operator = UnaryOperator.not;
			break;
		case positive:
			operator = UnaryOperator.positive;
			break;
		case preDecrement:
			operator = UnaryOperator.preDecrement;
			break;
		case preIncrement:
			operator = UnaryOperator.preIncrement;
			break;
		case posDecrement:
			operator = UnaryOperator.posDecrement;
			break;
		case posIncrement:
			operator = UnaryOperator.posIncrement;
			break;
		}
		img.setUnaryOperator(operator);
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * VariableDeclarationExpr, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.VariableDeclarationExpr n, Context ctx)
			throws Exception {
		VariableExpression img = NodeFacade.VariableExpression();
		if (n.getVars() != null) {
			List<VariableDeclaration> vars = new ArrayList<VariableDeclaration>();
			com.digiarea.jse.Node oldParent = ctx.getParent();
			ctx.setParent(n);
			for (VariableDeclarator item : n.getVars()) {
				if (item != null) {
					vars.add((VariableDeclaration) item.accept(this, ctx));
				}
			}
			img.setVariableDeclarations(NodeFacade.NodeList(vars));
			ctx.setParent(oldParent);
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * VariableDeclarator, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.VariableDeclarator n, Context ctx)
			throws Exception {
		com.digiarea.jse.Expression init = n.getInit();
		VariableDeclaratorId id = n.getId();
		return makeVariable(ctx, id.getName(), init);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * VariableDeclaratorId, java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.VariableDeclaratorId n, Context ctx)
			throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.VoidType,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.VoidType n, Context ctx)
			throws Exception {
		// FIXME
		return NodeFacade.IdentifierName(n.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.WhileStmt,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.WhileStmt n, Context ctx)
			throws Exception {
		WhileStatement img = NodeFacade.WhileStatement();
		if (n.getCondition() != null) {
			img.setCondition((Expression) n.getCondition().accept(this, ctx));
		}
		if (n.getBody() != null) {
			img.setBody((Statement) n.getBody().accept(this, ctx));
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.WildcardType
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(com.digiarea.jse.WildcardType n, Context ctx)
			throws Exception {
		// FIXME
		return NodeFacade.IdentifierName(n.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.ArraySlot,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(ArraySlot n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.Ellipsis,
	 * java.lang.Object)
	 */
	@Override
	public Node visit(Ellipsis n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.LambdaBlock
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(LambdaBlock n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.LambdaExpr
	 * , java.lang.Object)
	 */
	@Override
	public Node visit(LambdaExpr n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * CreationReference, java.lang.Object)
	 */
	@Override
	public Node visit(CreationReference n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * ExpressionMethodReference, java.lang.Object)
	 */
	@Override
	public Node visit(ExpressionMethodReference n, Context ctx)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * SuperMethodReference, java.lang.Object)
	 */
	@Override
	public Node visit(SuperMethodReference n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.
	 * TypeMethodReference, java.lang.Object)
	 */
	@Override
	public Node visit(TypeMethodReference n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.digiarea.jse.visitor.GenericVisitor#visit(com.digiarea.jse.NodeList,
	 * java.lang.Object)
	 */
	@Override
	public <E extends com.digiarea.jse.Node> Node visit(NodeList<E> n,
			Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
