/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 * 
 *
 */
package net.sourceforge.plantuml.creole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;

public class StripeTable implements Stripe {

	static enum Mode {
		HEADER, NORMAL
	};

	private FontConfiguration fontConfiguration;
	final private ISkinSimple skinParam;
	final private AtomTable table;
	final private Atom marged;
	final private StripeStyle stripeStyle = new StripeStyle(StripeStyleType.NORMAL, 0, '\0');

	public StripeTable(FontConfiguration fontConfiguration, ISkinSimple skinParam, String line) {
		this.fontConfiguration = fontConfiguration;
		this.skinParam = skinParam;
		this.table = new AtomTable(fontConfiguration.getColor());
		this.marged = new AtomWithMargin(table, 2, 2);
		analyzeAndAddInternal(line, Mode.HEADER);
	}

	public List<Atom> getAtoms() {
		return Collections.<Atom> singletonList(marged);
	}

	static Atom asAtom(List<StripeSimple> cells, double padding) {
		final Sheet sheet = new Sheet(HorizontalAlignment.LEFT);
		for (StripeSimple cell : cells) {
			sheet.add(cell);
		}
		return new SheetBlock1(sheet, 0, padding);
	}

	private HtmlColor getBackColor(String line) {
		if (CreoleParser.doesStartByColor(line)) {
			final int idx1 = line.indexOf('#');
			final int idx2 = line.indexOf('>');
			if (idx2 == -1) {
				throw new IllegalStateException();
			}
			final String color = line.substring(idx1, idx2);
			return skinParam.getIHtmlColorSet().getColorIfValid(color);
		}
		return null;
	}

	private String withouBackColor(String line) {
		final int idx2 = line.indexOf('>');
		if (idx2 == -1) {
			throw new IllegalStateException();
		}
		return line.substring(idx2 + 1);
	}

	private void analyzeAndAddInternal(String line, Mode mode) {
		HtmlColor lineBackColor = getBackColor(line);
		if (lineBackColor != null) {
			line = withouBackColor(line);
		}
		table.newLine(lineBackColor);
		for (final StringTokenizer st = new StringTokenizer(line, "|"); st.hasMoreTokens();) {
			String v = st.nextToken();
			HtmlColor cellBackColor = getBackColor(v);
			if (cellBackColor != null) {
				v = withouBackColor(v);
			}
			if (mode == Mode.HEADER && v.startsWith("=")) {
				v = v.substring(1);
			}
			final List<String> lines = getWithNewlinesInternal(v);
			final List<StripeSimple> cells = new ArrayList<StripeSimple>();
			for (String s : lines) {
				final StripeSimple cell = new StripeSimple(getFontConfiguration(mode), stripeStyle,
						new CreoleContext(), skinParam, CreoleMode.FULL);
				if (s.startsWith("<r>")) {
					cell.setCellAlignment(HorizontalAlignment.RIGHT);
					s = s.substring("<r>".length());
				}
				cell.analyzeAndAdd(s);
				cells.add(cell);
			}
			table.addCell(asAtom(cells, skinParam.getPadding()), cellBackColor);
		}
	}

	static List<String> getWithNewlinesInternal(String s) {
		final List<String> result = new ArrayList<String>();
		final StringBuilder current = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if (c == '\\' && i < s.length() - 1) {
				final char c2 = s.charAt(i + 1);
				i++;
				if (c2 == 'n') {
					result.add(current.toString());
					current.setLength(0);
				} else if (c2 == '\\') {
					current.append(c2);
				} else {
					current.append(c);
					current.append(c2);
				}
			} else {
				current.append(c);
			}
		}
		result.add(current.toString());
		return result;
	}

	private FontConfiguration getFontConfiguration(Mode mode) {
		if (mode == Mode.NORMAL) {
			return fontConfiguration;
		}
		return fontConfiguration.bold();
	}

	public void analyzeAndAddNormal(String line) {
		analyzeAndAddInternal(line, Mode.NORMAL);
	}

}
