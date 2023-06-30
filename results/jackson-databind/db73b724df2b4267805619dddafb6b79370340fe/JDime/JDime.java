package com.fasterxml.jackson.databind.util;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.core.io.NumberInput;

/**
 * Default {@link DateFormat} implementation used by standard Date
 * serializers and deserializers. For serialization defaults to using
 * an ISO-8601 compliant format (format String "yyyy-MM-dd'T'HH:mm:ss.SSSX")
 * and for deserialization, both ISO-8601 and RFC-1123.
 *<br>
 * Note that `X` in format String refers to ISO-8601 timezone notation which produces
 * values like "-08:00" -- that is, full minute/hour combo without colon, not using `Z`
 * as alias for "+0000".
 *<p>
 * Note also that to enable use of colon in timezone is possible by using method
 * {@link #withColonInTimeZone} for creating new differently configured format instance.
 */
@SuppressWarnings(value = { "serial" }) public class StdDateFormat extends DateFormat {
  protected final static String PATTERN_PLAIN_STR = "\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d";

  protected final static Pattern PATTERN_PLAIN = Pattern.compile(PATTERN_PLAIN_STR);

  protected final static Pattern PATTERN_ISO8601;

  static {
    Pattern p = null;
    try {
      p = Pattern.compile(PATTERN_PLAIN_STR + "[T]\\d\\d[:]\\d\\d(?:[:]\\d\\d)?" + "(\\.\\d+)?" + "(Z|[+-]\\d\\d(?:[:]?\\d\\d)?)?");
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
    PATTERN_ISO8601 = p;
  }

  /**
     * Defines a commonly used date format that conforms
     * to ISO-8601 date formatting standard, when it includes basic undecorated
     * timezone definition.
     */
  public final static String DATE_FORMAT_STR_ISO8601 = "yyyy-MM-dd\'T\'HH:mm:ss.SSSZ";

  /**
     * ISO-8601 with just the Date part, no time: needed for error messages
     */
  protected final static String DATE_FORMAT_STR_PLAIN = "yyyy-MM-dd";

  /**
     * This constant defines the date format specified by
     * RFC 1123 / RFC 822. Used for parsing via `SimpleDateFormat` as well as
     * error messages.
     */
  protected final static String DATE_FORMAT_STR_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

  /**
     * For error messages we'll also need a list of all formats.
     */
  protected final static String[] ALL_FORMATS = new String[] { DATE_FORMAT_STR_ISO8601, "yyyy-MM-dd\'T\'HH:mm:ss.SSS", DATE_FORMAT_STR_RFC1123, DATE_FORMAT_STR_PLAIN };

  /**
     * By default we use UTC for everything, with Jackson 2.7 and later
     * (2.6 and earlier relied on GMT)
     */
  protected final static TimeZone DEFAULT_TIMEZONE;

  static {
    DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
  }

  protected final static Locale DEFAULT_LOCALE = Locale.US;

  protected final static DateFormat DATE_FORMAT_RFC1123;

  protected final static DateFormat DATE_FORMAT_ISO8601;

  static {
    DATE_FORMAT_RFC1123 = new SimpleDateFormat(DATE_FORMAT_STR_RFC1123, DEFAULT_LOCALE);
    DATE_FORMAT_RFC1123.setTimeZone(DEFAULT_TIMEZONE);
    DATE_FORMAT_ISO8601 = new SimpleDateFormat(DATE_FORMAT_STR_ISO8601, DEFAULT_LOCALE);
    DATE_FORMAT_ISO8601.setTimeZone(DEFAULT_TIMEZONE);
  }

  /**
     * A singleton instance can be used for cloning purposes, as a blueprint of sorts.
     */
  public final static StdDateFormat instance = new StdDateFormat();

  /**
     * Blueprint "Calendar" instance for use during formatting. Cannot be used as is,
     * due to thread-safety issues, but can be used for constructing actual instances 
     * more cheaply by cloning.
     *
     * @since 2.9.1
     */
  protected static final Calendar CALENDAR = new GregorianCalendar(DEFAULT_TIMEZONE, DEFAULT_LOCALE);

  /**
     * Caller may want to explicitly override timezone to use; if so,
     * we will have non-null value here.
     */
  protected transient TimeZone _timezone;

  protected final Locale _locale;

  /**
     * Explicit override for leniency, if specified.
     *<p>
     * Cannot be `final` because {@link #setLenient(boolean)} returns
     * `void`.
     */
  protected Boolean _lenient;

  /**
     * Lazily instantiated calendar used by this instance for serialization ({@link #format(Date)}).
     *
     * @since 2.9.1
     */
  private transient Calendar _calendar;

  private transient DateFormat _formatRFC1123;

  /** 
     * Whether the TZ offset must be formatted with a colon between hours and minutes ({@code HH:mm} format)
     *<p>
     * NOTE: default changed to `true` in Jackson 3.0; was `false` earlier.
     */
  protected boolean _tzSerializedWithColon = true;

  public StdDateFormat() {
    _locale = DEFAULT_LOCALE;
  }

  protected StdDateFormat(TimeZone tz, Locale loc, Boolean lenient) {
    this(tz, loc, lenient, false);
  }

  /**
     * @since 2.9.1
     */
  protected StdDateFormat(TimeZone tz, Locale loc, Boolean lenient, boolean formatTzOffsetWithColon) {
    _timezone = tz;
    _locale = loc;
    _lenient = lenient;
    _tzSerializedWithColon = formatTzOffsetWithColon;
  }

  public static TimeZone getDefaultTimeZone() {
    return DEFAULT_TIMEZONE;
  }

  /**
     * Method used for creating a new instance with specified timezone;
     * if no timezone specified, defaults to the default timezone (UTC).
     */
  public StdDateFormat withTimeZone(TimeZone tz) {
    if (tz == null) {
      tz = DEFAULT_TIMEZONE;
    }
    if ((tz == _timezone) || tz.equals(_timezone)) {
      return this;
    }
    return new StdDateFormat(tz, _locale, _lenient, _tzSerializedWithColon);
  }

  /**
     * "Mutant factory" method that will return an instance that uses specified
     * {@code Locale}:
     * either {@code this} instance (if setting would not change), or newly
     * constructed instance with different {@code Locale} to use.
     */
  public StdDateFormat withLocale(Locale loc) {
    if (loc.equals(_locale)) {
      return this;
    }
    return new StdDateFormat(_timezone, loc, _lenient, _tzSerializedWithColon);
  }

  /**
     * "Mutant factory" method that will return an instance that has specified leniency
     * setting: either {@code this} instance (if setting would not change), or newly
     * constructed instance.
     *
     * @since 2.9
     */
  public StdDateFormat withLenient(Boolean b) {
    if (_equals(b, _lenient)) {
      return this;
    }
    return new StdDateFormat(_timezone, _locale, b, _tzSerializedWithColon);
  }

  /**
     * "Mutant factory" method that will return an instance that has specified
     * handling of colon when serializing timezone (timezone either written
     * like {@code +0500} or {@code +05:00}):
     * either {@code this} instance (if setting would not change), or newly
     * constructed instance with desired setting for colon inclusion.
     *<p>
     * NOTE: does NOT affect deserialization as colon is optional accepted
     * but not required -- put another way, either serialization is accepted
     * by this class.
     *
     * @since 2.9.1
     */
  public StdDateFormat withColonInTimeZone(boolean b) {
    if (_tzSerializedWithColon == b) {
      return this;
    }
    return new StdDateFormat(_timezone, _locale, _lenient, b);
  }

  @Override public StdDateFormat clone() {
    return new StdDateFormat(_timezone, _locale, _lenient, _tzSerializedWithColon);
  }

  @Override public TimeZone getTimeZone() {
    return _timezone;
  }

  @Override public void setTimeZone(TimeZone tz) {
    if (!tz.equals(_timezone)) {
      _clearFormats();
      _timezone = tz;
    }
  }

  /**
     * Need to override since we need to keep track of leniency locally,
     * and not via underlying {@link Calendar} instance like base class
     * does.
     */
  @Override public void setLenient(boolean enabled) {
    Boolean newValue = Boolean.valueOf(enabled);
    if (!_equals(newValue, _lenient)) {
      _lenient = newValue;
      _clearFormats();
    }
  }

  @Override public boolean isLenient() {
    return (_lenient == null) || _lenient.booleanValue();
  }

  /**
     * Accessor for checking whether this instance would include colon
     * within timezone serialization or not: if {code true}, timezone offset
     * is serialized like {@code -06:00}; if {code false} as {@code -0600}.
     *<p>
     * NOTE: only relevant for serialization (formatting), as deserialization
     * (parsing) always accepts optional colon but does not require it, regardless
     * of this setting.
     *
     * @return {@code true} if a colon is to be inserted between the hours and minutes 
     * of the TZ offset when serializing as String; otherwise {@code false}
     *
     * @since 2.9.1
     */
  public boolean isColonIncludedInTimeZone() {
    return _tzSerializedWithColon;
  }

  @Override public Date parse(String dateStr) throws ParseException {
    dateStr = dateStr.trim();
    ParsePosition pos = new ParsePosition(0);
    Date dt = _parseDate(dateStr, pos);
    if (dt != null) {
      return dt;
    }
    StringBuilder sb = new StringBuilder();
    for (String f : ALL_FORMATS) {
      if (sb.length() > 0) {
        sb.append("\", \"");
      } else {
        sb.append('\"');
      }
      sb.append(f);
    }
    sb.append('\"');
    throw new ParseException(String.format("Cannot parse date \"%s\": not compatible with any of standard forms (%s)", dateStr, sb.toString()), pos.getErrorIndex());
  }

  @Override public Date parse(String dateStr, ParsePosition pos) {
    try {
      return _parseDate(dateStr, pos);
    } catch (ParseException e) {
    }
    return null;
  }

  protected Date _parseDate(String dateStr, ParsePosition pos) throws ParseException {
    if (looksLikeISO8601(dateStr)) {
      return parseAsISO8601(dateStr, pos);
    }
    int i = dateStr.length();
    while (--i >= 0) {
      char ch = dateStr.charAt(i);
      if (ch < '0' || ch > '9') {
        if (i > 0 || ch != '-') {
          break;
        }
      }
    }
    if ((i < 0) && (dateStr.charAt(0) == '-' || NumberInput.inLongRange(dateStr, false))) {
      return _parseDateFromLong(dateStr, pos);
    }
    return parseAsRFC1123(dateStr, pos);
  }

  @Override public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
    TimeZone tz = _timezone;
    if (tz == null) {
      tz = DEFAULT_TIMEZONE;
    }
    _format(tz, _locale, date, toAppendTo);
    return toAppendTo;
  }

