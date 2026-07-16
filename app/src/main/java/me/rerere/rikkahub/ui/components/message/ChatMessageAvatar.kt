/*
 * 橘瓣 OrangeChat
 * 衍生自 RikkaHub (https://github.com/rikkahub/rikkahub)，原作者 RE
 * 本项目基于 GNU AGPL v3 开源，详见根目录 LICENSE 文件
 */

package me.rerere.rikkahub.ui.components.message

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.datetime.toJavaLocalDateTime
import me.rerere.ai.core.MessageRole
import me.rerere.ai.provider.Model
import me.rerere.ai.ui.UIMessage
import me.rerere.ai.ui.isEmptyUIMessage
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.data.model.Avatar
import me.rerere.rikkahub.ui.components.ui.AutoAIIcon
import me.rerere.rikkahub.ui.components.ui.UIAvatar
import me.rerere.rikkahub.ui.context.LocalSettings
import me.rerere.rikkahub.utils.toLocalString
import java.io.File

@Composable
private fun AvatarFrameOverlay(
    framePath: String,
    offsetX: Float,
    offsetY: Float,
    scale: Float,
    baseSize: Float,
) {
    if (framePath.isNotBlank() && File(framePath).exists()) {
        val context = LocalContext.current
        val bitmap = remember(framePath) {
            runCatching {
                BitmapFactory.decodeFile(framePath)?.asImageBitmap()
            }.getOrNull()
        }
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Avatar Frame",
                modifier = Modifier
                    .size((baseSize * scale).dp)
                    .offset(x = offsetX.dp, y = offsetY.dp),
                contentScale = ContentScale.Fit,
                alpha = 1f,
            )
        }
    }
}

@Composable
fun ChatMessageUserAvatar(
    message: UIMessage,
    avatar: Avatar,
    nickname: String,
    modifier: Modifier = Modifier,
) {
    val settings = LocalSettings.current[span_7](start_span)[span_7](end_span)
    if (message.role == MessageRole.USER && !message.parts.isEmptyUIMessage() && settings.displaySetting.showUserAvatar) {[span_8](start_span)[span_8](end_span)
        Row(
            modifier = modifier.padding(vertical = 4.dp), // 收紧垂直边距，与极简气泡流对齐
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),[span_9](start_span)[span_9](end_span)
            verticalAlignment = Alignment.CenterVertically,[span_10](start_span)[span_10](end_span)
        ) {
            Column(
                modifier = Modifier,[span_11](start_span)[span_11](end_span)
                horizontalAlignment = Alignment.End,[span_12](start_span)[span_12](end_span)
            ) {
                Text(
                    text = nickname.ifEmpty { stringResource(R.string.user_default_name) },[span_13](start_span)[span_13](end_span)
                    style = MaterialTheme.typography.titleSmall,[span_14](start_span)[span_14](end_span)
                    maxLines = 1,[span_15](start_span)[span_15](end_span)
                    color = LocalContentColor.current.copy(alpha = 0.85f),[span_16](start_span)[span_16](end_span)
                )
                if (settings.displaySetting.showDateBelowName) {[span_17](start_span)[span_17](end_span)
                    Text(
                        text = message.createdAt.toJavaLocalDateTime().toLocalString(),[span_18](start_span)[span_18](end_span)
                        style = MaterialTheme.typography.labelSmall,[span_19](start_span)[span_19](end_span)
                        color = LocalContentColor.current.copy(alpha = 0.5f), // 稍微调淡，提升高级空气感
                        maxLines = 1,[span_20](start_span)[span_20](end_span)
                    )
                }
            }
            Box(contentAlignment = Alignment.Center) {[span_21](start_span)[span_21](end_span)
                UIAvatar(
                    name = nickname,[span_22](start_span)[span_22](end_span)
                    modifier = Modifier.size(36.dp),[span_23](start_span)[span_23](end_span)
                    value = avatar,[span_24](start_span)[span_24](end_span)
                    loading = false,[span_25](start_span)[span_25](end_span)
                )
                AvatarFrameOverlay(
                    framePath = settings.displaySetting.userAvatarFramePath,[span_26](start_span)[span_26](end_span)
                    offsetX = settings.displaySetting.userAvatarFrameOffsetX,[span_27](start_span)[span_27](end_span)
                    offsetY = settings.displaySetting.userAvatarFrameOffsetY,[span_28](start_span)[span_28](end_span)
                    scale = settings.displaySetting.userAvatarFrameScale,[span_29](start_span)[span_29](end_span)
                    baseSize = 36f,[span_30](start_span)[span_30](end_span)
                )
            }
        }
    }
}

