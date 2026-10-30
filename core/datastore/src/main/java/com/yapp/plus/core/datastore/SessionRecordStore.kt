package com.yapp.plus.core.datastore

interface SessionRecordStore {
    suspend fun fetchTokens(): SessionTokenRecord

    suspend fun updateSession(transform: SessionRecordUpdate): SessionTokenRecord
}
