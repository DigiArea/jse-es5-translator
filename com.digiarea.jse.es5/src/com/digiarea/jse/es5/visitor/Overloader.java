package com.digiarea.jse.es5.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.digiarea.common.Arrow;
import com.digiarea.jse.AnnotationExpr;
import com.digiarea.jse.ArrayAccessExpr;
import com.digiarea.jse.BinaryExpr;
import com.digiarea.jse.BlockStmt;
import com.digiarea.jse.BodyDeclaration;
import com.digiarea.jse.CastExpr;
import com.digiarea.jse.ClassDeclaration;
import com.digiarea.jse.ConstructorDeclaration;
import com.digiarea.jse.Ellipsis;
import com.digiarea.jse.EnumDeclaration;
import com.digiarea.jse.Expression;
import com.digiarea.jse.ExpressionStmt;
import com.digiarea.jse.FieldAccessExpr;
import com.digiarea.jse.IfStmt;
import com.digiarea.jse.InstanceOfExpr;
import com.digiarea.jse.IntegerLiteralExpr;
import com.digiarea.jse.InterfaceDeclaration;
import com.digiarea.jse.JavadocComment;
import com.digiarea.jse.MethodDeclaration;
import com.digiarea.jse.NameExpr;
import com.digiarea.jse.NullLiteralExpr;
import com.digiarea.jse.Parameter;
import com.digiarea.jse.PrimitiveType;
import com.digiarea.jse.Project;
import com.digiarea.jse.ReferenceType;
import com.digiarea.jse.ReturnStmt;
import com.digiarea.jse.Statement;
import com.digiarea.jse.Type;
import com.digiarea.jse.TypeDeclaration;
import com.digiarea.jse.TypeParameter;
import com.digiarea.jse.VariableDeclarationExpr;
import com.digiarea.jse.VariableDeclaratorId;
import com.digiarea.jse.VoidType;
import com.digiarea.jse.utils.LangUtils;
import com.digiarea.jse.visitor.VoidVisitorAdapter;

public class Overloader implements Arrow<Project, Project> {

	private static class Data {

		private static final String JAVA_LANG_OBJECT = "java.lang.Object";

		private TypeDeclaration typeDeclaration;
		private Map<String, Set<MethodDeclaration>> methods = new HashMap<String, Set<MethodDeclaration>>();
		private Map<String, Set<ConstructorDeclaration>> constructors = new HashMap<String, Set<ConstructorDeclaration>>();

		public Data(TypeDeclaration typeDeclaration) {
			super();
			this.typeDeclaration = typeDeclaration;
		}

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
			typeDeclaration.setMembers(declarations);
		}

		private static String MAGIC_ARGUMENTS = "arguments";

		private ConstructorDeclaration makeConstructor(String key,
				Set<ConstructorDeclaration> value) {
			// FIXME - fix super(...) and this(...) statements
			String paramName = makeParamName(value);
			List<AnnotationExpr> annotations = new ArrayList<AnnotationExpr>();
			JavadocComment javaDoc = new JavadocComment("");
			int modifiers = 0;
			List<TypeParameter> typeParameters = null;
			List<Parameter> parameters = makeParameters(paramName);
			List<NameExpr> throwsList = new ArrayList<NameExpr>();
			BlockStmt body = null;
			IfStmt topIfStmt = null;
			IfStmt tmpIfStmt = null;
			for (ConstructorDeclaration item : value) {
				modifiers = makeModifiers(modifiers, item.getModifiers());
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
				if (ModifierSet.isAbstract(modifiers)) {
					modifiers = ModifierSet.removeModifier(modifiers,
							ModifierSet.ABSTRACT);
				}
				body = new BlockStmt(Arrays.asList((Statement) topIfStmt));
			}
			ConstructorDeclaration result = new ConstructorDeclaration();
			result.setName(key);
			result.setAnnotations(annotations);
			result.setJavaDoc(javaDoc);
			result.setModifiers(modifiers);
			result.setTypeParameters(typeParameters);
			// result.setParameters(parameters);
			result.setThrowsList(throwsList);
			result.setBlock(body);
			return result;
		}

		private MethodDeclaration makeMethod(String key,
				Set<MethodDeclaration> value) {
			String paramName = makeParamName(key, value);
			List<AnnotationExpr> annotations = new ArrayList<AnnotationExpr>();
			JavadocComment javaDoc = new JavadocComment("");
			int modifiers = 0;
			List<TypeParameter> typeParameters = null;
			Type type = NodeUtils.VOID_TYPE;
			List<Parameter> parameters = makeParameters(paramName);
			List<NameExpr> throwsList = new ArrayList<NameExpr>();
			BlockStmt body = null;
			IfStmt topIfStmt = null;
			IfStmt tmpIfStmt = null;
			for (MethodDeclaration item : value) {
				modifiers = makeModifiers(modifiers, item.getModifiers());
				typeParameters = makeTypeParameters(typeParameters,
						item.getTypeParameters());
				type = makeType(type, item.getType());
				BlockStmt stuff = item.getBody();
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
				if (ModifierSet.isAbstract(modifiers)) {
					modifiers = ModifierSet.removeModifier(modifiers,
							ModifierSet.ABSTRACT);
				}
				statements.add(topIfStmt);
				if (type == null) {
					statements.add(new ReturnStmt(new NullLiteralExpr()));
				}
				body = new BlockStmt(statements);
			}
			if (type == null) {
				type = NodeUtils.toClassOrInterfaceType(JAVA_LANG_OBJECT);

			}
			MethodDeclaration result = new MethodDeclaration();
			result.setName(key);
			result.setAnnotations(annotations);
			result.setJavaDoc(javaDoc);
			result.setModifiers(modifiers);
			result.setTypeParameters(typeParameters);
			result.setType(type);
			// result.setParameters(parameters);
			result.setThrowsList(throwsList);
			result.setBody(body);
			return result;
		}

