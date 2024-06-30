package com.example.chatapp.presentation.screen.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.example.chatapp.R
import com.example.chatapp.component.GoogleButton
import com.example.chatapp.component.MessageBar
import com.example.chatapp.domain.model.MessageBarState

@Composable
fun LoginContent(
    signedInState: Boolean,
    messageBarState: MessageBarState,
    onButtonClicked: () -> Unit,
    paddingValue: PaddingValues
) {

    val composistion by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.wavy))
    val progress by animateLottieCompositionAsState(
        composition = composistion,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier
            .padding(paddingValues = paddingValue),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Log.d("tokenId","${messageBarState.message} | ${messageBarState.error?.message}")
            MessageBar(messageBarState = messageBarState)
        }
        Column(
            modifier = Modifier
                .weight(9f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            LottieAnimation(
//                composition = composistion,
//                progress = { progress }
//            )
            CentralContent(
                signedInState = signedInState,
                onButtonClicked = onButtonClicked
            )
        }
    }
}

@Composable
fun CentralContent(
    signedInState: Boolean,
    onButtonClicked: () -> Unit
) {

    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.chat))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        modifier = Modifier
            .size(300.dp),
        composition = composition,
        progress = { progress },
        dynamicProperties = rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR_FILTER,
                value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    MaterialTheme.colorScheme.primary.hashCode(),
                    BlendModeCompat.SRC_ATOP
                ),
                keyPath = arrayOf(
                    "**"
                )
            )
        )
    )
//    Image(
//        modifier = Modifier
//            .padding(bottom = 20.dp)
//            .size(120.dp),
//        painter = painterResource(id = R.drawable.google_logo),
//        contentDescription = "Google Logo"
//    )
//    Text(
//        text = stringResource(R.string.sign_in_to_continue),
//        fontWeight = FontWeight.Bold,
//        fontSize = MaterialTheme.typography.titleLarge.fontSize
//    )
//    Text(
//        modifier = Modifier
//            .alpha(alpha = 0.7f)
//            .padding(bottom = 40.dp, top = 4.dp),
//        text = stringResource(R.string.sign_in_subtitle),
//        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
//        textAlign = TextAlign.Center
//    )
    GoogleButton(
        loadingState = signedInState,
        onClick = onButtonClicked
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun LoginContentPreview() {
    LoginContent(
        signedInState = false,
        messageBarState = MessageBarState(),
        onButtonClicked = {},
        paddingValue = PaddingValues(10.dp)
    )
}