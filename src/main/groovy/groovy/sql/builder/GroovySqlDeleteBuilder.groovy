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
import groovy.sql.builder.result.Statement
import groovy.sql.builder.node.factory.*
import groovy.sql.builder.node.ParameterizedCriteria
import groovy.sql.builder.node.LogicOperator

/**
 *
 *
 * @author Benjamin Muschko
 */
class GroovySqlDeleteBuilder extends AbstractGroovySqlFactoryBuilder {
    GroovySqlDeleteBuilder(Sql sql) {
        super(sql)
    }

    @Override
    List<NamedAbstractFactory> getNamedFactories() {
        [new DeleteFactory(), new EqualsCriteriaFactory(), new NotEqualsCriteriaFactory(), new LikeCriteriaFactory(),
         new IsNullCriteriaFactory(), new IsNotNullCriteriaFactory(), new GreaterThanCriteriaFactory(), new GreaterThanEqualsCriteriaFactory(),
         new LessThanCriteriaFactory(), new LessThanEqualsCriteriaFactory(), new BetweenCriteriaFactory(), new InCriteriaFactory(),
         new AndLogicOperationFactory(), new OrLogicOperationFactory(), new NotLogicOperatorFactory()].asImmutable()
    }

    private class DeleteFactory extends GroovySqlAbstractFactory {
        final String TABLE_ATTRIBUTE = 'table'

        @Override
        String getName() {
            'delete'
        }

        @Override
        Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
            Delete delete = new Delete()
            delete.table = (attributes && attributes.containsKey(TABLE_ATTRIBUTE)) ? attributes[TABLE_ATTRIBUTE] : value
            delete
        }

        @Override
        void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
            def statement = createStatement(node)
            node.statement = statement
            builder.sql.execute(statement.sql, statement.params)
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

            "DELETE FROM ${table} ${expression}"
        }

        private Statement createStatement(Object node) {
            String sql = createSql(node.table, node.criterias)
            def params = []
            collectCriteriaParams(params, node.criterias)
            new Statement(sql: sql, params: params)
        }

        @Override
        public boolean isLeaf() {
            false
        }
    }

    private class Delete {
        String table
        def criterias = []
        Statement statement
    }
}