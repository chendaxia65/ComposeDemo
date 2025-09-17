package com.servicebio.compose.application.component

import android.content.ClipData
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.servicebio.compose.application.utils.showToast
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PhoneSheetDialog(phoneNumber: String, onDismissRequest: () -> Unit) {
    val hostActivity = LocalActivity.current
    val nativeClipboard = LocalClipboard.current.nativeClipboard
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()


    val hideSheet:()-> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismissRequest()
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                WindowInsets.safeContent.exclude(
                    WindowInsets.ime
                )
            )
    ) {
        ModalBottomSheet(
            sheetState = sheetState,
            dragHandle = null,
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            onDismissRequest = onDismissRequest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        "${phoneNumber}可能时一个电话号码，你可以",
                        modifier = Modifier
                            .align(Alignment.Center),
                        fontSize = 12.sp
                    )
                }
                HorizontalDivider(thickness = Dp.Hairline)

                TextButton(
                    onClick = {
                        hostActivity?.startActivity(Intent(Intent.ACTION_DIAL).apply {
                            data = "tel:$phoneNumber".toUri()
                        })
                        hideSheet.invoke()
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .height(48.dp), contentPadding = PaddingValues(0.dp)
                ) {
                    Text("呼叫", color = Color.Black)
                }

                HorizontalDivider(thickness = Dp.Hairline)

                TextButton(
                    onClick = {
                        nativeClipboard.setPrimaryClip(ClipData.newPlainText(null, phoneNumber))
                        showToast("已复制")
                        hideSheet.invoke()
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .height(48.dp), contentPadding = PaddingValues(0.dp)
                ) {
                    Text("复制号码", color = Color.Black)
                }

                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp))

                TextButton(
                    onClick = { hideSheet.invoke() }, modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .height(48.dp), contentPadding = PaddingValues(0.dp)
                ) {
                    Text("取消", color = Color.Black)
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EmailSheetDialog(email: String, onDismissRequest: () -> Unit) {
    val hostActivity = LocalActivity.current
    val nativeClipboard = LocalClipboard.current.nativeClipboard
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()


    val hideSheet:()-> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismissRequest()
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                WindowInsets.safeContent.exclude(
                    WindowInsets.ime
                )
            )
    ) {
        ModalBottomSheet(
            sheetState = sheetState,
            dragHandle = null,
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            onDismissRequest = onDismissRequest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        "向${email}发送邮件",
                        modifier = Modifier
                            .align(Alignment.Center),
                        fontSize = 12.sp
                    )
                }
                HorizontalDivider(thickness = Dp.Hairline)

                TextButton(
                    onClick = {
                        hostActivity?.startActivity(Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:".toUri()
                            putExtra(Intent.EXTRA_EMAIL,arrayOf(email))
                        })
                        hideSheet.invoke()
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .height(48.dp), contentPadding = PaddingValues(0.dp)
                ) {
                    Text("使用默认邮箱发送", color = Color.Black)
                }

                HorizontalDivider(thickness = Dp.Hairline)

                TextButton(
                    onClick = {
                        nativeClipboard.setPrimaryClip(ClipData.newPlainText(null, email))
                        showToast("已复制")
                        hideSheet.invoke()
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .height(48.dp), contentPadding = PaddingValues(0.dp)
                ) {
                    Text("复制邮箱", color = Color.Black)
                }

                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp))

                TextButton(
                    onClick = { hideSheet.invoke() }, modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .height(48.dp), contentPadding = PaddingValues(0.dp)
                ) {
                    Text("取消", color = Color.Black)
                }
            }
        }
    }

}