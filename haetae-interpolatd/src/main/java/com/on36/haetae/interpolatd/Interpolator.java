package com.on36.haetae.interpolatd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.on36.haetae.interpolatd.core.EscapeHandler;
import com.on36.haetae.interpolatd.core.Interpolating;
import com.on36.haetae.interpolatd.core.InterpolationHandlerImpl;
import com.on36.haetae.interpolatd.core.Substitution;

public class Interpolator<T> {

	private final List<Interpolating<T>> interpolating = new ArrayList<Interpolating<T>>();

	public InterpolationHandler<T> when() {

		InterpolationHandlerImpl<T> handler = new InterpolationHandlerImpl<T>();
		interpolating.add(handler);
		return handler;
	}

	public InterpolationHandler<T> when(String characterClass) {

		InterpolationHandlerImpl<T> handler = new InterpolationHandlerImpl<T>(
				characterClass);
		interpolating.add(handler);
		return handler;
	}

	public void escapeWith(String escape) {

		interpolating.add(new EscapeHandler<T>(escape));
	}

	public String interpolate(String toInterpolate, T arg) {

		List<Substitution> substitutions = new ArrayList<Substitution>();
		for (Interpolating<T> handler : interpolating) {

			substitutions.addAll(handler.interpolate(toInterpolate, arg));
		}

		Collections.sort(substitutions);

		StringBuilder sb = new StringBuilder(toInterpolate);
		int diff = 0;
		int lastEnd = 0;
		Substitution lastEscape = null;
		for (int i = 0; i < substitutions.size(); i++) {

			Substitution sub = substitutions.get(i);

			if (sub.start() < lastEnd)
				continue;

			if (sub.isEscape()) {

				if (lastEscape != null && sub.isAfter(lastEscape)) {
					continue;
				}

				if (isActualEscape(sub, substitutions, i)) {
					lastEscape = sub;
				} else {
					continue;
				}

			} else if (lastEscape != null && sub.isAfter(lastEscape)) {

				lastEnd = sub.end();
				continue;
			}

			if (sub.value() == null)
				continue;

			sb.replace(sub.start() - diff, sub.end() - diff, sub.value());
			diff += sub.found().length() - sub.value().length();
			lastEnd = sub.end();
		}
		return sb.toString();
	}

	private boolean isActualEscape(Substitution esc,
			List<Substitution> substitutions, int index) {

		if (!hasNext(substitutions, index))
			return false;

		Substitution nextSub = getNext(substitutions, index);

		if (!nextSub.isAfter(esc))
			return false;

		if (!nextSub.isEscape())
			return true;

		/*
		 * nextSub is an escape immediately after the current escape; look ahead
		 * to see if a non-escape substitution occurs
		 */
		return isActualEscape(nextSub, substitutions, ++index);
	}

	private boolean hasNext(List<Substitution> substitutions, int currentIndex) {
		return (currentIndex + 1) < substitutions.size();
	}

	private Substitution getNext(List<Substitution> substitutions,
			int currentIndex) {
		return substitutions.get(currentIndex + 1);
	}
}
