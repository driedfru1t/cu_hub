package com.nikol.calendar.data.remote

import org.intellij.lang.annotations.Language
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

internal object XPathParser {

    private val documentBuilderFactory = DocumentBuilderFactory.newInstance().apply {
        isNamespaceAware = true
    }

    private val xpathFactory = XPathFactory.newInstance()

    private fun document(xml: String) =
        documentBuilderFactory.newDocumentBuilder().parse(xml.byteInputStream())

    fun string(xml: String, query: String): String =
        xpathFactory.newXPath()
            .evaluate(query, document(xml), XPathConstants.STRING)
            .toString()
            .trim()

    fun nodes(xml: String, query: String): List<Node> {
        val nodeList = xpathFactory.newXPath().evaluate(
            query,
            document(xml),
            XPathConstants.NODESET
        ) as NodeList

        return List(nodeList.length) { nodeList.item(it) }
    }

    fun evaluate(node: Node, query: String): String =
        ((xpathFactory.newXPath()
            .evaluate(
                query,
                node,
                XPathConstants.STRING
            )) as String)
            .trim()
}

fun String.xpathString(@Language("XPath") query: String): String =
    XPathParser.string(this, query)

fun String.xpathNodes(@Language("XPath") query: String): List<Node> =
    XPathParser.nodes(this, query)

fun Node.evaluate(@Language("XPath") query: String): String =
    XPathParser.evaluate(this, query)