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

import org.junit.Before
import org.junit.Test

/**
 *
 *
 * @author Benjamin Muschko
 */
class GroovySqlUpdateBuilderTest extends GroovySqlBuilderFixture {
    @Before
    @Override
    public void setUp() {
        super.setUp()
        sql.executeInsert('INSERT INTO city (name, state, founded_year) VALUES (?, ?, ?)', ['Las Vegas', 'Nevada', 1911])
        def firstRow = sql.firstRow("SELECT * from city WHERE id = ?", [1])
        assert firstRow.name == "Las Vegas"
        assert firstRow.state == "Nevada"
        assert firstRow.founded_year == 1911
    }

    @Test
    public void testBuildingWithoutCriteria() {
        def builder = new GroovySqlUpdateBuilder(sql)
        def update = builder.update(TABLE_NAME) {
            row(name: 'New Vegas', founded_year: 2011)
        }

        assert update.statement.sql == "UPDATE city SET name = ?, founded_year = ?"
        assert update.statement.params.size() == 2
        assert update.statement.params.get(0) == "New Vegas"
        assert update.statement.params.get(1) == 2011
        assertRowAfterUpdate()
        println "Updated rows: $update.result"
    }

    @Test
    public void testBuildingWithEqualsCriteria() {
        def builder = new GroovySqlUpdateBuilder(sql)
        def update = builder.update(TABLE_NAME) {
            row(name: 'New Vegas', founded_year: 2011)
            eq(name: 'name', value: 'Las Vegas')
        }

        assert update.statement.sql == "UPDATE city SET name = ?, founded_year = ? WHERE name = ?"
         assert update.statement.params.size() == 3
        assert update.statement.params.get(0) == "New Vegas"
        assert update.statement.params.get(1) == 2011
        assert update.statement.params.get(2) == "Las Vegas"
        assertRowAfterUpdate()
        println "Updated rows: $update.result"
    }

    @Test
    public void testBuildingWithNotCriteria() {
        def builder = new GroovySqlUpdateBuilder(sql)
        def update = builder.update(TABLE_NAME) {
            row(name: 'New Vegas', founded_year: 2011)
            not {
                eq(name: 'name', value: 'Las Vegas')
                isNull(name: 'name')
            }
        }

        assert update.statement.sql == "UPDATE city SET name = ?, founded_year = ? WHERE NOT (name = ? AND name is null)"
         assert update.statement.params.size() == 3
        assert update.statement.params.get(0) == "New Vegas"
        assert update.statement.params.get(1) == 2011
        assert update.statement.params.get(2) == "Las Vegas"
        assertRowAfterUpdate()
        println "Updated rows: $update.result"
    }

    private void assertRowAfterUpdate() {
        def firstRow = sql.firstRow("SELECT * from city WHERE id = ?", [1])
        assert firstRow.name == "New Vegas"
        assert firstRow.state == "Nevada"
        assert firstRow.founded_year == 2011
    }
}
