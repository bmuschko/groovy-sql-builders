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
import org.junit.Before

/**
 *
 *
 * @author Benjamin Muschko
 */
class GroovySqlDeleteBuilderTest extends GroovySqlBuilderFixture {
    @Before
    @Override
    public void setUp() {
        super.setUp()
        sql.executeInsert('INSERT INTO city (name, state, founded_year) VALUES (?, ?, ?)', ['Grand Rapids', 'Michigan', 1825])
        sql.executeInsert('INSERT INTO city (name, state, founded_year) VALUES (?, ?, ?)', ['Little Rock', 'Arkansas', 1821])
        sql.executeInsert('INSERT INTO city (name, state, founded_year) VALUES (?, ?, ?)', ['Boston', 'Massachusetts', 1630])
        def firstRow = sql.firstRow("SELECT * from city WHERE id = ?", [1])
        assert firstRow.name == "Grand Rapids"
        assert firstRow.state == "Michigan"
        assert firstRow.founded_year == 1825
        def secondRow = sql.firstRow("SELECT * from city WHERE id = ?", [2])
        assert secondRow.name == "Little Rock"
        assert secondRow.state == "Arkansas"
        assert secondRow.founded_year == 1821
        def thirdRow = sql.firstRow("SELECT * from city WHERE id = ?", [3])
        assert thirdRow.name == "Boston"
        assert thirdRow.state == "Massachusetts"
        assert thirdRow.founded_year == 1630
    }

