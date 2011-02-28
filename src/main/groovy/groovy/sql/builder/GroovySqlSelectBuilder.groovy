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
package groovy.sql.builder

import groovy.sql.Sql
import groovy.sql.builder.node.Criteria
import groovy.sql.builder.node.LogicOperator
import groovy.sql.builder.node.ParameterizedCriteria
import groovy.sql.builder.node.util.CriteriaUtil
import groovy.sql.builder.result.ResultAware
import groovy.sql.builder.result.Statement
import groovy.sql.builder.node.factory.*

/**
 *
 *
 * @author Benjamin Muschko
 */
class GroovySqlSelectBuilder extends AbstractGroovySqlFactoryBuilder {
        GroovySqlSelectBuilder(Sql sql) {
        super(sql)
    }

    @Override
    List<NamedAbstractFactory> getNamedFactories() {
        [new SelectFactory(), new EqualsCriteriaFactory(), new NotEqualsCriteriaFactory(), new LikeCriteriaFactory(),
         new IsNullCriteriaFactory(), new IsNotNullCriteriaFactory(), new GreaterThanCriteriaFactory(), new GreaterThanEqualsCriteriaFactory(),
         new LessThanCriteriaFactory(), new LessThanEqualsCriteriaFactory(), new BetweenCriteriaFactory(), new InCriteriaFactory(),
         new AndLogicOperationFactory(), new OrLogicOperationFactory(), new NotLogicOperatorFactory()].asImmutable()
    }

    private class SelectFactory extends NamedAbstractFactory {
        final String TABLE_ATTRIBUTE = 'table'

        @Override
        String getName() {
            'select'
        }

        @Override
        Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
            Select select = new Select()
            select.table = (attributes && attributes.containsKey(TABLE_ATTRIBUTE)) ? attributes[TABLE_ATTRIBUTE] : value
            select
        }

        @Override
        void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
            def statement = createStatement(node)
            node.statement = statement
            def rows = builder.sql.rows(statement.sql, statement.params)
            node.result = rows
        }

        private String createSql(String table, criterias) {
            def expression = new StringBuilder()

            if(criterias.size() > 0) {
                criterias.eachWithIndex { criteria, index ->
                    if(index == 0) {
                        expression <<= "WHERE "
                    }
                    else {
                        expression <<= " AND "
                    }

                    if(criteria instanceof Criteria) {
                        expression <<= criteria.renderExpression()
                    }
                }
            }

            "SELECT * FROM ${table} ${expression}"
        }

        private Statement createStatement(Object node) {
            String sql = createSql(node.table, node.criterias)
            def params = []
            collectCriteriaParams(params, node.criterias)
            new Statement(sql: sql, params: params)
        }

        private List<Object> collectCriteriaParams(List<Object> params, List<Criteria> criterias) {
            criterias.each { criteria ->
                if(criteria instanceof ParameterizedCriteria) {
                    params.addAll criteria.getParams()
                }
                else if(criteria instanceof LogicOperator) {
                    collectCriteriaParams(params, criteria.criterias)
                }
            }
        }

        @Override
        public boolean isLeaf() {
            false
        }
    }

    private class Select implements ResultAware {
        String table
        def criterias = []
        Statement statement
        def result

        @Override
        def getResult() {
            result
        }
    }
}