@Composable
fun ChatMessageAssistantAvatar(
    message: UIMessage,
    loading: Boolean,
    model: Model?,
    assistant: Assistant?,
    modifier: Modifier = Modifier,
) {
    val settings = LocalSettings.current[span_31](start_span)[span_31](end_span)
    val showIcon = settings.displaySetting.showModelIcon[span_32](start_span)[span_32](end_span)
    val useAssistantAvatar = assistant?.useAssistantAvatar == true[span_33](start_span)[span_33](end_span)
    if (message.role == MessageRole.ASSISTANT && (model != null || useAssistantAvatar)) {[span_34](start_span)[span_34](end_span)
        Row(
            modifier = modifier.padding(vertical = 4.dp), // 统一两侧 Padding 高度，使排版绝对对称
            horizontalArrangement = Arrangement.spacedBy(8.dp), // 统一间距为 8.dp，更内聚
            verticalAlignment = Alignment.CenterVertically,[span_35](start_span)[span_35](end_span)
        ) {
            if (useAssistantAvatar) {[span_36](start_span)[span_36](end_span)
                if (showIcon) {[span_37](start_span)[span_37](end_span)
                    Box(contentAlignment = Alignment.Center) {[span_38](start_span)[span_38](end_span)
                        UIAvatar(
                            name = assistant.name,[span_39](start_span)[span_39](end_span)
                            modifier = Modifier.size(36.dp), // 统一修改为 36.dp
                            value = assistant.avatar,[span_40](start_span)[span_40](end_span)
                            loading = loading,[span_41](start_span)[span_41](end_span)
                        )
                        AvatarFrameOverlay(
                            framePath = settings.displaySetting.aiAvatarFramePath,[span_42](start_span)[span_42](end_span)
                            offsetX = settings.displaySetting.aiAvatarFrameOffsetX,[span_43](start_span)[span_43](end_span)
                            offsetY = settings.displaySetting.aiAvatarFrameOffsetY,[span_44](start_span)[span_44](end_span)
                            scale = settings.displaySetting.aiAvatarFrameScale,[span_45](start_span)[span_45](end_span)
                            baseSize = 36f, // 同步改为 36f
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)[span_46](start_span)[span_46](end_span)
                ) {
                    if (settings.displaySetting.showModelName) {[span_47](start_span)[span_47](end_span)
                        Text(
                            text = assistant.name.ifEmpty { stringResource(R.string.assistant_page_default_assistant) },[span_48](start_span)[span_48](end_span)
                            style = MaterialTheme.typography.titleSmall, // 极简风建议改用标准 titleSmall，避免过度加粗
                            maxLines = 1,[span_49](start_span)[span_49](end_span)
                        )
                        if (settings.displaySetting.showDateBelowName) {[span_50](start_span)[span_50](end_span)
                            Text(
                                text = message.createdAt.toJavaLocalDateTime().toLocalString(),[span_51](start_span)[span_51](end_span)
                                style = MaterialTheme.typography.labelSmall, // 统一两侧的时间字体风格
                                color = LocalContentColor.current.copy(alpha = 0.5f),
                                maxLines = 1,[span_52](start_span)[span_52](end_span)
                            )
                        }
                    }
                }
            } else if (model != null) {[span_53](start_span)[span_53](end_span)
                if (showIcon) {[span_54](start_span)[span_54](end_span)
                    Box(contentAlignment = Alignment.Center) {[span_55](start_span)[span_55](end_span)
                        AutoAIIcon(
                            name = model.modelId,[span_56](start_span)[span_56](end_span)
                            modifier = Modifier.size(36.dp), // 统一修改为 36.dp
                            loading = loading[span_57](start_span)[span_57](end_span)
                        )
                        AvatarFrameOverlay(
                            framePath = settings.displaySetting.aiAvatarFramePath,[span_58](start_span)[span_58](end_span)
                            offsetX = settings.displaySetting.aiAvatarFrameOffsetX,[span_59](start_span)[span_59](end_span)
                            offsetY = settings.displaySetting.aiAvatarFrameOffsetY,[span_60](start_span)[span_60](end_span)
                            scale = settings.displaySetting.aiAvatarFrameScale,[span_61](start_span)[span_61](end_span)
                            baseSize = 36f, // 同步改为 36f
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)[span_62](start_span)[span_62](end_span)
                ) {
                    if (settings.displaySetting.showModelName) {[span_63](start_span)[span_63](end_span)
                        Text(
                            text = model.displayName,[span_64](start_span)[span_64](end_span)
                            style = MaterialTheme.typography.titleSmall, // 极简风建议改用标准 titleSmall
                        )
                        if (settings.displaySetting.showDateBelowName) {[span_65](start_span)[span_65](end_span)
                            Text(
                                text = message.createdAt.toJavaLocalDateTime().toLocalString(),[span_66](start_span)[span_66](end_span)
                                style = MaterialTheme.typography.labelSmall,[span_67](start_span)[span_67](end_span)
                                color = LocalContentColor.current.copy(alpha = 0.5f) // 统一时间半透明度
                            )
                        }
                    }
                }
            }
        }
    }
}
