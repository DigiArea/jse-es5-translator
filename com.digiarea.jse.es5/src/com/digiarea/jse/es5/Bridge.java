/*
 * 
 */
package com.digiarea.jse.es5;

import java.util.HashMap;

import com.digiarea.common.Arrow;
import com.digiarea.jse.Project;
import com.digiarea.jse.arrow.Abstractor;
import com.digiarea.jse.arrow.Cleaner;
import com.digiarea.jse.arrow.Enumer;
import com.digiarea.jse.arrow.Quiver;
import com.digiarea.jse.arrow.Renamer;
import com.digiarea.jse.arrow.Resolver;
import com.digiarea.jse.arrow.Resolver.NamesTo;
import com.digiarea.jse.builder.SimpleModelHierarchy;
import com.digiarea.jse.es5.visitor.Overloader;
import com.digiarea.jse.es5.visitor.Visitor;

public class Bridge implements Arrow<Project, com.digiarea.es5.Project> {

	private Quiver quiver = new Quiver();
	private boolean procesBodies = true;

	public Bridge(boolean procesBodies) {
		super();
		this.procesBodies = procesBodies;
		// FIXME fix literals: long (L), float (F), double (D)
		// quiver.addArrow(new Literally());
		// fix method overloading
		quiver.addArrow(new Overloader());
		// convert enumerations into regular classes
		quiver.addArrow(new Enumer());
		// resolve
		quiver.addArrow(new Resolver(NamesTo.QUALIFIED));
		// convert abstract classes into regular ones
		quiver.addArrow(new Abstractor());
		// clean up imports
		quiver.addArrow(new Cleaner());
		// resolve
		// quiver.addArrow(new Resolver(NamesTo.QUALIFIED));

		HashMap<String, String> classes = new HashMap<String, String>();
		classes.put("java.lang.StringBuffer", "digiarea.java.lang.StringBuffer");
		classes.put("java.lang.Error", "goog.debug.Error");
		classes.put("java.lang.Thread", "goog.debug.Error");
		classes.put("java.util.HashMap", "digiarea.java.util.HashMap");
		classes.put("java.util.ArrayList", "digiarea.java.util.Set");
		classes.put("java.util.List", "digiarea.java.util.Set");
		classes.put("java.io.StringReader", "digiarea.java.io.StringReader");
		classes.put("java.lang.System", "digiarea.java.lang.System");
		classes.put("java.lang.Character", "digiarea.java.lang.Character");
		classes.put("java.math.BigInteger", "digiarea.java.math.BigInteger");
		classes.put("java.lang.Float", "digiarea.java.math.Numbers");
		classes.put("java.lang.Double", "digiarea.java.math.Numbers");
		// rename
		quiver.addArrow(new Renamer(classes));
	}

	public Bridge() {
		this(true);

	}

	@Override
	public com.digiarea.es5.Project arrow(Project input) throws Exception {
		Project p = quiver.arrow(input);
		Context ctx = new Context();
		Visitor bridger = new Visitor(new SimpleModelHierarchy(p), procesBodies);
		return (com.digiarea.es5.Project) bridger.visit(p, ctx);
	}

}
