import com.android.tools.lint.checks.HardcodedValuesDetector
import com.android.tools.lint.detector.api.*
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.psi.PsiElement
import org.jetbrains.android.inspections.lint.AndroidAddStringResourceQuickFix
import org.jetbrains.android.inspections.lint.AndroidLintInspectionBase
import org.jetbrains.android.util.AndroidBundle

class HardcodedDimensionsInspection : AndroidLintInspectionBase("Hardcoded dimensions", HardcodedDimensDetector.ISSUE) {

    override fun getShortName(): String {
        return "AndroidLintHardcodedDimension"
    }
}