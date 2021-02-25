package no.nsd.qddt.model.builder.pdf

import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.io.IOException
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants.BLUE
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory.createFont
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.*
import com.itextpdf.kernel.pdf.action.PdfAction
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine
import com.itextpdf.kernel.pdf.navigation.PdfDestination
import com.itextpdf.layout.Document
import com.itextpdf.layout.Style
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.*
import com.itextpdf.layout.hyphenation.HyphenationConfig
import com.itextpdf.layout.layout.LayoutContext
import com.itextpdf.layout.layout.LayoutResult
import com.itextpdf.layout.property.*
import com.itextpdf.layout.renderer.ParagraphRenderer
import no.nsd.qddt.model.Comment
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.exception.StackTraceFilter
import no.nsd.qddt.utils.StringTool
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.AbstractMap.SimpleEntry
import java.util.stream.Collectors
import kotlin.collections.ArrayList

//import javassist.Loader
//import java.util.function.Consumer
//import java.util.function.Function
//import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
class PdfReport(outputStream: ByteArrayOutputStream?) : PdfDocument(PdfWriter(outputStream).setSmartMode(true)) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    
    private val toc: MutableList<SimpleEntry<String, SimpleEntry<String, Int>>> = ArrayList()
    var width100 = 0f
    private var font: PdfFont? = null
    private var bold: PdfFont? = null
    private var chapterHeading: PdfFont? = null
    private val sizeSmall = 9F
    private val sizeNormal = 12F
    private val sizeHeader2 = 14F
    private val sizeHeader1 = 23F
    private val cellStyleLeft = Style()
        .setFontSize(sizeSmall)
        .setTextAlignment(TextAlignment.LEFT)
        .setBorder(Border.NO_BORDER)
        .setPadding(1.0f)
    private val cellStyleRight = Style()
        .setFontSize(sizeSmall)
        .setTextAlignment(TextAlignment.RIGHT)
        .setBorder(Border.NO_BORDER)
        .setPadding(1.0f)
        .setPaddingRight(4.0f)
    private var document: Document? = null

    fun createToc() {
        val url = getResource("qddt.png")
        val startToc: Int = numberOfPages
        lastPage.setPageLabel(PageLabelNumberingStyle.LOWERCASE_ROMAN_NUMERALS, null, 1)
        document!!.add(AreaBreak(AreaBreakType.NEXT_PAGE))
        document!!.add(
            Image(ImageDataFactory.create(url))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
        )
            .add(
                Paragraph()
                    .setFont(bold)
                    .setFontSize(24F)
                    .setTextAlignment(TextAlignment.CENTER)
                    .add("Pdf report")
                    .setPaddingBottom(60F).setKeepTogether(false)
            )
            .add(
                Paragraph()
                    .setFont(font)
                    .setFontSize(18F)
                    .setTextAlignment(TextAlignment.CENTER)
                    .add(toc[0].value.key.split("\t").toTypedArray()[1])
                    .setPaddingBottom(60F)
            )
            .add(
                Paragraph()
                    .add("Generated " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE d MMMM YYYY HH:mm:SS")))
                    .setTextAlignment(TextAlignment.CENTER)
            )
        document!!.add(AreaBreak())
        document!!.add(Paragraph().setFont(bold).add("Table of Content").setDestination("toc"))
        val tabstops: MutableList<TabStop> = ArrayList()
        tabstops.add(TabStop(580F, TabAlignment.RIGHT, DottedLine()))
        for ((key, text) in toc) {
            document!!.add(
                Paragraph()
                    .setMultipliedLeading(1.2f)
                    .addTabStops(tabstops)
                    .add(text.key)
                    .add(Tab())
                    .add(text.value.toString())
                    .setAction(PdfAction.createGoTo(key))
            )
        }
        val lastPage: Int = numberOfPages
        val tocPages = lastPage - startToc
        try {
            for (i in 0 until tocPages) {
                movePage(getLastPage(), 1)
            }
        } catch (ex: Exception) {
            logger.error("createToc", ex)
        }
        //        LOG.info( String.join( ", ", getPageLabels() ) );
        getPage(1).setPageLabel(PageLabelNumberingStyle.LOWERCASE_ROMAN_NUMERALS, null, 1)
        getPage(tocPages + 1).setPageLabel(PageLabelNumberingStyle.DECIMAL_ARABIC_NUMERALS, "Page ", 1)
        getPage(startToc + 1).setPageLabel(
            PageLabelNumberingStyle.DECIMAL_ARABIC_NUMERALS,
            "Page ",
            startToc - tocPages + 1
        )
        document!!.flush()
        close()
    }


    private var outline: PdfOutline? = null

    fun addHeader(element: AbstractEntityAudit, header: String): Document {
        val values = header.split(" ").toTypedArray()
        var chapter = ""
        if (values.size > 1) {
            chapter = values[1]
            //            document.add( new AreaBreak(AreaBreakType.NEXT_AREA  ) );
            // document.add( new AreaBreak() );        //https://github.com/DASISH/qddt-client/issues/611
        }
        outline = createOutline(outline, StringTool.CapString(element.name), element.id.toString())
        val titlePage: SimpleEntry<String, Int> =
            SimpleEntry(chapter + "\t" + StringTool.CapString(element.name), numberOfPages)
        toc.add(SimpleEntry(element.id.toString(), titlePage))
        val p: Paragraph = Paragraph(element.name)
            .setFontColor(BLUE)
            .setFontSize(sizeHeader1)
            .setMultipliedLeading(1f) //            .setWidth(width100*0.8F)
            .setDestination(element.id.toString())
        val table = Table(UnitValue.createPercentArray(floatArrayOf(68.0f, 12.0f, 20.0f)))
        table.addCell(
            Cell(4, 1).add(Paragraph(header).setMultipliedLeading(1f).setFontSize(21F).setFont(chapterHeading))
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER)
                .add(
                    Paragraph("__________________________________________________________")
                        .setFontColor(BLUE)
                        .setVerticalAlignment(VerticalAlignment.TOP)
                )
                .add(p)
        )
            .addCell(Cell().add(Paragraph("Version")).addStyle(cellStyleRight))
            .addCell(Cell().add(Paragraph(element.version.toString())).addStyle(cellStyleLeft))
            .addCell(Cell().add(Paragraph("Last Saved")).addStyle(cellStyleRight))
            .addCell(Cell().add(Paragraph(String.format("%1\$TF %1\$TT", element.modified))).addStyle(cellStyleLeft))
            .addCell(Cell().add(Paragraph("Last Saved By")).addStyle(cellStyleRight))
            .addCell(Cell().add(Paragraph(StringTool.CapString(element.modifiedBy.username))).addStyle(cellStyleLeft))
            .addCell(Cell().add(Paragraph("Agency")).addStyle(cellStyleRight))
            .addCell(Cell().add(Paragraph(element.agency.name)).addStyle(cellStyleLeft))
            .setWidth(width100)
        p.setNextRenderer(UpdatePageRenderer(p, titlePage))
        val div: Div = Div().add(table).setKeepTogether(true).setKeepWithNext(true)
        return document!!.add(div)
    }

    val theDocument: Document?
        get() = document

    fun addHeader2(header: String?): Document {
        return addHeader2(header, null)
    }

    fun addHeader2(header: String?, rev: String?): Document {
        return document!!.add(
            Paragraph(header)
                .setMaxWidth(width100 * 0.8f)
                .setFontColor(BLUE)
                .setFontSize(sizeHeader2)
                .addTabStops(TabStop(width100 * 0.80f, TabAlignment.RIGHT))
                .add(Tab())
                .add(rev ?: "")
                .setKeepWithNext(true)
        )
    }

    fun addParagraph(value: String): Document? {
        try {
            val para: Paragraph = Paragraph().setWidth(width100 * 0.8f).setKeepTogether(false)
            Arrays.stream(value.split("\n").toTypedArray())
                .map {
                    when {
                        it.matches(Regex(HTML_PATTERN)) -> it
                        else -> "$it</br>"
                    }
                }
                .collect(Collectors.joining(" ")).also {
                    HtmlConverter.convertToElements(it).forEach {
                        element -> para.add(element as IBlockElement )
                    }
                    document!!.add(para)
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return document
    }

    fun addComments(comments: List<Comment>): Document {
        val table: Table = Table(UnitValue.createPercentArray(floatArrayOf(25.0f, 25.0f, 25.0f, 25.0f)))
            .setKeepTogether(true)
            .setWidth(width100 * 0.8f)
            .setPaddingBottom(15F)
        for (comment in comments.stream().filter(Comment::isPublic).collect(Collectors.toList())) {
            addCommentRow(table, comment, 0)
        }
        return document!!.add(table)
    }

    fun addPadding(): Document {
        return document!!.add(Paragraph().setPaddingBottom(15F).setKeepTogether(false))
    }

    private fun addCommentRow(table: Table, comment: Comment, level: Int) {
        table.setBackgroundColor(DeviceRgb(245, 245, 245)).setBorder(Border.NO_BORDER)
            .addCell(
                Cell(1, 3)
                    .setBorder(Border.NO_BORDER) //                .setWidth(width100*0.8F)
                    .setPaddingBottom(10F)
                    .setPaddingRight(10F)
                    .add(Paragraph(comment.comment).setPaddingLeft(20F * level))
            )
            .addCell(
                Cell(1, 1)
                    .setBorder(Border.NO_BORDER) //                .setWidth(width100*0.20F)
                    .add(
                        Paragraph(
                            comment.modifiedBy.username + "@" + (comment.modifiedBy.agency?.name ?:"?" ) +
                                    java.lang.String.format(" - %1\$TD %1\$TT", comment.modified.toLocalDateTime())
                        )
                    )
            )
        for (subComment in comment.comments.stream().filter(Comment::isPublic)
            .collect(Collectors.toList())) {
            addCommentRow(table, subComment, level + 1)
        }
    }

    private fun createOutline(source: PdfOutline?, title: String, key: String): PdfOutline {
         return when (source) {
             null -> {
                 getOutlines(false).addOutline(title).apply {
                     addDestination(PdfDestination.makeDestination(PdfString(key)))
                 }
             }
             else -> {
                 source.addOutline(title).apply {
                     addDestination(PdfDestination.makeDestination(PdfString(key)))
                 }
             }
         }
    }

    private inner class UpdatePageRenderer(modelElement: Paragraph?,private var entry: SimpleEntry<String, Int>) : ParagraphRenderer(modelElement) {
        override fun layout(layoutContext: LayoutContext): LayoutResult {
            return super.layout(layoutContext).also {
                entry.setValue(layoutContext.area.pageNumber)
            }
        }
    }

    private fun getResource(resource: String): URL {
        return  ClassLoader.getSystemResource(resource) ?: URL(resource)
//        var url = URL(resource)
//        //Try with the Thread Context Loader.
//        var classLoader = Thread.currentThread().contextClassLoader
//        if (classLoader != null) {
//            return classLoader.getResource(resource)
//        }
//
//        //Let's now try with the classloader that loaded this class.
//        classLoader = Loader::class.java.classLoader
//        if (classLoader != null) {
//            url = classLoader.getResource(resource)
//            if (url != null) {
//                return url
//            }
//        }
//        LOG.info("getResource failing soon...")
//
//        //Last ditch attempt. Get the resource from the classpath.
//        return ClassLoader.getSystemResource(resource)
    }

    companion object {
        private const val serialVersionUID = 1345354324653452L
        private const val HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>"
    }

    //    private PdfDocument pdfContent;
    init {
        try {
            font = createFont(StandardFonts.TIMES_ROMAN)
            bold = createFont(StandardFonts.TIMES_BOLD)
            chapterHeading = createFont(StandardFonts.COURIER)

//            pdfContent = new PdfDocument( new PdfWriter(dest) )
            initializeOutlines()
            getCatalog().pageMode = PdfName.UseOutlines
            document = Document(this, PageSize.A4)
            width100 = PageSize.A4.width - document!!.leftMargin - document!!.rightMargin
            document!!.setTextAlignment(TextAlignment.JUSTIFIED)
                .setHyphenation(HyphenationConfig("en", "uk", 3, 3))
                .setFont(font)
                .setFontSize(sizeNormal)
            addEventHandler(PdfDocumentEvent.START_PAGE, TextFooterEventHandler(document!!))
        } catch (ex: Exception) {
            logger.error("PdfReport()", ex)
            StackTraceFilter.filter(ex.stackTrace).stream()
                .map { it.toString() }
                .forEach { msg: String? -> logger.info(msg) }
        }
    }
}
