package com.magicbytes.lint.viewBinding

import com.google.common.base.CaseFormat
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.idea.references.SyntheticPropertyAccessorReference
import org.jetbrains.kotlin.psi.*

class ConvertViewBindingActivity(private val element: KtClass) {
    fun convert() {
        val allMethods = element.body?.functions.orEmpty()
        val onCreateMethod = allMethods.first { it.name == "onCreate" }
        val setContentExpression = onCreateMethod.bodyBlockExpression?.statements.orEmpty().firstOrNull {
            it.text.startsWith("setContentView(R.layout.")
        } as? KtCallExpression ?: return

        val nameLayout = setContentExpression.text.substringAfter("R.layout.").removeSuffix(")").trim()
        val nameLayoutAsClass = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, nameLayout)
        val fullBindingName = nameLayoutAsClass + "Binding"

        createProperty(fullBindingName)
        addBindingOnCreate(setContentExpression, fullBindingName)
        replaceAllSyntheticAccessWithBinding()
    }

    private fun createProperty(fullBindingName: String) {
        val factory = KtPsiFactory(element)
        val property = factory.createProperty("private lateinit var binding: $fullBindingName")
        element.body?.addAfter(property, element.body?.firstChild)
    }

    private fun addBindingOnCreate(
        setContentExpression: KtCallExpression,
        fullBindingName: String
    ) {
        val factory = KtPsiFactory(setContentExpression)

        val thirdLineExpression = factory.createExpression("setContentView(view)")
        val secondLineExpression = factory.createProperty("val view = binding.root")
        val firstLineExpression = factory.createExpression("binding = ${fullBindingName}.inflate(layoutInflater)")

        firstLineExpression.addAfter(factory.createWhiteSpace("\n"), firstLineExpression.lastChild)
        firstLineExpression.addAfter(secondLineExpression, firstLineExpression.lastChild)
        firstLineExpression.addAfter(factory.createWhiteSpace("\n"), firstLineExpression.lastChild)
        firstLineExpression.addAfter(thirdLineExpression, firstLineExpression.lastChild)

        setContentExpression.replace(firstLineExpression)
        CodeStyleManager.getInstance(setContentExpression.project).reformat(setContentExpression)
    }

    private fun replaceAllSyntheticAccessWithBinding() {
        element.accept(object : KtTreeVisitorVoid() {
            override fun visitBinaryExpression(expression: KtBinaryExpression) {
                super.visitBinaryExpression(expression)

                if (expression.isSyntheticAccessor) {
                    expression.convertToBindingCall()
                }
            }
        })
    }

    private val KtBinaryExpression.isSyntheticAccessor: Boolean
        get() {
            val dotExpression = left as? KtDotQualifiedExpression ?: return false
            val nameExpression = dotExpression.receiverExpression as? KtNameReferenceExpression ?: return false
            return nameExpression.references.any { it is SyntheticPropertyAccessorReference }
        }

    private fun KtBinaryExpression.convertToBindingCall() {
        val newBinaryExpression = KtPsiFactory(this).createExpression("binding.$text")
        this.replace(newBinaryExpression)
    }
}