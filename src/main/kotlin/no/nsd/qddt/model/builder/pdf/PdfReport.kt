package no.nsd.qddt.model.builder.pdf

import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.io.exceptions.IOException
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.colors.ColorConstants
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
import com.itextpdf.layout.properties.*
import com.itextpdf.layout.renderer.ParagraphRenderer
import no.nsd.qddt.config.exception.StackTraceFilter.filter
import no.nsd.qddt.model.Comment
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.utils.StringTool.capString
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*


/**
 * @author Stig Norland
 */
class PdfReport(outputStream: ByteArrayOutputStream) : PdfDocument(PdfWriter(outputStream).setSmartMode(true)) {

    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val toc: MutableList<AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, Int>>> = mutableListOf()

//    private val toc: MutableList<MutableMap<String, MutableMap<String, Int>>> = mutableListOf()

    private var document = Document(this, PageSize.A4)

    var width100 = PageSize.A4.width - document.leftMargin - document.rightMargin

    private var font: PdfFont = createFont(StandardFonts.TIMES_ROMAN)
    private var bold: PdfFont = createFont(StandardFonts.TIMES_BOLD)
    private var chapterHeading: PdfFont = createFont(StandardFonts.COURIER)
    private val sizeSmall = 9
    private val sizeNormal = 12
    private val sizeHeader2 = 14
    private val sizeHeader1 = 23


    private val cellStyleLeft = Style()
        .setFontSize(sizeSmall.toFloat())
        .setTextAlignment(TextAlignment.LEFT)
        .setBorder(Border.NO_BORDER).setPadding(1.0f)

    private val cellStyleRight = Style()
        .setFontSize(sizeSmall.toFloat())
        .setTextAlignment(TextAlignment.RIGHT)
        .setBorder(Border.NO_BORDER).setPadding(1.0f).setPaddingRight(4.0f)

    fun createToc() {
        val url = getResource("qddt.png")
        val startToc = numberOfPages
        lastPage.setPageLabel(PageLabelNumberingStyle.LOWERCASE_ROMAN_NUMERALS, null, 1)
        //        LOG.info( String.join( ", ", getPageLabels() ) );
        document.add(AreaBreak(AreaBreakType.NEXT_PAGE))
        document.add(
            Image(ImageDataFactory.create(url))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
        )
            .add(
                Paragraph()
                    .setFont(bold)
                    .setFontSize(24f)
                    .setTextAlignment(TextAlignment.CENTER)
                    .add("Pdf report")
                    .setPaddingBottom(60f).setKeepTogether(false)
            )
            .add(
                Paragraph()
                    .setFont(font)
                    .setFontSize(18f)
                    .setTextAlignment(TextAlignment.CENTER)
                    .add(toc[0].value.key.split("\t").toTypedArray()[1])
                    .setPaddingBottom(60f)
            )
            //        toc.get(0).getValue().getKey().split("\t")[1])

            .add(
                Paragraph()
                    .add("Generated " + DateTime.now().toString("EEEE d MMMM YYYY HH:mm:SS"))
                    .setTextAlignment(TextAlignment.CENTER)
            )
        document.add(AreaBreak())
        document.add(Paragraph().setFont(bold).add("Table of Content").setDestination("toc"))
        val tabStops: MutableList<TabStop> = mutableListOf()
        tabStops.add(TabStop(580F, TabAlignment.RIGHT, DottedLine()))
        for ((key, text) in toc) {
            document.add(
                Paragraph()
                    .setMultipliedLeading(1.2f)
                    .addTabStops(tabStops)
                    .add(text.key)
                    .add(Tab())
                    .add(text.value.toString())
                    .setAction(PdfAction.createGoTo(key))
            )
        }
        val lastPage = numberOfPages
        val tocPages = lastPage - startToc
        try {
            for (i in 0 until tocPages) {
                movePage(getLastPage(), 1)
            }
        } catch (ex: Exception) {
            logger.error("createToc", ex)
        }
        logger.info( pageLabels.joinToString(", "))
        getPage(1).setPageLabel(PageLabelNumberingStyle.LOWERCASE_ROMAN_NUMERALS, null, 1)
        getPage(tocPages + 1).setPageLabel(PageLabelNumberingStyle.DECIMAL_ARABIC_NUMERALS, "Page ", 1)
//        getPage(startToc + 1).setPageLabel(PageLabelNumberingStyle.DECIMAL_ARABIC_NUMERALS,"Page ",startToc - tocPages + 1)
        document.flush()
        close()
    }

