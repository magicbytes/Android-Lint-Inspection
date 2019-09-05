import com.android.SdkConstants.*
import com.android.resources.ResourceFolderType
import com.android.tools.lint.checks.HardcodedValuesDetector
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr
import java.util.*

class HardcodedDimensDetector : LayoutDetector() {
    override fun getApplicableAttributes(): Collection<String>? {
        return Arrays.asList(
                // Layouts
                ATTR_TEXT
        )
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return (folderType == ResourceFolderType.LAYOUT ||
                folderType == ResourceFolderType.MENU ||
                folderType == ResourceFolderType.XML)
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        val value = attribute.value
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