  protected void _format(TimeZone tz, Locale loc, Date date, StringBuffer buffer) {
    Calendar cal = _getCalendar(tz);
    cal.setTime(date);
    final int year = cal.get(Calendar.YEAR);
    if (cal.get(Calendar.ERA) == GregorianCalendar.BC) {
      _formatBCEYear(buffer, year);
    } else {
      if (year > 9999) {
        buffer.append('+');
      }
      pad4(buffer, year);
    }
    buffer.append('-');
    pad2(buffer, cal.get(Calendar.MONTH) + 1);
    buffer.append('-');
    pad2(buffer, cal.get(Calendar.DAY_OF_MONTH));
    buffer.append('T');
    pad2(buffer, cal.get(Calendar.HOUR_OF_DAY));
    buffer.append(':');
    pad2(buffer, cal.get(Calendar.MINUTE));
    buffer.append(':');
    pad2(buffer, cal.get(Calendar.SECOND));
    buffer.append('.');
    pad3(buffer, cal.get(Calendar.MILLISECOND));
    int offset = tz.getOffset(cal.getTimeInMillis());
    if (offset != 0) {
      int hours = Math.abs((offset / (60 * 1000)) / 60);
      int minutes = Math.abs((offset / (60 * 1000)) % 60);
      buffer.append(offset < 0 ? '-' : '+');
      pad2(buffer, hours);
      if (_tzSerializedWithColon) {
        buffer.append(':');
      }
      pad2(buffer, minutes);
    } else {
      if (_tzSerializedWithColon) {
        buffer.append("+00:00");
      } else {
        buffer.append("+0000");
      }
    }
  }

