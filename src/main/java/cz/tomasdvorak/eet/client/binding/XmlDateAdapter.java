package cz.tomasdvorak.eet.client.binding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * dateTime attribute is in EET defined by pattern \d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(Z|[+\-]\d\d:\d\d) according to ISO 8601.
 * The default CXF conversion includes also millis and timezone in format HHMM. We need to get rid of millis and timezone
 * has to be in format HH:MM. Thus we implement our own converter.
 *
 * TODO: is it needed? Exists any other or default solution?
 *
 * Valid date example: 2016-12-09T16:45:36+01:00
 *
 * @see XMLGregorianCalendar#toXMLFormat()
 */
public class XmlDateAdapter extends XmlAdapter<String, Date> {

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

    @Override
    public Date unmarshal(final String inputDate) throws ParseException {
        final String cleanInput = inputDate.replaceAll(":(\\d\\d)$", "$1");
        return getSimpleDateFormat().parse(cleanInput);
    }

    @Override
    public String marshal(final Date inputDate) {
        final String result = getSimpleDateFormat().format(inputDate);
        return result.substring(0, result.length() - 2) + ":" + result.substring(result.length() - 2);
    }

    private SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat(DATE_PATTERN, new Locale("cs", "CZ"));
    }

}
