package dev.shreyaspatil.example.captureissuedemo

import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.shreyaspatil.example.captureissuedemo.ui.theme.CaptureIssueDemoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CaptureIssueDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Demo()
                }
            }
        }
    }
}

@Composable
fun Demo() {
    Column {
        // 1. Show actual profile
        Text(text = "1. Just Composable")
        Profile()

        HorizontalDivider()

        // 2. Show profile with capturable modifier
        Text(text = "2. Composable with Bitmap capture-ability")
        ProfileWithCapturability()
    }
}

@Composable
fun ProfileWithCapturability() {
    Column {
        val picture = remember { Picture() }
        val scope = rememberCoroutineScope()
        var capturedImage by remember { mutableStateOf<ImageBitmap?>(null) }

        Column(Modifier.drawWithCache {
            // Example that shows how to redirect rendering to an Android Picture and then
            // draw the picture into the original destination
            val width = this.size.width.toInt()
            val height = this.size.height.toInt()

            onDrawWithContent {
                val pictureCanvas =
                    androidx.compose.ui.graphics.Canvas(
                        picture.beginRecording(
                            width,
                            height
                        )
                    )
                // requires at least 1.6.0-alpha01+
                draw(this, this.layoutDirection, pictureCanvas, this.size) {
                    this@onDrawWithContent.drawContent()
                }
                picture.endRecording()

                drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
            }
        }) {
            Profile()
        }

        Button(onClick = {
            scope.launch(Dispatchers.Default) {
                capturedImage = createBitmapFromPicture(picture).asImageBitmap()
            }
        }) {
            Text(text = "Capture Now")
        }

        capturedImage?.let { image ->
            Text(text = "This is captured ⬇️")
            Image(bitmap = image, contentDescription = "Captured content")
        }
    }
}

@Composable
fun Profile() {
    Column {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://3.bp.blogspot.com/-VVp3WvJvl84/X0Vu6EjYqDI/AAAAAAAAPjU/ZOMKiUlgfg8ok8DY8Hc-ocOvGdB0z86AgCLcBGAsYHQ/s1600/jetpack%2Bcompose%2Bicon_RGB.png")
                .crossfade(true)
                .build(),
            contentDescription = "Profile",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(100.dp)
        )
        Text(text = "Jetpack Compose")
    }
}

private fun createBitmapFromPicture(picture: Picture): Bitmap {
    // [START android_compose_draw_into_bitmap_convert_picture]
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Bitmap.createBitmap(picture)
    } else {
        val bitmap = Bitmap.createBitmap(
            picture.width,
            picture.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawPicture(picture)
        bitmap
    }
    // [END android_compose_draw_into_bitmap_convert_picture]
    return bitmap
}