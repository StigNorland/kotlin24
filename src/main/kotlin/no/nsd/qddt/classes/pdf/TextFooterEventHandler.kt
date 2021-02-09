package no.nsd.qddt.classes.pdf

import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.events.Event
import com.itextpdf.kernel.events.IEventHandler
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfPage
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Document
import org.slf4j.LoggerFactory

/**
 * @author Stig Norland
 */
class TextFooterEventHandler(doc: Document) : IEventHandler {

    protected val LOG = LoggerFactory.getLogger(this.javaClass)

    protected var doc: Document?

    override fun handleEvent(event: Event) {
        val pdfDoc: PdfDocumentEvent = event as PdfDocumentEvent
        val page: PdfPage = pdfDoc.page
        val pdfDocument: PdfDocument = pdfDoc.document
        val i: Int = pdfDocument.numberOfPages
        var labels: Array<String> = pdfDocument.pageLabels
        if (labels[i - 1].contains("Page")) {
            val canvas = PdfCanvas(pdfDoc.page)
            canvas.beginText()
            try {
                canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN), 9F)
                canvas.moveText((page.pageSize.width - (doc!!.rightMargin + 10)).toDouble(),
                    (doc!!.bottomMargin  - 10).toDouble()
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

    init {
        this.doc = doc
    }
}
