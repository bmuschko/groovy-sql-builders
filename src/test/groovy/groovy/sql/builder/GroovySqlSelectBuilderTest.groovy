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
class GroovySqlSelectBuilderTest extends GroovySqlBuilderFixture {
    @Test
    public void testBuildingWithEqualsCriteria() {
        def builder = new GroovySqlSelectBuilder(sql)
        def select = builder.select(TABLE_NAME) {
            eq(name: 'name', value: 'Las Vegas')
        }

        assert select.statement.sql == "SELECT * FROM city WHERE name = ?"
        assert select.statement.params.size() == 1
        assert select.statement.params.get(0) == "'Las Vegas'"
        println "Selected rows: ${select.result}"
    }
}
