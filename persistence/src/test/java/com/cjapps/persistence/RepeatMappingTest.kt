package com.cjapps.persistence

import com.cjapps.domain.Repeat
import com.cjapps.persistence.entity.RepeatType
import com.cjapps.persistence.mapper.toDomain
import com.cjapps.persistence.mapper.toEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class RepeatMappingTest {

    @Test
    fun testMappingToDomain() {
        assertEquals(Repeat.NONE, RepeatType.NONE.toDomain())
        assertEquals(Repeat.ONCE, RepeatType.ONCE.toDomain())
        assertEquals(Repeat.ALL, RepeatType.ALL.toDomain())
    }

    @Test
    fun testMappingToEntity() {
        assertEquals(RepeatType.NONE, Repeat.NONE.toEntity())
        assertEquals(RepeatType.ONCE, Repeat.ONCE.toEntity())
        assertEquals(RepeatType.ALL, Repeat.ALL.toEntity())
    }
}