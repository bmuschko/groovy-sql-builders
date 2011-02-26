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
class GroovySqlUpdateBuilderTest extends GroovySqlBuilderFixture {
    @Test
    public void testBuildingWithEqualsCriteria() {
        def builder = new GroovySqlUpdateBuilder(sql)
        def update = builder.update(TABLE_NAME) {
            row(name: 'New Vegas', founded_year: 2011)
            eq(name: 'name', value: 'Las Vegas')
        }

        assert update.statement.sql == "UPDATE city SET name = ?, founded_year = ? WHERE name = 'Las Vegas'"
        assert update.statement.params.size() == 2
        assert update.statement.params.get(0) == "'New Vegas'"
        assert update.statement.params.get(1) == '2011'
        println "Updated rows: $update.result"
    }
}
