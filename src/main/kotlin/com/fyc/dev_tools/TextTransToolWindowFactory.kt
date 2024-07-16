package com.fyc.dev_tools

import ai.grazie.utils.mpp.Base64
import ai.grazie.utils.mpp.URLEncoder
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.content.ContentFactory
import java.nio.charset.Charset
import java.util.*
import javax.swing.JButton


class TextTransToolWindowFactory : ToolWindowFactory {

    private val urlEncodeText = "Url编码";
    private val urlDecodeText = "Url解码";
    private val base64EncodeText = "Base64编码";
    private val base64DecodeText = "Base64解码";
    private val unicodeEncodeText = "Unicode编码";
    private val unicodeDecodeText = "Unicode解码";

    private val formatText = "格式化";
    private val compressText = "压缩";
    private val sortText = "排序";
    private val escSortText = "升序";
    private val descSortText = "降序";

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val encodeWindow = WorkWindow(toolWindow)
        encodeWindowSetting(encodeWindow, toolWindow)
        val encodeContent = ContentFactory.getInstance().createContent(encodeWindow.getContent(), "编解码", false)
        toolWindow.contentManager.addContent(encodeContent, 0)

        val jsonWindow = WorkWindow(toolWindow)
        jsonWindowSetting(jsonWindow, toolWindow)

