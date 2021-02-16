package no.nsd.qddt.model.builder

import no.nsd.qddt.model.Instrument
import no.nsd.qddt.model.interfaces.IEntityFactory

/**
 * @author Stig Norland
 */
class InstrumentFactory : IEntityFactory<Instrument> {
    override fun create(): Instrument {
        return Instrument()
    }

    override fun copyBody(source: Instrument, dest: Instrument): Instrument {
        dest.description = source.description
//        dest.label = source.label
        dest.name = source.name
        dest.externalInstrumentLocation = source.externalInstrumentLocation
        dest.instrumentKind = source.instrumentKind
        //        dest.setSequence( source.getSequence().stream()
//          .map( InstrumentElement::clone )
//          .collect(Collectors.toList()));
        // ?? why       dest.setStudy( source.getStudy() );
        return dest
    }
}
