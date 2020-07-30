package com.magicbytes.lint

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr

class HardcodedDimensDetector : LayoutDetector() {
    override fun getApplicableAttributes(): Collection<String>? {
        return listOf(
            "layout_marginEnd",
            "layout_margin",
            "layout_marginLeft",
            "layout_marginRight",
            "layout_marginStart",
            "layout_marginTop",
            "layout_marginBottom",
            "padding",
            "paddingBottom",
            "paddingTop",
            "paddingLeft",
            "paddingStart",
            "paddingRight",
            "paddingEnd",
            "contentPadding",
            "layout_width",
            "layout_height",
            "layout_editor_absoluteX"
        )
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return (folderType == ResourceFolderType.LAYOUT ||
                folderType == ResourceFolderType.MENU ||
                folderType == ResourceFolderType.XML)
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        val value = attribute.value
        if (value.contains(Regex("\\d+dp"))) {
            context.report(
                ISSUE,
                attribute,
                context.getLocation(attribute),
                String.format(
                    "Hardcoded dimension \"%1\$s\", should use `@dimen` resource", value
                )
            )
        }
    }

    companion object {
        /** The main issue discovered by this detector  */
        @JvmField
        val ISSUE = Issue.create(
            id = "HardcodedDimension",
            briefDescription = "Hardcoded dimens",
            explanation = """
                Brief
                """,
            category = Category.I18N,
            priority = 5,
            severity = Severity.ERROR,
            implementation = Implementation(
                HardcodedDimensDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )
    }
}