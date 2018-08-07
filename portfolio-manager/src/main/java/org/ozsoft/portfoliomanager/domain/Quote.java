package org.ozsoft.portfoliomanager.domain;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Stock quote, representing a stock's latest price, historic closing price or historic dividend payout.
 *
 * @author Oscar Stigter
 */
public class Quote implements Comparable<Quote> {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyy HH:mm:ss", Locale.US);

	private final Date date;

	private final BigDecimal price;

	private final int volume;

	/**
	 * Constructor for a quote without a volume (e.g. last price or a dividend payout).
	 *
	 * @param date  The date.
	 * @param price The price.
	 */
	public Quote(Date date, BigDecimal price) {
		this(date, price, -1);
	}

	/**
	 * Constructor for a quote with a volume (e.g. historic or intraday price).
	 *
	 * @param date   The date.
	 * @param price  The price.
	 * @param volume The volume.
	 */
	public Quote(Date date, BigDecimal price, int volume) {
		this.date = date;
		this.price = price;
		this.volume = volume;
	}

	/**
	 * Returns the date.
	 *
	 * @return The date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the price.
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * Returns the volume, or -1 if not set.
	 *
	 * @return
	 */
	public int getVolume() {
		return volume;
	}

	@Override
	public int compareTo(Quote other) {
		return date.compareTo(other.getDate());
	}

	@Override
	public String toString() {
		if (volume > 0) {
			return String.format("%s: $ %,.2f (%s)", DATE_FORMAT.format(date), price, formatVolume(volume));
		} else {
			return String.format("%s: $ %,.2f", DATE_FORMAT.format(date), price);
		}
	}

	public static String formatVolume(int volume) {
		double d = 0.0;
		char unit = 0;
		double scale = Math.floor(Math.log10(volume));
		if (scale > 8.0) {
			d = volume / 1000000000.0;
			unit = 'B';
		} else if (scale > 5.0) {
			d = volume / 1000000.0;
			unit = 'M';
		} else if (scale > 2.0) {
			d = volume / 1000.0;
			unit = 'k';
		} else {
			d = volume;
		}

		scale = Math.floor(Math.log10(d)) + 1.0;
		if (scale < 2.0) {
			return String.format("%.2f%c", d, unit);
		} else if (scale < 3.0) {
			return String.format("%.1f%c", d, unit);
		} else if (scale < 4.0) {
			return String.format("%.0f%c", d, unit);
		} else {
			return String.format("%,.0f", d);
		}
	}
}
