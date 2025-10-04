package dev.ajithgoveas.transliterator.utils

fun generateFriendlyAnchors(minZoom: Float, maxZoom: Float): List<Float> {
    val anchors = mutableListOf<Float>()
    var zoom = minZoom

    while (zoom <= maxZoom) {
        anchors.add(zoom)
        zoom *= if (zoom < 2f) 2f else if (zoom < 10f) 2f else 5f  // 1, 2, 5, 10, 20...
    }

    anchors.add(maxZoom) // ensure max included
    return anchors.distinct().sorted()
}