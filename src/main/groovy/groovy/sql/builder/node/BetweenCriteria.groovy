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
package groovy.sql.builder.node

import groovy.sql.builder.node.util.CriteriaUtil

/**
 *
 *
 * @author Benjamin Muschko
 */
class BetweenCriteria implements ParameterizedCriteria {
    String name
    Object start
    Object end

  BetweenCriteria(name, start, end) {
        this.name = name
        this.start = start
        this.end = end
    }

    @Override
    def renderExpression() {
        "${name} BETWEEN ? AND ?"
    }

    @Override
    def getParams() {
        [CriteriaUtil.getCriteriaValue(start), CriteriaUtil.getCriteriaValue(end)]
    }
}
