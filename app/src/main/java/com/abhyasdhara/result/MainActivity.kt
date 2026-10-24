package com.abhyasdhara.result

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntSize
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(background = Color(0xFFF9FAFB))) {
                TestResultPage()
            }
        }
    }
}

@Composable
private fun TestResultPage() {
    val context = LocalContext.current
    var viewport by remember { mutableStateOf(IntSize.Zero) }
    // Rasterize native paths once at the actual display resolution. Software Canvas supports
    // the soft, colored path shadows consistently on every supported Android release.
    val artwork = remember(viewport) {
        if (viewport.width == 0 || viewport.height == 0) null else {
            val scale = min(viewport.width / 1122f, viewport.height / 1402f)
            ResultArtwork(context).render((1122 * scale).toInt(), (1402 * scale).toInt()).asImageBitmap()
        }
    }
    Box(Modifier.fillMaxSize().background(Color(0xFFF9FAFB))) {
        Canvas(Modifier.fillMaxSize().onSizeChanged { viewport = it }.semantics {
            contentDescription = "Test result. अभ्यासधारा अकॅडमी – प्रीमियम साप्ताहिक पोलीस भरती टेस्ट. " +
                "17 to 24 October 2026. Test date: 17 October 2026. Obtained marks 0 out of 100. " +
                "Percentage 0 percent. Accuracy 0 percent. Total questions 100. Correct answers 0. " +
                "Wrong answers 0. Skipped questions 100. Attempted questions 0. " +
                "Total test duration 90 minutes. Time taken 2 seconds. Performance status: Needs More Practice."
        }) {
            artwork?.let { drawImage(it, topLeft = androidx.compose.ui.geometry.Offset(
                (size.width - it.width) / 2f, (size.height - it.height) / 2f)) }
        }
    }
}
