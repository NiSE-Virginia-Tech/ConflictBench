/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.query.criteria.internal.expression; 

import java.io.Serializable; 

import org.hibernate.internal.util.StringHelper; 
import org.hibernate.query.criteria.LiteralHandlingMode; 
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl; 
import org.hibernate.query.criteria.internal.ParameterRegistry; 
import org.hibernate.query.criteria.internal.ValueHandlerFactory; 
import org.hibernate.query.criteria.internal.ValueHandlerFactory.ValueHandler; 
import org.hibernate.query.criteria.internal.compile.RenderingContext; 

/**
 * Represents a literal expression.
 *
 * @author Steve Ebersole
 */
  class  LiteralExpression <T>  extends ExpressionImpl<T>   {
	

	

	

	

	

	

	

	

	

	

	

	

	

	

	

	

	

	<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647923827333/fstmerge_var1_2738756831422405954
=======
private String renderProjection(RenderingContext renderingContext) {
		if ( ValueHandlerFactory.isCharacter( literal ) ) {
			// In case literal is a Character, pass literal.toString() as the argument.
			return renderingContext.getDialect().inlineLiteral( literal.toString() );
		}

		// some drivers/servers do not like parameters in the select clause
		final ValueHandlerFactory.ValueHandler handler =
				ValueHandlerFactory.determineAppropriateHandler( literal.getClass() );

		if ( handler == null ) {
			return normalRender( renderingContext, LiteralHandlingMode.BIND );
		}
		else {
			return handler.render( literal );
		}
	}
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647923827333/fstmerge_var2_1093417347275772686


	

	

	

	


}
