package com.servicebio.compose.application.component

import android.util.Log
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

// Regex containing the syntax tokens

val symbolPattern = Regex(
    """(https?://[^\s\t\n]+)|
([a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(?::\d+)?(?:/[^\s\t\n]*)?)|
(`[^`]+`)|
(@\w+)|
(\*[\w]+\*)|
(_[\w]+_)|
(~[\w]+~)|
(\+?\d{1,4}[\s-]?\(?\d{2,4}\)?[\s-]?\d{5,11})|
([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,})""".trimMargin(),
    RegexOption.COMMENTS
)

// Accepted annotations for the ClickableTextWrapper
enum class SymbolAnnotationType {
    PERSON,
    LINK,
    PHONE,
    EMAIL;

    companion object {

        fun fromName(name: String): SymbolAnnotationType {
            return when (name) {
                PERSON.name -> PERSON
                LINK.name -> LINK
                else -> LINK
            }
        }
    }
}
typealias AnnotatedLink = AnnotatedString.Range<String>
// Pair returning styled content and annotation for ClickableText when matching syntax token
typealias SymbolAnnotation = Pair<AnnotatedString, AnnotatedLink?>

/**
 * Format a message following Markdown-lite syntax
 * | @username -> bold, primary color and clickable element
 * | http(s)://... -> clickable link, opening it into the browser
 * | *bold* -> bold
 * | _italic_ -> italic
 * | ~strikethrough~ -> strikethrough
 * | `MyClass.myMethod` -> inline code styling
 *
 * @param text contains message to be parsed
 * @return AnnotatedString with annotations used inside the ClickableText wrapper
 */
@Composable
fun messageFormatter(text: String, primary: Boolean): AnnotatedString {
    val tokens = symbolPattern.findAll(text)

    return buildAnnotatedString {

        var cursorPosition = 0

        val codeSnippetBackground =
            if (primary) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.surface
            }

        for (token in tokens) {
            append(text.slice(cursorPosition until token.range.first))

            val (annotatedString, stringAnnotation) = getSymbolAnnotation(
                matchResult = token,
                colorScheme = MaterialTheme.colorScheme,
                primary = primary,
                codeSnippetBackground = codeSnippetBackground,
            )

            append(annotatedString)

            if (stringAnnotation != null) {
                val (item, start, end, tag) = stringAnnotation
                Log.d("TAG", "MessageText: messageFormatter -> start = $start ; end = $end")

                addStringAnnotation(tag = tag, start = start, end = end, annotation = item)
            }
            cursorPosition = token.range.last + 1
        }

        if (!tokens.none()) {
            append(text.slice(cursorPosition..text.lastIndex))
        } else {
            append(text)
        }
    }
}

@Composable
private fun AnnotatedString.Builder.AddLinkAnnotation(
    tag: SymbolAnnotationType,
    text: String,
    start: Int,
    end: Int,
    onClick: (SymbolAnnotationType, String) -> Unit
) {
    val spanStyle = TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
    addLink(
        LinkAnnotation.Clickable(
            tag.name,
            styles = spanStyle,
            linkInteractionListener = null
        ), start, end
    )
}

/**
 * Map regex matches found in a message with supported syntax symbols
 *
 * @param matchResult is a regex result matching our syntax symbols
 * @return pair of AnnotatedString with annotation (optional) used inside the ClickableText wrapper
 */
private fun getSymbolAnnotation(
    matchResult: MatchResult,
    colorScheme: ColorScheme,
    primary: Boolean,
    codeSnippetBackground: Color,
): SymbolAnnotation {
    matchResult.groups.forEachIndexed { index, group ->
        if (group != null && index > 0) {
            return when (index) {
                1, 2 -> SymbolAnnotation(
                    AnnotatedString(
                        text = matchResult.value,
                        spanStyle = SpanStyle(
                            color = if (primary) colorScheme.inversePrimary else colorScheme.primary,
                        ),
                    ),
                    AnnotatedLink(
                        item = matchResult.value,
                        start = matchResult.range.first,
                        end = matchResult.range.last + 1,
                        tag = SymbolAnnotationType.LINK.name,
                    ),
                )

                3 -> SymbolAnnotation(
                    AnnotatedString(
                        text = matchResult.value.trim('`'),
                        spanStyle = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            background = codeSnippetBackground,
                            baselineShift = BaselineShift(0.2f),
                        ),
                    ),
                    null,
                )

                4 -> SymbolAnnotation(
                    AnnotatedString(
                        text = matchResult.value,
                        spanStyle = SpanStyle(
                            color = if (primary) colorScheme.inversePrimary else colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        ),
                    ),
                    AnnotatedLink(
                        item = matchResult.value,
                        start = matchResult.range.first,
                        end = matchResult.range.last + 1,
                        tag = SymbolAnnotationType.PERSON.name,
                    ),
                )

                5 -> SymbolAnnotation(
                    AnnotatedString(
                        text = matchResult.value.trim('*'),
                        spanStyle = SpanStyle(fontWeight = FontWeight.Bold),
                    ),
                    null,
                )

                6 -> SymbolAnnotation(
                    AnnotatedString(
                        text = matchResult.value.trim('_'),
                        spanStyle = SpanStyle(fontStyle = FontStyle.Italic),
                    ),
                    null,
                )

                7 -> SymbolAnnotation(
                    AnnotatedString(
                        text = matchResult.value.trim('~'),
                        spanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough),
                    ),
                    null,
                )

                8 -> SymbolAnnotation(
                    AnnotatedString(
                        text = matchResult.value,
                        spanStyle = SpanStyle(
                            color = if (primary) colorScheme.inversePrimary else colorScheme.primary,
                        ),
                    ),
                    AnnotatedLink(
                        item = matchResult.value,
                        start = matchResult.range.first,
                        end = matchResult.range.last + 1,
                        tag = SymbolAnnotationType.PHONE.name,
                    ),
                )

                9 -> SymbolAnnotation(
                    AnnotatedString(
                        text = matchResult.value,
                        spanStyle = SpanStyle(
                            color = if (primary) colorScheme.inversePrimary else colorScheme.primary,
                        ),
                    ),
                    AnnotatedLink(
                        item = matchResult.value,
                        start = matchResult.range.first,
                        end = matchResult.range.last + 1,
                        tag = SymbolAnnotationType.EMAIL.name,
                    ),
                )

                else -> SymbolAnnotation(AnnotatedString(matchResult.value), null)
            }
        }
    }

    return SymbolAnnotation(AnnotatedString(matchResult.value), null)
}
