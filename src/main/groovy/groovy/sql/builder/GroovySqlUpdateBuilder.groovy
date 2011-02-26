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
import groovy.sql.builder.criteria.Criteria
import groovy.sql.builder.criteria.Row
import groovy.sql.builder.criteria.util.CriteriaUtil
import groovy.sql.builder.result.Statement
import groovy.sql.builder.criteria.factory.*
import groovy.sql.builder.result.ResultAware
import groovy.sql.builder.criteria.ParametizedCriteria
import groovy.sql.builder.criteria.LogicOperator

/**
 *
 *
 * @author Benjamin Muschko
 */
class GroovySqlUpdateBuilder extends AbstractGroovySqlFactoryBuilder {
    GroovySqlUpdateBuilder(Sql sql) {
        super(sql)
    }

    @Override
    List<NamedAbstractFactory> getNamedFactories() {
        [new UpdateFactory(), new RowFactory(), new EqualsCriteriaFactory(), new NotEqualsCriteriaFactory(), new LikeCriteriaFactory(),
         new IsNullCriteriaFactory(), new IsNotNullCriteriaFactory(), new GreaterThanCriteriaFactory(), new GreaterThanEqualsCriteriaFactory(),
         new LessThanCriteriaFactory(), new LessThanEqualsCriteriaFactory(), new BetweenCriteriaFactory(), new InCriteriaFactory(),
         new AndLogicOperationFactory(), new OrLogicOperationFactory(), new NotLogicOperatorFactory()].asImmutable()
    }

    private class UpdateFactory extends NamedAbstractFactory {
        final String TABLE_ATTRIBUTE = 'table'

        @Override
        String getName() {
            'update'
        }

        @Override
        Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
            Update update = new Update()
            update.table = (attributes && attributes.containsKey(TABLE_ATTRIBUTE)) ? attributes[TABLE_ATTRIBUTE] : value
            update
        }

        @Override
        void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
            def statement = createStatement(node)
            node.statement = statement
            int rowsUpdated = builder.sql.executeUpdate(statement.sql, statement.params)
            node.result = rowsUpdated
        }

        private String createSql(String table, Row row, criterias) {
            def expression = new StringBuilder()

            row.columnNames.eachWithIndex { columnName, index ->
                expression <<= "$columnName = ?"

                if(index < row.columnNames.size() - 1) {
                    expression <<= ", "
                }
            }

            if(criterias.size() > 0) {
                criterias.eachWithIndex { criteria, index ->
                    if(index == 0) {
                        expression <<= " WHERE "
                    }
                    else {
                        expression <<= " AND "
                    }

                    if(criteria instanceof Criteria) {
                        expression <<= criteria.renderExpression()
                    }
                }
            }

            "UPDATE ${table} SET ${expression}"
        }

        private Statement createStatement(Object node) {
            String sql = createSql(node.table, node.row, node.criterias)
            List<Object> params = CriteriaUtil.getCriteraValues(node.row.values)
            collectCriteriaParams(params, node.criterias)
            new Statement(sql: sql, params: params)
        }

        private List<Object> collectCriteriaParams(List<Object> params, List<Criteria> criterias) {
            criterias.each { criteria ->
                if(criteria instanceof ParametizedCriteria) {
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

    private class Update implements ResultAware {
        String table
        Row row
        def criterias = []
        Statement statement
        int result

        @Override
        def getResult() {
            result
        }
    }
}
