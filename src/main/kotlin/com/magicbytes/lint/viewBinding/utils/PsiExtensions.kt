package com.magicbytes.lint.viewBinding.utils

import com.google.common.base.CaseFormat
import org.jetbrains.kotlin.idea.references.SyntheticPropertyAccessorReference
import org.jetbrains.kotlin.psi.*

val KtBinaryExpression.isSyntheticAccessor: Boolean
    get() {
        var dotExpression = if (isDoubleDotExpression) {
            (left as KtDotQualifiedExpression).receiverExpression as KtDotQualifiedExpression
        } else {
            left as? KtDotQualifiedExpression
        } ?: return false

        if (dotExpression.receiverExpression is KtDotQualifiedExpression) {
            dotExpression = dotExpression.receiverExpression as KtDotQualifiedExpression
        }
        val nameExpression = dotExpression.receiverExpression as? KtNameReferenceExpression ?: return false
        return nameExpression.references.any { it is SyntheticPropertyAccessorReference }
    }

val KtBinaryExpression.isDoubleDotExpression: Boolean
    get() {
        val dotExpression = left as? KtDotQualifiedExpression ?: return false
        return dotExpression.receiverExpression is KtDotQualifiedExpression
    }

fun KtBinaryExpression.convertToBindingCall() {
    val textBinding = if (isDoubleDotExpression) {
        text.substringAfter(".")
    } else {
        text
    }
    val newBinaryExpression = KtPsiFactory(this).createExpression("binding.$textBinding")
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

fun KtClass.createProperty(propertyText: String) {
    val factory = KtPsiFactory(this)
    val property = factory.createProperty(propertyText)
    body?.addAfter(property, body?.firstChild)
}

val KtExpression.fullBindingClassName: String
    get() {
        val nameLayout = text.substringAfter("R.layout.")
            .removeSuffix(")")
            .removeSuffix(", container, false")
            .trim()

        val nameLayoutAsClass = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, nameLayout)
        return nameLayoutAsClass + "Binding"
    }