  protected void _formatBCEYear(StringBuffer buffer, int bceYearNoSign) {
    if (bceYearNoSign == 1) {
      buffer.append("+0000");
      return;
    }
    final int isoYear = bceYearNoSign - 1;
    buffer.append('-');
    pad4(buffer, isoYear);
  }

  private static void pad2(StringBuffer buffer, int value) {
    int tens = value / 10;
    if (tens == 0) {
      buffer.append('0');
    } else {
      buffer.append((char) ('0' + tens));
      value -= 10 * tens;
    }
    buffer.append((char) ('0' + value));
  }

  private static void pad3(StringBuffer buffer, int value) {
    int h = value / 100;
    if (h == 0) {
      buffer.append('0');
    } else {
      buffer.append((char) ('0' + h));
      value -= (h * 100);
    }
    pad2(buffer, value);
  }

  private static void pad4(StringBuffer buffer, int value) {
    int h = value / 100;
    if (h == 0) {
      buffer.append('0').append('0');
    } else {
      if (h > 99) {
        buffer.append(h);
      } else {
        pad2(buffer, h);
      }
      value -= (100 * h);
    }
    pad2(buffer, value);
  }

  @Override public String toString() {
    return String.format("DateFormat %s: (timezone: %s, locale: %s, lenient: %s)", getClass().getName(), _timezone, _locale, _lenient);
  }

