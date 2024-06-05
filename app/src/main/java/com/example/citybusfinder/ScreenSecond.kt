package com.example.citybusfinder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun PermissionScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top, // Align content vertically at the top
        horizontalAlignment = Alignment.Start // Align content horizontally at the start
    ) {

//        Spacer(modifier = Modifier.height(20.dp))
//        Button(onClick = { navController.popBackStack() }) {
//            Text(text = "Go Back")
//        }
        Text(
            text = stringResource(R.string.Content_permission),
            modifier = Modifier.padding(top = 30.dp),
            fontWeight = FontWeight.Bold
        )
        val image: Painter = painterResource(R.drawable.bus2)
        Image(
            painter = image,
            contentDescription = null,
//            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(top = 100.dp, start = 15.dp).size(300.dp) .clip(
                RoundedCornerShape(16.dp)
            )
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(56.dp),
        verticalArrangement = Arrangement.Bottom, // Align content vertically at the top
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { /*TODO*/ },  modifier = Modifier
            .fillMaxWidth(0.8f)
        ) {
            Text(text = "Allow")
        }
        Button(onClick = { },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Denied")
        }

    }

}

