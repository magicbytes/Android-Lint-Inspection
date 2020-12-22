package com.magicbytes.lint.viewBinding

import com.google.common.base.CaseFormat
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.intentions.SelfTargetingIntention
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory

class ConvertViewBindingIntention : SelfTargetingIntention<KtClass>(KtClass::class.java, "Convert to ViewBinding"),
    LocalQuickFix {

    override fun applyTo(element: KtClass, editor: Editor?) {
        when (element.superTypeListEntries[0].typeReference?.text.orEmpty()) {
            "AppCompatActivity" -> {
                ConvertViewBindingActivity(element).convert()
            }
        }
    }

    override fun isApplicableTo(element: KtClass, caretOffset: Int): Boolean {
        val supportedTypesToApplyIntention = listOf("AppCompatActivity", "Fragment", "FrameLayout")
        val subclassName = element.superTypeListEntries[0].typeReference?.text.orEmpty()
        return supportedTypesToApplyIntention.contains(subclassName)
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        TODO("Not yet implemented")
    }
}