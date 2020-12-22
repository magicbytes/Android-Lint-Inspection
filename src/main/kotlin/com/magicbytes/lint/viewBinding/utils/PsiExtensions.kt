package com.magicbytes.lint.viewBinding.utils

import org.jetbrains.kotlin.idea.references.SyntheticPropertyAccessorReference
import org.jetbrains.kotlin.psi.*

val KtBinaryExpression.isSyntheticAccessor: Boolean
    get() {
        val dotExpression = left as? KtDotQualifiedExpression ?: return false
        val nameExpression = dotExpression.receiverExpression as? KtNameReferenceExpression ?: return false
        return nameExpression.references.any { it is SyntheticPropertyAccessorReference }
    }

fun KtBinaryExpression.convertToBindingCall() {
    val newBinaryExpression = KtPsiFactory(this).createExpression("binding.$text")
    this.replace(newBinaryExpression)
}

fun KtClass.findMethodWithName(searchName: String): KtNamedFunction? {
    val allMethods = body?.functions.orEmpty()
    return allMethods.firstOrNull { it.name == searchName }
}

fun KtClass.replaceAllSyntheticAccessWithBinding() {
    accept(object : KtTreeVisitorVoid() {
        override fun visitBinaryExpression(expression: KtBinaryExpression) {
            super.visitBinaryExpression(expression)

            if (expression.isSyntheticAccessor) {
                expression.convertToBindingCall()
            }
        }
    })
}