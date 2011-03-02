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

import org.junit.Test

/**
 *
 *
 * @author Benjamin Muschko
 */
class GroovySqlInsertBuilderTest extends GroovySqlBuilderFixture {
    @Test
    public void testBuildInsertWithoutTableAttribute() {
        def builder = new GroovySqlInsertBuilder(sql)
        def insert = builder.insert('city') {
            row(name: 'Grand Rapids', state: 'Michigan', founded_year: 1825)
            row(name: 'Little Rock', state: 'Arkansas', founded_year: 1821)
        }

        checkAssertions(insert)
        println "Created records with IDs $insert.result"
    }

    @Test
    public void testBuildInsertWithTableAttribute() {
        def builder = new GroovySqlInsertBuilder(sql)
        def insert = builder.insert(table: 'city') {
            row(name: 'Grand Rapids', state: 'Michigan', founded_year: 1825)
            row(name: 'Little Rock', state: 'Arkansas', founded_year: 1821)
        }

        checkAssertions(insert)
        println "Created records with IDs $insert.result"
    }

    private void checkAssertions(insert) {
        assert insert.statements.size() == 2
        assert insert.statements.get(0).sql == "INSERT INTO city (name, state, founded_year) VALUES (?, ?, ?)"
        assert insert.statements.get(0).params.size() == 3
        assert insert.statements.get(0).params.get(0) == "Grand Rapids"
        assert insert.statements.get(0).params.get(1) == "Michigan"
        assert insert.statements.get(0).params.get(2) == 1825
        assert insert.statements[1].sql == "INSERT INTO city (name, state, founded_year) VALUES (?, ?, ?)"
        assert insert.statements.get(1).params.size() == 3
        assert insert.statements.get(1).params.get(0) == "Little Rock"
        assert insert.statements.get(1).params.get(1) == "Arkansas"
        assert insert.statements.get(1).params.get(2) == 1821
        assert insert.result.size() == 2
        assert insert.result.get(0) == [1]
        assert insert.result.get(1) == [2]

        def firstRow = sql.firstRow("SELECT * from city WHERE id = ?", [1])
        assert firstRow.name == "Grand Rapids"
        assert firstRow.state == "Michigan"
        assert firstRow.founded_year == 1825
        def secondRow = sql.firstRow("SELECT * from city WHERE id = ?", [2])
        assert secondRow.name == "Little Rock"
        assert secondRow.state == "Arkansas"
        assert secondRow.founded_year == 1821
    }
}
