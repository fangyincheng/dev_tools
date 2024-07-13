package com.fyc.dev_tools

import ai.grazie.utils.mpp.Base64
import ai.grazie.utils.mpp.URLEncoder
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridLayout
import java.nio.charset.Charset
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent


class TextTransToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val encodeWindow = EncodeWindow(toolWindow)
        val encodeContent = ContentFactory.getInstance().createContent(encodeWindow.getContent(), "编解码", false)
        toolWindow.contentManager.addContent(encodeContent, 0)
    }

    class EncodeWindow(toolWindow: ToolWindow) {
        var toolWindow: ToolWindow = toolWindow

        private val urlEncodeText = "Url编码";
        private val urlDecodeText = "Url解码";
        private val base64EncodeText = "Base64编码";
        private val base64DecodeText = "Base64解码";
        private val unicodeEncodeText = "Unicode编码";
        private val unicodeDecodeText = "Unicode解码";

        fun getContent(): JComponent {
            val jbPanel = JBPanel<JBPanel<*>>()
            jbPanel.layout = BorderLayout()

            // 功能控制区域
            val checkBoxJBPanel = JBPanel<JBPanel<*>>()
            val urlCheckBox = jbCheckBox("Url", true)
            checkBoxJBPanel.add(urlCheckBox)
            val base64CheckBox = jbCheckBox("Base64", true)
            checkBoxJBPanel.add(base64CheckBox)
            val unicodeCheckBox = jbCheckBox("Unicode", true)
            checkBoxJBPanel.add(unicodeCheckBox)
            jbPanel.add(checkBoxJBPanel, BorderLayout.NORTH)

            // 按钮控件
            val buttonJbPanel = JBPanel<JBPanel<*>>()
            buttonJbPanel.layout = GridLayout(2, 4)
            val urlEncode = jButton(urlEncodeText, JBColor.GREEN)
            buttonJbPanel.add(urlEncode)
            val urlDecode = jButton(urlDecodeText, JBColor.GREEN)
            buttonJbPanel.add(urlDecode)
            val base64Encode = jButton(base64EncodeText, JBColor.YELLOW)
            buttonJbPanel.add(base64Encode)
            val base64Decode = jButton(base64DecodeText, JBColor.YELLOW)
            buttonJbPanel.add(base64Decode)
            val unicodeEncode = jButton(unicodeEncodeText, JBColor.BLUE)
            buttonJbPanel.add(unicodeEncode)
            val unicodeDecode = jButton(unicodeDecodeText, JBColor.BLUE)
            buttonJbPanel.add(unicodeDecode)

            // 内容区域
            val contentJbPanel = JBPanel<JBPanel<*>>()
            contentJbPanel.layout = BoxLayout(contentJbPanel, BoxLayout.Y_AXIS)
            val input = JBTextArea()
            input.lineWrap = true
            input.wrapStyleWord = true
            input.margin = JBUI.insets(10)
            val result = JBTextArea()
            result.lineWrap = true
            result.wrapStyleWord = true
            result.margin = JBUI.insets(10)
            result.isEditable = false
            contentJbPanel.add(input)
            contentJbPanel.add(buttonJbPanel)
            contentJbPanel.add(result)
            jbPanel.add(contentJbPanel, BorderLayout.CENTER)

            // 监听处理
            urlCheckBox.addActionListener() {
                if (urlCheckBox.isSelected) {
                    buttonJbPanel.add(urlEncode)
                    buttonJbPanel.add(urlDecode)
                    toolWindow.hide()
                    toolWindow.show()
                } else {
                    buttonJbPanel.remove(urlEncode)
                    buttonJbPanel.remove(urlDecode)
                    toolWindow.hide()
                    toolWindow.show()
                }
            }
            urlEncode.addActionListener {
                result.text = URLEncoder.encode(input.text)
            }
            urlDecode.addActionListener {
                result.text = URLEncoder.decode(input.text)
            }

            base64CheckBox.addActionListener {
                if (base64CheckBox.isSelected) {
                    buttonJbPanel.add(base64Encode)
                    buttonJbPanel.add(base64Decode)
                    toolWindow.hide()
                    toolWindow.show()
                } else {
                    buttonJbPanel.remove(base64Encode)
                    buttonJbPanel.remove(base64Decode)
                    toolWindow.hide()
                    toolWindow.show()
                }
            }
            base64Encode.addActionListener {
                result.text = Base64.encode(input.text.toByteArray())
            }
            base64Decode.addActionListener {
                try {
                    result.text = Base64.decode(input.text).toString(Charset.forName("UTF-8"))
                } catch (e: Exception) {
                    result.text = e.stackTraceToString()
                }
            }

            unicodeCheckBox.addActionListener {
                if (unicodeCheckBox.isSelected) {
                    buttonJbPanel.add(unicodeEncode)
                    buttonJbPanel.add(unicodeDecode)
                    toolWindow.hide()
                    toolWindow.show()
                } else {
                    buttonJbPanel.remove(unicodeEncode)
                    buttonJbPanel.remove(unicodeDecode)
                    toolWindow.hide()
                    toolWindow.show()
                }
            }
            unicodeEncode.addActionListener {
                val sb = StringBuilder()
                for (ch in input.text.toCharArray()) {
                    sb.append("\\u").append(Integer.toHexString(ch.code))
                }
                result.text = sb.toString()
            }
            unicodeDecode.addActionListener() {
                var strArr: List<String> = input.text.split("\\\\u")
                if (strArr.size == 1) {
                    strArr = input.text.split("\\u")
                }
                val sb = java.lang.StringBuilder()
                for (i in 1 until strArr.size) {
                    val hexVal = strArr[i].toInt(16)
                    sb.append(hexVal.toChar())
                }
                result.text = sb.toString()
            }

            return jbPanel
        }

        private fun jButton(text: String, color: JBColor): JButton {
            val urlEncode = JButton(text)
            urlEncode.toolTipText = text
            urlEncode.foreground = color
            return urlEncode
        }

        private fun jbCheckBox(text: String, iselected: Boolean): JBCheckBox {
            val urlCheckBox = JBCheckBox(text)
            urlCheckBox.isSelected = iselected
            return urlCheckBox
        }
    }

}