package com.autozen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var apiKeyInput by remember { mutableStateOf(state.weatherApiKey) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("设置", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))
        }

        // OBD Settings
        item {
            SettingsSectionTitle("OBD 数据源", Icons.Default.Bluetooth)
            SettingsCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingsToggle(
                        label = "使用真实 OBD-II 设备",
                        sub = "关闭则使用模拟数据",
                        checked = state.useRealObd,
                        onCheckedChange = viewModel::setUseRealObd
                    )
                    if (state.useRealObd) {
                        Divider(color = Color(0xFF2A2A3A))
                        Text("已配对 OBD 设备", fontSize = 13.sp, color = Color.Gray)
                        if (state.pairedObdDevices.isEmpty()) {
                            Text("未找到已配对的 ELM327/OBD 设备\n请先在系统蓝牙设置中配对",
                                fontSize = 13.sp, color = Color(0xFFFF9800),
                                lineHeight = 20.sp)
                        } else {
                            state.pairedObdDevices.forEach { device ->
                                @Suppress("MissingPermission")
                                val name = device.name ?: device.address
                                val selected = state.selectedDeviceAddress == device.address
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(name, fontSize = 14.sp, color = Color.White)
                                        Text(device.address, fontSize = 11.sp, color = Color.Gray)
                                    }
                                    RadioButton(
                                        selected = selected,
                                        onClick = { viewModel.selectObdDevice(device.address) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Units
        item {
            SettingsSectionTitle("单位设置", Icons.Default.Speed)
            SettingsCard {
                SettingsToggle(
                    label = "使用公制单位",
                    sub = if (state.useMetric) "km/h · °C · L/100km" else "mph · °F · mpg",
                    checked = state.useMetric,
                    onCheckedChange = viewModel::setUseMetric
                )
            }
        }

        // Weather API
        item {
            SettingsSectionTitle("天气 API", Icons.Default.WbSunny)
            SettingsCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("OpenWeatherMap API Key", fontSize = 13.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        placeholder = { Text("输入你的 API Key", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Button(
                        onClick = { viewModel.setWeatherApiKey(apiKeyInput) },
                        modifier = Modifier.align(Alignment.End)
                    ) { Text("保存") }
                }
            }
        }

        // App info
        item {
            SettingsSectionTitle("关于", Icons.Default.Settings)
            SettingsCard {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    InfoRow("版本", "1.1.0")
                    InfoRow("平台", "Android Automotive OS")
                    InfoRow("开源协议", "MIT License")
                    InfoRow("项目地址", "github.com/3286287363-pixel/autozen")
                }
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF00E5FF), letterSpacing = 1.sp)
    }
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}

@Composable
fun SettingsToggle(label: String, sub: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 15.sp, color = Color.White)
            Text(sub, fontSize = 12.sp, color = Color.Gray)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Text(value, fontSize = 13.sp, color = Color.White)
    }
}