  public String toPattern() {
    StringBuilder sb = new StringBuilder(100);
    sb.append("[one of: \'").append(DATE_FORMAT_STR_ISO8601).append("\', \'").append(DATE_FORMAT_STR_RFC1123).append("\' (");
    sb.append(Boolean.FALSE.equals(_lenient) ? "strict" : "lenient").append(")]");
    return sb.toString();
  }

  @Override public boolean equals(Object o) {
    return (o == this);
  }

  @Override public int hashCode() {
    return System.identityHashCode(this);
  }

  /**
     * Helper method used to figure out if input looks like valid
     * ISO-8601 string.
     */
  protected boolean looksLikeISO8601(String dateStr) {
    if (dateStr.length() >= 7 && Character.isDigit(dateStr.charAt(0)) && Character.isDigit(dateStr.charAt(3)) && dateStr.charAt(4) == '-' && Character.isDigit(dateStr.charAt(5))) {
      return true;
    }
    return false;
  }

  private Date _parseDateFromLong(String longStr, ParsePosition pos) throws ParseException {
    long ts;
    try {
      ts = NumberInput.parseLong(longStr);
    } catch (NumberFormatException e) {
      throw new ParseException(String.format("Timestamp value %s out of 64-bit value range", longStr), pos.getErrorIndex());
    }
    return new Date(ts);
  }

  protected Date parseAsISO8601(String dateStr, ParsePosition pos) throws ParseException {
    try {
      return _parseAsISO8601(dateStr, pos);
    } catch (IllegalArgumentException e) {
      throw new ParseException(String.format("Cannot parse date \"%s\", problem: %s", dateStr, e.getMessage()), pos.getErrorIndex());
    }
  }

