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
package groovy.sql.builder.node.util

/**
 *
 *
 * @author Benjamin Muschko
 */
final class CriteriaUtil {
    private CriteriaUtil() {
    }

    static String getCriteriaValue(value) {
        value instanceof String ? "'${value}'" : value
    }

    static getCriteraValues(values) {
        def criteriaValues = []

        values.each { value ->
            criteriaValues << getCriteriaValue(value)
        }

        criteriaValues
    }

    static String joinCriteriaValues(values, separator = ',') {
        def concatinatedValues = new StringBuilder()

        values.eachWithIndex { value, index ->
            concatinatedValues <<= getCriteriaValue(value)

            if(index < values.size() - 1) {
                concatinatedValues <<= separator
            }
        }

        concatinatedValues
    }
}