        val jsonContent = ContentFactory.getInstance().createContent(jsonWindow.getContent(), "JSON", false)
        toolWindow.contentManager.addContent(jsonContent, 1)
    }

    private fun jsonWindowSetting(jsonWindow: WorkWindow, toolWindow: ToolWindow) {
        val formatCheckBox = jsonWindow.addCheckBox(formatText, true)
        val compressCheckBox = jsonWindow.addCheckBox(compressText, true)
        val sortCheckBox = jsonWindow.addCheckBox(sortText, true)
        jsonWindow.setButtonGrid(1, 2)
        val formatButton = jsonWindow.addButton(formatText, JBColor.GREEN)
        val compressButton = jsonWindow.addButton(compressText, JBColor.YELLOW)
        val escSortButton = jsonWindow.addButton(escSortText, JBColor.BLUE)
        val descSortButton = jsonWindow.addButton(descSortText, JBColor.BLUE)
        addListener(toolWindow, jsonWindow, formatCheckBox, formatButton, fun(value: String): String {
            val typeRef
                    : TypeReference<Map<String, Any>> = object : TypeReference<Map<String, Any>>() {}
            val mapper = ObjectMapper()
            val objectMap: Map<String, Any>
            try {
                objectMap = mapper.readValue(value, typeRef)
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMap)
            } catch (e: Exception) {
                return e.stackTraceToString()
            }
        })
        addListener(toolWindow, jsonWindow, compressCheckBox, compressButton, fun(value: String): String {
            val typeRef
                    : TypeReference<Map<String, Any>> = object : TypeReference<Map<String, Any>>() {}
            val mapper = ObjectMapper()
            val objectMap: Map<String, Any>
            try {
                objectMap = mapper.readValue(value, typeRef)
                return mapper.writeValueAsString(objectMap)
            } catch (e: Exception) {
                return e.stackTraceToString()
            }
        })
        addListener(toolWindow, jsonWindow, sortCheckBox, escSortButton, descSortButton, fun(aVal: String): String {
            val typeRef
                    : TypeReference<TreeMap<String, Any>> = object : TypeReference<TreeMap<String, Any>>() {}
            val mapper = ObjectMapper()
            val objectMap: TreeMap<String, Any>
            try {
                objectMap = mapper.readValue(aVal, typeRef)
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMap)
            } catch (e: Exception) {
                return e.stackTraceToString()
            }
        }, fun(bVal: String): String {
            val typeRef
                    : TypeReference<TreeMap<String, Any>> = object : TypeReference<TreeMap<String, Any>>() {}
            val mapper = ObjectMapper()
            val objectMap: TreeMap<String, Any>
            try {
                objectMap = mapper.readValue(bVal, typeRef)
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMap.descendingMap())
            } catch (e: Exception) {
                return e.stackTraceToString()
            }
        })
    }

    private fun encodeWindowSetting(encodeWindow: WorkWindow, toolWindow: ToolWindow) {
        val urlCheckBox = encodeWindow.addCheckBox("Url", true)
        val base64CheckBox = encodeWindow.addCheckBox("Base64", true)
        val unicodeCheckBox = encodeWindow.addCheckBox("Unicode", true)
        encodeWindow.setButtonGrid(2, 4)
        val urlEncode = encodeWindow.addButton(urlEncodeText, JBColor.GREEN)
        val urlDecode = encodeWindow.addButton(urlDecodeText, JBColor.GREEN)
        val base64Encode = encodeWindow.addButton(base64EncodeText, JBColor.YELLOW)
        val base64Decode = encodeWindow.addButton(base64DecodeText, JBColor.YELLOW)
        val unicodeEncode = encodeWindow.addButton(unicodeEncodeText, JBColor.BLUE)
        val unicodeDecode = encodeWindow.addButton(unicodeDecodeText, JBColor.BLUE)
        addListener(toolWindow, encodeWindow, urlCheckBox, urlEncode, urlDecode, fun(enVal: String): String {
            return URLEncoder.encode(enVal)
        }, fun(deVal: String): String {
            return URLEncoder.decode(deVal)
        })
        addListener(toolWindow, encodeWindow, base64CheckBox, base64Encode, base64Decode, fun(enVal: String): String {
            return Base64.encode(enVal.toByteArray())
        }, fun(deVal: String): String {
            try {
                return Base64.decode(deVal).toString(Charset.forName("UTF-8"))
            } catch (e: Exception) {
                return e.stackTraceToString()
            }
        })
        addListener(
            toolWindow,
            encodeWindow,
            unicodeCheckBox,
            unicodeEncode,
            unicodeDecode,
            fun(enVal: String): String {
                val sb = StringBuilder()
                for (ch in enVal.toCharArray()) {
                    sb.append("\\u").append(Integer.toHexString(ch.code))
                }
                return sb.toString()
            },
            fun(deVal: String): String {
                var strArr: List<String> = deVal.split("\\\\u")
                if (strArr.size == 1) {
                    strArr = deVal.split("\\u")
                }
                val sb = java.lang.StringBuilder()
                for (i in 1 until strArr.size) {
                    val hexVal = strArr[i].toInt(16)
                    sb.append(hexVal.toChar())
                }
                return sb.toString()
            })
    }

    fun addListener(
        toolWindow: ToolWindow,
        workWindow: WorkWindow,
        checkBox: JBCheckBox,
        aButton: JButton,
        bButton: JButton,
        aFunction: (String) -> String,
        bFunction: (String) -> String
    ) {
        checkBox.addActionListener {
            if (checkBox.isSelected) {
                workWindow.addButton(aButton)
                workWindow.addButton(bButton)
                toolWindow.hide()
                toolWindow.show()
            } else {
                workWindow.removeButton(aButton)
                workWindow.removeButton(bButton)
                toolWindow.hide()
                toolWindow.show()
            }
        }
        aButton.addActionListener {
            workWindow.setResult(aFunction(workWindow.getInput()))
        }
        bButton.addActionListener {
            workWindow.setResult(bFunction(workWindow.getInput()))
        }
    }

    fun addListener(
        toolWindow: ToolWindow,
        workWindow: WorkWindow,
        checkBox: JBCheckBox,
        button: JButton,
        function: (String) -> String
    ) {
        checkBox.addActionListener {
            if (checkBox.isSelected) {
                workWindow.addButton(button)
                toolWindow.hide()
                toolWindow.show()
            } else {
                workWindow.removeButton(button)
                toolWindow.hide()
                toolWindow.show()
            }
        }
        button.addActionListener {
            workWindow.setResult(function(workWindow.getInput()))
        }
    }

}