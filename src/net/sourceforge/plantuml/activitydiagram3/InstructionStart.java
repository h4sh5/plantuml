/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.activitydiagram3;

import java.util.Objects;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.gtile.Gtile;
import net.sourceforge.plantuml.activitydiagram3.gtile.GtileCircleStart;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class InstructionStart extends MonoSwimable implements Instruction {

	private final LinkRendering inlinkRendering;

	public InstructionStart(Swimlane swimlane, LinkRendering inlinkRendering) {
		super(swimlane);
		this.inlinkRendering = Objects.requireNonNull(inlinkRendering);
	}

	@Override
	public boolean containsBreak() {
		return false;
	}

	@Override
	public Gtile createGtile(ISkinParam skinParam, StringBounder stringBounder) {
		return new GtileCircleStart(stringBounder, skinParam, HColorUtils.BLACK, getSwimlaneIn());
	}

	@Override
	public Ftile createFtile(FtileFactory factory) {
		Ftile result = factory.start(getSwimlaneIn());
		result = eventuallyAddNote(factory, result, result.getSwimlaneIn(), VerticalAlignment.CENTER);
		return result;
	}

	@Override
	public CommandExecutionResult add(Instruction other) {
		throw new UnsupportedOperationException();
	}

	@Override
	final public boolean kill() {
		return false;
	}

	@Override
	public LinkRendering getInLinkRendering() {
		return inlinkRendering;
	}

}
