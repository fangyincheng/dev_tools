package com.fyc.dev_tools

import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JComponent

class WorkWindow(toolWindow: ToolWindow) {
    var toolWindow = toolWindow

    val checkBoxJBPanel = JBPanel<JBPanel<*>>()
    val buttonJbPanel = JBPanel<JBPanel<*>>()
    val input = JBTextArea()
    val result = JBTextArea()

    fun addCheckBox(text: String, iselected: Boolean): JBCheckBox {
        val checkBox = JBCheckBox(text)
        checkBox.isSelected = iselected
        checkBoxJBPanel.add(checkBox)

        return checkBox
    }

    fun setButtonGrid(rows: Int, cols: Int) {
        buttonJbPanel.layout = GridLayout(rows, cols)
    }

    fun addButton(text: String, color: JBColor): JButton {
        val button = JButton(text)
        button.toolTipText = text
        button.foreground = color
        buttonJbPanel.add(button)
        return button
    }


    fun addButton(button: JButton) {
        buttonJbPanel.add(button)
    }


    fun removeButton(button: JButton) {
        buttonJbPanel.remove(button)
    }

    fun getInput(): String {
        return input.text
    }

    fun setResult(text: String) {
        result.text = text
    }

    fun getContent(): JComponent {
        val jbPanel = JBPanel<JBPanel<*>>()
        jbPanel.layout = BorderLayout()

        // 功能控制区域
        jbPanel.add(checkBoxJBPanel, BorderLayout.NORTH)

        // 按钮控件

        // 内容区域
        val contentJbPanel = JBPanel<JBPanel<*>>()
        val layout = GridBagLayout()
        contentJbPanel.layout = layout
        val constraints = GridBagConstraints()

        input.lineWrap = true
        input.margin = JBUI.insets(10)
        val inputScroll = JBScrollPane(input)

        result.lineWrap = true
        result.margin = JBUI.insets(10)
        result.isEditable = false
        val resultScroll = JBScrollPane(result)

        constraints.gridx = 0
        constraints.gridy = 0
        constraints.gridwidth = 1; // 列宽度
        constraints.gridheight = 1; // 行高度
        constraints.weightx = 1.0; // 水平百分比
        constraints.weighty = 0.49; // 垂直百分比
        constraints.fill = GridBagConstraints.BOTH;
        layout.setConstraints(inputScroll, constraints)
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 0.01;
        layout.setConstraints(buttonJbPanel, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1.0;
        constraints.weighty = 0.49;
        layout.setConstraints(resultScroll, constraints);

        contentJbPanel.add(inputScroll)
        contentJbPanel.add(buttonJbPanel)
        contentJbPanel.add(resultScroll)
        jbPanel.add(contentJbPanel, BorderLayout.CENTER)

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