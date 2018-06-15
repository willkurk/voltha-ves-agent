package filter;

import java.util.Arrays;

import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class MarkerFilter extends AbstractMatcherFilter<ILoggingEvent> {

	Marker markerToMatch;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.qos.logback.core.filter.Filter#decide(java.lang.Object)
	 */
	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (!isStarted()) {
			return FilterReply.NEUTRAL;
		}
		Marker marker = event.getMarker();
		if (marker == null) {
			return onMismatch;
		}

		if (markerToMatch.contains(marker)) {
			return onMatch;
		} else {
			return onMismatch;
		}
	}

	/**
	 * The marker to match in the event.
	 * 
	 * @param markerToMatch
	 */
	public void setMarker(String markerStr) {
		if (markerStr != null) {
			Marker marker = MarkerFactory.getMarker(markerStr);
			this.markerToMatch = marker;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.qos.logback.core.filter.Filter#start()
	 */
	@Override
	public void start() {
		if (this.markerToMatch != null) {
			super.start();
		}
	}
}
