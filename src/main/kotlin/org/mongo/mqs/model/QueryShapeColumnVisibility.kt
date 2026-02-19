package org.mongo.mqs.model

data class QueryShapeColumnVisibility(
    val execCount: Boolean,
    val avgExec: Boolean,
    val maxExec: Boolean,
    val minExec: Boolean,
    val collScan: Boolean,
    val targetScore: Boolean,
) {
    fun filter(input: List<String>): List<String> = input.filter {
        when (it) {
            "Execution Count" if !execCount -> false
            "Avg Execution" if !avgExec -> false
            "Max Execution" if !maxExec -> false
            "Min Execution" if !minExec -> false
            "Collection Scan" if !collScan -> false
            "Targeting Score" if !targetScore -> false
            else -> true
        }
    }

    companion object {
        val Default = QueryShapeColumnVisibility(
            execCount = true,
            avgExec = true,
            maxExec = true,
            minExec = true,
            collScan = true,
            targetScore = true
        )
    }
}
