package com.yapp.plus.data.supabase

class SupabaseDataSourceException internal constructor(
    message: String,
    val code: String? = null,
) : Exception(message)
