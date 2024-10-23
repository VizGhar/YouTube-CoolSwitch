package sk.skillmea.kandrac.compose.coolswitch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val check1 = remember { mutableStateOf(true) }
                    val check2 = remember { mutableStateOf(true) }

                    CoolSwitch(modifier = Modifier.size(100.dp, 50.dp), checked = check1.value, type = CoolSwitchType.DAY_NIGHT) { check1.value = it }
                    CoolSwitch(modifier = Modifier.size(100.dp, 50.dp), checked = check2.value, type = CoolSwitchType.ORANGE) { check2.value = it }

                }
            }
        }
    }
}

