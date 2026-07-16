package com.example.find.navigation

object Routes {
    const val MAP = "map"
    const val SEARCH = "search"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val REPORT = "report"
    const val ADMIN = "admin"
    const val BENCH_DETAIL = "bench/{benchId}"

    fun benchDetail(benchId: Long) = "bench/$benchId"
}
