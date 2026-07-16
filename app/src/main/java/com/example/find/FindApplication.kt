package com.example.find

import android.app.Application
import com.example.find.data.local.SessionManager
import com.example.find.data.repository.AuthRepository
import com.example.find.data.repository.BenchRepository

class FindApplication : Application() {

    lateinit var sessionManager: SessionManager
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var benchRepository: BenchRepository
        private set

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)
        authRepository = AuthRepository(sessionManager)
        benchRepository = BenchRepository()
    }
}
