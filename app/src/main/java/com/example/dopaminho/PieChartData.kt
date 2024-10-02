package com.example.dopaminho


// on below line we are creating data class for
// pie chart data and passing variable as browser
// name and value.
data class PieChartData(
    var browserName: String?,
    var value: Float?
)

// on below line we are creating a method
// in which we are passing all the data.
val getPieChartData = listOf(
    PieChartData("TikTok", 34.68F),
    PieChartData("Youtube", 16.60F),
    PieChartData("Instagram", 16.15F),
    PieChartData("Facebook", 15.62F),
)