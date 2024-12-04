import android.R.attr.bitmap
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.navigation.NavHostController
import com.example.drbanana.classifyImage
import com.example.drbanana.ui.ImagePicker
import com.example.drbanana.R
import com.example.drbanana.ui.ScanButton
import com.example.drbanana.ui.createBitmapFromUri
import kotlin.div
import kotlin.text.compareTo
import kotlin.toString

@Composable
fun ResultScreen(predictionResult: FloatArray?, imageUri: Uri?, navController: NavHostController) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    var isFullScreen by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val halfScreenHeight = (screenHeight / 2) + 25.dp
    val targetHeight by animateDpAsState(targetValue = if (isFullScreen) screenHeight else halfScreenHeight, animationSpec = tween(durationMillis = 1000))

        if (showDialog.value) {
        ImagePicker(onImageCaptured = { uri ->
            uri?.let {
                bitmap.value = createBitmapFromUri(context, it)
                bitmap.value?.let { bmp ->
                    val classificationResults = classifyImage(context, bmp)
                    navController.navigate(
                        "result/${Uri.encode(it.toString())}/${classificationResults.joinToString(",")}"
                    )
                }
            }
            showDialog.value = false
        }, onDismiss = { showDialog.value = false })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = rememberImagePainter(imageUri),
            contentDescription = "Captured Image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(30.dp)
                    .clickable {
                        showDialog.value = true
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.exit),
                contentDescription = "Back to home",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(30.dp)
                    .clickable {
                        navController.navigate("home")
                    }
            )
        }

        var disease by remember { mutableStateOf("No data available") }

        LaunchedEffect(predictionResult) {
            predictionResult?.let {
                disease = identifyDisease(it)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(targetHeight)
                .let { if (!isFullScreen) it.clip(RoundedCornerShape(20.dp)) else it }
                .background(Color.White)
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .height(5.dp)
                        .width(80.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.Gray)
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                isFullScreen = !isFullScreen
                            }
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.align(Alignment.Start)) {
                    Image(
                        painter = painterResource(id = R.drawable.greentick),
                        contentDescription = "check",
                        Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Hurray, we identified the Disease!",
                        fontSize = 14.sp,
                        color = Color(0xFF61AF2B)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Image(
                        painter = painterResource(id = if (isSaved) R.drawable.save else R.drawable.history),
                        contentDescription = "save",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(enabled = !isSaved) {
                                isSaved = true
                            }
                    )
                }

                when(disease) {
                    "Healthy" -> {
                        Text(
                            text = "Your plant is healthy!",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                    "Cordana Leaf Spot" -> { Cordana(isFullScreen) }
                    "Panama Disease" -> { Panama(isFullScreen) }
                    "Black/Yellow Sigatoka" -> { Sigatoka(isFullScreen)}
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        navController.navigate("home")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF61F878),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Recommendations",
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                ScanButton(onImageCaptured = {}, navController = navController)
            }
        }
    }
}

fun identifyDisease(predictionResult: FloatArray): String {
    val keys = listOf("Cordana Leaf Spot", "Healthy", "Panama Disease", "Black/Yellow Sigatoka")

    if (predictionResult.isEmpty()) {
        return "No data available"
    }

    var max = 0

    for (i  in predictionResult.indices) {
        Log.d("PredictionResult", "New max found at index $i with value ${predictionResult[i]}")
        if (predictionResult[i] > predictionResult[max]) {
            max = i
        }
    }

    if (predictionResult[max] > 0.6)
        return keys[max]

    return "No Tree"
}

@Composable
fun Panama(isFullScreen: Boolean){
    val fullText = "Panama disease,  a devastating disease of bananas caused by the soil-inhabiting fungus species Fusarium oxysporum forma specialis cubense. A form of fusarium wilt, Panama disease is widespread throughout the tropics and can be found wherever susceptible banana cultivars are grown."
    val halfText = "Panama disease,  a devastating disease of bananas caused by the soil-inhabiting fungus"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ){
        Text(
            text = "Panama Disease",
            fontSize = 20.sp,
            fontWeight = Bold,
            color = Color.Black
        )
        Text(
            text = "(fusarium wilt)",
            fontSize = 20.sp,
            fontWeight = Bold,
            color = Color.Black
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF0F3F6))
                .size(70.dp, 22.dp),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "soil-borne",
                fontSize = 12.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Description",
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isFullScreen) fullText else halfText,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun Cordana(isFullScreen: Boolean){
    val fullText = "Cordana leaf spot is a fungal disease in banana plants caused by Cordana musae. It primarily affects the leaves, causing small, oval to elongated spots with a pale center and dark brown or purple margins. Over time, these spots can enlarge, coalesce, and form irregular necrotic patches, leading to significant leaf damage. This reduces the photosynthetic capacity of the plant, ultimately affecting its growth and fruit yield. The disease typically thrives in warm, humid conditions and is often spread through water splash, wind, or contaminated tools."
    val halfText = "Cordana leaf spot is a fungal disease in banana plants caused by Cordana musae. It primarily affects the leaves, causing small,"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ){
        Text(
            text = "Cordana Leaf Spot",
            fontSize = 20.sp,
            fontWeight = Bold,
            color = Color.Black
        )
        Text(
            text = "(cordana musae)",
            fontSize = 20.sp,
            fontWeight = Bold,
            color = Color.Black
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF0F3F6))
                .size(70.dp, 22.dp),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "soil-borne",
                fontSize = 12.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Description",
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isFullScreen) fullText else halfText,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun Sigatoka(isFullScreen: Boolean){
    val fullText = "Black Sigatoka (caused by Pseudocercospora fijiensis) and Yellow Sigatoka (caused by Pseudocercospora musae) are destructive fungal diseases that affect banana plants, primarily targeting the leaves. Both diseases start with small, yellowish spots that progressively enlarge into brown or black streaks, eventually causing extensive leaf necrosis. Black Sigatoka is more aggressive than Yellow Sigatoka, leading to faster leaf destruction and significant reductions in photosynthesis. This results in stunted growth, poor fruit quality, and decreased yields. These diseases thrive in warm, humid climates and are spread through wind, rain splash, or contaminated tools."
    val halfText = "Black Sigatoka (caused by Pseudocercospora fijiensis) and Yellow Sigatoka (caused by"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ){
        Text(
            text = "Black Sigatoka or  Yellow Sigatoka",
            fontSize = 20.sp,
            fontWeight = Bold,
            color = Color.Black
        )
        Text(
            text = "(Pseudocercospora fijiensis or Pseudocercospora musae)",
            fontSize = 20.sp,
            fontWeight = Bold,
            color = Color.Black
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF0F3F6))
                .size(70.dp, 22.dp),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "soil-borne",
                fontSize = 12.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Description",
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isFullScreen) fullText else halfText,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}