    //    public PdfFont getParagraphFont() {
    //        return paragraphFont;
    //    }
    private var outline: PdfOutline? = null
    fun addHeader(element: AbstractEntityAudit, header: String): Document {
        val values = header.split(" ").toTypedArray()
        var chapter = ""
        if (values.size > 1) {
            chapter = values[1]
            //            document.add( new AreaBreak(AreaBreakType.NEXT_AREA  ) );
            // document.add( new AreaBreak() );        //https://github.com/DASISH/qddt-client/issues/611
        }
        outline = createOutline(outline, capString(element.name), element.id.toString())
        val titlePage = AbstractMap.SimpleEntry(chapter + "\t" + capString(element.name), numberOfPages)
        toc.add(AbstractMap.SimpleEntry(element.id.toString(), titlePage))
        val p = Paragraph(element.name)
            .setFontColor(ColorConstants.BLUE)
            .setFontSize(sizeHeader1.toFloat())
            .setMultipliedLeading(1f) //            .setWidth(width100*0.8F)
            .setDestination(element.id.toString())
        val table = Table(UnitValue.createPercentArray(floatArrayOf(68.0f, 12.0f, 20.0f)))
        table.addCell(
            Cell(4, 1).add(Paragraph(header).setMultipliedLeading(1f).setFontSize(21f).setFont(chapterHeading))
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER)
                .add(
                    Paragraph("__________________________________________________________")
                        .setFontColor(ColorConstants.BLUE)
                        .setVerticalAlignment(VerticalAlignment.TOP)
                )
                .add(p)
        )
            .addCell(Cell().add(Paragraph("Version")).addStyle(cellStyleRight))
            .addCell(Cell().add(Paragraph(element.version.toString())).addStyle(cellStyleLeft))
            .addCell(Cell().add(Paragraph("Last Saved")).addStyle(cellStyleRight))
            .addCell(Cell().add(Paragraph(String.format("%1\$TF %1\$TT", element.modified))).addStyle(cellStyleLeft))
            .addCell(Cell().add(Paragraph("Last Saved By")).addStyle(cellStyleRight))
            .addCell(Cell().add(Paragraph(capString(element.modifiedBy.username))).addStyle(cellStyleLeft))
            .addCell(Cell().add(Paragraph("Agency")).addStyle(cellStyleRight))
            .addCell(Cell().add(Paragraph(element.agency!!.name)).addStyle(cellStyleLeft))
            .setWidth(width100)
        p.setNextRenderer(UpdatePageRenderer(p, titlePage))
        val div = Div().add(table).setKeepTogether(true).setKeepWithNext(true)
        return document.add(div)
    }

    fun getTheDocument(): Document {
        return document
    }

    @JvmOverloads
    fun addHeader2(header: String?, rev: String? = null): Document {
        return document.add(
            Paragraph(header)
                .setMaxWidth(width100 * 0.8f)
                .setFontColor(ColorConstants.BLUE)
                .setFontSize(sizeHeader2.toFloat())
                .addTabStops(TabStop(width100 * 0.80f, TabAlignment.RIGHT))
                .add(Tab())
                .add(rev?:"")
                .setKeepWithNext(true)
        )
    }

    fun addParagraph(value: String): Document {
        try {
            val joined = value.split("\n").joinToString(" ") { if (it.matches(Regex(HTML_PATTERN))) it else "$it</br>" }


            val para = Paragraph().setWidth(width100 * 0.8f).setKeepTogether(false)

            HtmlConverter.convertToElements(joined).forEach {
                para.add(it as IBlockElement)
            }
            document.add(para)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return document
    }

    fun addComments(comments: List<Comment>?): Document {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(25.0f, 25.0f, 25.0f, 25.0f)))
            .setKeepTogether(true)
            .setWidth(width100 * 0.8f)
            .setPaddingBottom(15F)
        comments?.stream()?.filter { it?.isPublic == true }?.forEach {
            addCommentRow(table, it, 0)
        }
        return document.add(table)
    }

    fun addPadding(): Document {
        return document.add(Paragraph().setPaddingBottom(15f).setKeepTogether(false))
    }

    private fun addCommentRow(table: Table, comment: Comment, level: Int) {
        table.setBackgroundColor(DeviceRgb(245, 245, 245)).setBorder(Border.NO_BORDER)
            .addCell(
                Cell(1, 3)
                    .setBorder(Border.NO_BORDER) //                .setWidth(width100*0.8F)
                    .setPaddingBottom(10f)
                    .setPaddingRight(10f)
                    .add(Paragraph(comment.comment).setPaddingLeft((20 * level).toFloat()))
            )
            .addCell(
                Cell(1, 1)
                    .setBorder(Border.NO_BORDER) //                .setWidth(width100*0.20F)
                    .add(
                        Paragraph(
                            comment.modifiedBy.username + "@" + comment.modifiedBy.agency.name +
                                    String.format(" - %1\$TD %1\$TT", comment.modified?.toLocalDateTime())
                        )
                    )
            )
        comment.comments.stream().filter { it?.isPublic == true }?.forEach {
            addCommentRow(table, it,  level + 1)
        }
    }

    private fun createOutline(outline: PdfOutline?, title: String, key: String): PdfOutline? {
        return when (outline) {
            null -> {
                getOutlines(false)
                    .addOutline(title).apply {
                        addDestination(PdfDestination.makeDestination(PdfString(key)))
                    }
            }
            else -> {
                outline.addOutline(title).apply {
                    addDestination(PdfDestination.makeDestination(PdfString(key)))
                }
            }
        }
    }

    private inner class UpdatePageRenderer(modelElement: Paragraph?, protected var entry: AbstractMap.SimpleEntry<String, Int>) :
        ParagraphRenderer(modelElement) {
        override fun layout(layoutContext: LayoutContext): LayoutResult {
            val result = super.layout(layoutContext)
            entry.setValue(layoutContext.area.pageNumber)
            return result
        }
    }

    private fun getResource(resource: String): URL {

        Thread.currentThread().contextClassLoader.let { classLoader ->
            classLoader.getResource((resource)). let {
                if (it != null) return it
            }
        }

        logger.info("getResource failing soon...")

        //Last ditch attempts. Get the resource from the classpath.
        return ClassLoader.getSystemResource(resource)
    }

    companion object {
        const val HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>"
    }

    init {
        try {
//            pdfContent = new PdfDocument( new PdfWriter(dest) )
            initializeOutlines()
            catalog.pageMode = PdfName.UseOutlines

            document.setTextAlignment(TextAlignment.JUSTIFIED)
                .setHyphenation(HyphenationConfig("en", "uk", 3, 3))
                .setFont(font)
                .setFontSize(sizeNormal.toFloat())

            addEventHandler(PdfDocumentEvent.START_PAGE, TextFooterEventHandler(document))

        } catch (ex: Exception) {
            logger.error("PdfReport()", ex)
            filter(ex.stackTrace).stream()
                .map { a: StackTraceElement -> a.toString() }
                .forEach(logger::info)
        }
    }
}
