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
class GroovySqlDeleteBuilderTest extends GroovySqlBuilderFixture {
    @Test
    public void testBuildingWithEqualsCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            eq(name: 'name', value: 'Grand Rapids')
        }

        assert delete.statement.sql == "DELETE FROM city WHERE name = 'Grand Rapids'"
    }

    @Test
    public void testBuildingWithNotEqualsCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            ne(name: 'name', value: 'Grand Rapids')
        }

        assert delete.statement.sql == "DELETE FROM city WHERE name != 'Grand Rapids'"
    }

    @Test
    public void testBuildingWithLikeCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            like(name: 'name', value: 'Grand%')
        }

        assert delete.statement.sql == "DELETE FROM city WHERE name like 'Grand%'"
    }

    @Test
    public void testBuildingWithIsNullCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            isNull(name: 'name')
        }

        assert delete.statement.sql == "DELETE FROM city WHERE name is null"
    }

    @Test
    public void testBuildingWithIsNotNullCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            isNotNull(name: 'name')
        }

        assert delete.statement.sql == "DELETE FROM city WHERE name is not null"
    }

    @Test
    public void testBuildingWithGreaterThanCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            gt(name: 'founded_year', value: 1900)
        }

        assert delete.statement.sql == "DELETE FROM city WHERE founded_year > 1900"
    }

    @Test
    public void testBuildingWithGreaterThanEqualsCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            ge(name: 'founded_year', value: 1900)
        }

        assert delete.statement.sql == "DELETE FROM city WHERE founded_year >= 1900"
    }

    @Test
    public void testBuildingWithLessThanCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            lt(name: 'founded_year', value: 1900)
        }

        assert delete.statement.sql == "DELETE FROM city WHERE founded_year < 1900"
    }

    @Test
    public void testBuildingWithLessThanEqualsCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            le(name: 'founded_year', value: 1900)
        }

        assert delete.statement.sql == "DELETE FROM city WHERE founded_year <= 1900"
    }

    @Test
    public void testBuildingWithBetweenCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            between(name: 'founded_year', start: 1900, end: 1950)
        }

        assert delete.statement.sql == "DELETE FROM city WHERE founded_year BETWEEN 1900 AND 1950"
    }

    @Test
    public void testBuildingWithInCriteria() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            'in'(name: 'founded_year', value: [1900, 1901, 1903])
        }

        assert delete.statement.sql == "DELETE FROM city WHERE founded_year IN (1900, 1901, 1903)"
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

        assert delete.statement.sql == "DELETE FROM city WHERE (name = 'Grand Rapids' AND name is not null)"
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

        assert delete.statement.sql == "DELETE FROM city WHERE (name = 'Grand Rapids' OR name = 'Little Rock')"
    }

    @Test
    public void testBuildingWithNotStatement() {
        def builder = new GroovySqlDeleteBuilder(sql)
        def delete = builder.delete(TABLE_NAME) {
            not {
                between(name: 'founded_year', start: 1900, end: 1950)
            }
        }

        assert delete.statement.sql == "DELETE FROM city WHERE NOT (founded_year BETWEEN 1900 AND 1950)"
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

        assert delete.statement.sql == "DELETE FROM city WHERE (name is not null AND (name = 'Grand Rapids' OR name = 'Little Rock'))"
    }
}