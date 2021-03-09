package no.nsd.qddt.model.builder.pdf

import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.events.Event
import com.itextpdf.kernel.events.IEventHandler
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PageLabelNumberingStyle
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * @author Stig Norland
 */
class TextFooterEventHandler(protected var doc: Document) : IEventHandler {
//    protected val LOG: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun handleEvent(event: Event) {
        val pdfDoc = event as PdfDocumentEvent
        val page = pdfDoc.page
        val pdfDocument = pdfDoc.document
        val i = pdfDocument.numberOfPages
        var labels = pdfDocument.pageLabels
        if (labels == null) {
            page.setPageLabel(PageLabelNumberingStyle.DECIMAL_ARABIC_NUMERALS, "Page ", 1)
            labels = pdfDocument.pageLabels
        }
        if (labels!![i - 1].contains("Page")) {
            val canvas = PdfCanvas(pdfDoc.page)
            canvas.beginText()
            try {
                canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN), 9f)
                canvas.moveText(
                    (page.pageSize.width - (doc.rightMargin + 10)).toDouble(),
                    (doc.bottomMargin - 10).toDouble()
                )
                    .showText(labels[i - 1])
                    .endText()
                    .release()
            } catch (e: Exception) {
                e.printStackTrace()
                canvas.release()
            }
        }
    }

}

