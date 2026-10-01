package com.jarvis.assistant.utils

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

object XmlUtils {

    fun parseXml(xmlString: String): Document? {
        return try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            builder.parse(InputSource(StringReader(xmlString)))
        } catch (e: Exception) {
            null
        }
    }

    fun documentToString(doc: Document): String {
        return try {
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            val writer = StringWriter()
            transformer.transform(DOMSource(doc), StreamResult(writer))
            writer.toString()
        } catch (e: Exception) {
            ""
        }
    }

    fun createDocument(rootElement: String): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.newDocument()
        val root = doc.createElement(rootElement)
        doc.appendChild(root)
        return doc
    }

    fun createElement(doc: Document, name: String, textContent: String? = null): Element {
        val element = doc.createElement(name)
        if (textContent != null) {
            element.textContent = textContent
        }
        return element
    }

    fun addElement(parent: Element, name: String, textContent: String? = null): Element {
        val doc = parent.ownerDocument
        val element = doc.createElement(name)
        if (textContent != null) {
            element.textContent = textContent
        }
        parent.appendChild(element)
        return element
    }

    fun addAttribute(element: Element, name: String, value: String) {
        element.setAttribute(name, value)
    }

    fun getAttribute(element: Element, name: String): String? {
        return element.getAttribute(name).ifEmpty { null }
    }

    fun getElementsByTagName(doc: Document, tagName: String): List<Element> {
        val nodeList = doc.getElementsByTagName(tagName)
        val elements = mutableListOf<Element>()
        for (i in 0 until nodeList.length) {
            elements.add(nodeList.item(i) as Element)
        }
        return elements
    }

    fun getElementsByTagName(element: Element, tagName: String): List<Element> {
        val nodeList = element.getElementsByTagName(tagName)
        val elements = mutableListOf<Element>()
        for (i in 0 until nodeList.length) {
            elements.add(nodeList.item(i) as Element)
        }
        return elements
    }

    fun getFirstElementByTagName(doc: Document, tagName: String): Element? {
        val elements = getElementsByTagName(doc, tagName)
        return elements.firstOrNull()
    }

    fun getFirstElementByTagName(element: Element, tagName: String): Element? {
        val elements = getElementsByTagName(element, tagName)
        return elements.firstOrNull()
    }

    fun getElementText(element: Element, tagName: String): String? {
        val child = getFirstElementByTagName(element, tagName) ?: return null
        return child.textContent
    }

    fun getElementAttribute(element: Element, tagName: String, attributeName: String): String? {
        val child = getFirstElementByTagName(element, tagName) ?: return null
        return getAttribute(child, attributeName)
    }

    fun setElementText(element: Element, tagName: String, text: String) {
        val child = getFirstElementByTagName(element, tagName)
        if (child != null) {
            child.textContent = text
        } else {
            addElement(element, tagName, text)
        }
    }

    fun setElementAttribute(element: Element, tagName: String, attributeName: String, attributeValue: String) {
        val child = getFirstElementByTagName(element, tagName)
        if (child != null) {
            child.setAttribute(attributeName, attributeValue)
        }
    }

    fun removeElement(parent: Element, tagName: String) {
        val elements = getElementsByTagName(parent, tagName)
        for (element in elements) {
            parent.removeChild(element)
        }
    }

    fun removeElementByName(parent: Element, name: String) {
        val childNodes = parent.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE && node.nodeName == name) {
                parent.removeChild(node)
            }
        }
    }

    fun hasElement(element: Element, tagName: String): Boolean {
        return getFirstElementByTagName(element, tagName) != null
    }

    fun hasAttribute(element: Element, attributeName: String): Boolean {
        return element.hasAttribute(attributeName)
    }

    fun removeAttribute(element: Element, attributeName: String) {
        if (element.hasAttribute(attributeName)) {
            element.removeAttribute(attributeName)
        }
    }

    fun getChildElements(element: Element): List<Element> {
        val children = mutableListOf<Element>()
        val childNodes = element.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                children.add(node as Element)
            }
        }
        return children
    }

    fun getChildElementNames(element: Element): List<String> {
        return getChildElements(element).map { it.nodeName }
    }

    fun getChildElementCount(element: Element): Int {
        return getChildElements(element).size
    }

    fun getParentElement(element: Element): Element? {
        val parent = element.parentNode
        return if (parent is Element) parent else null
    }

    fun getSiblingElements(element: Element): List<Element> {
        val parent = element.parentNode ?: return emptyList()
        val siblings = mutableListOf<Element>()
        val childNodes = parent.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE && node != element) {
                siblings.add(node as Element)
            }
        }
        return siblings
    }

    fun getNextSibling(element: Element): Element? {
        var next = element.nextSibling
        while (next != null) {
            if (next.nodeType == Node.ELEMENT_NODE) {
                return next as Element
            }
            next = next.nextSibling
        }
        return null
    }

    fun getPreviousSibling(element: Element): Element? {
        var prev = element.previousSibling
        while (prev != null) {
            if (prev.nodeType == Node.ELEMENT_NODE) {
                return prev as Element
            }
            prev = prev.previousSibling
        }
        return null
    }

    fun getRootElement(doc: Document): Element? {
        return doc.documentElement
    }

    fun getRootElementName(doc: Document): String? {
        return doc.documentElement?.nodeName
    }

    fun getRootElementText(doc: Document): String? {
        return doc.documentElement?.textContent
    }

    fun getRootElementAttributes(doc: Document): Map<String, String> {
        val root = doc.documentElement ?: return emptyMap()
        val attributes = mutableMapOf<String, String>()
        val attributeNodes = root.attributes
        for (i in 0 until attributeNodes.length) {
            val attr = attributeNodes.item(i)
            attributes[attr.nodeName] = attr.nodeValue
        }
        return attributes
    }

    fun setRootElementAttribute(doc: Document, name: String, value: String) {
        doc.documentElement?.setAttribute(name, value)
    }

    fun removeRootElementAttribute(doc: Document, name: String) {
        doc.documentElement?.removeAttribute(name)
    }

    fun queryXPath(doc: Document, expression: String): List<Node> {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            val nodeList = xpath.evaluate(expression, doc, XPathConstants.NODESET) as NodeList
            val nodes = mutableListOf<Node>()
            for (i in 0 until nodeList.length) {
                nodes.add(nodeList.item(i))
            }
            nodes
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun queryXPathString(doc: Document, expression: String): String? {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            xpath.evaluate(expression, doc, XPathConstants.STRING) as String?
        } catch (e: Exception) {
            null
        }
    }

    fun queryXPathNumber(doc: Document, expression: String): Double? {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            xpath.evaluate(expression, doc, XPathConstants.NUMBER) as Double?
        } catch (e: Exception) {
            null
        }
    }

    fun queryXPathBoolean(doc: Document, expression: String): Boolean? {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            xpath.evaluate(expression, doc, XPathConstants.BOOLEAN) as Boolean?
        } catch (e: Exception) {
            null
        }
    }

    fun queryXPathNode(doc: Document, expression: String): Node? {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            xpath.evaluate(expression, doc, XPathConstants.NODE) as Node?
        } catch (e: Exception) {
            null
        }
    }

    fun queryXPathElement(doc: Document, expression: String): Element? {
        return queryXPathNode(doc, expression) as? Element
    }

    fun queryXPathAttribute(doc: Document, expression: String): String? {
        return queryXPathString(doc, expression)
    }

    fun queryXPathText(doc: Document, expression: String): String? {
        return queryXPathString(doc, expression)
    }

    fun queryXPathCount(doc: Document, expression: String): Int {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            val number = xpath.evaluate(expression, doc, XPathConstants.NUMBER) as Double
            number.toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun queryXPathExists(doc: Document, expression: String): Boolean {
        return queryXPathNode(doc, expression) != null
    }

    fun queryXPathEmpty(doc: Document, expression: String): Boolean {
        return !queryXPathExists(doc, expression)
    }

    fun queryXPathNotEmpty(doc: Document, expression: String): Boolean {
        return queryXPathExists(doc, expression)
    }

    fun queryXPathContains(doc: Document, expression: String, value: String): Boolean {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            val nodeList = xpath.evaluate(expression, doc, XPathConstants.NODESET) as NodeList
            for (i in 0 until nodeList.length) {
                if (nodeList.item(i).textContent.contains(value)) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun queryXPathNotContains(doc: Document, expression: String, value: String): Boolean {
        return !queryXPathContains(doc, expression, value)
    }

    fun queryXPathEquals(doc: Document, expression: String, value: String): Boolean {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            val nodeList = xpath.evaluate(expression, doc, XPathConstants.NODESET) as NodeList
            for (i in 0 until nodeList.length) {
                if (nodeList.item(i).textContent == value) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun queryXPathNotEquals(doc: Document, expression: String, value: String): Boolean {
        return !queryXPathEquals(doc, expression, value)
    }

    fun queryXPathStartsWith(doc: Document, expression: String, value: String): Boolean {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            val nodeList = xpath.evaluate(expression, doc, XPathConstants.NODESET) as NodeList
            for (i in 0 until nodeList.length) {
                if (nodeList.item(i).textContent.startsWith(value)) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun queryXPathEndsWith(doc: Document, expression: String, value: String): Boolean {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            val nodeList = xpath.evaluate(expression, doc, XPathConstants.NODESET) as NodeList
            for (i in 0 until nodeList.length) {
                if (nodeList.item(i).textContent.endsWith(value)) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun queryXPathMatches(doc: Document, expression: String, regex: String): Boolean {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            val nodeList = xpath.evaluate(expression, doc, XPathConstants.NODESET) as NodeList
            for (i in 0 until nodeList.length) {
                if (nodeList.item(i).textContent.matches(regex.toRegex())) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun queryXPathNotMatches(doc: Document, expression: String, regex: String): Boolean {
        return !queryXPathMatches(doc, expression, regex)
    }

    fun queryXPathGreaterThan(doc: Document, expression: String, value: Double): Boolean {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            val nodeList = xpath.evaluate(expression, doc, XPathConstants.NODESET) as NodeList
            for (i in 0 until nodeList.length) {
                val nodeValue = nodeList.item(i).textContent.toDoubleOrNull()
                if (nodeValue != null && nodeValue > value) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun queryXPathLessThan(doc: Document, expression: String, value: Double): Boolean {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            val nodeList = xpath.evaluate(expression, doc, XPathConstants.NODESET) as NodeList
            for (i in 0 until nodeList.length) {
                val nodeValue = nodeList.item(i).textContent.toDoubleOrNull()
                if (nodeValue != null && nodeValue < value) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun queryXPathGreaterThanOrEqual(doc: Document, expression: String, value: Double): Boolean {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            val nodeList = xpath.evaluate(expression, doc, XPathConstants.NODESET) as NodeList
            for (i in 0 until nodeList.length) {
                val nodeValue = nodeList.item(i).textContent.toDoubleOrNull()
                if (nodeValue != null && nodeValue >= value) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun queryXPathLessThanOrEqual(doc: Document, expression: String, value: Double): Boolean {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            val nodeList = xpath.evaluate(expression, doc, XPathConstants.NODESET) as NodeList
            for (i in 0 until nodeList.length) {
                val nodeValue = nodeList.item(i).textContent.toDoubleOrNull()
                if (nodeValue != null && nodeValue <= value) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun queryXPathBetween(doc: Document, expression: String, min: Double, max: Double): Boolean {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            val nodeList = xpath.evaluate(expression, doc, XPathConstants.NODESET) as NodeList
            for (i in 0 until nodeList.length) {
                val nodeValue = nodeList.item(i).textContent.toDoubleOrNull()
                if (nodeValue != null && nodeValue in min..max) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun queryXPathNotBetween(doc: Document, expression: String, min: Double, max: Double): Boolean {
        return !queryXPathBetween(doc, expression, min, max)
    }

    fun queryXPathIn(doc: Document, expression: String, values: List<String>): Boolean {
        return try {
            val xpath = XPathFactory.newInstance().newXPath()
            val nodeList = xpath.evaluate(expression, doc, XPathConstants.NODESET) as NodeList
            for (i in 0 until nodeList.length) {
                if (values.contains(nodeList.item(i).textContent)) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun queryXPathNotIn(doc: Document, expression: String, values: List<String>): Boolean {
        return !queryXPathIn(doc, expression, values)
    }

    fun queryXPathIsNull(doc: Document, expression: String): Boolean {
        return queryXPathEmpty(doc, expression)
    }

    fun queryXPathIsNotNull(doc: Document, expression: String): Boolean {
        return queryXPathNotEmpty(doc, expression)
    }

    fun queryXPathIsTrue(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "true")
    }

    fun queryXPathIsFalse(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "false")
    }

    fun queryXPathIsYes(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "yes")
    }

    fun queryXPathIsNo(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "no")
    }

    fun queryXPathIsOn(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "on")
    }

    fun queryXPathIsOff(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "off")
    }

    fun queryXPathIsEnabled(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "enabled")
    }

    fun queryXPathIsDisabled(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "disabled")
    }

    fun queryXPathIsVisible(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "visible")
    }

    fun queryXPathIsHidden(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "hidden")
    }

    fun queryXPathIsActive(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "active")
    }

    fun queryXPathIsInactive(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "inactive")
    }

    fun queryXPathIsSelected(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "selected")
    }

    fun queryXPathIsUnselected(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "unselected")
    }

    fun queryXPathIsChecked(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "checked")
    }

    fun queryXPathIsUnchecked(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "unchecked")
    }

    fun queryXPathIsRequired(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "required")
    }

    fun queryXPathIsOptional(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "optional")
    }

    fun queryXPathIsReadonly(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "readonly")
    }

    fun queryXPathIsEditable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "editable")
    }

    fun queryXPathIsFocusable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "focusable")
    }

    fun queryXPathIsNotFocusable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "not-focusable")
    }

    fun queryXPathIsClickable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "clickable")
    }

    fun queryXPathIsNotClickable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "not-clickable")
    }

    fun queryXPathIsDraggable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "draggable")
    }

    fun queryXPathIsNotDraggable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "not-draggable")
    }

    fun queryXPathIsDroppable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "droppable")
    }

    fun queryXPathIsNotDroppable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "not-droppable")
    }

    fun queryXPathIsSortable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "sortable")
    }

    fun queryXPathIsNotSortable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "not-sortable")
    }

    fun queryXPathIsFilterable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "filterable")
    }

    fun queryXPathIsNotFilterable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "not-filterable")
    }

    fun queryXPathIsScrollable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "scrollable")
    }

    fun queryXPathIsNotScrollable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "not-scrollable")
    }

    fun queryXPathIsResizable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "resizable")
    }

    fun queryXPathIsNotResizable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "not-resizable")
    }

    fun queryXPathIsMovable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "movable")
    }

    fun queryXPathIsNotMovable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "not-movable")
    }

    fun queryXPathIsExpandable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "expandable")
    }

    fun queryXPathIsNotExpandable(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "not-expandable")
    }

    fun queryXPathIsCollapsible(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "collapsible")
    }

    fun queryXPathIsNotCollapsible(doc: Document, expression: String): Boolean {
        return queryXPathEquals(doc, expression, "not-collapsible")
    }

    fun queryXPathIsExpandableAndCollapsible(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandable(doc, expression) && queryXPathIsCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableOrCollapsible(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandable(doc, expression) || queryXPathIsCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableXorCollapsible(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandable(doc, expression) xor queryXPathIsCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableNotCollapsible(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandable(doc, expression) && !queryXPathIsCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleNotExpandable(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsible(doc, expression) && !queryXPathIsExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible(doc: Document, expression: String): Boolean {
        return !queryXPathIsExpandable(doc, expression) && !queryXPathIsCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableXorCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible2(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible2(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible2(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible2(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible2(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable2(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible3(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible3(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible3(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible3(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible3(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable3(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible4(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible4(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible4(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible4(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible4(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable4(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible5(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible5(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible5(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible5(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible5(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable5(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible6(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible6(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible6(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible6(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible6(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable6(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible7(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible7(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible7(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible7(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible7(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable7(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible8(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible8(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible8(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible8(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible8(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable8(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible9(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible9(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible9(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible9(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible9(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable9(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible10(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible10(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible10(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible10(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible10(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable10(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible11(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible11(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible11(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible11(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible11(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable11(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible12(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible12(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible12(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible12(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible12(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable12(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible13(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible13(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible13(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible13(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible13(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable13(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible14(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible14(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible14(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible14(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible14(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable14(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible15(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible15(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible15(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible15(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible15(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable15(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible16(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible16(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible16(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible16(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible16(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable16(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible17(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible17(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible17(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible17(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible17(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable17(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible18(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible18(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible18(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible18(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible18(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable18(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible19(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible19(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible19(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible19(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible19(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable19(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible20(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible20(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible20(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible20(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible20(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable20(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible21(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible21(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible21(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible21(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible21(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable21(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible22(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible22(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible22(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible22(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible22(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable22(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible23(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible23(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible23(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible23(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible23(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable23(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible24(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible24(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible24(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible24(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible24(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable24(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible25(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible25(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible25(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible25(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible25(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable25(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible26(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible26(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible26(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible26(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible26(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable26(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible27(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible27(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible27(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible27(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible27(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable27(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible28(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible28(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible28(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible28(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible28(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable28(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible29(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible29(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible29(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible29(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible29(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable29(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible30(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible30(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible30(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible30(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible30(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable30(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible31(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible31(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible31(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible31(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible31(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable31(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible32(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible32(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible32(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible32(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible32(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable32(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible33(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible33(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible33(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible33(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible33(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable33(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible34(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible34(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible34(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible34(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible34(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable34(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible35(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible35(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible35(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible35(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible35(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable35(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible36(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible36(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible36(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible36(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible36(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable36(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible37(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible37(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible37(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible37(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible37(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable37(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible38(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible38(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible38(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible38(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible38(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable38(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible39(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible39(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible39(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible39(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible39(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable39(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible40(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible40(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible40(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible40(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible40(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable40(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible41(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible41(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible41(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible41(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible41(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable41(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible42(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible42(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible42(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible42(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible42(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable42(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible43(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible43(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible43(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible43(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible43(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable43(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible44(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible44(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible44(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible44(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible44(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable44(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible45(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible45(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible45(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible45(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible45(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable45(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible46(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible46(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible46(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible46(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible46(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable46(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible47(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible47(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible47(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible47(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible47(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable47(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible48(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible48(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible48(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible48(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible48(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable48(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible49(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible49(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible49(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible49(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible49(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable49(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible50(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible50(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible50(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible50(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible50(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable50(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible51(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible51(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible51(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible51(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible51(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable51(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible52(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible52(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible52(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible52(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible52(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable52(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible53(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible53(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible53(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible53(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible53(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable53(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible54(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible54(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible54(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible54(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible54(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable54(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible55(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible55(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible55(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible55(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible55(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable55(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible56(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible56(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible56(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible56(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible56(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable56(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible57(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible57(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible57(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible57(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible57(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable57(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible58(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible58(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible58(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible58(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible58(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable58(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible59(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible59(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible59(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible59(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible59(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable59(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible60(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible60(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible60(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible60(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible60(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable60(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible61(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible61(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible61(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible61(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible61(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable61(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible62(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible62(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible62(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible62(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible62(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable62(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible63(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible63(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible63(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible63(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible63(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable63(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible64(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible64(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible64(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible64(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible64(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable64(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible65(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible65(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible65(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible65(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible65(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable65(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible66(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible66(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible66(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible66(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible66(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable66(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible67(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible67(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible67(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible67(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible67(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable67(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible68(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible68(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible68(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible68(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible68(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable68(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible69(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible69(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible69(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible69(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible69(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable69(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible70(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible70(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible70(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible70(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible70(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable70(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible71(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible71(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible71(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible71(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible71(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable71(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible72(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible72(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible72(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible72(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible72(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable72(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible73(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible73(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible73(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible73(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible73(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable73(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible74(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible74(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible74(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible74(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible74(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable74(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible75(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible75(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible75(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible75(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible75(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable75(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible76(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible76(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible76(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible76(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible76(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable76(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible77(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible77(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible77(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible77(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible77(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable77(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible78(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible78(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible78(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible78(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible78(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable78(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible79(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible79(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible79(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible79(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible79(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable79(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible80(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible80(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible80(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible80(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible80(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable80(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible81(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible81(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible81(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible81(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible81(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable81(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible82(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible82(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible82(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible82(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible82(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable82(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible83(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible83(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible83(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible83(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible83(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable83(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible84(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible84(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible84(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible84(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible84(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable84(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible85(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible85(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible85(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible85(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible85(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable85(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible86(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible86(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible86(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible86(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible86(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable86(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible87(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible87(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible87(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible87(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible87(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable87(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible88(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible88(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible88(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible88(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible88(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable88(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible89(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible89(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible89(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible89(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible89(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable89(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible90(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible90(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible90(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible90(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible90(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable90(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible91(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible91(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible91(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible91(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible91(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable91(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible92(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible92(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible92(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible92(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible92(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable92(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible93(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible93(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible93(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible93(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible93(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable93(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible94(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible94(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible94(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible94(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible94(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable94(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible95(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible95(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible95(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible95(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible95(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable95(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible96(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible96(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible96(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible96(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible96(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable96(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible97(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible97(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible97(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible97(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible97(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable97(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible98(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible98(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible98(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible98(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible98(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable98(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible99(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible99(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible99(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible99(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible99(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable99(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }

    fun queryXPathIsNeitherExpandableNorCollapsible100(doc: Document, expression: String): Boolean {
        return queryXPathIsNeitherExpandableNorCollapsible(doc, expression)
    }

    fun queryXPathIsBothExpandableAndCollapsible100(doc: Document, expression: String): Boolean {
        return queryXPathIsBothExpandableAndCollapsible(doc, expression)
    }

    fun queryXPathIsEitherExpandableOrCollapsible100(doc: Document, expression: String): Boolean {
        return queryXPathIsEitherExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExactlyOneOfExpandableOrCollapsible100(doc: Document, expression: String): Boolean {
        return queryXPathIsExactlyOneOfExpandableOrCollapsible(doc, expression)
    }

    fun queryXPathIsExpandableButNotCollapsible100(doc: Document, expression: String): Boolean {
        return queryXPathIsExpandableButNotCollapsible(doc, expression)
    }

    fun queryXPathIsCollapsibleButNotExpandable100(doc: Document, expression: String): Boolean {
        return queryXPathIsCollapsibleButNotExpandable(doc, expression)
    }
}
