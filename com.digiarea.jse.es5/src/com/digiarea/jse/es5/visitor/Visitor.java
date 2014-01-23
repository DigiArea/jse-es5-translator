package com.digiarea.jse.es5.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import com.digiarea.common.utils.SourcePrinter;
import com.digiarea.es5.AllocationExpression;
import com.digiarea.es5.ArrayAccessExpression;
import com.digiarea.es5.ArrayLiteral;
import com.digiarea.es5.AssignmentExpression;
import com.digiarea.es5.AssignmentExpression.AssignOperator;
import com.digiarea.es5.BinaryExpression;
import com.digiarea.es5.BinaryExpression.BinaryOperator;
import com.digiarea.es5.Block;
import com.digiarea.es5.BlockComment;
import com.digiarea.es5.BooleanLiteral;
import com.digiarea.es5.BreakStatement;
import com.digiarea.es5.CallExpression;
import com.digiarea.es5.CaseBlock;
import com.digiarea.es5.CaseClause;
import com.digiarea.es5.CatchClause;
import com.digiarea.es5.Comment;
import com.digiarea.es5.CompilationUnit;
import com.digiarea.es5.ConditionalExpression;
import com.digiarea.es5.ContinueStatement;
import com.digiarea.es5.DecimalLiteral;
import com.digiarea.es5.DefaultClause;
import com.digiarea.es5.DoWhileStatement;
import com.digiarea.es5.EmptyStatement;
import com.digiarea.es5.EnclosedExpression;
import com.digiarea.es5.Expression;
import com.digiarea.es5.ExpressionStatement;
import com.digiarea.es5.FieldAccessExpression;
import com.digiarea.es5.ForStatement;
import com.digiarea.es5.ForeachStatement;
import com.digiarea.es5.FunctionExpression;
import com.digiarea.es5.HexIntegerLiteral;
import com.digiarea.es5.IdentifierName;
import com.digiarea.es5.IfStatement;
import com.digiarea.es5.JSDocComment;
import com.digiarea.es5.LabelledStatement;
import com.digiarea.es5.LineComment;
import com.digiarea.es5.NewExpression;
import com.digiarea.es5.Node;
import com.digiarea.es5.NullLiteral;
import com.digiarea.es5.ObjectLiteral;
import com.digiarea.es5.Parameter;
import com.digiarea.es5.Project;
import com.digiarea.es5.PropertyAssignment;
import com.digiarea.es5.PutAssignment;
import com.digiarea.es5.ReturnStatement;
import com.digiarea.es5.SequenceExpression;
import com.digiarea.es5.Statement;
import com.digiarea.es5.StringLiteralDouble;
import com.digiarea.es5.StringLiteralSingle;
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
import com.digiarea.jse.BlockStmt;
import com.digiarea.jse.BodyDeclaration;
import com.digiarea.jse.ClassDeclaration;
import com.digiarea.jse.ClassOrInterfaceType;
import com.digiarea.jse.ConstructorDeclaration;
import com.digiarea.jse.Ellipsis;
import com.digiarea.jse.EnumDeclaration;
import com.digiarea.jse.ExplicitConstructorInvocationStmt;
import com.digiarea.jse.FieldDeclaration;
import com.digiarea.jse.InitializerDeclaration;
import com.digiarea.jse.InstanceOfExpr;
import com.digiarea.jse.InterfaceDeclaration;
import com.digiarea.jse.JavadocComment;
import com.digiarea.jse.LambdaBlock;
import com.digiarea.jse.LambdaExpr;
import com.digiarea.jse.MethodDeclaration;
import com.digiarea.jse.MethodExprRef;
import com.digiarea.jse.MethodRef;
import com.digiarea.jse.MethodTypeRef;
import com.digiarea.jse.Modifiers;
import com.digiarea.jse.NameExpr;
import com.digiarea.jse.NodeList;
import com.digiarea.jse.PrimitiveType;
import com.digiarea.jse.PrimitiveType.Primitive;
import com.digiarea.jse.QualifiedNameExpr;
import com.digiarea.jse.ReferenceType;
import com.digiarea.jse.StringLiteralExpr;
import com.digiarea.jse.SuperExpr;
import com.digiarea.jse.SwitchEntryStmt;
import com.digiarea.jse.ThisExpr;
import com.digiarea.jse.ThrowStmt;
import com.digiarea.jse.Type;
import com.digiarea.jse.TypeDeclaration;
import com.digiarea.jse.VariableDeclarator;
import com.digiarea.jse.VariableDeclaratorId;
import com.digiarea.jse.VoidType;
import com.digiarea.jse.WildcardType;
import com.digiarea.jse.es5.Context;
import com.digiarea.jse.visitor.GenericVisitor;

public class Visitor implements GenericVisitor<Node, Context> {

	private static final String EMPTY = "";
	private static final String OBJECT = "object";
	private static final String UNDEFINED = "undefined";
	private static final String NUMBER = "number";
	private static final String STRING = "string";
	private static final String ERROR = "Error";
	private static final String BOOLEAN = "boolean";
	// closure
	private static final String GOOG_BASE = "goog.base";
	private static final String GOOG_REQUIRE = "goog.require";
	private static final String GOOG_INHERITS = "goog.inherits";
	private static final String GOOG_PROVIDE = "goog.provide";
	private static final String GOOG_ISSTRING = "goog.isString";
	private static final String GOOG_ISNUMBER = "goog.isNumber";