		private void makeJavaDoc(JavadocComment javaDoc, JavadocComment javaDok) {
			if (javaDok != null) {
				javaDoc.setContent(javaDoc.getContent() + javaDok.getContent());
			}
		}

		private void makeThrows(List<NameExpr> throwsList, List<NameExpr> throwz) {
			if (throwz != null) {
				for (NameExpr nameExpr : throwz) {
					if (!throwsList.contains(nameExpr)) {
						throwsList.add(nameExpr);
					}
				}
			}
		}

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

		private IfStmt makeIf(String paramName, List<Parameter> parameters,
				BlockStmt body, IfStmt ifStmt) {
			IfStmt ifStatement = new IfStmt();
			ifStatement.setCondition(makeCondition(paramName, parameters));
			List<Statement> statements = makeLocalDeclarations(paramName,
					parameters);
			if (body.getStatements() != null) {
				statements.addAll(body.getStatements());
			}
			ifStatement.setThenStmt(new BlockStmt(statements));
			if (ifStmt != null) {
				ifStmt.setElseStmt(ifStatement);
			}
			return ifStatement;
		}

		private List<Statement> makeLocalDeclarations(String paramName,
				List<Parameter> parameters) {
			List<Statement> statements = new ArrayList<Statement>();
			if (parameters != null && parameters.size() > 0) {
				int index = 0;
				for (Parameter parameter : parameters) {
					Expression init = new CastExpr(parameter.getType(),
							new ArrayAccessExpr(new NameExpr(paramName),
									new IntegerLiteralExpr(index)));
					VariableDeclarationExpr expr = NodeUtils
							.createVariableDeclarationExpr(parameter.getType(),
									parameter.getId().getName(), init);
					if (ModifierSet.isFinal(parameter.getModifiers())) {
						expr.setModifiers(ModifierSet.FINAL);
					}
					statements.add(new ExpressionStmt(expr));
					index++;
				}
			}
			return statements;
		}

		private Expression makeCondition(String paramName,
				List<Parameter> parameters) {
			int size = 0;
			if (parameters != null) {
				size = parameters.size();
			}
			Expression result = new BinaryExpr(new FieldAccessExpr(
					new NameExpr(paramName), "length"), new IntegerLiteralExpr(
					size), BinaryOperator.equals);
			if (size > 0) {
				int index = 0;
				for (Parameter parameter : parameters) {
					Type type = fixType(parameter.getType());
					result = new BinaryExpr(result, new InstanceOfExpr(
							new ArrayAccessExpr(new NameExpr(paramName),
									new IntegerLiteralExpr(index)), type),
							BinaryOperator.and);
					index++;
				}
			}
			return result;
		}

		private Type fixType(Type type) {
			if (type instanceof ReferenceType) {
				ReferenceType rType = (ReferenceType) type;
				return new ReferenceType(fixType(rType.getType()),
						rType.getArrayCount());
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

		private List<Parameter> makeParameters(String paramName) {
			List<Parameter> parameters = new ArrayList<Parameter>();
			Parameter parameter = new Parameter();
			parameter.setId(new VariableDeclaratorId(paramName));
			parameter.setType(NodeUtils
					.toClassOrInterfaceType(JAVA_LANG_OBJECT));
			parameter.setEllipsis(new Ellipsis());
			parameters.add(parameter);
			return parameters;
		}

		private Type makeType(Type type, Type type2) {
			// TODO type
			if (!(type2 instanceof VoidType)) {
				return null;
			}
			return type;
		}

		private List<TypeParameter> makeTypeParameters(
				List<TypeParameter> typeParameters,
				List<TypeParameter> typeParameters2) {
			// TODO typeParameters
			return null;
		}

		private int makeModifiers(int modifiers, int itemModifiers) {
			// TODO modifiers
			return ModifierSet.addModifier(modifiers, itemModifiers);
		}

		private String makeParamName(Set<ConstructorDeclaration> value) {
			return MAGIC_ARGUMENTS;
		}

		private String makeParamName(String key, Set<MethodDeclaration> value) {
			return MAGIC_ARGUMENTS;
		}
	}

	private static class Context {
		private Data data;

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public void process() {
			data.process();
		}

	}

	private static class Visitor extends VoidVisitorAdapter<Context> {

		@Override
		public void visit(ClassDeclaration n, Context ctx) throws Exception {
			Data oldData = ctx.getData();
			ctx.setData(new Data(n));
			super.visit(n, ctx);
			ctx.process();
			ctx.setData(oldData);
		}

		@Override
		public void visit(ConstructorDeclaration n, Context ctx)
				throws Exception {
			ctx.getData().addConstructor(n);
			super.visit(n, ctx);
		}

		@Override
		public void visit(EnumDeclaration n, Context ctx) throws Exception {
			Data oldData = ctx.getData();
			ctx.setData(new Data(n));
			super.visit(n, ctx);
			ctx.process();
			ctx.setData(oldData);
		}

		@Override
		public void visit(InterfaceDeclaration n, Context ctx) throws Exception {
			Data oldData = ctx.getData();
			ctx.setData(new Data(n));
			super.visit(n, ctx);
			ctx.process();
			ctx.setData(oldData);
		}

		@Override
		public void visit(MethodDeclaration n, Context ctx) throws Exception {
			ctx.getData().addMethod(n);
			super.visit(n, ctx);
		}

	}

	@Override
	public Project arrow(Project input) throws Exception {
		new Visitor().visit(input, new Context());
		return input;
	}

}
