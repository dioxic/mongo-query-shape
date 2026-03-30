package org.mongo.mqs.model

data class MetricLabels(
    val organisation: String? = null,
    val project: String? = null,
    val cluster: String? = null,
    val instance: String? = null,
) {
    fun toPromFilter() = toList()
        .joinToString(separator = ",", prefix = "{", postfix = "}")

    fun toList() = buildList {
        if (organisation != null) add("org_id=\"$organisation\"")
        if (project != null) add("group_id=\"$project\"")
        if (cluster != null) add("cl_name=\"$cluster\"")
        if (instance != null) add("instance=\"$instance\"")
    }
}
