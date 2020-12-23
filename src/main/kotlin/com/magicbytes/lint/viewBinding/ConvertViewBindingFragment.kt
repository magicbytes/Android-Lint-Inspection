package com.magicbytes.lint.viewBinding

import com.intellij.psi.codeStyle.CodeStyleManager
import com.magicbytes.lint.viewBinding.utils.createProperty
import com.magicbytes.lint.viewBinding.utils.findMethodWithName
import com.magicbytes.lint.viewBinding.utils.fullBindingClassName
import com.magicbytes.lint.viewBinding.utils.replaceAllSyntheticAccessWithBinding
import org.jetbrains.kotlin.psi.*

class ConvertViewBindingFragment(private val element: KtClass) {
    fun convert() {
        val onCreateViewMethod = element.findMethodWithName("onCreateView")!!
        val setContentExpression = onCreateViewMethod.bodyBlockExpression?.statements.orEmpty().firstOrNull {
            it.text.contains("inflater.inflate(R.layout")
        } ?: return

        val fullBindingClassName = setContentExpression.fullBindingClassName
        element.createProperty("private val binding get() = _binding!!")
        element.createProperty("private var _binding: $fullBindingClassName? = null")

        updateOnCreateView(setContentExpression, fullBindingClassName)
        element.replaceAllSyntheticAccessWithBinding()
        updateOnDestroyView()
    }

    private fun updateOnCreateView(setContentExpression: KtExpression, fullBindingClassName: String) {
        if (setContentExpression is KtReturnExpression) {
            val factory = KtPsiFactory(setContentExpression)
            val thirdLineExpression = factory.createExpression("return view")
            val secondLineExpression = factory.createProperty("val view = binding.root")
            val firstLineExpression =
                factory.createExpression("_binding = ${fullBindingClassName}.inflate(inflater, container, false)")

            firstLineExpression.addAfter(factory.createWhiteSpace("\n"), firstLineExpression.lastChild)
            firstLineExpression.addAfter(secondLineExpression, firstLineExpression.lastChild)
            firstLineExpression.addAfter(factory.createWhiteSpace("\n"), firstLineExpression.lastChild)
            firstLineExpression.addAfter(thirdLineExpression, firstLineExpression.lastChild)

            setContentExpression.replace(firstLineExpression)
            CodeStyleManager.getInstance(setContentExpression.project).reformat(element)
        }
    }

    private fun updateOnDestroyView() {
        val onDestroyViewMethod = element.findMethodWithName("onDestroyView")
        if (onDestroyViewMethod == null) {
            createOnDestroyViewMethod()
        } else {
            onDestroyViewMethod.bodyBlockExpression?.let { attachOnDestroyLogic(it) }
        }
    }

    private fun createOnDestroyViewMethod(): KtNamedFunction {
        val factory = KtPsiFactory(element)
        val onDestroyMethod = factory.createFunction("override fun onDestroyView()")

        val onDestroyBody = factory.createEmptyBody()
        val superCall = factory.createExpression("super.onDestroyView()")
        onDestroyBody.addBefore(superCall, onDestroyBody.lastChild)
        onDestroyBody.addBefore(factory.createWhiteSpace("\n"), onDestroyBody.lastChild)
        onDestroyBody.addBefore(factory.createExpression("_binding = null"), onDestroyBody.lastChild)

        onDestroyMethod.add(onDestroyBody)
        element.addBefore(onDestroyMethod, element.body?.lastChild)

        return onDestroyMethod
    }

    private fun attachOnDestroyLogic(onDestroyBody: KtBlockExpression) {
        val factory = KtPsiFactory(element)
        onDestroyBody.addBefore(factory.createWhiteSpace("\n"), onDestroyBody.lastChild)
        onDestroyBody.addBefore(factory.createExpression("_binding = null"), onDestroyBody.lastChild)
    }
}