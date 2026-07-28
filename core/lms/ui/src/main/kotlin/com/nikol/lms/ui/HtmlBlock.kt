package com.nikol.lms.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import javax.inject.Inject

@Immutable
sealed interface HtmlBlock {
    data class Text(val content: AnnotatedString) : HtmlBlock
    data class Heading(val content: AnnotatedString, val level: Int) : HtmlBlock
    data class Quote(val content: AnnotatedString) : HtmlBlock

    // Списки
    data class BulletList(val items: List<AnnotatedString>) : HtmlBlock
    data class OrderedList(val items: List<AnnotatedString>) : HtmlBlock

    //что то типо постраннее
    data class Code(val codeText: String, val language: String) : HtmlBlock
    object Divider : HtmlBlock
}

class HtmlToUiParser @Inject constructor() {

    fun parse(html: String): List<HtmlBlock> {
        val document = Jsoup.parseBodyFragment(html)
        val blocks = mutableListOf<HtmlBlock>()

        for (element in document.body().children()) {
            if (isTrashElement(element)) continue

            val block = when (element.tagName()) {
                "h1", "h2", "h3" -> parseHeading(element)
                "p" -> HtmlBlock.Text(parseInline(element))
                "ul" -> HtmlBlock.BulletList(parseList(element))
                "ol" -> HtmlBlock.OrderedList(parseList(element)) // Поддержка нумерованных списков
                "pre" -> parseCodeBlock(element)
                "blockquote" -> HtmlBlock.Quote(parseInline(element))
                "hr" -> HtmlBlock.Divider
                else -> HtmlBlock.Text(parseInline(element))
            }
            blocks.add(block)
        }
        return blocks
    }

    private fun parseList(element: Element): List<AnnotatedString> {
        return element.children()
            .filter { it.tagName() == "li" }
            .map { parseInline(it) }
    }

    private fun parseCodeBlock(element: Element): HtmlBlock.Code {
        val codeElement = element.selectFirst("code")

        return if (codeElement != null) {
            val language = codeElement.className().replace("language-", "").trim()
            HtmlBlock.Code(codeText = codeElement.wholeText(), language = language)
        } else {
            HtmlBlock.Code(codeText = element.wholeText(), language = "")
        }
    }

    private fun parseHeading(element: Element): HtmlBlock.Heading {
        val level = element.tagName().removePrefix("h").toIntOrNull() ?: 1
        val content = parseInline(element)
        return HtmlBlock.Heading(content, level)
    }

    private fun parseInline(element: Element): AnnotatedString {
        return buildAnnotatedString {
            element.childNodes().forEach { node ->
                when (node) {
                    is TextNode -> append(node.wholeText)
                    is Element -> {
                        when (node.tagName()) {
                            "strong", "b" -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(parseInline(node))
                            }

                            "i", "em" -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                append(parseInline(node))
                            }

                            "code" -> {
                                // Добавляем тег-маркер "CODE" (без цвета), чтобы потом покрасить его в UI
                                pushStringAnnotation("CODE", "")
                                withStyle(CodeSpanStyle) { append(node.text()) }
                                pop()
                            }

                            "a" -> {
                                pushStringAnnotation("URL", node.attr("href"))
                                withStyle(LinkSpanStyle) { append(parseInline(node)) }
                                pop()
                            }

                            "br" -> append("\n")
                            "span" -> append(parseInline(node))

                            // Поддержка вложенных маркированных списков внутри абзацев
                            "ul" -> {
                                append("\n")
                                node.children().filter { child -> child.tagName() == "li" }.forEach { li ->
                                    append("  • ")
                                    append(parseInline(li))
                                    append("\n")
                                }
                            }

                            // Поддержка вложенных пронумерованных списков внутри абзацев
                            "ol" -> {
                                append("\n")
                                node.children().filter { child -> child.tagName() == "li" }.forEachIndexed { index, li ->
                                    append("  ${index + 1}. ")
                                    append(parseInline(li))
                                    append("\n")
                                }
                            }

                            else -> append(parseInline(node))
                        }
                    }
                }
            }
        }
    }

    private fun isTrashElement(element: Element): Boolean {
        // Добавлены pre, ul, ol в белый список тегов, чтобы они не отбрасывались, если Jsoup посчитает их текст пустым
        return element.text().isBlank() && element.tagName() !in listOf("hr", "br", "img", "pre", "ul", "ol")
    }

    companion object {
        val CodeSpanStyle = SpanStyle(
            fontFamily = FontFamily.Monospace
        )
        val LinkSpanStyle = SpanStyle(
            textDecoration = TextDecoration.Underline
        )
    }
}