package cz.tomasdvorak.eet.client.binding;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * dateTime attribute is in EET defined by pattern
 * \d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(Z|[+\-]\d\d:\d\d) according to ISO 8601.
 * The default CXF conversion includes also millis and timezone in format HHMM.
 * We need to get rid of millis and timezone
 * has to be in format HH:MM. Thus we implement our own converter.
 *
 * TODO: is it needed? Exists any other or default solution?
 *
 * Valid date example: 2016-12-09T16:45:36+01:00
 *
 * @see XMLGregorianCalendar#toXMLFormat()
 */
public class XmlDateAdapter extends XmlAdapter<String, ZonedDateTime> {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Override
    public ZonedDateTime unmarshal(final String inputDate) throws ParseException {
        return ZonedDateTime.parse(inputDate, formatter);
    }

    @Override
    public String marshal(final ZonedDateTime inputDate) {
        return formatter.format(inputDate);
    }

//    private SimpleDateFormat getSimpleDateFormat() {
//        return new SimpleDateFormat(DATE_PATTERN, new Locale("cs", "CZ"));
//    }
}
