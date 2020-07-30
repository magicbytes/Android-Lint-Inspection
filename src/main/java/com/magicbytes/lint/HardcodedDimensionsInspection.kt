package com.magicbytes.lint

import com.android.tools.idea.lint.LintIdeIssueRegistry
import com.android.tools.lint.detector.api.Issue
import com.intellij.codeInspection.GlobalInspectionContext
import com.magicbytes.lint.HardcodedDimensDetector
import org.jetbrains.android.inspections.lint.AndroidLintInspectionBase

class HardcodedDimensionsInspection : AndroidLintInspectionBase("Hardcoded dimensions", HardcodedDimensDetector.ISSUE) {


    init {
        val registry = LintIdeIssueRegistry()
        val myIssue = registry.getIssue(HardcodedDimensDetector.ISSUE.id)
        if (myIssue == null) {
            val list = registry.issues as MutableList<Issue>
            list.add(HardcodedDimensDetector.ISSUE)
        }
    }

    override fun initialize(context: GlobalInspectionContext) {
        super.initialize(context)


    }

    override fun getShortName(): String {
        return "AndroidLintHardcodedDimension"
    }
}