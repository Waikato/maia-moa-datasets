/*
 * withColumnHeadersToInstancesHeader.kt
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 *
 * This file is part of MĀIA.
 *
 * MĀIA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MĀIA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MĀIA.  If not, see <https://www.gnu.org/licenses/>.
 */
package māia.ml.dataset.moa

import com.yahoo.labs.samoa.instances.Instances
import com.yahoo.labs.samoa.instances.InstancesHeader
import māia.ml.dataset.WithColumnHeaders
import māia.ml.dataset.WithMetadata
import māia.util.asIterable
import māia.util.enumerate
import māia.util.filter

fun withColumnHeadersToInstancesHeader(
    source: WithColumnHeaders,
    classIndex : Int? = null
): InstancesHeader {
    return when (source) {
        is MOADataStream -> source.source.header
        is MOADataRow -> source.stream.source.header
        else -> InstancesHeader(
            Instances(
                if (source is WithMetadata) source.name else "",
                Array(source.numColumns) {
                    dataColumnHeaderToAttribute(
                        source.getColumnHeader(it)
                    )
                },
                0
            )
        ).apply {
            val classIndexActual = classIndex ?: source
                .iterateColumnHeaders()
                .enumerate()
                .filter { it.second.isTarget }
                .asIterable()
                .firstOrNull()
                ?.first

            if (classIndexActual != null) setClassIndex(classIndexActual)
        }
    }
}
