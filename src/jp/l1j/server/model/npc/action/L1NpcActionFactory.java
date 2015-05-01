/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jp.l1j.server.model.npc.action;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

public class L1NpcActionFactory {
	private static Logger _log = Logger.getLogger(L1NpcActionFactory.class.getName());
	
	private static Map<String, Constructor<L1NpcAction>> _actions = new HashMap<String, Constructor<L1NpcAction>>();

	@SuppressWarnings("unchecked")
	private static Constructor<L1NpcAction> loadConstructor(Class c)
			throws NoSuchMethodException {
		return c.getConstructor(new Class[] { Element.class });
	}

	static {
		try {
			_actions.put("Action", loadConstructor(L1NpcListedAction.class));
			_actions.put("MakeItem", loadConstructor(L1NpcMakeItemAction.class));
			_actions.put("ShowHtml", loadConstructor(L1NpcShowHtmlAction.class));
			_actions.put("SetQuest", loadConstructor(L1NpcSetQuestAction.class));
			_actions.put("Teleport", loadConstructor(L1NpcTeleportAction.class));
		} catch (NoSuchMethodException e) {
			_log.log(Level.SEVERE, "Failed to load NpcAction class.", e);
		}
	}

	public static L1NpcAction newAction(Element element) {
		try {
			Constructor<L1NpcAction> con = _actions.get(element.getNodeName());
			return con.newInstance(element);
		} catch (NullPointerException e) {
			_log.warning(element.getNodeName() + " does not exist npc action.");
		} catch (Exception e) {
			_log.log(Level.SEVERE, "Failed to load NpcAction class.", e);
		}
		return null;
	}
}