    @Test
    public void testBuildingWithoutCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME)

        assert delete.statement.sql == "DELETE FROM city"
        assert delete.statement.params.size() == 0
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 0
    }

    @Test
    public void testBuildingWithEqualsCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            eq(name: 'name', value: 'Grand Rapids')
        }

        assert delete.statement.sql == "DELETE FROM city WHERE name = ?"
        assert delete.statement.params.size() == 1
        assert delete.statement.params.get(0) == "Grand Rapids"
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 2
        assert rows.get(0).name == "Little Rock"
        assert rows.get(0).state == "Arkansas"
        assert rows.get(0).founded_year == 1821
        assert rows.get(1).name == "Boston"
        assert rows.get(1).state == "Massachusetts"
        assert rows.get(1).founded_year == 1630
    }

    @Test
    public void testBuildingWithNotEqualsCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            ne(name: 'name', value: 'Grand Rapids')
        }

        assert delete.statement.sql == "DELETE FROM city WHERE name != ?"
        assert delete.statement.params.size() == 1
        assert delete.statement.params.get(0) == "Grand Rapids"
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 1
        assert rows.get(0).name == "Grand Rapids"
        assert rows.get(0).state == "Michigan"
        assert rows.get(0).founded_year == 1825
    }

    @Test
    public void testBuildingWithLikeCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            like(name: 'name', value: 'Grand%')
        }

        assert delete.statement.sql == "DELETE FROM city WHERE name like ?"
        assert delete.statement.params.size() == 1
        assert delete.statement.params.get(0) == "Grand%"
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 2
        assert rows.get(0).name == "Little Rock"
        assert rows.get(0).state == "Arkansas"
        assert rows.get(0).founded_year == 1821
        assert rows.get(1).name == "Boston"
        assert rows.get(1).state == "Massachusetts"
        assert rows.get(1).founded_year == 1630
    }

    @Test
    public void testBuildingWithIsNullCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            isNull(name: 'name')
        }

        assert delete.statement.sql == "DELETE FROM city WHERE name is null"
        assert delete.statement.params.size() == 0
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 3
        assert rows.get(0).name == "Grand Rapids"
        assert rows.get(0).state == "Michigan"
        assert rows.get(0).founded_year == 1825
        assert rows.get(1).name == "Little Rock"
        assert rows.get(1).state == "Arkansas"
        assert rows.get(1).founded_year == 1821
        assert rows.get(2).name == "Boston"
        assert rows.get(2).state == "Massachusetts"
        assert rows.get(2).founded_year == 1630
    }

    @Test
    public void testBuildingWithIsNotNullCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            isNotNull(name: 'name')
        }

        assert delete.statement.sql == "DELETE FROM city WHERE name is not null"
        assert delete.statement.params.size() == 0
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 0
    }

    @Test
    public void testBuildingWithGreaterThanCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            gt(name: 'founded_year', value: 1822)
        }

        assert delete.statement.sql == "DELETE FROM city WHERE founded_year > ?"
        assert delete.statement.params.size() == 1
        assert delete.statement.params.get(0) == 1822
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 2
        assert rows.get(0).name == "Little Rock"
        assert rows.get(0).state == "Arkansas"
        assert rows.get(0).founded_year == 1821
        assert rows.get(1).name == "Boston"
        assert rows.get(1).state == "Massachusetts"
        assert rows.get(1).founded_year == 1630
    }

    @Test
    public void testBuildingWithGreaterThanEqualsCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            ge(name: 'founded_year', value: 1821)
        }

        assert delete.statement.sql == "DELETE FROM city WHERE founded_year >= ?"
        assert delete.statement.params.size() == 1
        assert delete.statement.params.get(0) == 1821
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 1
        assert rows.get(0).name == "Boston"
        assert rows.get(0).state == "Massachusetts"
        assert rows.get(0).founded_year == 1630
    }

    @Test
    public void testBuildingWithLessThanCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            lt(name: 'founded_year', value: 1820)
        }

        assert delete.statement.sql == "DELETE FROM city WHERE founded_year < ?"
        assert delete.statement.params.size() == 1
        assert delete.statement.params.get(0) == 1820
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 2
        assert rows.get(0).name == "Grand Rapids"
        assert rows.get(0).state == "Michigan"
        assert rows.get(0).founded_year == 1825
        assert rows.get(1).name == "Little Rock"
        assert rows.get(1).state == "Arkansas"
        assert rows.get(1).founded_year == 1821
    }

    @Test
    public void testBuildingWithLessThanEqualsCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            le(name: 'founded_year', value: 1821)
        }

        assert delete.statement.sql == "DELETE FROM city WHERE founded_year <= ?"
        assert delete.statement.params.size() == 1
        assert delete.statement.params.get(0) == 1821
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 1
        assert rows.get(0).name == "Grand Rapids"
        assert rows.get(0).state == "Michigan"
        assert rows.get(0).founded_year == 1825
    }

    @Test
    public void testBuildingWithBetweenCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            between(name: 'founded_year', start: 1820, end: 1830)
        }

        assert delete.statement.sql == "DELETE FROM city WHERE founded_year BETWEEN ? AND ?"
        assert delete.statement.params.size() == 2
        assert delete.statement.params.get(0) == 1820
        assert delete.statement.params.get(1) == 1830
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 1
        assert rows.get(0).name == "Boston"
        assert rows.get(0).state == "Massachusetts"
        assert rows.get(0).founded_year == 1630
    }

    @Test
    public void testBuildingWithInCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            'in'(name: 'founded_year', value: [1821, 1825, 1700])
        }

        assert delete.statement.sql == "DELETE FROM city WHERE founded_year IN (?, ?, ?)"
        assert delete.statement.params.size() == 3
        assert delete.statement.params.get(0) == 1821
        assert delete.statement.params.get(1) == 1825
        assert delete.statement.params.get(2) == 1700
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 1
        assert rows.get(0).name == "Boston"
        assert rows.get(0).state == "Massachusetts"
        assert rows.get(0).founded_year == 1630
    }

    @Test
    public void testBuildingWithAndStatement() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            and {
                eq(name: 'name', value: 'Grand Rapids')
                isNotNull(name: 'name')
            }
        }

        assert delete.statement.sql == "DELETE FROM city WHERE (name = ? AND name is not null)"
        assert delete.statement.params.size() == 1
        assert delete.statement.params.get(0) == "Grand Rapids"
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 2
        assert rows.get(0).name == "Little Rock"
        assert rows.get(0).state == "Arkansas"
        assert rows.get(0).founded_year == 1821
        assert rows.get(1).name == "Boston"
        assert rows.get(1).state == "Massachusetts"
        assert rows.get(1).founded_year == 1630
    }

    @Test
    public void testBuildingWithOrStatement() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            or {
                eq(name: 'name', value: 'Grand Rapids')
                eq(name: 'name', value: 'Little Rock')
            }
        }

        assert delete.statement.sql == "DELETE FROM city WHERE (name = ? OR name = ?)"
        assert delete.statement.params.size() == 2
        assert delete.statement.params.get(0) == "Grand Rapids"
        assert delete.statement.params.get(1) == "Little Rock"
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 1
        assert rows.get(0).name == "Boston"
        assert rows.get(0).state == "Massachusetts"
        assert rows.get(0).founded_year == 1630
    }

    @Test
    public void testBuildingWithNotStatement() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            not {
                between(name: 'founded_year', start: 1820, end: 1830)
            }
        }

        assert delete.statement.sql == "DELETE FROM city WHERE NOT (founded_year BETWEEN ? AND ?)"
        assert delete.statement.params.size() == 2
        assert delete.statement.params.get(0) == 1820
        assert delete.statement.params.get(1) == 1830
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 2
        assert rows.get(0).name == "Grand Rapids"
        assert rows.get(0).state == "Michigan"
        assert rows.get(0).founded_year == 1825
        assert rows.get(1).name == "Little Rock"
        assert rows.get(1).state == "Arkansas"
        assert rows.get(1).founded_year == 1821
    }

    @Test
    public void testBuildingWithNestedLogicStatements() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            and {
                isNotNull(name: 'name')

                or {
                    eq(name: 'name', value: 'Grand Rapids')
                    eq(name: 'name', value: 'Little Rock')
                }
            }
        }

        assert delete.statement.sql == "DELETE FROM city WHERE (name is not null AND (name = ? OR name = ?))"
        assert delete.statement.params.size() == 2
        assert delete.statement.params.get(0) == "Grand Rapids"
        assert delete.statement.params.get(1) == "Little Rock"
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 1
        assert rows.get(0).name == "Boston"
        assert rows.get(0).state == "Massachusetts"
        assert rows.get(0).founded_year == 1630
    }

    @Test
    public void testBuildingWithControlStructure() {
        def cityNames = [ "Grand Rapids", "Little Rock", "Minneapolis"]
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            or {
                for(cityName in cityNames) {
                    eq(name: 'name', value: cityName)
                }
            }
        }

        assert delete.statement.sql == "DELETE FROM city WHERE (name = ? OR name = ? OR name = ?)"
        assert delete.statement.params.size() == 3
        assert delete.statement.params.get(0) == "Grand Rapids"
        assert delete.statement.params.get(1) == "Little Rock"
        assert delete.statement.params.get(2) == "Minneapolis"
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 1
        assert rows.get(0).name == "Boston"
        assert rows.get(0).state == "Massachusetts"
        assert rows.get(0).founded_year == 1630
    }
}