  protected Date _parseAsISO8601(String dateStr, ParsePosition bogus) throws IllegalArgumentException, ParseException {
    final int totalLen = dateStr.length();
    TimeZone tz = DEFAULT_TIMEZONE;
    if ((_timezone != null) && ('Z' != dateStr.charAt(totalLen - 1))) {
      tz = _timezone;
    }
    Calendar cal = _getCalendar(tz);
    cal.clear();
    String formatStr;
    if (totalLen <= 10) {
      Matcher m = PATTERN_PLAIN.matcher(dateStr);
      if (m.matches()) {
        int year = _parse4D(dateStr, 0);
        int month = _parse2D(dateStr, 5) - 1;
        int day = _parse2D(dateStr, 8);
        cal.set(year, month, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
      }
      formatStr = DATE_FORMAT_STR_PLAIN;
    } else {
      Matcher m = PATTERN_ISO8601.matcher(dateStr);
      if (m.matches()) {
        int start = m.start(2);
        int end = m.end(2);
        int len = end - start;
        if (len > 1) {
          int offsetSecs = _parse2D(dateStr, start + 1) * 3600;
          if (len >= 5) {
            offsetSecs += _parse2D(dateStr, end - 2) * 60;
          }
          if (dateStr.charAt(start) == '-') {
            offsetSecs *= -1000;
          } else {
            offsetSecs *= 1000;
          }
          cal.set(Calendar.ZONE_OFFSET, offsetSecs);
          cal.set(Calendar.DST_OFFSET, 0);
        }
        int year = _parse4D(dateStr, 0);
        int month = _parse2D(dateStr, 5) - 1;
        int day = _parse2D(dateStr, 8);
        int hour = _parse2D(dateStr, 11);
        int minute = _parse2D(dateStr, 14);
        int seconds;
        if ((totalLen > 16) && dateStr.charAt(16) == ':') {
          seconds = _parse2D(dateStr, 17);
        } else {
          seconds = 0;
        }
        cal.set(year, month, day, hour, minute, seconds);
        start = m.start(1) + 1;
        end = m.end(1);
        int msecs = 0;
        if (start >= end) {
          cal.set(Calendar.MILLISECOND, 0);
        } else {
          msecs = 0;
          final int fractLen = end - start;
          switch (fractLen) {
            default:
            if (fractLen > 9) {
              throw new ParseException(String.format("Cannot parse date \"%s\": invalid fractional seconds \'%s\'; can use at most 9 digits", dateStr, m.group(1).substring(1)), start);
            }
            case 3:
            msecs += (dateStr.charAt(start + 2) - '0');
            case 2:
            msecs += 10 * (dateStr.charAt(start + 1) - '0');
            case 1:
            msecs += 100 * (dateStr.charAt(start) - '0');
            break;
            case 0:
            break;
          }
          cal.set(Calendar.MILLISECOND, msecs);
        }
        return cal.getTime();
      }
      formatStr = DATE_FORMAT_STR_ISO8601;
    }
    throw new ParseException(String.format("Cannot parse date \"%s\": while it seems to fit format \'%s\', parsing fails (leniency? %s)", dateStr, formatStr, _lenient), 0);
  }

  private static int _parse4D(String str, int index) {
    return (1000 * (str.charAt(index) - '0')) + (100 * (str.charAt(index + 1) - '0')) + (10 * (str.charAt(index + 2) - '0')) + (str.charAt(index + 3) - '0');
  }

  private static int _parse2D(String str, int index) {
    return (10 * (str.charAt(index) - '0')) + (str.charAt(index + 1) - '0');
  }

  protected Date parseAsRFC1123(String dateStr, ParsePosition pos) {
    if (_formatRFC1123 == null) {
      _formatRFC1123 = _cloneFormat(DATE_FORMAT_RFC1123, DATE_FORMAT_STR_RFC1123, _timezone, _locale, _lenient);
    }
    return _formatRFC1123.parse(dateStr, pos);
  }

  private final static DateFormat _cloneFormat(DateFormat df, String format, TimeZone tz, Locale loc, Boolean lenient) {
    if (!loc.equals(DEFAULT_LOCALE)) {
      df = new SimpleDateFormat(format, loc);
      df.setTimeZone((tz == null) ? DEFAULT_TIMEZONE : tz);
    } else {
      df = (DateFormat) df.clone();
      if (tz != null) {
        df.setTimeZone(tz);
      }
    }
    if (lenient != null) {
      df.setLenient(lenient.booleanValue());
    }
    return df;
  }

  protected void _clearFormats() {
    _formatRFC1123 = null;
  }

  protected Calendar _getCalendar(TimeZone tz) {
    Calendar cal = _calendar;
    if (cal == null) {
      _calendar = cal = (Calendar) CALENDAR.clone();
    }
    if (!cal.getTimeZone().equals(tz)) {
      cal.setTimeZone(tz);
    }
    cal.setLenient(isLenient());
    return cal;
  }

  protected static <T extends java.lang.Object> boolean _equals(T value1, T value2) {
    if (value1 == value2) {
      return true;
    }
    return (value1 != null) && value1.equals(value2);
  }
}