	// java
	private static final String JAVA_LANG_INTEGER = "java.lang.Integer";
	private static final String JAVA_LANG_STRING = "java.lang.String";
	// private static final String JAVA_LANG_EXCEPTION = "java.lang.Exception";
	private static final String JAVA_LANG_OBJECT = "java.lang.Object";
	private static List<String> types = Arrays.asList(JAVA_LANG_STRING,
			JAVA_LANG_INTEGER);

	// javascript
	private static final String PROTOTYPE = "prototype";

	private ModelHierarchy modelHierarchy;
	private boolean procesBodies = true;

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

	public Visitor(ModelHierarchy modelHierarchy) {
		this(modelHierarchy, true);
	}

	private Statement makeTypeDeclaration(TypeDeclaration n, Context ctx)
			throws Exception {
		TypeDeclaration oldDecl = ctx.getTypeDeclaration();
		ctx.setTypeDeclaration(n);
		ctx.addEnclosure(n.getName());
		Block img = new Block();
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
						if (ModifierSet.isStatic(field.getModifiers())) {
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
				ConstructorDeclaration constructor = new ConstructorDeclaration(
						ModifierSet.PUBLIC, n.getName());
				com.digiarea.jse.Statement explicitConstructorInvocationStmt = new ExplicitConstructorInvocationStmt(
						false, null, null);
				constructor.setBlock(new BlockStmt(Arrays
						.asList(explicitConstructorInvocationStmt)));
				constructors.add((Statement) constructor.accept(this, ctx));
			} else {
				for (Statement statement : constructors) {
					ExpressionStatement constructor = (ExpressionStatement) statement;
					FunctionExpression functionExpression = (FunctionExpression) ((AssignmentExpression) constructor
							.getExpression()).getValue();
					List<Statement> body = new ArrayList<Statement>();
					if (functionExpression.getBody().getStmts() != null) {
						body.addAll(functionExpression.getBody().getStmts());
					}
					functionExpression.setBody(new Block(body));
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
			img.setStmts(members);
		}
		// we add comments from Class and Interfaces to goog.provide declaration
		// if (n.getJavaDoc() != null) {
		// img.setComment((Comment) n.getJavaDoc().accept(this, ctx));
		// }
		ctx.setTypeDeclaration(oldDecl);
		ctx.cutEnclosure();
		return img;
	}

	private Statement makePrototype(String decl, String name,
			Expression expression) {
		return new ExpressionStatement(new AssignmentExpression(
				new FieldAccessExpression(new FieldAccessExpression(
						new IdentifierName(decl), new IdentifierName(
								Visitor.PROTOTYPE)), new IdentifierName(name)),
				expression, AssignOperator.assign));
	}

	private Statement makeConstructor(String decl, Expression expression) {
		return new ExpressionStatement(new AssignmentExpression(
				new FieldAccessExpression(new IdentifierName(decl), null),
				expression, AssignOperator.assign));
	}

	private Statement makeStatic(String decl, String name, Expression expression) {
		return new ExpressionStatement(new AssignmentExpression(
				new FieldAccessExpression(new IdentifierName(decl),
						new IdentifierName(name)), expression,
				AssignOperator.assign));
	}

	private Statement makeSimple(String name, FunctionExpression expression) {
		return new ExpressionStatement(new AssignmentExpression(
				new IdentifierName(name), expression, AssignOperator.assign));
	}

	private Node makeVariable(Context ctx, String id,
			com.digiarea.jse.Expression init) throws Exception {
		com.digiarea.jse.Node parent = ctx.getParent();
		if (parent != null && (parent instanceof FieldDeclaration)) {
			FieldDeclaration field = (FieldDeclaration) parent;
			Expression head = null;
			if (ctx.isAnonymousClass()) {
				head = null;
			} else if (ModifierSet.isStatic(field.getModifiers())) {
				head = new FieldAccessExpression(new IdentifierName(
						getFullName(ctx)), null);
			} else {
				// head = new ThisExpression();
				head = new FieldAccessExpression(new IdentifierName(
						getFullName(ctx) + "." + PROTOTYPE), null);
			}
			Expression img = head == null ? new IdentifierName(id)
					: new FieldAccessExpression(head, new IdentifierName(id));
			if (init != null) {
				img = new AssignmentExpression(img, (Expression) init.accept(
						this, ctx), AssignOperator.assign);
			} else {
				img = new AssignmentExpression(img,
						getDefault(field.getType()), AssignOperator.assign);
			}
			return new ExpressionStatement(img);
		} else {
			VariableDeclaration img = new VariableDeclaration();
			if (id != null) {
				img.setName(id);
			}
			if (init != null) {
				img.setExpression((Expression) init.accept(this, ctx));
			}
			return img;
		}
	}

	private Expression getDefault(Type type) {
		if (type instanceof PrimitiveType) {
			if (((PrimitiveType) type).getType() == Primitive.Boolean) {
				return new BooleanLiteral(false);
			} else {
				return new DecimalLiteral("0");
			}
		} else {
			return new NullLiteral();
		}
	}

	@Override
	public Node visit(com.digiarea.jse.AnnotationDeclaration n, Context ctx)
			throws Exception {
		return makeTypeDeclaration(n, ctx);
	}

	@Override
	public Node visit(com.digiarea.jse.AnnotationMemberDeclaration n,
			Context ctx) throws Exception {
		FunctionExpression img = new FunctionExpression();
		img.setBody(new Block());
		Statement statement = null;
		if (ctx.isAnonymousClass()) {
			statement = makeSimple(n.getName(), img);
		} else if (ModifierSet.isStatic(n.getModifiers())) {
			statement = makeStatic(getFullName(ctx), n.getName(), img);
		} else {
			statement = makePrototype(getFullName(ctx), n.getName(), img);
		}
		com.digiarea.jse.Node oldParent = ctx.getParent();
		ctx.setParent(n);
		if (n.getJavaDoc() != null) {
			statement.setComment((Comment) n.getJavaDoc().accept(this, ctx));
		} else {
			statement.setComment((Comment) visit(new JavadocComment(), ctx));
		}
		ctx.setParent(oldParent);
		return statement;
	}

	@Override
	public Node visit(com.digiarea.jse.ArrayAccessExpr n, Context ctx)
			throws Exception {
		ArrayAccessExpression img = new ArrayAccessExpression();
		if (n.getName() != null) {
			img.setName((Expression) n.getName().accept(this, ctx));
		}
		if (n.getIndex() != null) {
			img.setIndex((Expression) n.getIndex().accept(this, ctx));
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.ArrayCreationExpr n, Context ctx)
			throws Exception {
		if (n.getInitializer() != null) {
			return (ArrayLiteral) n.getInitializer().accept(this, ctx);
		} else {
			Expression scope = new IdentifierName("Array");
			CallExpression img = new CallExpression(scope, null);
			List<Expression> values = new ArrayList<Expression>();
			for (com.digiarea.jse.Expression item : n.getDimensions()) {
				if (item != null) {
					values.add((Expression) item.accept(this, ctx));
				}
			}
			img.setArgs(values);
			return new AllocationExpression(img);
		}
	}

	@Override
	public Node visit(com.digiarea.jse.ArrayInitializerExpr n, Context ctx)
			throws Exception {
		ArrayLiteral img = new ArrayLiteral();
		if (n.getValues() != null) {
			List<Expression> values = new ArrayList<Expression>();
			for (com.digiarea.jse.Expression item : n.getValues()) {
				if (item != null) {
					values.add((Expression) item.accept(this, ctx));
				}
			}
			img.setExpressions(values);
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.AssertStmt n, Context ctx)
			throws Exception {
		return null;
	}

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

	@Override
	public Node visit(com.digiarea.jse.AssignExpr.AssignOperator n, Context ctx)
			throws Exception {
		AssignmentExpression img = new AssignmentExpression();
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

	@Override
	public Node visit(com.digiarea.jse.BinaryExpr.BinaryOperator n, Context ctx)
			throws Exception {
		BinaryExpression img = new BinaryExpression();
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

	@Override
	public Node visit(com.digiarea.jse.BlockComment n, Context ctx)
			throws Exception {
		BlockComment img = new BlockComment();
		img.setContent("/*" + n.getContent() + "*/");
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.BlockStmt n, Context ctx)
			throws Exception {
		Block img = new Block();
		if (n.getStatements() != null) {
			List<Statement> stmts = new ArrayList<Statement>();
			for (com.digiarea.jse.Statement item : n.getStatements()) {
				if (item != null) {
					stmts.add((Statement) item.accept(this, ctx));
				}
			}
			img.setStmts(stmts);
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.BooleanLiteralExpr n, Context ctx)
			throws Exception {
		return new BooleanLiteral(n.isValue());
	}

	@Override
	public Node visit(com.digiarea.jse.BreakStmt n, Context ctx)
			throws Exception {
		BreakStatement img = new BreakStatement();
		img.setIdentifier(n.getId());
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.CastExpr n, Context ctx)
			throws Exception {
		return (Expression) n.getExpression().accept(this, ctx);
	}

	@Override
	public Node visit(com.digiarea.jse.CatchClause n, Context ctx)
			throws Exception {
		CatchClause img = new CatchClause();
		// FIXME
		img.setString(n.getName());
		if (n.getCatchBlock() != null) {
			img.setBlock((Block) n.getCatchBlock().accept(this, ctx));
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.CharLiteralExpr n, Context ctx)
			throws Exception {
		return new StringLiteralSingle(String.valueOf((int) n.getValue()
				.charAt(0)));
	}

	@Override
	public Node visit(com.digiarea.jse.ClassDeclaration n, Context ctx)
			throws Exception {
		return makeTypeDeclaration(n, ctx);
	}

	@Override
	public Node visit(com.digiarea.jse.ClassExpr n, Context ctx)
			throws Exception {
		com.digiarea.jse.Node parent = ctx.getParent();
		ctx.setParent(n);
		FieldAccessExpression img = new FieldAccessExpression();
		if (n.getType() != null) {
			img.setScope((Expression) n.getType().accept(this, ctx));
		}
		img.setField(new IdentifierName("class"));
		ctx.setParent(parent);
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.ClassOrInterfaceType n, Context ctx)
			throws Exception {
		FieldAccessExpression img = new FieldAccessExpression();
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
					return new IdentifierName(expression.toString());
				}
			} else {
				return (FieldAccessExpression) expression;
			}
		}
		return img;
	}

	private LinkedHashSet<String> requires;
	private String provide;

	private void addRequire(String require) {
		if (!require.equals(provide)) {
			requires.add(require);
		}
	}

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
		// as package might be set we can set the new enclosure
		if (n.getPackageDeclaration() != null) {
			ctx.setEnclosure(n.getPackageDeclaration().getName());
		} else {
			ctx.setEnclosure(new NameExpr());
		}
		ctx.setCompilationUnit(n);
		CompilationUnit img = new CompilationUnit();
		if (n.getTypes() != null) {
			List<Statement> types = new ArrayList<Statement>();
			requires = new LinkedHashSet<String>();
			for (TypeDeclaration item : n.getTypes()) {
				if (item != null) {
					ExpressionStatement provide = createClosureProvide(n, ctx);
					if (item.getJavaDoc() != null) {
						provide.setComment((Comment) item.getJavaDoc().accept(
								this, ctx));
					}
					types.add(provide);
					Statement statement = (Statement) item.accept(this, ctx);
					for (String string : requires) {
						types.add(createClosureRequire(string, ctx));
					}
					types.add(statement);
				}
			}
			img.setElements(types);
		}
		img.setName(n.getName());
		return img;
	}

	private ExpressionStatement createClosureRequire(String n, Context ctx)
			throws Exception {
		CallExpression provideExpression = new CallExpression();
		IdentifierName name = new IdentifierName(GOOG_REQUIRE);
		provideExpression.setScope(name);
		List<Expression> args = new ArrayList<Expression>();
		args.add(new StringLiteralDouble(NodeUtils.toClassOrInterfaceType(n)
				.accept(this, ctx).toString()));
		provideExpression.setArgs(args);
		return new ExpressionStatement(provideExpression);
	}

	private ExpressionStatement createClosureInherit(ClassDeclaration clazz,
			Context ctx) throws Exception {
		if (clazz.getExtendsType() != null) {
			String extendsString = clazz.getExtendsType().accept(this, ctx)
					.toString();
			addRequire(extendsString);
			CallExpression provideExpression = new CallExpression();
			IdentifierName name = new IdentifierName(GOOG_INHERITS);
			provideExpression.setScope(name);
			List<Expression> args = new ArrayList<Expression>();
			args.add((Expression) ctx.getEnclosure().accept(this, ctx));
			args.add((Expression) new NameExpr(extendsString).accept(this, ctx));
			provideExpression.setArgs(args);
			return new ExpressionStatement(provideExpression);
		}
		return null;
	}

	private ExpressionStatement createClosureProvide(
			com.digiarea.jse.CompilationUnit n, Context ctx) throws Exception {
		CallExpression provideExpression = new CallExpression();
		IdentifierName name = new IdentifierName(GOOG_PROVIDE);
		provideExpression.setScope(name);
		List<Expression> args = new ArrayList<Expression>();
		provide = n.getName();
		args.add(new StringLiteralDouble(NodeUtils
				.toClassOrInterfaceType(provide).accept(this, ctx).toString()));
		provideExpression.setArgs(args);
		return new ExpressionStatement(provideExpression);
	}

	@Override
	public Node visit(com.digiarea.jse.ConditionalExpr n, Context ctx)
			throws Exception {
		ConditionalExpression img = new ConditionalExpression();
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

	@Override
	public Node visit(com.digiarea.jse.ConstructorDeclaration n, Context ctx)
			throws Exception {
		com.digiarea.jse.Node oldParent = ctx.getParent();
		ctx.setParent(n);
		FunctionExpression img = new FunctionExpression();
		// img.setName(getFullName(ctx));
		if (n.getParameters() != null) {
			List<Parameter> parameters = new ArrayList<Parameter>();
			for (com.digiarea.jse.Parameter item : n.getParameters()) {
				if (item != null) {
					parameters.add((Parameter) item.accept(this, ctx));
				}
			}
			img.setParameters(parameters);
		}
		if (procesBodies && n.getBlock() != null) {
			img.setBody((Block) n.getBlock().accept(this, ctx));
		} else {
			img.setBody(new Block());
		}
		Statement statement = null;
		statement = makeConstructor(getFullName(ctx), img);
		if (n.getJavaDoc() != null) {
			statement.setComment((Comment) n.getJavaDoc().accept(this, ctx));
		} else {
			statement.setComment((Comment) visit(new JavadocComment(), ctx));
		}
		ctx.setParent(oldParent);
		return statement;
	}

	@Override
	public Node visit(com.digiarea.jse.ContinueStmt n, Context ctx)
			throws Exception {
		ContinueStatement img = new ContinueStatement();
		img.setIdentifier(n.getId());
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.DoStmt n, Context ctx) throws Exception {
		DoWhileStatement img = new DoWhileStatement();
		if (n.getBody() != null) {
			img.setBody((Statement) n.getBody().accept(this, ctx));
		}
		if (n.getCondition() != null) {
			img.setCondition((Expression) n.getCondition().accept(this, ctx));
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.DoubleLiteralExpr n, Context ctx)
			throws Exception {
		String expr = n.getValue();
		char ch = expr.charAt(expr.length() - 1);
		if (ch == 'f' || ch == 'F' || ch == 'd' || ch == 'D') {
			expr = expr.substring(0, expr.length() - 1);
		}
		return new DecimalLiteral(expr);
	}

	@Override
	public Node visit(com.digiarea.jse.EmptyMemberDeclaration n, Context ctx)
			throws Exception {
		EmptyStatement img = new EmptyStatement();
		if (n.getJavaDoc() != null) {
			img.setComment((Comment) n.getJavaDoc().accept(this, ctx));
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.EmptyStmt n, Context ctx)
			throws Exception {
		return new EmptyStatement();
	}

	@Override
	public Node visit(com.digiarea.jse.EmptyTypeDeclaration n, Context ctx)
			throws Exception {
		return makeTypeDeclaration(n, ctx);
	}

	@Override
	public Node visit(com.digiarea.jse.EnclosedExpr n, Context ctx)
			throws Exception {
		EnclosedExpression img = new EnclosedExpression();
		if (n.getInner() != null) {
			img.setInner((Expression) n.getInner().accept(this, ctx));
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.EnumConstantDeclaration n, Context ctx)
			throws Exception {
		FunctionExpression img = new FunctionExpression();
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
			img.setBody(new Block(classBody));
		}
		Statement statement = makeStatic(getFullName(ctx), n.getName(),
				new CallExpression(new EnclosedExpression(
						new AllocationExpression(img)), args));
		if (n.getJavaDoc() != null) {
			statement.setComment((Comment) n.getJavaDoc().accept(this, ctx));
		}
		return statement;
	}

	@Override
	public Node visit(com.digiarea.jse.EnumDeclaration n, Context ctx)
			throws Exception {
		List<BodyDeclaration> members = new ArrayList<BodyDeclaration>();
		members.addAll(n.getEntries());
		if (n.getMembers() != null) {
			members.addAll(n.getMembers());
		}
		n.setMembers(members);
		return makeTypeDeclaration(n, ctx);
	}

	@Override
	public Node visit(com.digiarea.jse.ExplicitConstructorInvocationStmt n,
			Context ctx) throws Exception {
		return createClosureSuperCall(n, ctx);
	}

	private ExpressionStatement createClosureSuperCall(
			com.digiarea.jse.ExplicitConstructorInvocationStmt n, Context ctx)
			throws Exception {
		if (n != null) {
			if (ctx.getTypeDeclaration() instanceof ClassDeclaration) {
				ClassDeclaration clazz = (ClassDeclaration) ctx
						.getTypeDeclaration();
				if (clazz.getExtendsType() != null) {
					CallExpression provideExpression = new CallExpression();
					IdentifierName name = new IdentifierName(
							removeGenericTypes(clazz.getExtendsType())
									.toString() + ".call");
					provideExpression.setScope(name);
					List<Expression> args = new ArrayList<Expression>();
					args.add((Expression) new ThisExpr().accept(this, ctx));
					if (n.getArgs() != null) {
						for (com.digiarea.jse.Expression expression : n
								.getArgs()) {
							args.add((Expression) expression.accept(this, ctx));
						}
					}
					provideExpression.setArgs(args);
					return new ExpressionStatement(provideExpression);
				}
			}
		}
		return null;
	}

	@Override
	public Node visit(com.digiarea.jse.ExpressionStmt n, Context ctx)
			throws Exception {
		ExpressionStatement img = new ExpressionStatement();
		if (n.getExpression() != null) {
			img.setExpression((Expression) n.getExpression().accept(this, ctx));
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.FieldAccessExpr n, Context ctx)
			throws Exception {
		com.digiarea.jse.Node parent = ctx.getParent();
		ctx.setParent(n);
		FieldAccessExpression img = new FieldAccessExpression();
		if (n.getScope() != null) {
			img.setScope((Expression) n.getScope().accept(this, ctx));
		}
		img.setField(new IdentifierName(n.getField()));
		ctx.setParent(parent);
		return img;
	}

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
			statement = new Block(variables);
		}
		if (n.getJavaDoc() != null) {
			statement.setComment((Comment) n.getJavaDoc().accept(this, ctx));
		} else {
			statement.setComment((Comment) visit(new JavadocComment(), ctx));
		}
		ctx.setParent(oldParent);
		return statement;
	}

	@Override
	public Node visit(com.digiarea.jse.ForeachStmt n, Context ctx)
			throws Exception {
		ForeachStatement img = new ForeachStatement();
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

	@Override
	public Node visit(com.digiarea.jse.ForStmt n, Context ctx) throws Exception {
		ForStatement img = new ForStatement();
		if (n.getInit() != null) {
			List<Expression> init = new ArrayList<Expression>();
			for (com.digiarea.jse.Expression item : n.getInit()) {
				if (item != null) {
					init.add((Expression) item.accept(this, ctx));
				}
			}
			img.setVariable(new SequenceExpression(init));
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
			img.setExpr(new SequenceExpression(update));
		}
		if (n.getBody() != null) {
			img.setBody((Statement) n.getBody().accept(this, ctx));
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.IfStmt n, Context ctx) throws Exception {
		IfStatement img = new IfStatement();
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

	@Override
	public Node visit(com.digiarea.jse.ImportDeclaration n, Context ctx)
			throws Exception {
		// nothing to do, resolving must be run first
		return null;
	}

	@Override
	public Node visit(com.digiarea.jse.InitializerDeclaration n, Context ctx)
			throws Exception {
		if (n.getBlock() != null) {
			return n.getBlock().accept(this, ctx);
		} else {
			return new Block();
		}
	}

	@Override
	public Node visit(com.digiarea.jse.InstanceOfExpr n, Context ctx)
			throws Exception {
		Expression img = null;

		if (types.contains(n.getType().toString())) {
			CallExpression func = new CallExpression();
			if (n.getType().toString().equals(JAVA_LANG_STRING)) {
				func.setScope((Expression) new NameExpr(GOOG_ISSTRING).accept(
						this, ctx));
			} else if (n.getType().toString().equals(JAVA_LANG_INTEGER)) {
				func.setScope((Expression) new NameExpr(GOOG_ISNUMBER).accept(
						this, ctx));
			}
			List<Expression> parameters = new ArrayList<Expression>();
			parameters.add((Expression) n.getExpression().accept(this, ctx));
			func.setArgs(parameters);
			img = func;
		} else {
			BinaryExpression bin = new BinaryExpression();
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

	@Override
	public Node visit(com.digiarea.jse.IntegerLiteralExpr n, Context ctx)
			throws Exception {
		return new DecimalLiteral(n.getValue());
	}

	@Override
	public Node visit(com.digiarea.jse.InterfaceDeclaration n, Context ctx)
			throws Exception {
		return makeTypeDeclaration(n, ctx);
	}

	@Override
	public Node visit(com.digiarea.jse.JavadocComment n, Context ctx)
			throws Exception {
		JSDocComment img = new JSDocComment();
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
							+ getJSDocModifiers(typeDeclaration.getModifiers());
				}
			} else if (parent instanceof MethodDeclaration) {
				MethodDeclaration method = (MethodDeclaration) parent;
				content = content + getJSDocModifiers(method.getModifiers());
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
				content = content + getJSDocModifiers(method.getModifiers());
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
				int arrayCount = rType.getArrayCount();
				if (arrayCount == 0) {
					return getJSDocType(rType.getType(), ctx);
				} else {
					ReferenceType newType = new ReferenceType(rType.getType(),
							arrayCount - 1);
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
					rowType.append(getJSDocType(
							NodeUtils.toClassOrInterfaceType(clazz.getName()),
							ctx));
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

	private String getJSDocModifiers(int modifier) {
		String content = EMPTY;
		if (ModifierSet.isPrivate(modifier)) {
			content = content + "\n * @private";
		} else if (ModifierSet.isProtected(modifier)) {
			content = content + "\n * @protected";
		}
		return content;
	}

	@Override
	public Node visit(com.digiarea.jse.LabeledStmt n, Context ctx)
			throws Exception {
		LabelledStatement img = new LabelledStatement();
		img.setLabel(n.getLabel());
		if (n.getStmt() != null) {
			img.setStatement((Statement) n.getStmt().accept(this, ctx));
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.LineComment n, Context ctx)
			throws Exception {
		LineComment img = new LineComment();
		img.setContent("//" + n.getContent());
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.LongLiteralExpr n, Context ctx)
			throws Exception {
		String expr = n.getValue().substring(0, n.getValue().length() - 1);
		return new HexIntegerLiteral(expr);
	}

	@Override
	public Node visit(com.digiarea.jse.MarkerAnnotationExpr n, Context ctx)
			throws Exception {
		return null;
	}

	@Override
	public Node visit(com.digiarea.jse.MemberValuePair n, Context ctx)
			throws Exception {
		return null;
	}

	@Override
	public Node visit(com.digiarea.jse.MethodCallExpr n, Context ctx)
			throws Exception {
		CallExpression img = new CallExpression();
		Expression scope = null;
		IdentifierName name = new IdentifierName(n.getName());
		if (n.getScope() != null) {
			if (n.getScope() instanceof SuperExpr) {
				scope = new IdentifierName(GOOG_BASE);
			} else {
				scope = (Expression) n.getScope().accept(this, ctx);
				scope = new FieldAccessExpression(scope, name);
			}
		} else {
			scope = name;
		}
		img.setScope(scope);
		if (n.getArgs() != null) {
			List<Expression> args = new ArrayList<Expression>();
			if (n.getScope() instanceof SuperExpr) {
				args.add(new ThisExpression());
				args.add((Expression) new StringLiteralExpr(n.getName())
						.accept(this, ctx));
			}
			for (com.digiarea.jse.Expression item : n.getArgs()) {
				if (item != null) {
					args.add((Expression) item.accept(this, ctx));
				}
			}
			img.setArgs(args);
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.MethodDeclaration n, Context ctx)
			throws Exception {
		FunctionExpression img = new FunctionExpression();
		if (n.getParameters() != null) {
			List<Parameter> parameters = new ArrayList<Parameter>();
			for (com.digiarea.jse.Parameter item : n.getParameters()) {
				if (item != null) {
					parameters.add((Parameter) item.accept(this, ctx));
				}
			}
			img.setParameters(parameters);
		}
		if (procesBodies && n.getBody() != null) {
			img.setBody((Block) n.getBody().accept(this, ctx));
		} else {
			img.setBody(new Block());
		}
		Statement statement = null;
		if (ctx.isAnonymousClass()) {
			statement = makeSimple(n.getName(), img);
		} else if (ModifierSet.isStatic(n.getModifiers())) {
			statement = makeStatic(getFullName(ctx), n.getName(), img);
		} else {
			statement = makePrototype(getFullName(ctx), n.getName(), img);
		}
		com.digiarea.jse.Node oldParent = ctx.getParent();
		ctx.setParent(n);
		if (n.getJavaDoc() != null) {
			statement.setComment((Comment) n.getJavaDoc().accept(this, ctx));
		} else {
			statement.setComment((Comment) visit(new JavadocComment(), ctx));
		}
		ctx.setParent(oldParent);
		return statement;
	}

	private String getFullName(Context ctx) {
		// return ctx.getCompilationUnit().getPackageDeclaration().getName()
		// .toString()
		// + "." + ctx.getTypeDeclaration().getName();
		return ctx.getEnclosure().toString();
	}

	@Override
	public Node visit(com.digiarea.jse.ModifierSet n, Context ctx)
			throws Exception {
		return null;
	}

	@Override
	public Node visit(com.digiarea.jse.NameExpr n, Context ctx)
			throws Exception {
		IdentifierName img = new IdentifierName();
		String name = n.getName();
		com.digiarea.jse.Node parent = ctx.getParent();
		if (parent instanceof QualifiedNameExpr) {
			name = fixNamespace(name);
		}
		img.setValue(name);
		return img;
	}

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

	@Override
	public Node visit(com.digiarea.jse.NormalAnnotationExpr n, Context ctx)
			throws Exception {
		return null;
	}

	@Override
	public Node visit(com.digiarea.jse.NullLiteralExpr n, Context ctx)
			throws Exception {
		return new NullLiteral();
	}

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

	@Override
	public Node visit(com.digiarea.jse.ObjectCreationExpr n, Context ctx)
			throws Exception {
		com.digiarea.jse.Node parent = ctx.getParent();
		if (canAddRequire(n.getType(), ctx)) {
			addRequire(n.getType().getName().toString());
		}
		Expression scope = null;
		if (parent != null && (parent instanceof ThrowStmt)) {
			scope = new IdentifierName(ERROR);
		} else {
			IdentifierName name = new IdentifierName(removeGenericTypes(
					n.getType()).accept(this, ctx).toString());
			if (n.getScope() != null) {
				scope = new FieldAccessExpression((Expression) n.getScope()
						.accept(this, ctx), name);
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
		CallExpression img = new CallExpression(scope, args);
		ObjectLiteral initializer = null;
		if (n.getAnonymousClassBody() != null) {
			boolean anonymousClass = ctx.isAnonymousClass();
			ctx.setAnonymousClass(true);
			List<PropertyAssignment> properties = new ArrayList<>();
			initializer = new ObjectLiteral(properties);
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
						List<Statement> stmts = ((Block) result).getStmts();
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
		return new NewExpression(img, initializer);
	}

	private PutAssignment makePutAssignment(Statement statement, Context ctx)
			throws Exception {
		if (statement instanceof ExpressionStatement) {
			Expression expression = ((ExpressionStatement) statement)
					.getExpression();
			if (expression instanceof AssignmentExpression) {
				AssignmentExpression expr = (AssignmentExpression) expression;
				return new PutAssignment((IdentifierName) expr.getTarget(),
						expr.getValue());
			}
		} else {
			System.err.println("ERR: " + ctx.getCompilationUnit().getName());
			System.err.println("STM: " + statement.getClass());
			System.err.println("Wrong Statement for PutAssignment: "
					+ statement);
		}
		return null;
	}

	private ClassOrInterfaceType removeGenericTypes(ClassOrInterfaceType type)
			throws Exception {
		ClassOrInterfaceType newType = (ClassOrInterfaceType) type.clone();
		newType.setTypeArgs(null);
		return newType;
	}

	@Override
	public Node visit(com.digiarea.jse.PackageDeclaration n, Context ctx)
			throws Exception {
		// nothing to do
		return null;
	}

	@Override
	public Node visit(com.digiarea.jse.Parameter n, Context ctx)
			throws Exception {
		Parameter img = new Parameter();
		if (n.getId() != null) {
			img.setName(n.getId().getName());
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.PrimitiveType n, Context ctx)
			throws Exception {
		if (n.getType() == com.digiarea.jse.PrimitiveType.Primitive.Boolean) {
			return new IdentifierName("boolean");
		} else {
			return new IdentifierName(NUMBER);
		}
	}

	@Override
	public Node visit(com.digiarea.jse.PrimitiveType.Primitive n, Context ctx)
			throws Exception {
		return null;
	}

	@Override
	public Node visit(com.digiarea.jse.Project n, Context ctx) throws Exception {
		Project img = new Project();
		if (n.getCompilationUnits() != null) {
			List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();
			for (com.digiarea.jse.CompilationUnit item : n
					.getCompilationUnits()) {
				if (item != null) {
					compilationUnits.add((CompilationUnit) item.accept(this,
							ctx));
				}
			}
			img.setCompilationUnits(compilationUnits);
		}
		return img;
	}

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
		FieldAccessExpression img = new FieldAccessExpression();
		if (qualifier != null) {
			img.setScope((Expression) qualifier.accept(this, ctx));
		}
		img.setField(new IdentifierName(n.getName()));
		ctx.setParent(parent);
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.ReferenceType n, Context ctx)
			throws Exception {
		if (n.getType() != null) {
			return n.getType().accept(this, ctx);
		}
		return null;
	}

	@Override
	public Node visit(com.digiarea.jse.ReturnStmt n, Context ctx)
			throws Exception {
		ReturnStatement img = new ReturnStatement();
		if (n.getExpression() != null) {
			img.setExpression((Expression) n.getExpression().accept(this, ctx));
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.SingleMemberAnnotationExpr n, Context ctx)
			throws Exception {
		return null;
	}

	@Override
	public Node visit(com.digiarea.jse.StringLiteralExpr n, Context ctx)
			throws Exception {
		return new StringLiteralDouble(n.getValue());
	}

	@Override
	public Node visit(com.digiarea.jse.SuperExpr n, Context ctx)
			throws Exception {
		return new ThisExpression();
	}

	@Override
	public Node visit(com.digiarea.jse.SwitchEntryStmt n, Context ctx)
			throws Exception {
		CaseClause img = new CaseClause();
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
			img.setStatements(stmts);
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.SwitchStmt n, Context ctx)
			throws Exception {
		SwitchStatement img = new SwitchStatement();
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
			img.setBlock(new CaseBlock(new DefaultClause(es5Statements),
					entries));
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.SynchronizedStmt n, Context ctx)
			throws Exception {
		WithStatement img = new WithStatement();
		if (n.getExpression() != null) {
			img.setExpression((Expression) n.getExpression().accept(this, ctx));
		}
		if (n.getBlock() != null) {
			img.setStatement((Statement) n.getBlock().accept(this, ctx));
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.ThisExpr n, Context ctx)
			throws Exception {
		ThisExpression img = new ThisExpression();
		// TODO check this out (Expression) n.getClassExpr().accept(this, ctx);
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.ThrowStmt n, Context ctx)
			throws Exception {
		com.digiarea.jse.Node oldParent = ctx.getParent();
		ctx.setParent(n);
		ThrowStatement img = new ThrowStatement();
		if (n.getExpression() != null) {
			Expression expr = (Expression) n.getExpression().accept(this, ctx);
			img.setExpression(expr);
		}
		ctx.setParent(oldParent);
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.TryStmt n, Context ctx) throws Exception {
		TryStatement img = new TryStatement();
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

	@Override
	public Node visit(com.digiarea.jse.TypeDeclarationStmt n, Context ctx)
			throws Exception {
		return n.getTypeDeclaration().accept(this, ctx);
	}

	@Override
	public Node visit(com.digiarea.jse.TypeParameter n, Context ctx)
			throws Exception {
		return null;
	}

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

	@Override
	public Node visit(com.digiarea.jse.UnaryExpr.UnaryOperator n, Context ctx)
			throws Exception {
		UnaryExpression img = new UnaryExpression();
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

	@Override
	public Node visit(com.digiarea.jse.VariableDeclarationExpr n, Context ctx)
			throws Exception {
		VariableExpression img = new VariableExpression();
		if (n.getVars() != null) {
			List<VariableDeclaration> vars = new ArrayList<VariableDeclaration>();
			com.digiarea.jse.Node oldParent = ctx.getParent();
			ctx.setParent(n);
			for (VariableDeclarator item : n.getVars()) {
				if (item != null) {
					vars.add((VariableDeclaration) item.accept(this, ctx));
				}
			}
			img.setVariableDeclarations(vars);
			ctx.setParent(oldParent);
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.VariableDeclarator n, Context ctx)
			throws Exception {
		com.digiarea.jse.Expression init = n.getInit();
		VariableDeclaratorId id = n.getId();
		return makeVariable(ctx, id.getName(), init);
	}

	@Override
	public Node visit(com.digiarea.jse.VariableDeclaratorId n, Context ctx)
			throws Exception {
		return null;
	}

	@Override
	public Node visit(com.digiarea.jse.VoidType n, Context ctx)
			throws Exception {
		// FIXME
		return new IdentifierName(n.toString());
	}

	@Override
	public Node visit(com.digiarea.jse.WhileStmt n, Context ctx)
			throws Exception {
		WhileStatement img = new WhileStatement();
		if (n.getCondition() != null) {
			img.setCondition((Expression) n.getCondition().accept(this, ctx));
		}
		if (n.getBody() != null) {
			img.setBody((Statement) n.getBody().accept(this, ctx));
		}
		return img;
	}

	@Override
	public Node visit(com.digiarea.jse.WildcardType n, Context ctx)
			throws Exception {
		// FIXME
		return new IdentifierName(n.toString());
	}

	@Override
	public Node visit(ArraySlot n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visit(Ellipsis n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visit(LambdaBlock n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visit(LambdaExpr n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visit(MethodExprRef n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visit(MethodTypeRef n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visit(MethodRef n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visit(Modifiers n, Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends com.digiarea.jse.Node> Node visit(NodeList<E> n,
			Context ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
