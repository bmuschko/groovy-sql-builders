/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package groovy.sql.builder.node.factory

import groovy.sql.builder.node.Criteria
import groovy.sql.builder.GroovySqlSelectBuilder.Select

/**
 *
 *
 * @author Benjamin Muschko
 */
abstract class CriteriaAbstractFactory extends NamedAbstractFactory {
    @Override
    void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        if(child instanceof Criteria) {
            if(parent instanceof Select) {
                parent.clauseElements.where << child
            }
            else {
                parent.criterias << child
            }
        }
    }
}
