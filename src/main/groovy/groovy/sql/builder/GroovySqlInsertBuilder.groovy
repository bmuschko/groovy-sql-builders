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
import groovy.sql.builder.criteria.Row
import groovy.sql.builder.criteria.factory.MultipleRowsFactory
import groovy.sql.builder.criteria.factory.NamedAbstractFactory
import groovy.sql.builder.criteria.util.CriteriaUtil
import groovy.sql.builder.result.Statement
import groovy.sql.builder.result.ResultAware

/**
 *
 *
 * @author Benjamin Muschko
 */
class GroovySqlInsertBuilder extends AbstractGroovySqlFactoryBuilder {
    GroovySqlInsertBuilder(Sql sql) {
        super(sql)
    }

    @Override
    List<NamedAbstractFactory> getNamedFactories() {
        [new InsertFactory(), new MultipleRowsFactory()].asImmutable()
    }

    private class InsertFactory extends NamedAbstractFactory {
        final String TABLE_ATTRIBUTE = 'table'

        @Override
        String getName() {
            'insert'
        }

        @Override
        Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
            Insert insert = new Insert()
            insert.table = (attributes && attributes.containsKey(TABLE_ATTRIBUTE)) ? attributes[TABLE_ATTRIBUTE] : value
            insert
        }

        @Override
        void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
            def insertedIds = []

            node.rows.each { row ->
                def statement = createStatement(node, row)
                node.statements << statement
                def ids = builder.sql.executeInsert(statement.sql, statement.params)
                insertedIds.addAll ids
            }

            node.result = insertedIds
        }

        private String createSql(String table, Set<String> columnNames) {
            def questionMarks = getQuestionMarks(columnNames)
            "INSERT INTO ${table} (${columnNames.join(', ')}) VALUES (${questionMarks.join(', ')})"
        }

        private getQuestionMarks(Set<String> columnNames) {
            def questionMarks = []

            columnNames.size().times {
                questionMarks << '?'
            }

            questionMarks
        }

        private Statement createStatement(Object node, Row row) {
            String sql = createSql(node.table, row.columnNames)
            List<Object> params = CriteriaUtil.getCriteraValues(row.values)
            new Statement(sql: sql, params: params)
        }

        @Override
        public boolean isLeaf() {
            false
        }
    }

    private class Insert implements ResultAware {
        String table
        List<Row> rows = []
        List<Statement> statements = []
        List<List<Object>> result = []

        @Override
        def getResult() {
            result
        }
    }
}
