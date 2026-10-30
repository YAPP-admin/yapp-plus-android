package com.yapp.plus.data.auth

import com.yapp.plus.core.datastore.SessionRecordStore
import com.yapp.plus.core.datastore.SessionRecordUpdate
import com.yapp.plus.core.datastore.SessionTokenRecord

internal class FakeSessionRecordStore : SessionRecordStore {
    var record = SessionTokenRecord()

    override suspend fun fetchTokens(): SessionTokenRecord = record

    override suspend fun updateSession(transform: SessionRecordUpdate): SessionTokenRecord {
        record = transform(record)
        return record
    }
}
