package no.nsd.qddt.model.builder

import no.nsd.qddt.model.Instrument
import no.nsd.qddt.model.interfaces.IEntityFactory

/**
 * @author Stig Norland
 */
class InstrumentFactory : IEntityFactory<Instrument?> {
    fun create(): Instrument {
        return Instrument()
    }

    fun copyBody(source: Instrument, dest: Instrument): Instrument {
        dest.setDescription(source.getDescription())
        dest.setLabel(source.getLabel())
        dest.setName(source.getName())
        dest.setExternalInstrumentLocation(source.getExternalInstrumentLocation())
        dest.setInstrumentKind(source.getInstrumentKind())
        //        dest.setSequence( source.getSequence().stream()
//          .map( InstrumentElement::clone )
//          .collect(Collectors.toList()));
        // ?? why       dest.setStudy( source.getStudy() );
        return dest
    }
}
