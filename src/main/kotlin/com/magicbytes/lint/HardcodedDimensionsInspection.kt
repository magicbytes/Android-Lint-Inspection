package com.magicbytes.lint

import com.android.tools.idea.lint.common.AndroidLintInspectionBase
import com.android.tools.idea.lint.common.LintIdeIssueRegistry
import com.android.tools.lint.detector.api.Issue
import com.magicbytes.lint.HardcodedDimensDetector

class HardcodedDimensionsInspection : AndroidLintInspectionBase("Hardcoded dimensions", HardcodedDimensDetector.ISSUE) {


    init {
        val registry = LintIdeIssueRegistry()
        val myIssue = registry.getIssue(HardcodedDimensDetector.ISSUE.id)
        if (myIssue == null) {
            val list = registry.issues as MutableList<Issue>
            list.add(HardcodedDimensDetector.ISSUE)
        }
    }

    override fun getShortName(): String {
        return "AndroidLintHardcodedDimension"
    }
}